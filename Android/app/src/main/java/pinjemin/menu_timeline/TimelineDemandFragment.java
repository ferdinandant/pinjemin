/** ===================================================================================
 * [TIMELINE DEMAND FRAGMENT]
 * Fragment yang menampilkan timeline permintaan.
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */


package pinjemin.menu_timeline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import pinjemin.adapter.TimelineDemandAdapter;
import pinjemin.backgroundTask.PopulateTimelineTask;
import pinjemin.R;
import pinjemin.behavior.ClickListener;
import pinjemin.behavior.RecyclerOnItemTouchListener;
import pinjemin.model.PostDemand;
import pinjemin.utility.UtilityDate;


public class TimelineDemandFragment extends Fragment
{
	private static Calendar lastRequest = null;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private ArrayList<PostDemand> arrayDemand;


	public TimelineDemandFragment() {
		// instantiate ArrayList yang dipakai pada RecyclerView
		arrayDemand = new ArrayList<>();
	}

	/** ==============================================================================
	 * Dipanggil agar Fragment bisa meng-instantiate View-nya. (Opsional: by default,
	 * dia akan me-return null (untuk non-graphical fragment)).
	 * @return - view untuk fragment UI, atau null.
	 * ============================================================================== */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_timeline_demand, container, false);
		Log.d("DEBUG", "recreate view ...");

		return view;
	}

	/** ==============================================================================
	 * Dipanggil saat fragment activity sudah dibuat dan view hierarchy-nya telah
	 * diinstansia(setelah pemanggilan onCreateView()). Berguna untuk melakukan
	 * final initialization setelah semua component sudah diinisialisasi.
	 * ============================================================================== */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// configure recycler view:
		recyclerView = (RecyclerView) getActivity().findViewById(R.id.recylerViewDemand);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		// set recycler view adapter
		adapter = new TimelineDemandAdapter(arrayDemand);
		recyclerView.setAdapter(adapter);

		// tambahkan listener ke RecyclerView
		// NOTE: RecyclerOnItemTouchListener dideklarasikan di kelas terpisah
		// NOTE: inner class RecyclerClickListener dideklarasikan di bawah
		// Syntax: new OnItemTouchListener(activity, recyclerView, ClickListener)
		recyclerView.addOnItemTouchListener(
			new RecyclerOnItemTouchListener(getActivity(),
				recyclerView, new RecyclerClickListener()));

		// jalankan thread untuk handle refresh berkala
		refreshRoutine();
	}

	/** ==============================================================================
	 * Dipanggil saat Fragment dibentuk, sebelum pemanggilan onCreateView()
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** ==============================================================================
	 * Handling saat activity ini dipanggil kembali (e.g. saat context-switching,
	 * atau pindah dari activity lainnya ke activity ini)
	 * ============================================================================== */
	@Override
	public void onResume() {
		super.onResume();

		Log.d("DEBUG", "Jumlah barang di adapter " + adapter.getItemCount());

		// hanya kalau isi adapter kosong, jalankan refresh
		if (adapter.getItemCount() == 0) {
			Log.d("DEBUG", "Karena kosong, refresh lagi ya!");
			performRefresh();
		}
	}

	/** ==============================================================================
	 * Memaksa agar data pada timeline di-refresh lagi
	 * ============================================================================== */
	public static void resetLastRequest() {
		Log.d("DEBUG", "reset LastRequest!!");
		lastRequest = null;
	}

	/** ==============================================================================
	 * Me-refresh timeline
	 * ============================================================================== */
	public void performRefresh() {
		// cek apakah perlu refresh timeline data:
		Log.d("DEBUG", "Refreshing demand timeline");

		// jalankan background thread untuk fetch data dari server
		PopulateTimelineTask populateTimelineTask = new PopulateTimelineTask(
			getActivity(), PopulateTimelineTask.DEMAND_POST, adapter);
		populateTimelineTask.execute();

		// update timestamp terakhir kali refresh
		lastRequest = Calendar.getInstance();
	}

	/** ==============================================================================
	 * Untuk me-refresh timeline setiap beberapa detik sekali (thread terpisah!)
	 * ============================================================================== */
	public void refreshRoutine() {
		final Handler handler = new Handler();
		Runnable runnable = new Runnable()
		{
			public void run() {
				while (true) {
					// tes apakah perlu di-refresh
					if (lastRequest == null || UtilityDate.isToRefreshAgain(lastRequest)) {
						Log.d("DEBUG", "lastRequest null? " + (lastRequest == null));
						Log.d("DEBUG", "isToRefreshAgain? " + UtilityDate.isToRefreshAgain(lastRequest));
						// handler.post untuk melakukan sesuatu di UI thread
						handler.post(new Runnable()
						{
							public void run() {
								performRefresh();
							}
						});
					}

					// coba lagi 1 detik kemudian
					SystemClock.sleep(1000);
				}
			}
		};

		new Thread(runnable).start();
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation interface ClickListener (didefinisikan di kelas terpisah),
	 * digunakan untuk mengatur behavior saat ada item di RecyclerView timeline ditekan
	 * ============================================================================== */
	private class RecyclerClickListener implements ClickListener
	{
		@Override
		public void onClick(View view, int position) {
			Intent intent = new Intent(getActivity(), DetailPostDemandActivity.class);

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
			getActivity().startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {}
	}
}
