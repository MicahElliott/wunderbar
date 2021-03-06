(ns wunder.core-test
  (:require [clojure.test :refer :all]
            [wunder.core :refer :all]
            [clojure.data.json :as json]))

(def sfo (json/read-json (slurp "sfo-forecast.json")))

;; (select-present) =>
(def present-data
  {:windchill_c "NA",
   :relative_humidity "50%",
   :local_tz_long "America/Los_Angeles",
   :dewpoint_string "55 F (13 C)",
   :precip_1hr_string "-999.00 in ( 0 mm)",
   :forecast_url
   "http://www.wunderground.com/US/CA/Beverly_Hills.html",
   :local_time_rfc822 "Wed, 26 Apr 2017 10:38:56 -0700",
   :heat_index_c "NA",
   :temp_f 75.0,
   :feelslike_f "75.0",
   :pressure_in "29.90",
   :precip_today_in "0.00",
   :windchill_f "NA",
   :wind_degrees 270,
   :dewpoint_c 13,
   :dewpoint_f 55,
   :feelslike_string "75.0 F (23.9 C)",
   :visibility_km "16.1",
   :icon "clear",
   :nowcast "",
   :pressure_trend "-",
   :temp_c 23.9,
   :wind_gust_mph "1.2",
   :precip_1hr_metric " 0",
   :solarradiation "--",
   :precip_1hr_in "-999.00",
   :heat_index_string "NA",
   :pressure_mb "1012",
   :station_id "KCABEVER22",
   :temperature_string "75.0 F (23.9 C)",
   :observation_time "Last Updated on April 26, 10:32 AM PDT",
   :history_url
   "http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=KCABEVER22",
   :observation_time_rfc822 "Wed, 26 Apr 2017 10:32:25 -0700",
   :heat_index_f "NA",
   :local_tz_short "PDT",
   :wind_mph 0.6,
   :local_epoch "1493228336",
   :UV "6",
   :estimated {},
   :feelslike_c "23.9",
   :weather "Clear",
   :display_location
   {:elevation "168.9",
    :full "Beverly Hills, CA",
    :city "Beverly Hills",
    :longitude "-118.41000366",
    :state "CA",
    :magic "1",
    :state_name "California",
    :zip "90210",
    :latitude "34.09999847",
    :country_iso3166 "US",
    :country "US",
    :wmo "99999"},
   :image
   {:url "http://icons.wxug.com/graphics/wu2/logo_130x80.png",
    :title "Weather Underground",
    :link "http://www.wunderground.com"},
   :observation_location
   {:full "Monte Cielo Drive, Beverly Hills, California",
    :city "Monte Cielo Drive, Beverly Hills",
    :state "California",
    :country "US",
    :country_iso3166 "US",
    :latitude "34.099072",
    :longitude "-118.408760",
    :elevation "866 ft"},
   :local_tz_offset "-0700",
   :visibility_mi "10.0",
   :precip_today_metric "0",
   :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
   :wind_kph 1.0,
   :ob_url
   "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=34.099072,-118.408760",
   :wind_dir "West",
   :wind_gust_kph "1.9",
   :precip_today_string "0.00 in (0 mm)",
   :wind_string "Calm",
   :windchill_string "NA",
   :observation_epoch "1493227945"})

(deftest present-parsing
  (testing "present conditions become a simple display string"
    (is (= "Cr75/W1" (pres->str present-data)))))

