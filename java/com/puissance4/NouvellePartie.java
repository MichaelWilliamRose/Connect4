package com.puissance4;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.service.ServiceBluetooth;

/**
 * Classe de gestion de l'activité NouvellePartie.
 */
public class NouvellePartie extends Activity implements OnClickListener {

	// Contexte de l'application
	private Context _contexteApplication = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.nouvelle_partie);
        
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
    	
    	// Sauvegarde du contexte de l'application
        _contexteApplication = this.getApplicationContext();
    	
    	// On accède aux éléments de l'activité
    	Button button_creerPartieUnJoueur = (Button)findViewById(R.id._buttonPartieUnJoueur);
    	Button button_creerPartieMultijoueurLocale = (Button)findViewById(R.id._buttonPartieMultijoueurLocale);
    	Button button_creerPartieMultijoueurBluetooth = (Button)findViewById(R.id._buttonPartieMultijoueurBluetooth);
    	
    	// On initialise chaque élément
    	if (button_creerPartieUnJoueur != null) {
    		// On ajoute un listener sur le click du bouton
    		button_creerPartieUnJoueur.setOnClickListener(this);
    	}
    	
    	// On initialise chaque élément
    	if (button_creerPartieMultijoueurLocale != null) {
    		// On ajoute un listener sur le click du bouton
    		button_creerPartieMultijoueurLocale.setOnClickListener(this);
    	}
    	
    	// On initialise chaque élément
    	if (button_creerPartieMultijoueurBluetooth != null) {
    		// On ajoute un listener sur le click du bouton
    		button_creerPartieMultijoueurBluetooth.setOnClickListener(this);
    	}
    	
    }
	
    /**
     * Capter l'évènement Click sur la vue.
     */
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id._buttonPartieUnJoueur:
			this.demarrerPartieUnJoueur();
			break;
			
		case R.id._buttonPartieMultijoueurLocale:
			this.demarrerPartieMultijoueurLocale();
			break;
			
		case R.id._buttonPartieMultijoueurBluetooth:
			this.demarrerPartieMultijoueurBluetooth();
			break;
		
		default:
			break;
		
		}
		
	}
	
	/**
	 * Démarrer l'activité partie un joueur.
	 */
	private void demarrerPartieUnJoueur() {
		
		// Afficher l'activité PartieUnJoueur
		Intent intentPartieUnJoueur = new Intent(_contexteApplication, PartieUnJoueur.class);
		startActivity(intentPartieUnJoueur);
		// On termine cette activité
		finish();
		
	}
	
	/**
	 * Démarrer l'activité partie multijoueur locale.
	 */
	private void demarrerPartieMultijoueurLocale() {
		
		// Afficher l'activité PartieMultijoueurLocale
		Intent intentPartieMultijoueurLocale = new Intent(_contexteApplication, PartieMultijoueurLocale.class);
		startActivity(intentPartieMultijoueurLocale);
		// On termine cette activité
		finish();
		
	}
	
	/**
	 * Démarrer l'activité partie multijoueur Bluetooth.
	 */
	private void demarrerPartieMultijoueurBluetooth() {
		
		ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
		if (serviceBluetooth.estBluetoothDisponible()) {

			// Si le service Bluetooth n'est pas activé, effectuer une demande d'activation
			if (!serviceBluetooth.obtenirBluetoothAdapter().isEnabled()) {
				Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBlueTooth,ServiceBluetooth.REQUEST_CODE_ENABLE_BLUETOOTH);
			}
			else {
				this.nouvellePartieMultijoueurBluetooth();
			}

		} else {
			
			// Afficher un message d'erreur
			Toast.makeText(_contexteApplication, R.string.texte_erreur_bluetooth_indisponible, Toast.LENGTH_SHORT).show();
			
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		// S'il s'agit de la réponse à la demande d'activation du Bluetooth
		if (requestCode == ServiceBluetooth.REQUEST_CODE_ENABLE_BLUETOOTH) {

			if (resultCode == RESULT_OK) {
				// L'utilisateur a activé le bluetooth
				ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
				serviceBluetooth.obtenirBluetoothAdapter().enable();

				this.nouvellePartieMultijoueurBluetooth();
			}

		}

	}
	
	/**
	 * Démarrer une nouvelle partie multijoueur Bluetooth.
	 */
	private void nouvellePartieMultijoueurBluetooth() {
		
		// Afficher l'activité NouvellePartieMultijoueurBluetooth
		Intent intentNouvellePartieMultijoueurBluetooth = new Intent(_contexteApplication, NouvellePartieMultijoueurBluetooth.class);
		startActivity(intentNouvellePartieMultijoueurBluetooth);
		// On termine cette activité
		finish();
		
	}

}
