/** ===================================================================================
 * [FLOATING BUTTON BEHAVIOR]
 * Kelas untuk mengatur floating action button (jadi kalau list-nya di-scroll ke atas,
 * tombolnya hilang) -> MASIH BELUM BERHASIL.
 * ----------------------------------------------------------------------------------
 * Author: Ksemal Amru Ramadhan
 * Refactor & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import android.view.View;

import pinjemin.utility.UtilityMetric;


public class FloatingButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>
{
	private int toolbarHeight;

	public FloatingButtonBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.toolbarHeight = UtilityMetric.getToolbarHeight(context);
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent,
		FloatingActionButton fab, View dependency
	) {
		return dependency instanceof AppBarLayout;
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
		if (dependency instanceof AppBarLayout) {
			CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
			int fabBottomMargin = lp.bottomMargin;
			int distanceToScroll = fab.getHeight() + fabBottomMargin;
			float ratio = (float) dependency.getY() / (float) toolbarHeight;
			fab.setTranslationY(-distanceToScroll * ratio);
		}
		return true;
	}
}
