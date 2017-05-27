package visiteur.gsb.fr;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import visiteur.gsb.fr.Technique.Application;
import visiteur.gsb.fr.Technique.Session;

public class MenuActivity extends AppCompatActivity {

    final String TAG = "GSB_FILTER" ;
    TextView tvNomPrenom ;
    Button bSaisir ;
    Button bConsulter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.wtf(TAG, "Activité menu ok !");

        //Modification du titre de l'activité
        setTitle("GSB Visiteur - Menu");

        this.tvNomPrenom = (TextView) findViewById(R.id.nomPrenomUser);
        this.bConsulter = (Button) findViewById(R.id.Bconsulter);
        this.bSaisir = (Button) findViewById(R.id.Bsaisir);

        this.bConsulter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consulterRV();
            }
        });
        this.bSaisir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saisirRv();
            }
        });


        this.tvNomPrenom.setText(Session.getSession().getLeVisiteur().getNom() + " " + Session.getSession().getLeVisiteur().getPrenom());
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


    public void consulterRV(){

        Intent intentionEnvoyer = new Intent(this , RechercherActivity.class);
        startActivity(intentionEnvoyer);
    }

    public void saisirRv(){

        Intent intentionEnvoyer = new Intent(this , SaisirRvActivity.class);
        startActivity(intentionEnvoyer);
    }

}
