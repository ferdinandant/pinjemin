/** ===================================================================================
 * [LOGIN TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas LoginActivity (ini untuk login dengan akun pinjemin)
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.TreeMap;

import pinjemin.activity.MainActivity;
import pinjemin.activity.RegisterActivity;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;


public class LoginNormalTask extends AsyncTask<Void,Void,Void>
{
	public static final String PHP_PATH = "login.php";

	private Context context;
	private Activity activity;
	private TreeMap<String,String> dataToSend;
	private JSONObject jsonResponseObject;
	private JSONArray jsonResponseArray;

	private boolean isLoginSuccessful;
	private boolean isServerReachable;

	String userUID;
	String userAccountName;
	String userRealName;

	public LoginNormalTask(Activity activity, TreeMap<String,String> dataToSend) {
		this.dataToSend = dataToSend;
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.isLoginSuccessful = false;
		this.isServerReachable = false;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... voids) {
		try {
			// koneksi ke server, kirimkan data login
			// (isServerReachable == true) berarti server bisa dihubungi
			// login berhasil: server mengembalikan data user
			// login gagal: server mengembalikan empty set
			this.isServerReachable = false;
			String serverResponse = UtilityConnection.runPhp(PHP_PATH, dataToSend);
			Log.d("Login response: ", serverResponse);
			this.isServerReachable = true;

			// parse data JSON yang diterima dari server
			jsonResponseObject = new JSONObject(serverResponse);
			jsonResponseArray = jsonResponseObject.getJSONArray("server_response");

			// jika ada data user yang dikembalikan, login berhasil
			if (jsonResponseArray.length() > 0) {
				isLoginSuccessful = true;

				JSONObject userData = jsonResponseArray.getJSONObject(0);
				userUID = userData.getString("UID");
				userAccountName = userData.getString("AccountName");
				userRealName = userData.getString("RealName");

				Log.d("realname", userRealName);
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

				// cek apakah user perlu melakukan registrasi awal
				// (perlu jika nilai "RealName" belum dimasukkan di database server)
				if (!userRealName.equalsIgnoreCase("")) {
					// user sudah pernah registrasi
					// masukkan {userUID,userAccountName,userRealName} ke sessionManager
					sessionManager.createLoginSession(userUID, userAccountName);
					sessionManager.createRegisterSession(userRealName);

					// bawa user ke Main Activity
					activity.startActivity(new Intent(context, MainActivity.class));
				}
				else {
					// user belum pernah registrasi
					// masukkan {userUID,userAccountName} ke sessionManager
					sessionManager.createLoginSession(userUID, userAccountName);

					// bawa user ke RegisterActivity
					activity.startActivity(new Intent(context, RegisterActivity.class));
				}
			}
			catch (Exception e) {
				// terpaksa harus catch exception
				// seharusnya tidak akan masuk ke sini.
				e.printStackTrace();
			}
		}
		else if (!isServerReachable) {
			// jika server tidak bisa dihubungi
			Toast.makeText(context, "Tidak bisa menghubungi server.", Toast.LENGTH_LONG).show();
		}
		else {
			// jika login gagal, tampilkan pesan "login failed."
			Toast.makeText(context, "Login gagal.", Toast.LENGTH_LONG).show();
		}
	}
}
