package com.service;

import java.util.Set;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;

/**
 * Classe de service pour les fonctionnalités Bluetooth.
 */
public class ServiceBluetooth {

	// Instance du singleton.
	private static ServiceBluetooth _instance = null;
	
	// BluetoothAdapter du périphérique
	private BluetoothAdapter _bluetoothAdapter = null;
	
	// Code de retour pour la demande d'activation du Bluetooth
	public final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
	
	// Code de retour pour la demande d'activation de la visibilité du périphérique
	public final static int REQUEST_CODE_ENABLE_VISIBILITY = 1;
	
	// Durée de visibilité du périphérique
	public final static int VISIBILITY_DURATION = 300;
	
	// BroadcastReceiver pour avertir la détection de nouveaux périphériques
	private BroadcastReceiver _bluetoothReceiver = null;
	
	// Identifiant unique utilisé par les fonctionnalités Bluetooth
	private final UUID _uuid = UUID.randomUUID();
	
	/**
	 * Constructeur.
	 */
	private ServiceBluetooth() {
		this.initialiserBluetooth();
	}
	
	/**
	 * Singleton.
	 * @return
	 */
	public static ServiceBluetooth obtenirInstance() {
		
		if (_instance == null) {
			_instance = new ServiceBluetooth();
		}
		
		return _instance;
		
	}
	
	/**
	 * Obtenir le BluetoothAdapter.
	 * @return
	 */
	public BluetoothAdapter obtenirBluetoothAdapter() {
		return _bluetoothAdapter;
	}
	
	/**
	 * Définir le BluetoothAdapter.
	 * @param bluetoothAdapter
	 */
	public void definirBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
		_bluetoothAdapter = bluetoothAdapter;
	}
	
	/**
	 * Obltenir le BroadcastReceiver.
	 * @return
	 */
	public BroadcastReceiver obtenirBluetoothReceiver() {
		return _bluetoothReceiver;
	}
	
	/**
	 * Définir le BroadcastReceiver.
	 * @param bluetoothReceiver
	 */
	public void definirBluetoothReceiver(BroadcastReceiver bluetoothReceiver) {
		_bluetoothReceiver = bluetoothReceiver;
	}
	
	/**
	 * Initialiser le Bluetooth.
	 */
	private void initialiserBluetooth() {
		
		// Initialisation du BluetoothAdapter 
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null)
			_bluetoothAdapter = bluetoothAdapter;
		
	}
	
	/**
	 * Obtenir l'UUID.
	 * @return
	 */
	public UUID obtenirUUID() {
		return _uuid;
	}
	
	/**
	 * Permet de savoir si la technologie Bluetooth est présente sur le périphérique.
	 * @return
	 */
	public Boolean estBluetoothDisponible() {
		
		Boolean estDisponible = true;
		
		if (_bluetoothAdapter == null) {
			estDisponible = false;
		}
		
		return estDisponible;
		
	}
	
	/**
	 * Obtenir la liste des périphériques disponibles.
	 * @return
	 */
	public Set<BluetoothDevice> obtenirListePeripheriques() {
		
		Set<BluetoothDevice> liste = _bluetoothAdapter.getBondedDevices();
		return liste;
		
	}
	
}
