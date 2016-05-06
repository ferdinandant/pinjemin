package pinjemin.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pinjemin.R;
import pinjemin.activity.MainActivity;
import pinjemin.session.SessionManager;


public class DetailProfilActivity extends AppCompatActivity
{

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
		outputTelepon.setText("Tambahkan sebagai teman untuk melihat");
		outputRating.setText(rating);
		outputNumRating.setText(" Berdasarkan " + numRating + " review");

		if (status.equalsIgnoreCase("OwnProfile")) {
			btnUbahProfil.setVisibility(View.VISIBLE);

			btnUbahProfil.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					ubahProfil();
				}
			});

		}
		else if (status.equalsIgnoreCase("NotFriends")) {
			btnTambahTeman.setVisibility(View.VISIBLE);

			btnTambahTeman.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					tambahTeman();
				}
			});

		}
		else if (status.equalsIgnoreCase("Friends")) {
			btnHapusTeman.setVisibility(View.VISIBLE);

			outputTelepon.setText(telepon);

			btnHapusTeman.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					hapusTeman();
				}
			});

		}
		else if (status.equalsIgnoreCase("Requesting")) {
			btnBatalRequest.setVisibility(View.VISIBLE);

			btnBatalRequest.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					batalRequest();
				}
			});

		}
		else if (status.equalsIgnoreCase("Requested")) {
			setujuTolak.setVisibility(View.VISIBLE);

			btnSetujuRequest.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					setujuRequest();
				}
			});

			btnTolakRequest.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					tolakRequest();
				}
			});
		}
	}

	public void ubahProfil() {

	}

	public void tambahTeman() {

	}

	public void hapusTeman() {

	}

	public void batalRequest() {

	}

	public void tolakRequest() {

	}

	public void setujuRequest() {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
