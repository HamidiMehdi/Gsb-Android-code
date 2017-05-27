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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.MySingleton;
import visiteur.gsb.fr.Technique.Session;

/**
 * Created by Mehdi on 20/05/2017.
 */

public class ChangerMdpActivity extends AppCompatActivity {

    final String TAG = "GSB_FILTER" ;
    private String ipServeur = Application.getIpServeur();
    private RequestQueue monRequestQueue;

    private TextView tvErreur;
    private EditText etMdpActuel;
    private EditText etNewMdr1;
    private EditText etNewMdp2;
    private Button bValider;
    private Button bAnnuler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changer_mdp);
        Log.wtf(TAG, "Activité changer mdp ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Changer mdp");

        this.tvErreur = (TextView) findViewById(R.id.tvErreurMdp);
        this.etMdpActuel = (EditText) findViewById(R.id.etMdpActuel);
        this.etNewMdr1 = (EditText) findViewById(R.id.etNewMdr1);
        this.etNewMdp2 = (EditText) findViewById(R.id.etNewMdr2);

        this.bValider = (Button) findViewById(R.id.bValider);
        this.bValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerMdp();
            }
        });

        this.bAnnuler = (Button) findViewById(R.id.bRetourMenu);
        this.bAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retourMenu();
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


    public void changerMdp(){

        if(Session.getSession().getLeVisiteur().getMdp().equals(md5(this.etMdpActuel.getText().toString()))){
            if(this.etNewMdr1.getText().toString().matches("")){ // si new mdr 1 est vide
                tvErreur.setText("Veuillez inserer les deux nouveaux mots de passe");
                tvErreur.setTextColor(Color.RED);
            }
            else if(this.etNewMdp2.getText().toString().matches("")){
                tvErreur.setText("Veuillez inserer les deux nouveaux mots de passe");
                tvErreur.setTextColor(Color.RED);
            }
            else if(!this.etNewMdr1.getText().toString().equals(this.etNewMdp2.getText().toString())){ // si 2 new mdp different
                tvErreur.setText("Les deux mots de passe ne sont pas identiques");
                tvErreur.setTextColor(Color.RED);
            }
            else if(this.etNewMdr1.getText().toString().equals(this.etNewMdp2.getText().toString())){ //si tt est bon

                JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, ipServeur+"/ModifierMdp/"+
                        Session.getSession().getLeVisiteur().getMatricule()+"/"+md5(etNewMdr1.getText().toString()), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    Boolean update = response.getBoolean("modificationMDP");
                                    if(update){ // si le rapport a été mis a jour
                                        tvErreur.setText("Le mot de passe a bien été modifié");
                                        tvErreur.setTextColor(Color.GREEN);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ChangerMdpActivity.this, "Erreur HTTP", Toast.LENGTH_LONG).show();
                            }
                        }
                );

                // Add the request to the RequestQueue.
                monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
                monRequestQueue.add(req);
            }
        }
        else{
            tvErreur.setText("Mot de passe actuel incorrect");
            tvErreur.setTextColor(Color.RED);
        }

    }

    public void retourMenu(){
        Intent intentionEnvoyer = new Intent(this , MenuActivity.class);
        startActivity(intentionEnvoyer);
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
