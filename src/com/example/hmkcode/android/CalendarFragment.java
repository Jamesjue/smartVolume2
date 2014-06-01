package com.example.hmkcode.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarFragment extends Fragment implements OnClickListener {
	public static final String ARG_PLANET_NUMBER = "planet_number";

	public CalendarFragment() {
		// Empty constructor required for fragment subclasses
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// View rootView = inflater.inflate(R.layout.fragment, container, false);
	// int i = getArguments().getInt(ARG_PLANET_NUMBER);
	// String planet = MainActivity.getName(i);
	//
	// getActivity().setTitle(planet);
	// return rootView;
	// }
	//
	private static final String tag = "SimpleCalendarViewActivity";

	private ImageView calendarToJournalButton;
	private Button selectedDayMonthYearButton;
	private Button day_event_1, day_event_2, day_event_3, day_event_4,
			day_event_5;
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
	private static Context mcontext;
	public static View rootView;
    private List<Button> listOfButtons;	
	
	CalendarFragment curFrag;
	
	// steve
	int EVENT_NUM = 0;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.curFrag=this;
		
		rootView = inflater.inflate(R.layout.simple_calendar_view, container,
				false);
		// super.onCreate(savedInstanceState);
		// setContentView(R.layout.simple_calendar_view);

		int i = getArguments().getInt(ARG_PLANET_NUMBER);
		String planet = MainActivity.getName(i);

		getActivity().setTitle(planet);

		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);
		Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
				+ year);

		selectedDayMonthYearButton = (Button) rootView
				.findViewById(R.id.selectedDayMonthYear);
		selectedDayMonthYearButton.setText("Selected: ");
		// steve
		// day_event_1 = (Button) rootView.findViewById(R.id.day_event_1);
		// day_event_2 = (Button) rootView.findViewById(R.id.day_event_2);
		// day_event_3 = (Button) rootView.findViewById(R.id.day_event_3);
		// day_event_4 = (Button) rootView.findViewById(R.id.day_event_4);
		// day_event_5 = (Button) rootView.findViewById(R.id.day_event_5);

		prevMonth = (ImageView) rootView.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) rootView.findViewById(R.id.currentMonth);
		currentMonth.setText(dateFormatter.format(dateTemplate,
				_calendar.getTime()));

		nextMonth = (ImageView) rootView.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) rootView.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getActivity().getApplicationContext(),
				R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
		return rootView;
	}

	
	public void changeButton(List<myEvent> list, String calName){
		this.listOfButtons.clear();
		Log.d("calendar", "event list size: " + list.size());
		LinearLayout linearLayout = (LinearLayout) this.rootView
				.findViewById(R.id.day_event_list);
		if (linearLayout.getChildCount() > 0)
			linearLayout.removeAllViews();

		//generate calendar button
		Button calButton = new Button(getActivity());
		listOfButtons.add(calButton);
		Log.d("calendar", "add one button");
		// For buttons visibility, you must set the layout params in
		// order to give some width and height:
		LayoutParams calparams = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		Log.d("calendar", calName);
		calButton.setText(calName);
		calButton.setTag(calName);			
		calButton.setTextColor(Color.parseColor("white"));
		calButton.setLayoutParams(calparams);
		calButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent volCtrl = new Intent(getActivity(), VolumeControlActivity.class);
				volCtrl.putExtra("event", (String)v.getTag());
				startActivity(volCtrl);
			}
		});
		linearLayout.addView(calButton);

		
		for (int i = 0; i < list.size(); i++) {
			Button button = new Button(getActivity());
			listOfButtons.add(button);
			Log.d("calendar", "add one button");
			// For buttons visibility, you must set the layout params in
			// order to give some width and height:
			LayoutParams params = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			//Log.d("calendar", list.get(i).getTitle());
			button.setText(list.get(i).getTitle());
			button.setTag(list.get(i).toString());			
			button.setTextColor(Color.parseColor("white"));
			button.setLayoutParams(params);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent volCtrl = new Intent(getActivity(), VolumeControlActivity.class);
					volCtrl.putExtra("event", (String)v.getTag());
					startActivity(volCtrl);
				}
			});
			linearLayout.addView(button);
		}
	}
	
	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getActivity().getApplicationContext(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(dateFormatter.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}
			Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}

	}

	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View ...");
		super.onDestroy();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private final int month, year;
		private int daysInMonth, prevMonthDays;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-MMM-yyyy");
//		private List<Button> listOfButtons;

		// private static final Context contextForEvent = new Context();//steve

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;
			listOfButtons = new ArrayList<Button>();
			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
//			Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

//			Log.d(tag, "Current Month: " + " " + currentMonthName + " having "	+ daysInMonth + " days.");

			// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			Log.d(tag, "Week Day:" + currentWeekDay + " is "
					+ getWeekDayAsString(currentWeekDay));
			Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
