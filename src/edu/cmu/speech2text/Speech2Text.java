package edu.cmu.speech2text;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Speech2Text extends Activity implements OnClickListener {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private ListView mList;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button recordButton = (Button) findViewById(R.id.button_record);
        
        mList = (ListView) findViewById(R.id.list);
        
        // check if recognition activity is present
        PackageManager pm = getPackageManager();
        
        List<ResolveInfo> activities = pm.queryIntentActivities(
        		new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        
        if (activities.size() != 0) {
        	recordButton.setOnClickListener(this);
        }
        else {
        	recordButton.setEnabled(false);
        	recordButton.setText("Recognizer not present");
        }
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_record)
		{
			startVoiceRecognitionActivity();
		}
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech 2 Text Demo");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches =
				data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
					matches));
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}