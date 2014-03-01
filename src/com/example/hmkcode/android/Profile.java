package com.example.hmkcode.android;

/**
 * model for volume database
 * @author jj
 *
 */
public class Profile {
	  private String event;
	  private String profile;

	  public Profile(String event, String profile){
		  this.event=event;
		  this.profile=profile;
	  }
	  
	  public String getEvent() {
	    return event;
	  }

	  public void setEvent(String event) {
	    this.event = event;
	  }

	  public String getProfile() {
	    return this.profile;
	  }

	  public void setProfile(String profile) {
	    this.profile= profile;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return event + ":" + profile;
	  }
}
