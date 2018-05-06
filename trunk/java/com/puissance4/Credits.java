package com.puissance4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

/**
 * Classe de gestion de l'activité Credits.
 */
public class Credits extends Activity {
	
	private Context _contexteApplication;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);
        
        // Mémoriser le contexte de l'application
        _contexteApplication = this.getApplicationContext();
        
        // Initialisation de l'activité
        this.initialisation();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Pas de menu pour l'instant
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onBackPressed() {
    	
    	// On affiche l'activité principale
		Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
		startActivity(intent);
		// On termine cette activité
		finish();
		
    }
    
    /**
     * Fonction d'initialisation au démarrage de l'application.
     */
    public void initialisation() {
    }

}
