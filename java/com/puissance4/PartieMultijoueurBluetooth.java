package com.puissance4;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.service.ServiceBluetooth;
import com.service.ServicePartieBluetooth;

/**
 * Classe de gestion de l'activit� PartieMultijoueurBluetooth.
 */
public class PartieMultijoueurBluetooth extends Activity implements OnClickListener, AnimationListener {
	
	// Le contexte de l'application
	private static Context _contexteApplication = null;
	
	// Le service permettant de jouer une partie Bluetooth
	private ServicePartieBluetooth _servicePartieBluetooth = null;
	
	// Le Handler qui r�cup�re les informations en retour du ServicePartieBluetooth 
    // et permettant de mettre � jour l'activit� lorsqu'un sous-thread a termin� son travail
    private HandlerActionJoueur _handler = new HandlerActionJoueur(this);
	
	// La liste des jetons
	private static ArrayList<Integer> _listeJeton = null;
	
	// Grille permettant de m�moriser le choix de l'utilisateur
	private static SparseIntArray _grille = null;
	
	// La couleur du premier jeton pos� (0 = noir, 1 = jaune, 2 = rouge)
	private static int _couleurJeton = 0;
	
	// Cache d'attente utilis� pour avertir l'attente de connexion de la part d'un autre joueur
	private static LinearLayout _layoutAttentePartenaire = null;
	
	// Barre de progression utilis� pour avertir de l'attente d'une action de la part de l'autre joueur
	private static ProgressBar _progressBarAttentePartenaire = null;
	
	// Cache d'attente utilis� pour avertir l'attente d'une action de la part de l'autre joueur
	private static LinearLayout _layoutFinPartie = null;
	
	// Bool�an permettant de savoir si � la fin d'une partie, il y a �galit�
	private Boolean _egalite = false;
	
	// Jeton d'animation
	private ImageView _jetonAnimation;
	
	// Case libre o� le jeton du joueur actuel peut �tre pos�
	private ImageView _caseLibre;
	
	// L'index de la case libre o� le jeton du joueur actuel peut �tre pos�
	private int _indexCaseLibre;
	
	// L'index de la colonne de la case libre o� le jeton du joueur actuel peut �tre pos�
	private int _indexColonneCaseLibre;
	
	// Nom du joueur 1
	private String _nomJoueur1;
	
	// Nom du joueur 2
	private static String _nomJoueur2;
	
	// Variables permettant de savoir de quel p�riph�rique on traite (�metteur ou receveur)
	private static String _typePeripherique;
	private static final String EMETTEUR = "Emetteur";
	private static final String RECEVEUR = "Receveur";

	// Variables utilis�es pour le d�bogage
    private static final String TAG = "Puissance4_Bluetooth";
    private static final boolean D = true;
	
    // Les diff�rents types de messages envoy�s depuis le Handler du ServicePartieBluetooth
    public static final int CHANGEMENT_ETAT_CONNEXION = 1;
    public static final int DONNEES_LECTURE = 2;
    public static final int DONNEES_ECRITURE = 3;
    public static final int CONENXION_REUSSIE = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int REJOUER_PARTIE = -1;

    // Noms cl�s re�us depuis le Handler du ServicePartieBluetooth
    public static final String NOM_PERIPHERIQUE = "device_name";
    public static final String TOAST = "toast";
	
    /**
     * Classe repr�sentant le Handler des actions des joueurs.
     */
    private static class HandlerActionJoueur extends Handler {
    	
    	// WeakReference n�cessaire afin de r�sourdre le warning :
    	// Android Handler � The handler class should be static or leak might occur
    	// Voir : http://ucla.jamesyxu.com/?p=285
    	// Explications: Sans la WeakReference vers l'activit� PartieMultijoueurBluetooth, la classe Handler va
    	//               automatiquement garder une copie de l'activit� PartieMultijoueurBluetooth. Cette copie
    	//               ne sera pas traitable par le GarbageCollector ce qui emp�chera la lib�ration totale des
    	//               ressources de l'activit�.
    	private WeakReference<PartieMultijoueurBluetooth> _reference;
    	
