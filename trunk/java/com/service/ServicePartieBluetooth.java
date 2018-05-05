package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.puissance4.PartieMultijoueurBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Classe permettant la gestion d'une connexion Bluetooth entre deux p�riph�riques.
 * Cette classe est g�n�rique est peut �tre r�utilis�e pour un autre projet.
 */
public class ServicePartieBluetooth {
	
	// Variables utilis�es pour le d�bogage
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Noms des enregistrements SDP lors de la cr�ation du socket du serveur
    private static final String NOM_SECURISE = "Puissance 4 s�curis�";
    private static final String NOM_NON_SECURISE = "Puissance 4 non s�curis�";

    // UUID uniques pour cette application
    private static final UUID UUID_SECURISE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_NON_SECURISE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // Variables membres
    private final BluetoothAdapter _bluetoothAdapter;
    private final Handler _handler;
    private ServeurBluetooth _threadServeurSecurise;
    private ServeurBluetooth _threadServeurNonSecurise;
    private ClientBluetooth _threadClient;
    private ConnexionBluetooth _threadConnexion;
    private int _etatConnexion;

    // Constantes indiquant l'�tat courant de la connexion
    // Rien ne se passe 
    public static final int ETAT_INDEFINI = 0;
    // A l'�coute d'une nouvelle connexion
    public static final int ETAT_ATTENTE_NOUVELLE_CONNEXION = 1;
    // Initialisation d'une connexion exterieure
    public static final int ETAT_CONNEXION_INITIALISATION = 2;
    // Connexion �tablie avec un p�riph�rique
    public static final int ETAT_CONNEXION_ETABLIE = 3;

    /**
     * Constructeur
     * @param context
     * @param handler
     */
    public ServicePartieBluetooth(Context context, Handler handler) {
    	
    	ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
    	
        _bluetoothAdapter = serviceBluetooth.obtenirBluetoothAdapter();
        _etatConnexion = ETAT_INDEFINI;
        _handler = handler;
        
    }
    
    /**
     * D�finir l'�tat courant de la connexion Bluetooth.
     * @param etat
     */
    private synchronized void definirEtatConnexion(int etat) {
    	
        if (D) Log.d(TAG, "setState() " + _etatConnexion + " -> " + etat);
        _etatConnexion = etat;

        // Donner le nouvel �tat � l'Handler pour que l'activit� PartieMultijoueurBluetooth puisse se mettre � jour
        _handler.obtainMessage(PartieMultijoueurBluetooth.CHANGEMENT_ETAT_CONNEXION, etat, -1).sendToTarget();
        
    }

    /**
     * Obtenir l'�tat courant de la connexion Bluetooth.
     */
    public synchronized int getState() {
        return _etatConnexion;
    }

    /**
     * D�marrer le serveur Bluetooth.
     */
    public synchronized void demarrerServeurBluetooth() {
    	
        if (D) Log.d(TAG, "start");

        // Fermer tous les thread en attente de connexion
        if (_threadClient != null) {
        	_threadClient.arreter(); 
        	_threadClient = null;
        }

        // Fermer tous les thread actuellement connect�s
        if (_threadConnexion != null) {
        	_threadConnexion.arreter(); 
        	_threadConnexion = null;
        }

        definirEtatConnexion(ETAT_ATTENTE_NOUVELLE_CONNEXION);

        // D�marrer le thread serveur afin d'�couter sur un BluetoothServerSocket 
        if (_threadServeurSecurise == null) {
            _threadServeurSecurise = new ServeurBluetooth(true);
            _threadServeurSecurise.start();
        }
        
        if (_threadServeurNonSecurise == null) {
            _threadServeurNonSecurise = new ServeurBluetooth(false);
            _threadServeurNonSecurise.start();
        }
        
    }

    /**
     * D�marrer le client Bluetooth.
     * @param peripherique
     * @param securise
     */
    public synchronized void demarrerClientBluetooth(BluetoothDevice peripherique, boolean securise) {
    	
        if (D) Log.d(TAG, "connect� �: " + peripherique);

        // Fermer tous les thread en attente de connexion
        if (_etatConnexion == ETAT_CONNEXION_INITIALISATION) {
            
        	if (_threadClient != null) {
        		_threadClient.arreter(); 
        		_threadClient = null;
        	}
        
        }

        // Fermer tous les thread actuellement connect�s
        if (_threadConnexion != null) {
        	_threadConnexion.arreter(); 
        	_threadConnexion = null;
        }

        // D�marrer le thread client afin de se connect� avec le p�riph�rique s�lectionn�
        _threadClient = new ClientBluetooth(peripherique, securise);
        _threadClient.start();
        definirEtatConnexion(ETAT_CONNEXION_INITIALISATION);
    
    }

