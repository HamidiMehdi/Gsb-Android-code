package visiteur.gsb.fr.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RapportVisite implements Parcelable{

	private int numero ;
	private String bilan ;
	private String coefConfiance ;
	private GregorianCalendar dateVisite ;
	private GregorianCalendar dateRedaction ;
	private boolean lu ;
	
	private Praticien lePraticien ;
	private Visiteur leVisiteur ;
	private Motif leMotif ;

	public RapportVisite() {
		super();
	}

	public RapportVisite(int numero, String bilan, String coefConfiance,
			GregorianCalendar dateVisite, GregorianCalendar dateRedaction,
			boolean lu) {
		super();
		this.numero = numero;
		this.bilan = bilan;
		this.coefConfiance = coefConfiance;
		this.dateVisite = dateVisite;
		this.dateRedaction = dateRedaction;
		this.lu = lu;
	}

	public RapportVisite(int numero, String bilan, String coefConfiance,
			GregorianCalendar dateVisite, GregorianCalendar dateRedaction,
			boolean lu, Praticien lePraticien, Visiteur leVisiteur,
			Motif leMotif) {
		super();
		this.numero = numero;
		this.bilan = bilan;
		this.coefConfiance = coefConfiance;
		this.dateVisite = dateVisite;
		this.dateRedaction = dateRedaction;
		this.lu = lu;
		this.lePraticien = lePraticien;
		this.leVisiteur = leVisiteur;
		this.leMotif = leMotif;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getBilan() {
		return bilan;
	}

	public void setBilan(String bilan) {
		this.bilan = bilan;
	}

	public String getCoefConfiance() {
		return coefConfiance;
	}

	public void setCoefConfiance(String coefConfiance) {
		this.coefConfiance = coefConfiance;
	}

	public GregorianCalendar getDateVisite() {
		return dateVisite;
	}

	public void setDateVisite(GregorianCalendar dateVisite) {
		this.dateVisite = dateVisite;
	}

	public GregorianCalendar getDateRedaction() {
		return dateRedaction;
	}

	public void setDateRedaction(GregorianCalendar dateRedaction) {
		this.dateRedaction = dateRedaction;
	}

	public boolean isLu() {
		return lu;
	}

	public void setLu(boolean lu) {
		this.lu = lu;
	}

	public Praticien getLePraticien() {
		return lePraticien;
	}

	public void setLePraticien(Praticien lePraticien) {
		this.lePraticien = lePraticien;
	}

	public Visiteur getLeVisiteur() {
		return leVisiteur;
	}

	public void setLeVisiteur(Visiteur leVisiteur) {
		this.leVisiteur = leVisiteur;
	}

	public Motif getLeMotif() {
		return leMotif;
	}

	public void setLeMotif(Motif leMotif) {
		this.leMotif = leMotif;
	}

	@Override
	public String toString() {

		int jour = this.getDateVisite().get(Calendar.DAY_OF_MONTH);
		int mois = this.getDateVisite().get(Calendar.MONTH);
		int annee = this.getDateVisite().get(Calendar.YEAR);

		return "Date visite : " + jour + "/" + mois + "/" + annee + " \nLe praticien : " + this.lePraticien.getNom() + " "+ this.lePraticien.getPrenom()  ;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.numero);
		dest.writeString(this.bilan);
		dest.writeString(this.coefConfiance);
		dest.writeValue(this.dateVisite);
		dest.writeValue(this.dateRedaction);
		dest.writeInt(this.lu ? 1 : 0);
		dest.writeValue(lePraticien);
		dest.writeValue(this.leMotif);

	}

	public RapportVisite( Parcel in){
		this.numero = in.readInt();
		this.bilan = in.readString();
		this.coefConfiance = in.readString();
		this.dateVisite = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
		this.dateRedaction = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
		this.lu = in.readInt() == 1;
		this.lePraticien = (Praticien) in.readValue(Praticien.class.getClassLoader());
		this.leMotif = (Motif) in.readValue(Motif.class.getClassLoader());
	}

	public static final Creator<RapportVisite> CREATOR = new Creator<RapportVisite>() {

		public RapportVisite createFromParcel(Parcel source){
			return new RapportVisite(source);
		}

		public RapportVisite[] newArray(int size){
			return new RapportVisite[size];
		}
	};
}
