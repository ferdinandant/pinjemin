/** ===================================================================================
 * [CLICK LISTENER]
 * Fragement click interface listener.
 * Dipakai di TimelineDemandFragment dan TimelineSupplyFragment
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */


package pinjemin.behavior;

import android.view.View;


public interface ClickListener
{
	void onClick(View view, int position);

	void onLongClick(View view, int position);
}