    /**
     * D�marrer la connexion Bluetooth (thread connect� permettant la gestion de la connexion).
     * @param socket
     * @param peripherique
     */
    public synchronized void connexion(BluetoothSocket socket, BluetoothDevice peripherique, final String typeSocket) {
        
    	if (D) Log.d(TAG, "connexion, Type de socket:" + typeSocket);

    	// Fermer le thread qui a permis l'�tablissement de la connexion 
        if (_threadClient != null) {
        	_threadClient.arreter();
        	_threadClient = null;
        }

        // Fermer tous les thread actuellement connect�s
        if (_threadConnexion != null) {
        	_threadConnexion.arreter();
        	_threadConnexion = null;
        }

        // Fermer le thread serveur car on ne veut qu'une seule connexion (une seule p�riph�rique connect�)
        if (_threadServeurSecurise != null) {
            _threadServeurSecurise.arreter();
            _threadServeurSecurise = null;
        }
        if (_threadServeurNonSecurise != null) {
            _threadServeurNonSecurise.arreter();
            _threadServeurNonSecurise = null;
        }

        // D�marrer le thread connect� afin de g�rer la connexion et les transferts de donn�es
        _threadConnexion = new ConnexionBluetooth(socket, typeSocket);
        _threadConnexion.start();

        // Envoyer le nom du p�riph�rique connect� � l'activit� PartieMultijoueurBluetooth
        Message message = _handler.obtainMessage(PartieMultijoueurBluetooth.CONENXION_REUSSIE);
        Bundle bundle = new Bundle();
        bundle.putString(PartieMultijoueurBluetooth.NOM_PERIPHERIQUE, peripherique.getName());
        message.setData(bundle);
        _handler.sendMessage(message);

        definirEtatConnexion(ETAT_CONNEXION_ETABLIE);
        
    }

    /**
     * Arr�ter tous les threads.
     */
    public synchronized void stop() {
    	
        if (D) Log.d(TAG, "stop");

        if (_threadClient != null) {
            _threadClient.arreter();
            _threadClient = null;
        }

        if (_threadConnexion != null) {
            _threadConnexion.arreter();
            _threadConnexion = null;
        }

        if (_threadServeurSecurise != null) {
            _threadServeurSecurise.arreter();
            _threadServeurSecurise = null;
        }

        if (_threadServeurNonSecurise != null) {
            _threadServeurNonSecurise.arreter();
            _threadServeurNonSecurise = null;
        }
        
        definirEtatConnexion(ETAT_INDEFINI);
        
    }

    /**
     * Ecrire au thread connect� de fa�on asynchrone. 
     * @param donnees
     * @see ConnexionBluetooth#ecrire(byte[])
     */
    public void ecrire(byte[] donnees) {
    	
        // Cr�er un objet temporaire
        ConnexionBluetooth threadConnexionTemporaire;
        
        // Synchroniser une copie du thread connexion
        synchronized (this) {
            
        	if (_etatConnexion != ETAT_CONNEXION_ETABLIE) 
            	return;
            
        	threadConnexionTemporaire = _threadConnexion;
        
        }
        
        // Effectuer l'�criture asynchrone
        threadConnexionTemporaire.ecrire(donnees);
        
    }
    
    /**
     * Indiquer que la tentative de connexion a �chou� et notifier l'activit� PartieBluetooth.
     */
    private void connexionErreur() {
    	
    	// Envoyer un message d'erreur � l'activit� PartieBluetooth
        Message message = _handler.obtainMessage(PartieMultijoueurBluetooth.MESSAGE_TOAST);
        
        Bundle bundle = new Bundle();
        bundle.putString(PartieMultijoueurBluetooth.TOAST, "Impossible de connecter le p�riph�rique");
        
        message.setData(bundle);
        _handler.sendMessage(message);

        // D�marrer le service dans le but de relancer le mode d'�coute du serveur Bluetooth
        ServicePartieBluetooth.this.demarrerServeurBluetooth();
    
    }

