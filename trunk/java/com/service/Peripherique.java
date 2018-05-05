package com.service;

import android.bluetooth.BluetoothDevice;

/**
 * Classe représentant un périphérique Bluetooth (utilisé pour l'affichage de la liste des périphériques).
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
	 * Obtenir le périphérique.
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
