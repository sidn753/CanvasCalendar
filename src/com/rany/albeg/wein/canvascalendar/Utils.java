package com.rany.albeg.wein.canvascalendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;

public class Utils {

	public static int getDaysInMonth(int month, int year) {
		switch (month) {
			default:
				throw new IllegalArgumentException("Invalid Month");
			case 0:
			case 2:
			case 4:
			case 6:
			case 7:
			case 9:
			case 11:
				return 31;
			case 3:
			case 5:
			case 8:
			case 10:
				return 30;
			case 1:
				if (year % 4 == 0)
					return 29;
				return 28;
		}
	}

	// month is 0 based (i.e. Jan = 0)
	public static String getMonthName(Context context, int month) {
		Resources res = context.getResources();

		switch (month) {
			default:
				return res.getString(R.string.January);
			case 0:
				return res.getString(R.string.January);
			case 1:
				return res.getString(R.string.February);
			case 2:
				return res.getString(R.string.March);
			case 3:
				return res.getString(R.string.April);
			case 4:
				return res.getString(R.string.May);
			case 5:
				return res.getString(R.string.June);
			case 6:
				return res.getString(R.string.July);
			case 7:
				return res.getString(R.string.August);
			case 8:
				return res.getString(R.string.September);
			case 9:
				return res.getString(R.string.October);
			case 10:
				return res.getString(R.string.November);
			case 11:
				return res.getString(R.string.December);
		}
	}

	public static int getDayOfWeek(int year, int month, int day) {

		Calendar calender = new GregorianCalendar();

		calender.set(year, month, day);

		return calender.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfMonthNow() {

		return new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
	}

	public static String getMonthAndYearTitle(Context context, int month, int year) {
		return getMonthName(context, month) + " " + year;
	}

	public static boolean isCurrentMonth(int month, int year) {
		Calendar c = new GregorianCalendar();
		return c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year;
	}

	public static Vibrator getVibrator(Context context) {
		return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}
}
