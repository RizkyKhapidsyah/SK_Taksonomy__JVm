package com.rey.taxonomy;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rey.taxonomy.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Arti extends Activity implements TextToSpeech.OnInitListener {
	
	final static int  SEMUA  = 1;
	final static int  HEWAN = 2;
	final static int  TUMBUHAN  = 3;
	final String[] dict_name = new String[] {"Semua","HEWAN", "TUMBUHAN"};
	
	private int DICT;
	private String text;
	private SQLiteDatabase db = null;
	private Cursor kingdom = null;
	private DBKamus db_kamus_helper;
    private TextToSpeech mTts;
    private int SpeechStatus;
    private float font_size;

	TextView title, content;
	Button speech, close;
	WebView pic;
	
	Handler handler = new Handler();
	Thread searching;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      /* Get Intent Parameter */
      this.getIntentParameter();
      
      /* Open Database File */
      db_kamus_helper = new DBKamus(this);
      try {
      	db_kamus_helper.openDataBase();
      	db = db_kamus_helper.getReadableDatabase();
      }catch(SQLException sqle){
      	throw sqle;
      }
      
      /* UI Initialization */
      if (checkResult()) this.initUI();
	}

	private void getIntentParameter() {
		// TODO Auto-generated method stub
		Uri data = this.getIntent().getData();
		if (data!=null) {
			DICT = Integer.parseInt(data.getQueryParameter("dict"));
			text = data.getQueryParameter("text");
		} else {
			DICT = this.getIntent().getExtras().getInt("DICT", 1);
		    text = this.getIntent().getExtras().getString("TEXT");
		}
	}

	private boolean checkResult() {
		// TODO Auto-generated method stub
		String kingdom1;
		switch(DICT) {
			default:
			case SEMUA:
				kingdom1 = "SELECT penjelasan FROM Main where lower(Type)=lower('"+text.replace("'", "''")+"') ";
				break;
			case HEWAN:
				kingdom1 = "SELECT * FROM Hewan where lower(Nama_Hewan)=lower('"+text.replace("'", "''")+"')";
				break;
			case TUMBUHAN:
				kingdom1 = "SELECT * FROM Tumbuhan where lower(Nama_Tumbuhan)=lower('"+text.replace("'", "''")+"')";
				break;
		}
		kingdom = db.rawQuery(kingdom1, null);
		if (!kingdom.moveToFirst()) {
			Toast.makeText(this, "word `"+text+"` not found in "+dict_name[DICT-1]+" dictionary !", Toast.LENGTH_SHORT).show();
			this.finish();
			return false;
		} else return true;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.arti);
		this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mTts = new TextToSpeech(this,this);
		
		title = (TextView) findViewById(R.id.title);
		content = (TextView) findViewById(R.id.content);
		speech = (Button) findViewById(R.id.speech);
		close = (Button) findViewById(R.id.close);
		pic = (WebView) findViewById(R.id.tampil);
		
		
		/* Define event callback function */
		speech.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (SpeechStatus == TextToSpeech.LANG_MISSING_DATA || SpeechStatus == TextToSpeech.LANG_NOT_SUPPORTED)
					installLanguage();
				else
					mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			}
		});
		
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Arti.this.finish();
			}
		});
	}
	
	private boolean isValidUrl(String url) {
	    Pattern p = Patterns.WEB_URL;
	    Matcher m = p.matcher(url);
	    if(m.matches())
	        return true;
	    else
	    return false;
	}
	

	
	@Override
	public void onStart() {
		super.onStart();
		
		/* Get Preferences */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		font_size = Float.parseFloat(prefs.getString("font_size", "18"));
		
		/* Display Result */
		title.setText(text);
		content.setTextSize(TypedValue.COMPLEX_UNIT_SP, font_size);
		switch(DICT){
		case  SEMUA : 
			pic.loadUrl("file:///android_asset/img/Q.jpg");
			content.setText(kingdom.getString(0));
			content.setLinkTextColor(Color.rgb(0x44, 0x44, 0xBB));
			break;
		default :
			if(kingdom.getString(9).equals("-")){
				pic.loadUrl("file:///android_asset/img/Q.jpg");
				pic.getSettings().setJavaScriptEnabled(true);
			}else{
					pic.loadUrl(kingdom.getString(9));			    
				}
				
			
			content.setText("Kingdom\t:\t"+kingdom.getString(8)+
					"\nPhyllum\t:\t"+kingdom.getString(7)+
					"\nClass\t    :\t"+kingdom.getString(6)+
					"\nOrdo\t    :\t"+kingdom.getString(5)+
					"\nFamilia\t:\t"+kingdom.getString(4)+
					"\nGenus\t    :\t"+kingdom.getString(3)+
					"\nSpesies\t:\t"+kingdom.getString(2));
			content.setLinkTextColor(Color.rgb(0x44, 0x44, 0xBB));
			break;
		}
		
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.drawable.readermenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      float current_size = content.getTextSize();
      switch (item.getItemId()) {
      case R.id.zoomout:
    	  content.setTextSize(TypedValue.COMPLEX_UNIT_PX, current_size*(float)(1/1.2));
    	  break;
      case R.id.zoomin:
    	  content.setTextSize(TypedValue.COMPLEX_UNIT_PX, current_size*(float)1.2);
    	  break;
      default:
        return super.onOptionsItemSelected(item);
      }
      return true;
    }
    
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    /* Implements TextToSpeech.OnInitListener. */
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
        	switch(DICT) {
				default:
				case SEMUA:
				case HEWAN:
					SpeechStatus = mTts.setLanguage(Locale.ITALY);
					break;
				case TUMBUHAN:
					SpeechStatus = mTts.setLanguage(Locale.ITALY);
					break;
        	}
            if (SpeechStatus == TextToSpeech.LANG_MISSING_DATA || SpeechStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
            	speech.setBackgroundResource(R.drawable.speech_disabled);
            } else {
            	speech.setBackgroundResource(R.drawable.speech_button);
            }
        } else {
        	speech.setBackgroundResource(R.drawable.speech_disabled);
        	speech.setEnabled(false);
        	speech.setClickable(false);
        }
    }
    
    /* Install speech language if not available */
    protected void installLanguage() {
		// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
    	.setMessage("This language is not available.\nDo you want to install ?")
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent installIntent = new Intent();
		        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		        startActivity(installIntent);
			}
		})
		.setNegativeButton("No", null)
    	.show();
	}
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}