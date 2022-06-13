package helpers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateAndTime {
    private static final Calendar calendar;
    private static final SimpleDateFormat formatter;
    private static final int GMT_MAX_VALUE = 12;
    private static final int GMT_MIN_VALUE = -GMT_MAX_VALUE;

    static{
        calendar = Calendar.getInstance();
        formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        setTimeZone(2);
    }

    public static void setTimeZone(int GMTAdditionValue){
        if(inRange(GMTAdditionValue, GMT_MIN_VALUE, GMT_MAX_VALUE)){
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+" + GMTAdditionValue));
        }
    }

    public static int getCurrentYear(){
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth(){
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay(){
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentHour(){
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMinute(){
        return calendar.get(Calendar.MINUTE);
    }

    public static int getCurrentSecond(){
        return calendar.get(Calendar.SECOND);
    }

    private static boolean inRange(int val, int rangeMin, int rangeMax) {
        return (val >= rangeMin && val <= rangeMax);
    }

    public static String getCurrentDateAndTime(){
        return timeInMillisToDateAndTimeFormat(getCurrentDateAndTimeInMillis());
    }

    public static long getCurrentDateAndTimeInMillis(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return calendar.getTimeInMillis();
    }

    public static String timeInMillisToDateAndTimeFormat(long timeInMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return formatter.format(calendar.getTime());
    }

    public static long DateAndTimeFormatToTimeInMillis(String dateAndTime){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(dateAndTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

}
