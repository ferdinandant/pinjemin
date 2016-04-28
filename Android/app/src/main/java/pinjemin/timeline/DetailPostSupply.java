package pinjemin.timeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pinjemin.activity.MainActivity;
import pinjemin.model.PostDemand;
import pinjemin.R;
import pinjemin.session.SessionManager;

public class DetailPostSupply extends AppCompatActivity {
    private TextView pembuatPost, tanggal, namaBarang, deskripsi, harga;
    private Button btnMintaPinjem, btnLihatProfil;
    private Toolbar toolbar;
    private SessionManager session;
    private PostDemand postDemand;
    private String intentUid, intentTimestamp, intentNamaBarang, intentDeskripsi, intentHarga, intentAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post_supply);

        session = new SessionManager(this);
        String currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Post Supply");
        setSupportActionBar(toolbar);

        pembuatPost = (TextView) findViewById(R.id.pembuatPost);
        tanggal = (TextView) findViewById(R.id.tanggal);
        namaBarang = (TextView) findViewById(R.id.namaBarang);
        deskripsi = (TextView) findViewById(R.id.deskripsi);
        harga = (TextView) findViewById(R.id.harga);

        btnMintaPinjem = (Button) findViewById(R.id.btn_mintaPinjem);
        btnLihatProfil = (Button) findViewById(R.id.btn_lihatProfil);

        //Isi Post
        Intent intent = getIntent();
        intentUid =  intent.getStringExtra("uid");
        intentTimestamp = intent.getStringExtra("timestamp");
        intentAccountName = intent.getStringExtra("accountName");
        intentNamaBarang = intent.getStringExtra("namaBarang");
        intentDeskripsi = intent.getStringExtra("deskripsi");
        intentHarga = intent.getStringExtra("harga");

        pembuatPost.setText(intentAccountName);
        tanggal.setText(intentTimestamp);
        namaBarang.setText(intentNamaBarang);
        deskripsi.setText(intentDeskripsi);
        harga.setText(intentHarga);

        if (intentUid.equalsIgnoreCase(currentUid)) {
            btnMintaPinjem.setText("Ubah");

            btnLihatProfil.setText("Hapus");

            btnMintaPinjem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ubah();
                }
            });

            btnLihatProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hapus();
                }
            });

        } else {
            btnMintaPinjem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mintaPinjem();
                }
            });

            btnLihatProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lihatProfil();
                }
            });
        }
    }

    public void mintaPinjem() {
        String uidPemberi = session.getUserDetails().get(SessionManager.KEY_UID);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void lihatProfil() {
        String uidPeminjam = intentUid;
    }

    public void ubah() {

    }

    public void hapus() {

    }
}
