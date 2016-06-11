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
import pinjemin.adapter.PeminjamanWaitingAdapter;
import pinjemin.backgroundTask.GetProfilTask;
import pinjemin.backgroundTask.UbahProfilFetchTask;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;
import pinjemin.menu_friend.FriendFragment;
import pinjemin.menu_friend.FriendRequestFragment;
import pinjemin.menu_friend.FriendTemanAndaFragment;
import pinjemin.menu_notification.NotificationFragment;
import pinjemin.menu_peminjaman.ExpiredFragment;
import pinjemin.menu_peminjaman.LogPeminjamanFragment;
import pinjemin.menu_peminjaman.OngoingDipinjamFragment;
import pinjemin.menu_peminjaman.OngoingDipinjamkanFragment;
import pinjemin.menu_peminjaman.WaitingFragment;
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
	private ViewPagerListener viewPagerListener;
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


	/** ==============================================================================
	 * Inisialisasi fragments dan loaders, dipanggil sebelum activity di-start
	 * ============================================================================== */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// initialize sessionManager: cek sudah login atau belum
		sessionManager = new SessionManager(getApplicationContext());
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

		// Sign Out Google (configure objek sign in):
		// konstruksi objek GoogleSignInOptions untuk me-request user ID dan basic profile
		// ID dan basic profile dapat diminta dengan GoogleSignInOptions.DEFAULT_SIGN_IN
		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
			GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

		// bentuk GoogleApiClient dengan akses ke Google Sign-In API,
		// menggunakan options yang di-specify oleh googleSignInOptions.
		// NOTE: Parameter enableAutoManage(FragmentActivity, OnConnectionFailedListener)
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.enableAutoManage(this, this)
			.addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
			.build();
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
		viewPagerListener = new ViewPagerListener();
		menuTabViewPager.addOnPageChangeListener(viewPagerListener);
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
	 * dipanggil saat activity ini di-resume (i.e. saat aplikasi berpindah dari
	 * background ke foreground, mis. saal context-switching dari aplikasi lain)
	 * ============================================================================== */
	@Override
	public void onResume() {
		super.onResume();
	}

	/** ==============================================================================
	 * Handler ketika ada menu item pada yang dipilih.
	 * @param item: MenuItem yang dipilih
	 * ============================================================================== */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_logout) {
			// google logout sequence
			Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status status) {
						sessionManager.logoutUser();
					}
				}
			);
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
			if (viewPagerListener.getSelectedPage() == 0) {
				Log.d("DEBUG", "Meminta refresh timeline");
				Toast.makeText(this, "Memperbarui timeline ...", Toast.LENGTH_LONG).show();
				try {
					TimelineSupplyFragment.resetLastRequest();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di supply");
				}
				try {
					TimelineDemandFragment.resetLastRequest();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di demand");
				}
			}
			else if (viewPagerListener.getSelectedPage() == 1) {
				Log.d("DEBUG", "Meminta refresh log peminjaman");
				Toast.makeText(this, "Memperbarui log peminjaman ...", Toast.LENGTH_LONG).show();
				// TWEAK: untuk menghemat memori, yang disimpan di memori hanya tab yang aktif
				// dan tab-tab di sebelahnya. Jadi, ada kemungkinan Null Pointer untuk tab yang tidak aktif.
				// Tidak perlu dipermasalahkan. fokuskan refresh di tab yang aktif.
				try {
					WaitingFragment.performRefresh();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di waiting");
				}
				try {
					OngoingDipinjamFragment.performRefresh();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di ongoing 1");
				}
				try {
					OngoingDipinjamkanFragment.performRefresh();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di ongoing 2");
				}
				try {
					ExpiredFragment.performRefresh();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di expired");
				}
			}
			else if (viewPagerListener.getSelectedPage() == 2) {
				Log.d("DEBUG", "Meminta refresh pertemanan");
				Toast.makeText(this, "Memperbarui daftar pertemanan ...", Toast.LENGTH_LONG).show();
				try {
					FriendRequestFragment.performRefresh();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di friend request");
				}
				try {
					FriendTemanAndaFragment.performRefresh();
				}
				catch (NullPointerException e) {
					Log.d("DEBUG", "NullPointer di friend teman anda");
				}
			}
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
	 * Handler saat koneksi login google gagal.
	 * ============================================================================== */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not
		// be available.
		Log.d(TAG, "onConnectionFailed:" + connectionResult);
	}

	/** ==============================================================================
	 * Handler saat tombol back ditekan
	 * ==============================================================================*/
	public void onBackPressed() {
		moveTaskToBack(true);
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas ViewPager.OnPageChangeListener, digunakan
	 * sebagai action listener untuk objek menuTabViewPager (saat suatu menu tab icon ditekan)
	 * ============================================================================== */
	private class ViewPagerListener implements ViewPager.OnPageChangeListener
	{
		private int selectedPage = 0;
		public final String[] FRAGMENT_TITLE = {
			"Timeline", "Log Peminjaman", "Pertemanan", "Notifikasi"
		};

		//--------------------------------------------------------------------------------
		// saat menu tab item ditekan, ganti judul pada toolbar
		// @param position: position index page yang dipilih
		//--------------------------------------------------------------------------------
		@Override
		public void onPageSelected(int position) {
			selectedPage = position;
			toolbar.setTitle(FRAGMENT_TITLE[position]);
		}

		//--------------------------------------------------------------------------------
		// mendapatkan indeks menu tab item yang aktif saat ini
		// @return indeks menu tab yang aktif saat ini (0..3)
		//--------------------------------------------------------------------------------
		public int getSelectedPage() {
			return selectedPage;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}
}