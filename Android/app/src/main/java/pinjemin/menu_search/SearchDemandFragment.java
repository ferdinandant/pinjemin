package pinjemin.menu_search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import pinjemin.R;
import pinjemin.adapter.TimelineDemandAdapter;
import pinjemin.backgroundTask.PopulateTimelineTask;
import pinjemin.backgroundTask.SearchTask;
import pinjemin.behavior.ClickListener;
import pinjemin.behavior.RecyclerOnItemTouchListener;
import pinjemin.menu_timeline.DetailPostDemandActivity;
import pinjemin.model.PostDemand;
import pinjemin.utility.UtilityDate;


public class SearchDemandFragment extends Fragment
{
	private String query;
	private TreeMap<String,String> searchQuery = new TreeMap<>();
	private static ArrayList<PostDemand> arrayDemand;
	private static RecyclerView recyclerView;
	private static RecyclerView.Adapter adapter;

	public SearchDemandFragment() {
		// instantiate ArrayList yang dipakai pada RecyclerView
		arrayDemand = new ArrayList<>();
		adapter = new TimelineDemandAdapter(arrayDemand);
		Log.d("DEBUG", "YEEEE");
	}

	/** ==============================================================================
	 * Dipanggil agar Fragment bisa meng-instantiate View-nya. (Opsional: by default,
	 * dia akan me-return null (untuk non-graphical fragment)).
	 * @return - view untuk fragment UI, atau null.
	 * ============================================================================== */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState
	) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_timeline_demand, container, false);

		return view;
	}

	/** ==============================================================================
	 * Dipanggil saat fragment activity sudah dibuat dan view hierarchy-nya telah
	 * diinstansia(setelah pemanggilan onCreateView()). Berguna untuk melakukan
	 * final initialization setelah semua component sudah diinisialisasi.
	 * ============================================================================== */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// configure recycler view:
		recyclerView = (RecyclerView) getActivity().findViewById(R.id.recylerViewDemand);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setHasFixedSize(true);

		// set recycler view adapt
		Log.d("DEBUG", "PIIII");
		recyclerView.setAdapter(adapter);

		// tambahkan listener ke RecyclerView
		// NOTE: RecyclerOnItemTouchListener dideklarasikan di kelas terpisah
		// NOTE: inner class RecyclerClickListener dideklarasikan di bawah
		// Syntax: new OnItemTouchListener(activity, recyclerView, ClickListener)
		recyclerView.addOnItemTouchListener(
			new RecyclerOnItemTouchListener(getActivity(),
				recyclerView, new RecyclerClickListener()));
	}

	/** ==============================================================================
	 * Dipanggil saat Fragment dibentuk, sebelum pemanggilan onCreateView()
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** ==============================================================================
	 * Untuk mendapatkan adapter RecyclerView pada kelas ini
	 * @return adapter RecyclerView kelas ini
	 * ============================================================================== */
	public static RecyclerView.Adapter getAdapter() {
		Log.d("DEBUG", "NOOOO");
		Log.d("DEBUG", "Null? " + (adapter == null));
		return adapter;
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
