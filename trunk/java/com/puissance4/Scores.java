package com.puissance4;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Classe de gestion de l'activit� Scores.
 */
public class Scores extends Activity implements OnClickListener {

    // UI variables
    private TextView _textViewScoreGagnerIA = null;
    private TextView _textViewScorePerdreIA = null;
    private TextView _textViewScoreGagnerLocale = null;
    private TextView _textViewScorePerdreLocale = null;
    private TextView _textViewScoreGagnerBluetooth = null;
    private TextView _textViewScorePerdreBluetooth = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores);
        
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
    	
    	// On affiche l'activit� principale
		Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
		startActivity(intent);
		// On termine cette activit�
		finish();
		
    }
    
    /**
     * Fonction d'initialisation au d�marrage de l'application.
     */
    public void initialisation() {

        _textViewScoreGagnerIA = (TextView) findViewById(R.id._textViewScoreGagnerIA);
        _textViewScorePerdreIA = (TextView) findViewById(R.id._textViewScorePerdreIA);
        _textViewScoreGagnerLocale = (TextView) findViewById(R.id._textViewScoreGagnerLocale);
        _textViewScorePerdreLocale = (TextView) findViewById(R.id._textViewScorePerdreLocale);
        _textViewScoreGagnerBluetooth = (TextView) findViewById(R.id._textViewScoreGagnerBluetooth);
        _textViewScorePerdreBluetooth = (TextView) findViewById(R.id._textViewScorePerdreBluetooth);

        // Charger les scores
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES, 0);
        _textViewScoreGagnerIA.setText(Integer.toString(preferences.getInt("score_gagner_IA", 0)));
        _textViewScorePerdreIA.setText(Integer.toString(preferences.getInt("score_perdre_IA", 0)));
        _textViewScoreGagnerLocale.setText(Integer.toString(preferences.getInt("score_gagner_locale", 0)));
        _textViewScorePerdreLocale.setText(Integer.toString(preferences.getInt("score_perdre_locale", 0)));
        _textViewScoreGagnerBluetooth.setText(Integer.toString(preferences.getInt("score_gagner_bluetooth", 0)));
        _textViewScorePerdreBluetooth.setText(Integer.toString(preferences.getInt("score_perdre_bluetooth", 0)));

    }
	
    /**
     * Capter l'�v�nement Click sur la vue.
     */
	public void onClick(View v) {
	}

}
