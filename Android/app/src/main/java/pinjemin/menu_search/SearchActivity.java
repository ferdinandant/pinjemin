package pinjemin.menu_search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
	private CustomViewPagerAdapter adapter;

	public String search = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		searchTabViewPager = (CustomViewPager) findViewById(R.id.viewpager);
		adapter = configureViewPager();

		// configure tabLayout:
		// TabLayout mangatur horizontal layout untuk menampilkan tabs.
		searchTabLayout = (TabLayout) findViewById(R.id.tabs);
		searchTabLayout.setupWithViewPager(searchTabViewPager);

		// initialize components:
		// langsung berikan focus pada inputSearch
		inputSearch = (EditText) findViewById(R.id.input_search);
		UtilityGUI.requestFocus(this, inputSearch);

		inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// configure searchTabLayout:
					// TabLayout mangatur horizontal layout untuk menampilkan tabs.

					search = inputSearch.getText().toString();
					Log.d("input search", search);

					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

					transaction.remove(adapter.getItem(0));
					transaction.remove(adapter.getItem(1));
					transaction.remove(adapter.getItem(2));

					transaction.commit();

					adapter = configureViewPager();

					Log.d("Search", "Masuk search");
					Log.d("Query Search", search);
					return true;
				}
				return false;
			}
		});
	}

	/** ==============================================================================
	 * Mengatur searchTabViewPager: mengatur adapter (penampung fragment) da
	 * action listener-nya
	 * ============================================================================== */
	private CustomViewPagerAdapter configureViewPager() {
		// NOTE: kelas CustomViewPagerAdapter didekarasikan di kelas terpisah
		// NOTE: inner class ViewPagerListener dideklarasikan di bawah

		// mengonfigurasi adapter untuk MenuTabViewPager
		// syntax: addFragment(fragment, fragmentTitle)
		// fragmentTitle sengaja kosong agar tidak ada teks di sebelah icon
		Fragment searchDemandFragment = new SearchDemandFragment();
		Fragment searchSupplyFragment = new SearchSupplyFragment();
		Fragment searchUserFragment = new SearchUserFragment();

		Bundle data = new Bundle();
		data.putString("query", search);
		searchDemandFragment.setArguments(data);
		searchSupplyFragment.setArguments(data);
		searchUserFragment.setArguments(data);

		// mengonfigurasi adapter untuk MenuTabViewPager
		// syntax: addFragment(fragment, fragmentTitle)
		CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(searchDemandFragment, "Permintaan");
		adapter.addFragment(searchSupplyFragment, "Penawaran");
		adapter.addFragment(searchUserFragment, "Pengguna");
		searchTabViewPager.setAdapter(adapter);

		return adapter;
		// menambahkan action listener untuk MenuTabViewPager
		//searchTabViewPager.addOnPageChangeListener(new ViewPagerListener());
	}

}