package pinjemin.menu_search;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.LoginTask;
import pinjemin.backgroundTask.SearchTask;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;


public class SearchActivity extends AppCompatActivity
{
	public static final int DEMAND_SEARCH = 0;
	public static final int SUPPLY_SEARCH = 1;
	public static final int USER_SEARCH = 2;

	private TabLayout searchTabLayout;
	private CustomViewPager searchTabViewPager;
	private EditText inputSearch;
	private int currentSearchMode = 0;

	public String search = "test";
	public TreeMap<String,String> searchQuery;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		searchTabViewPager = (CustomViewPager) findViewById(R.id.viewpager);
		configureViewPager();

		// configure searchTabLayout:
		// TabLayout mangatur horizontal layout untuk menampilkan tabs.
		searchTabLayout = (TabLayout) findViewById(R.id.tabs);
		searchTabLayout.setupWithViewPager(searchTabViewPager);

		inputSearch = (EditText) findViewById(R.id.input_search);
		search = inputSearch.toString();

		inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			//--------------------------------------------------------------------------------
			// Handling saat enter dipencet
			//--------------------------------------------------------------------------------
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| actionId == EditorInfo.IME_ACTION_DONE
					|| event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					// dapatkan String yang dimasukkan User
					String userInput = textView.getText().toString();
					TreeMap<String,String> dataToSend = new TreeMap<>();
					dataToSend.put("query", userInput);

					// dapatkan adapter yang sesuai dengan mode pencarian
					RecyclerView.Adapter targetAdapter = null;
					switch (currentSearchMode) {
						case DEMAND_SEARCH:
							Log.d("DEBUG", "Masuk demand...");
							targetAdapter = SearchDemandFragment.getAdapter();
							break;
						default:
							Log.d("DEBUG", "Masuk supply...");
							targetAdapter = SearchSupplyFragment.getAdapter();
							break;
					}

					Log.d("DEBUG", targetAdapter.toString());
					SearchTask searchTask = new SearchTask(
						SearchActivity.this, currentSearchMode, dataToSend, targetAdapter);
					searchTask.execute();
					Log.d("DEBUG", userInput + " " + currentSearchMode);

					// notify change ke adapter
					targetAdapter.notifyDataSetChanged();
				}
				return false; // pass on to other listeners.
			}
		});
	}

	/** ==============================================================================
	 * Mengatur searchTabViewPager: mengatur adapter (penampung fragment) da
	 * action listener-nya
	 * ============================================================================== */
	private void configureViewPager() {
		// NOTE: kelas CustomViewPagerAdapter didekarasikan di kelas terpisah
		// NOTE: inner class ViewPagerListener dideklarasikan di bawah

		// mengonfigurasi adapter untuk MenuTabViewPager
		// syntax: addFragment(fragment, fragmentTitle)
		// fragmentTitle sengaja kosong agar tidak ada teks di sebelah icon
		Fragment searchDemandFragment = new SearchDemandFragment();
		Fragment searchsupplyFragment = new SearchSupplyFragment();
		Fragment searchUserFragment = new SearchUserFragment();

		// mengonfigurasi adapter untuk MenuTabViewPager
		// syntax: addFragment(fragment, fragmentTitle)
		CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(searchDemandFragment, "Permintaan");
		adapter.addFragment(searchsupplyFragment, "Penawaran");
		adapter.addFragment(searchUserFragment, "Pengguna");
		searchTabViewPager.setAdapter(adapter);

		Bundle data = new Bundle();
		data.putString("query", search);
		searchDemandFragment.setArguments(data);
		searchsupplyFragment.setArguments(data);
		searchUserFragment.setArguments(data);

		// menambahkan action listener untuk MenuTabViewPager
		searchTabViewPager.addOnPageChangeListener(new ViewPagerListener());
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
					currentSearchMode = DEMAND_SEARCH;
					break;
				case 1:
					// dipencet tab item index 1: penawaran
					currentSearchMode = SUPPLY_SEARCH;
					break;
				case 2:
					// dipencet tab item index 2: penawaran
					currentSearchMode = USER_SEARCH;
					break;
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageScrollStateChanged(int state) {}
	}
}
