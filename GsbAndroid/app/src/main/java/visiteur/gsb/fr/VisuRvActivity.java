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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import visiteur.gsb.fr.Entities.RapportVisite;
import visiteur.gsb.fr.Entities.Visiteur;
import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.MySingleton;
import visiteur.gsb.fr.Technique.Session;

/**
 * Created by Mehdi on 18/05/2017.
 */

public class VisuRvActivity extends AppCompatActivity {

    final String TAG = "GSB_FILTER" ;
    private String ipServeur = Application.getIpServeur();
    private RequestQueue monRequestQueue;
    TextView numero;
    TextView bilan;
    TextView coefConfiance;
    TextView dateVisite;
    TextView dateRedac;
    TextView lu;
    TextView nomDuPraticien;
    TextView motif;
    Button bListesEchantillons ;
    private RapportVisite rv ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visu_rv);
        Log.wtf(TAG, "Activité Visu rapport visite ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Visu rapport visite");

        this.numero = (TextView) findViewById(R.id.numero);
        this.bilan = (TextView) findViewById(R.id.bilan);
        this.coefConfiance = (TextView) findViewById(R.id.coefConfiance);
        this.dateVisite = (TextView) findViewById(R.id.dateVisite);
        this.dateRedac = (TextView) findViewById(R.id.dateRedac);
        this.lu = (TextView) findViewById(R.id.lu);
        this.nomDuPraticien = (TextView) findViewById(R.id.nomDuPraticien);
        this.motif = (TextView) findViewById(R.id.motif);
        this.bListesEchantillons = (Button) findViewById(R.id.BMedicament);
        this.bListesEchantillons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLesEchantillons();
            }
        });

        Bundle paquet = this.getIntent().getExtras();
        this.rv = paquet.getParcelable("RapportVisite");

        this.numero.setText("Numero : " + this.rv.getNumero());
        this.bilan.setText("Bilan : " + this.rv.getBilan());
        this.coefConfiance.setText("Coef confiance : " + this.rv.getCoefConfiance());
        this.dateVisite.setText("Date de visite : "+ this.rv.getDateVisite().get(Calendar.DAY_OF_MONTH)+"/"+this.rv.getDateVisite().get(Calendar.MONTH)+"/"+this.rv.getDateVisite().get(Calendar.YEAR));
        this.dateRedac.setText("Date de redaction : "+this.rv.getDateRedaction().get(Calendar.DAY_OF_MONTH)+"/"+this.rv.getDateRedaction().get(Calendar.MONTH)+"/"+this.rv.getDateRedaction().get(Calendar.YEAR));
        this.lu.setText("Est lu : " + this.rv.isLu());
        this.nomDuPraticien.setText("Praticien : " + this.rv.getLePraticien().getNom() + " " + this.rv.getLePraticien().getPrenom());
        this.motif.setText("Motif : " + this.rv.getLeMotif().getLibelle());

        if(this.rv.isLu() == false){
            updateLu(rv.getNumero());
        }
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

    public void getLesEchantillons(){

        Bundle paquet = new Bundle();
        paquet.putParcelable("RapportVisite", rv );

        Intent intentionEnvoyer = new Intent( this , VisuEchantActivity.class);
        intentionEnvoyer.putExtras(paquet);

        startActivity(intentionEnvoyer);
    }

    public void updateLu(int numero){

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, ipServeur+"/UpdateRapport/"+numero, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean update = response.getBoolean("modifier");
                            if(update){ // si le rapport a été mis a jour
                                Toast.makeText(VisuRvActivity.this, "Le rapport est maintenant considéré comme lu", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(VisuRvActivity.this, "Le rapport n'a pas pu être mis à jour", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VisuRvActivity.this, "Erreur HTTP", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);
    }

}
