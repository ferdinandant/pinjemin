package pinjemin.backgroundTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import pinjemin.adapter.TimelineDemandAdapter;
import pinjemin.adapter.TimelineSupplyAdapter;
import pinjemin.behavior.ClickListener;
import pinjemin.menu_timeline.DetailPostDemandActivity;
import pinjemin.model.PostDemand;
import pinjemin.model.PostSupply;
import pinjemin.model.User;
import pinjemin.utility.UtilityConnection;
import pinjemin.utility.UtilityDate;


public class SearchTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_SEARCH_DEMAND = "searchpermintaan.php";
	public static final String PHP_PATH_SEARCH_SUPPLY = "searchpenawaran.php";
	public static final String PHP_PATH_SEARCH_USER = "searchuser.php";
	public static final int DEMAND_SEARCH = 0;
	public static final int SUPPLY_SEARCH = 1;
	public static final int USER_SEARCH = 2;

	private Activity activity;
	private Context context;
	private String phpFilePath;
	private int searchType;

	// bagian RecyclerView:
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// penampung object RecyclerView:
	private TreeMap<String,String> dataToSend;
	private ArrayList<PostSupply> arraySupply;
	private ArrayList<PostDemand> arrayDemand;
	private ArrayList<User> arrayUser;


	/** ==============================================================================
	 * Constructor kelas SearchTask
	 * ============================================================================== */
	public SearchTask(Context context, int searchType, TreeMap<String,String> dataToSend,
		RecyclerView.Adapter adapter
	) {
		this.context = context;
		this.activity = (Activity) context;
		this.searchType = searchType;
		this.dataToSend = dataToSend;
		this.adapter = adapter;

		// configure file phpFilePath yang benar
		if (searchType == SUPPLY_SEARCH) {
			this.phpFilePath = PHP_PATH_SEARCH_SUPPLY;
			this.arraySupply = ((TimelineSupplyAdapter) adapter).getarrayList();
		}
		else if (searchType == DEMAND_SEARCH) {
			this.phpFilePath = PHP_PATH_SEARCH_DEMAND;
			this.arrayDemand = ((TimelineDemandAdapter) adapter).getarrayList();
		}
		else if (searchType == USER_SEARCH) {
			this.phpFilePath = PHP_PATH_SEARCH_USER;
			// TODO: adapter user
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SEBELUM subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected void onPreExecute() {
		// configure layoutManager
		layoutManager = new LinearLayoutManager(context);
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			// kirim permintaan ke server, tanpa mengirimkan parameter apa pun
			String serverResponse = UtilityConnection.runPhp(phpFilePath, dataToSend);
			Log.d("DEBUG", serverResponse);

			// parse data JSON yang diterima dari server (berisi daftar post)
			JSONObject jsonResponseObject = new JSONObject(serverResponse);
			JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("server_response");
			int jsonResponseArrayLength = jsonResponseArray.length();

			// --- UNTUK PENCARIAN DEMAND ---
			if (searchType == DEMAND_SEARCH) {
				// reset array biar kosong
				arrayDemand.clear();

				for (int i = 0; i < jsonResponseArrayLength; i++) {
					JSONObject postInstance = jsonResponseArray.getJSONObject(i);

					// extract fields dari postInstance:
					String dataPID = postInstance.getString("PID");
					String dataUID = postInstance.getString("UID");
					String dataTimestamp = postInstance.getString("Timestamp");
					String dataNamaBarang = postInstance.getString("NamaBarang");
					String dataDeskripsi = postInstance.getString("Deskripsi");
					String dataRealName = postInstance.getString("RealName");
					String dataAccountName = postInstance.getString("AccountName");
					String dataFormattedDate = UtilityDate.formatTimestampElapsedTime(dataTimestamp);
					String dataLastNeed = postInstance.getString("LastNeed");
					dataLastNeed = "Terakhir dibutuhkan "
						+ UtilityDate.formatTimestampDateOnly(dataLastNeed);

					// buat instance PostSupply baru
					PostDemand postDemand = new PostDemand(
						dataPID, dataUID, dataFormattedDate, dataNamaBarang,
						dataDeskripsi, dataLastNeed, dataAccountName, dataRealName);

					// publish perubahan ke main UI thread
					// pemanggilan ini akan memanggil onProgressUpdate() di bawah
					publishProgress(postDemand);
				}
			}

			// --- UNTUK PENCARIAN SUPPLY ---
			if (searchType == SUPPLY_SEARCH) {
				// reset array biar kosong
				arraySupply.clear();

				for (int i = 0; i < jsonResponseArrayLength; i++) {
					JSONObject postInstance = jsonResponseArray.getJSONObject(i);

					// extract fields dari postInstance:
					String dataPID = postInstance.getString("PID");
					String dataUID = postInstance.getString("UID");
					String dataTimestamp = postInstance.getString("Timestamp");
					String dataNamaBarang = postInstance.getString("NamaBarang");
					String dataDeskripsi = postInstance.getString("Deskripsi");
					String dataRealName = postInstance.getString("RealName");
					String dataAccountName = postInstance.getString("AccountName");
					String dataFormattedDate = UtilityDate.formatTimestampElapsedTime(dataTimestamp);
					String dataLastNeed = postInstance.getString("LastNeed");
					String dataHarga = postInstance.getString("Harga");
					dataHarga = "Rp" + dataHarga;

					// buat instance PostSupply baru
					PostSupply postSupply = new PostSupply(
						dataPID, dataUID, dataFormattedDate, dataNamaBarang,
						dataDeskripsi, dataHarga, dataAccountName, dataRealName);

					// publish perubahan ke main UI thread
					// pemanggilan ini akan memanggil onProgressUpdate() di bawah
					publishProgress(postSupply);
				}
			}

			// --- UNTUK PENCARIAN USER ---
			if (searchType == USER_SEARCH) {
				for (int i = 0; i < jsonResponseArrayLength; i++) {
					// reset array biar kosong
					arrayUser.clear();

					JSONObject postInstance = jsonResponseArray.getJSONObject(i);

					// extract fields dari postInstance:
					String uid = postInstance.getString("UID");
					String accountName = postInstance.getString("AccountName");
					String realName = postInstance.getString("RealName");

					// buat instance user baru
					// data yang tidak diperlukan diisi null
					User user = new User(uid, accountName, realName,
						null, null, null, null, null, null);

					// publish perubahan ke main UI thread
					// pemanggilan ini akan memanggil onProgressUpdate() di bawah
					publishProgress(user);
				}
			}

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			Log.e("PopulateTimelineTask", "Tried accessing host: " + phpFilePath);
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
		if (searchType == DEMAND_SEARCH) {
			// tambahkan instance PostDemand ke arrayDemand
			// notify adapter bahwa datanya sudah berubah (supaya di-relayout)
			arrayDemand.add((PostDemand) object[0]);
			adapter.notifyDataSetChanged();
			Log.d("DEBUG", "Length:" + arrayDemand.size());
		}
		else if (searchType == SUPPLY_SEARCH) {
			// tambahkan instance PostSupply ke arraySupply
			// notify adapter bahwa datanya sudah berubah (supaya di-relayout)
			arraySupply.add((PostSupply) object[0]);
			adapter.notifyDataSetChanged();
		}
		else if (searchType == USER_SEARCH) {
			// TODO
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {}
}