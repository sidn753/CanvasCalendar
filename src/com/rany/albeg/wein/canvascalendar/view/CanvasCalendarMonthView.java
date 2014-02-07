package com.rany.albeg.wein.canvascalendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rany.albeg.wein.canvascalendar.R;
import com.rany.albeg.wein.canvascalendar.Utils;

public class CanvasCalendarMonthView extends View {

	private static final String		_TAG			= "CanvasCalendarMonthView";
	private static final int		_WEEK			= 7;
	private static final long		_VIBRATE_MILLIS	= 15;
	/*
	 * The Y point where the day names header is being drawn.
	 */
	private float					mDaysHeaderY;
	/*
	 * The Y start point of month's title
	 */
	private float					mMonthTitleY;
	/*
	 * Limits of the rectangle that bounds the section in which the day numbers
	 * are being drawn.
	 */
	private float					mDaysStartY;
	private float					mDaysStartX;
	private float					mDaysEndY;
	private float					mDaysEndX;
	/*
	 * The number of rows needed for displaying the whole month.
	 */
	private int						mRows;
	/*
	 * Day Padding Horizontal - the horizontal padding of a day in this view.
	 */
	private float					mDayPaddingH;
	/*
	 * Day Padding Vertical - the vertical padding of a day in this view.
	 */
	private float					mDayPaddingV;
	/*
	 * x and y factors when drawing days
	 */
	private float					mXFactor;
	private float					mYFactor;
	/*
	 * The first day of week of the first week in this month
	 */
	private int						mFirstDayOfWeek;
	/*
	 * The number of day which is currently circled
	 */
	private int						mDayCircled;
	/*
	 * This month number. 0 = January, 11 = December.
	 */
	private int						mMonth;
	/*
	 * This view's year number.
	 */
	private int						mYear;
	/*
	 * The number of days in this view's month.
	 */
	private int						mDaysInMonth;
	/*
	 * A paint object for drawing the days on the canvas.
	 */
	private Paint					mTextPaint;
	/*
	 * A paint object for drawing a circle around selected day.
	 */
	private Paint					mCirclePaint;
	/*
	 * A rectangle to represent the bounds a text
	 */
	private Rect					mTextRect;
	/*
	 * A listener for selecting a day from this view's month
	 */
	private OnDaySelectedListener	mOnDaySelectedListener;

	private Context					mContext;

	private Vibrator				mVibrator;
	private boolean					mVibrateOnTouch;

	public CanvasCalendarMonthView(Context context, int month, int year) {
		super(context);

		mContext = context;

		Resources res = getResources();

		mDayPaddingH = res.getDimension(R.dimen.day_padding_horizontal);
		mDayPaddingV = res.getDimension(R.dimen.day_padding_vertical);
		mMonthTitleY = res.getDimension(R.dimen.month_title_y);

		mDaysHeaderY = mMonthTitleY + mDayPaddingV;
		mDaysStartY = mDaysHeaderY + 2 * mDayPaddingV;
		mDaysStartX = mDayPaddingH;
		mXFactor = 2 * mDayPaddingH;
		mYFactor = 2 * mDayPaddingV;

		mDayCircled = -1;

		mMonth = month;
		mYear = year;

		mDaysInMonth = Utils.getDaysInMonth(mMonth, mYear);

		mFirstDayOfWeek = Utils.getDayOfWeek(mYear, mMonth, 1);

		mRows = calcNumberOfRows();

		mDaysEndX = mDaysStartX + _WEEK * mXFactor;
		mDaysEndY = mDaysStartY + mRows * mYFactor;

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFakeBoldText(true);

		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(res.getColor(R.color.dark_blue));
		mCirclePaint.setAlpha(80);

		mTextRect = new Rect();

		mVibrator = Utils.getVibrator(mContext);
	}

	public CanvasCalendarMonthView(Context context, AttributeSet attrs) {
		super(context, attrs);

		/*********************
		 * TO BE IMPLEMENTED *
		 *********************/
	}

	/**
	 * Calculate the number of rows needed to populate this view with day
	 * numbers.
	 * 
	 * @return: The number of rows.
	 */
	private int calcNumberOfRows() {

		int rows = (mDaysInMonth + mFirstDayOfWeek - 1) / _WEEK + 1;

		if ((mDaysInMonth + mFirstDayOfWeek - 1) % _WEEK == 0)
			--rows;

		return rows;
	}

