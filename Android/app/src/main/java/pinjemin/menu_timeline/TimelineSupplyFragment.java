/** ===================================================================================
 * [TIMELINE SUPPLY FRAGMENT]
 * Fragment yang menampilkan timeline penawaran.
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.menu_timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import pinjemin.adapter.TimelineSupplyAdapter;
import pinjemin.backgroundTask.PopulateTimelineTask;
import pinjemin.R;
import pinjemin.behavior.ClickListener;
import pinjemin.behavior.RecyclerOnItemTouchListener;
import pinjemin.model.PostSupply;
import pinjemin.utility.UtilityDate;


public class TimelineSupplyFragment extends Fragment
{
	private static Calendar lastRequest = null;
	private RecyclerView recyclerView;
	private ArrayList<PostSupply> arraySupply;


	public TimelineSupplyFragment() {
		// instantiate ArrayList yang dipakai pada RecyclerView
		arraySupply = new ArrayList<>();
	}

	/** ==============================================================================
	 * Dipanggil agar Fragment bisa meng-instantiate View-nya. (Opsional: by default,
	 * dia akan me-return null (untuk non-graphical fragment)).
	 * @return - view untuk fragment UI, atau null.
	 * ============================================================================== */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_timeline_supply, container, false);

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
		recyclerView = (RecyclerView) getActivity().findViewById(R.id.recylerViewSupply);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setHasFixedSize(true);

		// set recycler view adapter
		RecyclerView.Adapter adapter = new TimelineSupplyAdapter(arraySupply);
		recyclerView.setAdapter(adapter);

		// tambahkan listener ke RecyclerView
		// NOTE: RecyclerOnItemTouchListener dideklarasikan di kelas terpisah
		// NOTE: inner class RecyclerClickListener dideklarasikan di bawah
		// Syntax: new OnItemTouchListener(activity, recyclerView, ClickListener)
		recyclerView.addOnItemTouchListener(
			new RecyclerOnItemTouchListener(getActivity(),
				recyclerView, new RecyclerClickListener()));

		// cek apakah perlu refresh timeline data:
		if (lastRequest == null || UtilityDate.isToRefreshAgain(lastRequest)) {
			Log.d("DEBUG", "Refreshing supply timeline");

			// jalankan background thread untuk fetch data dari server
			PopulateTimelineTask populateTimelineTask = new PopulateTimelineTask(
				getActivity(), PopulateTimelineTask.SUPPLY_POST, adapter);
			populateTimelineTask.execute();

			// update timestamp terakhir kali refresh
			lastRequest = Calendar.getInstance();
		}
		else {
			Log.d("DEBUG", "Not refreshing demand timeline");
		}
	}

	/** ==============================================================================
	 * Dipanggil saat Fragment dibentuk, sebelum pemanggilan onCreateView()
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** ==============================================================================
	 * Memaksa agar data pada timeline di-refresh lagi
	 * ============================================================================== */
	public static void resetLastRequest() {
		lastRequest = null;
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
			Intent intent = new Intent(getActivity(), DetailPostSupplyActivity.class);

			// dapatkan instance post yang dipilih
			PostSupply postSupply = arraySupply.get(position);

			// sisipkan data post yang akan ditampilkan ke intent
			intent.putExtra("pid", postSupply.getPid());
			intent.putExtra("uid", postSupply.getUid());
			intent.putExtra("timestamp", postSupply.getTimestamp());
			intent.putExtra("namaBarang", postSupply.getNamaBarang());
			intent.putExtra("deskripsi", postSupply.getDeskripsi());
			intent.putExtra("harga", postSupply.getHarga());
			intent.putExtra("accountName", postSupply.getAccountName());

			// start activity DetailPostSupplyActivity
			getActivity().startActivity(intent);
		}

		@Override
		public void onLongClick(View view, int position) {}
	}
}
