package pinjemin.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pinjemin.R;
import pinjemin.session.SessionManager;

public class DetailProfilActivity extends AppCompatActivity {

    private TextView outputRealname, outputAccountname, outputRating, outputNumRating,
            outputBio, outputFakultas, outputProdi, outputTelepon;

    private TextView btnUbahProfil, btnSetujuRequest, btnTolakRequest,
            btnBatalRequest, btnTambahTeman, btnHapusTeman;

    private LinearLayout setujuTolak;

    private String uid, realName, accountName, bio, fakultas, prodi, telepon, rating, numRating, status;

    private Toolbar toolbar;

    private SessionManager sessionManager;

    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profil);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Profil");
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        currentUid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);

        outputRealname = (TextView) findViewById(R.id.output_realname);
        outputAccountname = (TextView) findViewById(R.id.output_accountname);
        outputBio = (TextView) findViewById(R.id.output_bio);
        outputFakultas = (TextView) findViewById(R.id.output_fakultas);
        outputProdi = (TextView) findViewById(R.id.output_prodi);
        outputTelepon = (TextView) findViewById(R.id.output_telepon);
        outputRating = (TextView) findViewById(R.id.output_rating);
        outputNumRating = (TextView) findViewById(R.id.output_numrating);

        btnUbahProfil = (TextView) findViewById(R.id.btn_ubah_profil);
        btnSetujuRequest = (TextView) findViewById(R.id.btn_setuju_request);
        btnTolakRequest = (TextView) findViewById(R.id.btn_tolak_request);
        btnBatalRequest = (TextView) findViewById(R.id.btn_batal_request);
        btnTambahTeman = (TextView) findViewById(R.id.btn_tambah_teman);
        btnHapusTeman = (TextView) findViewById(R.id.btn_hapus_teman);

        setujuTolak = (LinearLayout) findViewById(R.id.setuju_tolak);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        realName = intent.getStringExtra("realName");
        accountName = intent.getStringExtra("accountName");
        bio = intent.getStringExtra("bio");
        fakultas = intent.getStringExtra("fakultas");
        prodi = intent.getStringExtra("prodi");
        telepon = intent.getStringExtra("telepon");
        rating = intent.getStringExtra("rating");
        numRating = intent.getStringExtra("numRating");
        status = intent.getStringExtra("status");

        outputRealname.setText(realName);
        outputAccountname.setText(accountName);
        outputBio.setText(bio);
        outputFakultas.setText(fakultas);
        outputProdi.setText(prodi);
        outputTelepon.setText(telepon);
        outputRating.setText(rating);
        outputNumRating.setText(numRating);

        if (status.equalsIgnoreCase("ownProfile")) {
            btnUbahProfil.setVisibility(View.VISIBLE);

            btnUbahProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ubahProfil();
                }
            });
        }
    }

    public void ubahProfil() {

    }
}
