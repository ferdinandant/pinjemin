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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import pinjemin.R;
import pinjemin.adapter.PeminjamanExpiredAdapter;
import pinjemin.adapter.PeminjamanOngoingPinjamAdapter;
import pinjemin.adapter.PeminjamanOngoingPinjamkanAdapter;
import pinjemin.adapter.PeminjamanWaitingAdapter;
import pinjemin.menu_friend.FriendRequest;
import pinjemin.menu_friend.FriendTemanAnda;
import pinjemin.menu_timeline.DetailPostDemandActivity;
import pinjemin.menu_timeline.DetailPostSupplyActivity;
import pinjemin.model.PostPeminjaman;
import pinjemin.utility.UtilityConnection;

;


public class PopulatePeminjamanTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_PEMINJAMAN_WAITING = "getwaitinglog.php";
	public static final String PHP_PATH_PEMINJAMAN_ONGOING_DIPINJAM = "getongoingdipinjamlog.php";
	public static final String PHP_PATH_PEMINJAMAN_ONGOING_DIPINJAMKAN = "getongoingdipinjamkanlog.php";
	public static final String PHP_PATH_PEMINJAMAN_EXPIRED = "getexpiredlog.php";

	public static final int PEMINJAMAN_WAITING = 1;
	public static final int PEMINJAMAN_ONGOING_DIPINJAM = 2;
	public static final int PEMINJAMAN_ONGOING_DIPINJAMKAN = 3;
	public static final int PEMINJAMAN_EXPIRED = 4;

	private Activity activity;
	private Context context;
	private String phpFilePath;
	private int peminjamanType;

	// bagian RecyclerView:
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	// penampung object RecyclerView:
	private ArrayList<PostPeminjaman> arrayPeminjaman;

	private String currentUid;

	/** ==============================================================================
	 * Constructor kelas PopulateTimelineTask
	 * @param context - context dari mana PopulateTimelineTask dipanggil
	 * @param peminjamanType - DEMAND_SEARCH atau SUPPLY_SEARCH, tergantung jenis
	 * 	timeline yang akan dimintakan ke server.
	 * ============================================================================== */
	public PopulatePeminjamanTask(Context context, int peminjamanType, String currentUid) {
		this.context = context;
		this.activity = (Activity) context;
		this.peminjamanType = peminjamanType;
		this.currentUid = currentUid;

		// configure file phpFilePath yang benar
		if (peminjamanType == PEMINJAMAN_WAITING) {
			this.phpFilePath = PHP_PATH_PEMINJAMAN_WAITING;
		}
		else if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAM) {
			this.phpFilePath = PHP_PATH_PEMINJAMAN_ONGOING_DIPINJAM;
		}
		else if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAMKAN) {
			this.phpFilePath = PHP_PATH_PEMINJAMAN_ONGOING_DIPINJAMKAN;
		}
		else if (peminjamanType == PEMINJAMAN_EXPIRED) {
			this.phpFilePath = PHP_PATH_PEMINJAMAN_EXPIRED;
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SEBELUM subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected void onPreExecute() {
		// configure layoutManager
		layoutManager = new LinearLayoutManager(context);

		if (peminjamanType == PEMINJAMAN_WAITING) {
			// create array, configure adapter
			arrayPeminjaman = new ArrayList<>();
			adapter = new PeminjamanWaitingAdapter(arrayPeminjaman, currentUid);

			Log.d("Masuk", "Waiting");

			// configure RecyclerView
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewWaiting);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new PopulatePeminjamanTask.RecyclerTouchListener
					(context, recyclerView, new PeminjamanListener()));

		}
		else if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAM) {
			// create array, configure adapter
			arrayPeminjaman = new ArrayList<>();
			adapter = new PeminjamanOngoingPinjamAdapter(arrayPeminjaman);

			// configure recycler view barang yang di pinjam
			recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerViewDipinjam);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			recyclerView.addOnItemTouchListener(
				new PopulatePeminjamanTask.RecyclerTouchListener
					(context, recyclerView, new PeminjamanListener()));


		}
		else if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAMKAN) {
			// create array, configure adapter
			arrayPeminjaman = new ArrayList<>();
			adapter = new PeminjamanOngoingPinjamkanAdapter(arrayPeminjaman);

			// configure recycler view barang yang di pinjam
			recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerViewDipinjamkan);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			recyclerView.addOnItemTouchListener(
				new PopulatePeminjamanTask.RecyclerTouchListener
					(context, recyclerView, new PeminjamanListener()));


		}
		else if (peminjamanType == PEMINJAMAN_EXPIRED) {
			// create array, configure adapter
			arrayPeminjaman = new ArrayList<>();
			adapter = new PeminjamanExpiredAdapter(arrayPeminjaman, currentUid);

			Log.d("Masuk", "Expired");

			// configure recycler view
			recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewExpired);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);

			// tambahkan listener ke RecyclerView
			recyclerView.addOnItemTouchListener(
				new PopulatePeminjamanTask.RecyclerTouchListener
					(context, recyclerView, new PeminjamanListener()));
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {

			if (currentUid != null) {
				// kirim permintaan ke server, tanpa mengirimkan parameter apa pun
				TreeMap<String,String> input = new TreeMap<>();
				input.put("ownUID", currentUid);

				String serverResponse = UtilityConnection.runPhp(phpFilePath, input);
				Log.d("DEBUG", serverResponse);

				// parse data JSON yang diterima dari server (berisi daftar post)
				JSONObject jsonResponseObject = new JSONObject(serverResponse);
				JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("server_response");
				int jsonResponseArrayLength = jsonResponseArray.length();

				for (int i = 0; i < jsonResponseArrayLength; i++) {
					JSONObject postInstance = jsonResponseArray.getJSONObject(i);

					PostPeminjaman postPeminjaman;

					// extract fields dari postInstance:

					if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAM
						|| peminjamanType == PEMINJAMAN_ONGOING_DIPINJAMKAN) {
						String PID = postInstance.getString("PID");
						String Deadline = postInstance.getString("Deadline");
						String TimestampMulai = postInstance.getString("TimestampMulai");
						String NamaBarang = postInstance.getString("NamaBarang");
						String Deskripsi = postInstance.getString("Deskripsi");
						int UnreadCount = Integer.parseInt(postInstance.getString("UnreadCount"));

						String RealName = postInstance.getString("RealName");


						if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAM) {
							String RealNamePemberi = postInstance.getString("RealNamePemberi");
							String UIDPemberi = postInstance.getString("UIDPemberi");

							postPeminjaman = new PostPeminjaman(PID, currentUid, UIDPemberi, null,
								null, TimestampMulai, null, Deadline,
								null, null, null, NamaBarang, Deskripsi,
								RealName, RealNamePemberi, null, null, null);
							postPeminjaman.setUnreadCount(UnreadCount);

							publishProgress(postPeminjaman);

						}
						else if (peminjamanType == PEMINJAMAN_ONGOING_DIPINJAMKAN) {
							String RealNamePenerima = postInstance.getString("RealNamePenerima");
							String UIDPenerima = postInstance.getString("UIDPenerima");

							postPeminjaman = new PostPeminjaman(PID, currentUid, null, UIDPenerima,
								null, TimestampMulai, null, Deadline,
								null, null, null, NamaBarang, Deskripsi,
								RealName, null, RealNamePenerima, null, null);
							postPeminjaman.setUnreadCount(UnreadCount);

							publishProgress(postPeminjaman);
						}
					}
					else if (peminjamanType == PEMINJAMAN_EXPIRED) {
						String PID = postInstance.getString("PID");
						String Deadline = postInstance.getString("Deadline");
						String TimestampMulai = postInstance.getString("TimestampMulai");
						String TimestampKembali = postInstance.getString("TimestampKembali");
						String NamaBarang = postInstance.getString("NamaBarang");
						String Deskripsi = postInstance.getString("Deskripsi");
						String RealName = postInstance.getString("RealName");
						String RealNamePemberi = postInstance.getString("RealNamePemberi");
						String RealNamePenerima = postInstance.getString("RealNamePenerima");
						String UIDPemberi = postInstance.getString("UIDPemberi");
						String UIDPenerima = postInstance.getString("UIDPenerima");
						String Status = postInstance.getString("Status");
						int UnreadCount = Integer.parseInt(postInstance.getString("UnreadCount"));

						postPeminjaman = new PostPeminjaman(PID, currentUid, UIDPemberi, UIDPenerima,
							null, TimestampMulai, TimestampKembali, Deadline,
							Status, null, null, NamaBarang, Deskripsi, RealName, RealNamePemberi, RealNamePenerima, null, null);
						postPeminjaman.setUnreadCount(UnreadCount);

						publishProgress(postPeminjaman);
					}
					else {
						String PID = postInstance.getString("PID");
						String UID = postInstance.getString("UID");
						String Timestamp = postInstance.getString("Timestamp");
						String NamaBarang = postInstance.getString("NamaBarang");
						String Deskripsi = postInstance.getString("Deskripsi");
						String RealName = postInstance.getString("RealName");
						String LastNeed = postInstance.getString("LastNeed");
						String Harga = postInstance.getString("Harga");
						int UnreadCount = Integer.parseInt(postInstance.getString("UnreadCount"));

						postPeminjaman = new PostPeminjaman(PID, UID, null, null,
							Timestamp, null, null, null,
							null, null, null, NamaBarang, Deskripsi, RealName, null, null, LastNeed, Harga);
						postPeminjaman.setUnreadCount(UnreadCount);

						publishProgress(postPeminjaman);
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
		arrayPeminjaman.add((PostPeminjaman) object[0]);
		adapter.notifyDataSetChanged();
	}

	public void update() {
	}

	@Override
	protected void onPostExecute(Void aVoid) {
	}

	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas TimelineSupplyFragment.ClickListener, digunakan
	 * untuk mengatur behavior saat item di TimelineSupplyFragment ditekan
	 * ============================================================================== */
	private class PeminjamanListener implements PopulatePeminjamanTask.ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			// dapatkan instance post yang

			PostPeminjaman peminjaman = arrayPeminjaman.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			// passing data post yang akan ditampilkan ke intent
			String PID = peminjaman.getPid();

			TreeMap<String,String> input = new TreeMap<>();
			input.put("PID", PID);

			if (peminjamanType == PEMINJAMAN_WAITING) {
				Intent intent;

				if (peminjaman.getHarga() == "null") {
					intent = new Intent(context, DetailPostDemandActivity.class);
				}
				else {
					intent = new Intent(context, DetailPostSupplyActivity.class);
				}

				intent.putExtra("pid", peminjaman.getPid());
				context.startActivity(intent);
			}
			else {
				GetDetailPeminjamanTask task = new GetDetailPeminjamanTask(context, input);
				task.execute();
			}
		}

		@Override
		public void onLongClick(View view, int position) {
		}
	}

	public interface ClickListener
	{
		void onClick(View view, int position);

		void onLongClick(View view, int position);
	}

	public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
	{

		private GestureDetector gestureDetector;
		private PopulatePeminjamanTask.ClickListener clickListener;

		public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PopulatePeminjamanTask.ClickListener clickListener) {
			this.clickListener = clickListener;
			gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
			{
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return true;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
					if (child != null && clickListener != null) {
						clickListener.onLongClick(child, recyclerView.getChildPosition(child));
					}
				}
			});
		}

		@Override
		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

			View child = rv.findChildViewUnder(e.getX(), e.getY());
			if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
				clickListener.onClick(child, rv.getChildPosition(child));
			}
			return false;
		}

		@Override
		public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		}

		@Override
		public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

		}
	}
}