package edu.cmu.speech2text;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Speech2Text extends Activity implements OnClickListener, OnInitListener  {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private static final int TEXT_TO_SPEECH_CODE = 007;
	
	private ListView mList;
	
	private TextToSpeech tts;
	
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
        
        Button speakButton = (Button) findViewById(R.id.button_speak);
        speakButton.setOnClickListener(this);
        
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE);

    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_record) {
			startVoiceRecognitionActivity();
		}
		else if (v.getId() == R.id.button_speak) {
			String text = "";
			for (int i = 0; i < mList.getCount(); i++) {
				text += mList.getItemAtPosition(i).toString();
			}
			
			if (text != null && text.length() > 0) {
				Toast.makeText(this, "Saying: " + text, Toast.LENGTH_LONG).show();
				tts.speak(text, TextToSpeech.QUEUE_ADD, null);
			}
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
		} else if (requestCode == TEXT_TO_SPEECH_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				tts = new TextToSpeech(this, this);
			}
			else {
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Toast.makeText(this,
					"Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
		}
		else if (status == TextToSpeech.ERROR) {
			Toast.makeText(this,
					"Error occured while initializing Text-To-Speech engine",
					Toast.LENGTH_LONG).show();
		}
	}
}