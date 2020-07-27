package com.rey.taxonomy;

import com.rey.taxonomy.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
 
public class Setting extends PreferenceActivity {
		Preference search_limit;
		
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.layout.setting);
                
                search_limit = (Preference) findPreference("search_limit");
                search_limit.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference prefs, Object new_value) {
						// TODO Auto-generated method stub
						search_limit.setSummary(new_value.toString()+" Entri");
						return true;
					}
				});
        }
        
        public void onStart() {
        	super.onStart();
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	search_limit.setSummary(prefs.getString("search_limit", "20")+" Entri");
        }
        
}
