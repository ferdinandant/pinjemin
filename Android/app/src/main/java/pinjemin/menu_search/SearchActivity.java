package pinjemin.menu_search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import pinjemin.R;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;
import pinjemin.utility.UtilityGUI;


public class SearchActivity extends AppCompatActivity
{

	private TabLayout searchTabLayout;
	private CustomViewPager searchTabViewPager;
	private EditText inputSearch;
	private CustomViewPagerAdapter pagerAdapter;

	public String searchKeyword = "";
	public int searchMenuSelectedPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// configure pagerAdapter:
		// searchTabViewPager adalah penampung page of Fragments
		searchTabViewPager = (CustomViewPager) findViewById(R.id.viewpager);
		pagerAdapter = configureViewPager();

		// configure searchTabLayout:
		// searchTabLayout mangatur horizontal layout untuk menampilkan tabs.
		searchTabLayout = (TabLayout) findViewById(R.id.tabs);
		searchTabLayout.setupWithViewPager(searchTabViewPager);

		// initialize components:
		// langsung berikan focus pada inputSearch
		inputSearch = (EditText) findViewById(R.id.input_search);
		UtilityGUI.requestFocus(this, inputSearch);

		// pasang listener pada inputSearch
		inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchKeyword = inputSearch.getText().toString();
					Log.d("DEBUG", "input searchKeyword: " + searchKeyword);

					// NOTE: FragmentTransaction adalah API untuk melakukan transaksi pada Fragment
					// NOTE: getSupportFragmentManager() mengembalikan FragmentManager (berfungsi
					// untuk berinteraksi dengan fragment-fragment pada suatu activity)
					// NOTE: beginTransaction() untuk memulai serangkaian edit operations pada
					// fragment-fragemnt yang terasosiasi dengan FragmentManager
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

					// remove Fragment yang sudah ada
					transaction.remove(pagerAdapter.getItem(0));
					transaction.remove(pagerAdapter.getItem(1));
					transaction.remove(pagerAdapter.getItem(2));
					transaction.commit();

					// reenitialize Fragment baru
					pagerAdapter = configureViewPager();
					Log.d("DEBUG", "Query Search: " + searchKeyword);

					// buka page tab yang sebelumnya terpilih
					searchTabViewPager.setCurrentItem(searchMenuSelectedPage);

					// consume event agar tidak ditangani handler lainnya
					// (jika return false, hanlder lain masih bisa meng-handle event ini)
					return true;
				}

				return false;
			}
		});

		// pasang listener pada searchTabViewPager
		// untuk handling saat berpindah halaman pada tab search
		searchTabViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				searchMenuSelectedPage = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageScrollStateChanged(int state) {}
		});
	}

	/** ==============================================================================
	 * Mengatur searchTabViewPager: mengatur pagerAdapter (penampung fragment) da
	 * action listener-nya
	 * ============================================================================== */
	private CustomViewPagerAdapter configureViewPager() {
		// NOTE: kelas CustomViewPagerAdapter didekarasikan di kelas terpisah
		// NOTE: inner class ViewPagerListener dideklarasikan di bawah

		// mengonfigurasi pagerAdapter untuk MenuTabViewPager
		// syntax: addFragment(fragment, fragmentTitle)
		// fragmentTitle sengaja kosong agar tidak ada teks di sebelah icon
		Fragment searchDemandFragment = new SearchDemandFragment();
		Fragment searchSupplyFragment = new SearchSupplyFragment();
		Fragment searchUserFragment = new SearchUserFragment();

		Bundle data = new Bundle();
		data.putString("query", searchKeyword);
		searchDemandFragment.setArguments(data);
		searchSupplyFragment.setArguments(data);
		searchUserFragment.setArguments(data);

		// membuat Fragment Adapter baru
		// syntax: addFragment(fragment, fragmentTitle)
		CustomViewPagerAdapter newPagerAdapter =
			new CustomViewPagerAdapter(getSupportFragmentManager());
		newPagerAdapter.addFragment(searchDemandFragment, "Permintaan");
		newPagerAdapter.addFragment(searchSupplyFragment, "Penawaran");
		newPagerAdapter.addFragment(searchUserFragment, "Pengguna");
		searchTabViewPager.setAdapter(newPagerAdapter);

		return newPagerAdapter;
	}

}