    	/**
    	 * Constructeur.
    	 * @param reference
    	 */
    	public HandlerActionJoueur(PartieMultijoueurBluetooth reference) {
    		_reference = new WeakReference<PartieMultijoueurBluetooth>(reference);
    	}
    	
    	@Override
	    public void handleMessage(Message msg) {
    		
    		PartieMultijoueurBluetooth partieMultijoueurBluetooth = _reference.get();
            
        	switch (msg.what) {
            
        	case CHANGEMENT_ETAT_CONNEXION:
        		
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                
                switch (msg.arg1) {
                case ServicePartieBluetooth.ETAT_CONNEXION_ETABLIE:
                    //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    //mConversationArrayAdapter.clear();
                    break;
                case ServicePartieBluetooth.ETAT_CONNEXION_INITIALISATION:
                    //setStatus(R.string.title_connecting);
                    break;
                case ServicePartieBluetooth.ETAT_ATTENTE_NOUVELLE_CONNEXION:
                case ServicePartieBluetooth.ETAT_INDEFINI:
                    //setStatus(R.string.title_not_connected);
                    break;
                }
                
                break;
        	
        	case DONNEES_ECRITURE:
        		
        		byte[] tamponEcriture = (byte[]) msg.obj;
                String donneesEcriture = new String(tamponEcriture);
                int numeroDonneesEcriture = Integer.parseInt(donneesEcriture);
                
                if (numeroDonneesEcriture == REJOUER_PARTIE) {
                	partieMultijoueurBluetooth.rejouerPartie();
                } else {
                	_typePeripherique = EMETTEUR;
                	// Afficher le jeton sur la grille
                    partieMultijoueurBluetooth.poserJeton(numeroDonneesEcriture);
                }
        		
                break;
            
        	case DONNEES_LECTURE:
        		
        		byte[] tamponLecture = (byte[]) msg.obj;
                String donneesLecture = new String(tamponLecture, 0, msg.arg1);
                int numeroDonneesLecture = Integer.parseInt(donneesLecture);
                
                if (numeroDonneesLecture == REJOUER_PARTIE) {
                	partieMultijoueurBluetooth.rejouerPartie();
                } else {
                	_typePeripherique = RECEVEUR;
                	// Afficher le jeton sur la grille
                	partieMultijoueurBluetooth.poserJeton(numeroDonneesLecture);
                }
        		
                break;
            
        	case CONENXION_REUSSIE:
        		
            	Toast.makeText(_contexteApplication, "D�but de la partie !", Toast.LENGTH_SHORT).show();
            	
            	// Enlever le cache d'attente une fois que la partie a commenc�
        		_layoutAttentePartenaire.setVisibility(View.GONE);
        		
        		// Activer tous les jetons
        		for (Integer item : _listeJeton) {
        			ImageView image = (ImageView) partieMultijoueurBluetooth.findViewById(item);
        			image.setEnabled(true);
        		}
        		
                break;
                
        	case MESSAGE_TOAST:
                
        		Toast.makeText(_contexteApplication, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            
        	}
    		
	    }
    	
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.partie_multijoueur_bluetooth);
		
		// Initialiser l'activit�
		this.initialisation();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Pas de menu pour l'instant
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		
		// Fermeture de tous les Threads
		_servicePartieBluetooth.stop();
		
		// On affiche l'activit� nouvelle partie
		Intent intent = new Intent(this.getApplicationContext(), NouvellePartieMultijoueurBluetooth.class);
		startActivity(intent);
		
		// On termine cette activit�
		finish();
		
	}

