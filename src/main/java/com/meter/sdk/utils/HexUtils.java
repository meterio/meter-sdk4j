package com.meter.sdk.utils;

public class HexUtils {

    public static Long getAmountToDecimal(String hex) {
        // String hexRawTxn
        // ="0xa9059cbb00000000000000000000000067e37c1896fe00284d7dcc7fdfc61810c10c004f000000000000000000000000000000000000000000000000016345785d8a0000";

        // String to = "0x" + hexRawTxn.substring(10,74).replaceFirst("^0+(?!$)", "");
        String value = hex.substring(74).replaceFirst("^0+(?!$)", "");

        Long decimal = Long.parseLong(value, 16);

        return decimal;
    }

    public static String getAmount(String hex) {

        String value = "0x" + hex.substring(74).replaceFirst("^0+(?!$)", "");

        return value;
    }

    public static String getToAddress(String hex) {
        // String hexRawTxn
        // ="0xa9059cbb00000000000000000000000067e37c1896fe00284d7dcc7fdfc61810c10c004f000000000000000000000000000000000000000000000000016345785d8a0000";

        String to = "0x" + hex.substring(10, 74).replaceFirst("^0+(?!$)", "");
        // String value = hex.substring(74).replaceFirst("^0+(?!$)", "");
        return to;

    }

}
