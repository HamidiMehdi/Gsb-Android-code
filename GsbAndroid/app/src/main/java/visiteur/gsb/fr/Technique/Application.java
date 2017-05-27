package visiteur.gsb.fr.Technique;

/**
 * Created by Mehdi on 16/05/2017.
 */

public class Application {

    private static String ipServeur = "http://192.168.1.61/ServeurWebService/web/app.php" ;
    private static String aPropos = "Mehdi Hamidi | Francois Lenin \n" +
                                    "Lycée Louis Armand \n" +
                                    "2016 - 2017 \n" +
                                    "Web service : Symfony version 2.7";

    public static String getIpServeur() {
        return ipServeur;
    }

    public static String getaPropos() {
        return aPropos;
    }
}
