package visiteur.gsb.fr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import visiteur.gsb.fr.Entities.Echantillon;
import visiteur.gsb.fr.Entities.Medicament;
import visiteur.gsb.fr.Entities.Motif;
import visiteur.gsb.fr.Entities.RapportVisite;
import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.MySingleton;
import visiteur.gsb.fr.Technique.Session;

/**
 * Created by Mehdi on 19/05/2017.
 */

public class SaisirEchantActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final String TAG = "GSB_FILTER" ;
    private String ipServeur = Application.getIpServeur();
    private RequestQueue monRequestQueue;

    private RapportVisite rv;
    private List<Medicament> lMedicament = new ArrayList<Medicament>();
    private ListView lvEchantillon ;
    private List<Echantillon> lEchantillon = new ArrayList<Echantillon>();
    private Button bAnnuler;
    private Button bValider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisir_echant);
        Log.wtf(TAG, "Activité saisir echantillons ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Saisir echantillons");

        Bundle paquet = this.getIntent().getExtras();
        this.rv = paquet.getParcelable("RapportVisite");

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, ipServeur + "/Medicaments" , null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject medicamentJson = (JSONObject) response.get(i);

                                Medicament medicament = new Medicament();
                                medicament.setDepotLegal(medicamentJson.getString("depotLegal"));
                                medicament.setNomCommercial(medicamentJson.getString("nomCommercial"));

                                SaisirEchantActivity.this.lMedicament.add(medicament);
                                SaisirEchantActivity.this.lEchantillon.add(new Echantillon(medicament, 0));
                            }
                            lvEchantillon = (ListView) findViewById(R.id.lvEchant);
                            ItemSaisieEchantillonAdapteur adaptateur = new ItemSaisieEchantillonAdapteur(SaisirEchantActivity.this);
                            lvEchantillon.setAdapter(adaptateur);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SaisirEchantActivity.this, "Erreur HTTP (medicament)", Toast.LENGTH_LONG).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);

        this.bAnnuler = (Button) findViewById(R.id.bAnnuler);
        this.bAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                annuler();
            }
        });

        this.bValider = (Button) findViewById(R.id.bValiderRv);
        this.bValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouterRv();
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

    public List<Medicament> getLMedicament(){
        return this.lMedicament;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    class ItemSaisieEchantillonAdapteur extends ArrayAdapter<Medicament> {
        public ItemSaisieEchantillonAdapteur(Activity context) {
            super(context, R.layout.item_saisie_echantillon, getLMedicament());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View vItem = convertView;

            if(vItem == null){
                LayoutInflater convertisseur = getLayoutInflater();
                vItem = convertisseur.inflate(R.layout.item_saisie_echantillon, parent, false);
            }

            // Renseigne le nom de chaque medoc de la liste
            TextView tvNomMedoc = (TextView) vItem.findViewById(R.id.tvNomMedicament);
            tvNomMedoc.setText(lMedicament.get(position).getNomCommercial());

            // Renseigne le spinner des quantites
            Spinner spQuantite = (Spinner) vItem.findViewById(R.id.spQuantiteMedicament);
            ArrayAdapter<Integer> aaQuantite = new ArrayAdapter<Integer>(
                    SaisirEchantActivity.this,
                    android.R.layout.simple_spinner_item,
                    getQuantiteSpinner()
            );

            aaQuantite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spQuantite.setAdapter(aaQuantite);

            spQuantite.setSelection(0);
            spQuantite.setTag(Integer.valueOf(position));

            final int posMedocList = position;

            spQuantite.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                            View view, int positionQuantite, long id) {
                                lEchantillon.get(posMedocList).setQuantite(positionQuantite); // on met a jour la quantité quand on touche au spinner

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            //
                        }
                    }
            );
            return vItem;
        }
    }

    public List<Integer> getQuantiteSpinner(){
        List<Integer> sQuantite = new ArrayList<Integer>();
        sQuantite.add(0);
        sQuantite.add(1);
        sQuantite.add(2);
        sQuantite.add(3);
        sQuantite.add(4);
        sQuantite.add(5);

        return sQuantite;
    }

    public void annuler(){
        Intent intentionEnvoyer = new Intent(this , MenuActivity.class);
        startActivity(intentionEnvoyer);
        Toast.makeText(this, "Saisie du rapport visite annulé", Toast.LENGTH_LONG).show();
    }

    public void ajouterRv(){

        String[] tabBilan = rv.getBilan().split(" ");
        String bilan = "" ;
        if(tabBilan.length > 0){
            int derniereLigne = tabBilan.length - 1 ;
            for(int i=0; i<tabBilan.length; i++){
                if(i == derniereLigne){
                    bilan = bilan + tabBilan[i] ;
                }
                else{
                    bilan = bilan + tabBilan[i] + "|" ;
                }
            }
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ipServeur+"/NewRapport/"
                + Session.getSession().getLeVisiteur().getMatricule()+"/"+rv.getLePraticien().getNumero()+"/"
                + bilan +"/"+ rv.getLeMotif().getCode() + "/" + rv.getCoefConfiance()+ "/"
                + rv.getDateVisite().get(Calendar.YEAR)+"-"+rv.getDateVisite().get(Calendar.MONTH)
                + "-"+rv.getDateVisite().get(Calendar.DAY_OF_MONTH), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int numDuRapport = response.getInt("rap_num");
                            ajouterEchant(numDuRapport);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SaisirEchantActivity.this, "Erreur HTTP(insert rapport)", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);
    }

    public void ajouterEchant(int numRapport){

        String echantillons = "";
        int dernierEchant = lEchantillon.size() - 1 ;
        for(int i=0; i < lEchantillon.size(); i++){

            if(i == dernierEchant){
                echantillons = echantillons+lEchantillon.get(i).getMedicament().getDepotLegal()+";"+lEchantillon.get(i).getQuantite();
            }
            else{
                echantillons = echantillons+lEchantillon.get(i).getMedicament().getDepotLegal()+";"+lEchantillon.get(i).getQuantite()+"|";
            }
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, ipServeur+"/AjouterEchant/"
                + Session.getSession().getLeVisiteur().getMatricule()+"/"+numRapport+"/"+echantillons, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean insertionOk = response.getBoolean("insertion");
                            if(insertionOk){
                                Intent intentionEnvoyer = new Intent(SaisirEchantActivity.this , MenuActivity.class);
                                startActivity(intentionEnvoyer);
                                Toast.makeText(SaisirEchantActivity.this, "Le rapport a bien été enregitré", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SaisirEchantActivity.this, "Erreur HTTP(insert echant)", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);
    }



}
