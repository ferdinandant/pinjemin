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

import pinjemin.adapter.FriendRequestAdapter;
import pinjemin.adapter.FriendTemanAndaAdapter;
import pinjemin.menu_friend.FriendRequest;
import pinjemin.menu_friend.FriendTemanAnda;
import pinjemin.model.User;
import pinjemin.R;
import pinjemin.utility.UtilityConnection;


public class PopulateFriendTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_FRIEND_TEMAN_ANDA = "getpenawarantimeline.php";
	public static final String PHP_PATH_FRIEND_REQUEST = "getpermintaantimeline.php";
	public static final int FRIEND_TEMAN_ANDA = 1;
	public static final int FRIEND_REQUEST = 2;

	private Activity activity;
	private Context context;
	private String phpFilePath;
	private int friendType;

	// bagian RecyclerView:
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// penampung object RecyclerView:
	private ArrayList<User> arrayUser;

	private String status;
	private String currentUid;

	/** ==============================================================================
	 * Constructor kelas PopulateTimelineTask
	 * @param context - context dari mana PopulateTimelineTask dipanggil
	 * @param friendType - DEMAND_SEARCH atau SUPPLY_SEARCH, tergantung jenis
	 * 	timeline yang akan dimintakan ke server.
	 * ============================================================================== */
	public PopulateFriendTask(Context context, int friendType, String currentUid) {
		this.context = context;
		this.activity = (Activity) context;
		this.friendType = friendType;
		this.currentUid = currentUid;

		// configure file phpFilePath yang benar
		if (friendType == FRIEND_TEMAN_ANDA) {
			this.phpFilePath = PHP_PATH_FRIEND_TEMAN_ANDA;
		}
		else if (friendType == FRIEND_REQUEST) {
			this.phpFilePath = PHP_PATH_FRIEND_REQUEST;
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SEBELUM subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected void onPreExecute() {
		// configure layoutManager
		layoutManager = new LinearLayoutManager(context);

		if (friendType == FRIEND_TEMAN_ANDA) {
			// create array, configure adapter
			arrayUser = new ArrayList<>();
			adapter = new FriendTemanAndaAdapter(arrayUser);

			// configure RecyclerView
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewFriendTemanAnda);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new FriendTemanAnda.RecyclerTouchListener
					(context, recyclerView, new FriendTemanAndaListener()));
		}

		else if (friendType == FRIEND_REQUEST) {
			// create array, configure adapter
			arrayUser = new ArrayList<>();
			adapter = new FriendRequestAdapter(arrayUser);

			// configure recycler view
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewFriendRequest);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new FriendRequest.RecyclerTouchListener
					(context, recyclerView, new FriendRequestListener()));
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			// kirim permintaan ke server, tanpa mengirimkan parameter apa pun
			TreeMap<String,String> input = new TreeMap<>();
			input.put("ownUID", currentUid);

			String serverResponse = UtilityConnection.runPhp(phpFilePath, null);

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
		arrayUser.add((User) object[0]);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPostExecute(Void aVoid) {}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas TimelineDemandFragment.ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineDemandFragment ditekan
	 * ============================================================================== */
	private class FriendTemanAndaListener implements FriendTemanAnda.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			// dapatkan instance post yang
			User user = arrayUser.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			// passing data post yang akan ditampilkan ke intent
			String uid = user.getUid();

			TreeMap<String,String> input = new TreeMap<>();
			input.put("ownUID", currentUid);
			input.put("targetUID", uid);

			GetProfilTask getProfilTask = new GetProfilTask(context, input);
			getProfilTask.execute();
		}

		@Override
		public void onLongClick(View view, int position) {}
	}

	/** ==============================================================================
	 * Custom implementation kelas TimelineSupplyFragment.ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineSupplyFragment ditekan
	 * ============================================================================== */
	private class FriendRequestListener implements FriendRequest.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			// dapatkan instance post yang
			User user = arrayUser.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			// passing data post yang akan ditampilkan ke intent
			String uid = user.getUid();

			TreeMap<String,String> input = new TreeMap<>();
			input.put("ownUID", currentUid);
			input.put("targetUID", uid);

			GetProfilTask getProfilTask = new GetProfilTask(context, input);
			getProfilTask.execute();
		}

		@Override
		public void onLongClick(View view, int position) {}
	}
}