package com.github.arkadiusz97.online.voting.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static final Long DAY_AS_MILLISECONDS = 1000L * 60L * 60L * 24L;
    public static final Date NOW = new Date();
    public static final Long NOW_AS_MILLISECONDS = new Date().getTime();

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    public static Date getDateAheadOfDays(int days) {
        return new Date(NOW_AS_MILLISECONDS + DAY_AS_MILLISECONDS * days);
    }

}
