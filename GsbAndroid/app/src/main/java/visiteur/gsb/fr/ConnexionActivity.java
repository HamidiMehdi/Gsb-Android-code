package visiteur.gsb.fr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import visiteur.gsb.fr.Entities.Visiteur;
import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.MySingleton;
import visiteur.gsb.fr.Technique.Session;

public class ConnexionActivity extends AppCompatActivity {

    public static final String TAG = "GSB_FILTER";
    private TextView tvErreur;
    private EditText etMatricule;
    private EditText etMdp;
    private Button bSeConnecter;
    private RequestQueue monRequestQueue;
    private String ipServeur = Application.getIpServeur();
    private ProgressDialog pDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        Log.wtf(TAG, "Activité connexion lancé !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Connexion");

        this.etMatricule = (EditText) findViewById(R.id.etMatricule);
        this.etMdp = (EditText) findViewById(R.id.etMdp);
        this.tvErreur = (TextView) findViewById(R.id.messageErreur);
        this.bSeConnecter = (Button) findViewById(R.id.Bvalider);
        this.bSeConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seConnecter(etMatricule.getText().toString(), etMdp.getText().toString());
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Connexion...");
        pDialog.setCancelable(false);
    }


    public void seConnecter(String matricule, String mdp) {

        String mdpHash = md5(mdp) ;
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, ipServeur+"/Connexion/"+matricule+"/"+mdpHash, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean exist = response.getBoolean("existe");
                            if(exist){ //si le mec existe
                                Visiteur visiteur = new Visiteur();
                                visiteur.setMatricule(response.getString("matricule"));
                                visiteur.setMdp(response.getString("mdp"));
                                visiteur.setNom(response.getString("nom"));
                                visiteur.setPrenom(response.getString("prenom"));

                                Session.getSession().ouvrir(visiteur);
                                Intent intentionEnvoyer = new Intent(ConnexionActivity.this, MenuActivity.class);
                                startActivity(intentionEnvoyer);

                                Toast.makeText(ConnexionActivity.this, "Vous êtes connecté", Toast.LENGTH_LONG).show();
                            }else{
                                tvErreur.setText("Echec de l'authentification. Recommencez...");
                                etMdp.setText("");
                            }
                            pDialog.hide();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ConnexionActivity.this, "Erreur HTTP", Toast.LENGTH_LONG).show();
                        pDialog.hide();
                    }
                }
        );

        // Add the request to the RequestQueue.
        monRequestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        monRequestQueue.add(req);
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