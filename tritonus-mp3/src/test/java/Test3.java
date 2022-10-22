/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.SourceDataLine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tritonus.sampled.convert.javalayer.MpegFormatConversionProvider;
import org.tritonus.share.TDebug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * tritonus-mp3.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/11 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
class Test3 {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod", "org\\.tritonus\\.share\\.TDebug#out");

        TDebug.TraceAudioConverter = false;
        TDebug.TraceCircularBuffer = false;
        TDebug.TraceAudioFileReader = false;
    }

    @BeforeAll
    static void setup() throws Exception {
        Files.createDirectories(Paths.get("tmp"));
    }

    @Property
    String inFile = "src/test/resources/test.mp3";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        for (AudioFileFormat.Type type : AudioSystem.getAudioFileTypes()) {
            System.err.println(type);
        }
        Test3 app = new Test3();
        PropsEntity.Util.bind(app);
        app.test2();
    }

    @Test
    @DisplayName("decoding")
    void test2() throws Exception {
        AudioInputStream originalAudioInputStream = AudioSystem.getAudioInputStream(Paths.get(inFile).toFile());
        AudioFormat originalAudioFormat = originalAudioInputStream.getFormat();
        System.err.println(originalAudioFormat);
        AudioFormat targetAudioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalAudioFormat.getSampleRate(),
                16,
                2,
                4,
                originalAudioFormat.getSampleRate(),
                false);
        System.err.println(targetAudioFormat);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(targetAudioFormat, originalAudioInputStream);
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.addLineListener(event -> {
            if (event.getType().equals(LineEvent.Type.START)) {
                System.err.println("play");
            }
            if (event.getType().equals(LineEvent.Type.STOP)) {
                System.err.println("done");
            }
        });

        byte[] buf = new byte[8192];
        line.open(audioFormat, buf.length);
        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        double gain = .02d; // number between 0 and 1 (loudest)
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
        line.start();
        int r;
        while (true) {
            r = audioInputStream.read(buf, 0, buf.length);
            if (r < 0) {
                break;
            }
            line.write(buf, 0, r);
        }
        line.drain();
        line.close();
    }

    // TODO FileChannel#transfarXXX doesn't work???
    @Test
    @DisplayName("encoding")
    void test1() throws Exception {
        URL url = Test3.class.getResource("/test.wav");
        AudioInputStream ais = AudioSystem.getAudioInputStream(url);
        AudioFormat inFormat = ais.getFormat();
        System.err.println(inFormat);
        AudioFormat outFormat = new AudioFormat(
                MpegFormatConversionProvider.MPEG1L3,
                -1f,
                -1,
                2,
                -1,
                -1f,
                false);
        System.err.println(outFormat);
        AudioInputStream aout = AudioSystem.getAudioInputStream(outFormat, ais);

        OutputStream fos = Files.newOutputStream(Paths.get("tmp", "out.mp3"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        byte[] buf = new byte[8192];
        while (true) {
            int r = aout.read(buf, 0, buf.length);
            if (r < 0) {
                break;
            }
            fos.write(buf, 0, r);
        }
        fos.close();
        aout.close();

        assertEquals(Checksum.getChecksum(Paths.get("tmp", "out.mp3")), Checksum.getChecksum(Paths.get(inFile)));
    }
}

/* */
