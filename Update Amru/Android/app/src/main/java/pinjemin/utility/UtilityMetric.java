/** ===================================================================================
 * [UTILITY CONNECTION]
 * Helper class untuk melakukan perhitunga metris (pixel height, etc.)
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.utility;

import android.content.Context;
import android.content.res.TypedArray;

import pinjemin.R;


public class UtilityMetric
{
	/** ==============================================================================
	 * Mendapatkan tinggi toolbar (action bar)
	 * @return tinggi toolbar (action bar)
	 * ============================================================================== */
	public static int getToolbarHeight(Context context) {
		// ambil instance objek toolbar (setelah di-stylized?)
		// masukkan ke array styledAttributes
		final TypedArray styledAttributes =
			context.getTheme().obtainStyledAttributes(new int[] {R.attr.actionBarSize});

		// ambil dimensi (ukuran) R.attr.actionBarSize dari array styledAttributes
		// Syntax: getDimension(arrayIndex, defaultValueIfNotFound)
		int toolbarHeight = (int) styledAttributes.getDimension(0, 0);

		// destroy styledAttributes
		styledAttributes.recycle();

		return toolbarHeight;
	}
}
