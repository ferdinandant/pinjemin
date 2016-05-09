/** ===================================================================================
 * [UBAH PROFIL SUBMIT  TASK]
 * Modifikasi RegisterTask. Yang diubah: agar tidak perlu lagi start MainActivity.
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.TreeMap;

import pinjemin.activity.MainActivity;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;


public class UbahProfilSubmitTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH = "register.php";

	private Context context;
	private TreeMap<String,String> dataToSend;
	private String ubah;

	/** ==============================================================================
	 * Constructor kelas UbahProfilSubmitTask
	 * @param context - context dari mana RegisterTask dipanggil
	 * @param dataToSend - TreeMap atau HashMap, berisi (key,value) pair data yang
	 *   akan dikirim ke server.
	 * ============================================================================== */
	public UbahProfilSubmitTask(Context context, TreeMap<String,String> dataToSend) {
		this.context = context;
		this.dataToSend = dataToSend;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		// kirim data yang dimasukkan saat registrasi ke database
		try {
			String hasil = UtilityConnection.runPhp(PHP_PATH, dataToSend);
			Log.d("Server response", hasil);
		}
		catch (IOException e) {
			Log.e("RegisterTask", "Tried accessing host: " + PHP_PATH);
			e.printStackTrace();
		}

		return null;
	}

	@Override
	/** ==============================================================================
	 * Hal yang perlu dilakukan SETELAH doInBackground selesai dijalankan
	 * ============================================================================== */
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);

		// dapatkan nama asli user dari data
		String realname = dataToSend.get("realname");

		SessionManager session = new SessionManager(context);

		if (dataToSend.get("ubah") == null) {
			// masukkan data username ke SessionManager
			session.createRegisterSession(realname);
		}
		else {
			session.ubahProfilSession(realname);
		}
	}

	@Override
	protected void onPreExecute() {}

	@Override
	protected void onProgressUpdate(Object... object) {}

}