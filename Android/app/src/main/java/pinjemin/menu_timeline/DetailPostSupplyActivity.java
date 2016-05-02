package pinjemin.menu_timeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pinjemin.activity.MainActivity;
import pinjemin.model.PostDemand;
import pinjemin.R;
import pinjemin.session.SessionManager;


public class DetailPostSupplyActivity extends AppCompatActivity
{
	private TextView pembuatPost, tanggal, namaBarang, deskripsi, harga;
	private RelativeLayout calonPeminjam, pemberiPinjam;
	private LinearLayout btnMintaPinjem, btnLihatProfil, btnUbah, btnHapus;
	private Toolbar toolbar;
	private SessionManager session;
	private PostDemand postDemand;
	private String postMakerUid, intentTimestamp, intentNamaBarang, intentDeskripsi, intentHarga, intentAccountName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_post_supply);

		session = new SessionManager(this);
		String currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

		// initialize toolbar:
		// set judul dan jadikan dia main toolbar di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Detail Post Supply");
		setSupportActionBar(toolbar);

		pembuatPost = (TextView) findViewById(R.id.pembuatPost);
		tanggal = (TextView) findViewById(R.id.tanggal);
		namaBarang = (TextView) findViewById(R.id.namaBarang);
		deskripsi = (TextView) findViewById(R.id.deskripsi);
		harga = (TextView) findViewById(R.id.harga);

		calonPeminjam = (RelativeLayout) findViewById(R.id.calon_peminjam);
		pemberiPinjam = (RelativeLayout) findViewById(R.id.pemberi_pinjam);

		btnMintaPinjem = (LinearLayout) findViewById(R.id.btn_mintaPinjem);
		btnLihatProfil = (LinearLayout) findViewById(R.id.btn_lihatProfil);
		btnUbah = (LinearLayout) findViewById(R.id.btn_ubah);
		btnHapus = (LinearLayout) findViewById(R.id.btn_hapus);

		// ambil detail post dari intent
		Intent intent = getIntent();
		postMakerUid = intent.getStringExtra("uid");
		intentTimestamp = intent.getStringExtra("timestamp");
		intentAccountName = intent.getStringExtra("accountName");
		intentNamaBarang = intent.getStringExtra("namaBarang");
		intentDeskripsi = intent.getStringExtra("deskripsi");
		intentHarga = intent.getStringExtra("harga");

		// set TextView values:
		// gunakan data yang di-extract dari intent di atas
		pembuatPost.setText(intentAccountName);
		tanggal.setText(intentTimestamp);
		namaBarang.setText(intentNamaBarang);
		deskripsi.setText(intentDeskripsi);
		harga.setText(intentHarga);

		// set action buttons:
		if (postMakerUid.equalsIgnoreCase(currentUid)) {
			calonPeminjam.setVisibility(View.GONE);

			btnMintaPinjem.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					ubah();
				}
			});

			btnLihatProfil.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					hapus();
				}
			});

		}
		else {
			pemberiPinjam.setVisibility(View.GONE);

			btnMintaPinjem.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					mintaPinjem();
				}
			});

			btnLihatProfil.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					lihatProfil();
				}
			});
		}
	}


	// --- action handlers ---

	public void mintaPinjem() {
		String uidPemberi = session.getUserDetails().get(SessionManager.KEY_UID);

		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	public void lihatProfil() {
		String uidPeminjam = postMakerUid;
	}

	public void ubah() {

	}

	public void hapus() {

	}
}
