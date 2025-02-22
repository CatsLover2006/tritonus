/*
 * GetVoiceStatusTestCase.java
 */

/*
 *  Copyright (c) 2006 by Matthias Pfisterer
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

package org.tritonus.test.api.midi.synthesizer;

import javax.sound.midi.Synthesizer;
import javax.sound.midi.VoiceStatus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test for javax.sound.midi.Synthesizer.getLatency().
 */
public class GetVoiceStatusTestCase
        extends BaseSynthesizerTestCase {
    protected void checkSynthesizer(Synthesizer synth)
            throws Exception {
        VoiceStatus[] status;
        synth.open();
        try {
            status = synth.getVoiceStatus();
            assertNotNull(status,
                    constructErrorMessage(synth, "getVoiceStatus() result null", true));
            int numVoices = status.length;
            assertTrue(numVoices == 0 || numVoices == synth.getMaxPolyphony(),
                    constructErrorMessage(synth, "getVoiceStatus() result has wrong length", true));
        } finally {
            synth.close();
        }
    }
}


/* GetVoiceStatusTestCase.java */
