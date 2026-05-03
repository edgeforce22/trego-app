package com.example.tregoapp.customer.utils;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.text.format.DateUtils;
import android.view.View;

import com.example.tregoapp.R;

import java.util.Date;
import java.util.Locale;

public class Utility {

    private static Utility utility;
    public Utility() {}

    public static Utility getInstance() {
        if (utility == null) {
            utility = new Utility();
        }
        return utility;
    }
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

    public String formatDate2(String isoDate) {

        try {

            // Input ISO format
            SimpleDateFormat isoFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = isoFormat.parse(isoDate);

            // Output format: 12 Mar 2025 10:10 PM
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

            return outputFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public int vehicleIconConvertor(String vehicleType) {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            return R.drawable.bike;
        }

        String type = vehicleType.trim().toLowerCase();

        switch (type) {
            case "bicycler":
                return R.drawable.bicycle;
            case "bike":
                return R.drawable.bike;
            case "car":
                return R.drawable.car;
            case "scooter":
                return R.drawable.scooter;
            case "truck":
                return R.drawable.truck;
            default:
                return R.drawable.bike;
        }
    }
}
