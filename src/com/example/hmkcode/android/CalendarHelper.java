package com.example.hmkcode.android;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class CalendarHelper {
	// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		public static final String[] CALENDAR_PROJECTION = new String[] {
		    Calendars._ID,                           // 0
		    Calendars.ACCOUNT_NAME,                  // 1
		    Calendars.CALENDAR_DISPLAY_NAME,         // 2
		    Calendars.OWNER_ACCOUNT                  // 3
		};
		  
		// The indices for the projection array above.
		private static final int PROJECTION_ID_INDEX = 0;
		private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
		private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
		private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
		
		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		public static final String[] EVENT_PROJECTION = new String[] {
		    Events.TITLE,                           // 0
		    Events.EVENT_LOCATION,                  // 1
		    Events.DESCRIPTION,         			// 2
		    Events.DTSTART,                			// 3
		    Events.DTEND,							// 4
		    Events.RDATE,							// 5
		    Events.RRULE							// 6
		};
		  
		// The indices for the projection array above.
		private static final int EVENT_TITLE = 0;
		private static final int EVENT_LOCATION = 1;
		private static final int EVENT_DESCRIPTION = 2;
		private static final int EVENT_DTSTART = 3;
		private static final int EVENT_DTEND = 4;
		private static final int EVENT_RDATE = 5;
		private static final int EVENT_RRULE = 6;
		
		public CalendarHelper(){
			
		}
		/*
		 * This method gets all existing calendar related to the given account name 
		 * and account type
		 * 
		 * @param accName: account name. e.g. "example@gmail.com"
		 * @param accType: account type. e.g. "com.google"
		 * 
		 */
		public ArrayList<myCalendar> getAllCalendars(ContentResolver cr, String accName, String accType){
			ArrayList<myCalendar> allCalendars = new ArrayList<myCalendar>();
			// Run query to get all calendars
			Cursor cur = null;
			Uri uri = Calendars.CONTENT_URI;   
			String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
			                        + Calendars.ACCOUNT_TYPE + " = ?))";
			String[] selectionArgs = new String[] {accName, accType}; 
			// Submit the query and get a Cursor object back. 
			cur = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);
			
			// Use the cursor to step through the returned records
			while (cur.moveToNext()) {
			    long calID = 0;
			    String displayName = null;
			    String accountName = null;
			    String ownerName = null;
			      
			    // Get the field values
			    calID = cur.getLong(PROJECTION_ID_INDEX);
			    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
			    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
			              
			    // Do something with the values...
			    myCalendar temp = new myCalendar(calID, displayName, accountName, ownerName);
			    allCalendars.add(temp);
			    //Log.v("ahaha", "calID: " + calID + "displayName: " + displayName + "accountName: " + accountName + "ownerName: " + ownerName);
			}
			
			return allCalendars;
			
		}
		
		/*
		 * This method retrieves a list of events given a time and a calendar object
		 * 
		 * @param cr: contentResolver declared in MainActivity e.g.	ContentResolver cr = getContentResolver();
		 * @param cal: calendar object
		 * @param time: time in milliseconds
		 */
		public ArrayList<myEvent> getEventByCalendarAndTime(ContentResolver cr, myCalendar cal, long time){
			ArrayList<myEvent> eventList = new ArrayList<myEvent>();
			
			// declare cursor and set up uri
			Cursor cur = null;
			Uri uri = Events.CONTENT_URI;   
			
			// get id and start time in string format
			String calID = cal.getCalendarID() + "";
			String time_string = time + "";
			long endTime = time + 86400000;
			String endTime_string = endTime + "";
			
			String selection = "((" + Events.CALENDAR_ID + " = ?) AND ("
								+ Events.DTSTART + " > ?) AND ("
								+ Events.DTSTART + " < ?))";
			String[] selectionArgs = new String[] {calID, time_string, endTime_string}; 
			// Submit the query and get a Cursor object back. 
			cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
			
			// access result
			while (cur.moveToNext()) {
			    String title = null;
			    String location = null;
			    String description = null;
			    Date StartDate = null;
			    Date EndDate = null;
			    String RDATE = null;
			    String RRULE = null;
			      
			    // Get the field values
			    title = cur.getString(EVENT_TITLE);
			    location = cur.getString(EVENT_LOCATION);
			    description = cur.getString(EVENT_DESCRIPTION);
			    StartDate = new Date(Long.parseLong(cur.getString(EVENT_DTSTART)));
			    EndDate = new Date(Long.parseLong(cur.getString(EVENT_DTEND)));
			    RDATE = cur.getString(EVENT_RDATE);
			    RRULE = cur.getString(EVENT_RRULE);
			              
			    // Do something with the values...
			    myEvent temp = new myEvent(title, location, description, StartDate, EndDate, RDATE, RRULE);
			    eventList.add(temp);
			    //Log.v("ahaha", "calID: " + calID + "displayName: " + displayName + "accountName: " + accountName + "ownerName: " + ownerName);
			}
			
			return eventList;
		}
}
