package com.example.tregoapp.mechanic.utils;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.text.format.DateUtils;

import java.util.Date;
import java.util.Locale;

public class Utility {

    public Utility() {}
    public String formatDate(String isoDate) {

        try {

            SimpleDateFormat isoFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = isoFormat.parse(isoDate);
            long time = date.getTime();
            long now = System.currentTimeMillis();

            long diff = now - time;

            // less than 1 day → relative
            if (diff < DateUtils.DAY_IN_MILLIS) {

                return DateUtils.getRelativeTimeSpanString(
                        time,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                ).toString();
            }

            // yesterday
            if (diff < 2 * DateUtils.DAY_IN_MILLIS) {

                SimpleDateFormat format =
                        new SimpleDateFormat("hh:mm a", Locale.getDefault());

                return "Yesterday, " + format.format(date);
            }

            // older dates
            SimpleDateFormat format =
                    new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

            return format.format(date);

        } catch (Exception e) {
            return "";
        }
    }
}
