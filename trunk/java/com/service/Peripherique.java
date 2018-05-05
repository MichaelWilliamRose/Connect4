package com.service;

import android.bluetooth.BluetoothDevice;

/**
 * Classe repr�sentant un p�riph�rique Bluetooth (utilis� pour l'affichage de la liste des p�riph�riques).
 */
public class Peripherique {
	
	private BluetoothDevice _bluetoothDevice = null;
	
	/**
	 * Constructeur.
	 */
	public Peripherique(BluetoothDevice bluetoothDevice) {
		_bluetoothDevice = bluetoothDevice;
	}
	
	/**
	 * Obtenir le p�riph�rique.
	 * @return
	 */
	public BluetoothDevice obtenirPeripherique() {
		return _bluetoothDevice;
	}
	
	@Override
	public String toString() {
		return _bluetoothDevice.getName();
	}

}
