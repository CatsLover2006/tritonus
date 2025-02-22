/*
 * BufferTestCase.java
 */

/*
 *  Copyright (c) 2005 by Matthias Pfisterer
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

package org.tritonus.test.tritonus.lowlevel.pogg;

import org.junit.jupiter.api.Test;
import org.tritonus.lowlevel.pogg.Buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests for class org.tritonus.lowlevel.pogg.Buffer.
 */
public class BufferTestCase {
    private static final int[] mask =
            {
                    0x00000000, 0x00000001, 0x00000003, 0x00000007,
                    0x0000000f, 0x0000001f, 0x0000003f, 0x0000007f,
                    0x000000ff, 0x000001ff, 0x000003ff, 0x000007ff,
                    0x00000fff, 0x00001fff, 0x00003fff, 0x00007fff,
                    0x0000ffff, 0x0001ffff, 0x0003ffff, 0x0007ffff,
                    0x000fffff, 0x001fffff, 0x003fffff, 0x007fffff,
                    0x00ffffff, 0x01ffffff, 0x03ffffff, 0x07ffffff,
                    0x0fffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,
                    0xffffffff
            };

    private static final int[] testbuffer1 =
            {
                    18, 12, 103948, 4325, 543, 76, 432, 52,
                    3, 65, 4, 56, 32, 42, 34, 21,
                    1, 23, 32, 546, 456, 7, 567, 56,
                    8, 8, 55, 3, 52, 342, 341, 4,
                    265, 7, 67, 86, 2199, 21, 7, 1,
                    5, 1, 4
            };

    private static final int test1size = testbuffer1.length;

    private static final int[] testbuffer2 =
            {
                    216531625, 1237861823, 56732452, 131,
                    3212421, 12325343, 34547562, 12313212,
                    1233432, 534, 5, 346435231,
                    14436467, 7869299, 76326614, 167548585,
                    85525151, 0, 12321, 1,
                    349528352
            };

    private static final int test2size = 21;

    private static final int[] testbuffer3 =
            {
                    1, 0, 14, 0, 1, 0, 12, 0,
                    1, 0, 0, 0, 1, 1, 0, 1,
                    0, 1, 0, 1, 0, 1, 0, 1,
                    0, 1, 0, 0, 1, 1, 1, 1,
                    1, 0, 0, 1, 0, 1, 30, 1,
                    1, 1, 0, 0, 1, 0, 0, 0,
                    12, 0, 11, 0, 1, 0, 0, 1
            };
    private static final int test3size = 56;

    private static final int[] large =
            {
                    2136531625, 2137861823, 56732452, 131,
                    3212421, 12325343, 34547562, 12313212,
                    1233432, 534, 5, 2146435231,
                    14436467, 7869299, 76326614, 167548585,
                    85525151, 0, 12321, 1,
                    2146528352
            };

    private static final int onesize = 33;
    private static byte[] one =
            {
                    (byte) 146, 25, 44, (byte) 151, (byte) 195, 15, (byte) 153, (byte) 176,
                    (byte) 233, (byte) 131, (byte) 196, 65, 85, (byte) 172, 47, 40,
                    34, (byte) 242, (byte) 223, (byte) 136, 35, (byte) 222, (byte) 211, 86,
                    (byte) 171, 50, (byte) 225, (byte) 135, (byte) 214, 75, (byte) 172, (byte) 223,
                    4
            };

    private static final int twosize = 6;
    private static byte[] two =
            {
                    61, (byte) 255, (byte) 255, (byte) 251, (byte) 231, 29
            };

    private static final int threesize = 54;
    private static final byte[] three =
            {
                    (byte) 169, 2, (byte) 232, (byte) 252, 91, (byte) 132, (byte) 156, 36,
                    89, 13, 123, (byte) 176, (byte) 144, 32, (byte) 254, (byte) 142,
                    (byte) 224, 85, 59, 121, (byte) 144, 79, 124, 23,
                    67, 90, 90, (byte) 216, 79, 23, 83, 58,
                    (byte) 135, (byte) 196, 61, 55, (byte) 129, (byte) 183, 54, 101,
                    100, (byte) 170, 37, 127, 126, 10, 100, 52,
                    4, 14, 18, 86, 77, 1
            };

    private static final int foursize = 38;
    private static byte[] four =
            {
                    18, 6, (byte) 163, (byte) 252, 97, (byte) 194, 104, (byte) 131,
                    32, 1, 7, 82, (byte) 137, 42, (byte) 129, 11,
                    72, (byte) 132, 60, (byte) 220, 112, 8, (byte) 196, 109,
                    64, (byte) 179, 86, 9, (byte) 137, (byte) 195, (byte) 208, 122,
                    (byte) 169, 28, 2, (byte) 133, 0, 1
            };

    private static final int fivesize = 45;
    private static final byte[] five =
            {
                    (byte) 169, 2, (byte) 126, (byte) 139, (byte) 144, (byte) 172, 30, 4,
                    80, 72, (byte) 240, 59, (byte) 130, (byte) 218, 73, 62,
                    (byte) 241, 24, (byte) 210, 44, 4, 20, 0, (byte) 248,
                    116, 49, (byte) 135, 100, 110, (byte) 130, (byte) 181, (byte) 169,
                    84, 75, (byte) 159, 2, 1, 0, (byte) 132, (byte) 192,
                    8, 0, 0, 18, 22
            };

