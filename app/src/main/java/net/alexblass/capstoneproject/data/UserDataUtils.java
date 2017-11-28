package net.alexblass.capstoneproject.data;

import net.alexblass.capstoneproject.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A class of helper methods to manipulate or evaluate user data.
 */

public class UserDataUtils {

    public static int getGenderStringId(long id){
        switch((int)id){
            case 1:
                return R.string.gender_cismale;
            case 2:
                return R.string.gender_cisfemale;
            case 3:
                return R.string.gender_ftm;
            case 4:
                return R.string.gender_mtf;
            case 5:
                return R.string.gender_neutral;
            case 6:
                return R.string.gender_other;
            default:
                return R.string.default_selection;
        }
    }

    public static int getGenderAbbreviationStringId(long id){
        switch((int)id){
            case 1:
                return R.string.gender_cismale_abbreviation;
            case 2:
                return R.string.gender_cisfemale_abbreviation;
            case 3:
                return R.string.gender_ftm_abbreviation;
            case 4:
                return R.string.gender_mtf_abbreviation;
            case 5:
                return R.string.gender_neutral_abbreviation;
            case 6:
                return R.string.gender_other_abbreviation;
            default:
                return R.string.default_selection_abbreviation;
        }
    }

    public static int calculateAge(Calendar birthdayCalendar){
        final double daysInYear = 365.25; // To account for leap years
        long day = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
        double currentAge;

        Calendar currentDate = Calendar.getInstance();
        birthdayCalendar = new GregorianCalendar(1999, 11 - 1, 27);

        currentDate.set(Calendar.MILLISECOND, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);

        birthdayCalendar.set(Calendar.MILLISECOND, 0);
        birthdayCalendar.set(Calendar.SECOND, 0);
        birthdayCalendar.set(Calendar.MINUTE, 0);
        birthdayCalendar.set(Calendar.HOUR_OF_DAY, 0);

        currentAge = (double) ((currentDate.getTimeInMillis() - birthdayCalendar.getTimeInMillis()) / day) / daysInYear;

        return (int) Math.floor(currentAge);
    }
}
