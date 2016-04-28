package pinjemin.peminjaman;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pinjemin.R;
import pinjemin.session.SessionManager;

public class DetailPostPeminjaman extends AppCompatActivity {

    private TextView pembuatPost, tanggal, namaBarang, deskripsi, harga, deadline;
    private Button btnUbahStatus, btnLihatProfil;
    private Toolbar toolbar;
    private SessionManager session;
    private String intentUid, intentTimestamp, intentNamaBarang, intentDeskripsi, intentHarga, intentDeadline, intentAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post_peminjaman);

        session = new SessionManager(this);
        String currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Post Peminjaman");
        setSupportActionBar(toolbar);

        pembuatPost = (TextView) findViewById(R.id.pembuatPost);
        tanggal = (TextView) findViewById(R.id.tanggal);
        namaBarang = (TextView) findViewById(R.id.namaBarang);
        deskripsi = (TextView) findViewById(R.id.deskripsi);
        harga = (TextView) findViewById(R.id.harga);

        btnUbahStatus = (Button) findViewById(R.id.btn_ubahStatus);
        btnLihatProfil = (Button) findViewById(R.id.btn_lihatProfil);

        //Isi Post Peminjaman
        Intent intent = getIntent();
        intentUid = intent.getStringExtra("uid");
        intentTimestamp = intent.getStringExtra("timestamp");
        intentAccountName = intent.getStringExtra("accountName");
        intentNamaBarang = intent.getStringExtra("namaBarang");
        intentDeskripsi = intent.getStringExtra("deskripsi");
        intentHarga = intent.getStringExtra("harga");
        intentDeadline = intent.getStringExtra("deadline");

        pembuatPost.setText(intentAccountName);
        tanggal.setText(intentTimestamp);
        namaBarang.setText(intentNamaBarang);
        deskripsi.setText(intentDeskripsi);
        harga.setText(intentHarga);
        deadline.setText(intentDeadline);

        btnLihatProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lihatProfil();
            }
        });
    }

    public void lihatProfil() {
        String uidPeminjam = intentUid;
    }

    public void ubahStatus() {

    }
}