    /**
     * Indiquer que la connexion a �t� perdu et notifier l'activit� PartieBluetooth.
     */
    private void connexionPerdue() {
    	
    	// Envoyer un message d'erreur � l'activit� PartieBluetooth
        Message message = _handler.obtainMessage(PartieMultijoueurBluetooth.MESSAGE_TOAST);
        
        Bundle bundle = new Bundle();
        bundle.putString(PartieMultijoueurBluetooth.TOAST, "Connexion au p�riph�rique perdue");
        
        message.setData(bundle);
        _handler.sendMessage(message);

        // D�marrer le service dans le but de relancer le mode d'�coute du serveur Bluetooth
        ServicePartieBluetooth.this.demarrerServeurBluetooth();
        
    }

    /**
     * Classe repr�sentant le serveur Bluetooth.
     */
    private class ServeurBluetooth extends Thread {
    	
    	// Le serveur de socket
        private final BluetoothServerSocket _bluetoothServerSocket;
        private String _typeSocket;

        /**
         * Constructeur.
         * @param securise
         */
        public ServeurBluetooth(boolean securise) {
        	
            BluetoothServerSocket bluetoothServerSocketTemporaire = null;
            _typeSocket = securise ? "Securis�" : "Non s�curis�";

            // Cr�ation d'un nouveau serveur de socket (� l'�coute d'une nouvelle connexion)
            try {
            	
                if (securise) {
                    bluetoothServerSocketTemporaire = _bluetoothAdapter.listenUsingRfcommWithServiceRecord(NOM_SECURISE, UUID_SECURISE);
                } else {
                    bluetoothServerSocketTemporaire = _bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NOM_NON_SECURISE, UUID_NON_SECURISE);
                }
                
            } catch (IOException e) {
                
            	Log.e(TAG, "Type de socket: " + _typeSocket + "listen() a �chou�", e);
            
            }
            
            _bluetoothServerSocket = bluetoothServerSocketTemporaire;
            
        }

        /**
         * D�marrer le serveur Bluetooth.
         */
        public void run() {
        	
            if (D) Log.d(TAG, "Type de socket: " + _typeSocket + "DEBUT _threadServeur" + this);
            setName("ServeurBluetooth" + _typeSocket);

            BluetoothSocket socket = null;

            // Ecouter le serveur de socket si aucune connexion n'est �tablie
            while (_etatConnexion != ETAT_CONNEXION_ETABLIE) {
            	
                try {
                	// Appel bloquant, ne s'arr�te que lors d'une connexion r�ussie ou d'une erreur
                    socket = _bluetoothServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Type de socket: " + _typeSocket + "accept() a �chou�", e);
                    break;
                }

                // Si une connexion a �t� accept�e
                if (socket != null) {
                    
                	synchronized (ServicePartieBluetooth.this) {
                        
                		switch (_etatConnexion) {
                        
                		case ETAT_ATTENTE_NOUVELLE_CONNEXION:
                        
                		case ETAT_CONNEXION_INITIALISATION:
                			// Situation normale, d�marrer le thread de connexion
                            connexion(socket, socket.getRemoteDevice(), _typeSocket);
                            break;
                        
                		case ETAT_INDEFINI:
                        
                		case ETAT_CONNEXION_ETABLIE:
                			// Fermer le socket s'il n'est pas pr�t ou d�j� connect�
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Impossible de fermer le socket ind�sirable", e);
                            }
                            break;
                        
                		}
                    
                	}
                	
                }
                
            }
            
