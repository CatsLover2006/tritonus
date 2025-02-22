/*
 * Cdparanoia.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 by Matthias Pfisterer
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

package org.tritonus.lowlevel.cdda.cdparanoia;

import org.tritonus.share.TDebug;


/**
 * Reading audio CDs using libcdparanoia.
 */
public class Cdparanoia {
    static {
        if (TDebug.TraceCdda) {
            TDebug.out("Cdparanoia.<clinit>(): loading native library tritonuscdparanoia");
        }
        System.loadLibrary("tritonuscdparanoia");
        if (TDebug.TraceCdda) {
            TDebug.out("Cdparanoia.<clinit>(): loaded");
        }
        setTrace(TDebug.TraceCddaNative);

        if (Boolean.getBoolean("tritonus.DisableParanoia")) {
            setParanoiaMode(false);
        }
    }


    /*
     * This holds a pointer for the native code -
     * do not touch!
     */
    @SuppressWarnings("unused")
    private long m_lNativeHandle;


    public Cdparanoia(String strDevice) {
        if (TDebug.TraceCdda) {
            TDebug.out("Cdparanoia.<init>: begin");
        }
        int nResult = open(strDevice);
        if (nResult < 0) {
            throw new RuntimeException("cannot open device '" + strDevice + "'");
        }
        if (TDebug.TraceCdda) {
            TDebug.out("Cdparanoia.<init>: end");
        }
    }


    /**
     * Searches the device.
     * Calls cdda_identify().
     *
     * @return 0 on success, negative values on error.
     */
    private native int find(String strDevice);

    /**
     * Opens and initializes the device.
     * Calls cdda_open(), paranoia_init()
     * and paranoia_modeset().
     *
     * @return 0 on success, negative values on error.
     */
    private native int open(String strDevice);

    /**
     * Closes the device.
     * Calls cdda_close().
     */
    public native void close();


    /* Read the table of contents.
     anValues[0] first track
     anValues[1] last track

     anStartTrack[x] start sector of the track x.
     anType[x] type of track x.
    */
    public native int readTOC(int[] anValues,
                              int[] anStartFrame,
                              int[] anLength,
                              int[] anType,
                              boolean[] abAudio,
                              boolean[] abCopy,
                              boolean[] abPre,
                              int[] anChannels);


    public native int prepareTrack(int nTrack);


    /**
     * Reads one or more raw frames from the CD.
     * This call reads <CODE>nCount</CODE> frames from
     * the track that has been set by
     * <CODE>prepareTrack()</CODE>.
     * <CODE>abData</CODE>  has to be big enough to hold the
     * amount of data requested (<CODE>2352 * nCount</CODE> bytes).
     */
    public native int readNextFrame(int nCount, byte[] abData);


    private static native void setTrace(boolean bTrace);


    /**
     * Set the paranoia level.
     * This setting influences the value that is used in the call
     * 'paranoia_modeset(cdrom_paranoia*, int [mode])'.
     * If set to true a hard-coded default value will be used.
     * (Currently 'PARANOIA_MODE_FULL ^ PARANOIA_MODE_NEVERSKIP', but
     * for definitive answers, look it up in src/lib/cdparanoia/org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia.c).
     * If set to false, 'PARANOIA_MODE_DISABLE' will be used.
     * Note that currently, changing this value only has an effect prior
     * to opening the device.
     */
    private static native void setParanoiaMode(boolean bPoranoiaMode);
}


/* Cdparanoia.java */
