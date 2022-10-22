/*
 * AlsaSeqQueueStatus.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2001 by Matthias Pfisterer
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

package org.tritonus.lowlevel.alsa;

import org.tritonus.share.TDebug;


public class AlsaSeqQueueStatus {
    static {
        Alsa.loadNativeLibrary();
        if (TDebug.TraceAlsaSeqNative) {
            setTrace(true);
        }
    }


    /**
     * Holds the pointer to snd_seq_queue_status_t
     * for the native code.
     * This must be long to be 64bit-clean.
     */
    /*private*/ long m_lNativeHandle;


    public AlsaSeqQueueStatus() {
        if (TDebug.TraceAlsaSeqNative) {
            TDebug.out("AlsaSeq.QueueStatus.<init>(): begin");
        }
        int nReturn = malloc();
        if (nReturn < 0) {
            throw new RuntimeException("malloc of port_info failed");
        }
        if (TDebug.TraceAlsaSeqNative) {
            TDebug.out("AlsaSeq.QueueStatus.<init>(): end");
        }
    }


    protected void finalize() {
        // TODO: call free()
        // call super.finalize() first or last?
        // and introduce a flag if free() has already been called?
    }


    private native int malloc();

    public native void free();

    public native int getQueue();

    public native int getEvents();

    public native long getTickTime();

    public native long getRealTime();

    public native int getStatus();

    private static native void setTrace(boolean bTrace);
}


/* AlsaSeqQueueStatus.java */
