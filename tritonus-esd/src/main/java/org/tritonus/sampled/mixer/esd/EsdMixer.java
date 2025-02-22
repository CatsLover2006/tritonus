/*
 * EsdMixer.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999, 2000 by Matthias Pfisterer
 *
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.sampled.mixer.esd;

import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.tritonus.share.GlobalInfo;
import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.mixer.TMixer;
import org.tritonus.share.sampled.mixer.TMixerInfo;
import org.tritonus.share.sampled.mixer.TSoftClip;


public class EsdMixer
        extends TMixer {
    // default buffer size in bytes.
    private static final int DEFAULT_BUFFER_SIZE = 32768;

    private static AudioFormat[] FORMATS =
            {
                    // hack for testing.
                    // new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 11025/*AudioSystem.NOT_SPECIFIED*/, 16, 1, 2, 11025/*AudioSystem.NOT_SPECIFIED*/, false),
                    // Formats supported directely by esd.
                    new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, 8, 1, 1, AudioSystem.NOT_SPECIFIED, true),
                    new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, 8, 1, 1, AudioSystem.NOT_SPECIFIED, false),
                    new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, 8, 2, 2, AudioSystem.NOT_SPECIFIED, true),
                    new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, 8, 2, 2, AudioSystem.NOT_SPECIFIED, false),


                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 16, 1, 2, AudioSystem.NOT_SPECIFIED, false),
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 16, 2, 4, AudioSystem.NOT_SPECIFIED, false),

                    /*
                     * Format supported through "simple" conversions.
                     * "Simple" conversions are changes in the byte order
                     * and changing signed/unsigned for 8 bit.
                     */

                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 8, 1, 1, AudioSystem.NOT_SPECIFIED, true),
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 8, 1, 1, AudioSystem.NOT_SPECIFIED, false),
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 8, 2, 2, AudioSystem.NOT_SPECIFIED, true),
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 8, 2, 2, AudioSystem.NOT_SPECIFIED, false),

                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 16, 1, 2, AudioSystem.NOT_SPECIFIED, true),
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 16, 2, 4, AudioSystem.NOT_SPECIFIED, true),
            };

    private static Line.Info[] SOURCE_LINE_INFOS =
            {
                    new DataLine.Info(SourceDataLine.class, FORMATS, AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED),
            };

    private static Line.Info[] TARGET_LINE_INFOS =
            {
                    new DataLine.Info(TargetDataLine.class, FORMATS, AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED),
            };


    public EsdMixer() {
        super(new TMixerInfo(
                        "Esd Mixer",
                        GlobalInfo.getVendor(),
                        "Mixer for the Enlightened Sound Daemon (esd) running on the local machine",
                        GlobalInfo.getVersion()),
                new Line.Info(Mixer.class),
                Arrays.asList(FORMATS),
                Arrays.asList(FORMATS),
                Arrays.asList(SOURCE_LINE_INFOS),
                Arrays.asList(TARGET_LINE_INFOS));
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.<init>: begin");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.<init>: end");
        }
    }


    //////////////// Line //////////////////////////////////////


    // TODO: allow real close and reopen of mixer
    public void open() {
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.open(): begin");
        }

        // currently does nothing

        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.open(): end");
        }
    }


    public void close() {
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.close(): begin");
        }

        // currently does nothing

        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.close(): end");
        }
    }


    //////////////// Mixer //////////////////////////////////////


    public int getMaxLines(Line.Info info) {
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getMaxLines(): begin");
        }

        int nMaxLines = 0;
        if (info instanceof DataLine.Info) {
            Class<?> lineClass = info.getLineClass();
            if (lineClass == SourceDataLine.class) {
                nMaxLines = 32;
            } else if (lineClass == TargetDataLine.class) {
                nMaxLines = 1;
            } else {
                /* DO NOTHING; only source and target lines are supported. */
            }
        } else {
            /* DO NOTHING; only data lines are supported. */
        }

        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getMaxLines(): end");
        }
        return nMaxLines;
    }


    //////////////// private //////////////////////////////////////


    // nBufferSize is in bytes!
    protected SourceDataLine getSourceDataLine(AudioFormat format, int nBufferSize)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getSourceDataLine(): begin");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getSourceDataLine(): format: " + format);
            TDebug.out("EsdMixer.getSourceDataLine(): buffer size: " + nBufferSize);
        }
        if (nBufferSize < 1) {
            nBufferSize = DEFAULT_BUFFER_SIZE;
        }
        // int   nBufferSizeInBytes = nBufferSize * format.getFrameSize();
        EsdSourceDataLine sourceDataLine = new EsdSourceDataLine(this, format, nBufferSize);
        sourceDataLine.start();
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getSourceDataLine(): returning: " + sourceDataLine);
        }
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getSourceDataLine(): end");
        }
        return sourceDataLine;
    }


    // nBufferSize is in bytes!
    protected TargetDataLine getTargetDataLine(AudioFormat format, int nBufferSize)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getTargetDataLine(): begin");
        }
        int nBufferSizeInBytes = nBufferSize * format.getFrameSize();
        EsdTargetDataLine targetDataLine = new EsdTargetDataLine(this, format, nBufferSizeInBytes);
        // registerChannel(sourceDataLine);
        targetDataLine.start();
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getTargetDataLine(): returning: " + targetDataLine);
        }
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getTargetDataLine(): end");
        }
        return targetDataLine;
    }


    protected Clip getClip(AudioFormat format)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getClip(): begin");
        }
        Clip clip = new TSoftClip(this, format);
        if (TDebug.TraceMixer) {
            TDebug.out("EsdMixer.getClip(): end");
        }
        return clip;
    }


    // -------------------------------------------------------------


}


/* EsdMixer.java */
