/** ===================================================================================
 * [MAIN ACTIVITY]
 * Kelas yang menampilkan halaman utama program
 * ----------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactor & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import pinjemin.friend.FriendFragment;
import pinjemin.notification.NotificationFragment;
import pinjemin.peminjaman.ListPeminjamanFragment;
import pinjemin.behavior.CustomViewPager;
import pinjemin.R;
import pinjemin.session.SessionManager;
import pinjemin.timeline.TimelineFragment;


public class MainActivity extends AppCompatActivity
{
	private SessionManager sessionManager;
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private CustomViewPager viewPager;

	private int[] tabIcons = {
		R.drawable.ic_tab_home,
		R.drawable.ic_tab_list,
		R.drawable.ic_tab_friend,
		R.drawable.ic_tab_notification
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// initialize sessionManager:
		sessionManager = new SessionManager(getApplicationContext());

		// cek sudah login atau belum
		sessionManager.checkLogin();

		// initialize toolbar:
		// initially langsung set title pada toolbar "Timeline"
		// jadikan toolbar ini Action Bar (main toolbar) di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Timeline");
		setSupportActionBar(toolbar);

		// configure viewPager:
		// CustomViewPager memungkinkan paginasi data (user bisa navigate halamannya)
		// Implementasikan PagerAdapter untuk menentukan halaman yang ditampilkan pada view
		viewPager = (CustomViewPager) findViewById(R.id.viewpager);
		configureViewPager();

		// configure tabLayout:
		// TabLayout mangatur horizontal layout untuk menampilkan tabs.
		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
		configureTabLayout();
	}

	/** ==============================================================================
	 * Mengatur viewPager: mengatur adapter (penampung fragment) dan action listener-nya
	 * ============================================================================== */
	private void configureViewPager() {
		// NOTE: inner class ViewPagerAdapter didekarasikan di bawah
		// NOTE: inner class ViewPagerListener dideklarasikan di bawah

		// mengonfigurasi adapter untuk viewPager
		// syntax: addFragment(fragment, fragmentTitle)
		// fragmentTitle sengaja kosong agar tidak ada teks di sebelah icon
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new TimelineFragment(), "");
		adapter.addFragment(new ListPeminjamanFragment(), "");
		adapter.addFragment(new FriendFragment(), "");
		adapter.addFragment(new NotificationFragment(), "");

		// menambahkan adapter untuk viewPager
		viewPager.setAdapter(adapter);

		// menambahkan action listener untuk viewPager
		viewPager.addOnPageChangeListener(new ViewPagerListener());
	}

	/** ==============================================================================
	 * Mengatur tabLayout: menentukan gambar icon yang ditampilkan pada menu tab icon
	 * ============================================================================== */
	private void configureTabLayout() {
		tabLayout.getTabAt(0).setIcon(tabIcons[0]);
		tabLayout.getTabAt(1).setIcon(tabIcons[1]);
		tabLayout.getTabAt(2).setIcon(tabIcons[2]);
		tabLayout.getTabAt(3).setIcon(tabIcons[3]);
	}

	/** ==============================================================================
	 * Handler ketika ada menu item pada yang dipilih.
	 * @param item: MenuItem yang dipilih
	 * ============================================================================== */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_logout) {
			sessionManager.logoutUser();
		}

		return super.onOptionsItemSelected(item);
	}

	/** ==============================================================================
	 * Inisialisasi menu pada ActivityMain. (Berdasarkan spesifikasi Android: method
	 * ini hanya dipanggil sekali; dipanggil juga jika terjadi perubahan pada menu).
	 * @param menu: menu pada activity ini
	 * ============================================================================== */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	/** ==============================================================================
	 * Handler ketika tombol back ditekan
	 * ============================================================================== */
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(Intent.ACTION_MAIN);

		// CATEGORY_HOME menandakan bahwa ini home activity
		// (yang biasanya dijalankan saat app/device dinyalakan?)
		intent.addCategory(Intent.CATEGORY_HOME);

		// NOTE: FLAG_ACTIVITY_CLEAR_TOP: Tutup semua activity di atas activity ini
		// (e.g. jika history activity-nya A->B->C->D, dan D memanggil activity B dengan
		// FLAG_ACTIVITY_CLEAR_TOP di-set, hasil akhir history-nya: A->B)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

		// tutup activity sebelumnya
		finish();

		// memastikan bahwa aplikasi di-terminate
		// (jadi tidak hanya back ke home screen/dipindah ke background)
		System.exit(0);
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas FragmentPagerAdapter, digunakan untuk mengatur
	 * behavior objek viewPager.
	 * ============================================================================== */
	class ViewPagerAdapter extends FragmentPagerAdapter
	{
		private final List<Fragment> fragmentList = new ArrayList<>();
		private final List<String> fragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			fragmentList.add(fragment);
			fragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragmentTitleList.get(position);
		}
	}

	/** ==============================================================================
	 * Custom implementation kelas ViewPager.OnPageChangeListener, digunakan
	 * sebagai action listener untuk objek viewPager (saat suatu menu tab icon ditekan)
	 * ============================================================================== */
	private class ViewPagerListener implements ViewPager.OnPageChangeListener
	{
		public final String[] FRAGMENT_TITLE = {
			"Timeline", "List Peminjaman", "Friend", "Notification"
		};

		@Override
		//--------------------------------------------------------------------------------
		// saat menu tab item ditekan, ganti judul pada toolbar
		// @param position: position index page yang dipilih
		//--------------------------------------------------------------------------------
		public void onPageSelected(int position) {
			toolbar.setTitle(FRAGMENT_TITLE[position]);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageScrollStateChanged(int state) {}
	}
}