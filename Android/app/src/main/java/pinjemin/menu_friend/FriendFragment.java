package pinjemin.menu_friend;

import android.os.Bundle;
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


public class FriendFragment extends Fragment
{

	private TabLayout tabLayout;
	private CustomViewPager viewPager;

	public FriendFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState
	) {
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

	private void setupViewPager(ViewPager viewPager) {
		CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(
			getChildFragmentManager());

		adapter.addFragment(new FriendTemanAndaFragment(), "Teman Anda");
		adapter.addFragment(new FriendRequestFragment(), "Request Add");

		Log.d("debug", "bikin tab");
		viewPager.setAdapter(adapter);

	}
}
