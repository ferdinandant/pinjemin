/** ===================================================================================
 * [CREATE POST SUPPLY ACTIVITY]
 * Activity untuk membuat post penawaran baru
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */


package pinjemin.menu_timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.TreeMap;

import pinjemin.backgroundTask.CreatePostTask;
import pinjemin.R;
import pinjemin.backgroundTask.UbahPostTask;
import pinjemin.behavior.EditTextTextWatcher;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityGUI;


public class UbahPostSupplyActivity extends AppCompatActivity
{
    private EditText inputNamaBarang, inputDeskripsi, inputHarga;
    private TextInputLayout inputLayoutNamaBarang, inputLayoutDeskripsi, inputLayoutHarga;
    private Toolbar toolbar;
    private Button btnSubmit;
    private SessionManager sessionManager;
    private String intentUid, intentPid, intentTimestamp, intentRealName, intentNamaBarang, intentDeskripsi, intentHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_post_supply);

        // initialize session manager
        sessionManager = new SessionManager(this);

        // initialize toolbar:
        // set title dan jadikan toolbar ini Action Bar (main toolbar) di activity ini
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ubah Post Penawaran");
        setSupportActionBar(toolbar);

        // initialize layouts:
        inputLayoutNamaBarang = (TextInputLayout) findViewById(R.id.input_layout_namaBarang);
        inputLayoutDeskripsi = (TextInputLayout) findViewById(R.id.input_layout_deskripsi);
        inputLayoutHarga = (TextInputLayout) findViewById(R.id.input_layout_harga);

        // intialize components:
        inputNamaBarang = (EditText) findViewById(R.id.input_namaBarang);
        inputDeskripsi = (EditText) findViewById(R.id.input_deskripsi);
        inputHarga = (EditText) findViewById(R.id.input_harga);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        // set default text pada harga (Rp0)
        inputHarga.setText("0", TextView.BufferType.EDITABLE);

        // set action listener (text watchers)
        inputNamaBarang.addTextChangedListener(new EditTextTextWatcher(
                this, inputNamaBarang, inputLayoutNamaBarang, "Masukkan nama barang"));
        inputDeskripsi.addTextChangedListener(new EditTextTextWatcher(
                this, inputDeskripsi, inputLayoutDeskripsi, "Masukkan deskripsi"));
        inputHarga.addTextChangedListener(new EditTextTextWatcher(
                this, inputHarga, inputLayoutHarga, "Masukkan harga"));

        // set action listener (submit form)
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        // saat melakukan ubah post
        Intent intent = getIntent();
        intentUid = intent.getStringExtra("uid");
        intentRealName = intent.getStringExtra("realname");
        intentNamaBarang = intent.getStringExtra("namabarang");
        intentTimestamp = intent.getStringExtra("timestamp");
        intentDeskripsi = intent.getStringExtra("deskripsi");
        intentHarga = intent.getStringExtra("harga");
        intentPid = intent.getStringExtra("pid");

        inputNamaBarang.setText(intentNamaBarang);
        inputDeskripsi.setText(intentDeskripsi);
        inputHarga.setText(intentHarga);
    }

    /** ==============================================================================
     * Submit data pembuatan post penawaran
     * ============================================================================== */
    private void submitForm() {
        // cek apakah setiap field sudah diisi dengan benar
        if (!UtilityGUI.assureNotEmpty(this, inputNamaBarang, inputLayoutNamaBarang,
                "Masukkan nama barang")) return;
        if (!UtilityGUI.assureNotEmpty(this, inputDeskripsi, inputLayoutDeskripsi,
                "Masukkan deskripsi")) return;
        if (!UtilityGUI.assureNotEmpty(this, inputHarga, inputLayoutHarga,
                "Masukkan harga")) return;

        // ambil data dari sessionManager dan EditText fields
        String uid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);
        String namaBarang = inputNamaBarang.getText().toString();
        String deskripsi = inputDeskripsi.getText().toString();
        String harga = inputHarga.getText().toString();

        // susun data untuk dikirim ke server
        TreeMap<String,String> inputSend = new TreeMap<>();
        inputSend.put("PID", intentPid);
        inputSend.put("NamaBarang", namaBarang);
        inputSend.put("Deskripsi", deskripsi);
        inputSend.put("Harga", harga);

        // kirim data ke server
        UbahPostTask sendTask = new UbahPostTask(this, UbahPostTask.SUPPLY_POST, inputSend);
        sendTask.execute();

        finish();
    }
}
