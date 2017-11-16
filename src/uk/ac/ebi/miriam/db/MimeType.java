package uk.ac.ebi.miriam.db;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 14/08/2014
 *         Time: 12:49
 */
public class MimeType {
    private int id;
    private String mimetype = new String();
    private String displayText = new String();

    public MimeType(int id, String mimetype, String displayText) {
        this.id = id;
        this.mimetype = mimetype;
        this.displayText = displayText;
    }

    public int getId() {
        return id;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getDisplayText() {
        return displayText;
    }
}
