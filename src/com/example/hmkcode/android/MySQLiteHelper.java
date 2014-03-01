package com.example.hmkcode.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	public static final String TABLE_VOLUME = "volume";
	public static final String COLUMN_ID = "event";
	public static final String COLUMN_PROFILE = "profile";

	private static final String DATABASE_NAME = "volume.db";
	private static final int DATABASE_VERSION = 1;
	private static final String tag= "MySQLiteHelper";
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_VOLUME + "(" + COLUMN_ID + " TEXT PRIMARY KEY NOT NULL, "
			+ COLUMN_PROFILE + " text not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.d(tag, "created database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUME);
		onCreate(db);
	}
	
	/**
	 * all CRUD operations
	 */
	   // Adding new contact
    public void addProfile(Profile item) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, item.getEvent()); // get event
        values.put(COLUMN_PROFILE, item.getProfile()); // get profile
 
        // Inserting Row
        try{
            db.insertOrThrow(TABLE_VOLUME, null, values);        	
        } catch (SQLException e){
        	Log.e(tag, "sql insert error, possibly inserting duplicate key");
        	Log.d(tag,"choose to update such row instead");
        	this.updateProfile(item);
        }

        db.close(); // Closing database connection
    }
 
    // Getting single profile
    Profile getProfile(String event) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_VOLUME, new String[] { COLUMN_PROFILE}, COLUMN_ID + "=?",
                new String[] {event}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        else {
        	Log.e(tag, "didn't find profile corresponding to event: "+ event);
        	return new Profile(event, null);
        };
        
        if (cursor.getCount() >0){
            Profile profile = new Profile(event,cursor.getString(0));
            // return contact
            return profile;
        } else {
        	Log.e(tag, "didn't find profile corresponding to event: "+ event);
        	return new Profile(event, null);        	
        }
    }
    
    public int updateProfile(Profile item) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
//        values.put(COLUMN_ID, item.getEvent());
        values.put(COLUMN_PROFILE, item.getProfile());
     
        // updating row
        int ret= db.update(TABLE_VOLUME, values, COLUMN_ID+ " = ?",
                new String[] {item.getEvent()});
        Log.d(tag, "update " + ret + " record");
        db.close();
        return ret;
    }    
    
    // Deleting single contact
    public void deleteProfile(String event) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VOLUME, COLUMN_ID + " = ?",
                new String[] { event});
        db.close();
    }
    
    
/*     
    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return contactList;
    }
 
    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());
 
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }
 
    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
 
    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }	
*/	
}
