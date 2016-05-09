package pinjemin.backgroundTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeMap;

import pinjemin.model.User;
import pinjemin.user.DetailProfilActivity;
import pinjemin.user.UbahProfilActivity;
import pinjemin.utility.UtilityConnection;


/**
 * Created by K-A-R on 30/04/2016.
 */
public class UbahProfilFetchTask extends AsyncTask<Void,Object,Void>
{
	private Context context;
	private Activity activity;
	private TreeMap<String,String> input;

	private String phpFilePath = "getprofiledetail.php";

	private User targetUser;

	private String status;

	public UbahProfilFetchTask(Context context, TreeMap<String,String> input) {
		this.context = context;
		this.activity = (Activity) context;
		this.input = input;
	}

	@Override
	protected void onPreExecute() {

	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			// kirim permintaan ke server, tanpa mengirimkan parameter apa pun
			String serverResponse = UtilityConnection.runPhp(phpFilePath, input);

			Log.d("Server Response", serverResponse);

			// parse data JSON yang diterima dari server (berisi daftar post)
			JSONObject jsonResponseObject = new JSONObject(serverResponse);
			JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("server_response");
			int jsonResponseArrayLength = jsonResponseArray.length();

			for (int i = 0; i < jsonResponseArrayLength; i++) {
				JSONObject postInstance = jsonResponseArray.getJSONObject(i);

				String uid = postInstance.getString("UID");
				String accountName = postInstance.getString("AccountName");
				String realName = postInstance.getString("RealName");
				String bio = postInstance.getString("Bio");
				String fakultas = postInstance.getString("Fakultas");
				String prodi = postInstance.getString("Prodi");
				String telepon = postInstance.getString("Telepon");
				String totalRating = postInstance.getString("TotalRating");
				String numRating = postInstance.getString("NumRating");
				status = postInstance.getString("Status");

				Double ttlRating = Double.parseDouble(totalRating);
				Double nmRating = Double.parseDouble(numRating);

				Double rating = ttlRating / nmRating;

				User user = new User(uid, accountName, realName, bio, fakultas, prodi, telepon, "" + rating, numRating);

				publishProgress(user);

			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SELAMA subclass AsyncTask ini di-execute
	 * @param object - normalnya berisi instance yang baru saja di-parse dari server
	 * ============================================================================== */
	@Override
	protected void onProgressUpdate(Object... object) {
		targetUser = (User) object[0];
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		Intent intent = new Intent(context, UbahProfilActivity.class);

		// dapatkan instance post yang dipilih
		User user = targetUser;

		// sisipkan data post yang akan ditampilkan ke intent
		// passing data post yang akan ditampilkan ke intent
		intent.putExtra("uid", user.getUid());
		intent.putExtra("realName", user.getRealName());
		intent.putExtra("accountName", user.getAccountName());
		intent.putExtra("bio", user.getBio());
		intent.putExtra("fakultas", user.getFakultas());
		intent.putExtra("prodi", user.getProdi());
		intent.putExtra("telepon", user.getTelepon());

		// start activity DetailPostDemandActivity
		context.startActivity(intent);
	}
}