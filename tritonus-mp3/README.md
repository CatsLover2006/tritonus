# README.mp3

## MP3 DECODING

This version of Tritonus includes mp3 decoding support. This is due to
using javalayer 1.0, a pure-java mp3 decoder developed by
the javalayer project. Thanks to Eric B., who wrote the decoder.

To compile from scratch, you need jl1.0.jar (see
http://sourceforge.net/projects/javalayer/) in the classpath.
In this case, an ant build will include the mp3 plugin.

For a runnable program, see DecodingAudioPlayer.java or
AudioPlayer.java from the Java Sound Examples.

The decoder can also be used as plug-in for any Java Sound
implementation (like the Sun jdk1.3). Precompiled jar files are at
http://www.tritonus.org/plugins.html

JavaLayer project:
http://www.javazoom.net/javalayer/javalayer.html

## MP3 ENCODING

MP3 file creation is supported with native access to
the LAME library. A pure Java MP3 encoder does not exist
in our knowledge and would probably be too slow - nowadays.
LAME is acknowledged to provide high quality mp3 encoding.
Additionally, it is completely open source (LGPL) and does
not use 3rd party source code.
As LAME is accessed via JNI, the current implementation
in Tritonus can only be used under Linux. In future,
a port to Windows and other systems is possible.

Capabilities in Tritonus:
The encoder creates MPEG1 layer III, MPEG2 layer III
or MPEG2.5 layer III files and chooses automatically
the right encoding. Bit rates may vary from 8 to 320KBit/s,
VBR (variable bit rate) is supported, too. Different
quality levels may be choosen which affect

- encoding speed for CBR (constant bit rate)
- mp3 file size for VBR.

## Downloading LAME

LAME has to be installed on your system.
You need at least LAME V3.88beta or later. You can get LAME from
http://sourceforge.net/projects/lame/
To get the latest CVS version of lame, issue these 2 commands:
cvs -d:pserver:anonymous@cvs.lame.sourceforge.net:/cvsroot/lame login
(Password empty)
cvs -z3 -d:pserver:anonymous@cvs.lame.sourceforge.net:/cvsroot/lame co lame

## Installation of LAME

In the main directory of lame, issue "./configure --enable-shared"
and "make".
As root, invoke "make install". This copies the library to /usr/local/lib.
You may need to include /usr/local/lib to /etc/ld.so.conf and then
run "ldconfig"

## Integration in Tritonus

You must re-run "configure" after installation of LAME. Be sure to
remove "config.cache" before. "configure" outputs a line like
"Will build stuff for LAME encoder" when everything is fine.
Call "make" and as root "make install" (or make install-link).

## Testing

A simple command line tool for converting any audio file to mp3 is
included in the directory test: Mp3Encoder.java. Run it without
parameters to see brief usage instructions. Looking at its source
code will show you how it works (and the problems...).
Please send problems or bugs to florian@tritonus.org or submit them
to the bug database on sourceforge.

## Thanks

Many thanks to Mark Taylor, development lead of LAME.
He was open to my (Florian's) proposals and let me work on LAME
to make the integration into Tritonus possible.
