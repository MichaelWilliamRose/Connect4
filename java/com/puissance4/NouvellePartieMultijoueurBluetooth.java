package com.puissance4;

import com.service.ServiceBluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Classe de gestion de l'activité NouvellePartieMultijoueurBluetooth.
 */
public class NouvellePartieMultijoueurBluetooth extends Activity implements OnClickListener {

	// Contexte de l'application
	private Context _contexteApplication;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouvelle_partie_multijoueur_bluetooth);
        
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
    	
    	// On affiche l'activité NouvellePartie
		Intent intent = new Intent(this.getApplicationContext(), NouvellePartie.class);
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
    	Button button_creerPartie = (Button)findViewById(R.id._buttonCreerPartie);
    	Button button_rejoindrePartie = (Button)findViewById(R.id._buttonRejoindrePartie);
    	
    	// On initialise chaque élément
    	if (button_creerPartie != null) {
    		// On ajoute un listener sur le click du bouton
    		button_creerPartie.setOnClickListener(this);
    	}
    	
    	if (button_rejoindrePartie != null) {
    		// On ajoute un listener sur le click du bouton
    		button_rejoindrePartie.setOnClickListener(this);
    	}
    	
    }
	
    /**
     * Capter l'évènement Click sur la vue.
     */
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id._buttonCreerPartie:
			this.demarrerCreerPartie();
			break;
			
		case R.id._buttonRejoindrePartie:
			this.demarrerRejoindrePartie();
			break;
			
		default:
			break;
			
		}
		
	}
	
	/**
	 * Démarrer l'activité créer une partie.
	 */
	private void demarrerCreerPartie() {
		
		BluetoothAdapter bluetoothAdapter = ServiceBluetooth.obtenirInstance().obtenirBluetoothAdapter();
		
		// Si le périphérique n'est pas visible, demander l'autorisation de le rendre visible
		if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ServiceBluetooth.VISIBILITY_DURATION);
			startActivityForResult(discoverableIntent, ServiceBluetooth.REQUEST_CODE_ENABLE_VISIBILITY);
		} else {
			this.creerPartie();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		// S'il s'agit de la réponse à la demande d'activation de la visibilité du périphérique
		if (requestCode == ServiceBluetooth.REQUEST_CODE_ENABLE_VISIBILITY) {

			if (resultCode == ServiceBluetooth.VISIBILITY_DURATION) {
				// L'utilisateur a activé la visibilité du périphérique, on affiche l'activité PartieMultijoueurBluetooth
				this.creerPartie();
			}

		}

	}
	
	/**
	 * Créer une nouvelle partie.
	 */
	private void creerPartie() {
		
		Intent intentCreerPartie = new Intent(_contexteApplication, PartieMultijoueurBluetooth.class);
		startActivity(intentCreerPartie);
		// On termine cette activité
		finish();
		
	}
	
	/**
	 * Démarrer l'activité rejoindre une partie.
	 */
	private void demarrerRejoindrePartie() {
		
		// On affiche l'activité RejoindrePartie
		Intent intentRejoindrePartie = new Intent(_contexteApplication, RejoindrePartie.class);
		startActivity(intentRejoindrePartie);
		// On termine cette activité
		finish();
		
	}

}
