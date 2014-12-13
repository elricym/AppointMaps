package com.example.appointmaps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

public class SettingActivity extends Activity {

    Intent intent;

    int hours, minutes;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

        // get the intent from main activity
        intent = this.getIntent();
		Button confirmButton = (Button) findViewById(R.id.button1);
        TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker1);
        timePicker.setIs24HourView(true);

        // set a listener of time picker
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override

            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minuteOfNow) {
                hours = hourOfDay;
                minutes = minuteOfNow;

                Log.i("Ming","hour:" + hours+" minute:"+minutes);
            }
        });

        timePicker.clearFocus();
		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


                // set params
                Bundle bundle = new Bundle();
                bundle.putString("title", getTitleString());
                bundle.putInt("hour", hours);
                bundle.putInt("minute", minutes);
                intent.putExtras(bundle);

                // return to the main activity
				SettingActivity.this.setResult(RESULT_OK,intent);

                // stop this activity
                SettingActivity.this.finish();
			}
		});
	}
    public String getTitleString(){
        EditText editText = (EditText)findViewById(R.id.editText1);
        return editText.getText().toString();
    }
}
