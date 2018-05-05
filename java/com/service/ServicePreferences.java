package com.service;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.puissance4.R;

/**
 * Classe permettant des charger les préférences depuis des ressources XML.
 */
public class ServicePreferences extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		// Charger les préférences depuis les ressources XML
		addPreferencesFromResource(R.xml.preferences);
		
	}
	
}
