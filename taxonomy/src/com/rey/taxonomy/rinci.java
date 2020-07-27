package com.rey.taxonomy;


import com.rey.taxonomy.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class rinci extends Activity {
	Button p ,h,t, k;
	Intent translate_dialog;
	private int DICT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rinci);
	        
	        /* UI Initialization */
	    	p = (Button) findViewById(R.id.PTaxonomy);
			h = (Button) findViewById(R.id.HTaxonomy);
			t = (Button) findViewById(R.id.TTaxonomy);
			k = (Button) findViewById(R.id.kuis);
			p.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					translate("Taksonomi"); 
				}
			});
			
			h.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					translate("Hewan"); 
					
				}
			});
			
			t.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					translate("Tumbuhan"); 
					
				}
			});
			
			k.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					translates("kuis");
				}
			});
	}	
	
	protected void translates(String text) {
		translate_dialog = new Intent().setClass(this, kuis.class);
		startActivity(translate_dialog);
		
	}
	protected void translate(String text) {
		translate_dialog = new Intent().setClass(this, Arti.class);
		translate_dialog.putExtra("DICT", DICT);
		translate_dialog.putExtra("TEXT", text);
		startActivity(translate_dialog);
	}
}
