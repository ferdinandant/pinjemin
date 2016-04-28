/** ===================================================================================
 * [REGISTER ACTIVITY]
 * Kelas yang menampilkan halaman untuk register
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactor & Documentation: Ferdinand Antonius
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

import java.util.HashMap;
import java.util.TreeMap;

import pinjemin.backgroundTask.RegisterTask;
import pinjemin.R;
import pinjemin.session.SessionManager;


public class RegisterActivity extends AppCompatActivity
{
	private Toolbar toolbar;
	private SessionManager sessionManager;

	private TextInputLayout inputLayoutName;
	private TextInputLayout inputLayoutFakultas;
	private TextInputLayout inputLayoutProdi;
	private TextInputLayout inputLayoutTelepon;
	private TextInputLayout inputLayoutBio;

	private EditText inputName;
	private EditText inputFakultas;
	private EditText inputProdi;
	private EditText inputTelepon;
	private EditText inputBio;
	private Button buttonSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// initialize toolbar:
		// set toolbar sebagai action bar (main toolbar) untuk activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Registrasi");
		setSupportActionBar(toolbar);

		// initialize sessionManager:
		sessionManager = new SessionManager(this);

		// initialize layout:
		inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
		inputLayoutFakultas = (TextInputLayout) findViewById(R.id.input_layout_fakultas);
		inputLayoutProdi = (TextInputLayout) findViewById(R.id.input_layout_prodi);
		inputLayoutTelepon = (TextInputLayout) findViewById(R.id.input_layout_telepon);
		inputLayoutBio = (TextInputLayout) findViewById(R.id.input_layout_bio);

		// initialize components:
		inputName = (EditText) findViewById(R.id.input_name);
		inputFakultas = (EditText) findViewById(R.id.input_fakultas);
		inputProdi = (EditText) findViewById(R.id.input_prodi);
		inputTelepon = (EditText) findViewById(R.id.input_telepon);
		inputBio = (EditText) findViewById(R.id.input_bio);
		buttonSubmit = (Button) findViewById(R.id.btn_submit);

		// set textWatcher ke semua component Text:
		// (Field bio tidak perlu di-validate)
		inputName.addTextChangedListener(new MyTextWatcher(inputName));
		inputFakultas.addTextChangedListener(new MyTextWatcher(inputFakultas));
		inputProdi.addTextChangedListener(new MyTextWatcher(inputProdi));
		inputTelepon.addTextChangedListener(new MyTextWatcher(inputTelepon));

		// NOTE: inner class ButtonSubmitListener diimplementasikan di bawah
		buttonSubmit.setOnClickListener(new ButtonSubmitListener());
	}

	/** ==============================================================================
	 * Memvalidasi field name
	 * @return true jika valid (tidak kosong), false jika tidak
	 * ============================================================================== */
	private boolean validateName() {
		if (inputName.getText().toString().trim().isEmpty()) {
			inputLayoutName.setError("Masukkan nama lengkap Anda");
			requestFocus(inputName);
			return false;
		}
		else {
			inputLayoutName.setErrorEnabled(false);
			return true;
		}
	}

	/** ==============================================================================
	 * Memvalidasi field fakiltas
	 * @return true jika valid (tidak kosong), false jika tidak
	 * ============================================================================== */
	private boolean validateFakultas() {
		if (inputFakultas.getText().toString().trim().isEmpty()) {
			inputLayoutFakultas.setError("Masukkan fakultas Anda");
			requestFocus(inputFakultas);
			return false;
		}
		else {
			inputLayoutFakultas.setErrorEnabled(false);
			return true;
		}
	}

	/** ==============================================================================
	 * Memvalidasi field prodi
	 * @return true jika valid (tidak kosong), false jika tidak
	 * ============================================================================== */
	private boolean validateProdi() {
		if (inputProdi.getText().toString().trim().isEmpty()) {
			inputLayoutProdi.setError("Masukkan jurusan Anda");
			requestFocus(inputProdi);
			return false;
		}
		else {
			inputLayoutProdi.setErrorEnabled(false);
			return true;
		}
	}

	/** ==============================================================================
	 * Memvalidasi field telepon
	 * @return true jika valid (tidak kosong dan hanya mengandung angka), false jika tidak
	 * ============================================================================== */
	private boolean validateTelepon() {
		String inputTeleponContent = inputTelepon.getText().toString().trim();

		// kalau kosong, maka salah
		if (inputTeleponContent.isEmpty()) {
			inputLayoutTelepon.setError("Masukkan nomor telepon Anda");
			requestFocus(inputTelepon);
			return false;
		}

		// kalau tidak bisa di-parse ke angka, maka salah
		try {
			long inputTeleponParse = Long.parseLong(inputTeleponContent);
		}
		catch (Exception e) {
			inputLayoutTelepon.setError("Masukkan angka aja untuk nomor telepon");
			requestFocus(inputTelepon);
			return false;
		}

		inputLayoutTelepon.setErrorEnabled(false);
		return true;
	}

	/** ==============================================================================
	 * Memasang focus pada suatu View component
	 * ============================================================================== */
	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas TextWatcher, untuk memantau perubahan state
	 * pada setiap EditText fields
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
				case R.id.input_fakultas:
					validateFakultas();
					break;
				case R.id.input_prodi:
					validateProdi();
					break;
				case R.id.input_telepon:
					validateTelepon();
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
	 * sebagai action listener untuk buttonSubmit (saat tombol tersebut ditekan)
	 * ============================================================================== */
	private class ButtonSubmitListener implements View.OnClickListener
	{
		@Override
		public void onClick(View view) {
			// jika ada field yang tidak valid, jangan lakukan apa pun
			if (!validateName()) return;
			if (!validateFakultas()) return;
			if (!validateProdi()) return;
			if (!validateTelepon()) return;

			// ambil data yang dimasukkan user
			String realname = inputName.getText().toString();
			String fakultas = inputFakultas.getText().toString();
			String prodi = inputProdi.getText().toString();
			String telepon = inputTelepon.getText().toString();
			String bio = inputBio.getText().toString();

			// ambil data UID dari sessionManager
			HashMap<String,String> sessionData = sessionManager.getUserDetails();
			String uid = sessionData.get(SessionManager.KEY_UID);

			// susun data untuk dikirim ke server
			TreeMap<String,String> dataToSend = new TreeMap<>();
			dataToSend.put("uid", uid);
			dataToSend.put("realname", realname);
			dataToSend.put("fakultas", fakultas);
			dataToSend.put("prodi", prodi);
			dataToSend.put("telepon", telepon);
			dataToSend.put("bio", bio);

			// kirim data user ke server
			RegisterTask registerTask =
				new RegisterTask(RegisterActivity.this, dataToSend);
			registerTask.execute();

			// tutup activity ini
			finish();
		}
	}
}