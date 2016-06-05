/** ===================================================================================
 * [SESSION MANAGER]
 * Static class yang menyimpan data session user (status login, uid, username, realname)
 * ------------------------------------------------------------------------------------
 * @author Kemal Amru Ramadhan
 * @refactor Ferdinand Antonius
 * =================================================================================== */

package pinjemin.session;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import pinjemin.activity.LoginActivity;
import pinjemin.activity.MainActivity;


public class SessionManager
{
	// SharedPreference berisi session data (UID, Username, etc.)
	// Editor adalah interface untuk mengubah data pada SharedPreference
	SharedPreferences sharedPreference;
	Editor editor;
	Context context;

	// Filename yang akan dipakai untuk menyimpan (backup) SharedPreference
	public static final String PREF_FILENAME = "Pinjemin";

	// field-field yang digunakan dalam SharedPreferences
	public static final String IS_LOGIN = "IsLoggedIn";
	public static final String KEY_UID = "uid";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_REALNAME = "realname";


	/** ==============================================================================
	 * Constructor SessionManager
	 * @param context - context dari Activity saat ini (context merepresentasikan
	 *   environment data, dan menyediakan interface untuk database).
	 * ============================================================================== */
	public SessionManager(Context context) {
		// MODE_PRIVATE menunjukkan privilege: file preferences hanya bisa diakses oleh aplikasi pemanggil
		// ---
		// getSharedPreferences() akan mengecek apakah PREF_FILENAME sudah ada; kalau belum, dia akan
		// membuat file baru. Jika file-nya sudah ada, pemanggilan getSharedPreferences() akan me-load
		// preferences yang disimpan di file tersebut.
		this.context = context;
		this.sharedPreference = this.context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
		this.editor = sharedPreference.edit();
	}

	/** ==============================================================================
	 * Mengecek apakah user dalam keadaan login
	 * @return true jika user dalam keadaan login, false jika tidak.
	 * ============================================================================== */
	public boolean isLoggedIn() {
		// ambil data IS_LOGIN dari sharedPreference (dengan default value false)
		// call syntax: sharedPreference.getBoolean(key, defaultValue)
		return sharedPreference.getBoolean(IS_LOGIN, false);
	}

	/** ==============================================================================
	 * Membuat login session baru
	 * @param uid - UID user (di-fetch dari database)
	 * @param username - nama akun JUITA user (e.g. ferdinand.antonius)
	 * ============================================================================== */
	public void createLoginSession(String uid, String username) {
		// Store login value as TRUE
		editor.putBoolean(IS_LOGIN, true);

		// simpan uid dan username
		editor.putString(KEY_UID, uid);
		editor.putString(KEY_USERNAME, username);

		// commit changes
		editor.commit();
	}

	/** ==============================================================================
	 * Melengkapkan data session user pada saat register
	 * @param realname - nama asli user (e.g. Ferdinand Antonius)
	 * ============================================================================== */
	public void createRegisterSession(String realname) {
		editor.putString(KEY_REALNAME, realname);

		// commit changes
		editor.commit();
	}

	/** ==============================================================================
	 * Mengubah data session user pada saat register / ubah profil
	 * @param realname - nama asli user (e.g. Ferdinand Antonius)
	 * ============================================================================== */
	public void ubahProfilSession(String realname) {
		editor.remove(KEY_REALNAME);
		editor.putString(KEY_REALNAME, realname);

		// commit changes
		editor.commit();
	}

	/** ==============================================================================
	 * Mengecek status login user. Jika user tidak dalam keadaan login, user akan
	 * dialihkan ke halaman login.
	 * ============================================================================== */
	public void checkLogin() {
		if (!this.isLoggedIn()) {
			// intent menunjuk ke activity ActivityLogin
			Intent intent = new Intent(context, LoginActivity.class);
			// Closing all the Activities
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// tampilkan LoginActivity
			context.startActivity(intent);
		}
	}

	/** ==============================================================================
	 * Mendapatkan detail user
	 * @return sebuah hashmap, dengan key {uid, username, realname}
	 * ============================================================================== */
	public HashMap<String,String> getUserDetails() {
		HashMap<String,String> userDetails = new HashMap<String,String>();

		// masukan data uid, username, dan realname ke userDetails
		userDetails.put(KEY_UID, sharedPreference.getString(KEY_UID, null));
		userDetails.put(KEY_USERNAME, sharedPreference.getString(KEY_USERNAME, null));
		userDetails.put(KEY_REALNAME, sharedPreference.getString(KEY_REALNAME, null));

		// kembalikkan userData
		return userDetails;
	}

	/** ==============================================================================
	 * Melakukan logout (menghapus session details)
	 * ============================================================================== */
	public void logoutUser() {
		// hapus semua data yang tersimpan pada sharedVariable,
		// dan commit perubahan ini.
		editor.clear();
		editor.commit();

		// setelah logout, redirect user ke LoginActivity
		Intent intent = new Intent(context, LoginActivity.class);

		// NOTE: FLAG_ACTIVITY_CLEAR_TOP: Tutup semua activity di atas activity ini
		// (e.g. jika history activity-nya A->B->C->D, dan D memanggil activity B dengan
		// FLAG_ACTIVITY_CLEAR_TOP di-set, hasil akhir history-nya: A->B)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// NOTE: FLAG_ACTIVITY_NEW_TASK: buat activity history baru, dengan activity
		// ini berada di paling atas ("paling terakhir dijalankan pada history").
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// tampilkan LoginActivity
		context.startActivity(intent);
	}
}