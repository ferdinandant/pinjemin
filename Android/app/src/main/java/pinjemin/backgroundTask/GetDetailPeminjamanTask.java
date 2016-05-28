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

import pinjemin.menu_peminjaman.DetailPostPeminjamanActivity;
import pinjemin.model.PostPeminjaman;
import pinjemin.utility.UtilityConnection;


/**
 * Created by K-A-R on 30/04/2016.
 */
public class GetDetailPeminjamanTask extends AsyncTask<Void,Object,Void>
{

	private Context context;
	private Activity activity;
	private TreeMap<String,String> input;

	private String phpFilePath = "getpeminjamandetail.php";

	private PostPeminjaman postPeminjaman;


	public GetDetailPeminjamanTask(Context context, TreeMap<String,String> input) {
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

				String PID = postInstance.getString("PID");
				String UIDPemberi = postInstance.getString("UIDPemberi");
				String UIDPenerima = postInstance.getString("UIDPenerima");
				String Deadline = postInstance.getString("Deadline");
				String TimestampMulai = postInstance.getString("TimestampMulai");
				String TimestampKembali = postInstance.getString("TimestampKembali");
				String Rating = postInstance.getString("Rating");
				String Review = postInstance.getString("Review");
				String Status = postInstance.getString("Status");
				String UID = postInstance.getString("UID");
				String Timestamp = postInstance.getString("Timestamp");
				String NamaBarang = postInstance.getString("NamaBarang");
				String Deskripsi = postInstance.getString("Deskripsi");
				String RealName = postInstance.getString("RealName");
				String RealNamePemberi = postInstance.getString("RealNamePemberi");
				String RealNamePenerima = postInstance.getString("RealNamePenerima");

				postPeminjaman = new PostPeminjaman(PID, UID, UIDPemberi, UIDPenerima,
					Timestamp, TimestampMulai, TimestampKembali, Deadline,
					Status, Review, Rating, NamaBarang, Deskripsi, RealName, RealNamePemberi, RealNamePenerima, null, null);

				publishProgress(postPeminjaman);
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
	 * =============================================================================== */
	@Override
	protected void onProgressUpdate(Object... object) {
		postPeminjaman = (PostPeminjaman) object[0];
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		Intent intent = new Intent(context, DetailPostPeminjamanActivity.class);

		// dapatkan instance post yang dipilih

		// sisipkan data post yang akan ditampilkan ke intent
		// passing data post yang akan ditampilkan ke intent
		intent.putExtra("UID", postPeminjaman.getUid());
		intent.putExtra("UIDPemberi", postPeminjaman.getUidPemberi());
		intent.putExtra("UIDPenerima", postPeminjaman.getUidPenerima());
		intent.putExtra("RealName", postPeminjaman.getRealname());
		intent.putExtra("Timestamp", postPeminjaman.getTimestamp());
		intent.putExtra("NamaBarang", postPeminjaman.getNamaBarang());
		intent.putExtra("RealNamePemberi", postPeminjaman.getRealnamePemberi());
		intent.putExtra("RealNamePenerima", postPeminjaman.getRealnamePenerima());
		intent.putExtra("Status", postPeminjaman.getStatus());
		intent.putExtra("Status", postPeminjaman.getStatus());
		intent.putExtra("Deskripsi", postPeminjaman.getDeskripsi());
		intent.putExtra("Deadline", postPeminjaman.getDeadline());
		intent.putExtra("PID", postPeminjaman.getPid());

		// start activity DetailPostDemandActivity
		context.startActivity(intent);
	}

}