	/**
	 * Initialiser l'activit�.
	 */
	public void initialisation() {
		
		// Sauvegarde du contexte de l'application
		_contexteApplication = this.getApplicationContext();
		
		// D�marrer le serveur Bluetooth
		_servicePartieBluetooth = new ServicePartieBluetooth(this, _handler);
		_servicePartieBluetooth.demarrerServeurBluetooth();
		
		// Bloquer l'orientation de l'activit� en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Initialiser le jeton du joueur qui commece la partie
		_couleurJeton = 1;
		
		// La liste des jetons
		_listeJeton = new ArrayList<Integer>();

		// Enregisgtrement de tous les jetons dans un tableau
		_listeJeton.add(R.id._imageView1);
		_listeJeton.add(R.id._imageView2);
		_listeJeton.add(R.id._imageView3);
		_listeJeton.add(R.id._imageView4);
		_listeJeton.add(R.id._imageView5);
		_listeJeton.add(R.id._imageView6);
		_listeJeton.add(R.id._imageView7);
		_listeJeton.add(R.id._imageView8);
		_listeJeton.add(R.id._imageView9);
		_listeJeton.add(R.id._imageView10);
		_listeJeton.add(R.id._imageView11);
		_listeJeton.add(R.id._imageView12);
		_listeJeton.add(R.id._imageView13);
		_listeJeton.add(R.id._imageView14);
		_listeJeton.add(R.id._imageView15);
		_listeJeton.add(R.id._imageView16);
		_listeJeton.add(R.id._imageView17);
		_listeJeton.add(R.id._imageView18);
		_listeJeton.add(R.id._imageView19);
		_listeJeton.add(R.id._imageView20);
		_listeJeton.add(R.id._imageView21);
		_listeJeton.add(R.id._imageView22);
		_listeJeton.add(R.id._imageView23);
		_listeJeton.add(R.id._imageView24);
		_listeJeton.add(R.id._imageView25);
		_listeJeton.add(R.id._imageView26);
		_listeJeton.add(R.id._imageView27);
		_listeJeton.add(R.id._imageView28);
		_listeJeton.add(R.id._imageView29);
		_listeJeton.add(R.id._imageView30);
		_listeJeton.add(R.id._imageView31);
		_listeJeton.add(R.id._imageView32);
		_listeJeton.add(R.id._imageView33);
		_listeJeton.add(R.id._imageView34);
		_listeJeton.add(R.id._imageView35);
		_listeJeton.add(R.id._imageView36);
		_listeJeton.add(R.id._imageView37);
		_listeJeton.add(R.id._imageView38);
		_listeJeton.add(R.id._imageView39);
		_listeJeton.add(R.id._imageView40);
		_listeJeton.add(R.id._imageView41);
		_listeJeton.add(R.id._imageView42);

		// Grille permettant de m�moriser le choix de l'utilisateur
		_grille = new SparseIntArray();
		for (Integer item : _listeJeton) {
			_grille.append(item, 0);
		}
		
		// Ajout d'un �couteur sur le click pour chaque jeton
		for (Integer item : _listeJeton) {
			
			final ImageView image = (ImageView) findViewById(item);
			image.setOnClickListener(new OnClickListener() {

				/**
				 * Capter l'�v�nement Click sur la vue.
				 */
				public void onClick(View v) {
					
					// Envoyer l'index de la case libre au deux p�riph�riques
		            byte[] indexJeton = Integer.toString(_listeJeton.indexOf(image.getId())).getBytes();
		            _servicePartieBluetooth.ecrire(indexJeton);
					
				}
				
			});
			
		}
		
		// Initialiser le jeton d'animation
		_jetonAnimation = (ImageView) findViewById(R.id._imageViewAnimation);
		
		// Cacher le jeton d'animation
		_jetonAnimation.setVisibility(View.INVISIBLE);
		
		// Charger le nom des joueurs
		this.chargerNomJoueurs();
		
		// Afficher les informations du joueur
		this.afficherInformationsJoueur();
		
		// Initialiser le cache d'attente de connexion
		_layoutAttentePartenaire = (LinearLayout) findViewById(R.id._linearLayoutAttentePartenaire);
		
		// Initialiser le cache d'attente d'une action
		_progressBarAttentePartenaire = (ProgressBar) findViewById(R.id._progressBarAttentePartenaire);
		
		// Afficher le cache d'attente de connexion tant que la partie n'a pas commenc� et cacher le cache d'attente d'une action 
		_layoutAttentePartenaire.setVisibility(View.VISIBLE);
		_progressBarAttentePartenaire.setVisibility(View.INVISIBLE);
		
		// D�sactiver tous les jetons
		this.activerGrille(false);
		
		// Initialiser le cache de fin de partie
		_layoutFinPartie = (LinearLayout) findViewById(R.id._linearLayoutFinPartie);
		
		// Ajouter les �couteurs sur les boutons du cache de fin de partie
		Button boutonFinPartieRejouer = (Button) findViewById(R.id._buttonFinPartieRejouer);
		Button boutonFinPartieQuitter = (Button) findViewById(R.id._buttonFinPartieQuitter);
		boutonFinPartieRejouer.setOnClickListener(this);
		boutonFinPartieQuitter.setOnClickListener(this);
		
		// Ne pas affiche le cache de fin de partie tant que la partie n'est pas termin�e
		_layoutFinPartie.setVisibility(View.GONE);
		
		// Tenter de r�cup�rer de l'adresse d'un p�riph�rique s�lectionn�
		// (dans le cas d'une connexion depuis l'activit� RejoindrePartie)
		Intent intentRejoindrePartie = this.getIntent();
		if (intentRejoindrePartie.hasExtra(RejoindrePartie.ADRESSE_PERIPHERIQUE)) {
			this.connexionPeripherique(intentRejoindrePartie, true);
		}
		
	}
	
