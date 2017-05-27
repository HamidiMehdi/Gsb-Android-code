package visiteur.gsb.fr.Entities;

public class Echantillon {

    private Medicament medicament;
    private int quantite;

    public Echantillon(Medicament medicament, int quantite){
        this.medicament = medicament;
        this.quantite = quantite;
    }

    public Medicament getMedicament(){
        return this.medicament;
    }

    public int getQuantite(){
        return this.quantite;
    }

    public void setQuantite(int quantite){
        this.quantite = quantite;
    }


}