(def hourly-data
  '({:wdir {:dir "NW", :degrees "310"},
     :windchill {:english "-9999", :metric "-9999"},
     :temp {:english "77", :metric "25"},
     :wspd {:english "10", :metric "16"},
     :pop "0",
     :dewpoint {:english "45", :metric "7"},
     :icon "clear",
     :qpf {:english "0.0", :metric "0"},
     :mslp {:english "29.88", :metric "1012"},
     :feelslike {:english "77", :metric "25"},
     :humidity "33",
     :snow {:english "0.0", :metric "0"},
     :fctcode "1",
     :uvi "9",
     :FCTTIME
     {:weekday_name_abbrev "Wed",
      :epoch "1493233200",
      :min "00",
      :hour "12",
      :yday "115",
      :age "",
      :weekday_name_night_unlang "Wednesday Night",
      :mon_padded "04",
      :hour_padded "12",
      :tz "",
      :isdst "1",
      :civil "12:00 PM",
      :ampm "PM",
      :weekday_name "Wednesday",
      :min_unpadded "0",
      :year "2017",
      :UTCDATE "",
      :weekday_name_night "Wednesday Night",
      :sec "0",
      :month_name_abbrev "Apr",
      :month_name "April",
      :mon "4",
      :pretty "12:00 PM PDT on April 26, 2017",
      :mon_abbrev "Apr",
      :mday_padded "26",
      :weekday_name_unlang "Wednesday",
      :mday "26"},
     :condition "Clear",
     :wx "Sunny",
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :heatindex {:english "-9999", :metric "-9999"},
     :sky "18"}
    {:wdir {:dir "NNW", :degrees "340"},
     :windchill {:english "-9999", :metric "-9999"},
     :temp {:english "80", :metric "27"},
     :wspd {:english "12", :metric "19"},
     :pop "0",
     :dewpoint {:english "47", :metric "8"},
     :icon "clear",
     :qpf {:english "0.0", :metric "0"},
     :mslp {:english "29.82", :metric "1010"},
     :feelslike {:english "80", :metric "27"},
     :humidity "32",
     :snow {:english "0.0", :metric "0"},
     :fctcode "1",
     :uvi "6",
     :FCTTIME
     {:weekday_name_abbrev "Wed",
      :epoch "1493244000",
      :min "00",
      :hour "15",
      :yday "115",
      :age "",
      :weekday_name_night_unlang "Wednesday Night",
      :mon_padded "04",
      :hour_padded "15",
      :tz "",
      :isdst "1",
      :civil "3:00 PM",
      :ampm "PM",
      :weekday_name "Wednesday",
      :min_unpadded "0",
      :year "2017",
      :UTCDATE "",
      :weekday_name_night "Wednesday Night",
      :sec "0",
      :month_name_abbrev "Apr",
      :month_name "April",
      :mon "4",
      :pretty "3:00 PM PDT on April 26, 2017",
      :mon_abbrev "Apr",
      :mday_padded "26",
      :weekday_name_unlang "Wednesday",
      :mday "26"},
     :condition "Clear",
     :wx "Mostly Sunny",
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :heatindex {:english "-9999", :metric "-9999"},
     :sky "20"}
    {:wdir {:dir "N", :degrees "354"},
     :windchill {:english "-9999", :metric "-9999"},
     :temp {:english "76", :metric "24"},
     :wspd {:english "14", :metric "23"},
     :pop "0",
     :dewpoint {:english "48", :metric "9"},
     :icon "partlycloudy",
     :qpf {:english "0.0", :metric "0"},
     :mslp {:english "29.8", :metric "1009"},
     :feelslike {:english "76", :metric "24"},
     :humidity "37",
     :snow {:english "0.0", :metric "0"},
     :fctcode "2",
     :uvi "1",
     :FCTTIME
     {:weekday_name_abbrev "Wed",
      :epoch "1493254800",
      :min "00",
      :hour "18",
      :yday "115",
      :age "",
      :weekday_name_night_unlang "Wednesday Night",
      :mon_padded "04",
      :hour_padded "18",
      :tz "",
      :isdst "1",
      :civil "6:00 PM",
      :ampm "PM",
      :weekday_name "Wednesday",
      :min_unpadded "0",
      :year "2017",
      :UTCDATE "",
      :weekday_name_night "Wednesday Night",
      :sec "0",
      :month_name_abbrev "Apr",
      :month_name "April",
      :mon "4",
      :pretty "6:00 PM PDT on April 26, 2017",
      :mon_abbrev "Apr",
      :mday_padded "26",
      :weekday_name_unlang "Wednesday",
      :mday "26"},
     :condition "Partly Cloudy",
     :wx "Partly Cloudy",
     :icon_url "http://icons.wxug.com/i/c/k/partlycloudy.gif",
     :heatindex {:english "-9999", :metric "-9999"},
     :sky "39"}
    {:wdir {:dir "N", :degrees "352"},
     :windchill {:english "-9999", :metric "-9999"},
     :temp {:english "71", :metric "22"},
     :wspd {:english "12", :metric "19"},
     :pop "0",
     :dewpoint {:english "50", :metric "10"},
     :icon "partlycloudy",
     :qpf {:english "0.0", :metric "0"},
     :mslp {:english "29.83", :metric "1010"},
     :feelslike {:english "71", :metric "22"},
     :humidity "48",
     :snow {:english "0.0", :metric "0"},
     :fctcode "2",
     :uvi "0",
     :FCTTIME
     {:weekday_name_abbrev "Wed",
      :epoch "1493265600",
      :min "00",
      :hour "21",
      :yday "115",
      :age "",
      :weekday_name_night_unlang "Wednesday Night",
      :mon_padded "04",
      :hour_padded "21",
      :tz "",
      :isdst "1",
      :civil "9:00 PM",
      :ampm "PM",
      :weekday_name "Wednesday",
      :min_unpadded "0",
      :year "2017",
      :UTCDATE "",
      :weekday_name_night "Wednesday Night",
      :sec "0",
      :month_name_abbrev "Apr",
      :month_name "April",
      :mon "4",
      :pretty "9:00 PM PDT on April 26, 2017",
      :mon_abbrev "Apr",
      :mday_padded "26",
      :weekday_name_unlang "Wednesday",
      :mday "26"},
     :condition "Partly Cloudy",
     :wx "Partly Cloudy",
     :icon_url "http://icons.wxug.com/i/c/k/nt_partlycloudy.gif",
     :heatindex {:english "-9999", :metric "-9999"},
     :sky "30"}))

