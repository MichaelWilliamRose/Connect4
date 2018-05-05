package com.puissance4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Classe de gestion de l'activité Credits.
 */
public class Credits extends Activity implements OnClickListener {
	
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
    	
    	Button button_reglesDuJeu = (Button)findViewById(R.id._buttonReglesJeu);
    	
    	// On initialise chaque élément
    	if (button_reglesDuJeu != null) {
    		// On ajoute un listener sur le click du bouton
    		button_reglesDuJeu.setOnClickListener(this);
    	}
    	
    }

    /**
     * Capter l'évènement Click sur la vue.
     */
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id._buttonReglesJeu:
			this.demarrerReglesDuJeu();
			break;
		
		default:
			break;
			
		}
		
	}
	
	/**
	 * Démarrer l'activité Créer une partie.
	 */
	private void demarrerReglesDuJeu() {
		
		// Afficher l'activité ReglesDuJeu
		Intent intentReglesDuJeu = new Intent(_contexteApplication, ReglesDuJeu.class);
		startActivity(intentReglesDuJeu);
		// On termine cette activité
		finish();
		
	}

}
