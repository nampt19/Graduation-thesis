package com.nampt.socialnetworkproject.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalculateTimeUtil {

    public static CalculateTimeUtil instance;

    private CalculateTimeUtil() {
    }

    public static CalculateTimeUtil getInstance() {
        if (instance == null) {
            instance = new CalculateTimeUtil();
        }
        return instance;
    }

    @SuppressLint("DefaultLocale")
    public String calculatorTimeFromChatViews(Date date,Date dateNow) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        Calendar calendarCurrent = new GregorianCalendar();
        calendarCurrent.setTime(dateNow);

        Calendar calCurrentStart = Calendar.getInstance();
        calCurrentStart.setTime(calendarCurrent.getTime());
        calCurrentStart.set(Calendar.HOUR_OF_DAY, 0);
        calCurrentStart.set(Calendar.MINUTE, 0);
        calCurrentStart.set(Calendar.SECOND, 0);
        calCurrentStart.set(Calendar.MILLISECOND, 0);


        Calendar calAfter3Days = new GregorianCalendar();
        calAfter3Days.setTime(calCurrentStart.getTime());
        calAfter3Days.add(Calendar.DATE, -3);

        if (calendar.compareTo(calendarCurrent) > 0) return "invalid";

        else if (calendar.compareTo(calCurrentStart) >= 0 && calendar.compareTo(calendarCurrent) <= 0)
            return String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        else if (calendar.compareTo(calAfter3Days) >= 0 && calendar.compareTo(calCurrentStart) < 0)
            return String.format("%s lúc %d:%02d",
                    getNameDayByCalendar(calendar),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));

        else {
            if (calendar.get(Calendar.YEAR) == calendarCurrent.get(Calendar.YEAR))
                return String.format("%d/%d lúc %d:%02d",
                        calendar.get(Calendar.DATE),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
            else return String.format("%d/%d/%04d lúc %d:%02d",
                    calendar.get(Calendar.DATE),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
        }

    }

    @SuppressLint("DefaultLocale")
    public String calculatorTimeCommon(Date dateParse,Date dateNow) {

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateParse);

        Calendar timeCurrent = new GregorianCalendar();
        timeCurrent.setTime(dateNow);

        Calendar timeCurrentStart = new GregorianCalendar();
        timeCurrentStart.setTime(timeCurrent.getTime());
        timeCurrentStart.set(Calendar.HOUR_OF_DAY, 0);
        timeCurrentStart.set(Calendar.MINUTE, 0);
        timeCurrentStart.set(Calendar.SECOND, 0);
        timeCurrentStart.set(Calendar.MILLISECOND, 0);

        Calendar time1minuteAgo = new GregorianCalendar();
        time1minuteAgo.setTime(new Date(timeCurrent.getTimeInMillis() - 1000L * 60));

        Calendar time1HourAgo = new GregorianCalendar();
        time1HourAgo.setTime(new Date(timeCurrent.getTimeInMillis() - 1000L * 60 * 60));

        Calendar time1DayAgo = new GregorianCalendar();
        time1DayAgo.setTime(new Date(timeCurrent.getTimeInMillis() - 1000L * 60 * 60 * 24));

        Calendar time1WeekAgo = new GregorianCalendar();
        time1WeekAgo.setTime(new Date(timeCurrent.getTimeInMillis() - 1000L * 60 * 60 * 24 * 7));

        if (calendar.compareTo(timeCurrent) > 0) return "invalid";
        else if (calendar.compareTo(time1minuteAgo) > 0 && calendar.compareTo(timeCurrent) <= 0)
            return "vừa xong";
        else if (calendar.compareTo(time1HourAgo) > 0 && calendar.compareTo(time1minuteAgo) <= 0)
            return String.format("%d phút trước",
                    (timeCurrent.getTimeInMillis() - calendar.getTimeInMillis()) / (1000L * 60));
        else if (calendar.compareTo(time1DayAgo) > 0 && calendar.compareTo(time1HourAgo) <= 0)
            return String.format("%d giờ trước",
                    (timeCurrent.getTimeInMillis() - calendar.getTimeInMillis()) / (1000L * 60 * 60));
        else if (calendar.compareTo(time1WeekAgo) > 0 && calendar.compareTo(time1DayAgo) <= 0)
            return String.format("%d ngày trước",
                    (timeCurrent.getTimeInMillis() - calendar.getTimeInMillis()) / (1000L * 60 * 60 * 24));
        else {
            if (calendar.get(Calendar.YEAR) == timeCurrent.get(Calendar.YEAR))
                return String.format("%d/%d lúc %d:%02d",
                        calendar.get(Calendar.DATE),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
            else return String.format("%d/%d/%04d lúc %d:%02d",
                    calendar.get(Calendar.DATE),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
        }
    }

    public String getNameDayByCalendar(Calendar calendar) {
        String result = null;
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                result = "CN";
                break;
            case 2:
                result = "T2";
                break;
            case 3:
                result = "T3";
                break;
            case 4:
                result = "T4";
                break;
            case 5:
                result = "T5";
                break;
            case 6:
                result = "T6";
                break;
            case 7:
                result = "T7";
                break;
        }
        return result;
    }
}
