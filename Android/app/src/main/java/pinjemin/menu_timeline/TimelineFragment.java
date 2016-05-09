/** ===================================================================================
 * [TIMELINE FRAGMENT]
 * Fragment yang menampilkan timeline. Karena ada tabulasi, kelas ini juga terkait
 * dengan fragements TimelineDemandFragment dan TimelineSupplyFragment.
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.menu_timeline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.behavior.CustomViewPagerAdapter;
import pinjemin.behavior.CustomViewPager;
import pinjemin.R;
import pinjemin.behavior.CustomViewPagerRepopulator;


public class TimelineFragment extends Fragment
{
	public static final int DEMAND_MODE = 0;
	public static final int SUPPLY_MODE = 1;

	private TabLayout timelineTabLayout;
	private CustomViewPager timelineViewPager;
	private FloatingActionButton floatingActionButton;
	private int currentTimelineMode = 0;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState
	) {
		// beritahu bahwa fragment ini ingin mem-populate menu sendiri
		// (akan memanggil onCreateOptionsMenu() dan method-method terkait)
		setHasOptionsMenu(true);
		Log.d("DEBUG", "setHasOptionsMenu(true)");

		// Inflate (render) layout pada fragment ini:
		// Syntax: inflate(xmlLayoutFile, parentViewGroup, attachToRoot)
		// (attachToRoot == false, berarti dia stand-alone view (tidak jadi child))
		View view = inflater.inflate(R.layout.fragment_timeline, null, false);

		// initialize components:
		timelineTabLayout = (TabLayout) view.findViewById(R.id.tabs);
		timelineViewPager = (CustomViewPager) view.findViewById(R.id.viewpager);
		floatingActionButton = (FloatingActionButton) view.findViewById(R.id.btn_post);
		configureViewPager(timelineViewPager);

		// bind timelineViewPager dengan timelineTabLayout
		// setting supaya tab items selalu di-repopulate (error di Google Library?)
		timelineTabLayout.setupWithViewPager(timelineViewPager);
		timelineTabLayout.post(new CustomViewPagerRepopulator(
			timelineTabLayout, timelineViewPager));

		// setting action listeners:
		// (1) pada timelineViewPager: saat tab item dipencet (permintaan/penawaran)
		// (2) pada floatingActionButton: saat dipencet
		timelineViewPager.addOnPageChangeListener(new ViewPagerListener());
		floatingActionButton.setOnClickListener(new FloatingActionButtonListener());

		return view;
	}

	/** ==============================================================================
	 * Mengatur timelineViewPager: mengatur adapter (penampung fragment) dan
	 * action listener-nya
	 * ============================================================================== */
	private void configureViewPager(ViewPager viewPager) {
		// NOTE: CustomViewPagerAdapter dideklarasikan di kelas terpisah
		CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getChildFragmentManager());

		// tambahkan tab "Permintaan" dan "Penawaran"
		// menambahkan adapter untuk timelineViewPager
		// Syntax: addFragment(fragment, title)
		adapter.addFragment(new TimelineDemandFragment(), "Permintaan");
		adapter.addFragment(new TimelineSupplyFragment(), "Penawaran");
		viewPager.setAdapter(adapter);
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
	 * Mengonfigurasi menu (yang di pojok kanan atas: e.g. search, logout)
	 * ============================================================================== */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		// pada timeline fragment, tampilkan tombol refresh
		menu.findItem(R.id.action_refresh).setVisible(true).setEnabled(true);
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Action Listener untuk timelineViewPager, berguna untuk handling saat tab item ditekan
	 * (saat berpindah tab peminjaman <-> penawaran)
	 * ============================================================================== */
	private class ViewPagerListener implements CustomViewPager.OnPageChangeListener
	{
		@Override
		public void onPageSelected(int position) {
			switch (position) {
				case 0:
					// dipencet tab item index 0: permintaan
					currentTimelineMode = DEMAND_MODE;
					break;
				case 1:
					// dipencet tab item index 1: penawaran
					currentTimelineMode = SUPPLY_MODE;
					break;
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageScrollStateChanged(int state) {}
	}

	/** ==============================================================================
	 * Action Listener untuk FloatingActionButtonListener, berguna untuk handling
	 * saat tombol float action button ditekan
	 * ============================================================================== */
	private class FloatingActionButtonListener implements View.OnClickListener
	{
		@Override
		public void onClick(View view) {
			if (currentTimelineMode == 0) {
				// mulai activity CreatePostDemandActivity (permintaan baru)
				startActivity(new Intent(getActivity(), CreatePostDemandActivity.class));
			}
			else {
				// mulai activity CreatePostSupplyActivity (penawaran baru)
				startActivity(new Intent(getActivity(), CreatePostSupplyActivity.class));
			}
		}
	}
}
