package pinjemin.menu_peminjaman;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;


public class OngoingFragment extends Fragment
{

	private static TabLayout tabLayout;
	private CustomViewPager viewPager;
	private CustomViewPagerAdapter adapter;

	public OngoingFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_log_ongoing, container, false);

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

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/** ==============================================================================
	 * Mengubah text pada tabLayout sehingga menampilkan jumlah items
	 * ============================================================================== */
	private void setupViewPager(ViewPager viewPager) {
		adapter = new CustomViewPagerAdapter(
			getChildFragmentManager());

		adapter.addFragment(new OngoingDipinjamFragment(), "Dipinjam");
		adapter.addFragment(new OngoingDipinjamkanFragment(), "Dipinjamkan");

		Log.d("debug", "bikin tab");
		viewPager.setAdapter(adapter);
	}

	/** ==============================================================================
	 * Mengubah text pada tabLayout sehingga menampilkan jumlah items
	 * ============================================================================== */
	public static void updateTabLayoutDisplay() {
		if (tabLayout.getTabCount() >= 2) {
			tabLayout.getTabAt(0).setText("Dipinjam ("
				+ LogPeminjamanFragment.OngoingTakenCount + ")");
			tabLayout.getTabAt(1).setText("Dipinjamkan ("
				+ LogPeminjamanFragment.OngoingGivenCount + ")");
		}
	}
}
