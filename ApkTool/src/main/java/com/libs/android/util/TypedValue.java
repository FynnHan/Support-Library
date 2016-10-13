package com.libs.android.util;

public class TypedValue {

    public static final int TYPE_NULL = 0x00;
    public static final int TYPE_REFERENCE = 0x01;
    public static final int TYPE_ATTRIBUTE = 0x02;
    public static final int TYPE_STRING = 0x03;
    public static final int TYPE_FLOAT = 0x04;
    public static final int TYPE_DIMENSION = 0x05;
    public static final int TYPE_FRACTION = 0x06;
    public static final int TYPE_DYNAMIC_REFERENCE = 0x07;
    public static final int TYPE_DYNAMIC_ATTRIBUTE = 0x08;
    public static final int TYPE_FIRST_INT = 0x10;
    public static final int TYPE_INT_DEC = 0x10;
    public static final int TYPE_INT_HEX = 0x11;
    public static final int TYPE_INT_BOOLEAN = 0x12;
    public static final int TYPE_FIRST_COLOR_INT = 0x1c;
    public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    public static final int TYPE_INT_COLOR_RGB8 = 0x1d;
    public static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
    public static final int TYPE_INT_COLOR_RGB4 = 0x1f;
    public static final int TYPE_LAST_COLOR_INT = 0x1f;
    public static final int TYPE_LAST_INT = 0x1f;
    public static final int COMPLEX_UNIT_SHIFT = 0;
    public static final int COMPLEX_UNIT_MASK = 0xf;
    public static final int COMPLEX_UNIT_PX = 0;
    public static final int COMPLEX_UNIT_DIP = 1;
    public static final int COMPLEX_UNIT_SP = 2;
    public static final int COMPLEX_UNIT_PT = 3;
    public static final int COMPLEX_UNIT_IN = 4;
    public static final int COMPLEX_UNIT_MM = 5;
    public static final int COMPLEX_UNIT_FRACTION = 0;
    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;
    public static final int COMPLEX_RADIX_SHIFT = 4;
    public static final int COMPLEX_RADIX_MASK = 0x3;
    public static final int COMPLEX_RADIX_23p0 = 0;
    public static final int COMPLEX_RADIX_16p7 = 1;
    public static final int COMPLEX_RADIX_8p15 = 2;
    public static final int COMPLEX_RADIX_0p23 = 3;
    public static final int COMPLEX_MANTISSA_SHIFT = 8;
    public static final int COMPLEX_MANTISSA_MASK = 0xffffff;
    public static final int DATA_NULL_UNDEFINED = 0;
    public static final int DATA_NULL_EMPTY = 1;
    public static final int DENSITY_DEFAULT = 0;
    public static final int DENSITY_NONE = 0xffff;
    public int type;
    private static final float MANTISSA_MULT = 1.0f / (1 << TypedValue.COMPLEX_MANTISSA_SHIFT);
    private static final float[] RADIX_MULTS = new float[]{1.0f * MANTISSA_MULT, 1.0f / (1 << 7) * MANTISSA_MULT, 1.0f / (1 << 15) * MANTISSA_MULT, 1.0f / (1 << 23) * MANTISSA_MULT};

    public static float complexToFloat(int complex) {
        return (complex & (TypedValue.COMPLEX_MANTISSA_MASK << TypedValue.COMPLEX_MANTISSA_SHIFT)) * RADIX_MULTS[(complex >> TypedValue.COMPLEX_RADIX_SHIFT) & TypedValue.COMPLEX_RADIX_MASK];
    }

    private static final String[] DIMENSION_UNIT_STRS = new String[]{"px", "dip", "sp", "pt", "in", "mm"};
    private static final String[] FRACTION_UNIT_STRS = new String[]{"%", "%p"};

    public static final String coerceToString(int type, int data) {
        switch (type) {
            case TYPE_NULL:
                return null;
            case TYPE_REFERENCE:
                return "@" + data;
            case TYPE_ATTRIBUTE:
                return "?" + data;
            case TYPE_FLOAT:
                return Float.toString(Float.intBitsToFloat(data));
            case TYPE_DIMENSION:
                return Float.toString(complexToFloat(data))
                        + DIMENSION_UNIT_STRS[(data >> COMPLEX_UNIT_SHIFT)
                        & COMPLEX_UNIT_MASK];
            case TYPE_FRACTION:
                return Float.toString(complexToFloat(data) * 100)
                        + FRACTION_UNIT_STRS[(data >> COMPLEX_UNIT_SHIFT)
                        & COMPLEX_UNIT_MASK];
            case TYPE_INT_HEX:
                return String.format("0x%08X", data);
            case TYPE_INT_BOOLEAN:
                return data != 0 ? "true" : "false";
        }

        if (type >= TYPE_FIRST_COLOR_INT && type <= TYPE_LAST_COLOR_INT) {
            String res = String.format("%08x", data);
            char[] vals = res.toCharArray();
            switch (type) {
                default:
                case TYPE_INT_COLOR_ARGB8:// #AaRrGgBb
                    break;
                case TYPE_INT_COLOR_RGB8:// #FFRrGgBb->#RrGgBb
                    res = res.substring(2);
                    break;
                case TYPE_INT_COLOR_ARGB4:// #AARRGGBB->#ARGB
                    res = new StringBuffer().append(vals[0]).append(vals[2]).append(vals[4]).append(vals[6]).toString();
                    break;
                case TYPE_INT_COLOR_RGB4:// #FFRRGGBB->#RGB
                    res = new StringBuffer().append(vals[2]).append(vals[4]).append(vals[6]).toString();
                    break;
            }
            return "#" + res;
        } else if (type >= TYPE_FIRST_INT && type <= TYPE_LAST_INT) {
            String res;
            switch (type) {
                default:
                case TYPE_INT_DEC:
                    res = Integer.toString(data);
                    break;
            }
            return res;
        }
        return null;
    }
}
