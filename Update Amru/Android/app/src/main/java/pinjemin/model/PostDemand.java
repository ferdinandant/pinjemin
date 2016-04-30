/** ===================================================================================
 * [POST DEMAND]
 * Kelas yang merepresentasikan instance sebuah post permintaan
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.model;

public class PostDemand
{
	private String uid;
	private String timestamp;
	private String namaBarang;
	private String deskripsi;
	private String batasAkhir;
	private String accountName;


	/** ==============================================================================
	 * Constructor kelas PostDemand
	 * ============================================================================== */
	public PostDemand(String uid, String timestamp, String namaBarang,
		String deskripsi, String batasAkhir, String accountName
	) {
		this.uid = uid;
		this.timestamp = timestamp;
		this.namaBarang = namaBarang;
		this.deskripsi = deskripsi;
		this.batasAkhir = batasAkhir;
		this.accountName = accountName;
	}


	// --- setter & getter methods ---

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

	public String getBatasAkhir() {
		return batasAkhir;
	}

	public void setBatasAkhir(String batasAkhir) {
		this.batasAkhir = batasAkhir;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
