/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.SysexMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tritonus.midi.device.fluidsynth.FluidSynthesizer;
import org.tritonus.share.TDebug;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080701 nsano initial version <br>
 */
@PropsEntity(url = "file:local.properties")
public class Test1 {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod",
                "org\\.tritonus\\.share\\.TDebug#out");
    }

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "test.midi")
    String midi = "../tritonus-midishare/src/test/resources/sounds/trippygaia1.mid";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }

        TDebug.TraceMidiDeviceProvider = true;
    }

    @Test
    void test() throws Exception {
        main(new String[] {midi});
    }

    /**
     * @param args 0: midi
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
Debug.println("midi: " + args[0]);

        Sequence sequence = MidiSystem.getSequence(file);
System.err.println("sequence: " + sequence);

        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
System.err.println("synthesizer: " + synthesizer);
        if (synthesizer instanceof FluidSynthesizer) {
            float gain = 0.02f;
            ((FluidSynthesizer) synthesizer).setGain(gain);
Debug.println("set gain: " + gain);
        } else {
            throw new IllegalStateException("this is FluidSynthesizer test");
        }

        // if MidiSystem#getSequencer()'s argument connected is set true
        // the sequencer uses a synthesizer created by MidiSystem instead of yours.
        Sequencer sequencer = MidiSystem.getSequencer(false);
        sequencer.open();
System.err.println("sequencer: " + sequencer);
        // tell the sequencer to use your synthesizer instance which volume is downed
        sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());

        CountDownLatch countDownLatch = new CountDownLatch(1);
        MetaEventListener mel = meta -> {
            System.err.println("META: " + meta.getType());
            if (meta.getType() == 47) {
                countDownLatch.countDown();
            }
        };

        sequencer.setSequence(sequence);
        sequencer.addMetaEventListener(mel);
        sequencer.start();
System.err.println("START");
if (!System.getProperty("vavi.test", "").equals("ide")) {
 Thread.sleep(5 * 1000);
 sequencer.stop();
 Debug.println("STOP");
} else {
        countDownLatch.await();
}
System.err.println("END");
        sequencer.stop();
        sequencer.removeMetaEventListener(mel);
        sequencer.close();
    }

    /** TODO doesn't work? */
    static void volume(Receiver receiver, float volume) throws InvalidMidiDataException {
        int value = (int) (16383 * volume);
        byte[] data = { (byte) 0xf0, 0x7f, 0x7f, 0x04, 0x01, (byte) (value & 0x7f), (byte) ((value >> 7) & 0x7f), (byte) 0xf7 };
        MidiMessage sysex = new SysexMessage(data, data.length);
        receiver.send(sysex, -1);
    }
}

/* */