(deftest hourly-parsing
  (testing "hourly conditions become a simple display string"
    (is (= "Cr77 Cr80 PC76 PC71 " (hourly->str hourly-data)))))

(def daily-data
  '({:date
     {:epoch "1493258400",
      :min "00",
      :day 26,
      :hour 19,
      :yday 115,
      :monthname_short "Apr",
      :isdst "1",
      :month 4,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "April",
      :year 2017,
      :weekday "Wednesday",
      :sec 0,
      :pretty "7:00 PM PDT on April 26, 2017",
      :weekday_short "Wed"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 33,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "clear",
     :high {:fahrenheit "82", :celsius "28"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Clear",
     :avewind {:mph 13, :kph 21, :dir "NNW", :degrees 329},
     :maxhumidity 0,
     :low {:fahrenheit "61", :celsius "16"},
     :period 1,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :maxwind {:mph 20, :kph 32, :dir "NNW", :degrees 329}}
    {:date
     {:epoch "1493344800",
      :min "00",
      :day 27,
      :hour 19,
      :yday 116,
      :monthname_short "Apr",
      :isdst "1",
      :month 4,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "April",
      :year 2017,
      :weekday "Thursday",
      :sec 0,
      :pretty "7:00 PM PDT on April 27, 2017",
      :weekday_short "Thu"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 35,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "partlycloudy",
     :high {:fahrenheit "81", :celsius "27"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Partly Cloudy",
     :avewind {:mph 19, :kph 31, :dir "NNW", :degrees 342},
     :maxhumidity 0,
     :low {:fahrenheit "60", :celsius "16"},
     :period 2,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/partlycloudy.gif",
     :maxwind {:mph 25, :kph 40, :dir "NNW", :degrees 342}}
    {:date
     {:epoch "1493431200",
      :min "00",
      :day 28,
      :hour 19,
      :yday 117,
      :monthname_short "Apr",
      :isdst "1",
      :month 4,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "April",
      :year 2017,
      :weekday "Friday",
      :sec 0,
      :pretty "7:00 PM PDT on April 28, 2017",
      :weekday_short "Fri"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 24,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "clear",
     :high {:fahrenheit "84", :celsius "29"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Clear",
     :avewind {:mph 17, :kph 27, :dir "N", :degrees 350},
     :maxhumidity 0,
     :low {:fahrenheit "60", :celsius "16"},
     :period 3,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :maxwind {:mph 25, :kph 40, :dir "N", :degrees 350}}
    {:date
     {:epoch "1493517600",
      :min "00",
      :day 29,
      :hour 19,
      :yday 118,
      :monthname_short "Apr",
      :isdst "1",
      :month 4,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "April",
      :year 2017,
      :weekday "Saturday",
      :sec 0,
      :pretty "7:00 PM PDT on April 29, 2017",
      :weekday_short "Sat"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 14,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "clear",
     :high {:fahrenheit "84", :celsius "29"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Clear",
     :avewind {:mph 8, :kph 13, :dir "ESE", :degrees 123},
     :maxhumidity 0,
     :low {:fahrenheit "58", :celsius "14"},
     :period 4,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :maxwind {:mph 10, :kph 16, :dir "ESE", :degrees 123}}
    {:date
     {:epoch "1493604000",
      :min "00",
      :day 30,
      :hour 19,
      :yday 119,
      :monthname_short "Apr",
      :isdst "1",
      :month 4,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "April",
      :year 2017,
      :weekday "Sunday",
      :sec 0,
      :pretty "7:00 PM PDT on April 30, 2017",
      :weekday_short "Sun"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 13,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "clear",
     :high {:fahrenheit "82", :celsius "28"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Clear",
     :avewind {:mph 11, :kph 18, :dir "SSW", :degrees 192},
     :maxhumidity 0,
     :low {:fahrenheit "59", :celsius "15"},
     :period 5,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :maxwind {:mph 15, :kph 24, :dir "SSW", :degrees 192}}
    {:date
     {:epoch "1493690400",
      :min "00",
      :day 1,
      :hour 19,
      :yday 120,
      :monthname_short "May",
      :isdst "1",
      :month 5,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "May",
      :year 2017,
      :weekday "Monday",
      :sec 0,
      :pretty "7:00 PM PDT on May 01, 2017",
      :weekday_short "Mon"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 31,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "clear",
     :high {:fahrenheit "81", :celsius "27"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Clear",
     :avewind {:mph 8, :kph 13, :dir "S", :degrees 182},
     :maxhumidity 0,
     :low {:fahrenheit "60", :celsius "16"},
     :period 6,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :maxwind {:mph 10, :kph 16, :dir "S", :degrees 182}}
    {:date
     {:epoch "1493776800",
      :min "00",
      :day 2,
      :hour 19,
      :yday 121,
      :monthname_short "May",
      :isdst "1",
      :month 5,
      :ampm "PM",
      :tz_short "PDT",
      :tz_long "America/Los_Angeles",
      :monthname "May",
      :year 2017,
      :weekday "Tuesday",
      :sec 0,
      :pretty "7:00 PM PDT on May 02, 2017",
      :weekday_short "Tue"},
     :snow_allday {:in 0.0, :cm 0.0},
     :qpf_day {:in 0.0, :mm 0},
     :pop 0,
     :qpf_allday {:in 0.0, :mm 0},
     :avehumidity 38,
     :minhumidity 0,
     :snow_day {:in 0.0, :cm 0.0},
     :icon "clear",
     :high {:fahrenheit "82", :celsius "28"},
     :skyicon "",
     :snow_night {:in 0.0, :cm 0.0},
     :conditions "Clear",
     :avewind {:mph 7, :kph 11, :dir "S", :degrees 171},
     :maxhumidity 0,
     :low {:fahrenheit "60", :celsius "16"},
     :period 7,
     :qpf_night {:in 0.0, :mm 0},
     :icon_url "http://icons.wxug.com/i/c/k/clear.gif",
     :maxwind {:mph 10, :kph 16, :dir "S", :degrees 171}})
  )

(deftest daily-parsing
  (testing "daily conditions become a simple display string"
    (is (= "Cr6182 PC6081 Cr6084 Cr5884 Cr5982 Cr6081 Cr6082"
           (forecast->str daily-data)))))
