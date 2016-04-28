/** ===================================================================================
 * [REGISTER TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas RegisterActivity
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.TreeMap;

import pinjemin.activity.MainActivity;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;


public class RegisterTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH = "register.php";

	private Context context;
	private TreeMap<String,String> dataToSend;

	public RegisterTask(Context context, TreeMap<String,String> dataToSend) {
		this.context = context;
		this.dataToSend = dataToSend;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		// kirim data yang dimasukkan saat registrasi ke database
		UtilityConnection.runPhp(PHP_PATH, dataToSend);
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

		// masukkan data username ke SessionManager
		SessionManager session = new SessionManager(context);
		session.createRegisterSession(realname);

		// tampilkan MainActivity
		context.startActivity(new Intent(context, MainActivity.class));
	}

	@Override
	protected void onPreExecute() {}

	@Override
	protected void onProgressUpdate(Object... object) {}

}