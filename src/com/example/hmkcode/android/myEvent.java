package com.example.hmkcode.android;

import java.util.Date;

public class myEvent {
	private String Title;
	private String Location;
	private String Description;
	private Date StartDate;
	private Date EndDate;
	private String RDATE;
	private String RRULE;
	
	public myEvent(String Title, String Location, String Description, Date StartDate, Date EndDate, String RDATE, String RRULE){
		this.Title = Title;
		this.Location = Location;
		this.Description = Description;
		this.StartDate = StartDate;
		this.EndDate = EndDate;
		this.RDATE = RDATE;
		this.RRULE = RRULE;
	}
	
	public void setTitle(String Title){
		this.Title = Title;
	}
	public void setLocation(String Location){
		this.Location = Location;
	}
	public void setDescription(String Description){
		this.Description = Description;
	}
	public void setStartDate(Date StartDate){
		this.StartDate = StartDate;
	}
	public void setEndDate(Date EndDate){
		this.EndDate = EndDate;
	}
	public void setRDATE(String RDATE){
		this.RDATE = RDATE;
	}
	public void setRRULE(String RRULE){
		this.RRULE = RRULE;
	}
	
	public String getTitle(){
		return this.Title;
	}
	public String getLocation(){
		return this.Location;
	}
	public String getDescription(){
		return this.Description;
	}
	public Date getStartDate(){
		return this.StartDate;
	}
	public Date getEndDate(){
		return this.EndDate;
	}
	public String getRDATE(){
		return this.RDATE;
	}
	public String getRRULE(){
		return this.RRULE;
	}
	
	/*
	 * returns hour and min info in the format of "hh/mm" as a string
	 */
	public String getStartDateHourAndMin(){
		return this.StartDate.getHours() + ":" + this.StartDate.getMinutes();
	}
	/*
	 * returns hour and min info in the format of "hh/mm" as a string
	 */
	public String getEndDateHourAndMin(){
		return this.EndDate.getHours() + ":" + this.EndDate.getMinutes();
	}
}
