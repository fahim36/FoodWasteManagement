package com.example.foodwastemanagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    public static String epoch2DateString(long j, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(j);
    }

    public static long dateStringToEpoch(String str, String format) {
        try {
            return new SimpleDateFormat(format, Locale.getDefault()).parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
