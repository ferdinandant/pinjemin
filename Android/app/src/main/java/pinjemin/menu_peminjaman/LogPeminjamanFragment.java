package pinjemin.menu_peminjaman;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;


public class LogPeminjamanFragment extends Fragment
{
	private static TabLayout tabLayout;
	private CustomViewPager viewPager;

	public static int WaitingCount = 0;
	public static int OngoingGivenCount = 0;
	public static int OngoingTakenCount = 0;
	public static int ExpiredCount = 0;

	public LogPeminjamanFragment() {
		// Required empty public constructor
	}

	/** ==============================================================================
	 * Inisialisasi fragments dan loaders, dipanggil sebelum activity di-start
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState
	) {
		// beritahu bahwa fragment ini ingin mem-populate menu sendiri
		// (akan memanggil onCreateOptionsMenu() dan method-method terkait)
		setHasOptionsMenu(true);
		Log.d("DEBUG", "log setHasOptionsMenu(true)");

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_list_peminjaman, container, false);

		tabLayout = (TabLayout) view.findViewById(R.id.tabs);
		viewPager = (CustomViewPager) view.findViewById(R.id.viewpager);
		setupViewPager(viewPager);
		tabLayout.post(new Runnable()
		{
			@Override
			public void run() {
				tabLayout.setupWithViewPager(viewPager);
			}
		});

		return view;
	}

	/** ==============================================================================
	 * Mengatur viewPager: mengatur adapter (penampung fragment) dan action listener-nya
	 * ============================================================================== */
	private void setupViewPager(ViewPager viewPager) {
		CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(
			getChildFragmentManager());

		adapter.addFragment(new WaitingFragment(), "Menunggu");
		adapter.addFragment(new OngoingFragment(), "Berjalan");
		adapter.addFragment(new ExpiredFragment(), "Berlalu");

		viewPager.setAdapter(adapter);
	}

	/** ==============================================================================
	 * Mengonfigurasi menu (yang di pojok kanan atas: e.g. search, logout)
	 * ============================================================================== */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		// pada timeline fragment, tampilkan tombol refresh
		menu.findItem(R.id.action_refresh).setVisible(true).setEnabled(true);
	}

	/** ==============================================================================
	 * Mengubah text pada tabLayout sehingga menampilkan jumlah items
	 * ============================================================================== */
	public static void updateTabLayoutDisplay() {
		tabLayout.getTabAt(0).setText("Menunggu (" +  WaitingCount + ")");
		tabLayout.getTabAt(1).setText("Berjalan (" +  (OngoingGivenCount+OngoingTakenCount) + ")");
		tabLayout.getTabAt(2).setText("Berlalu (" +  ExpiredCount + ")");
	}
}
