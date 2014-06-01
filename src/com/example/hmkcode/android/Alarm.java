package com.example.hmkcode.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class Alarm {
	private CalendarHelper mHelper;	
	List<PendingIntent> pil;
	List<BroadcastReceiver> brl;
	List<AlarmManager> aml;
	Context mContext;
	//sql database
	MySQLiteHelper db;

	//max volume indices
	private int max_alarm_index;
	private int max_ring_index;
	private int max_music_index;
	private int max_system_index;
	private int max_noti_index;	

	//audio manager
	private AudioManager myAudioManager;	
	
	private static final String tag="Alarm";
	
	//text for different modes
	private String[] modeText=new String[]{"ring","silent","vibrate"};
	
	public Alarm(Context mContext){
		//get application context
		this.mContext = mContext;

		//get database
		this.db= new MySQLiteHelper(mContext);
		
		//grab audio manager
		myAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
		
		//========================initialize all max volumes======================
		this.max_alarm_index=this.getMaxVolumeIndex(AudioManager.STREAM_ALARM);
		this.max_ring_index=this.getMaxVolumeIndex(AudioManager.STREAM_RING);
		this.max_music_index=this.getMaxVolumeIndex(AudioManager.STREAM_MUSIC);
		this.max_system_index=this.getMaxVolumeIndex(AudioManager.STREAM_SYSTEM);
		this.max_noti_index=this.getMaxVolumeIndex(AudioManager.STREAM_NOTIFICATION);		

		//========================================add callbacks===============================
		//intialize all lists
		this.pil = new ArrayList<PendingIntent>();
		this.brl = new ArrayList<BroadcastReceiver>();
		this.aml = new ArrayList<AlarmManager>();
		
		// try to set alarm manager according a list of events
		mHelper= new CalendarHelper();
		List<myCalendar> calList=mHelper.getAllCalendars(this.mContext.getContentResolver(), "canjian.myself@gmail.com", "com.google");

		long milliseconds = System.currentTimeMillis();				
		Log.d(tag, "current time in milli: " + milliseconds);
		
		//support multiple calendar
		for (myCalendar eachCal: calList){
			ArrayList<myEvent> curEvents=mHelper.getEventByCalendarAndTime(this.mContext.getContentResolver(), eachCal, milliseconds);
			for (myEvent eachEvt:curEvents){
				Log.d(tag, "event set alarm: " + eachEvt.toString());
				BroadcastReceiver brt= new BroadcastReceiver(){
					@Override
					public void onReceive(Context context, Intent intent) {
						//load volume profile and change volume here
						Log.d(tag, "waked up !!! profile record key: "+intent.getAction());
						Toast.makeText(context, "Rise and Shine!!!!!!!!!!", Toast.LENGTH_LONG).show();
						setProfile(intent.getAction(),intent.getStringExtra("cal"));
					}
				};
				AlarmManager amt;
				PendingIntent pit;
				mContext.registerReceiver(brt, new IntentFilter(eachEvt.toString()));
				//broad cast event
				Intent br_intent= new Intent(eachEvt.toString());
				br_intent.putExtra("cal", eachCal.toString());
				pit = PendingIntent.getBroadcast(this.mContext, 0,
						br_intent, 0);
				amt = (AlarmManager) (this.mContext.getSystemService(Context.ALARM_SERVICE));
				//set wakeup time
				long wakeupTime = eachEvt.getStartDate().getTime();
				Log.d(tag,"wake up after (ms): "+(wakeupTime - System.currentTimeMillis()) + ";  wake up date: "+eachEvt.getStartDate().toString());
				Log.d(tag,"current elapsedreal time: " + SystemClock.elapsedRealtime());
				amt.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (wakeupTime - System.currentTimeMillis()), pit);
				
				//add all of these stuff into list
				this.brl.add(brt);
				this.aml.add(amt);
				this.pil.add(pit);
			}
		}
	}
	
	/**
	 * should be called when activity is destroyed. cancel all alarms
	 */
	public void destroy() {
		int cnt=0;
		for (AlarmManager eachAlarm: aml){
			eachAlarm.cancel(pil.get(cnt));
			this.mContext.unregisterReceiver(brl.get(cnt));
			cnt ++;			
		}
	}		
	
	/**
	 * set volume to profile index by event
	 * @param event
	 */
	private void setProfile(String event, String cal){
		Log.d(tag, "event happend: "+event);		
		Log.d(tag, "query db to find profile");
		Profile out=db.getProfile(event);
		Log.d(tag, "profile: "+ out.getProfile());
		
		String data=out.getProfile();
		if (data != null){ //only change settings if there is a profile
			//parse the values first
			String[] valueString=data.split(";");
			if (valueString.length != 7){
				Log.e(tag, "record has wrong number of items. don't change current volume");
			} else {
				//convert String to int first
				int[] values=new int[7];
				for (int i =0; i<7;i++){
					values[i] = Integer.valueOf(valueString[i]);
				}
				if (values[6]== 0){ //ringer mode
					
					float index_ring= (float)values[0]/100 * (float)max_alarm_index;
					float index_music= (float)values[1]/100 * (float)max_ring_index;
					float index_system= (float)values[2]/100 * (float)max_music_index;
					float index_alarm= (float)values[3]/100 * (float)max_system_index;
					float index_noti= (float)values[4]/100 * (float)max_noti_index;			

					myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);					
					this.setVolume(AudioManager.STREAM_RING, (int)(index_ring+0.5));
					this.setVolume(AudioManager.STREAM_MUSIC,(int)(index_music+0.5) );			
					this.setVolume(AudioManager.STREAM_ALARM, (int)(index_alarm+0.5));			
					this.setVolume(AudioManager.STREAM_SYSTEM, (int)(index_system+0.5));					
					this.setVolume(AudioManager.STREAM_NOTIFICATION, (int)(index_noti+0.5));	
					Toast.makeText(this.mContext, "turn on ringer mode", Toast.LENGTH_LONG).show();
				} else if (values[6] == 1) { //silent
					myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);					
					Toast.makeText(this.mContext, "turn on silent mode", Toast.LENGTH_LONG).show();					
				} else if (values[6] == 2){ //vibrate
					myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);					
					Toast.makeText(this.mContext, "turn on vibrate mode", Toast.LENGTH_LONG).show();					
				} else {
					Log.e(tag, "some unkown mode in the database");
				}
			}
		} else {  //use calendar default
			setCalProfile(cal);
		}
		
	}
	
	
	private void setCalProfile(String cal){
		Log.d(tag,"no profile match. try calendar default");
		Log.d(tag, "query db to find profile");
		Profile out=db.getProfile(cal);
		Log.d(tag, "profile: "+ out.getProfile());
		
		String data=out.getProfile();
		if (data != null){ //only change settings if there is a profile
			//parse the values first
			String[] valueString=data.split(";");
			if (valueString.length != 7){
				Log.e(tag, "record has wrong number of items. don't change current volume");
			} else {
				//convert String to int first
				int[] values=new int[7];
				for (int i =0; i<7;i++){
					values[i] = Integer.valueOf(valueString[i]);
				}
				if (values[6]== 0){ //ringer mode
					float index_ring= (float)values[1]/100 * (float)max_ring_index;
					float index_music= (float)values[2]/100 * (float)max_music_index;
					float index_system= (float)values[3]/100 * (float)max_system_index;
					float index_alarm= (float)values[4]/100 * (float)max_alarm_index;
					float index_noti= (float)values[5]/100 * (float)max_noti_index;			

					this.setVolume(AudioManager.STREAM_RING, (int)(index_ring+0.5));
					this.setVolume(AudioManager.STREAM_MUSIC,(int)(index_music+0.5) );			
					this.setVolume(AudioManager.STREAM_ALARM, (int)(index_alarm+0.5));			
					this.setVolume(AudioManager.STREAM_SYSTEM, (int)(index_system+0.5));					
					this.setVolume(AudioManager.STREAM_NOTIFICATION, (int)(index_noti+0.5));
					myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);										
					Toast.makeText(this.mContext, "turn on ringer mode", Toast.LENGTH_LONG).show();
				} else if (values[6] == 1) { //silent
					this.setVolume(AudioManager.STREAM_MUSIC, 0);
					this.setVolume(AudioManager.STREAM_SYSTEM, 0);
					this.setVolume(AudioManager.STREAM_NOTIFICATION, 0);					
					this.setVolume(AudioManager.STREAM_ALARM, 0);					
					myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);					
					Toast.makeText(this.mContext, "turn on silent mode", Toast.LENGTH_LONG).show();					
				} else if (values[6] == 2){ //vibrate
					this.setVolume(AudioManager.STREAM_MUSIC, 0);
					this.setVolume(AudioManager.STREAM_SYSTEM, 0);
					this.setVolume(AudioManager.STREAM_NOTIFICATION, 0);					
					this.setVolume(AudioManager.STREAM_ALARM, 0);					
					myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					Toast.makeText(this.mContext, "turn on vibrate mode", Toast.LENGTH_LONG).show();					
				} else {
					Log.e(tag, "some unkown mode in the database");
				}
			}
		} else {  
			setProfile(cal, null);
		}
		
	}
	
	
	/**
	 * set volume of streamtype at index
	 * index should be less than the Max volulme of that type
	 * use getVolumeIndex first
	 * @param streamtype
	 * @param index
	 */
	public void setVolume(int streamtype, int index){
		if (index > myAudioManager.getStreamMaxVolume(streamtype))
			Log.e(tag,"try to set over maximum index of streamtype: "+ streamtype);
		//set the volume of streamtype, and show a toast of current stream volume
		myAudioManager.setStreamVolume(streamtype, index, 0);	
	}
	
	
	/**
	 * get the max index that can be set for a specific kind of stream
	 * 
	 * @param streamType: should be using constants such as AudioManager.STREAM_MUSIC
	 */
	public int getMaxVolumeIndex(int streamType){
		int ret;
		switch (streamType){
		case AudioManager.STREAM_ALARM:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
			Log.d(tag,"get max index of alarm");
			break;
		case AudioManager.STREAM_DTMF:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF);
			Log.d(tag,"get max index of stream dtmf");			
			break;
		case AudioManager.STREAM_MUSIC:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			Log.d(tag,"get max index of music");			
			break;
		case AudioManager.STREAM_NOTIFICATION:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
			Log.d(tag,"get max index of notification");			
			break;
		case AudioManager.STREAM_RING:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			Log.d(tag,"get max index of ring");			
			break;
		case AudioManager.STREAM_SYSTEM:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			Log.d(tag,"get max index of system");			
			break;
		case AudioManager.STREAM_VOICE_CALL:
			ret = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			Log.d(tag,"get max index of voice call");			
			break;
		default:
			ret=-1;
		}
		return ret;
	}
	
}
