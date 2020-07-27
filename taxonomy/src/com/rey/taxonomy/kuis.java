package com.rey.taxonomy;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class kuis extends Activity {

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
	Button speech, close, next;
	WebView pic;
	RadioButton a,b,c,d;
	RadioGroup g;
	int nilai=0;
	
	Handler handler = new Handler();
	Thread searching;

	
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	      /* Get Intent Parameter */
	      
	      /* Open Database File */
	      db_kamus_helper = new DBKamus(this);
	      try {
	      	db_kamus_helper.openDataBase();
	      	db = db_kamus_helper.getReadableDatabase();
	      }catch(SQLException sqle){
	      	throw sqle;
	      }
	      
	      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.setContentView(R.layout.kuis);
			this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			
			title = (TextView) findViewById(R.id.title);
			content = (TextView) findViewById(R.id.content);
			speech = (Button) findViewById(R.id.speech);
			close = (Button) findViewById(R.id.close);
			a = (RadioButton) findViewById(R.id.a);
			b = (RadioButton) findViewById(R.id.b);
			c = (RadioButton) findViewById(R.id.c);
			d = (RadioButton) findViewById(R.id.d);
			g = (RadioGroup) findViewById(R.id.radioGroup1);
			next = (Button) findViewById(R.id.next);
			
			
		
			close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "nilai anda adalah = "+nilai, Toast.LENGTH_LONG).show();
	
					kuis.this.finish();
					
				}
			});
			next.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int radioButtonID = g.getCheckedRadioButtonId();
					View radioButton = g.findViewById(radioButtonID);
					int idx = g.indexOfChild(radioButton);
					if (kingdom.getString(idx+2).equals(kingdom.getString(6))){
						nilai = nilai +1;
					}
					quiz();
				}
			});
			quiz();
		}
	
public void quiz() {
		checkResult();
		title.setText(text);
		content.setText(kingdom.getString(1));
		a.setText(kingdom.getString(2));
		b.setText(kingdom.getString(3));
		c.setText(kingdom.getString(4));
		d.setText(kingdom.getString(5));
	}
	
	
	private boolean checkResult() {
		// TODO Auto-generated method stub
		int random = 1 + (int)(Math.random()*7); 
		String ran = Integer.toString(random);
		String kingdom1;
		kingdom1 = "SELECT * FROM kuis where id_kuis='"+ran.replace("'", "''")+"' ";
		kingdom = db.rawQuery(kingdom1, null);
		if (!kingdom.moveToFirst()) {
			Toast.makeText(this, "word `"+text+"` not found in "+dict_name[DICT-1]+" dictionary !", Toast.LENGTH_SHORT).show();
			this.finish();
			return false;
		} else return true;
	}


}

