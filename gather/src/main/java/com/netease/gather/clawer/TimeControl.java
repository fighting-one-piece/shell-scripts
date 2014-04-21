package com.netease.gather.clawer;

import java.util.Calendar;

/**
 * User: AzraelX
 * Date: 13-7-25
 * Time: 下午5:55
 */
public class TimeControl {

    private static final int Interval = 120;

    public static Calendar clawStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        int min = cal.get(Calendar.MINUTE);
        int initmin = min > 30 ? min > 45?45:30 : min > 15?15:0;
        cal.set(Calendar.MINUTE, initmin);
        cal.add(Calendar.MINUTE, -Interval);
        return cal;
    }

    public static Calendar clawEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        int min = cal.get(Calendar.MINUTE);
        int initmin = min > 30 ? min > 45?45:30 : min > 15?15:0;
        cal.set(Calendar.MINUTE, initmin);
        return cal;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(TimeControl.clawStartTime().getTime());
        System.out.println(TimeControl.clawEndTime().getTime());
    }

}
