package com.ecs.android.sample.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class AndroidWidgetSample extends Activity implements OnSharedPreferenceChangeListener{

	private Button btnSendText;
	private Button btnAlarmToggler;
	private AlarmManager alarmManager;
	private SharedPreferences prefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.prefs.registerOnSharedPreferenceChangeListener(this);
        this.alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        
        btnSendText = (Button) findViewById(R.id.btn_send_text);
        btnAlarmToggler = (Button) findViewById(R.id.btn_alarm_toggle);
        
        toggleButtonText();

        btnSendText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	sendTextToWidget(AndroidWidgetSample.this);
            }
        });
        btnAlarmToggler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	toggleAlarm();
            }
        });

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.prefs.unregisterOnSharedPreferenceChangeListener(this);
	}
    
    
    private void sendTextToWidget(Context context) {
		Intent uiIntent2 = new Intent(Constants.ACTION_WIDGET_UPDATE_FROM_ACTIVITY);
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		uiIntent2.putExtra(Constants.INTENT_EXTRA_WIDGET_TEXT,"Button clicked on Activity at " + sdf.format(new Date()));
		context.sendBroadcast(uiIntent2);
    }
    
    public void startAlarm() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());
    	calendar.add(Calendar.SECOND, 10);
    	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20*1000, getPendingIntentForAlarm());
    	final Editor edit = prefs.edit();
		edit.putBoolean(Constants.ALARM_STATUS, true);
		edit.commit();
    }
    
	private void toggleAlarm() {
		if (isAlarmEnabled()) {
			stopAlarm();
		} else {
			startAlarm();
		}
	}
	
	private boolean isAlarmEnabled() {
		return this.prefs.getBoolean(Constants.ALARM_STATUS, false);
	}
    
    private PendingIntent getPendingIntentForAlarm() {
    	Intent intent = new Intent(Constants.ACTION_WIDGET_UPDATE_FROM_ALARM);
    	intent.putExtra(Constants.INTENT_EXTRA_WIDGET_TEXT,"Alarm triggered widget");
    	return PendingIntent.getBroadcast(this, 0, intent, 0);
    }
    
    public void stopAlarm() {
    	alarmManager.cancel(getPendingIntentForAlarm());
    	final Editor edit = prefs.edit();
		edit.putBoolean(Constants.ALARM_STATUS, false);
		edit.commit();    	
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		if (Constants.ALARM_STATUS.equals(key)) {
			toggleButtonText();
		}
	}


	private void toggleButtonText() {
		if (isAlarmEnabled()) {
			this.btnAlarmToggler.setText("Stop Alarm");
		} else {
			this.btnAlarmToggler.setText("Start Alarm");
		}
	}
}