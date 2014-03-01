package com.example.hmkcode.android;

public class myCalendar {
	private long calendarID;
	private String displayName;
	private String accountName;
	private String ownerName;
	
	public myCalendar(long calendarID, String displayName, String accountName, String OwnerName){
		this.calendarID = calendarID;
		this.displayName = displayName;
		this.accountName = accountName;
		this.ownerName = ownerName;
	}
	
	public void setCalendarID(long calendarID){
		this.calendarID = calendarID;
	}
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	public void setAccountName(String accountName){
		this.accountName = accountName;
	}
	public void setOwnerName(String ownerName){
		this.ownerName = ownerName;
	}
	
	public long getCalendarID(){
		return this.calendarID;
	}
	public String getDisplayName(){
		return this.displayName;
	}
	public String getAccountName(){
		return this.accountName;
	}
	public String getOwnerName(){
		return this.ownerName;
	}
}
