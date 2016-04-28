package pinjemin.timeline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TreeMap;

import pinjemin.backgroundTask.CreatePostTask;
import pinjemin.R;
import pinjemin.session.SessionManager;

public class CreatePostDemand extends AppCompatActivity {

    private EditText inputNamaBarang, inputDeskripsi, inputHarga;
    private TextInputLayout inputLayoutNamaBarang, inputLayoutDeskripsi, inputLayoutHarga;
    private TextView dateView;
    private View garisBawah;
    private Toolbar toolbar;
    private Button btnSubmit;
    private ImageButton btnBatas;
    private int tahun, bulan, hari;
    private SessionManager session;
    private static final int DILOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_demand);
        session = new SessionManager(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Membuat Permintaan");
        setSupportActionBar(toolbar);

        final Calendar cal = Calendar.getInstance();
        tahun = cal.get(Calendar.YEAR);
        bulan = cal.get(Calendar.MONTH);
        hari = cal.get(Calendar.DAY_OF_MONTH);

        inputLayoutNamaBarang = (TextInputLayout) findViewById(R.id.input_layout_namaBarang);
        inputLayoutDeskripsi = (TextInputLayout) findViewById(R.id.input_layout_deskripsi);
        inputLayoutHarga = (TextInputLayout) findViewById(R.id.input_layout_harga);

        inputNamaBarang = (EditText) findViewById(R.id.input_namaBarang);
        inputDeskripsi = (EditText) findViewById(R.id.input_deskripsi);
        inputHarga = (EditText) findViewById(R.id.input_harga);

        dateView = (TextView) findViewById(R.id.dateView);
        garisBawah = findViewById(R.id.garisBawah);

        dateView.setText((bulan + 1) + "/" + hari + "/" + tahun);

        btnBatas = (ImageButton) findViewById(R.id.btn_batas);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        inputNamaBarang.addTextChangedListener(new MyTextWatcher(inputNamaBarang));
        inputDeskripsi.addTextChangedListener(new MyTextWatcher(inputDeskripsi));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        btnBatas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DILOG_ID);
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DILOG_ID);
            }
        });

    }

    @Override
    public Dialog onCreateDialog(int id) {
        if (id == DILOG_ID) {
            return new DatePickerDialog(this,datePickerDialog, tahun, bulan, hari);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tahun = year;
            bulan = monthOfYear;
            hari = dayOfMonth;

            dateView.setText((bulan + 1) + "/" + hari + "/"  + tahun);
            garisBawah.setMinimumWidth(dateView.getWidth());
            //Toast.makeText(getApplicationContext(), tahun + "-" + bulan + 1 + "-" + hari, Toast.LENGTH_LONG).show();
        }
    };

    private void submitForm() {
        if (!validateInput(inputNamaBarang, inputLayoutNamaBarang, "Masukkan Nama Barang")) {
            return;
        }

        if (!validateInput(inputDeskripsi, inputLayoutDeskripsi, "Masukkan Deskripsi")) {
            return;
        }

        bulan = bulan + 1;
        String uid = session.getUserDetails().get(SessionManager.KEY_UID);
        String namaBarang = inputNamaBarang.getText().toString();
        String deskripsi = inputDeskripsi.getText().toString();
        String batas = tahun + "-" + bulan + "-" + hari;

        TreeMap<String, String> inputSend = new TreeMap<>();
        inputSend.put("uid", uid);
        inputSend.put("namaBarang", namaBarang);
        inputSend.put("deskripsi", deskripsi);
        inputSend.put("lastNeed", batas);

        CreatePostTask sendTask = new CreatePostTask(this, "cratenewpermintaan.php", inputSend);
        sendTask.execute();

        //BackgroundTaskDatabase backgroundTaskDatabase = new BackgroundTaskDatabase(this, "createnewpermintaan.php", "send", inputSend);
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
            }
        }
    }
}
