<img src="https://raw.githubusercontent.com/MicahElliott/wunderbar/master/img/wunderbar.jpg" align="left" hspace="5px"/>

[Configuration][2] | [Running][3] | [i3status][4] | [Shell Prompt][5]

Wunderbar is a "widget" for
i3bar/[i3status](https://github.com/i3/i3status) (or any text based
display like prompts or conky).

**Wunderbar prints a concise display of current and forecasted
weather.**

    Cl48/W3 | Cl48 MC45 MC44 MC45 | MC4357 CR5054 CR4054 CR4055 ...
    ^^^^^^^   ^^^^^^^^^^^^^^^^^^^   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    present       next 12 hrs               next 7 days

The above tells me I may not be spending much time outside for the
next several days.  More specifically, the present condition is
"Cloudy, 48 degrees, a little wind.  The next hours (4 indicators, 3
hours apart) and days will be "Mostly Cloudy" with "Chances of Rain".
See the source (`conds` table) for a little more detail on the weather
conditions mnemonics.


## Configure Wunderground and Wunderbar

Before running you have to do some minimal configuration.

### API key (required)

Get your own free Wunderground API key
by [signing up](https://www.wunderground.com/signup?mode=api_signup).
Quick and easy (beware the auto-play audio ads!).  You might need to
visit [the API](https://www.wunderground.com/weather/api/) to find
your key.  Tell Wunderbar
about your key (and other things) by exporting environment variables
in your shell:

    % export WUNDERKEY=abc123  # but use your own!

### Set location (required to be useful)

Find your approximate location
at [wunderground.com](https://www.wunderground.com/) by searching.
Then click _Change Station_ to get more specific about your
nearest
[personal weather station](https://www.wunderground.com/weatherstation/overview.asp) (PWS).
Alternatively, you can just use your zip code (or regional
equivalent).  An example for _Beaverton, OR, USA_ is:

    % export WUNDERLOC=KORBEAVE74  # a pws near me
    OR
    % export WUNDERLOC=97005

### Temperature units (recommended)

Set your _temperature units_ for Fahrenheit (`f` (default)) or Celsius
(`c`).

    % export WUNDERUNITS=f

### Personal Weather Station display (optional)

You might want to display your PWS ID if you move around much.

    % export WUNDERSHOWLOC=yes

### Font-awesome icons (optional)

It's pretty neat to see weather icons instead of little text
abbreviations.  If you want the icons in your bar, see
this
[i3 config page](https://wiki.archlinux.org/index.php/I3#Iconic_fonts_in_the_status_bar).

Basically, you need to install [font-awesome](http://fontawesome.io/)
on your system (place the `.otf` in `~/.fonts`).  Or, on Fedora you
can just `dnf install fontawesome-fonts`.

Then put this into your
`~/.i3/config` (and see further config for i3status below):

    bar {
      ...
      font pango:DejaVu Sans Mono, Awesome 8
    }

<img src="https://raw.githubusercontent.com/MicahElliott/wunderbar/master/img/example-with-icons.jpg" hspace="5px"/>

The font-awesome icons weren't designed for weather, but there are
approximations that convey the conditions quite well (though "Clear
skies" is just a smiley face since "Sunny" is apparently different and
takes the sun icon).  There's a prefix of `c` ("chance of"), `m`
("mostly"), and `p` ("partly") for each icon when appropriate.

Icons are now the default.  If you want to go with plain text (and not
have to install font-awesome):

    % export WUNDERJUSTTEXT=yes


## Run it

### Option 1: source-clone and build (harder)

Clone [this repo](https://github.com/MicahElliott/wunderbar).

Ensure [Leiningen](https://leiningen.org/) is installed.

Build a runnable _uberjar_ (generate something like
`target/wunder-0.1.0-SNAPSHOT-standalone.jar`):

    % lein uberjar

### Option 2: simply download (easy)

Download a runnable [jar file](http://micahelliott.com/dl/wunder-0.1.0-SNAPSHOT-standalone.jar).

### Run continuously

Put Weatherbar into your crontab to run every half-hour.

    % crontab -e
    ...
    */30 * * * *    WUNDERKEY=123... WUNDERLOC=456... java -jar /path/to/wunder-0.1.0-SNAPSHOT-standalone.jar

On each run, a temporary file, `/tmp/wunderbar.txt`, is created/replaced,
holding the one-line text of the current run.

While testing, be careful about making runs in quick succession.
The
[API's free plan](https://www.wunderground.com/weather/api/d/pricing.html?MR=1) wants
to limit you to 10 calls/minute, and each Wunderbar run costs you 3
calls.

If you want to see logging output:

    % export WUNDERDEBUG=yes

It should be enough to **update (run) every 15 or 30 minutes**.
Although it may be slow to run (a second or two), the status bar
itself will be very quick to update (e.g., you may update your actual
bar every second or few) since it's only reading a temporary file —
not calling anything.


## Add to i3status

Modify your `~/.i3/config` to call a custom `my-i3status` command (as
described
[here](https://i3wm.org/i3status/manpage.html#_external_scripts_programs_with_i3status).

    bar {
      ...
      status_command my-i3status
    }

Then your `my-i3status` script is something like:

    i3status --config ~/.i3status.conf | while :
    do
      read line
      weather=$(cat /tmp/wunderbar.txt)
      echo "$weather | $line" || exit 1
    done


## As a shell prompt

Instead of printing the whole long line, you could just _cut_ present
conditions (`Cl48/W3`) and put it into your shell prompt.  Here's all
you need for a simple Zsh prompt:

    % PROMPT='$(cut -f1 -d" " /tmp/wunderbar.txt) %F{red}%n%f@%F{blue}%m%f %F{yellow}%1~%f %# '
    Cl50/W0 mde@arix ~ %


## Improvement ideas

I couldn't figure out how to get color working, but that would be
awesome if someone knows how to feed this through i3status as json
with markup.  Highs in red, lows in blue, a few other colors would
make it more quickly readable.

A lunch-time weather report could be spoken to you.  Would be fun to
parse the concise data generated by Wunderbar into a mini weather
report.

Might be worth adding _wind_ reporting if you live in a windy city.
It's already captured, but I don't display it for brevity's sake.

Precipitation might be nice, but if the condition is "Rain" then I
don't see a huge benefit to a "100% precipitation" indicator.  Other
percentages may be useful.

You could have a randomly rotating weather status showing all the
places you'd rather be, like Hawaiʻi.

Could auto-detect location based on IP, etc, and find nearby PWS.


## Prior work

This project started out as a Zsh script using OpenWeatherMap.  I
found Wunderground to be a more reliable and accurate service and
wanted to rewrite it in Clojure to be more robust.  So this builds on
some things learned from Weatherbar.  This version is missing OWM's
sunup/sundown time feature, which was kinda' nice.


## License

Copyright © 2017 Micah Elliott

Distributed under the Eclipse Public License, the same as Clojure.


[2]: #configure-wunderground-and-wunderbar
[3]: #run-it
[4]: #add-to-i3status
[5]: #as-a-shell-prompt
