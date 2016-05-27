/**
 * ===================================================================================
 * [POPULATE TIMELINE TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas CreatePostDemand, CreatePostSupply
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * ===================================================================================
 */

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
import java.util.TreeMap;

import pinjemin.R;
import pinjemin.adapter.FriendTemanAndaAdapter;
import pinjemin.adapter.TimelineDemandAdapter;
import pinjemin.adapter.TimelineSupplyAdapter;
import pinjemin.behavior.ClickListener;
import pinjemin.behavior.RecyclerOnItemTouchListener;
import pinjemin.model.PostDemand;
import pinjemin.model.PostSupply;
import pinjemin.model.User;
import pinjemin.menu_search.SearchDemandFragment;
import pinjemin.menu_search.SearchSupplyFragment;
import pinjemin.menu_search.SearchUserFragment;
import pinjemin.menu_search.SearchActivity;
import pinjemin.session.SessionManager;
import pinjemin.menu_timeline.DetailPostDemandActivity;
import pinjemin.menu_timeline.DetailPostSupplyActivity;
import pinjemin.utility.UtilityConnection;
import pinjemin.utility.UtilityDate;


public class SearchTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_SEARCH_DEMAND = "searchpermintaan.php";
	public static final String PHP_PATH_SEARCH_SUPPLY = "searchpenawaran.php";
	public static final String PHP_PATH_SEARCH_USER = "searchuser.php";
	public static final int DEMAND_POST = 1;
	public static final int SUPPLY_POST = 2;
	public static final int USER_POST = 3;

	private Activity activity;
	private Context context;
	private String phpFilePath;
	private int searchType;

	// bagian RecyclerView:
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// penampung object RecyclerView:
	private TreeMap<String,String> input;
	private ArrayList<PostSupply> arraySupply;
	private ArrayList<PostDemand> arrayDemand;
	private ArrayList<User> arrayUser;

	/** ==============================================================================
	 * Constructor kelas PopulateTimelineTask
	 * @param context - context dari mana PopulateTimelineTask dipanggil
	 * @param searchType - DEMAND_POST atau SUPPLY_POST, tergantung jenis
	 * 	timeline yang akan dimintakan ke server.
	 * ============================================================================== */
	public SearchTask(Context context, int searchType, TreeMap<String,String> input) {
		this.context = context;
		this.activity = (Activity) context;
		this.searchType = searchType;
		this.input = input;

		// configure file phpFilePath yang benar
		if (searchType == SUPPLY_POST) {
			this.phpFilePath = PHP_PATH_SEARCH_SUPPLY;
		}
		else if (searchType == DEMAND_POST) {
			this.phpFilePath = PHP_PATH_SEARCH_DEMAND;
		}
		else if (searchType == USER_POST) {
			this.phpFilePath = PHP_PATH_SEARCH_USER;
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SEBELUM subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected void onPreExecute() {
		// configure layoutManager
		layoutManager = new LinearLayoutManager(context);

		if (searchType == SUPPLY_POST) {
			// create array, configure adapter
			arraySupply = new ArrayList<>();
			adapter = new TimelineSupplyAdapter(arraySupply);

			// configure RecyclerView
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewSearchSupply);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new RecyclerOnItemTouchListener
					(context, recyclerView, new SearchSupplyFragmentListener()));
		}
		else if (searchType == DEMAND_POST) {
			// create array, configure adapter
			arrayDemand = new ArrayList<>();
			adapter = new TimelineDemandAdapter(arrayDemand);

			// configure recycler view
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewSearchDemand);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new RecyclerOnItemTouchListener
					(context, recyclerView, new SearchDemandFragmentListener()));
		}
		else if (searchType == USER_POST) {
			arrayUser = new ArrayList<>();
			adapter = new FriendTemanAndaAdapter(arrayUser);

			// configure recycler view
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewSearchUser);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new RecyclerOnItemTouchListener
					(context, recyclerView, new SearchUserFragmentListener()));
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {

			if (searchType == USER_POST) {
				String serverResponse = UtilityConnection.runPhp(phpFilePath, input);

				Log.d("Server Response", serverResponse);

				// parse data JSON yang diterima dari server (berisi daftar post)
				JSONObject jsonResponseObject = new JSONObject(serverResponse);
				JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("server_response");
				int jsonResponseArrayLength = jsonResponseArray.length();

				for (int i = 0; i < jsonResponseArrayLength; i++) {
					JSONObject postInstance = jsonResponseArray.getJSONObject(i);

					// extract fields dari postInstance:
					String uid = postInstance.getString("UID");
					String accountName = postInstance.getString("AccountName");
					String realName = postInstance.getString("RealName");

					User user = new User(uid, accountName, realName, null, null, null, null, null, null);

					publishProgress(user);
				}
			}
			else {
				// kirim permintaan ke server, tanpa mengirimkan parameter apa pun
				String serverResponse = UtilityConnection.runPhp(phpFilePath, input);

				Log.d("Server Response", serverResponse);

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
					String dataFormattedDate = UtilityDate.formatTimestampElapsedTime(dataTimestamp);

					if (searchType == SUPPLY_POST) {
						// dapatkan field khusus untuk post supply (harga)
						String dataHarga = postInstance.getString("Harga");

						// buat instance PostSupply baru
						PostSupply postSupply = new PostSupply(
							dataPID, dataUID, dataTimestamp, dataNamaBarang,
							dataDeskripsi, dataHarga, dataRealName);

						// publish perubahan ke main UI thread
						// pemanggilan ini akan memanggil onProgressUpdate() di bawah
						publishProgress(postSupply);
					}
					else if (searchType == DEMAND_POST) {
						// dapatkan field khusus untuk post demand (lastNeed)
						String dataLastNeed = postInstance.getString("LastNeed");

						// buat instance PostSupply baru
						PostDemand postDemand = new PostDemand(
							dataPID, dataUID, dataTimestamp, dataNamaBarang,
							dataDeskripsi, dataLastNeed, dataRealName);

						// publish perubahan ke main UI thread
						// pemanggilan ini akan memanggil onProgressUpdate() di bawah
						publishProgress(postDemand);
					}
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
		if (searchType == SUPPLY_POST) {
			// tambahkan instance PostSupply ke arraySupply
			// notify adapter bahwa datanya sudah berubah (supaya di-relayout)
			arraySupply.add((PostSupply) object[0]);
			adapter.notifyDataSetChanged();
		}
		else if (searchType == DEMAND_POST) {
			// tambahkan instance PostSupply ke arraySupply
			// notify adapter bahwa datanya sudah berubah (supaya di-relayout)
			arrayDemand.add((PostDemand) object[0]);
			adapter.notifyDataSetChanged();
		}
		else if (searchType == USER_POST) {
			arrayUser.add((User) object[0]);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineDemandFragment ditekan
	 * ============================================================================== */
	private class SearchDemandFragmentListener implements ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			Intent intent = new Intent(context, DetailPostDemandActivity.class);

			// dapatkan instance post yang dipilih
			PostDemand postDemand = arrayDemand.get(position);

			// passing data post yang akan ditampilkan ke intent
			intent.putExtra("pid", postDemand.getPid());

			// start activity DetailPostDemand
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {
		}
	}

	/** ==============================================================================
	 * Custom implementation kelas ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineSupplyFragment ditekan
	 * ============================================================================== */
	private class SearchSupplyFragmentListener implements ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			Intent intent = new Intent(context, DetailPostSupplyActivity.class);

			// dapatkan instance post yang
			PostSupply postSupply = arraySupply.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			intent.putExtra("pid", postSupply.getPid());

			// start activity DetailPostSupply
			context.startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {
		}
	}

	/** ==============================================================================
	 * Custom implementation kelas ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineSupplyFragment ditekan
	 * ============================================================================== */
	private class SearchUserFragmentListener implements ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			// dapatkan instance post yang
			User user = arrayUser.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			// passing data post yang akan ditampilkan ke intent
			String uid = user.getUid();

			SessionManager session = new SessionManager(context);
			String currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

			TreeMap<String,String> input = new TreeMap<>();
			input.put("ownUID", currentUid);
			input.put("targetUID", uid);

			GetProfilTask getProfilTask = new GetProfilTask(context, input);
			getProfilTask.execute();
		}

		@Override
		public void onLongClick(View view, int position) {
		}
	}
}