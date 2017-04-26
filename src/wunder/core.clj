(ns wunder.core
  "wunderbar — current/forecasted weather in i3status 1-line format

  Dependencies: Weather Underground API key"
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [org.httpkit.client :as http]
            ;; [taoensso.timbre :as timbre :refer [log info debug warn error]]
            [environ.core :refer [env]]))

;; TODO: move defs to defns to avoid work during compile
;; TODO: auto-detect location
;; TODO: standardize on env, not getenv


;; Wunderground API endpoint
(def wapi "http://api.wunderground.com/api/")

;; Wunderground API key every user/dev must have personally
(def wkey (System/getenv "WUNDERKEY"))

(defn debug [& msgs]
  (when (env :wunderdebug)
    (binding [*out* *err*] (apply println msgs))))

(defn abort [& msgs]
  (binding [*out* *err*] (apply println msgs))
  (System/exit 1))

(defn check-key []
  (when-not wkey
    (abort "Must export WUNDERKEY env var."
           "\nSign up for a free key:"
           "\nhttps://www.wunderground.com/weather/api/d/pricing.html")))

;; Default to Beverly Hills where it's sure to be beautiful!
(def default-loc "90210")
(def loc (env :wunderloc default-loc))
;; BUG: what if they really are in 90210??
(when (= loc default-loc)
  (binding [*out* *err*]
    (println "WARNING: Defaulting to" loc "for demo purposes.")))

;; For request query: determine if zip code or personal weather station
(def geoq (if (re-matches #"^\d{5}$" loc)
            ;; Ex: Beaverton zip code: 97005
            ;; Not sure what the 1.99999 junk is for; seems necessary for zips.
            (str "/q/zmw:" loc ".1.99999.json")
            ;; Ex: Beaverton personal weather station (pws): KORBEAVE74
            (str "/q/pws:" loc ".json")))

(defn check-loc []
  (when-not loc
    (abort "Must export WUNDERLOC env var for zip code or nearest station."
           "\nFind a station:"
           "\nhttps://www.wunderground.com/weatherstation/ListStations.asp")))

;; Optionally show PWS location in bar
(def pws-display
  (if (env :wundershowloc)
    (str " (" loc ")")
    ""))

;; (def wunits (or (System/getenv "WUNDERUNITS") :fahrenheit))
;; NOTE: not error checking this user input
(def wunits (or (System/getenv "WUNDERUNITS") "f"))
;; Pretty lame inconsistency of units across calls
(def fc-units   (if (= wunits "f") :fahrenheit :celcius))
(def hrly-units (if (= wunits "f") :english :metric))
(def pres-units (if (= wunits "f") :temp_f :temp_c))

;; Ex: "http://api.wunderground.com/api/abc123/forecast10day/q/zmw:97005.1.99999.json"
(defn mkurl [dur] (str wapi wkey "/" dur geoq))
(def present-url  (mkurl "conditions"))
(def hourly-url   (mkurl "hourly"))
(def forecast-url (mkurl "forecast10day"))
(def ndays 7)  ; number of forecast days

;; Wunderground status codes, based on icon sets.
;; https://www.wunderground.com/weather/api/d/docs?d=resources/icon-sets
;; This is a cute little abbreviation table for compressed display.
;; Surprising how many few codes wunderground has compared to
;; openweathermap.
(def conds {"chanceflurries" "CF"
            "chancerain"     "CR"
            "chancesleet"    "CH"
            "chancesnow"     "CS"
            "chancetstorms"  "CT"
            "clear"          "Cr"
            "cloudy"         "Cd"
            "flurries"       "Fl"
            "fog"            "Fg"
            "hazy"           "Hz"
            "mostlycloudy"   "MC"
            "mostlysunny"    "MS"
            "partlycloudy"   "PC"
            "partlysunny"    "PS"
            "rain"           "Rn"
            "sleet"          "Hl"
            "snow"           "Sw"
            "sunny"          "Sy"
            "tstorms"        "Th"})

;; Could grab all three URLs in parallel, but not worth it yet.
;; Really should check for full payload containing valid JSON, and
;; retry if not.
(defn wunder-request
  "Request the weather data json for a wunderground url.
  Called a few times to get present, hourly, and daily conditions.

  NOTE: Not part of test suite since heavy network calls."
  [url]
  (debug "requesting url:" url)
  (let [{:keys [status headers body error] :as resp}
        @(http/get url)]
    (if error
      ;; Silly API does not give errors, but shows good resp with
      ;; error in text, so not really doing anything with problem case.
      (println "error: " error)
      ;; Read the JSON and convert to hashmap.
      (json/read-json body true))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Present conditions

(defn select-present
  "Grab the blob of present-time observations."
  []
  (get-in (wunder-request present-url) [:current_observation]))

(defn pres->str
  "Massage present conditions metrics to desired units as strings.

  Input: large map of present snapshot
  Output: \"Cr79/W1\""
  [pc] ; present conditions
  (let [cnd    (get conds (:icon pc))
        temp   (str (Math/round (pres-units pc)))
        barom  (str "B" (:pressure_in pc))
        wind   (str "W" (Math/round (:wind_mph pc)))
        precip (str "P" (:precip_1hr_in pc))]
    ;; Not using all, but doesn't hurt to have captured them
    (str cnd temp "/" wind)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Hourly forecast for today

(defn select-hours
  "Grab pieces of hourly blob; every third hour."
  []
  (take 4 (take-nth 3 (:hourly_forecast
                       (wunder-request hourly-url)))))

;; (hourly->str (select-hours))
(defn hourly->str
  "Massage hourly conditions into a string.

  Input: large map of hourly data
  Output: \"CR54 CR54 CR53 Rn50 \""
  [hc] ; hourly conditions
  (map (juxt :icon :temp :wdir) hc)
  (let [cnds (map #(get conds (:icon %)) hc)
        temp (map #(get-in % [:temp hrly-units]) hc)
        wdir (map #(get-in % [:wdir :dir]) hc)]
    (apply str (interleave cnds temp (repeat " ")))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Daily forecasting for this week

(defn select-days
  "Request/transform into list of forecast days data"
  []
  (take ndays (get-in (wunder-request forecast-url)
                      [:forecast :simpleforecast :forecastday])))

;; '(("CR" "Rn" "Rn" "Rn") ("45" "45" "47" "47") ("67" "53" "54" "53"))
(defn lohis
  "Generate seq of conds, los, his from `days` (large structure)"
  [days]
  (let [lhs  (for [lohi [:low :high]]
               (map #(get-in % [lohi fc-units]) days))
        cnds (map #(get conds (:icon %)) days)]
    (conj lhs cnds)))

(defn forecast->str
  "Massage the whole week into a string.

  Input: large map of week forecast data
  Output: \"CR4567 Rn4553 Rn4754 Rn4753\""
  [days]
  (let [ds (map #(apply str %)
                (partition 3 (apply interleave (lohis days))))]
    (str/join " " ds)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main

(defn -main
  "Put present, hourly, daily together into output for status bar

  Output: \"Cl52/W2/P-999.00 | CR51 Rn48 Rn47 Rn46 | Rn4556 Rn4353 Rn4852 Rn4252 CR4054 CR3958 PC4463\"
  "
  [& args]
  (check-key)
  (debug "fetching weather for" loc)
  (let [pres (pres->str (select-present))
        hrly (hourly->str (select-hours))
        dyly (forecast->str (select-days))]
    (spit "/tmp/wunderbar.txt" (str pres pws-display " | " hrly "| " dyly))))

;; (-main) test run
