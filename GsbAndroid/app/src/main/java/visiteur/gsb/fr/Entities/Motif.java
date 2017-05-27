package visiteur.gsb.fr.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.GregorianCalendar;

public class Motif implements Parcelable{

	private int code ;
	private String libelle ;

    public Motif() {
        super();
    }

	public Motif(int code, String libelle) {
		super();
		this.code = code;
		this.libelle = libelle;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@Override
	public String toString() {
		return  libelle;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.code);
		dest.writeString(this.libelle);
	}

	public Motif( Parcel in){
		this.code = in.readInt();
		this.libelle = in.readString();
	}

	public static final Creator<Motif> CREATOR = new Creator<Motif>() {

		public Motif createFromParcel(Parcel source){
			return new Motif(source);
		}

		public Motif[] newArray(int size){
			return new Motif[size];
		}
	};
}
