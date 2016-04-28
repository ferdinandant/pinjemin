/** ===================================================================================
 * [LOGIN ACTIVITY]
 * Kelas yang menampilkan halaman login
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.TreeMap;

import pinjemin.backgroundTask.LoginTask;
import pinjemin.R;


public class LoginActivity extends AppCompatActivity
{
	private Toolbar toolbar;
	private EditText inputName, inputPassword;
	private TextInputLayout inputLayoutName, inputLayoutPassword;
	private Button buttonSignIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// initialize toolbar
		// set toolbar sebagai action bar (main toolbar) untuk activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// initialize layouts
		inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
		inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

		// initialize components
		inputName = (EditText) findViewById(R.id.input_name);
		inputPassword = (EditText) findViewById(R.id.input_password);
		buttonSignIn = (Button) findViewById(R.id.btn_signup);

		// NOTE: inner class MyTextWatcher diimplementasikan  di bawah
		inputName.addTextChangedListener(new MyTextWatcher(inputName));
		inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

		// NOTE: inner class ButtonSignInListener diimplementasikan di bawah
		buttonSignIn.setOnClickListener(new ButtonSignInListener());
	}

	/** ==============================================================================
	 * Memvalidasi field username
	 * @return true jika username valid (tidak kosong), false jika tidak
	 * ============================================================================== */
	private boolean validateName() {
		if (inputName.getText().toString().trim().isEmpty()) {
			inputLayoutName.setError("Masukkan Username Anda");
			requestFocus(inputName);
			return false;
		}
		else {
			inputLayoutName.setErrorEnabled(false);
			return true;
		}
	}

	/** ==============================================================================
	 * Memvalidasi field password
	 * @return true jika password valid (tidak kosong), false jika tidak
	 * ============================================================================== */
	private boolean validatePassword() {
		if (inputPassword.getText().toString().trim().isEmpty()) {
			inputLayoutPassword.setError("Masukkan Password Anda");
			requestFocus(inputPassword);
			return false;
		}
		else {
			inputLayoutPassword.setErrorEnabled(false);
			return true;
		}
	}

	/** ==============================================================================
	 * Memasang focus pada suatu View component
	 * ============================================================================== */
	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas TextWatcher, untuk memantau perubahan state
	 * pada inputName dan inputPassword
	 * ============================================================================== */
	private class MyTextWatcher implements TextWatcher
	{
		private View view;

		private MyTextWatcher(View view) {
			this.view = view;
		}

		@Override
		public void afterTextChanged(Editable editable) {
			switch (view.getId()) {
				case R.id.input_name:
					validateName();
					break;
				case R.id.input_password:
					validatePassword();
					break;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	}

	/** ==============================================================================
	 * Custom implementation kelas View.OnClickListener, digunakan
	 * sebagai action listener untuk buttonSignIn (saat tombol tersebut ditekan)
	 * ============================================================================== */
	private class ButtonSignInListener implements View.OnClickListener
	{
		@Override
		//--------------------------------------------------------------------------------
		// saat tombol sign-in ditekan, submit form-nya
		//--------------------------------------------------------------------------------
		public void onClick(View view) {
			// kalau form tidak valid, jangan lakukan apa-apa lagi
			if (!validateName()) return;
			if (!validatePassword()) return;

			// ambil username dan password dari text field
			String username = inputName.getText().toString();
			String password = inputPassword.getText().toString();

			// susun informasi login yang akan dikirim ke server
			TreeMap<String,String> loginData = new TreeMap<String,String>();
			loginData.put("username", username);
			loginData.put("password", password);

			// kirimkan data login ke server pada background
			// LoginTask loginTask = new LoginTask(LoginActivity.this, "login.php", username, password);
			LoginTask loginTask = new LoginTask(LoginActivity.this, loginData);
			loginTask.execute();
		}
	}
}