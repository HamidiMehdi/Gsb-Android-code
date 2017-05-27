package visiteur.gsb.fr.Technique;

import visiteur.gsb.fr.Entities.Visiteur;

/**
 * Created by eleve on 20/02/17.
 */
public class Session {

    private static Session session = null ;
    private Visiteur leVisiteur = null  ;


    private Session (Visiteur visiteur){
        this.leVisiteur = visiteur ;
    }

    public static void ouvrir(Visiteur visiteur){
        Session.session = new Session(visiteur);
    }

    public static void fermer(){
        session = null ;
    }


    public Visiteur getLeVisiteur() {
        return leVisiteur;
    }

    public static Session getSession() {
        return session;
    }
}
