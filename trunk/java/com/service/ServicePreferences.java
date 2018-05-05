package com.service;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.puissance4.R;

/**
 * Classe permettant des charger les pr�f�rences depuis des ressources XML.
 */
public class ServicePreferences extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		// Charger les pr�f�rences depuis les ressources XML
		addPreferencesFromResource(R.xml.preferences);
		
	}
	
}
