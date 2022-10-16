package ru.rsmu.olympreg.utils;

import java.util.Calendar;

/**
 * @author leonid.
 */
public class YearHelper {
    /**
     * After september it works with first stage of olympiad. So actual year is current year.
     * Before september it works with second stage and actual year is previous one.
     * @return Actual year for current olympiad
     */
    public static int getActualYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR);
        return ( calendar.get( Calendar.MONTH ) > 8 ? year : year - 1 );
    }
}