	/**
	 * 
	 * @param x
	 *            : X axis.
	 * @param y
	 *            : Y axis.
	 * @return Number of day pressed. 0 or negative value for touch out of
	 *         month's bounds.
	 * 
	 */
	private int getDayNumberFromCoordinates(float x, float y) {

		/*
		 * The touch was out of month's bounds.
		 */
		if (y >= mDaysEndY - mDayPaddingV || x >= mDaysEndX - mDayPaddingH)
			return -1;
		/*
		 * Normalize coordinates to (i,j) cells.
		 */
		int i = (int) (y / (mYFactor + mDayPaddingV / 4)) - 1;
		int j = (int) (x / mXFactor) + 1;
		/*
		 * Calculate day number from (i,j).
		 */
		int day = i * _WEEK + j - (mFirstDayOfWeek - 1);

		/*
		 * Ignore touches after the last day.
		 */
		if (day > mDaysInMonth)
			day = -1;

		return day;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			/*
			 * We return true here so that ACTION_UP will be called when the
			 * finger is being released.
			 */
			return true;
		if (event.getAction() == MotionEvent.ACTION_UP) {

			int selectedDay = getDayNumberFromCoordinates(event.getX(), event.getY());
			/*
			 * The user selected an already selected day - remove the circle by
			 * giving a negative number which will be ignored by onDraw().
			 */
			if (mDayCircled == selectedDay) {
				mDayCircled = -1;
			} else {
				mDayCircled = selectedDay;
				if (mDayCircled > 0) {
					if (mOnDaySelectedListener != null) {

						mOnDaySelectedListener.onDaySelected(mDayCircled, mMonth, mYear);
						if (mVibrateOnTouch)
							mVibrator.vibrate(_VIBRATE_MILLIS);
					}
				}
			}

			invalidate();
		}

		return super.onTouchEvent(event);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension((int) (mXFactor * _WEEK + mDayPaddingH), (int) (mYFactor * mRows + mDayPaddingV + mDaysHeaderY));
	}

	/**
	 * Draw this month on the provided canvas.
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		Resources res = getResources();
		/*
		 * Draw month title
		 */
		drawMonthTitle(canvas, res.getDimension(R.dimen.month_name_text_size), res.getColor(R.color.dark_blue));
		/*
		 * Draw day headers.
		 */
		String[] days = new String[] { res.getString(R.string.sun), res.getString(R.string.mon), res.getString(R.string.tue),
				res.getString(R.string.wed), res.getString(R.string.thu), res.getString(R.string.fri), res.getString(R.string.sat) };

		drawDayHeaders(canvas, res.getDimension(R.dimen.days_header_text_size), res.getColor(R.color.dark_grey), days);
		/*
		 * Draw days.
		 */
		drawDayNumbers(canvas, res.getDimension(R.dimen.day_text_size));
	}

	private void drawDayNumbers(Canvas canvas, float dimension) {
		mTextPaint.setTextSize(dimension);

		float x, y;
		float dayOffset = (mFirstDayOfWeek - 1) * mXFactor;
		float cx;

		int day = 1;
		int i, j;

		String dayStr;

		for (y = mDaysStartY, i = 0; i < mRows; i++, y += mYFactor) {
			for (x = mDaysStartX, j = 0; j < _WEEK; j++, x += mXFactor) {
				if (day <= mDaysInMonth) {

					/*
					 * This is the place for the first day of the first week in
					 * this month. Apply offset.
					 */
					if (i == 0 && j == 0) {
						x += dayOffset;
						j = mFirstDayOfWeek - 1;
					}
					/*
					 * If day is a single digit - center it.
					 */
					cx = (float) (day < 10 ? (x + mDayPaddingH * 0.25) : x);
					/*
					 * String representation of a day.
					 */
					dayStr = Integer.toString(day);
					/*
					 * Draw the day on the canvas in (x,y) using the paint
					 * object
					 */
					canvas.drawText(dayStr, cx, y, mTextPaint);

					mTextPaint.getTextBounds(dayStr, 0, dayStr.length(), mTextRect);

					if (mDayCircled == day) {
						/*
						 * Draw a circle around selected day.
						 */
						canvas.drawCircle(cx + mTextRect.centerX(), y + mTextRect.centerY(), dimension, mCirclePaint);
					}

					++day;
				}
			}
		}

	}

	private void drawDayHeaders(Canvas canvas, float dimension, int color, String[] days) {
		mTextPaint.setTextSize(dimension);
		mTextPaint.setColor(color);

		/*
		 * The padding between the day names will be the same as the padding
		 * between the days. ( SUN <--mXFactor--> MON ).
		 */
		float x;
		int j;
		for (x = mDaysStartX, j = 0; j < _WEEK; j++, x += mXFactor) {
			canvas.drawText(days[j], x, mDaysHeaderY, mTextPaint);
		}

	}

	private void drawMonthTitle(Canvas canvas, float dimension, int color) {
		mTextPaint.setTextSize(dimension);
		mTextPaint.setColor(color);
		String monthTitle = Utils.getMonthAndYearTitle(mContext, mMonth, mYear);
		canvas.drawText(monthTitle, mDaysEndX / 2 - mTextPaint.measureText(monthTitle) / 2, mMonthTitleY, mTextPaint);
	}

	/**
	 * Sets this view to vibrate when a day is being selected.
	 * 
	 * @param vibrate
	 *            : true/false.
	 */
	public void setVibrateOnDaySelected(boolean vibrate) {
		mVibrateOnTouch = vibrate;
	}

	/**
	 * Sets a listener for the event of selecting a single day.
	 * 
	 * @param l
	 *            : An object that implements OnDaySelectedListener.
	 */
	public void setOnDaySelectedListener(OnDaySelectedListener l) {
		mOnDaySelectedListener = l;
	}

	public interface OnDaySelectedListener {
		public void onDaySelected(int day, int month, int year);
	}
}
