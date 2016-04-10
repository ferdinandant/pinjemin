package pinjem.pinjemin;

/**
 * Created by K-A-R on 08/04/2016.
 */
public class PostSupply {
    String namaBarang;
    String deskripsi;
    String tanggal;

    public PostSupply(String namaBarang, String deskripsi, String tanggal) {
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
