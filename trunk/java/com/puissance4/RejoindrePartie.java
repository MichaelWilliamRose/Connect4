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
 * Classe de gestion de l'activit� RejoindrePartie.
 */
public class RejoindrePartie extends ListActivity implements OnClickListener, OnItemClickListener  {
	
	// Le contexte de l'application
	private Context _contexteApplication;
	
	// La constante pour l'adresse du p�riph�rique � envoy� � l'activit� PartieBluetooth
    public static final String ADRESSE_PERIPHERIQUE = "Adresse p�riph�rique";
    
	// Liste des p�riph�riques � afficher
	ArrayAdapter<Peripherique> _adapter = null;
	
	// Liste affichant les p�riph�riques
	ListView _listView = null;
	
	// BroadcastReceiver pour r�cup�rer les p�riph�riques visibles
	private final BroadcastReceiver _bluetoothReceiver = new BroadcastReceiver() {
		
		/**
		 * Capter la d�couverte d'un p�riph�rique Bluetooth.
		 */
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		  		BluetoothDevice peripherique = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		  		Peripherique item = new Peripherique(peripherique);
		  		_adapter.add(item);
		  		// Mise � jour de la liste des p�riph�riques
		  		setListAdapter(_adapter);
		  	}
			
		}
		
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejoindre_partie);
        
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
    	
    	// On affiche l'activit� nouvelle partie
		Intent intent = new Intent(this.getApplicationContext(), NouvellePartieMultijoueurBluetooth.class);
		startActivity(intent);
		// On termine cette activit�
		finish();
		
    }
    
    @Override
	protected void onDestroy() {
		
	  super.onDestroy();
	  ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
	  // Arr�ter la d�couverte de p�riph�rique Bluetooth
	  serviceBluetooth.obtenirBluetoothAdapter().cancelDiscovery();
	  // D�senregistrer le BroadcastReceiver
	  unregisterReceiver(_bluetoothReceiver);
	  
	}
	
	/**
	 * Initialiser l'activit�.
	 */
    public void initialisation() {
    	
    	// Enregistrer le contexte de l'application
    	_contexteApplication = this.getApplicationContext();
    	
    	// Initialisation de la liste des p�riph�riques
    	_adapter = new ArrayAdapter<Peripherique>(this, android.R.layout.simple_list_item_1);
    	
    	// Ajouter un �couteur sur le clique d'un p�riph�rique
    	_listView = (ListView) findViewById(android.R.id.list);
    	_listView.setOnItemClickListener(this);
    	
    	// On r�cup�re la liste des p�riph�riques visibles
    	this.rechercherListePeripherique();
    	
    }

    /**
     * Capter l'�v�nement Click sur la vue.
     */
	public void onClick(View v) {
	}
	
	/**
	 * Capter l'�v�nement Click sur un item de la liste des p�riph�riques.
	 */
	public void onItemClick(AdapterView<?> parent, View ligne, int position, long id) {
		
		// R�cup�rer le BluetoothDevice s�lectionn�
		Peripherique peripheriqueSelectionne = (Peripherique)_adapter.getItem(position);
		
		// On affiche l'activit� nouvelle partie
		Intent intent = new Intent(_contexteApplication, PartieMultijoueurBluetooth.class);
		
		// On passe l'adresse MAC du p�riph�rique en param�tre � l'activit� PartieMultijoueurBluetooth 
		intent.putExtra(ADRESSE_PERIPHERIQUE, peripheriqueSelectionne.obtenirPeripherique().getAddress());
		startActivity(intent);

		// On termine cette activit�
		finish();
		
	}
	
	/**
	 * Rechercher des nouveaux p�riph�riques.
	 */
	private void rechercherListePeripherique() {
		
		// Enregistrement du broadcast
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(_bluetoothReceiver, filter);
		
		// D�marrer la d�couverte de p�riph�riques
		ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
		serviceBluetooth.obtenirBluetoothAdapter().startDiscovery();
		
	}
	
}
