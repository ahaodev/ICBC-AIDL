package com.icbc.selfserviceticketing.deviceservice;

public class DataFormatUtil {


    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static float bytesToFloat(byte high, byte low) {
        return (low & 0xFF) | ((high & 0xFF) << 8);
    }

    public static int getByteToInt(byte hb, byte lb) {
        String toHex = bytesToHex(new byte[]{hb, lb});
        if (toHex.startsWith("f")) {
            return ((hb) << 8) | (lb);
        } else {
            return ((hb & 0xFF) << 8) | (lb & 0xFF);
        }
    }
}
