package pinjemin.timeline;

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

import pinjemin.backgroundTask.CreatePostTask;
import pinjemin.R;
import pinjemin.session.SessionManager;

public class CreatePostSupply extends AppCompatActivity {

    private EditText inputNamaBarang, inputDeskripsi, inputHarga;
    private TextInputLayout inputLayoutNamaBarang, inputLayoutDeskripsi, inputLayoutHarga;
    private Toolbar toolbar;
    private Button btnSubmit;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_supply);

        session = new SessionManager(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Membuat Penawaran");
        setSupportActionBar(toolbar);

        inputLayoutNamaBarang = (TextInputLayout) findViewById(R.id.input_layout_namaBarang);
        inputLayoutDeskripsi = (TextInputLayout) findViewById(R.id.input_layout_deskripsi);
        inputLayoutHarga = (TextInputLayout) findViewById(R.id.input_layout_harga);

        inputNamaBarang = (EditText) findViewById(R.id.input_namaBarang);
        inputDeskripsi = (EditText) findViewById(R.id.input_deskripsi);
        inputHarga = (EditText) findViewById(R.id.input_harga);

        btnSubmit = (Button) findViewById(R.id.btn_submit);

        inputNamaBarang.addTextChangedListener(new MyTextWatcher(inputNamaBarang));
        inputDeskripsi.addTextChangedListener(new MyTextWatcher(inputDeskripsi));
        inputHarga.addTextChangedListener(new MyTextWatcher(inputHarga));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        if (!validateInput(inputNamaBarang, inputLayoutNamaBarang, "Masukkan Nama Barang")) {
            return;
        }

        if (!validateInput(inputDeskripsi, inputLayoutDeskripsi, "Masukkan Deskripsi")) {
            return;
        }

        if (!validateInput(inputHarga, inputLayoutHarga, "Masukkan Harga")) {
            return;
        }

        String uid = session.getUserDetails().get(SessionManager.KEY_UID);
        String namaBarang = inputNamaBarang.getText().toString();
        String deskripsi = inputDeskripsi.getText().toString();
        String harga = inputHarga.getText().toString();

        TreeMap<String, String> inputSend = new TreeMap<>();
        inputSend.put("uid", uid);
        inputSend.put("namaBarang", namaBarang);
        inputSend.put("deskripsi", deskripsi);
        inputSend.put("harga", harga);

        CreatePostTask sendTask = new CreatePostTask(this, "createnewpenawaran.php", inputSend);
        sendTask.execute();

        //BackgroundTaskDatabase backgroundTaskDatabase = new BackgroundTaskDatabase(this, "createnewpenawaran.php", "send", inputSend);
        //backgroundTaskDatabase.execute();

        finish();
    }
    private boolean validateInput(EditText input, TextInputLayout inputLayout, String error) {
        if (input.getText().toString().trim().isEmpty()) {
            inputLayout.setError(error);
            requestFocus(input);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
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
                case R.id.input_namaBarang:
                    validateInput(inputNamaBarang, inputLayoutNamaBarang, "Silahkan Masukkan Nama Barang");
                    break;
                case R.id.input_deskripsi:
                    validateInput(inputDeskripsi, inputLayoutDeskripsi, "Silahkan Masukkan Deskripsi");
                    break;
                case R.id.input_harga:
                    validateInput(inputHarga, inputLayoutHarga, "Silahkan Masukkan Harga");
                    break;
            }
        }
    }
}
