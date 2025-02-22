/*
 * AlsaDataLineMixerProvider.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2002 by Matthias Pfisterer
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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.sampled.mixer.alsa;

import org.tritonus.lowlevel.alsa.Alsa;
import org.tritonus.lowlevel.alsa.AlsaCtl;
import org.tritonus.lowlevel.alsa.AlsaCtlCardInfo;
import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.mixer.TMixerProvider;


public class AlsaDataLineMixerProvider
        extends TMixerProvider {
    private static boolean sm_bInitialized = false;


    public AlsaDataLineMixerProvider() {
        super();
        if (TDebug.TraceMixerProvider) {
            TDebug.out("AlsaDataLineMixerProvider.<init>(): begin");
        }
        if (!sm_bInitialized && !isDisabled()) {
            if (!Alsa.isLibraryAvailable()) {
                disable();
            } else {
                staticInit();
                sm_bInitialized = true;
            }
        } else {
            if (TDebug.TraceMixerProvider) {
                TDebug.out("AlsaDataLineMixerProvider.<init>(): already initialized or disabled");
            }
        }

        if (TDebug.TraceMixerProvider) {
            TDebug.out("AlsaDataLineMixerProvider.<init>(): end");
        }
    }


    protected void staticInit() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("AlsaDataLineMixerProvider.staticInit(): begin");
        }
        int[] anCards = AlsaCtl.getCards();
        if (TDebug.TraceMixerProvider) {
            TDebug.out("AlsaDataLineMixerProvider.staticInit(): num cards: " + anCards.length);
        }
        for (int i = 0; i < anCards.length; i++) {
            if (TDebug.TraceMixerProvider) {
                TDebug.out("AlsaDataLineMixerProvider.staticInit():card #" + i + ": " + anCards[i]);
            }
            if (TDebug.TraceMixerProvider) {
                TDebug.out("AlsaDataLineMixerProvider.staticInit(): creating Ctl object...");
            }
            String strPcmName = "hw:" + anCards[i];
            // String strPcmName = AlsaDataLineMixer.getPcmName(anCards[i]);
            AlsaCtl ctl;
            try {
                ctl = new AlsaCtl(strPcmName, 0);
            } catch (Exception e) {
                if (TDebug.TraceMixerProvider || TDebug.TraceAllExceptions) {
                    TDebug.out(e);
                }
                continue;
            }
            if (TDebug.TraceMixerProvider) {
                TDebug.out("AlsaDataLineMixerProvider.staticInit(): calling getCardInfo()...");
            }
            AlsaCtlCardInfo cardInfo = new AlsaCtlCardInfo();
            ctl.getCardInfo(cardInfo);
            if (TDebug.TraceMixerProvider) {
                TDebug.out("AlsaDataLineMixerProvider.staticInit(): ALSA sound card:");
                TDebug.out("AlsaDataLineMixerProvider.staticInit(): card: " + cardInfo.getCard());
                TDebug.out("AlsaDataLineMixerProvider.staticInit(): id: " + cardInfo.getId());
            }
            int[] anDevices = ctl.getPcmDevices();
            if (TDebug.TraceMixerProvider) {
                TDebug.out("AlsaDataLineMixerProvider.staticInit(): num devices: " + anDevices.length);
            }
            // TODO: combine devices into one AlsaDataLineMixer?
            // pass device number to AlsaDataLineMixer constructor?
            for (int nDevice = 0; nDevice < anDevices.length; nDevice++) {
                if (TDebug.TraceMixerProvider) {
                    TDebug.out("AlsaDataLineMixerProvider.staticInit(): device #" + nDevice + ": " + anDevices[nDevice]);
                }
            }
            // ctl.close();

   /*
     We do not use strPcmName because the mixer may choose to open as 'plughw', while for ctl, the device name always has to be 'hw'.
   */
            AlsaDataLineMixer mixer = new AlsaDataLineMixer(anCards[i]);
            super.addMixer(mixer);
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("AlsaDataLineMixerProvider.staticInit(): end");
        }
    }
}


/* AlsaDataLineMixerProvider.java */
