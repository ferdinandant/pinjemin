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
import pinjemin.behavior.EditTextTextWatcher;
import pinjemin.utility.UtilityGUI;


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
		inputName.addTextChangedListener(new EditTextTextWatcher(
			this, inputName, inputLayoutName, "Masukkan username Anda"));
		inputPassword.addTextChangedListener(new EditTextTextWatcher(
			this, inputPassword, inputLayoutPassword, "Masukkan password Anda"));

		// set action listener (submit form)
		buttonSignIn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				submitForm();
			}
		});
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}


	/** ==============================================================================
	 * Submit data login
	 * ============================================================================== */
	private void submitForm() {
		// kalau form tidak valid, jangan lakukan apa-apa lagi
		if (!UtilityGUI.assureNotEmpty(this, inputName, inputLayoutName,
			"Masukkan username Anda")) return;
		if (!UtilityGUI.assureNotEmpty(this, inputPassword, inputLayoutPassword,
			"Masukkan password Anda")) return;

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