	/**
	 * Activer ou d�saciver la grille de jeu.
	 * @param activer
	 */
	private void activerGrille(Boolean activer) {
		
		for (Integer item : _listeJeton) {
			ImageView image = (ImageView) findViewById(item);
			image.setEnabled(activer);
		}
		
	}
	
	/**
	 * Charger le nom des joueurs depuis les pr�f�rences de l'application.
	 */
	private void chargerNomJoueurs() {

		// Nom par d�faut du joueur 1
		_nomJoueur1 = getString(R.string.texte_nom_joueur_1);
		
		// Nom par d�faut du joueur 2
		_nomJoueur2 = getString(R.string.texte_nom_joueur_2);
		
	}
	
	/**
	 * Afficher le nom du joueur courant.
	 */
	private void afficherInformationsJoueur() {
		
		// Affichage du nom du joueur et de son jeton
		TextView texteInformationJoueur = (TextView) findViewById(R.id._textViewTourJoueur);
		ImageView jetonJoueur = (ImageView) findViewById(R.id._imageViewTourJoueur);
		
		if (_couleurJeton == 1) {
			texteInformationJoueur.setText(getString(R.string.texte_information_tour_joueur) + " " + _nomJoueur1);
			jetonJoueur.setImageResource(R.drawable.jeton_jaune);
		} else if (_couleurJeton == 2) { 
			texteInformationJoueur.setText(getString(R.string.texte_information_tour_joueur) + " " + _nomJoueur2);
			jetonJoueur.setImageResource(R.drawable.jeton_rouge);
		}
		
	}
	
	/**
	 * Connexion � un p�riph�rique.
	 * @param intent
	 * @param connexionSecurisee
	 */
	private void connexionPeripherique(Intent intent, boolean connexionSecurisee) {
		
        // Obtenir l'adresse MAC du p�riph�rique
        String adresseMac = intent.getExtras().getString(RejoindrePartie.ADRESSE_PERIPHERIQUE);
        
        // Obtenir l'objet BluetoothDevice
        ServiceBluetooth serviceBluetooth = ServiceBluetooth.obtenirInstance();
        BluetoothDevice peripherique = serviceBluetooth.obtenirBluetoothAdapter().getRemoteDevice(adresseMac);
        
        // Attempt to connect to the device
        _servicePartieBluetooth.demarrerClientBluetooth(peripherique, connexionSecurisee);
        
    }
	
