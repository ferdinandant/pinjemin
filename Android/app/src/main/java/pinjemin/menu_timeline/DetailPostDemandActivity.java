package pinjemin.menu_timeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.TreeMap;

import pinjemin.activity.MainActivity;
import pinjemin.backgroundTask.GetProfilTask;
import pinjemin.model.PostDemand;
import pinjemin.R;
import pinjemin.session.SessionManager;


public class DetailPostDemandActivity extends AppCompatActivity
{
	private TextView pembuatPost, tanggal, namaBarang, deskripsi, lastNeed;
	private RelativeLayout pemberiPinjam, calonPeminjam;
	private LinearLayout btnKasihPinjem, btnLihatProfil, btnUbah, btnHapus;
	private Toolbar toolbar;
	private SessionManager session;
	private PostDemand postDemand;
	private String intentUid, intentTimestamp, intentNamaBarang, intentDeskripsi, intentLastNeed, intentAccountName;
	String currentUid, postMakerUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_post_demand);

		session = new SessionManager(this);
		currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

		// initialize toolbar:
		// set judul dan jadikan dia main toolbar di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Detail Post Demand");
		setSupportActionBar(toolbar);

		// initialize layouts:
		calonPeminjam = (RelativeLayout) findViewById(R.id.calon_peminjam);
		pemberiPinjam = (RelativeLayout) findViewById(R.id.pemberi_pinjam);
		btnKasihPinjem = (LinearLayout) findViewById(R.id.btn_kasihPinjem);
		btnLihatProfil = (LinearLayout) findViewById(R.id.btn_lihatProfil);
		btnUbah = (LinearLayout) findViewById(R.id.btn_ubah);
		btnHapus = (LinearLayout) findViewById(R.id.btn_hapus);

		// initialize components:
		pembuatPost = (TextView) findViewById(R.id.pembuatPost);
		tanggal = (TextView) findViewById(R.id.tanggal);
		namaBarang = (TextView) findViewById(R.id.namaBarang);
		deskripsi = (TextView) findViewById(R.id.deskripsi);
		lastNeed = (TextView) findViewById(R.id.lastNeed);

		// ambil detail post dari intent
		Intent intent = getIntent();
		postMakerUid = intent.getStringExtra("uid");
		intentTimestamp = intent.getStringExtra("timestamp");
		intentAccountName = intent.getStringExtra("accountName");
		intentNamaBarang = intent.getStringExtra("namaBarang");
		intentDeskripsi = intent.getStringExtra("deskripsi");
		intentLastNeed = intent.getStringExtra("lastNeed");

		// set TextView values:
		// gunakan data yang di-extract dari intent di atas
		pembuatPost.setText(intentAccountName);
		tanggal.setText(intentTimestamp);
		namaBarang.setText(intentNamaBarang);
		deskripsi.setText(intentDeskripsi);
		lastNeed.setText(intentLastNeed);

		// set action buttons:
		if (postMakerUid.equalsIgnoreCase(currentUid)) {
			pemberiPinjam.setVisibility(View.GONE);

			btnUbah.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					ubah();
				}
			});

			btnHapus.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					hapus();
				}
			});

		}
		else {
			calonPeminjam.setVisibility(View.GONE);

			btnKasihPinjem.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					kasihPinjem();
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

	public void kasihPinjem() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	public void lihatProfil() {
		TreeMap<String,String> input = new TreeMap<>();
		input.put("ownUID", currentUid);
		input.put("targetUID", postMakerUid);

		GetProfilTask getProfilTask = new GetProfilTask(this, input);
		getProfilTask.execute();

		finish();
	}

	public void ubah() {

	}

	public void hapus() {

	}
}
