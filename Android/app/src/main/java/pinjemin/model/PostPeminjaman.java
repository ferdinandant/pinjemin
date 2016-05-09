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

	private String uidPeminjam;
	private String uidPemberi;
	private String timestamp;
	private String namaBarang;
	private String deskripsi;
	private String harga;
	private String accountName;
	private String deadline;
	private String status;


	/** ==============================================================================
	 * Constructor kelas PostPeminjaman
	 * ============================================================================== */
	public PostPeminjaman(String uidPeminjam, String uidPemberi,
		String timestamp, String namaBarang, String deskripsi,
		String harga, String accountName, String deadline, String status
	) {
		this.uidPeminjam = uidPeminjam;
		this.uidPemberi = uidPemberi;
		this.timestamp = timestamp;
		this.namaBarang = namaBarang;
		this.deskripsi = deskripsi;
		this.harga = harga;
		this.accountName = accountName;
		this.deadline = deadline;
		this.status = status;
	}


	// --- setter & getter methods ---

	public String getUidPeminjam() {
		return uidPeminjam;
	}

	public void setUidPeminjam(String uidPeminjam) {
		this.uidPeminjam = uidPeminjam;
	}

	public String getUidPemberi() {
		return uidPemberi;
	}

	public void setUidPemberi(String uidPemberi) {
		this.uidPemberi = uidPemberi;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
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

	public String getHarga() {
		return harga;
	}

	public void setHarga(String harga) {
		this.harga = harga;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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
}
