package com.puissance4;

import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.service.Peripherique;
import com.service.ServiceBluetooth;

/**
 * Classe de gestion de l'activité RejoindrePartie.
 */
public class RejoindrePartie extends ListActivity implements OnClickListener, OnItemClickListener  {
	
	// Le contexte de l'application
	private Context _contexteApplication;
	
	// La constante pour l'adresse du périphérique à envoyé à l'activité PartieBluetooth
    public static final String ADRESSE_PERIPHERIQUE = "Adresse périphérique";
    
	// Liste des périphériques à afficher
	ArrayAdapter<Peripherique> _adapter = null;
	
	// Liste affichant les périphériques
	ListView _listView = null;
	
	// BroadcastReceiver pour récupérer les périphériques visibles
	private final BroadcastReceiver _bluetoothReceiver = new BroadcastReceiver() {
		
		/**
		 * Capter la découverte d'un périphérique Bluetooth.
		 */
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		  		BluetoothDevice peripherique = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		  		Peripherique item = new Peripherique(peripherique);
		  		_adapter.add(item);
		  		// Mise à jour de la liste des périphériques
		  		setListAdapter(_adapter);
		  	}
			
		}
		
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejoindre_partie);
        
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
    	
    	// On affiche l'activité nouvelle partie
		Intent intent = new Intent(this.getApplicationContext(), NouvellePartieMultijoueurBluetooth.class);
		startActivity(intent);
		// On termine cette activité
		finish();
		
    }
    
    @Override
	protected void onDestroy() {
		
	  super.onDestroy();
	  ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
	  // Arrêter la découverte de périphérique Bluetooth
	  serviceBluetooth.obtenirBluetoothAdapter().cancelDiscovery();
	  // Désenregistrer le BroadcastReceiver
	  unregisterReceiver(_bluetoothReceiver);
	  
	}
	
	/**
	 * Initialiser l'activité.
	 */
    public void initialisation() {
    	
    	// Enregistrer le contexte de l'application
    	_contexteApplication = this.getApplicationContext();
    	
    	// Initialisation de la liste des périphériques
    	_adapter = new ArrayAdapter<Peripherique>(this, android.R.layout.simple_list_item_1);
    	
    	// Ajouter un écouteur sur le clique d'un périphérique
    	_listView = (ListView) findViewById(android.R.id.list);
    	_listView.setOnItemClickListener(this);
    	
    	// On récupère la liste des périphériques visibles
    	this.rechercherListePeripherique();
    	
    }

    /**
     * Capter l'évènement Click sur la vue.
     */
	public void onClick(View v) {
	}
	
	/**
	 * Capter l'évènement Click sur un item de la liste des périphériques.
	 */
	public void onItemClick(AdapterView<?> parent, View ligne, int position, long id) {
		
		// Récupérer le BluetoothDevice sélectionné
		Peripherique peripheriqueSelectionne = (Peripherique)_adapter.getItem(position);
		
		// On affiche l'activité nouvelle partie
		Intent intent = new Intent(_contexteApplication, PartieMultijoueurBluetooth.class);
		
		// On passe l'adresse MAC du périphérique en paramètre à l'activité PartieMultijoueurBluetooth 
		intent.putExtra(ADRESSE_PERIPHERIQUE, peripheriqueSelectionne.obtenirPeripherique().getAddress());
		startActivity(intent);

		// On termine cette activité
		finish();
		
	}
	
	/**
	 * Rechercher des nouveaux périphériques.
	 */
	private void rechercherListePeripherique() {
		
		// Enregistrement du broadcast
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(_bluetoothReceiver, filter);
		
		// Démarrer la découverte de périphériques
		ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
		serviceBluetooth.obtenirBluetoothAdapter().startDiscovery();
		
	}
	
}
