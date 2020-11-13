[![Download](https://api.bintray.com/packages/umjammer/maven/tritonus/images/download.svg)](https://bintray.com/umjammer/maven/tritonus/_latestVersion) [![Maven Package](https://github.com/umjammer/tritonus/workflows/Maven%20Package/badge.svg)](https://github.com/umjammer/tritonus/actions) [![Parent](https://img.shields.io/badge/Parent-vavi--sound--sandbox-pink)](https://github.com/umjammer/vavi-sound-sandbox)

# tritonus

This is mavenized Tritonus

Tritonus is an implementation of the Java Sound API and several 
Java Sound plugins ("service providers"). For pre-compiled
versions of these components, see: 
http://www.tritonus.org/plugins.html

| module        | status | comment | library |
|---------------|:------:|---------|---------|
| share         | ✅    |         | |
| remaining     | ✅    |         | |
| dsp           | ✅    |         | |
| core          | ✅    |         | |
| gsm           | ✅    |         | |
| javasequencer | ✅    |         | |
| jorbis        | ✅    |         | |
| midishare     | ✅    |         | |
| mp3           | ✅    |         | brew:lame |
| esd           | 🚫    | linux only | libesd |
| alsa          | 🚫    | linux only | libasound |
| vorbis        | ✅    |         | | brew:libvorbis |
| pvorbis       | 🚧    | test | this |
| cdda          | 🚫    | linux only | libcdda_interface libcdda_paranoia |
| fluidsynth    | ✅    |         | brew:fluid-synth |
| src           | ✅    |         | |
| aos           | ✅    |         | |
| saol          | 🚧    |         | |
| test          | 🚧    |         | |
| timidity      | 🚧    |         | [libtimidity](https://github.com/sezero/libtimidity) |


## License

Tritonus is distributed under the terms of the Apache License,
Version 2.0. See the file LICENSE for details.

### License Exceptions

- the low level GSM code (package org.tritonus.lowlevel.gsm)
  is licensed under the GNU GPL
- BladeMP3EncDLL.h for Windows is licensed under the GNU LGPL.
- the pvorbis lib is licensed under a BSD style license

## Installation

https://bintray.com/umjammer/maven/tritonus

## TODO

 * ~~deploy to bintray via github actions~~
 * timidity
