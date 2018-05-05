package com.puissance4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Classe de gestion de l'activité ReglesDuJeu.
 */
public class ReglesDuJeu extends Activity implements OnClickListener {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regles_jeu);
        
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
    	
    	// On affiche l'activité Credits
		Intent intent = new Intent(this.getApplicationContext(), Credits.class);
		startActivity(intent);
		// On termine cette activité
		finish();
		
    }
    
    /**
     * Fonction d'initialisation au démarrage de l'application.
     */
    public void initialisation() {
    }

    /**
     * Capter l'évènement Click sur la vue.
     */
	public void onClick(View v) {
	}

}
