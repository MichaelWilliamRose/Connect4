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
 * Classe de gestion de l'activit� principale de l'application.
 */
public class MainActivity extends Activity implements OnClickListener {

	// Contexte de l'application
	private Context _contexteApplication = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Création de l'activité
		super.onCreate(savedInstanceState);
		
		// Définir la vue courante
		setContentView(R.layout.activity_main);
		
		// Initialisation de l'activité principale
		this.initialisation();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Pas de menu pour l'instant
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Fonction d'initialisation au démarrage de l'application.
	 */
	public void initialisation() {
		
		// Sauvegarde du contexte de l'application
		_contexteApplication = this.getApplicationContext();
		
		// On accède aux éléments de l'activité
		Button button_nouvellePartie = (Button) findViewById(R.id._buttonNouvellePartie);
		Button button_scores = (Button) findViewById(R.id._buttonScores);
		Button button_options = (Button) findViewById(R.id._buttonOptions);
		Button button_credits = (Button) findViewById(R.id._buttonCredits);
		Button button_quitter = (Button) findViewById(R.id._buttonQuitter);

		// On initialise chaque élément
		if (button_nouvellePartie != null) {
			// On ajoute un listener sur le click du bouton
			button_nouvellePartie.setOnClickListener(this);
		}

		if (button_scores != null) {
			// On ajoute un listener sur le click du bouton
			button_scores.setOnClickListener(this);
		}

		if (button_options != null) {
			// On ajoute un listener sur le click du bouton
			button_options.setOnClickListener(this);
		}

		if (button_credits != null) {
			// On ajoute un listener sur le click du bouton
			button_credits.setOnClickListener(this);
		}

		if (button_quitter != null) {
			// On ajoute un listener sur le click du bouton
			button_quitter.setOnClickListener(this);
		}
		
	}

	/**
	 * Capter l'évènement Click sur la vue.
	 */
	public void onClick(View vue) {
		
		switch (vue.getId()) {
		
		case R.id._buttonNouvellePartie:
			// On démarre une nouvelle partie
			this.demarrerNouvellePartie();
			break;
			
		case R.id._buttonScores:
			// Afficher l'activité concernant les scores
			this.demarrerScores();
			break;
			
		case R.id._buttonOptions:
			// Afficher l'activité concernant les options
			this.demarrerOptions();
			break;
			
		case R.id._buttonCredits:
			// Afficher l'activité concernant les crédits
			this.demarrerCredits();
			break;
			
		case R.id._buttonQuitter:
			// Quitter l'application
			this.quitterApplication();
			break;
			
		default:
			break;
			
		}
		
	}

	/**
	 * Démarrer l'activité nouvelle partie.
	 */
	private void demarrerNouvellePartie() {
		
		// Afficher l'activité NouvellePartie
		Intent intentNouvellePartie = new Intent(_contexteApplication, NouvellePartie.class);
		startActivity(intentNouvellePartie);
		// On termine cette activité
		finish();
		
	}

	/**
	 * Démarrer l'activité Scores.
	 */
	private void demarrerScores() {

		// Afficher l'activité Scores
		Intent intentScores = new Intent(_contexteApplication, Scores.class);
		startActivity(intentScores);
		// On termine cette activité
		finish();

	}

	/**
	 * Démarrer l'activité Options.
	 */
	private void demarrerOptions() {

		// Afficher l'activité Options
		Intent intentOptions = new Intent(_contexteApplication, Options.class);
		startActivity(intentOptions);
		// On termine cette activité
		finish();

	}

	/**
	 * Démarrer l'activité Credits.
	 */
	private void demarrerCredits() {

		// Afficher l'activité Credits
		Intent intentCredits = new Intent(_contexteApplication, Credits.class);
		startActivity(intentCredits);
		// On termine cette activité
		finish();

	}

	/**
	 * Quitter l'application.
	 */
	private void quitterApplication() {
		finish();
	}

}
