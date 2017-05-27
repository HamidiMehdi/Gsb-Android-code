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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import visiteur.gsb.fr.Entities.Medicament;
import visiteur.gsb.fr.Entities.Motif;
import visiteur.gsb.fr.Entities.Praticien;
import visiteur.gsb.fr.Entities.RapportVisite;
import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.MySingleton;
import visiteur.gsb.fr.Technique.Session;

/**
 * Created by Mehdi on 18/05/2017.
 */

public class VisuEchantActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final String TAG = "GSB_FILTER" ;
    private String ipServeur = Application.getIpServeur();
    private RequestQueue monRequestQueue;
    private RapportVisite rv;
    TextView tvMedicament ;
    ListView lvMedicament ;
    private Map<Medicament,Integer> lesEchantillons = new HashMap<Medicament,Integer>() ;
    private List<Medicament> lMedicaments = new ArrayList<Medicament>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visu_echant);
        Log.wtf(TAG, "Activité Visu echantillon ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Visu echantillons");

        Bundle paquet = this.getIntent().getExtras();
        this.rv = paquet.getParcelable("RapportVisite");

        this.tvMedicament = (TextView) findViewById(R.id.Medicament);
        this.lvMedicament = (ListView) findViewById(R.id.lvMedicament);

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, ipServeur + "/Echantillons/" +this.rv.getNumero(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{
                            int exist = response.length();
                            if(exist > 0){ // si il y a des echantillons
                                for(int i = 0; i < response.length(); i++) {
                                    JSONObject echantillonsJson = (JSONObject) response.get(i);

                                    JSONObject medicamentJson = echantillonsJson.getJSONObject("medicament");
                                    Medicament medicament = new Medicament();
                                    medicament.setDepotLegal(medicamentJson.getString("depotLegal"));
                                    medicament.setNomCommercial(medicamentJson.getString("nomCommercial"));

                                    int quantite = echantillonsJson.getInt("quantite");

                                    lesEchantillons.put(medicament, quantite);
                                }

                                lMedicaments = new ArrayList<Medicament>(lesEchantillons.keySet());

                                ItemCommandeAdaptateur adaptateur = new ItemCommandeAdaptateur();

                                lvMedicament.setAdapter(adaptateur);
                                lvMedicament.setOnItemClickListener(VisuEchantActivity.this);

                                tvMedicament.setText("Vous avez " + lesEchantillons.size() + " échantillon(s) : ");
                            }
                            else{
                                tvMedicament.setText("Pas de d'échantillon pour ce rapport");
                                tvMedicament.setTextColor(Color.RED);
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
                        Toast.makeText(VisuEchantActivity.this, "Erreur HTTP", Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class ItemCommandeAdaptateur extends ArrayAdapter<Medicament>{

        ItemCommandeAdaptateur(){
            super(VisuEchantActivity.this, R.layout.item_echantillons, R.id.tvItemEchant, lMedicaments);
        }
        public View getView(int position, View convertView, ViewGroup parent){

            View vItem = super.getView(position, convertView, parent);
            TextView tvQuantite = (TextView) vItem.findViewById(R.id.tvItemQuantite);

            List<Integer> lq = new ArrayList<Integer>(lesEchantillons.values());
            tvQuantite.setText(lq.get(position).toString());

            return vItem;
        }
    }


}
