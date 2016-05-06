/** ===================================================================================
 * [POST SUPPLY]
 * Kelas yang merepresentasikan instance sebuah post penawaran
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.model;

public class PostSupply
{
	private String pid;
	private String uid;
	private String timestamp;
	private String namaBarang;
	private String deskripsi;
	private String harga;
	private String accountName;

	public PostSupply(String pid, String uid, String timestamp, String namaBarang, String deskripsi, String harga, String accountName) {
		this.pid = pid;
		this.uid = uid;
		this.timestamp = timestamp;
		this.namaBarang = namaBarang;
		this.deskripsi = deskripsi;
		this.harga = harga;
		this.accountName = accountName;
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
}
