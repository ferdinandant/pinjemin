package pinjemin.timeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pinjemin.activity.MainActivity;
import pinjemin.model.PostDemand;
import pinjemin.R;
import pinjemin.session.SessionManager;

public class DetailPostDemand extends AppCompatActivity {
    private TextView pembuatPost, tanggal, namaBarang, deskripsi, lastNeed;
    private Button btnKasihPinjem, btnLihatProfil;
    private Toolbar toolbar;
    private SessionManager session;
    private PostDemand postDemand;
    private String intentUid, intentTimestamp, intentNamaBarang, intentDeskripsi, intentLastNeed, intentAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post_demand);

        session = new SessionManager(this);
        String currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Post Demand");
        setSupportActionBar(toolbar);

        pembuatPost = (TextView) findViewById(R.id.pembuatPost);
        tanggal = (TextView) findViewById(R.id.tanggal);
        namaBarang = (TextView) findViewById(R.id.namaBarang);
        deskripsi = (TextView) findViewById(R.id.deskripsi);
        lastNeed = (TextView) findViewById(R.id.lastNeed);

        btnKasihPinjem = (Button) findViewById(R.id.btn_kasihPinjem);
        btnLihatProfil = (Button) findViewById(R.id.btn_lihatProfil);

        //Isi Post
        Intent intent = getIntent();
        intentUid =  intent.getStringExtra("uid");
        intentTimestamp = intent.getStringExtra("timestamp");
        intentAccountName = intent.getStringExtra("accountName");
        intentNamaBarang = intent.getStringExtra("namaBarang");
        intentDeskripsi = intent.getStringExtra("deskripsi");
        intentLastNeed = intent.getStringExtra("lastNeed");

        pembuatPost.setText(intentAccountName);
        tanggal.setText(intentTimestamp);
        namaBarang.setText(intentNamaBarang);
        deskripsi.setText(intentDeskripsi);
        lastNeed.setText(intentLastNeed);

        if (intentUid.equalsIgnoreCase(currentUid)) {
            Log.d("current ID", currentUid+ "__" + intentUid);
            btnKasihPinjem.setText("Ubah");

            btnLihatProfil.setText("Hapus");

            btnKasihPinjem.setOnClickListener(new View.OnClickListener() {
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
            btnKasihPinjem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    kasihPinjem();
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

    public void kasihPinjem() {
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
