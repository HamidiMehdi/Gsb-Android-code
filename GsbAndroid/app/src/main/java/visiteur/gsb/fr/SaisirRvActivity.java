package visiteur.gsb.fr;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import visiteur.gsb.fr.Entities.Motif;
import visiteur.gsb.fr.Entities.Praticien;
import visiteur.gsb.fr.Entities.RapportVisite;
import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.MySingleton;
import visiteur.gsb.fr.Technique.Session;

/**
 * Created by Mehdi on 19/05/2017.
 */

public class SaisirRvActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,AdapterView.OnItemSelectedListener {

    final String TAG = "GSB_FILTER" ;
    private String ipServeur = Application.getIpServeur();
    private RequestQueue monRequestQueue;

    private TextView tvErreur ;
    private TextView tvDateVisite ;
    private GregorianCalendar dateOjd = new GregorianCalendar();
    private GregorianCalendar laDateVisite = new GregorianCalendar();
    private Button bSaisirDateVisite ;
    private Spinner spPraticien ;
    private Spinner spMotif ;
    private EditText etBilan ;
    private EditText etCoefConf ;
    private Button bSaisirEchant ;
    private Button bAnnuler ;

    public List<Praticien> lPraticiens;
    public List<Motif> lMotifs;

    public ArrayAdapter<Praticien> aaPraticien;
    public ArrayAdapter<Motif> aaMotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisir_rv);
        Log.wtf(TAG, "Activité saisir rapport visite ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Saisir rapport visite");

        this.tvErreur = (TextView) findViewById(R.id.tvErreur);

        int moisDefault = this.dateOjd.get(Calendar.MONTH) ;
        moisDefault = moisDefault+1;
        this.laDateVisite = new GregorianCalendar(this.dateOjd.get(Calendar.YEAR), moisDefault, this.dateOjd.get(Calendar.DAY_OF_MONTH));
        this.tvDateVisite = (TextView) findViewById(R.id.tvDateVisite);

        this.tvDateVisite.setText(this.laDateVisite.get(Calendar.DAY_OF_MONTH) + "/" + this.laDateVisite.get(Calendar.MONTH) + "/" + this.laDateVisite.get(Calendar.YEAR));
        this.bSaisirDateVisite = (Button) findViewById(R.id.bSaisirDate);
        this.bSaisirDateVisite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saisirDate();
            }
        });

        this.spPraticien = (Spinner) findViewById(R.id.spPraticiens);
        this.getLesPraticiens();

        this.spMotif = (Spinner) findViewById(R.id.spMotifs);
        this.getLesMotifs();

        this.etBilan = (EditText) findViewById(R.id.etBilan);
        this.etCoefConf = (EditText) findViewById(R.id.etCoefConf);

        this.bSaisirEchant = (Button) findViewById(R.id.bSaisirEchant);
        this.bSaisirEchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saisirEchant();
            }
        });
        this.bAnnuler = (Button) findViewById(R.id.bAnnuler);
        this.bAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                annuler();
            }
        });
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
                //on redictionne vers la vue de modif mdp
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

    public void saisirDate(){
        int jour = laDateVisite.get(Calendar.DAY_OF_MONTH);
        int mois = laDateVisite.get(Calendar.MONTH);
        int annee = laDateVisite.get(Calendar.YEAR);

        new DatePickerDialog(this, this, annee, mois, jour).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateVisite = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
        this.tvDateVisite.setText(dateVisite);
        this.laDateVisite = new GregorianCalendar(year, month +1, dayOfMonth);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getLesPraticiens(){

        final List<Praticien> lesPra = new ArrayList<Praticien>();

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, ipServeur + "/Praticiens" , null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject praticienJson = (JSONObject) response.get(i);

                                Praticien praticien = new Praticien();
                                praticien.setNumero(praticienJson.getInt("numero"));
                                praticien.setNom(praticienJson.getString("nom"));
                                praticien.setPrenom(praticienJson.getString("prenom"));

                                lesPra.add(new Praticien(praticien.getNumero(), praticien.getNom(), praticien.getPrenom()));
                            }
                            MajSpinnerPraticien(lesPra);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SaisirRvActivity.this, "Erreur HTTP (praticien)", Toast.LENGTH_LONG).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);
    }

    private void MajSpinnerPraticien(List<Praticien> praticiens){

        lPraticiens = praticiens;
        aaPraticien = new ArrayAdapter<Praticien>(this,android.R.layout.simple_spinner_item,lPraticiens);
        aaPraticien.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPraticien.setAdapter(aaPraticien);
        spPraticien.setSelection(0);
    }

    public void getLesMotifs(){

        final List<Motif> lesMotifs = new ArrayList<Motif>();

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, ipServeur + "/Motifs" , null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject motifJson = (JSONObject) response.get(i);

                                Motif motif = new Motif();
                                motif.setCode(motifJson.getInt("code"));
                                motif.setLibelle(motifJson.getString("libelle"));

                                lesMotifs.add(new Motif(motif.getCode(), motif.getLibelle()));
                            }
                            MajSpinnerMotif(lesMotifs);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SaisirRvActivity.this, "Erreur HTTP (motif)", Toast.LENGTH_LONG).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);
    }

    private void MajSpinnerMotif(List<Motif> motifs){

        lMotifs = motifs;
        aaMotif = new ArrayAdapter<Motif>(this,android.R.layout.simple_spinner_item,lMotifs);
        aaMotif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotif.setAdapter(aaMotif);
        spMotif.setSelection(0);
    }

    public void saisirEchant(){
        if(this.etBilan.getText().toString().matches("")){ // si le champ est vide
            this.tvErreur.setText("Le bilan n'a pas été inséré");
            this.tvErreur.setTextColor(Color.RED);
        }
        else{
            if(this.etCoefConf.getText().toString().matches("")){
                this.tvErreur.setText("Le coefficient de confiance n'a pas été inséré");
                this.tvErreur.setTextColor(Color.RED);
            }
            else{

                RapportVisite rv = new RapportVisite();
                rv.setBilan(this.etBilan.getText().toString());
                rv.setCoefConfiance(this.etCoefConf.getText().toString());
                rv.setDateVisite(this.laDateVisite);
                rv.setLu(false);
                rv.setLePraticien(lPraticiens.get(this.spPraticien.getSelectedItemPosition()));
                rv.setLeMotif(lMotifs.get(this.spMotif.getSelectedItemPosition()));
                rv.setLeVisiteur(Session.getSession().getLeVisiteur());

                Bundle paquet = new Bundle();
                paquet.putParcelable("RapportVisite",rv );

                Intent intentionEnvoyer = new Intent(this , SaisirEchantActivity.class);
                intentionEnvoyer.putExtras(paquet);

                startActivity(intentionEnvoyer);
            }
        }
    }

    public void annuler(){
        Intent intentionEnvoyer = new Intent(this , MenuActivity.class);
        startActivity(intentionEnvoyer);
        Toast.makeText(this, "Saisie du rapport visite annulé", Toast.LENGTH_LONG).show();
    }
}
