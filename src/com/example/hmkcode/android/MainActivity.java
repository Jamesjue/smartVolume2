package com.example.hmkcode.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	//private List<String>drawerListViewItems;
	private static String[] drawerListViewItems;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	//private String categoery;
	public static long currentID;
	public static ArrayList<myCalendar> calendars;
	public static ContentResolver cr;
	public static CalendarHelper calHelp;
	private static Alarm mAlarm;
	
	@SuppressLint("NewApi") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Intent inMsg=getIntent();
		//this.categoery = inMsg.getStringExtra("categoery");
		//String[] calendarCategoery = categoery.split("[&&&]+");
		cr = getContentResolver();
		// declare CalendarHelper
		calHelp = new CalendarHelper();
		String accountName = "canjian.myself@gmail.com";
		String accountType = "com.google";
		
		calendars = calHelp.getAllCalendars(cr, accountName, accountType);
		drawerListViewItems = new String[calendars.size()];
		Iterator<myCalendar> iter = calendars.iterator();
		int count = 0;
		while(iter.hasNext()){
			myCalendar temp = iter.next();
			drawerListViewItems[count] = temp.getDisplayName();
			count++;
		}
		

		// get list items from strings.xml
		//drawerListViewItems = getResources().getStringArray(R.array.items);
		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_listview_item, drawerListViewItems));

		// App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		actionBarDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.ic_navigation_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				);


		// Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		drawerListView.setOnItemClickListener(new SlideMenuClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true); 

		// just styling option add shadow the right edge of the drawer
		drawerLayout.setDrawerShadow(R.drawable.ic_navigation_drawer, GravityCompat.START);
		
		mAlarm = new Alarm(getApplicationContext());
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			currentID = calendars.get(position).getCalendarID();
			selectItem(position);
		}
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new CalendarFragment();
		Bundle args = new Bundle();
		args.putInt(CalendarFragment.ARG_PLANET_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		drawerListView.setItemChecked(position, true);
		setTitle(drawerListViewItems[position]);
		drawerLayout.closeDrawer(drawerListView);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
		// then it has handled the app icon touch event
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy(){
		mAlarm.destroy();
		super.onDestroy();
	}
	
	
	public static String getName(int position){
		return drawerListViewItems[position];
	}
}
