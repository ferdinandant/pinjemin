/** ===================================================================================
 * [CUSTOM FRAGMENT PAGER ADAPTER]
 * Implementasi FragmentPagerAdapter generik. (FragmentPagerAdapter merupakan
 * implementasi PagerAdapter yang setiap page-nya berupa Fragment.)
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.behavior;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class CustomViewPagerAdapter extends FragmentPagerAdapter
{
	private final List<Fragment> fragmentList = new ArrayList<>();
	private final List<String> fragmentTitleList = new ArrayList<>();

	/** ==============================================================================
	 * Constructor kelas CustomViewPagerAdapter
	 * ============================================================================== */
	public CustomViewPagerAdapter(FragmentManager manager) {
		super(manager);
	}

	public void addFragment(Fragment fragment, String title) {
		fragmentList.add(fragment);
		fragmentTitleList.add(title);
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentTitleList.get(position);
	}
}
