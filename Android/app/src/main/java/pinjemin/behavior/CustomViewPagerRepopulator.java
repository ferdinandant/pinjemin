/** ===================================================================================
 * [CUSTOM VIEW PAGER REPOPULATOR]
 * Normalnya, saat sudah berpindah menu tab, pageViewer pada submenu tab tidak lagi
 * menampilkan list items-nya. Kelas ini dibuat untuk menyelesaikan permasalahan
 * tersebut. (Google API error?)
 * ----------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan, Ferdinand Antonius
 * http://stackoverflow.com/questions/31544617/tabs-of-tablayout-not-showing
 * =================================================================================== */

package pinjemin.behavior;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;


public class CustomViewPagerRepopulator implements Runnable
{
	private TabLayout tabLayout;
	private ViewPager viewPager;


	/** ==============================================================================
	 * Constructor kelas CustomViewPagerRepopulator
	 * ============================================================================== */
	public CustomViewPagerRepopulator(TabLayout tabLayout, ViewPager viewPager) {
		this.tabLayout = tabLayout;
		this.viewPager = viewPager;
	}

	@Override
	public void run() {
		tabLayout.setupWithViewPager(viewPager);
	}
}
