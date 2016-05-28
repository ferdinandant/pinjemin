/** ===================================================================================
 * [POST PEMINJAMAN]
 * Kelas yang merepresentasikan instance sebuah post peminjaman
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.model;

public class PostPeminjaman
{

	private String pid;
	private String uid;
	private String uidPemberi;
	private String uidPenerima;

	private String timestamp;
	private String timestampMulai;
	private String timestampKembali;
	private String deadline;

	private String status;
	private String review;
	private String rating;

	private String namaBarang;
	private String deskripsi;

	private String realname;
	private String realnamePemberi;
	private String realnamePenerima;

	private String lastneed;
	private String harga;

	private int unreadCount;

	public PostPeminjaman(String pid, String uid, String uidPemberi, String uidPenerima, String timestamp, String timestampMulai, String timestampKembali, String deadline, String status, String review, String rating, String namaBarang, String deskripsi, String realname, String realnamePemberi, String realnamePenerima, String lastneed, String harga) {
		this.pid = pid;
		this.uid = uid;
		this.uidPemberi = uidPemberi;
		this.uidPenerima = uidPenerima;
		this.timestamp = timestamp;
		this.timestampMulai = timestampMulai;
		this.timestampKembali = timestampKembali;
		this.deadline = deadline;
		this.status = status;
		this.review = review;
		this.rating = rating;
		this.namaBarang = namaBarang;
		this.deskripsi = deskripsi;
		this.realname = realname;
		this.realnamePemberi = realnamePemberi;
		this.realnamePenerima = realnamePenerima;
		this.lastneed = lastneed;
		this.harga = harga;
		this.unreadCount = 0;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}


	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUidPemberi() {
		return uidPemberi;
	}

	public void setUidPemberi(String uidPemberi) {
		this.uidPemberi = uidPemberi;
	}

	public String getUidPenerima() {
		return uidPenerima;
	}

	public void setUidPenerima(String uidPenerima) {
		this.uidPenerima = uidPenerima;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestampMulai() {
		return timestampMulai;
	}

	public void setTimestampMulai(String timestampMulai) {
		this.timestampMulai = timestampMulai;
	}

	public String getTimestampKembali() {
		return timestampKembali;
	}

	public void setTimestampKembali(String timestampKembali) {
		this.timestampKembali = timestampKembali;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getDeskripsi() {
		return deskripsi;
	}

	public void setDeskripsi(String deskripsi) {
		this.deskripsi = deskripsi;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getRealnamePemberi() {
		return realnamePemberi;
	}

	public void setRealnamePemberi(String realnamePemberi) {
		this.realnamePemberi = realnamePemberi;
	}

	public String getRealnamePenerima() {
		return realnamePenerima;
	}

	public void setRealnamePenerima(String realnamePenerima) {
		this.realnamePenerima = realnamePenerima;
	}

	public String getLastneed() {
		return lastneed;
	}

	public void setLastneed(String lastneed) {
		this.lastneed = lastneed;
	}

	public String getHarga() {
		return harga;
	}

	public void setHarga(String harga) {
		this.harga = harga;
	}
}
