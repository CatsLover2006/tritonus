/*
 * SyncState.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 - 2001 by Matthias Pfisterer
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

package org.tritonus.lowlevel.ogg;

import org.tritonus.share.TDebug;


/**
 * Wrapper for ogg_sync_state.
 */
public class SyncState {
    static {
        Ogg.loadNativeLibrary();
        if (TDebug.TraceOggNative) {
            setTrace(true);
        }
    }


    /**
     * Holds the pointer to ogg_sync_state
     * for the native code.
     * This must be long to be 64bit-clean.
     */
    @SuppressWarnings("unused")
    private long m_lNativeHandle;


    public SyncState() {
        if (TDebug.TraceOggNative) {
            TDebug.out("SyncState.<init>(): begin");
        }
        int nReturn = malloc();
        if (nReturn < 0) {
            throw new RuntimeException("malloc of ogg_sync_state failed");
        }
        if (TDebug.TraceOggNative) {
            TDebug.out("SyncState.<init>(): end");
        }
    }


    protected void finalize() {
        // TODO: call free()
        // call super.finalize() first or last?
        // and introduce a flag if free() has already been called?
    }


    private native int malloc();

    public native void free();


    /**
     * Calls ogg_sync_init().
     */
    public native void init();


    /**
     * Calls ogg_sync_clear().
     */
    public native void clear();


    /**
     * Calls ogg_sync_reset().
     */
    public native void reset();


    /**
     * Calls ogg_sync_destroy().
     */
    public native void destroy();


    /**
     * Calls ogg_sync_buffer()
     * and ogg_sync_wrote().
     */
    public native int write(byte[] abBuffer, int nBytes);


    /**
     * Calls ogg_sync_pageseek().
     */
    public native int pageseek(Page page);


    /**
     * Calls ogg_sync_pageout().
     */
    public native int pageOut(Page page);


    private static native void setTrace(boolean bTrace);
}


/* SyncState.java */
