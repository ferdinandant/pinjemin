package pinjemin.model;

/**
 * Created by K-A-R on 29/04/2016.
 */
public class User {

    private String uid;
    private String accountName;
    private String realName;
    private String bio;
    private String fakultas;
    private String prodi;
    private String telepon;
    private String rating;
    private String numRating;

    public User(String uid, String accountName, String realName, String bio, String fakultas, String prodi, String telepon, String rating, String numRating) {
        this.uid = uid;
        this.accountName = accountName;
        this.realName = realName;
        this.bio = bio;
        this.fakultas = fakultas;
        this.prodi = prodi;
        this.telepon = telepon;
        this.rating = rating;
        this.numRating = numRating;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) {
        this.prodi = prodi;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNumRating() {
        return numRating;
    }

    public void setNumRating(String numRating) {
        this.numRating = numRating;
    }
}