	/**
	 * Poser un jeton dans la grille.
	 * @param indexCaseSelectionnee
	 * @return
	 */
	public void poserJeton(int indexCaseSelectionnee) {
	
		Boolean poserJeton = false;
		ImageView caseSelectionnee = (ImageView) findViewById(_listeJeton.get(indexCaseSelectionnee));
			
		// Poser le jeton dans la case la plus basse possible
		int indexImage = _listeJeton.indexOf(caseSelectionnee.getId());
		ImageView caseLibre = null;
		
		if (indexImage < 6) {
			
			// 1�re colonne
			int i = 0;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 6) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 1;
				poserJeton = true;
				
			}
			
		}
		else if (indexImage < 12) {
			
			// 2�me colonne
			int i = 6;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 12) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 2;
				poserJeton = true;
				
			}
			
		}
		else if (indexImage < 18) {
			
			// 3�me colonne
			int i = 12;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 18) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 3;
				poserJeton = true;
				
			}
			
		}
		else if (indexImage < 24) {
			
			// 4�me colonne
			int i = 18;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 24) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 4;
				poserJeton = true;
				
			}
			
		}
		else if (indexImage < 30) {
			
			// 5�me colonne
			int i = 24;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 30) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 5;
				poserJeton = true;
				
			}
			
		}
		else if (indexImage < 36) {
			
			// 6�me colonne
			int i = 30;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 36) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 6;
				poserJeton = true;
				
			}
			
		}
		else if (indexImage < 42) {
			
			// 7�me colonne
			int i = 36;
			while (_grille.get(_listeJeton.get(i)) != 0) {
				i++;
				// Si on a atteint le dernier jeton de la grille (en haut � droite)
				if (i == 42)
					break;
			}
			
			if (i < 42) {
				
				// Si la colonne n'est pas enti�rement remplie, r�cup�rer la case libre
				caseLibre = (ImageView)findViewById(_listeJeton.get(i));
				_indexCaseLibre = i;
				_indexColonneCaseLibre = 7;
				poserJeton = true;
				
			}
			
		}
		
		if (poserJeton) {
			
			// Sauvegarder la case libre
			_caseLibre = caseLibre;
			
			// Enregistrer le choix dans la grille
			_grille.delete(caseLibre.getId());
			_grille.append(caseLibre.getId(), _couleurJeton);
		
			// Cr�er l'animation
			LinearLayout linearLayout = (LinearLayout) caseLibre.getParent();
			TranslateAnimation animation = new TranslateAnimation(caseLibre.getLeft() + 6, caseLibre.getLeft() + 6, 0, linearLayout.getTop() + 6);
			animation.setStartOffset(0);
			animation.setFillAfter(true);
			animation.setDuration(this.calculerDureeAnimation());
			animation.setAnimationListener(this);
			
			// Appliquer l'animation pour poser le jeton
			_jetonAnimation.setVisibility(View.VISIBLE);
			_jetonAnimation.startAnimation(animation);
			
		}

	}
	
	/**
	 * Permet de calculer la dur�e de l'animation de la pose d'un jeton.
	 * @return
	 */
	private int calculerDureeAnimation() {
		
		int duree = 1000;
		int ecart = 0;
		final int vitesse = 100;
		
		for (int i = 0; i < 7; i++) {
			if (_indexCaseLibre == (0 + (i * 6))){
				ecart = 0;
			}
			else if (_indexCaseLibre == (1 + (i * 6))) {
				ecart = vitesse * 1;
			}
			else if (_indexCaseLibre == (2 + (i * 6))) {
				ecart = vitesse * 2;
			}
			else if (_indexCaseLibre == (3 + (i * 6))) {
				ecart = vitesse * 3;
			}
			else if (_indexCaseLibre == (4 + (i * 6))) {
				ecart = vitesse * 4;
			}
			else if (_indexCaseLibre == (5 + (i * 6))) {
				ecart = vitesse * 5;
			}
		}
		
		duree = duree - ecart;
		return duree;
		
	}
	
	/**
	 * Permet de v�rifier si la partie est termin�e.
	 * @return
	 */
	private Boolean verifierEtatPartieTerminee() {
		
		Boolean partieTerminee = false;
		int couleur = _grille.get(_listeJeton.get(_indexCaseLibre));
		int nombreJetonAlignesVertiale = 1;
		int nombreJetonAlignesHorizontal = 1;
		int nombreJetonAlignesDigonaleGaucheDroiteBas = 1;
		int nombreJetonAlignesDiagonaleGaucheDroiteHaut = 1;
		
		// Vertical (haut)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre + i; 
			// V�rifier qu'on est dans la grille
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				// V�rifier qu'on ne change pas de colonne
				if (this.estDansMemeColonne(_indexColonneCaseLibre, indexJetonATester)) {
					// V�rifier si la case contient un jeton de la couleur du joueur
					if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
						nombreJetonAlignesVertiale++;
					else
						break;
				}
			}
		}
		
		// Vertical (bas)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre - i;
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				if (this.estDansMemeColonne(_indexColonneCaseLibre, indexJetonATester)) {
					if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
						nombreJetonAlignesVertiale++;
					else
						break;
				}
			}
		}
		
		// Horizontal (droite)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre + (i * 6);
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
					nombreJetonAlignesHorizontal++;
				else
					break;
			}
		}
		
		// Horizontal (gauche)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre - (i * 6);
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
					nombreJetonAlignesHorizontal++;
				else
					break;
			}
		}
		
		// Diagonale (haut gauche)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre - (i * 5);
			// V�rifier qu'on est dans la grille
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				// V�rifier qu'on change d'une colonne
				if (this.estDansMemeColonne(_indexColonneCaseLibre - i, indexJetonATester)) {
					// V�rifier si la case contient un jeton de la couleur du joueur
					if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
						nombreJetonAlignesDigonaleGaucheDroiteBas++;
					else
						break;
				}
			}
		}
		
		// Diagonale (bas droite)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre + (i * 5);
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				if (this.estDansMemeColonne(_indexColonneCaseLibre + i, indexJetonATester)) {
					if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
						nombreJetonAlignesDigonaleGaucheDroiteBas++;
					else
						break;
				}
			}
		}
		
		// Diagonale (haut droite)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre + (i * 7);
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				if (this.estDansMemeColonne(_indexColonneCaseLibre + i, indexJetonATester)) {
					if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
						nombreJetonAlignesDiagonaleGaucheDroiteHaut++;
					else
						break;
				}
			}
		}
		
		// Diagonale (bas gauche)
		for (int i = 1; i < 4; i++) {
			int indexJetonATester = _indexCaseLibre - (i * 7);
			if(indexJetonATester >= 0 && indexJetonATester < 42) {
				if (this.estDansMemeColonne(_indexColonneCaseLibre - i, indexJetonATester)) {
					if (_grille.get(_listeJeton.get(indexJetonATester)) == couleur)
						nombreJetonAlignesDiagonaleGaucheDroiteHaut++;
					else
						break;
				}
			}
		}
		
		// Sinon v�rifier s'il y a �galit�
		_egalite = true;
		for (int i = 0; i < _grille.size(); i++) {
			if (_grille.valueAt(i) == 0) {
				_egalite = false;
			}
		}
		
		// V�rifier si le joueur qui vient de poser le jeton a gagn�
		if ((nombreJetonAlignesVertiale > 3) || 
				(nombreJetonAlignesHorizontal > 3) || 
				(nombreJetonAlignesDigonaleGaucheDroiteBas > 3) || 
				(nombreJetonAlignesDiagonaleGaucheDroiteHaut > 3)) {
			
			partieTerminee = true;
			
		} else if (_egalite) {
			
			partieTerminee = true;
			
		}
		
		return partieTerminee;
		
	}
	
	/**
	 * Permet de savoir si le jeton se trouve dans la colonne sp�cifi�e.
	 * @param indexColonne
	 * @param indexJeton
	 * @return
	 */
	private Boolean estDansMemeColonne(int indexColonne, int indexJeton) {
		
		Boolean estDansMemeColonne = false;
		
		switch (indexColonne) {
		case 1:
			if (0 <= indexJeton && indexJeton <= 5) {
				estDansMemeColonne = true;
			}
			break;
		case 2:
			if (6 <= indexJeton && indexJeton <= 11) {
				estDansMemeColonne = true;
			}
			break;
		case 3:
			if (12 <= indexJeton && indexJeton <= 17) {
				estDansMemeColonne = true;
			}
			break;
		case 4:
			if (18 <= indexJeton && indexJeton <= 23) {
				estDansMemeColonne = true;
			}
			break;
		case 5:
			if (24 <= indexJeton && indexJeton <= 29) {
				estDansMemeColonne = true;
			}
			break;
		case 6:
			if (30 <= indexJeton && indexJeton <= 35) {
				estDansMemeColonne = true;
			}
			break;
		case 7:
			if (36 <= indexJeton && indexJeton <= 41) {
				estDansMemeColonne = true;
			}
			break;
		default:
			break;
		}
		
		return estDansMemeColonne;
		
	}
	
	/**
	 * Rejouer une partie.
	 */
	public void rejouerPartie() {
		
		// R�initialiser la grille de jeu
		_grille.clear();
		for (Integer item : _listeJeton) {
			_grille.append(item, 0);
		}
		
		// R�initialiser les jetons
		for (Integer item : _listeJeton) {
			ImageView image = (ImageView) findViewById(item);
			image.setEnabled(true);
			image.setImageResource(R.drawable.case_grille);
		}
		
		// R�initialiser le jeton du joueur qui commece la partie
		_couleurJeton = 1;
		
		// Modifier la couleur du jeton d'animation pour le prochain joueur
		_jetonAnimation.setImageResource(R.drawable.jeton_jaune);
		
		// Afficher les informations du joueur
		this.afficherInformationsJoueur();
		
		// Enlever le cache de fin de partie
		_layoutFinPartie.setVisibility(View.GONE);
		
		// Cacher le jeton d'animation
		_jetonAnimation.clearAnimation();
		_jetonAnimation.setVisibility(View.INVISIBLE);
		
		Toast.makeText(_contexteApplication, R.string.texte_rejouer_partie, Toast.LENGTH_SHORT).show();
		
	}
	
	/**
	 * Capter l'�v�nement Click sur la vue.
	 */
	public void onClick(View vue) {
		
		switch (vue.getId()) {
		
		case R.id._buttonFinPartieRejouer:
			// Envoyer la demande pour rejouer la partie
            byte[] rejouerPartie = Integer.toString(PartieMultijoueurBluetooth.REJOUER_PARTIE).getBytes();
            _servicePartieBluetooth.ecrire(rejouerPartie);
			break;
			
		case R.id._buttonFinPartieQuitter:
			// Fermeture de tous les Threads
			_servicePartieBluetooth.stop();
			// On affiche l'activit� NouvellePartieMultijoueurBluetooth
			Intent intentNouvellePartieMultijoueurBluetooth = new Intent(this.getApplicationContext(), NouvellePartieMultijoueurBluetooth.class);
			startActivity(intentNouvellePartieMultijoueurBluetooth);
			// On termine cette activit�
			finish();
			break;
			
		default:
			break;
			
		}
		
	}
	
	/**
	 * Capter l'�v�nement de fin d'animation.
	 */
	public void onAnimationEnd(Animation animation) {
		
		if (_couleurJeton == 1) {
		
			// valider le jeton dans la grille
			_caseLibre.setImageResource(R.drawable.case_jeton_jaune);
			
			// Modifier la couleur du jeton d'animation pour le prochain joueur
			_jetonAnimation.setImageResource(R.drawable.jeton_rouge);
			
			// Changement de joueur
			_couleurJeton = 2;
			
		}
		else if (_couleurJeton == 2) {
			
			// valider le jeton dans la grille
			_caseLibre.setImageResource(R.drawable.case_jeton_rouge);
			
			// Modifier la couleur du jeton d'animation pour le prochain joueur
			_jetonAnimation.setImageResource(R.drawable.jeton_jaune);
			
			// Changement de joueur
			_couleurJeton = 1;
			
		}
		
		// V�rifier si la partie est termin�e
		if (this.verifierEtatPartieTerminee()) {
			
			// Afficher le jeton du joueur ayant gagn� la partie
			TextView textViewTexteGagantFinPartie = (TextView) findViewById(R.id._textViewTexteGagnantFinPartie);
			
			if (_egalite) {
				
				textViewTexteGagantFinPartie.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textViewTexteGagantFinPartie.setText(R.string.texte_information_fin_partie_egalite);
				
			} 
			else {
			
				if (_couleurJeton == 1)
					textViewTexteGagantFinPartie.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jeton_rouge, 0, 0, 0);
				if (_couleurJeton == 2)
					textViewTexteGagantFinPartie.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jeton_jaune, 0, 0, 0);
				
				textViewTexteGagantFinPartie.setText(R.string.texte_information_fin_partie_gagnant);

				// Sauvegarder le score
				this.sauvegarderScore();
				
			}
			
			// D�sactiver tous les jetons
			this.activerGrille(false);
			
			// Enlever le cache d'attente d'une action
			_progressBarAttentePartenaire.setVisibility(View.INVISIBLE);
			
			// Afficher le cache de fin de partie
			_layoutFinPartie.setVisibility(View.VISIBLE);
			
		}
		else {
			
			if (_typePeripherique == RECEVEUR) {
				
				// R�activer tous les jetons
				this.activerGrille(true);
				
			}
			else if (_typePeripherique == EMETTEUR) {
				
				// Afficher la barre de progression d'attente d'une action
				_progressBarAttentePartenaire.setVisibility(View.VISIBLE);
				
			}
			
			// Afficher les informations du joueur
			this.afficherInformationsJoueur();
			
		}
		
	}

	/**
	 * Sauvegarder le score.
	 */
	private void sauvegarderScore() {

		SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES, 0);
		SharedPreferences.Editor editeur = preferences.edit();

		if (_couleurJeton == 1) {
			int score = preferences.getInt("score_perdre_bluetooth", 0);
			score++;
			editeur.putInt("score_perdre_bluetooth", score);
		}
		if (_couleurJeton == 2) {
			int score = preferences.getInt("score_gagner_bluetooth", 0);
			score++;
			editeur.putInt("score_gagner_bluetooth", score);
		}

		editeur.commit();

	}

	/**
	 * Capter l'�v�nement de r�p�tition de l'animation.
	 */
	public void onAnimationRepeat(Animation animation) {
		// Ne rien faire
	}

	/**
	 * Capter l'�v�nement de d�but d'animation.
	 */
	public void onAnimationStart(Animation animation) {
		
		// D�sactiver tous les jetons
		this.activerGrille(false);
		
		// Enlever la barre de progression d'attente d'une action
		_progressBarAttentePartenaire.setVisibility(View.INVISIBLE);
		
	}

}
