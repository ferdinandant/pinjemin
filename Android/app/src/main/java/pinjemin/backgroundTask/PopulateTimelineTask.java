/** ===================================================================================
 * [TIMELINE TASK]
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
	public static String PHP_PATH_SUPPLY_TIMELINE = "getpenawarantimeline.php";
	public static String PHP_PATH_DEMAND_TIMELINE = "getpermintaantimeline.php";

	private static String host = UtilityConnection.HOST_ADDRESS;
	private String json_string;

	private Activity activity;
	private Context context;
	private String path;
	private String objectType;

	//Bagian RecyclerView
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// Penampung Object RecyclerView
	private String[] inputReceive;
	private ArrayList<PostSupply> arraySupply;
	private ArrayList<PostDemand> arrayDemand;

	public PopulateTimelineTask(Context context, String objectType, String[] inputReceive) {
		this.context = context;
		this.activity = (Activity) context;
		this.objectType = objectType;
		this.inputReceive = inputReceive;

		// configure file path yang benar
		if (objectType.equals("postSupply")) {
			this.path = PHP_PATH_SUPPLY_TIMELINE;
		}
		else if (objectType.equals("postDemand")) {
			this.path = PHP_PATH_DEMAND_TIMELINE;
		}
	}

	@Override
	/** ==============================================================================
	 * Hal yang perlu dilakukan SEBELUM subclass AsyncTask ini di-execute
	 * ============================================================================== */
	protected void onPreExecute() {
		if (objectType.equalsIgnoreCase("postSupply")) {
			arraySupply = new ArrayList<>();
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewSupply);

			adapter = new TimelineSupplyAdapter(arraySupply);
			layoutManager = new LinearLayoutManager(context);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			recyclerView.addOnItemTouchListener(
				new TimelineSupplyFragment.RecyclerTouchListener
					(context, recyclerView, new TimelineSupplyFragmentListener()));
		}
		else if (objectType.equalsIgnoreCase("postDemand")) {
			arrayDemand = new ArrayList<>();
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewDemand);
			adapter = new TimelineDemandAdapter(arrayDemand);
			recyclerView.setAdapter(adapter);

			layoutManager = new LinearLayoutManager(context);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			recyclerView.addOnItemTouchListener(
				new TimelineDemandFragment.RecyclerTouchListener
					(context, recyclerView, new TimelineDemandFragmentListener()));
		}
	}

	@Override
	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	protected Void doInBackground(Void... params) {
		try {
			URL url = new URL(host + path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

			InputStream inputStream = httpURLConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			StringBuilder stringBuilder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}

			bufferedReader.close();
			inputStream.close();
			httpURLConnection.disconnect();

			json_string = stringBuilder.toString().trim();

			try {

				JSONObject jsonObject = new JSONObject(json_string);
				JSONArray jsonArray = jsonObject.getJSONArray("server_response");

				String[] input = new String[inputReceive.length];

				int count = 0;
				while (count < jsonArray.length()) {

					JSONObject JO = jsonArray.getJSONObject(count);

					for (int ii = 0; ii < input.length; ii++) {
						input[ii] = JO.getString(inputReceive[ii]);
					}

					if (objectType.equalsIgnoreCase("postSupply")) {

						String formatTanggal = UtilityDate.formatPostTimestamp(input[2]);
						PostSupply postSupply =
							new PostSupply(input[1], formatTanggal, input[3], input[4], "Rp" + input[5], input[6]);
						publishProgress(postSupply);

					}
					else if (objectType.equalsIgnoreCase("postDemand")) {
						String formatTanggal = UtilityDate.formatPostTimestamp(input[2]);
						PostDemand postDemand =
							new PostDemand(input[1], formatTanggal, input[3], input[4], "Terakhir dibutuhkan " + input[5], input[6]);
						publishProgress(postDemand);
					}
					count++;
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

			Log.d("JSON_STRING", json_string);

		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	/** ==============================================================================
	 * Hal yang perlu dilakukan SELAMA subclass AsyncTask ini di-execute
	 * ============================================================================== */
	protected void onProgressUpdate(Object... object) {
		if (objectType.equalsIgnoreCase("postSupply")) {
			arraySupply.add((PostSupply) object[0]);
			adapter.notifyDataSetChanged();

		}
		else if (objectType.equalsIgnoreCase("postDemand")) {
			arrayDemand.add((PostDemand) object[0]);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {}


	// --- inner class declaration ---

	private class TimelineDemandFragmentListener implements TimelineDemandFragment.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			PostDemand postDemand = arrayDemand.get(position);

			Intent intent = new Intent(context, DetailPostDemand.class);
			intent.putExtra("uid", postDemand.getUid());
			intent.putExtra("timestamp", postDemand.getTimestamp());
			intent.putExtra("namaBarang", postDemand.getNamaBarang());
			intent.putExtra("deskripsi", postDemand.getDeskripsi());
			intent.putExtra("lastNeed", postDemand.getBatasAkhir());
			intent.putExtra("accountName", postDemand.getAccountName());
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {

		}
	}

	private class TimelineSupplyFragmentListener implements TimelineSupplyFragment.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			PostSupply postSupply = arraySupply.get(position);

			Intent intent = new Intent(context, DetailPostSupply.class);
			intent.putExtra("uid", postSupply.getUid());
			intent.putExtra("timestamp", postSupply.getTimestamp());
			intent.putExtra("namaBarang", postSupply.getNamaBarang());
			intent.putExtra("deskripsi", postSupply.getDeskripsi());
			intent.putExtra("harga", postSupply.getHarga());
			intent.putExtra("accountName", postSupply.getAccountName());
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {

		}
	}
}