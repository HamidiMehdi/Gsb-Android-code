package visiteur.gsb.fr;


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
import android.widget.ListView;
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
 * Created by Mehdi on 17/05/2017.
 */

public class ListeRvActivity extends AppCompatActivity {

    final String TAG = "GSB_FILTER" ;
    private String ipServeur = Application.getIpServeur();
    private RequestQueue monRequestQueue;
    private TextView tvInfo ;
    private ListView lvRapport;
    private List<RapportVisite> listRv = new ArrayList<RapportVisite>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_rv);
        Log.wtf(TAG, "Activité Liste rapport visite ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Liste rapport visite");

        this.tvInfo = (TextView) findViewById(R.id.tvInfo);
        this.lvRapport = (ListView) findViewById(R.id.lvRapport);

        Bundle paquet = this.getIntent().getExtras();
        final String mois = paquet.getString("mois");
        final String annee = paquet.getString("annee");
        int moisNb = getMoisNum(mois);

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, ipServeur + "/RapportVisite/" +
                Session.getSession().getLeVisiteur().getMatricule() + "/" + moisNb + "/" + annee, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{
                            int exist = response.length();
                            if(exist > 0){ // si il y a des rapports de visite
                                for(int i = 0; i < response.length(); i++) {
                                    JSONObject rapportJson = (JSONObject) response.get(i);

                                    JSONObject motifJson = rapportJson.getJSONObject("motif");
                                    Motif motif = new Motif();
                                    motif.setCode(motifJson.getInt("code"));
                                    motif.setLibelle(motifJson.getString("libelle"));

                                    JSONObject praticienJson = rapportJson.getJSONObject("praticien");
                                    Praticien praticien = new Praticien();
                                    praticien.setNumero(praticienJson.getInt("numero"));
                                    praticien.setNom(praticienJson.getString("nom"));
                                    praticien.setPrenom(praticienJson.getString("prenom"));

                                    RapportVisite rv = new RapportVisite();
                                    rv.setNumero(rapportJson.getInt("numero"));
                                    rv.setBilan(rapportJson.getString("bilan"));
                                    rv.setCoefConfiance(rapportJson.getString("coefConfiance"));
                                    rv.setDateVisite(transformDate(rapportJson.getString("dateVisite")));
                                    rv.setDateRedaction(transformDate(rapportJson.getString("dateRedaction")));
                                    rv.setLu(rapportJson.getBoolean("lu"));
                                    rv.setLePraticien(praticien);
                                    rv.setLeMotif(motif);
                                    rv.setLeVisiteur(Session.getSession().getLeVisiteur());

                                    listRv.add(rv);
                                }

                                ArrayAdapter<RapportVisite> adaptateur = new ArrayAdapter<RapportVisite>(ListeRvActivity.this, android.R.layout.simple_list_item_1, listRv) ;
                                lvRapport.setAdapter(adaptateur);

                                tvInfo.setText("Vous avez " + listRv.size() + " rapport(s) visite :" );

                                lvRapport.setOnItemClickListener(
                                        new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                Bundle paquet = new Bundle();
                                                paquet.putParcelable("RapportVisite", listRv.get(position) );

                                                Intent intentionEnvoyer = new Intent( ListeRvActivity.this , VisuRvActivity.class);
                                                intentionEnvoyer.putExtras(paquet);

                                                startActivity(intentionEnvoyer);

                                            }
                                        }
                                );

                            }
                            else{
                                tvInfo.setText("Pas de rapport pour le mois de "+mois+" "+annee);
                                tvInfo.setTextColor(Color.RED);
                            }
                        }
                        catch (JSONException e){
                        e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListeRvActivity.this, "Erreur HTTP", Toast.LENGTH_LONG).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);



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


    public int getMoisNum(String mois) {
        int numMois = 0 ;
        switch (mois) {
            case "Janvier":
                numMois = 1;
                break;
            case "Février":
                numMois = 2;
                break;
            case "Mars":
                numMois = 3;
                break;
            case "Avril":
                numMois = 4;
                break;
            case "Mai":
                numMois = 5;
                break;
            case "Juin":
                numMois = 6;
                break;
            case "Juillet":
                numMois = 7;
                break;
            case "Aout":
                numMois = 8;
                break;
            case "Septembre":
                numMois = 9;
                break;
            case "Octobre":
                numMois = 10;
                break;
            case "Novembre":
                numMois = 11;
                break;
            case "Decembre":
                numMois = 12;
                break;
        }
        return numMois;
    }

    public GregorianCalendar transformDate(String dateSql){

        String[] tab = dateSql.split("-");
        int year = Integer.parseInt( tab[0]);
        int month = Integer.parseInt( tab[1]);
        int day = Integer.parseInt( tab[2]);

        return new GregorianCalendar(year, month, day);
    }




}

