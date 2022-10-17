package org.tritonus.share.sampled;


/**
 * Annexe: how the arrays were created.
 */
class TConversionToolTest {

    /*
     * Converts a uLaw byte to a linear signed 16bit sample.
     * Ported to Java by fb.
     * <BR>Originally by:<BR>
     *
     * Craig Reese: IDA/Supercomputing Research Center <BR>
     * 29 September 1989 <BR>
     *
     * References: <BR>
     * <OL>
     * <LI>CCITT Recommendation G.711  (very difficult to follow)</LI>
     * <LI>MIL-STD-188-113,"Interoperability and Performance Standards
     *     for Analog-to_Digital Conversion Techniques,"
     *     17 February 1987</LI>
     * </OL>
     */
    private static final int[] exp_lut2 = {
            0, 132, 396, 924, 1980, 4092, 8316, 16764
    };

    public static short _ulaw2linear(int ulawbyte) {
        int sign, exponent, mantissa, sample;

        ulawbyte = ~ulawbyte;
        sign = (ulawbyte & 0x80);
        exponent = (ulawbyte >> 4) & 0x07;
        mantissa = ulawbyte & 0x0F;
        sample = exp_lut2[exponent] + (mantissa << (exponent + 3));
        if (sign != 0) sample = -sample;
        return ((short) sample);
    }

    /** u- to A-law conversions: copied from CCITT G.711 specifications */
    private static final byte[] _u2a = {
            1, 1, 2, 2, 3, 3, 4, 4,
            5, 5, 6, 6, 7, 7, 8, 8,
            9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23, 24,
            25, 27, 29, 31, 33, 34, 35, 36,
            37, 38, 39, 40, 41, 42, 43, 44,
            46, 48, 49, 50, 51, 52, 53, 54,
            55, 56, 57, 58, 59, 60, 61, 62,
            64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 75, 76, 77, 78, 79,
            81, 82, 83, 84, 85, 86, 87, 88,
            89, 90, 91, 92, 93, 94, 95, 96,
            97, 98, 99, 100, 101, 102, 103, 104,
            105, 106, 107, 108, 109, 110, 111, 112,
            113, 114, 115, 116, 117, 118, 119, 120,
            121, 122, 123, 124, 125, 126, 127, (byte) 128};

    /* u-law to A-law conversion */
    /*
     * This source code is a product of Sun Microsystems, Inc. and is provided
     * for unrestricted use.  Users may copy or modify this source code without
     * charge.
     */
    public static byte _ulaw2alaw(byte sample) {
        sample = (byte) (sample & 0xff);
        return (byte) (((sample & 0x80) != 0) ? (0xD5 ^ (_u2a[(0x7F ^ sample) & 0x7F] - 1)) :
                (0x55 ^ (_u2a[(0x7F ^ sample) & 0x7F] - 1)));
    }

    /** A- to u-law conversions */
    private static final byte[] _a2u = {
            1, 3, 5, 7, 9, 11, 13, 15,
            16, 17, 18, 19, 20, 21, 22, 23,
            24, 25, 26, 27, 28, 29, 30, 31,
            32, 32, 33, 33, 34, 34, 35, 35,
            36, 37, 38, 39, 40, 41, 42, 43,
            44, 45, 46, 47, 48, 48, 49, 49,
            50, 51, 52, 53, 54, 55, 56, 57,
            58, 59, 60, 61, 62, 63, 64, 64,
            65, 66, 67, 68, 69, 70, 71, 72,
            73, 74, 75, 76, 77, 78, 79, 79,
            80, 81, 82, 83, 84, 85, 86, 87,
            88, 89, 90, 91, 92, 93, 94, 95,
            96, 97, 98, 99, 100, 101, 102, 103,
            104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119,
            120, 121, 122, 123, 124, 125, 126, 127};

    /*
     * This source code is a product of Sun Microsystems, Inc. and is provided
     * for unrestricted use.  Users may copy or modify this source code without
     * charge.
     */
    public static byte _alaw2ulaw(byte sample) {
        sample = (byte) (sample & 0xff);
        return (byte) (((sample & 0x80) != 0) ? (0xFF ^ _a2u[(sample ^ 0xD5) & 0x7F]) :
                (0x7F ^ _a2u[(sample ^ 0x55) & 0x7F]));
    }

    public static void print_a2u() {
        System.out.println("\tprivate static byte[] a2u = {");
        for (int i = -128; i < 128; i++) {
            if (((i + 128) % 16) == 0) {
                System.out.print("\t\t");
            }
            byte b = (byte) i;
            System.out.print(_alaw2ulaw(b) + ", ");
            if (((i + 128) % 16) == 15) {
                System.out.println();
            }
        }
        System.out.println("\t};");
    }

    public static void print_u2a() {
        System.out.println("\tprivate static byte[] u2a = {");
        for (int i = -128; i < 128; i++) {
            if (((i + 128) % 16) == 0) {
                System.out.print("\t\t");
            }
            byte b = (byte) i;
            System.out.print(_ulaw2alaw(b) + ", ");
            if (((i + 128) % 16) == 15) {
                System.out.println();
            }
        }
        System.out.println("\t};");
    }
}