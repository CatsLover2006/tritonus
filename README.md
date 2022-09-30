[![GitHub Packages](https://github.com/umjammer/tritonus/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer/tritonus/actions/workflows/maven-publish.yml)
[![Java CI](https://github.com/umjammer/tritonus/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/tritonus/actions)
[![Parent](https://img.shields.io/badge/Parent-vavi--sound--sandbox-pink)](https://github.com/umjammer/vavi-sound-sandbox)
![Java](https://img.shields.io/badge/Java-8-b07219)

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

https://github.com/umjammer/tritonus/packages

## TODO

 * ~~deploy to bintray via github actions~~
 * timidity
