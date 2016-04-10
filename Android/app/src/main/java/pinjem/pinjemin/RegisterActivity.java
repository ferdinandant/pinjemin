package pinjem.pinjemin;

import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.TreeMap;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName, inputFakultas, inputProdi, inputTelepon, inputBio;
    private TextInputLayout inputLayoutName, inputLayoutFakultas, inputLayoutProdi, inputLayoutTelepon, inputLayoutBio;
    private Button btnSubmit;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Registrasi");
        setSupportActionBar(toolbar);

        session = new SessionManager(this);

        inputLayoutName     = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutFakultas = (TextInputLayout) findViewById(R.id.input_layout_fakultas);
        inputLayoutProdi    = (TextInputLayout) findViewById(R.id.input_layout_prodi);
        inputLayoutTelepon  = (TextInputLayout) findViewById(R.id.input_layout_telepon);
        inputLayoutBio      = (TextInputLayout) findViewById(R.id.input_layout_bio);

        inputName     = (EditText) findViewById(R.id.input_name);
        inputFakultas = (EditText) findViewById(R.id.input_fakultas);
        inputProdi    = (EditText) findViewById(R.id.input_prodi);
        inputTelepon  = (EditText) findViewById(R.id.input_telepon);
        inputBio      = (EditText) findViewById(R.id.input_bio);

        btnSubmit = (Button) findViewById(R.id.btn_submit);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }


    private void submitForm() {
        // kalau ada yang nggak valid, gagal.
        if (!validateName()) return;
        if (!validateFakultas()) return;
        if (!validateProdi()) return;
        if (!validateTelepon()) return;
        if (!validateBio()) return;

        // get value-nya dari textbox
        String realname = inputName.getText().toString();
        String fakultas = inputFakultas.getText().toString();
        String prodi = inputProdi.getText().toString();
        String telepon = inputTelepon.getText().toString();
        String bio = inputBio.getText().toString();

        // dapatkan uid user
        HashMap<String, String> user = session.getUserDetails();
        String uid = user.get(SessionManager.KEY_UID);

        // masukan semua parameters ke treemap
        TreeMap<String, String> inputSend = new TreeMap<>();
        inputSend.put("uid", uid);
        inputSend.put("realname", realname);
        inputSend.put("fakultas", fakultas);
        inputSend.put("prodi", prodi);
        inputSend.put("telepon", telepon);
        inputSend.put("bio", bio);

        //String[] inputSend = {uid, realname, fakultas, prodi, telepon, bio};

        BackgroundTaskDatabase backgroundTaskDatabase = new BackgroundTaskDatabase(this, "register.php", "register", inputSend);
        backgroundTaskDatabase.execute();

    }

    /** ==============================================================================
     * Mengecek apakah field nama valid
     * -------------------------------------------------------------------------------
     * @return: true jika valid, false jika tidak.
     * ============================================================================== */
    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Masukkan Nama Lengkap Anda");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    /** ==============================================================================
     * Mengecek apakah field fakultas valid
     * -------------------------------------------------------------------------------
     * @return: true jika valid, false jika tidak.
     * ============================================================================== */
    private boolean validateFakultas() {
        if (inputFakultas.getText().toString().trim().isEmpty()) {
            inputLayoutFakultas.setError("Masukkan Fakultas Anda");
            requestFocus(inputFakultas);
            return false;
        } else {
            inputLayoutFakultas.setErrorEnabled(false);
        }
        return true;
    }

    /** ==============================================================================
     * Mengecek apakah field prodi valid
     * -------------------------------------------------------------------------------
     * @return: true jika valid, false jika tidak.
     * ============================================================================== */
    private boolean validateProdi() {
        if (inputProdi.getText().toString().trim().isEmpty()) {
            inputLayoutProdi.setError("Masukkan Jurusan Anda");
            requestFocus(inputProdi);
            return false;
        } else {
            inputLayoutProdi.setErrorEnabled(false);
        }
        return true;
    }

    /** ==============================================================================
     * Mengecek apakah field telepon valid
     * -------------------------------------------------------------------------------
     * @return: true jika valid, false jika tidak.
     * ============================================================================== */
    private boolean validateTelepon() {
        if (inputTelepon.getText().toString().trim().isEmpty()) {
            inputLayoutTelepon.setError("Masukkan Nomor Telepon Anda");
            requestFocus(inputTelepon);
            return false;
        } else {
            inputLayoutTelepon.setErrorEnabled(false);
        }
        return true;
    }

    /** ==============================================================================
     * Mengecek apakah field biodata valid
     * -------------------------------------------------------------------------------
     * @return: true jika valid, false jika tidak.
     * ============================================================================== */
    private boolean validateBio() {
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

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
                case R.id.input_bio:
                    validateBio();
                    break;
            }
        }
    }
}