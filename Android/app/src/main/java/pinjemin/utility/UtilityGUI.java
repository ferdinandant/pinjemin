/** ===================================================================================
 * [UTILITY CONNECTION]
 * Helper class untuk melakukan perhitunga metris (pixel height, etc.)
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import pinjemin.R;


public class UtilityGUI
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

	/** ==============================================================================
	 * Mengecek apakah suatu EditText field ada isinya. Jika kosong, pesan error akan
	 * ditampilkan, dan focus akan diberikan pada objek EditText tersebut.
	 * @param activity - activity dari mana method ini dipanggil
	 * @param input - EditText objek yang akan dicek apakah kosong atau tidak
	 * @param inputLayout - layout yang digunakan oleh EditText
	 * @param errorMessage - pesan error yang ditampilkan jika field ini kosong
	 * @return true jika OK (tidak kosong), false jika error (kosong).
	 * ============================================================================== */
	public static boolean assureNotEmpty(Activity activity, EditText input,
		TextInputLayout inputLayout, String errorMessage
	) {
		if (input.getText().toString().trim().isEmpty()) {
			inputLayout.setError(errorMessage);
			requestFocus(activity, input);
			return false;
		}
		else {
			inputLayout.setErrorEnabled(false);
		}
		return true;
	}

	/** ==============================================================================
	 * Mengecek apakah suatu EditText field berisi data numerik (long). Jika kosong,
	 * pesan error akan ditampilkan, dan focus akan diberikan pada objek EditText tersebut.
	 * @param activity - activity dari mana method ini dipanggil
	 * @param input - EditText objek yang akan dicek apakah datanya numerik
	 * @param inputLayout - layout yang digunakan oleh EditText
	 * @param errorMessage - pesan error yang ditampilkan jika field datanya tidak numerik
	 * @return true jika OK (tidak kosong), false jika error (kosong).
	 * ============================================================================== */
	public static boolean assureNumeric(Activity activity, EditText input,
		TextInputLayout inputLayout, String errorMessage
	) {
		String dataString = input.getText().toString().trim();

		// kalau kosong, maka salah
		if (dataString.isEmpty()) {
			input.setError(errorMessage);
			requestFocus(activity, input);
			return false;
		}

		// kalau tidak bisa di-parse ke angka, maka salah
		try {
			long inputLongParse = Long.parseLong(dataString);
		}
		catch (Exception e) {
			input.setError(errorMessage);
			requestFocus(activity, input);
			return false;
		}

		inputLayout.setErrorEnabled(false);
		return true;
	}

	/** ==============================================================================
	 * Menaruh focus pada suatu objek view
	 * @param activity - activity dari mana method ini dipanggil
	 * @param view - view yang akan diberikan focus.
	 * ============================================================================== */
	public static void requestFocus(Activity activity, View view) {
		if (view.requestFocus()) {
			activity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}
}
