package com.puissance4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Classe de gestion de l'activit� Scores.
 */
public class Scores extends Activity implements OnClickListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores);
        
        // Initialisation de l'activit�
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
    	
    	// On affiche l'activit� principale
		Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
		startActivity(intent);
		// On termine cette activit�
		finish();
		
    }
    
    /**
     * Fonction d'initialisation au d�marrage de l'application.
     */
    public void initialisation() {
    }
	
    /**
     * Capter l'�v�nement Click sur la vue.
     */
	public void onClick(View v) {
	}

}
