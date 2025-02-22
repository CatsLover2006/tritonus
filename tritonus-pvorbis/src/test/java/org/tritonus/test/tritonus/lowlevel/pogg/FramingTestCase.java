/*
 * FramingTestCase.java
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

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.tritonus.lowlevel.pogg.Packet;
import org.tritonus.lowlevel.pogg.Page;
import org.tritonus.lowlevel.pogg.StreamState;
import org.tritonus.lowlevel.pogg.SyncState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests for classes org.tritonus.lowlevel.pogg.* except Buffer.
 */
public class FramingTestCase {
    /* 17 only */
    private static final int[] head1_0 = {0x4f, 0x67, 0x67, 0x53, 0, 0x06,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0x15, 0xed, 0xec, 0x91,
            1,
            17};

    /* 17, 254, 255, 256, 500, 510, 600 byte, pad */
    private static final int[] head1_1 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0x59, 0x10, 0x6c, 0x2c,
            1,
            17};
    private static final int[] head2_1 = {0x4f, 0x67, 0x67, 0x53, 0, 0x04,
            0x07, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0x89, 0x33, 0x85, 0xce,
            13,
            254, 255, 0, 255, 1, 255, 245, 255, 255, 0,
            255, 255, 90};

    /* nil packets; beginning,middle,end */
    private static final int[] head1_2 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0xff, 0x7b, 0x23, 0x17,
            1,
            0};
    private static final int[] head2_2 = {0x4f, 0x67, 0x67, 0x53, 0, 0x04,
            0x07, 0x28, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0x5c, 0x3f, 0x66, 0xcb,
            17,
            17, 254, 255, 0, 0, 255, 1, 0, 255, 245, 255, 255, 0,
            255, 255, 90, 0};

    /* large initial packet */
    private static final int[] head1_3 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0x01, 0x27, 0x31, 0xaa,
            18,
            255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 10};

    private static final int[] head2_3 = {0x4f, 0x67, 0x67, 0x53, 0, 0x04,
            0x07, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0x7f, 0x4e, 0x8a, 0xd2,
            4,
            255, 4, 255, 0};


    /* continuing packet test */
    private static final int[] head1_4 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0xff, 0x7b, 0x23, 0x17,
            1,
            0};

    private static final int[] head2_4 = {0x4f, 0x67, 0x67, 0x53, 0, 0x00,
            0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0x34, 0x24, 0xd5, 0x29,
            17,
            255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255};

    private static final int[] head3_4 = {0x4f, 0x67, 0x67, 0x53, 0, 0x05,
            0x07, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 2, 0, 0, 0,
            0xc8, 0xc3, 0xcb, 0xed,
            5,
            10, 255, 4, 255, 0};


    /* page with the 255 segment limit */
    private static final int[] head1_5 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0xff, 0x7b, 0x23, 0x17,
            1,
            0};

    private static final int[] head2_5 = {0x4f, 0x67, 0x67, 0x53, 0, 0x00,
            0x07, 0xfc, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0xed, 0x2a, 0x2e, 0xa7,
            255,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10};

    private static final int[] head3_5 = {0x4f, 0x67, 0x67, 0x53, 0, 0x04,
            0x07, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 2, 0, 0, 0,
            0x6c, 0x3b, 0x82, 0x3d,
            1,
            50};


    /* packet that overspans over an entire page */
    private static final int[] head1_6 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0xff, 0x7b, 0x23, 0x17,
            1,
            0};

    private static final int[] head2_6 = {0x4f, 0x67, 0x67, 0x53, 0, 0x00,
            0x07, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0x3c, 0xd9, 0x4d, 0x3f,
            17,
            100, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255};

    private static final int[] head3_6 = {0x4f, 0x67, 0x67, 0x53, 0, 0x01,
            0x07, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 2, 0, 0, 0,
            0xbd, 0xd5, 0xb5, 0x8b,
            17,
            255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255};

    private static final int[] head4_6 = {0x4f, 0x67, 0x67, 0x53, 0, 0x05,
            0x07, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 3, 0, 0, 0,
            0xef, 0xdd, 0x88, 0xde,
            7,
            255, 255, 75, 255, 4, 255, 0};

    /* packet that overspans over an entire page */
    private static final int[] head1_7 = {0x4f, 0x67, 0x67, 0x53, 0, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
            0xff, 0x7b, 0x23, 0x17,
            1,
            0};

    private static final int[] head2_7 = {0x4f, 0x67, 0x67, 0x53, 0, 0x00,
            0x07, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 1, 0, 0, 0,
            0x3c, 0xd9, 0x4d, 0x3f,
            17,
            100, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255};

    private static final int[] head3_7 = {0x4f, 0x67, 0x67, 0x53, 0, 0x05,
            0x07, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x02, 0x03, 0x04, 2, 0, 0, 0,
            0xd4, 0xe0, 0x60, 0xe5,
            1, 0};


    private static int sequence = 0;
    private static int lastno = 0;


    private void checkpacket(Packet op, int len, int no, int pos) {
        assertEquals(len,
                op.getData().length, "incorrect packet length!\n");
        assertEquals(pos,
                op.getGranulePos(), "incorrect packet position!\n");
  /* packet number just follows sequence/gap; adjust the input number
     for that */
        if (no == 0) {
            sequence = 0;
        } else {
            sequence++;
            if (no > lastno + 1)
                sequence++;
        }
        lastno = no;
        assertEquals(sequence,
                op.getPacketNo(), "incorrect packet sequence");

        /* Test data */
        byte[] abContent = op.getData();
        for (int j = 0; j < abContent.length; j++) {
            assertEquals((j + no) & 0xFF,
                    abContent[j] & 0xFF, "body data mismatch (1) at pos " + j + ":");
        }
    }


    void check_page(byte[] data, int nOffset, int[] header, Page og) {
        int j;
        byte[] abHeader = og.getHeader();
        byte[] abBody = og.getBody();

        /* Test data */
        for (j = 0; j < abBody.length; j++) {
            assertEquals(data[j + nOffset],
                    abBody[j], "body data mismatch (2) at pos %ld:");
        }

        /* Test header */
        for (j = 0; j < abHeader.length; j++) {
            assertEquals(header[j],
                    (abHeader[j] & 0xFF), "header content mismatch at pos " + j + ":");
        }
        assertEquals(header[26] + 27,
                abHeader.length, "header length incorrect! (%ld!=%d)\n");
    }


    private void copy_page(Page og) {
        byte[] abHeader = og.getHeader();
        byte[] abBody = og.getBody();
        og.setData(abHeader, 0, abHeader.length,
                abBody, 0, abBody.length);
    }


    private void writePageToSyncState(Page page, SyncState sync) {
        byte[] abPageHeader = page.getHeader();
        byte[] abPageBody = page.getBody();
        sync.write(abPageHeader, abPageHeader.length);
        sync.write(abPageBody, abPageBody.length);
    }


    private void writeToSyncState(byte[] abData, int nOffset, int nLength,
                                  SyncState sync) {
        if (nOffset > 0) {
            byte[] abTmp = new byte[nLength];
            System.arraycopy(abData, nOffset, abTmp, 0, abTmp.length);
            abData = abTmp;
        }
        sync.write(abData, nLength);
    }


    private boolean equals(Packet p1, Packet p2) {
        return Arrays.equals(p1.getData(), p2.getData()) &&
                p1.isBos() == p2.isBos() &&
                p1.isEos() == p2.isEos() &&
                p1.getGranulePos() == p2.getGranulePos() &&
                p1.getPacketNo() == p2.getPacketNo();
    }


    private boolean equals(byte[] b1, int nOffset1,
                           byte[] b2, int nOffset2,
                           int nLength) {
        if (nOffset1 + nLength > b1.length || nOffset2 + nLength > b2.length)
            return false;
        for (int i = 0; i < nLength; i++) {
            if (b1[nOffset1 + i] != b2[nOffset2 + i])
                return false;
        }
        return true;
    }


    private void test_pack(int[] pl, int[][] headers) {
        StreamState os_en = new StreamState();
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        byte[] data = new byte[1024 * 1024]; /* for scripted test cases only */
        int inptr = 0;
        int outptr = 0;
        int deptr = 0;
        int depacket = 0;
        long granule_pos = 7;
        int pageno = 0;
        int i, j, packets;
        int pageout = 0;
        boolean eosflag = false;
        boolean bosflag = false;

        os_en.init(0x04030201);
        os_de.init(0x04030201);
        oy.init();
        os_en.reset();
        os_de.reset();
        oy.reset();

        packets = pl.length;
        //System.out.println("packets: " + packets);
        for (i = 0; i < packets; i++) {
            /* construct a test packet */
            Packet op = new Packet();
            int len = pl[i];

            byte[] abPacketData = new byte[len];
            for (j = 0; j < len; j++)
                abPacketData[j] = (byte) (i + j);
            System.arraycopy(abPacketData, 0, data, inptr, len);
            inptr += len;
            op.setData(abPacketData, 0, len);
            op.setFlags(false, i + 1 == packets, granule_pos, 0);

            granule_pos += 1024;


            /* submit the test packet */
            os_en.packetIn(op);

            /* retrieve any finished pages */
            Page og = new Page();

            while (os_en.pageOut(og) != 0) {
                /* We have a page.  Check it carefully */

                //fprintf(stderr,"%ld, ",pageno);

                assertTrue(pageno < headers.length,
                        "coded too many pages!\n");

                check_page(data, outptr, headers[pageno], og);

                outptr += og.getBody().length;
                pageno++;

                /* have a complete page; submit it to sync/decode */

                Page og_de = new Page();
                Packet op_de = new Packet();
                Packet op_de2 = new Packet();
                writePageToSyncState(og, oy);

                while (oy.pageOut(og_de) > 0) {
                    /* got a page.  Happy happy.  Verify that it's good. */
                    // temporarily inserted
                    og_de.setChecksum();
                    // end insertion
                    check_page(data, deptr, headers[pageout], og_de);
                    deptr += og_de.getBody().length;
                    pageout++;

                    /* submit it to deconstitution */
                    os_de.pageIn(og_de);

                    /* packets out? */
                    while (os_de.packetPeek(op_de2) > 0) {
                        os_de.packetPeek(null);
                        os_de.packetOut(op_de); /* just catching them all */

                        /* verify peek and out match */
                        assertTrue(equals(op_de, op_de2),
                                "packetout != packetpeek! pos=%ld\n");

                        /* verify the packet! */
                        /* check data */
                        assertTrue(equals(data, depacket, op_de.getData(), 0, op_de.getData().length),
                                "packet data mismatch in decode! pos=%ld\n");
                        /* check bos flag */
                        if (bosflag) {
                            assertFalse(op_de.isBos(), "b_o_s flag incorrectly set on packet!\n");
                        } else {
                            assertTrue(op_de.isBos(),
                                    "b_o_s flag not set on packet!\n");
                        }
                        bosflag = true;
                        depacket += op_de.getData().length;

                        /* check eos flag */
                        assertFalse(eosflag, "Multiple decoded packets with eos flag!\n");

                        if (op_de.isEos())
                            eosflag = true;

                        /* check granulepos flag */
// 						if (op_de.granulepos!=-1)
// 						{
// 							fprintf(stderr," granule:%ld ",(long)op_de.granulepos);
// 						}
                    }
                }
            }
        }
        data = null;
        assertEquals(pageno, headers.length, "did not write last page!");
        assertEquals(pageout, headers.length, "did not decode last page!");
        assertEquals(inptr,
                outptr, "encoded page data incomplete!\n");
        assertEquals(inptr,
                deptr, "decoded page data incomplete!\n");
        assertEquals(inptr,
                depacket, "decoded packet data incomplete!\n");
        assertTrue(eosflag,
                "Never got a packet with EOS set!\n");
    }

    @Test
    public void testStreamEncoding0()
            throws Exception {
        /* 17 only */
        // fprintf(stderr,"testing single page encoding... ");
        test_pack(new int[] {17},
                new int[][] {head1_0});
    }

    @Test
    public void testStreamEncoding1()
            throws Exception {
        /* 17, 254, 255, 256, 500, 510, 600 byte, pad */
        // fprintf(stderr,"testing basic page encoding... ");
        test_pack(new int[] {17, 254, 255, 256, 500, 510, 600},
                new int[][] {head1_1, head2_1});
    }

    @Test
    public void testStreamEncoding2()
            throws Exception {
        /* nil packets; beginning,middle,end */
        // fprintf(stderr,"testing basic nil packets... ");
        test_pack(new int[] {0, 17, 254, 255, 0, 256, 0, 500, 510, 600, 0},
                new int[][] {head1_2, head2_2});
    }

    @Test
    public void testStreamEncoding3()
            throws Exception {
        /* large initial packet */
        // fprintf(stderr,"testing initial-packet lacing > 4k... ");
        test_pack(new int[] {4345, 259, 255},
                new int[][] {head1_3, head2_3});
    }

    @Test
    public void testStreamEncoding4()
            throws Exception {
        /* continuing packet test */
        // fprintf(stderr,"testing single packet page span... ");
        test_pack(new int[] {0, 4345, 259, 255},
                new int[][] {head1_4, head2_4, head3_4});
    }

    @Test
    public void testStreamEncoding5()
            throws Exception {
        /* page with the 255 segment limit */
        int[] packets = new int[]
                {
                        0, 10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10, 10, 10, 10, 50
                };
        // fprintf(stderr,"testing max packet segments... ");
        test_pack(packets,
                new int[][] {head1_5, head2_5, head3_5});
    }

    @Test
    public void testStreamEncoding6()
            throws Exception {
        /* packet that overspans over an entire page */
        // fprintf(stderr,"testing very large packets... ");
        test_pack(new int[] {0, 100, 9000, 259, 255},
                new int[][] {head1_6, head2_6, head3_6, head4_6});
    }

    @Test
    public void testStreamEncoding7()
            throws Exception {
        /* term only page.  why not? */
        // fprintf(stderr,"testing zero data page (1 nil packet)... ");
        test_pack(new int[] {0, 100, 4080},
                new int[][] {head1_7, head2_7, head3_7});
    }


    /**
     * Create six pages containing 12 packets for testing.
     */
    private Page[] createPages()
            throws Exception {
        StreamState os_en = new StreamState();
        os_en.init(0x04030201);

        /* build a bunch of pages for testing */
        int[] pl = new int[] {0, 100, 4079, 2956, 2057, 76, 34, 912, 0, 234, 1000, 1000, 1000, 300};
        Page[] og = new Page[5];
        for (int i = 0; i < og.length; i++)
            og[i] = new Page();

        os_en.reset();

        for (int i = 0; i < pl.length; i++) {
            Packet op = new Packet();
            int len = pl[i];

            byte[] abPacketData = new byte[len];
            for (int j = 0; j < len; j++) {
                abPacketData[j] = (byte) (i + j);
            }
            op.setData(abPacketData, 0, len);
            op.setFlags(false, i + 1 == pl.length, (i + 1) * 1000, 0);

            os_en.packetIn(op);
        }


        /* retrieve finished pages */
        for (int i = 0; i < 5; i++) {
            assertTrue(os_en.pageOut(og[i]) != 0,
                    "Too few pages output building sync tests");
            copy_page(og[i]);
        }
        return og;
    }


    @Test
    public void testFraming()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

    }


    /* Test lost pages on pagein/packetout: no rollback */
    @Test
    public void testFraming1()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

        Page temp = new Page();
        Packet test = new Packet();

        // fprintf(stderr,"Testing loss of pages... ");

        oy.reset();
        os_de.reset();
        for (Page page : og) {
            writePageToSyncState(page, oy);
        }

        oy.pageOut(temp);
        os_de.pageIn(temp);
        oy.pageOut(temp);
        os_de.pageIn(temp);
        oy.pageOut(temp);
        /* skip */
        oy.pageOut(temp);
        os_de.pageIn(temp);

        /* do we get the expected results/packets? */

        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 0, 0, 0);
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 100, 1, -1);
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 4079, 2, 3000);
        assertEquals(-1,
                os_de.packetOut(test), "loss of page did not return error");
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 76, 5, -1);
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 34, 6, -1);
    }


    /* Test lost pages on pagein/packetout: rollback with continuation */
    @Test
    public void testFraming2()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

        Page temp = new Page();
        Packet test = new Packet();

        //fprintf(stderr,"Testing loss of pages (rollback required)... ");

        oy.reset();
        os_de.reset();
        for (int i = 0; i < 5; i++) {
            writePageToSyncState(og[i], oy);
        }

        oy.pageOut(temp);
        os_de.pageIn(temp);
        oy.pageOut(temp);
        os_de.pageIn(temp);
        oy.pageOut(temp);
        os_de.pageIn(temp);
        oy.pageOut(temp);
        /* skip */
        oy.pageOut(temp);
        os_de.pageIn(temp);

        /* do we get the expected results/packets? */
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 0, 0, 0);
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 100, 1, -1);
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 4079, 2, 3000);
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 2956, 3, 4000);
        assertEquals(-1,
                os_de.packetOut(test), "retrieving packet after lost page did not return error");
        assertEquals(1,
                os_de.packetOut(test), "retrieving packet after lost page");
        checkpacket(test, 300, 13, 14000);
    }


    /* the rest only test sync */
    @Test
    public void testFraming3()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

        Page og_de = new Page();
        /* Test fractional page inputs: incomplete capture */
        //fprintf(stderr,"Testing sync on partial inputs... ");
        oy.reset();
        byte[] abHeader = og[1].getHeader();
        oy.write(abHeader, 3);
        assertTrue(oy.pageOut(og_de) <= 0,
                "sync on incomplete capture");

        /* Test fractional page inputs: incomplete fixed header */
        writeToSyncState(abHeader, 3, 20, oy);
        assertTrue(oy.pageOut(og_de) <= 0,
                "sync on incomplete fixed header");

        /* Test fractional page inputs: incomplete header */
        writeToSyncState(abHeader, 23, 5, oy);
        assertTrue(oy.pageOut(og_de) <= 0,
                "sync on incomplete header");

        /* Test fractional page inputs: incomplete body */

        writeToSyncState(abHeader, 28, abHeader.length - 28, oy);
        assertTrue(oy.pageOut(og_de) <= 0,
                "sync on incomplete body");

        byte[] abBody = og[1].getBody();
        oy.write(abBody, 1000);
        assertTrue(oy.pageOut(og_de) <= 0,
                "sync on incomplete body");

        writeToSyncState(abBody, 1000, abBody.length - 1000, oy);
        assertTrue(oy.pageOut(og_de) > 0,
                "sync on complete body");
    }


    /* Test fractional page inputs: page + incomplete capture */
    @Test
    public void testFraming4()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

        Page og_de = new Page();
        //fprintf(stderr,"Testing sync on 1+partial inputs... ");
        oy.reset();

        writePageToSyncState(og[1], oy);

        byte[] abHeader = og[1].getHeader();
        oy.write(abHeader, 20);
        assertTrue(oy.pageOut(og_de) > 0,
                "");
        assertTrue(oy.pageOut(og_de) <= 0,
                "");

        writeToSyncState(abHeader, 20, abHeader.length - 20, oy);
        byte[] abBody = og[1].getBody();
        oy.write(abBody, abBody.length);
        assertTrue(oy.pageOut(og_de) > 0,
                "");
    }


    /* Test recapture: garbage + page */
    @Test
    public void testFraming5()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

        Page og_de = new Page();
        //fprintf(stderr,"Testing search for capture... ");
        oy.reset();

        /* 'garbage' */
        byte[] abBody = og[1].getBody();
        writeToSyncState(abBody, 0, abBody.length, oy);

        writePageToSyncState(og[1], oy);

        byte[] abHeader = og[2].getHeader();
        writeToSyncState(abHeader, 0, 20, oy);

        assertTrue(oy.pageOut(og_de) <= 0,
                "");
        assertTrue(oy.pageOut(og_de) > 0,
                "");
        assertTrue(oy.pageOut(og_de) <= 0,
                "");

        writeToSyncState(abHeader, 20, abHeader.length - 20, oy);
        abBody = og[2].getBody();
        writeToSyncState(abBody, 0, abBody.length, oy);
        assertTrue(oy.pageOut(og_de) > 0,
                "");
    }


    /* Test recapture: page + garbage + page */
    @Test
    public void testFraming6()
            throws Exception {
        StreamState os_de = new StreamState();
        SyncState oy = new SyncState();
        os_de.init(0x04030201);
        oy.init();

        Page[] og = createPages();

        Page og_de = new Page();
        //fprintf(stderr,"Testing recapture... ");
        oy.reset();

        writePageToSyncState(og[1], oy);

        byte[] abHeader = og[2].getHeader();
        writeToSyncState(abHeader, 0, abHeader.length, oy);

        assertTrue(oy.pageOut(og_de) > 0,
                "");

        byte[] abBody = og[2].getBody();
        writeToSyncState(abBody, 0, abBody.length - 5, oy);

        writePageToSyncState(og[3], oy);

        assertTrue(oy.pageOut(og_de) <= 0,
                "");
        assertTrue(oy.pageOut(og_de) > 0,
                "");
    }
}


/* FramingTestCase.java */
