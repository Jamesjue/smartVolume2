package com.example.hmkcode.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * acitivty class that represents an audio volume profile. 
 * When started should pass in an intent with "fileName" as key of an extra
 * Such extra represent what file this profile will store into
 * @author jj
 *
 */
public class VolumeControlActivity extends Activity implements OnSeekBarChangeListener {

	private static final String tag="audioController";
	private Button Vibrate, Ring, Silent;
	private TextView Status;
	private AudioManager myAudioManager;
	private SeekBar allBar,ringBar,notiBar,alarmBar,musicBar,systemBar;
	private int max_alarm_index;
	private int max_ring_index;
	private int max_music_index;
	private int max_system_index;
	private int max_noti_index;	

	private int cur_alarm_index;
	private int cur_ring_index;
	private int cur_music_index;
	private int cur_system_index;
	private int cur_noti_index;	
	private String event;
	
	private String[] modeText=new String[]{"ring","silent","vibrate"};
			
	//volume mode: 0 -- ring, 1 -- silent, 2 -- vibrate
	private int mode;
	
	//sql database
	MySQLiteHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volumecontrol);
		
		Intent inMsg=getIntent();
		this.event= inMsg.getStringExtra("event"); //the key of database record. event.toString();
		Log.d(tag,"volum control get event " + this.event);
		if (this.event == null){
			this.event="tmp";
			Log.e(tag, "profile activity invoked without event name");
		}

		//activity layout
		Vibrate = (Button) findViewById(R.id.button2);
		Ring = (Button) findViewById(R.id.button4);
		Silent = (Button) findViewById(R.id.button3);
		Status = (TextView) findViewById(R.id.textView2);


		this.allBar=(SeekBar)findViewById(R.id.allBar);
		this.ringBar=(SeekBar)findViewById(R.id.ringBar);
		this.musicBar=(SeekBar)findViewById(R.id.musicBar);
		this.systemBar=(SeekBar)findViewById(R.id.systemBar);
		this.notiBar=(SeekBar)findViewById(R.id.notificationBar);
		this.alarmBar=(SeekBar)findViewById(R.id.alarmBar);
		
		
		myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		//get database
		this.db= new MySQLiteHelper(this);
		
		//initialize all max volumes
		this.max_alarm_index=this.getMaxVolumeIndex(AudioManager.STREAM_ALARM);
		this.max_ring_index=this.getMaxVolumeIndex(AudioManager.STREAM_RING);
		this.max_music_index=this.getMaxVolumeIndex(AudioManager.STREAM_MUSIC);
		this.max_system_index=this.getMaxVolumeIndex(AudioManager.STREAM_SYSTEM);
		this.max_noti_index=this.getMaxVolumeIndex(AudioManager.STREAM_NOTIFICATION);		
		
		//initialize all cur volumes
		this.getCurVolumes();

		//==================test
		/*
		Log.d(tag,"start debug mysqlite");
		db.addProfile(new Profile(this.event, "1;10;1;10;10;10;0;1"));
		Profile out=db.getProfile(this.event);
		Log.d(tag, "prev_settings: "+ out.getProfile());
		db.deleteProfile(this.event);
		Log.d(tag, "delete record");
		out=db.getProfile(this.event);
		Log.d(tag, "should be null actual: "+ out.getProfile());
		*/
		
		this.load();
		
		//set progress change listener. must be after load
		allBar.setOnSeekBarChangeListener(this);
		ringBar.setOnSeekBarChangeListener(this);
		musicBar.setOnSeekBarChangeListener(this);
		systemBar.setOnSeekBarChangeListener(this);
		notiBar.setOnSeekBarChangeListener(this);
		alarmBar.setOnSeekBarChangeListener(this);		
	}

	/**
	 * set all bars to a certain value
	 * @param arg1
	 */
	private void setAllBars(int arg1){
		this.alarmBar.setProgress(arg1);
		this.ringBar.setProgress(arg1);
		this.allBar.setProgress(arg1);
		this.musicBar.setProgress(arg1);
		this.systemBar.setProgress(arg1);
		this.notiBar.setProgress(arg1);			
	}
	
	
	/**
	 * get current volumes of all stream
	 */	
	private void getCurVolumes(){
		this.cur_alarm_index=myAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		this.cur_ring_index=myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		this.cur_music_index=myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.cur_system_index=myAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		this.cur_noti_index=myAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);		
	}

	public void vibrate(View view) {
		this.setAllBars(0);
		this.mode=2;
	}
	
	/**
	 * set ring
	 * @param view
	 */
	public void ring(View view) {
		this.mode=0;
	}

	/**
	 * silent phone
	 * @param view
	 */
	public void silent(View view) {
		this.mode=1;
		this.setAllBars(0);		
	}
	
	public void mode(View view) {
		int mod = myAudioManager.getRingerMode();
		if (mod == AudioManager.RINGER_MODE_NORMAL) {
			Status.setText("Current Status: Ring");
		} else if (mod == AudioManager.RINGER_MODE_SILENT) {
			Status.setText("Current Status: Silent");
		} else if (mod == AudioManager.RINGER_MODE_VIBRATE) {
			Status.setText("Current Status: Vibrate");
		} else {
			Log.e(tag, "something with the mode: it's not ring, silent nor vibrate");
		}
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
		myAudioManager.setStreamVolume(streamtype, index, AudioManager.FLAG_SHOW_UI);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * called when user changes value of seek bars
	 */
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
//		this.Status.setText(String.valueOf(arg1));
		if (arg0 == this.allBar){
/*
			float index_alarm= (float)arg1/100 * (float)max_alarm_index;
			float index_ring= (float)arg1/100 * (float)max_ring_index;
			float index_music= (float)arg1/100 * (float)max_music_index;
			float index_system= (float)arg1/100 * (float)max_system_index;
			float index_noti= (float)arg1/100 * (float)max_noti_index;			
			
			this.setVolume(AudioManager.STREAM_RING, (int)(index_ring+0.5));
			this.setVolume(AudioManager.STREAM_MUSIC,(int)(index_music+0.5) );			
			this.setVolume(AudioManager.STREAM_ALARM, (int)(index_alarm+0.5));			
			this.setVolume(AudioManager.STREAM_SYSTEM, (int)(index_system+0.5));					
			this.setVolume(AudioManager.STREAM_NOTIFICATION, (int)(index_noti+0.5));	
*/
			this.setAllBars((int)(arg1+0.5));
			
		} 
/*		
		else if (arg0 == this.ringBar){
			float index= (float)arg1/100 * (float)max_ring_index;
			int index_int=(int)(index+0.5);
			this.setVolume(AudioManager.STREAM_RING, index_int);
		} else if (arg0 == this.musicBar){
			float index= (float)arg1/100 * (float)max_music_index;
			int index_int=(int)(index+0.5);			
			this.setVolume(AudioManager.STREAM_MUSIC, index_int);			
		} else if (arg0 == this.alarmBar){
			float index= (float)arg1/100 * (float)max_alarm_index;
			int index_int=(int)(index+0.5);						
			this.setVolume(AudioManager.STREAM_ALARM, index_int);			
		} else if (arg0 == this.systemBar){
			float index= (float)arg1/100 * (float)max_system_index;
			int index_int=(int)(index+0.5);									
			this.setVolume(AudioManager.STREAM_SYSTEM, index_int);			
		} else if (arg0 == this.notiBar){
			float index= (float)arg1/100 * (float)max_noti_index;
			int index_int=(int)(index+0.5);												
			this.setVolume(AudioManager.STREAM_NOTIFICATION, index_int);			
		}
		if (arg1 != 0){
			myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);			
		}
		*/
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * get current settings from seekbars
	 * this is the value in the sql database
	 */
	private String getCurSettings(){
		StringBuilder mProfile=new StringBuilder();
		mProfile.append(this.allBar.getProgress()+";");		
		mProfile.append(this.ringBar.getProgress()+";");
		mProfile.append(this.musicBar.getProgress()+";");
		mProfile.append(this.systemBar.getProgress()+";");
		mProfile.append(this.alarmBar.getProgress()+";");		
		mProfile.append(this.notiBar.getProgress()+";");
		mProfile.append(this.mode);
		return mProfile.toString();

	}
	
	/**
	 * save current seekbar values to file
	 * in order of: ring, music, system, alarm, noti in each line
	 */
	private void save(){
		String profile=this.getCurSettings();
		Profile saveInstance=new Profile(this.event, profile);
		this.db.addProfile(saveInstance);
		Log.d(tag, "add record "+saveInstance.toString());
	}

	/**
	 * current settings of the phone
	 */
	private void generic_setting(){
		//indicate initial mode
		int mod = myAudioManager.getRingerMode();
		if (mod == AudioManager.RINGER_MODE_NORMAL) {
			Status.setText("ring");
			this.mode=0;
		} else if (mod == AudioManager.RINGER_MODE_SILENT) {
			Status.setText("silent");
			this.mode=1;
		} else if (mod == AudioManager.RINGER_MODE_VIBRATE) {
			Status.setText("vibrate");
			this.mode=2;
		}

		//initialize all cur volumes
		this.getCurVolumes();

		//initialize seek bars to match current volume
		if (mod == AudioManager.RINGER_MODE_NORMAL){
			float alarm_per = (float)this.cur_alarm_index / this.max_alarm_index;
			float ring_per = (float)this.cur_ring_index/this.max_ring_index;
			float music_per = (float)this.cur_music_index / this.max_music_index;
			float system_per = (float)this.cur_system_index / this.max_system_index;
			float noti_per = (float)this.cur_noti_index / this.max_noti_index;
			this.alarmBar.setProgress((int)(alarm_per*100));
			this.ringBar.setProgress((int)(ring_per*100));
			this.allBar.setProgress((int)(ring_per*100));
			this.musicBar.setProgress((int)(music_per*100));
			this.systemBar.setProgress((int)(system_per*100));
			this.notiBar.setProgress((int)(noti_per*100));			
		} else { //when not in ringer mode, seekbar should be zero
			this.setAllBars(0);
		}
	}
	
	/**
	 * try to load from database's profile to set seekbar values as previous values
	 * if there is no entry in the database, then load current system values
	 */
	private void load(){
		Log.d(tag, "load: query event "+ this.event);
		Profile prev_profile=db.getProfile(this.event);
		String prev_data=prev_profile.getProfile();
		if (prev_data == null){ //when such record not find set all bars
			this.generic_setting();
		} else {
			//parse the values first
			String[] values=prev_data.split(";");
			if (values.length != 7){
				Log.e(tag, "record has wrong number of items. loading generic settings");
				this.generic_setting();				
			} else {
				if (Integer.valueOf(values[6]) != 0){ //silent or vibrate
					this.setAllBars(0);
					this.mode=Integer.valueOf(values[6]);
					this.Status.setText(this.modeText[this.mode]);
				} else {
					this.allBar.setProgress(Integer.valueOf(values[0]));
					this.ringBar.setProgress(Integer.valueOf(values[1]));
					this.musicBar.setProgress(Integer.valueOf(values[2]));
					this.systemBar.setProgress(Integer.valueOf(values[3]));
					this.alarmBar.setProgress(Integer.valueOf(values[4]));
					this.notiBar.setProgress(Integer.valueOf(values[5]));
					this.mode=Integer.valueOf(values[6]);
					this.Status.setText(this.modeText[this.mode]);
				}
				Log.d(tag, "load success");								
			}
		}
	}
	
	
	@Override
	public void onStop(){
		Log.d(tag, "stop the audio profile acitivity, save settings");
		this.save();
		super.onStop();
	}
}
