/** ===================================================================================
 * [LOGIN TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas LoginActivity
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.TreeMap;

import pinjemin.activity.MainActivity;
import pinjemin.activity.RegisterActivity;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;


public class LoginTask extends AsyncTask<Void,Void,Void>
{
	public static final String PHP_PATH = "login.php";

	private Context context;
	private TreeMap<String,String> dataToSend;
	private boolean isLoginSuccessful;
	private JSONObject jsonResponseObject;
	private JSONArray jsonResponseArray;

	public LoginTask(Context context, TreeMap<String,String> dataToSend) {
		this.dataToSend = dataToSend;
		this.context = context;
		this.isLoginSuccessful = false;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... voids) {
		try {
			// koneksi ke server, kirimkan data login
			// login berhasil: server mengembalikan data user
			// login gagal: server mengembalikan empty set
			String serverResponse = UtilityConnection.runPhp(PHP_PATH, dataToSend);

			// parse data JSON yang diterima dari server
			jsonResponseObject = new JSONObject(serverResponse);
			jsonResponseArray = jsonResponseObject.getJSONArray("server_response");

			// jika ada data user yang dikembalikan, login berhasil
			if (jsonResponseArray.length() > 0) {
				isLoginSuccessful = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SETELAH doInBackground selesai dijalankan
	 * ============================================================================== */
	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);

		if (isLoginSuccessful) {
			try {
				// initialize semua data pada kelas SessionManager
				// (Data user pada SessionManager bersifat statis, ada beberapa yang tidak)
				SessionManager sessionManager = new SessionManager(context);

				// ambil data user
				JSONObject userData = jsonResponseArray.getJSONObject(0);
				String userUID = userData.getString("UID");
				String userAccountName = userData.getString("AccountName");
				String userRealName = userData.getString("RealName");

				// cek apakah user perlu melakukan registrasi awal
				// (perlu jika nilai "RealName" belum dimasukkan di database server)
				if (!userRealName.equalsIgnoreCase("")) {
					// user sudah pernah registrasi
					// masukkan {userUID,userAccountName,userRealName} ke sessionManager
					sessionManager.createLoginSession(userUID, userAccountName);
					sessionManager.createRegisterSession(userRealName);

					// bawa user ke Main Activity
					context.startActivity(new Intent(context, MainActivity.class));
				}
				else {
					// user belum pernah registrasi
					// masukkan {userUID,userAccountName} ke sessionManager
					sessionManager.createLoginSession(userUID, userAccountName);

					// bawa user ke RegisterActivity
					context.startActivity(new Intent(context, RegisterActivity.class));
				}
			}
			catch (Exception e) {
				// terpaksa harus catch exception
				// seharusnya tidak akan masuk ke sini.
				e.printStackTrace();
			}
		}
		else {
			// jika login gagal, tampilkan pesan "login failed."
			Toast.makeText(context, "Login gagal.", Toast.LENGTH_LONG).show();
		}
	}

}
