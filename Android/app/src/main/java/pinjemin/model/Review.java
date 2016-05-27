package pinjemin.model;

/**
 * Created by K-A-R on 23/05/2016.
 */
public class Review {

    private String realName;
    private String review;
    private String rating;
    private String namaBarang;

    public Review(String realName, String review, String rating, String namaBarang) {
        this.realName = realName;
        this.review = review;
        this.rating = rating;
        this.namaBarang = namaBarang;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }
}
