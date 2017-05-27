package visiteur.gsb.fr.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eleve on 20/02/17.
 */
public class Visiteur implements Parcelable {


    private String matricule ;
    private String mdp ;
    private String nom ;
    private String prenom ;


    public Visiteur (){

    }

    public Visiteur (String matricule, String mdp, String nom, String prenom){

        this.matricule = matricule ;
        this.mdp = mdp ;
        this.nom = nom ;
        this.prenom = prenom ;
    }


    public String getMatricule() {
        return this.matricule;
    }

    public String getMdp() {
        return this.mdp;
    }

    public String getNom() {
        return this.nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.matricule);
        dest.writeString(this.mdp);
        dest.writeString(this.nom);
        dest.writeString(this.prenom);
    }

    public Visiteur( Parcel in){
        this.matricule = in.readString();
        this.mdp = in.readString();
        this.nom = in.readString();
        this.prenom = in.readString();
    }

    public static final Creator<Visiteur> CREATOR = new Creator<Visiteur>() {

        public Visiteur createFromParcel(Parcel source){
            return new Visiteur(source);
        }

        public Visiteur[] newArray(int size){
            return new Visiteur[size];
        }
    };

    @Override
    public String toString() {
        return "Visiteur{" +
                "matricule='" + matricule + '\'' +
                ", mdp='" + mdp + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}
