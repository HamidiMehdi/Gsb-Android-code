package visiteur.gsb.fr.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicament implements Parcelable{

	private String depotLegal ;
	private String nomCommercial ;
	
	public Medicament(String depotLegal, String nomCommercial) {
		super();
		this.depotLegal = depotLegal;
		this.nomCommercial = nomCommercial;
	}

	public Medicament() {
		super();
	}

	public String getDepotLegal() {
		return depotLegal;
	}

	public void setDepotLegal(String depotLegal) {
		this.depotLegal = depotLegal;
	}

	public String getNomCommercial() {
		return nomCommercial;
	}

	public void setNomCommercial(String nomCommercial) {
		this.nomCommercial = nomCommercial;
	}

	@Override
	public String toString() {
		return "Medicament : "+ nomCommercial ;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.depotLegal);
		dest.writeString(this.nomCommercial);
	}

	public Medicament( Parcel in){
		this.depotLegal = in.readString();
		this.nomCommercial = in.readString();
	}

	public static final Creator<Medicament> CREATOR = new Creator<Medicament>() {

		public Medicament createFromParcel(Parcel source){
			return new Medicament(source);
		}

		public Medicament[] newArray(int size){
			return new Medicament[size];
		}
	};

	
}
