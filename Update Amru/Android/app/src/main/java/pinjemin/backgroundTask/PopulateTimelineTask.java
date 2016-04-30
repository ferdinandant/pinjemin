/** ===================================================================================
 * [POPULATE TIMELINE TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas CreatePostDemand, CreatePostSupply
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
import pinjemin.model.PostDemand;
import pinjemin.model.PostSupply;
import pinjemin.R;
import pinjemin.timeline.DetailPostDemand;
import pinjemin.timeline.DetailPostSupply;
import pinjemin.timeline.TimelineDemandFragment;
import pinjemin.timeline.TimelineSupplyFragment;
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
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// penampung object RecyclerView:
	private String[] inputReceive;
	private ArrayList<PostSupply> arraySupply;
	private ArrayList<PostDemand> arrayDemand;


	/** ==============================================================================
	 * Constructor kelas PopulateTimelineTask
	 * @param context - context dari mana PopulateTimelineTask dipanggil
	 * @param timelineType - DEMAND_POST atau SUPPLY_POST, tergantung jenis
	 * 	timeline yang akan dimintakan ke server.
	 * ============================================================================== */
	public PopulateTimelineTask(Context context, int timelineType) {
		this.context = context;
		this.activity = (Activity) context;
		this.timelineType = timelineType;

		// configure file phpFilePath yang benar
		if (timelineType == SUPPLY_POST) {
			this.phpFilePath = PHP_PATH_SUPPLY_TIMELINE;
		}
		else if (timelineType == DEMAND_POST) {
			this.phpFilePath = PHP_PATH_DEMAND_TIMELINE;
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SEBELUM subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected void onPreExecute() {
		// configure layoutManager
		layoutManager = new LinearLayoutManager(context);

		if (timelineType == SUPPLY_POST) {
			// create array, configure adapter
			arraySupply = new ArrayList<>();
			adapter = new TimelineSupplyAdapter(arraySupply);

			// configure RecyclerView
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewSupply);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new TimelineSupplyFragment.RecyclerTouchListener
					(context, recyclerView, new TimelineSupplyFragmentListener()));
		}

		else if (timelineType == DEMAND_POST) {
			// create array, configure adapter
			arrayDemand = new ArrayList<>();
			adapter = new TimelineDemandAdapter(arrayDemand);

			// configure recycler view
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewDemand);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new TimelineDemandFragment.RecyclerTouchListener
					(context, recyclerView, new TimelineDemandFragmentListener()));
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			// kirim permintaan ke server, tanpa mengirimkan parameter apa pun
			String serverResponse = UtilityConnection.runPhp(phpFilePath, null);

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
				String dataAccountName = postInstance.getString("AccountName");
				String dataFormattedDate = UtilityDate.formatPostTimestamp(dataTimestamp);

				if (timelineType == SUPPLY_POST) {
					// dapatkan field khusus untuk post supply (harga)
					String dataHarga = postInstance.getString("Harga");
					dataHarga = "Rp" + dataHarga;

					// buat instance PostSupply baru
					PostSupply postSupply = new PostSupply(
						dataUID, dataFormattedDate, dataNamaBarang,
						dataDeskripsi, dataHarga, dataAccountName);

					// publish perubahan ke main UI thread
					// pemanggilan ini akan memanggil onProgressUpdate() di bawah
					publishProgress(postSupply);
				}
				else if (timelineType == DEMAND_POST) {
					// dapatkan field khusus untuk post demand (lastNeed)
					String dataLastNeed = postInstance.getString("LastNeed");
					dataLastNeed = "Terakhir dibutuhkan " + dataLastNeed;

					// buat instance PostSupply baru
					PostDemand postDemand = new PostDemand(
						dataUID, dataFormattedDate, dataNamaBarang,
						dataDeskripsi, dataLastNeed, dataAccountName);

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
	private class TimelineDemandFragmentListener implements TimelineDemandFragment.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			Intent intent = new Intent(context, DetailPostDemand.class);

			// dapatkan instance post yang dipilih
			PostDemand postDemand = arrayDemand.get(position);

			// passing data post yang akan ditampilkan ke intent
			intent.putExtra("uid", postDemand.getUid());
			intent.putExtra("timestamp", postDemand.getTimestamp());
			intent.putExtra("namaBarang", postDemand.getNamaBarang());
			intent.putExtra("deskripsi", postDemand.getDeskripsi());
			intent.putExtra("lastNeed", postDemand.getBatasAkhir());
			intent.putExtra("accountName", postDemand.getAccountName());

			// start activity DetailPostDemand
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {}
	}

	/** ==============================================================================
	 * Custom implementation kelas TimelineSupplyFragment.ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineSupplyFragment ditekan
	 * ============================================================================== */
	private class TimelineSupplyFragmentListener implements TimelineSupplyFragment.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			Intent intent = new Intent(context, DetailPostSupply.class);

			// dapatkan instance post yang
			PostSupply postSupply = arraySupply.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			intent.putExtra("uid", postSupply.getUid());
			intent.putExtra("timestamp", postSupply.getTimestamp());
			intent.putExtra("namaBarang", postSupply.getNamaBarang());
			intent.putExtra("deskripsi", postSupply.getDeskripsi());
			intent.putExtra("harga", postSupply.getHarga());
			intent.putExtra("accountName", postSupply.getAccountName());

			// start activity DetailPostSupply
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {}
	}
}