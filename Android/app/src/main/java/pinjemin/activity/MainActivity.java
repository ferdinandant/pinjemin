/** ===================================================================================
 * [MAIN ACTIVITY]
 * Kelas yang menampilkan halaman utama program
 * ----------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.UbahProfilFetchTask;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;
import pinjemin.menu_friend.FriendFragment;
import pinjemin.menu_notification.NotificationFragment;
import pinjemin.menu_peminjaman.LogPeminjamanFragment;
import pinjemin.menu_search.SearchActivity;
import pinjemin.menu_timeline.TimelineDemandFragment;
import pinjemin.menu_timeline.TimelineSupplyFragment;
import pinjemin.session.SessionManager;
import pinjemin.menu_timeline.TimelineFragment;


public class MainActivity extends AppCompatActivity
{
	private SessionManager sessionManager;
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private CustomViewPager menuTabViewPager;
	private String currentUid;

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

		currentUid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);

		// initialize toolbar:
		// initially langsung set title pada toolbar "Timeline"
		// jadikan toolbar ini Action Bar (main toolbar) di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Timeline");
		setSupportActionBar(toolbar);

		// configure menuTabViewPager:
		// ViewPager memungkinkan paginasi data (user bisa navigate halamannya).
		// Implementasikan PagerAdapter (direlasisasikan dalam CustomViewPagerAdapter)
		// untuk menentukan halaman yang ditampilkan pada view.
		menuTabViewPager = (CustomViewPager) findViewById(R.id.viewpager);
		configureViewPager();

		// configure tabLayout:
		// TabLayout mangatur horizontal layout untuk menampilkan tabs.
		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(menuTabViewPager);
		configureTabLayout();
	}

	/** ==============================================================================
	 * Mengatur menuTabViewPager: mengatur adapter (penampung fragment) dan action listener-nya
	 * ============================================================================== */
	private void configureViewPager() {
		// NOTE: kelas CustomViewPagerAdapter didekarasikan di kelas terpisah
		// NOTE: inner class ViewPagerListener dideklarasikan di bawah

		// mengonfigurasi adapter untuk menuTabViewPager
		// syntax: addFragment(fragment, fragmentTitle)
		// fragmentTitle sengaja kosong agar tidak ada teks di sebelah icon
		CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new TimelineFragment(), "");
		adapter.addFragment(new LogPeminjamanFragment(), "");
		adapter.addFragment(new FriendFragment(), "");
		adapter.addFragment(new NotificationFragment(), "");

		// menambahkan adapter untuk menuTabViewPager
		menuTabViewPager.setAdapter(adapter);

		// menambahkan action listener untuk menuTabViewPager
		menuTabViewPager.addOnPageChangeListener(new ViewPagerListener());
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
		else if (id == R.id.action_ubah_profil) {
			TreeMap<String,String> input = new TreeMap<>();
			input.put("ownUID", currentUid);
			input.put("targetUID", currentUid);

			UbahProfilFetchTask ubahProfil = new UbahProfilFetchTask(this, input);
			ubahProfil.execute();

		}
		else if (id == R.id.action_search) {
			startActivity(new Intent(this, SearchActivity.class));
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

	/** ==============================================================================
	 * dipanggil saat activity ini di-resume (i.e. saat aplikasi berpindah dari
	 * background ke foreground, mis. saal context-switching dari aplikasi lain)
	 * ============================================================================== */
	@Override
	public void onResume() {
		super.onResume();
		// reset last request pada demand dan supply timeline
		TimelineDemandFragment.resetLastRequest();
		TimelineSupplyFragment.resetLastRequest();
		Log.d("DEBUG", "Minta refresh ulang dari context-switching");
	}

	/** ==============================================================================
	 * Handler ketika tombol back ditekan
	 * ============================================================================== */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.d("DEBUG", "Back press dari main");
		/*
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

		// tutup activity ini
		finish();
		*/
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas ViewPager.OnPageChangeListener, digunakan
	 * sebagai action listener untuk objek menuTabViewPager (saat suatu menu tab icon ditekan)
	 * ============================================================================== */
	private class ViewPagerListener implements ViewPager.OnPageChangeListener
	{
		public final String[] FRAGMENT_TITLE = {
			"Timeline", "Log Peminjaman", "Friend", "Notification"
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
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}
}