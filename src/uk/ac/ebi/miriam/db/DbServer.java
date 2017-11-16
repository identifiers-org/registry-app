package uk.ac.ebi.miriam.db;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 20/03/14
 *         Time: 12:29
 */
public class DbServer {

    //TODO: Remove this once the DB is in the new infrastrcture
    public static String getDbServerText(){
        String datacenter = System.getenv("DATACENTRE");
        //for local testing
        if(datacenter == null){
            return "hx";
        }
        else {
            return datacenter;
        }
    }
}
