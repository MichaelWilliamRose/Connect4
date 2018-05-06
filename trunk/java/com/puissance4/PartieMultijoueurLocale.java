package com.puissance4;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Classe de gestion de l'activit� PartieMultijoueurLocale.
 */
public class PartieMultijoueurLocale extends Activity implements OnClickListener, AnimationListener {
	
	// Le contexte de l'application
	private static Context _contexteApplication = null;
	
	// Le handler permettant de mettre � jour l'activit� lorsqu'un sous-thread a termin� son travail
	private Handler _handler = new Handler();
	
	// La liste des jetons
	private static ArrayList<Integer> _listeJeton = null;
	
	// Grille permettant de m�moriser le choix de l'utilisateur
	private static SparseIntArray _grille = null;

	// La couleur du premier jeton pos� (0 = noir, 1 = jaune, 2 = rouge)
	private static int _couleurJeton = 0;
	
	// Cache d'attente utilis� pour avertir l'attente d'une action de la part de l'autre joueur
	private LinearLayout _layoutFinPartie = null;
	
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
	private String _nomJoueur2;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.partie_multijoueur_locale);
		
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
		
		// On affiche l'activit� NouvellePartie
		Intent intent = new Intent(this.getApplicationContext(), NouvellePartie.class);
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
					
					// Ouverture d'un sous-thread permettant de mettre � jour l'activit�
					_handler.post(new Runnable() {
				
						/**
						 * Ex�cuter le thread.
						 */
						public void run() {

							int indexJeton = _listeJeton.indexOf(image.getId());
							
							// Afficher le jeton choisi par l'utilisateur
							//Boolean poserJetonJoueur = afficherJeton(indexJeton);
							poserJeton(indexJeton);
							
						}
						
					});
					
				}
				
			});
			
		}
		
		// Initialiser le cache de fin de partie
		_layoutFinPartie = (LinearLayout) findViewById(R.id._linearLayoutFinPartie);
		
		// Ajouter les �couteurs sur les boutons du cache de fin de partie
		Button boutonFinPartieRejouer = (Button) findViewById(R.id._buttonFinPartieRejouer);
		Button boutonFinPartieQuitter = (Button) findViewById(R.id._buttonFinPartieQuitter);
		boutonFinPartieRejouer.setOnClickListener(this);
		boutonFinPartieQuitter.setOnClickListener(this);
		
		// Ne pas afficher le cache de fin de partie tant que la partie n'est pas termin�e
		_layoutFinPartie.setVisibility(View.GONE);
		
		// Initialiser le jeton d'animation
		_jetonAnimation = (ImageView) findViewById(R.id._imageViewAnimation);
		
		// Cacher le jeton d'animation
		_jetonAnimation.setVisibility(View.INVISIBLE);
		
		// Charger le nom des joueurs
		this.chargerNomJoueurs();
		
		// Afficher les informations du joueur
		this.afficherInformationsJoueur();
		
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
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		_nomJoueur1 = preferences.getString(getString(R.string.cle_option_nom_joueur_1), getString(R.string.texte_nom_joueur_1));
		_nomJoueur2 = preferences.getString(getString(R.string.cle_option_nom_joueur_2), getString(R.string.texte_nom_joueur_2));

		if (_nomJoueur1.equalsIgnoreCase(""))
			_nomJoueur1 = getString(R.string.texte_nom_joueur_1);

		if (_nomJoueur2.equalsIgnoreCase(""))
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
	 * G�n�rer un index de jeton al�toire pour la grille de jeu.
	 * @return
	 */
	public int genererIndexJetonAleatoire() {
		
		int indexJetonAleatoire = 0;
		
		Random generateurNombreAleatoire = new Random();
		indexJetonAleatoire = generateurNombreAleatoire.nextInt(42);
		
		// Tester si la case n'a pas d�j� �t� prise
		if (_grille.get(_listeJeton.get(indexJetonAleatoire)) != 0) {
			indexJetonAleatoire = genererIndexJetonAleatoire();
		}
		
		return indexJetonAleatoire;
		
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
		
		// Affichage des informations du joueur
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
			// Rejouer une partie
            this.rejouerPartie();
			break;
			
		case R.id._buttonFinPartieQuitter:
			// On affiche l'activit� NouvellePartie
			Intent intentNouvellePartie = new Intent(this.getApplicationContext(), NouvellePartie.class);
			startActivity(intentNouvellePartie);
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
			
			// Afficher le cache de fin de partie
			_layoutFinPartie.setVisibility(View.VISIBLE);
			
		}
		else {
			
			// R�activer tous les jetons
			this.activerGrille(true);
			
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
			int score = preferences.getInt("score_perdre_locale", 0);
			score++;
			editeur.putInt("score_perdre_locale", score);
		}
		if (_couleurJeton == 2) {
			int score = preferences.getInt("score_gagner_locale", 0);
			score++;
			editeur.putInt("score_gagner_locale", score);
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
		
	}

}