            if (D) Log.i(TAG, "FIN _threadServeur, Type de socket: " + _typeSocket);

        }

        /**
         * Arr�ter le serveur Bluetooth.
         */
        public void arreter() {
        	
            if (D) Log.d(TAG, "Socket Type" + _typeSocket + "arr�ter " + this);
            
            try {
                _bluetoothServerSocket.close();
            } 
            catch (IOException e) {
                Log.e(TAG, "Type de socket: " + _typeSocket + "close() du serveur a �chou�", e);
            }
        
        }
        
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ClientBluetooth extends Thread {
    	
        private final BluetoothSocket _bluetoothSocket;
        private final BluetoothDevice _bluetoothDevice;
        private String _typeSocket;

        public ClientBluetooth(BluetoothDevice peripherique, boolean securise) {
            
        	_bluetoothDevice = peripherique;
            BluetoothSocket bluetoothSocketTemporaire = null;
            _typeSocket = securise ? "Secure" : "Insecure";

            // Obtenir le BluetoothSocket pour une demande de connexion avec le p�riph�rique s�lectionn�
            try {
             
            	if (securise) {
                    bluetoothSocketTemporaire = peripherique.createRfcommSocketToServiceRecord(UUID_SECURISE);
                } else {
                    bluetoothSocketTemporaire = peripherique.createInsecureRfcommSocketToServiceRecord(UUID_NON_SECURISE);
                }
            	
            } catch (IOException e) {
                
            	Log.e(TAG, "Type de socket: " + _typeSocket + "create() �chou�", e);
            
            }
            
            _bluetoothSocket = bluetoothSocketTemporaire;
            
        }

        /**
         * D�marrer le client Bluetooth.
         */
        public void run() {
        	
            Log.i(TAG, "DEBUT _threadClient, type de socket:" + _typeSocket);
            setName("Connexion" + _typeSocket);

            // Toujours arr�ter la d�couverte de p�riph�rique car cela ralenti la vitesse de connexion
            _bluetoothAdapter.cancelDiscovery();

            // D�marrer une demande de connexion au BluetoothSocket
            try {
            	
            	// Appel bloquant, ne s'arr�te que lors d'une connexion r�ussie ou d'une erreur
                _bluetoothSocket.connect();
            
            } catch (IOException e) {
            	
            	// Arr�ter le socket
                try {
                    _bluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Impossible de fermer le socket " + _typeSocket + " lors d'une erreur de connexion", e2);
                }
                
                connexionErreur();
                return;
                
            }

            // R�initialiser le thread de connexion car la connexion a r�ussi 
            synchronized (ServicePartieBluetooth.this) {
                _threadClient = null;
            }

            // D�marrer le thread de connexion
            connexion(_bluetoothSocket, _bluetoothDevice, _typeSocket);
            
        }

        /**
         * Arr�ter la connexion.
         */
        public void arreter() {
        	
            try {
                _bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() du socket de connexion " + _typeSocket + " a �chou�", e);
            }
        
        }
    }

    /**
     * Ce thread est ex�cut� durant la connexion avec un p�riph�rique.
     * Il permet de transf�rer les donn�es entre les p�riph�riques.
     */
    private class ConnexionBluetooth extends Thread {
    	
        private final BluetoothSocket _bluetoothSocket;
        private final InputStream _donneesEntree;
        private final OutputStream _donneesSortie;

        /**
         * Constructeur.
         * @param socket
         * @param typeSocket
         */
        public ConnexionBluetooth(BluetoothSocket socket, String typeSocket) {
        	
            Log.d(TAG, "Cr�ation ConnexionBluetooth: " + typeSocket);
            _bluetoothSocket = socket;
            InputStream donneesEntreeTemporaire = null;
            OutputStream donneesSortieTemporaire = null;

            // Obtenir les donn�es entrantes et sortantes du BluetoothSocket
            try {
                donneesEntreeTemporaire = socket.getInputStream();
                donneesSortieTemporaire = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "donn�es temporaires du socket non cr��es", e);
            }

            _donneesEntree = donneesEntreeTemporaire;
            _donneesSortie = donneesSortieTemporaire;
            
        }

        /**
         * D�marrer la connexion Bluetooth.
         */
        public void run() {
        	
            Log.i(TAG, "DEBUT _threadConnexion");
            byte[] buffer = new byte[1024];
            int bytes;

            // Rester � l'�coute des donn�es entrantes durant la connexion
            while (true) {
                
            	try {
                	
                	// Lire les donn�es entrantes depuis la InputStream
                    bytes = _donneesEntree.read(buffer);

                    // Envoyer les donn�es r�cup�r�es � l'activit� PartieMultijoueurBluetooth
                    _handler.obtainMessage(PartieMultijoueurBluetooth.DONNEES_LECTURE, bytes, -1, buffer).sendToTarget();
                    
                } catch (IOException e) {
                    
                	Log.e(TAG, "d�connect�", e);
                    connexionPerdue();
                    
                    // D�marrer le service dans le but de relancer le mode d'�coute du serveur Bluetooth
                    ServicePartieBluetooth.this.demarrerServeurBluetooth();
                    break;
                
                }
            	
            }
            
        }

        /**
         * Ecrire dans la cha�ne de sortie.
         * @param tampon
         */
        public void ecrire(byte[] tampon) {
        	
            try {
                
            	_donneesSortie.write(tampon);

            	// Partager le message envoy� � l'activit� PartieBluetooth
                _handler.obtainMessage(PartieMultijoueurBluetooth.DONNEES_ECRITURE, -1, -1, tampon).sendToTarget();
                
            } catch (IOException e) {
                
            	Log.e(TAG, "Exception during write", e);
            
            }
            
        }

        /**
         * Arr�ter le thread connect�.
         */
        public void arreter() {
        	
            try {
                _bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() du socket connect� �chou�", e);
            }
        }
        
    }
    
}
