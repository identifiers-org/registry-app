package uk.ac.ebi.miriam.db;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 14/08/2014
 *         Time: 12:42
 */
public class Format {
    private int id;
    private String urlPrefix = new String();
    private String urlSuffix = new String();
    private MimeType mimeType;
    private int deprecated;
    private int mimeTypeId;

    public Format() {
    }

    public Format(int id, String urlPrefix, String urlSuffix, MimeType mimeType, int deprecated) {
        this.id = id;
        this.urlPrefix = urlPrefix;
        this.urlSuffix = urlSuffix;
        this.mimeType = mimeType;
        this.deprecated = deprecated;
        this.mimeTypeId = mimeType.getId();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public void setDeprecated(int deprecated) {
        this.deprecated = deprecated;
    }

    public void setMimeTypeId(int mimeTypeId) {
        this.mimeTypeId = mimeTypeId;
    }

    public int getId() {
        return id;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public int getDeprecated() {
        return deprecated;
    }

    public int getMimeTypeId() {
        return mimeTypeId;
    }

    public boolean hasSameContent(Format format){
        return ((this.urlPrefix.equals(format.urlPrefix)) &&
                (this.urlSuffix.equals(format.urlSuffix)) &&
                (this.mimeTypeId == format.mimeTypeId));
    }

    public String toString(){
        StringBuilder tmp = new StringBuilder();
        tmp.append("       - URL prefix:  " + getUrlPrefix() + "\n");
        tmp.append("       - URL suffix:  " + getUrlSuffix() + "\n");
        tmp.append("       - MimeType id: "+ getMimeTypeId()+"\n");
        tmp.append("       - Deprecated: " + getDeprecated() + "\n");
        return tmp.toString();
    }
}
