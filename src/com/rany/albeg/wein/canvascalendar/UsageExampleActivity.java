package com.rany.albeg.wein.canvascalendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rany.albeg.wein.canvascalendar.view.CanvasCalendarMonthView;
import com.rany.albeg.wein.canvascalendar.view.CanvasCalendarMonthView.OnDaySelectedListener;

public class UsageExampleActivity extends Activity implements OnDaySelectedListener {

	private LinearLayout	mRootLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRootLayout = (LinearLayout) findViewById(R.id.root_layout);

		int m, y;

		for (y = 2014; y < 2040; y++) {

			for (m = 0; m < 12; m++) {

				CanvasCalendarMonthView canvasMonthView = new CanvasCalendarMonthView(this, m, y);

				canvasMonthView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				canvasMonthView.setBackgroundResource(R.drawable.month_bg);
				canvasMonthView.setVibrateOnDaySelected(true);
				canvasMonthView.setOnDaySelectedListener(this);

				mRootLayout.addView(canvasMonthView);
			}
		}
	}

	@Override
	public void onDaySelected(int day, int month, int year) {
		Toast.makeText(this, day + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
	}

}
