package uk.ac.ebi.miriam.db;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 06/10/2014
 *         Time: 12:34
 */
public class Ownership {
    int ownership_id;
    String user;
    String resource;
    int status;

    public Ownership() {
    }

    public Ownership(int ownership_id, String user, String resource, int status) {
        this.ownership_id = ownership_id;
        this.user = user;
        this.resource = resource;
        this.status = status;
    }

    public int getOwnership_id() {
        return ownership_id;
    }

    public void setOwnership_id(int ownership_id) {
        this.ownership_id = ownership_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
