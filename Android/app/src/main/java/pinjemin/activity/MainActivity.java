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
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.GetProfilTask;
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


public class MainActivity extends AppCompatActivity implements
		GoogleApiClient.OnConnectionFailedListener
{
	private SessionManager sessionManager;
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private CustomViewPager menuTabViewPager;
	private String currentUid;

	// Google Sign Out
	private GoogleApiClient mGoogleApiClient;
	private static final String TAG = "SignOutActivity";

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
		// Sign Out Google
		// [START configure_signin]
		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();
		// [END configure_signin]
		// [START build_client]
		// Build a GoogleApiClient with access to the Google Sign-In API and the
		// options specified by gso.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();
		// [END build_client]
	}
	public void onBackPressed() {
		moveTaskToBack(true);
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

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not
		// be available.
		Log.d(TAG, "onConnectionFailed:" + connectionResult);
	}


	/** ==============================================================================
	 * Handler ketika ada menu item pada yang dipilih.
	 * @param item: MenuItem yang dipilih
	 * ============================================================================== */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_logout) {
			// [START signOut]
			Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
			new ResultCallback<Status>() {
				@Override
				public void onResult(Status status) {
					// [START_EXCLUDE]
					sessionManager.logoutUser();
					// [END_EXCLUDE]
				}
			});
			// [END signOut]
		}
		else if (id == R.id.action_lihat_profil) {
			TreeMap<String,String> input = new TreeMap<>();
			input.put("ownUID", currentUid);
			input.put("targetUID", currentUid);

			GetProfilTask lihatProfil = new GetProfilTask(this, input);
			lihatProfil.execute();
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
		else if (id == R.id.action_refresh) {
			Log.d("DEBUG", "Meminta refresh manual");
			Toast.makeText(this, "Memperbarui timeline ...", Toast.LENGTH_LONG).show();
			TimelineSupplyFragment.resetLastRequest();
			TimelineDemandFragment.resetLastRequest();
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
		// menu yang di-inflate: search, ubah profil, logout, etc.
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
		//TimelineDemandFragment.resetLastRequest();
		//TimelineSupplyFragment.resetLastRequest();
		//Log.d("DEBUG", "Minta refresh ulang dari context-switching");
	}

	/** ==============================================================================
	 * Handler ketika tombol back ditekan
	 * ============================================================================== */
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