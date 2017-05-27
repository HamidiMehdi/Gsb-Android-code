package visiteur.gsb.fr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.Session;

/**
 * Created by Mehdi on 14/05/2017.
 */

public class RechercherActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final String TAG = "GSB_FILTER" ;
    private Spinner sMois ;
    private Spinner sAnnee ;
    private Button bConsulter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechercher);
        Log.wtf(TAG, "Activité rechercher ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Rechercher");

        this.bConsulter = (Button) findViewById(R.id.Bconsulter);
        this.bConsulter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consulter();
            }
        });

        List<String> lesMois = this.getMois();
        List<String> lesAnnees = this.getAnnee();

        this.sMois = (Spinner) findViewById(R.id.sMois);
        this.sMois.setOnItemSelectedListener(this);
        ArrayAdapter<String> aaMois = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lesMois);
        aaMois.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sMois.setAdapter(aaMois);

        this.sAnnee = (Spinner) findViewById(R.id.sAnnee);
        this.sAnnee.setOnItemSelectedListener(this);
        ArrayAdapter<String> aaAnnee = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lesAnnees);
        aaAnnee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sAnnee.setAdapter(aaAnnee);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu à l'ActionBar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.changer_mdp:
                Intent intentionEnvoyers = new Intent(this , ChangerMdpActivity.class);
                startActivity(intentionEnvoyers);
                return true;
            case R.id.aPropos:
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("A propos ...");
                alertDialog.setMessage(Application.getaPropos());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            case R.id.deconnexion:
                Session.getSession().fermer();
                Log.wtf(TAG, "Deconnexion");
                Intent intentionEnvoyer = new Intent(this, ConnexionActivity.class);
                startActivity(intentionEnvoyer);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void consulter(){

        Bundle paquet = new Bundle();
        paquet.putString("mois", sMois.getSelectedItem().toString() );
        paquet.putString("annee", sAnnee.getSelectedItem().toString());

        Intent intentionEnvoyer = new Intent(this , ListeRvActivity.class);
        intentionEnvoyer.putExtras(paquet);

        startActivity(intentionEnvoyer);

    }

    public List<String> getMois(){
        List<String> lesMois = new ArrayList<String>();
        lesMois.add("Janvier");
        lesMois.add("Février");
        lesMois.add("Mars");
        lesMois.add("Avril");
        lesMois.add("Mai");
        lesMois.add("Juin");
        lesMois.add("Juillet");
        lesMois.add("Aout");
        lesMois.add("Septembre");
        lesMois.add("Octobre");
        lesMois.add("Novembre");
        lesMois.add("Decembre");

        return lesMois;
    }

    public List<String> getAnnee(){
        List<String> lesAnnees = new ArrayList<String>();
        lesAnnees.add("2010");
        lesAnnees.add("2011");
        lesAnnees.add("2012");
        lesAnnees.add("2013");
        lesAnnees.add("2014");
        lesAnnees.add("2015");
        lesAnnees.add("2016");
        lesAnnees.add("2017");

        return lesAnnees;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
