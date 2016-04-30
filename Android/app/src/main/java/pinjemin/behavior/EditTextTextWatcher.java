/** ===================================================================================
 * [EDITTEXT TEXT WATCHER]
 * TextWatcher untuk objek EditText. Memastikan bahwa nilai field EditText tidak kosong
 * setelah di-edit.
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.behavior;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import pinjemin.utility.UtilityGUI;


public class EditTextTextWatcher implements TextWatcher
{
	private Activity activity;
	private View view;
	private EditText editText;
	private TextInputLayout editTextLayout;
	private String errorMessage;


	/** ==============================================================================
	 * Constructor kelas EditTextTextWatcher
	 * @param activity - activity dari mana method ini dipanggil
	 * @param view - objek view (EditText) yang akan dicek apakah kosong atau tidak
	 * @param editTextLayout - layout yang digunakan oleh view
	 * @param errorMessage - pesan error yang ditampilkan jika field ini kosong
	 * @return true jika tidak kosong, false jika kosong.
	 * ============================================================================== */
	public EditTextTextWatcher(Activity activity, View view,
		TextInputLayout editTextLayout, String errorMessage
	) {
		this.activity = activity;
		this.view = view;
		this.editText = (EditText) view;
		this.editTextLayout = editTextLayout;
		this.errorMessage = errorMessage;
	}

	public void afterTextChanged(Editable editable) {
		UtilityGUI.assureNotEmpty(activity, editText, editTextLayout, errorMessage);
	}

	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
}