/*				
				Log.d(tag,
						"PREV MONTH:= "
								+ prevMonth
								+ " => "
								+ getMonthAsString(prevMonth)
								+ " "
								+ String.valueOf((daysInPrevMonth
										- trailingSpaces + DAY_OFFSET)
										+ i));
*/										
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				/*
				Log.d(currentMonthName, String.valueOf(i) + " "
						+ getMonthAsString(currentMonth) + " " + yy);
						*/
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap findNumberOfEventsPerMonth(int year, int month) {
			HashMap map = new HashMap<String, Integer>();
			// DateFormat dateFormatter2 = new DateFormat();
			//
			// String day = dateFormatter2.format("dd", dateCreated).toString();
			//
			// if (map.containsKey(day))
			// {
			// Integer val = (Integer) map.get(day) + 1;
			// map.put(day, val);
			// }
			// else
			// {
			// map.put(day, 1);
			// }
			return map;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_day_gridcell, parent,
						false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

//			Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					num_events_per_day = (TextView) row
							.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());
				}
			}

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
//			Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"		+ theyear);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(Color.WHITE);
			}
			if (day_color[1].equals("BLUE")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.static_text_color));
			}
			return row;
		}

		private int cur_event = 0;
		private String strEvent = new String("event");

		@Override
		public void onClick(View view) {

			String date_month_year = (String) view.getTag();
			selectedDayMonthYearButton.setText("Selected: " + date_month_year);
			int day_event_num = EVENT_NUM; // **
			Calendar c = Calendar.getInstance();

			// format string
			String convert_string = date_month_year.replaceAll("January", "01");
			convert_string = convert_string.replaceAll("February", "02");
			convert_string = convert_string.replaceAll("March", "03");
			convert_string = convert_string.replaceAll("April", "04");
			convert_string = convert_string.replaceAll("May", "05");
			convert_string = convert_string.replaceAll("June", "06");
			convert_string = convert_string.replaceAll("July", "07");
			convert_string = convert_string.replaceAll("August", "08");
			convert_string = convert_string.replaceAll("September", "09");
			convert_string = convert_string.replaceAll("October", "10");
			convert_string = convert_string.replaceAll("November", "11");
			convert_string = convert_string.replaceAll("December", "12");

			Log.d("calendar", "convert_string: " + convert_string);
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			Date currentDate = new Date();
			try {
				currentDate = format.parse(convert_string);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Log.d("calendar", currentDate.toString());
			long current_time = currentDate.getTime();
			List<myEvent> list = MainActivity.calHelp
					.getEventByCalendarAndTime(MainActivity.cr,
							MainActivity.calendars
									.get((int) (MainActivity.currentID - 1)),
							current_time);

			curFrag.changeButton(list, MainActivity.calendars.get((int)(MainActivity.currentID-1)).getDisplayName());
/*			
			Log.d("calendar", "event list size: " + list.size());
			LinearLayout linearLayout = (LinearLayout) CalendarFragment.rootView
					.findViewById(R.id.day_event_list);
			// TODO Auto-generated method stub
			for (int i = 0; i < list.size(); i++) {
				Button button = new Button(getActivity());
				listOfButtons.add(button);
				Log.d("calendar", "add one button");
				// For buttons visibility, you must set the layout params in
				// order to give some width and height:
				LayoutParams params = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//				button.setBackgroundColor(0xffffff);
				Log.d("calendar", list.get(i).toString());
				button.setTag(list.get(i).toString());
				button.setText(list.get(i).getDescription());
				button.setTextColor(0xFFFFFF);
				button.setLayoutParams(params);

				linearLayout.addView(button);
			}
*/			
			/*
			 * for(int i = 0; i < list.size(); i++){ Button button = new
			 * Button(getActivity()); listOfButtons.add(button);
			 * Log.d("calendar", "add one button" ); //For buttons visibility,
			 * you must set the layout params in order to give some width and
			 * height: LayoutParams params = new
			 * LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			 * // button.setBackgroundColor("0xffffff");
			 * Log.d("calendar",list.get(i).toString());
			 * button.setTag(list.get(i).toString());
			 * button.setLayoutParams(params);
			 * button.setText(list.get(i).getDescription()); //
			 * button.setTextColor(0xFFFFFF); linearLayout.addView(button);
			 * 
			 * }
			 */
			// //refresh buttons
			// day_event_5.setText("");
			// day_event_4.setText("");
			// day_event_3.setText("");
			// day_event_2.setText("");
			// day_event_1.setText("");
			// //set buttons
			// switch(EVENT_NUM=EVENT_NUM%6){
			// default:
			// case 5: day_event_5.setText("event_5");
			// case 4: day_event_4.setText("event_4");
			// case 3: day_event_3.setText("event_3");
			// case 2: day_event_2.setText("event_2");
			// case 1: day_event_1.setText("event_1");
			// case 0: break;
			// }
			// EVENT_NUM++;
			// Button day_event = new Button(_context);
			// day_event.setText(strEv

			try {
				Date parsedDate = dateFormatter.parse(date_month_year);
				Log.d(tag, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}
}
