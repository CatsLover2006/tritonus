/*
 *  Copyright (c) 2000 by Florian Bomers
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
 */

package org.tritonus.sampled.convert.lame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.lowlevel.lame.Lame;
import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.AudioFormatSet;
import org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;
import org.tritonus.share.sampled.convert.TSimpleFormatConversionProvider;


/**
 * ConversionProvider for encoding MP3 audio files with the lame lib.
 * <p>
 * It uses a sloppy implementation of the MPEG1L3 encoding: It is used as a
 * common denominator. So users can always ask for MPEG1L3 encoding but may get
 * in fact an MPEG2L3 or MPEG2.5L3 encoded stream.
 * <p>
 * TODO: add decoding ? more work on LAME itself...
 * TODO: byte swapping support in LAME ?
 *
 * @author Florian Bomers
 */
public class Mp3LameFormatConversionProvider extends
        TSimpleFormatConversionProvider {

    private static final int ALL = AudioSystem.NOT_SPECIFIED;

    private static final int MPEG_BITS_PER_SAMPLE = ALL;
    private static final int MPEG_FRAME_RATE = ALL;
    @SuppressWarnings("unused")
    private static final int MPEG_FRAME_SIZE = ALL;

    public static final AudioFormat.Encoding MPEG1L3 = Lame.MPEG1L3;
    // Lame converts automagically to MPEG2 or MPEG2.5, if necessary.
    public static final AudioFormat.Encoding MPEG2L3 = Lame.MPEG2L3;
    public static final AudioFormat.Encoding MPEG2DOT5L3 = Lame.MPEG2DOT5L3;

    /**
     * Lame provides these formats:<br>
     * MPEG1 layer III samplerates(kHz): 32 44.1 48<br>
     * bitrates(kbs): 32 40 48 56 64 80 96 112 128 160 192 224 256 320<br>
     * <br>
     * MPEG2 layer III samplerates(kHz): 16 22.05 24<br>
     * bitrates(kbs): 8 16 24 32 40 48 56 64 80 96 112 128 144 160<br>
     * <br>
     * MPEG2.5 layer III samplerates(kHz): 8 11.025 12<br>
     * bitrates(kbs): 8 16 24 32 40 48 56 64 80 96 112 128 144 160<br>
     */
    private static final AudioFormat[] OUTPUT_FORMATS = {
            new AudioFormat(MPEG2DOT5L3, 8000, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG2DOT5L3, 8000, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG2DOT5L3, 8000, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG2DOT5L3, 8000, ALL, 2, ALL, ALL, true),
            new AudioFormat(MPEG2DOT5L3, 11025, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG2DOT5L3, 11025, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG2DOT5L3, 11025, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG2DOT5L3, 11025, ALL, 2, ALL, ALL, true),
            new AudioFormat(MPEG2DOT5L3, 12000, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG2DOT5L3, 12000, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG2DOT5L3, 12000, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG2DOT5L3, 12000, ALL, 2, ALL, ALL, true),

            new AudioFormat(MPEG2L3, 16000, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG2L3, 16000, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG2L3, 16000, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG2L3, 16000, ALL, 2, ALL, ALL, true),
            new AudioFormat(MPEG2L3, 22050, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG2L3, 22050, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG2L3, 22050, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG2L3, 22050, ALL, 2, ALL, ALL, true),
            new AudioFormat(MPEG2L3, 24000, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG2L3, 24000, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG2L3, 24000, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG2L3, 24000, ALL, 2, ALL, ALL, true),
            // automagic
            new AudioFormat(MPEG1L3, 8000, ALL, 1, ALL, ALL, false), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 8000, ALL, 2, ALL, ALL, false), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 8000, ALL, 1, ALL, ALL, true), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 8000, ALL, 2, ALL, ALL, true), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 11025, ALL, 1, ALL, ALL, false),// MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 11025, ALL, 2, ALL, ALL, false),// MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 11025, ALL, 1, ALL, ALL, true), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 11025, ALL, 2, ALL, ALL, true), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 12000, ALL, 1, ALL, ALL, false),// MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 12000, ALL, 2, ALL, ALL, false),// MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 12000, ALL, 1, ALL, ALL, true), // MPEG2DOT5L3
            new AudioFormat(MPEG1L3, 12000, ALL, 2, ALL, ALL, true), // MPEG2DOT5L3
            // automagic
            new AudioFormat(MPEG1L3, 16000, ALL, 1, ALL, ALL, false), // MPEG2L3
            new AudioFormat(MPEG1L3, 16000, ALL, 2, ALL, ALL, false), // MPEG2L3
            new AudioFormat(MPEG1L3, 16000, ALL, 1, ALL, ALL, true), // MPEG2L3
            new AudioFormat(MPEG1L3, 16000, ALL, 2, ALL, ALL, true), // MPEG2L3
            new AudioFormat(MPEG1L3, 22050, ALL, 1, ALL, ALL, false), // MPEG2L3
            new AudioFormat(MPEG1L3, 22050, ALL, 2, ALL, ALL, false), // MPEG2L3
            new AudioFormat(MPEG1L3, 22050, ALL, 1, ALL, ALL, true), // MPEG2L3
            new AudioFormat(MPEG1L3, 22050, ALL, 2, ALL, ALL, true), // MPEG2L3
            new AudioFormat(MPEG1L3, 24000, ALL, 1, ALL, ALL, false), // MPEG2L3
            new AudioFormat(MPEG1L3, 24000, ALL, 2, ALL, ALL, false), // MPEG2L3
            new AudioFormat(MPEG1L3, 24000, ALL, 1, ALL, ALL, true), // MPEG2L3
            new AudioFormat(MPEG1L3, 24000, ALL, 2, ALL, ALL, true), // MPEG2L3

            new AudioFormat(MPEG1L3, 32000, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG1L3, 32000, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG1L3, 32000, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG1L3, 32000, ALL, 2, ALL, ALL, true),
            new AudioFormat(MPEG1L3, 44100, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG1L3, 44100, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG1L3, 44100, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG1L3, 44100, ALL, 2, ALL, ALL, true),
            new AudioFormat(MPEG1L3, 48000, ALL, 1, ALL, ALL, false),
            new AudioFormat(MPEG1L3, 48000, ALL, 2, ALL, ALL, false),
            new AudioFormat(MPEG1L3, 48000, ALL, 1, ALL, ALL, true),
            new AudioFormat(MPEG1L3, 48000, ALL, 2, ALL, ALL, true),
    };

    private static final AudioFormat[] INPUT_FORMATS = {
            new AudioFormat(8000, 16, 1, true, false),
            new AudioFormat(8000, 16, 1, true, true),
            new AudioFormat(11025, 16, 1, true, false),
            new AudioFormat(11025, 16, 1, true, true),
            new AudioFormat(12000, 16, 1, true, false),
            new AudioFormat(12000, 16, 1, true, true),
            new AudioFormat(16000, 16, 1, true, false),
            new AudioFormat(16000, 16, 1, true, true),
            new AudioFormat(22050, 16, 1, true, false),
            new AudioFormat(22050, 16, 1, true, true),
            new AudioFormat(24000, 16, 1, true, false),
            new AudioFormat(24000, 16, 1, true, true),
            new AudioFormat(32000, 16, 1, true, false),
            new AudioFormat(32000, 16, 1, true, true),
            new AudioFormat(44100, 16, 1, true, false),
            new AudioFormat(44100, 16, 1, true, true),
            new AudioFormat(48000, 16, 1, true, false),
            new AudioFormat(48000, 16, 1, true, true),

            new AudioFormat(8000, 16, 2, true, false),
            new AudioFormat(8000, 16, 2, true, true),
            new AudioFormat(11025, 16, 2, true, false),
            new AudioFormat(11025, 16, 2, true, true),
            new AudioFormat(12000, 16, 2, true, false),
            new AudioFormat(12000, 16, 2, true, true),
            new AudioFormat(16000, 16, 2, true, false),
            new AudioFormat(16000, 16, 2, true, true),
            new AudioFormat(22050, 16, 2, true, false),
            new AudioFormat(22050, 16, 2, true, true),
            new AudioFormat(24000, 16, 2, true, false),
            new AudioFormat(24000, 16, 2, true, true),
            new AudioFormat(32000, 16, 2, true, false),
            new AudioFormat(32000, 16, 2, true, true),
            new AudioFormat(44100, 16, 2, true, false),
            new AudioFormat(44100, 16, 2, true, true),
            new AudioFormat(48000, 16, 2, true, false),
            new AudioFormat(48000, 16, 2, true, true),
    };

    /**
     * Constructor.
     */
    public Mp3LameFormatConversionProvider() {
        super(Arrays.asList(INPUT_FORMATS), Arrays.asList(OUTPUT_FORMATS));
        if (!Lame.isLibAvailable()) {
            disable();
            if (TDebug.TraceAudioConverter) {
                TDebug.out("******* Error initializing LAME mp3 encoder: "
                        + Lame.getLinkError());
            }
        }
    }

    public AudioInputStream getAudioInputStream(AudioFormat targetFormat,
                                                AudioInputStream audioInputStream) {
        if (isConversionSupported(targetFormat, audioInputStream.getFormat())) {
            return new EncodedMpegAudioInputStream(getDefaultTargetFormat(
                    targetFormat, audioInputStream.getFormat(), false),
                    audioInputStream);
        } else {
            throw new IllegalArgumentException("conversion not supported");
        }
    }

    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding,
                                          AudioFormat sourceFormat) {
        if (TDebug.TraceAudioConverter) {
            TDebug.out(">MP3Lame getTargetFormats(AudioFormat.Encoding, AudioFormat):");
            TDebug.out("checking out possible target formats");
            TDebug.out("from: " + sourceFormat);
            TDebug.out("to  : " + targetEncoding);
        }
        if (isConversionSupported(targetEncoding, sourceFormat)) {
            AudioFormatSet result = new AudioFormatSet();
            for (AudioFormat targetFormat : getCollectionTargetFormats()) {
                //                if (TDebug.TraceAudioConverter) {
//                    TDebug.out("-checking target format " + targetFormat);
//                }
                if (doMatch(targetFormat.getSampleRate(),
                        sourceFormat.getSampleRate())
                        && targetFormat.getEncoding().equals(targetEncoding)
                        && doMatch(targetFormat.getChannels(),
                        sourceFormat.getChannels())) {
                    targetFormat = getDefaultTargetFormat(targetFormat,
                            sourceFormat, true);
//                    if (TDebug.TraceAudioConverter) {
//                        TDebug.out("-yes. added " + targetFormat);
//                    }
                    result.add(targetFormat);
//                } else {
//                    if (TDebug.TraceAudioConverter) {
//                        boolean e = targetFormat.getEncoding().equals(targetEncoding);
//                        TDebug.out("-no.\"" + targetFormat.getEncoding() + "\"==\"" + targetEncoding + "\" ?" + e);
//                    }
                }
            }

            if (TDebug.TraceAudioConverter) {
                TDebug.out("<found " + result.size() + " matching formats.");
            }
            return result.toAudioFormatArray();
        } else {
            if (TDebug.TraceAudioConverter) {
                TDebug.out("<returning empty array.");
            }
            return EMPTY_FORMAT_ARRAY;
        }
    }

    protected AudioFormat getDefaultTargetFormat(AudioFormat targetFormat,
                                                 AudioFormat sourceFormat, boolean allowNotSpecified) {
        // always set bits per sample to MPEG_BITS_PER_SAMPLE
        // set framerate to MPEG_FRAME_RATE, framesize to FRAME_SIZE
        // always retain sample rate
        float targetSampleRate = targetFormat.getSampleRate();
        if (targetSampleRate == AudioSystem.NOT_SPECIFIED) {
            targetSampleRate = sourceFormat.getSampleRate();
        }
        if ((!allowNotSpecified && targetSampleRate == AudioSystem.NOT_SPECIFIED)
                || (targetSampleRate != AudioSystem.NOT_SPECIFIED
                && sourceFormat.getSampleRate() != AudioSystem.NOT_SPECIFIED && targetSampleRate != sourceFormat.getSampleRate())) {
            throw new IllegalArgumentException("Illegal sample rate ("
                    + targetSampleRate + ") !");
        }
        int targetChannels = targetFormat.getChannels();
        if (targetChannels == AudioSystem.NOT_SPECIFIED) {
            targetChannels = sourceFormat.getChannels();
        }
        if ((!allowNotSpecified && targetChannels == AudioSystem.NOT_SPECIFIED)
                || (targetChannels != AudioSystem.NOT_SPECIFIED
                && sourceFormat.getChannels() != AudioSystem.NOT_SPECIFIED && targetChannels != sourceFormat.getChannels())) {
            throw new IllegalArgumentException("Illegal number of channels ("
                    + targetChannels + ") !");
        }
        AudioFormat newTargetFormat = new AudioFormat(
                targetFormat.getEncoding(), targetSampleRate,
                MPEG_BITS_PER_SAMPLE, targetChannels, getFrameSize(
                targetFormat.getEncoding(), targetSampleRate,
                MPEG_BITS_PER_SAMPLE, targetChannels, MPEG_FRAME_RATE,
                false, 0), MPEG_FRAME_RATE, false, targetFormat.properties());
        return newTargetFormat;
    }

    // implementation from TSimpleFormatConversionProvider
    protected int getFrameSize(AudioFormat.Encoding encoding, float sampleRate,
                               int sampleSize, int channels, float frameRate, boolean bigEndian,
                               int oldFrameSize) {
        if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED)
                || encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            return super.getFrameSize(encoding, sampleRate, sampleSize,
                    channels, frameRate, bigEndian, oldFrameSize);
        }
        // return default frame rate for MPEG
        return MPEG_FRAME_RATE;
    }

    public static class EncodedMpegAudioInputStream extends
            TAsynchronousFilteredAudioInputStream {
        private InputStream pcmStream;
        private Lame encoder;

        private byte[] pcmBuffer;
        private byte[] encodedBuffer;

        public EncodedMpegAudioInputStream(AudioFormat targetFormat,
                                           AudioInputStream sourceStream) {
            super(targetFormat, -1);
            pcmStream = sourceStream;
            encoder = new Lame(sourceStream.getFormat(), targetFormat);
            this.format = encoder.getEffectiveFormat();
            pcmBuffer = new byte[encoder.getPCMBufferSize()];
            encodedBuffer = new byte[encoder.getMP3BufferSize()];
        }

        public void execute() {
            try {
                if (encoder == null) {
                    if (TDebug.TraceAudioConverter) {
                        TDebug.out("mp3 lame encoder is null (already at end of stream)");
                    }
                    getCircularBuffer().close();
                    return;
                }
                int encodedBytes = 0;
                byte[] buffer = null;
                while (encodedBytes == 0 && encoder != null) {
                    int readBytes = pcmStream.read(pcmBuffer);
                    // what to do in case of readBytes==0 ?
                    if (readBytes > 0) {
                        encodedBytes = encoder.encodeBuffer(pcmBuffer, 0,
                                readBytes, encodedBuffer);
                        buffer = encodedBuffer;
                    } else {
                        // take the larger buffer for the remaining frame(s)
                        buffer = encodedBuffer.length > pcmBuffer.length ? encodedBuffer
                                : pcmBuffer;
                        encodedBytes = encoder.encodeFinish(buffer);
                        encoder.close();
                        encoder = null;
                    }
                }
                if (encodedBytes > 0) {
                    getCircularBuffer().write(buffer, 0, encodedBytes);
                }
                if (encoder == null) {
                    getCircularBuffer().close();
                }
            } catch (ArrayIndexOutOfBoundsException | IOException e) {
                if (TDebug.TraceAudioConverter || TDebug.TraceAllExceptions) {
                    TDebug.out(e);
                }
            }
        }

        public void close() throws IOException {
            super.close();
            pcmStream.close();
            if (encoder != null) {
                encoder.encodeFinish(null);
                encoder.close();
                encoder = null;
            }
        }
    }
}