    private static final int sixsize = 7;
    private static final byte[] six =
            {
                    17, (byte) 177, (byte) 170, (byte) 242, (byte) 169, 19, (byte) 148
            };

    private Buffer o;
    private Buffer r;


    private static int ilog(int v) {
        int ret = 0;
        while (v > 0) {
            ret++;
            v >>>= 1;
        }
        return ret;
    }


    private void cliptest(int[] b, int bits,
                          byte[] comp, int compsize) {
        int bytes;
        int i;
        byte[] buffer;

        o.reset();
        for (i = 0; i < b.length; i++) {
            o.write(b[i], (bits != 0) ? bits : ilog(b[i]));
        }
        buffer = o.getBuffer();
        bytes = o.bytes();
        assertEquals(bytes, compsize, "wrong number of bytes!\n");

        for (i = 0; i < bytes; i++) {
            assertEquals(buffer[i], comp[i], "wrote incorrect value!\n");
            {
                //for (i = 0; i < bytes; i++)
                //fprintf(stderr,"%x %x\n",(int)buffer[i],(int)comp[i]);
            }
        }
        r.readInit(buffer, bytes);
        for (i = 0; i < b.length; i++) {
            int tbit = (bits != 0) ? bits : ilog(b[i]);
            assertTrue(r.look(tbit) != -1, "out of data!\n");
            assertEquals(b[i] & mask[tbit],
                    r.look(tbit), "looked at incorrect value!\n");
            if (tbit == 1)
                assertEquals(b[i] & mask[tbit],
                        r.look1(), "looked at single bit incorrect value!\n");
            if (tbit == 1) {
                assertEquals(b[i] & mask[tbit],
                        r.read1(), "read incorrect single bit value!\n");
            } else {
                assertEquals(b[i] & mask[tbit],
                        r.read(tbit), "read incorrect value!\n");
            }
        }
        assertEquals(r.bytes(), bytes, "leftover bytes after read!\n");
    }


    @Test
    public void testBuffer()
            throws Exception {
        byte[] buffer;
        int bytes, i;

        // TODO: should free them
        o = new Buffer();
        r = new Buffer();
        /* Test read/write together */
        /* Later we test against pregenerated bitstreams */
        o.writeInit();

        //fprintf(stderr,"\nSmall preclipped packing (LSb): ");
        cliptest(testbuffer1, 0, one, onesize);
        //fprintf(stderr,"ok.");

        // fprintf(stderr,"\nNull bit call (LSb): ");
        cliptest(testbuffer3, 0, two, twosize);
        // fprintf(stderr,"ok.");

        // fprintf(stderr,"\nLarge preclipped packing (LSb): ");
        cliptest(testbuffer2, 0, three, threesize);
        // fprintf(stderr,"ok.");

        // fprintf(stderr,"\n32 bit preclipped packing (LSb): ");
        o.reset();
        for (i = 0; i < test2size; i++) {
            o.write(large[i], 32);
        }
        buffer = o.getBuffer();
        bytes = o.bytes();
        r.readInit(buffer, bytes);
        for (i = 0; i < test2size; i++) {
            assertTrue(r.look(32) != -1, "out of data. failed!");
            assertEquals(large[i],
                    r.look(32), "read incorrect value!\n");
            {
                // fprintf(stderr,"%ld != %ld (%lx!=%lx):",oggpack_look(&r, 32),large[i],
                // oggpack_look(&r, 32),large[i]);
            }
            r.adv(32);
        }
        assertEquals(bytes,
                r.bytes(), "leftover bytes after read!\n");
        // fprintf(stderr,"ok.");

        // fprintf(stderr,"\nSmall unclipped packing (LSb): ");
        cliptest(testbuffer1, 7, four, foursize);
        // fprintf(stderr,"ok.");

        // fprintf(stderr,"\nLarge unclipped packing (LSb): ");
        cliptest(testbuffer2, 17, five, fivesize);
        // fprintf(stderr,"ok.");

        // fprintf(stderr,"\nSingle bit unclipped packing (LSb): ");
        cliptest(testbuffer3, 1, six, sixsize);
        // fprintf(stderr,"ok.");

        // fprintf(stderr,"\nTesting read past end (LSb): ");
        r.readInit(new byte[8], 8);
        for (i = 0; i < 64; i++) {
            assertEquals(r.read(1), 0, "failed; got -1 prematurely.\n");
        }
        assertEquals(r.look(1), -1, "failed; lookahead past end without -1.\n");
        assertEquals(r.read(1), -1, "failed; read past end without -1.\n");
        r.readInit(new byte[8], 8);
        assertEquals(0,
                r.read(30), "failed 2; got -1 prematurely.\n");
        assertEquals(0,
                r.read(16), "failed 2; got -1 prematurely.\n");

        assertEquals(0,
                r.look(18), "failed 3; got -1 prematurely.\n");
        assertEquals(0,
                r.look(18), "failed 3; got -1 prematurely.\n");
        assertTrue(r.look(19) == -1 && r.look(19) == -1,
                "failed; read past end without -1.\n");
        assertEquals(-1,
                r.look(32), "failed; read past end without -1.\n");
        // fprintf(stderr,"ok.\n");
    }
}


/* BufferTestCase.java */
