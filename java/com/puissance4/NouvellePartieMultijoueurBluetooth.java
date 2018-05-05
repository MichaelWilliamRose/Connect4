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
 * Classe de gestion de l'activit� NouvellePartieMultijoueurBluetooth.
 */
public class NouvellePartieMultijoueurBluetooth extends Activity implements OnClickListener {

	// Contexte de l'application
	private Context _contexteApplication;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouvelle_partie_multijoueur_bluetooth);
        
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
    	
    	// On affiche l'activit� NouvellePartie
		Intent intent = new Intent(this.getApplicationContext(), NouvellePartie.class);
		startActivity(intent);
		// On termine cette activit�
		finish();
		
    }
    
    /**
     * Fonction d'initialisation au d�marrage de l'application.
     */
    public void initialisation() {
    	
    	// Sauvegarde du contexte de l'application
        _contexteApplication = this.getApplicationContext();
    	
    	// On acc�de aux �l�ments de l'activit�
    	Button button_creerPartie = (Button)findViewById(R.id._buttonCreerPartie);
    	Button button_rejoindrePartie = (Button)findViewById(R.id._buttonRejoindrePartie);
    	
    	// On initialise chaque �l�ment
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
     * Capter l'�v�nement Click sur la vue.
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
	 * D�marrer l'activit� cr�er une partie.
	 */
	private void demarrerCreerPartie() {
		
		BluetoothAdapter bluetoothAdapter = ServiceBluetooth.obtenirInstance().obtenirBluetoothAdapter();
		
		// Si le p�riph�rique n'est pas visible, demander l'autorisation de le rendre visible
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

		// S'il s'agit de la r�ponse � la demande d'activation de la visibilit� du p�riph�rique
		if (requestCode == ServiceBluetooth.REQUEST_CODE_ENABLE_VISIBILITY) {

			if (resultCode == ServiceBluetooth.VISIBILITY_DURATION) {
				// L'utilisateur a activ� la visibilit� du p�riph�rique, on affiche l'activit� PartieMultijoueurBluetooth
				this.creerPartie();
			}

		}

	}
	
	/**
	 * Cr�er une nouvelle partie.
	 */
	private void creerPartie() {
		
		Intent intentCreerPartie = new Intent(_contexteApplication, PartieMultijoueurBluetooth.class);
		startActivity(intentCreerPartie);
		// On termine cette activit�
		finish();
		
	}
	
	/**
	 * D�marrer l'activit� rejoindre une partie.
	 */
	private void demarrerRejoindrePartie() {
		
		// On affiche l'activit� RejoindrePartie
		Intent intentRejoindrePartie = new Intent(_contexteApplication, RejoindrePartie.class);
		startActivity(intentRejoindrePartie);
		// On termine cette activit�
		finish();
		
	}

}
