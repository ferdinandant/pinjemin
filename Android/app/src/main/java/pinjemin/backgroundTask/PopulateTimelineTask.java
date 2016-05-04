/** ===================================================================================
 * [POPULATE TIMELINE TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas CreatePostDemandActivity, CreatePostSupplyActivity
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

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

import pinjemin.adapter.TimelineDemandAdapter;
import pinjemin.adapter.TimelineSupplyAdapter;
import pinjemin.behavior.ClickListener;
import pinjemin.menu_timeline.DetailPostDemandActivity;
import pinjemin.model.PostDemand;
import pinjemin.model.PostSupply;
import pinjemin.utility.UtilityConnection;
import pinjemin.utility.UtilityDate;


public class PopulateTimelineTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_SUPPLY_TIMELINE = "getpenawarantimeline.php";
	public static final String PHP_PATH_DEMAND_TIMELINE = "getpermintaantimeline.php";
	public static final int DEMAND_POST = 1;
	public static final int SUPPLY_POST = 2;

	private Activity activity;
	private Context context;
	private String phpFilePath;
	private int timelineType;

	// bagian RecyclerView:
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// penampung object RecyclerView:
	private ArrayList<PostSupply> arraySupply;
	private ArrayList<PostDemand> arrayDemand;


	/** ==============================================================================
	 * Constructor kelas PopulateTimelineTask
	 * @param context - context dari mana PopulateTimelineTask dipanggil
	 * @param timelineType - DEMAND_POST atau SUPPLY_POST, tergantung jenis
	 * 	timeline yang akan dimintakan ke server.
	 * ============================================================================== */
	public PopulateTimelineTask(Context context, int timelineType, RecyclerView.Adapter adapter) {
		this.context = context;
		this.context = (Activity) context;
		this.timelineType = timelineType;
		this.adapter = adapter;

		// configure file phpFilePath dan array yang benar
		if (timelineType == SUPPLY_POST) {
			this.phpFilePath = PHP_PATH_SUPPLY_TIMELINE;
			this.arraySupply = ((TimelineSupplyAdapter) adapter).getarrayList();
		}
		else if (timelineType == DEMAND_POST) {
			this.phpFilePath = PHP_PATH_DEMAND_TIMELINE;
			this.arrayDemand = ((TimelineDemandAdapter) adapter).getarrayList();
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
			String serverResponse = UtilityConnection.runPhp(phpFilePath, null);
			Log.d("DEBUG", serverResponse);

			// parse data JSON yang diterima dari server (berisi daftar post)
			JSONObject jsonResponseObject = new JSONObject(serverResponse);
			JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("server_response");
			int jsonResponseArrayLength = jsonResponseArray.length();

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

				if (timelineType == SUPPLY_POST) {
					// dapatkan field khusus untuk post supply (harga)
					String dataHarga = postInstance.getString("Harga");
					dataHarga = "Rp" + dataHarga;

					// buat instance PostSupply baru
					PostSupply postSupply = new PostSupply(
						dataPID, dataUID, dataFormattedDate, dataNamaBarang,
						dataDeskripsi, dataHarga, dataRealName);

					// publish perubahan ke main UI thread
					// pemanggilan ini akan memanggil onProgressUpdate() di bawah
					publishProgress(postSupply);
				}
				else if (timelineType == DEMAND_POST) {
					// dapatkan field khusus untuk post demand (lastNeed)
					String dataLastNeed = postInstance.getString("LastNeed");
					dataLastNeed = "Terakhir dibutuhkan "
						+ UtilityDate.formatTimestampDateOnly(dataLastNeed);

					// buat instance PostSupply baru
					PostDemand postDemand = new PostDemand(
						dataPID, dataUID, dataFormattedDate, dataNamaBarang,
						dataDeskripsi, dataLastNeed, dataRealName);

					// publish perubahan ke main UI thread
					// pemanggilan ini akan memanggil onProgressUpdate() di bawah
					publishProgress(postDemand);
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
		if (timelineType == SUPPLY_POST) {
			// tambahkan instance PostSupply ke arraySupply
			// notify adapter bahwa datanya sudah berubah (supaya di-relayout)
			arraySupply.add((PostSupply) object[0]);
			adapter.notifyDataSetChanged();
		}
		else if (timelineType == DEMAND_POST) {
			// tambahkan instance PostSupply ke arraySupply
			// notify adapter bahwa datanya sudah berubah (supaya di-relayout)
			arrayDemand.add((PostDemand) object[0]);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas TimelineDemandFragment.ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineDemandFragment ditekan
	 * ============================================================================== */
	private class TimelineDemandFragmentListener implements ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			Intent intent = new Intent(context, DetailPostDemandActivity.class);

			// dapatkan instance post yang dipilih
			PostDemand postDemand = arrayDemand.get(position);

			// passing data post yang akan ditampilkan ke intent
			intent.putExtra("pid", postDemand.getPid());
			intent.putExtra("uid", postDemand.getUid());
			intent.putExtra("timestamp", postDemand.getTimestamp());
			intent.putExtra("namaBarang", postDemand.getNamaBarang());
			intent.putExtra("deskripsi", postDemand.getDeskripsi());
			intent.putExtra("lastNeed", postDemand.getBatasAkhir());
			intent.putExtra("accountName", postDemand.getAccountName());

			// start activity DetailPostDemandActivity
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {}
	}
}