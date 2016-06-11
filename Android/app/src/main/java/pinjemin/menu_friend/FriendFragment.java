/** ===================================================================================
 * [FRIEND FRAGMENT]
 * Fragment yang menampilkan tab teman anda dan request pertemanan
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan, Ferdinand Antonius
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.menu_friend;

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


public class FriendFragment extends Fragment
{
	private static TabLayout tabLayout;
	private CustomViewPager viewPager;
	private CustomViewPagerAdapter adapter;

	public static int temanAndaCount = 0;
	public static int requestCount = 0;

	public FriendFragment() {
	}

	/** ==============================================================================
	 * Inisialisasi fragment GUI
	 * ============================================================================== */
	@Override
	public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState
	) {
		// beritahu bahwa fragment ini ingin mem-populate menu sendiri
		// (akan memanggil onCreateOptionsMenu() dan method-method terkait)
		setHasOptionsMenu(true);
		Log.d("DEBUG", "friend setHasOptionsMenu(true)");

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_friend, container, false);

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
	 * Dipanggil saat Fragment dibentuk, sebelum pemanggilan onCreateView()
	 * NOTE: Hanya dipanggil kalau ada setHasOptionsMenu(true) di onCreateView
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** ==============================================================================
	 * Unuk inisialisasi viewPager: meng-assign adapter yang menampung tab items dan
	 * inisialisasi fragments di dalamnya
	 * ============================================================================== */
	private void setupViewPager(ViewPager viewPager) {
		adapter = new CustomViewPagerAdapter(
			getChildFragmentManager());

		adapter.addFragment(new FriendTemanAndaFragment(), "Teman Anda");
		adapter.addFragment(new FriendRequestFragment(), "Request Add");

		Log.d("debug", "bikin tab");
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
		tabLayout.getTabAt(0).setText("Teman Anda (" +  temanAndaCount + ")");
		tabLayout.getTabAt(1).setText("Request Add (" +  requestCount + ")");
	}
}
