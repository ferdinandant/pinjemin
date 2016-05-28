package pinjemin.menu_peminjaman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.GetProfilTask;
import pinjemin.comment.CustomThreadBlock;
import pinjemin.model.Comment;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;
import pinjemin.utility.UtilityDate;


public class DetailPostPeminjamanActivity extends AppCompatActivity
{

	private TextView pembuatPost, tanggal, namaBarang, deskripsi, status, deadline, partnerName;
	private LinearLayout btnUbahStatus, btnLihatProfil;
	private Toolbar toolbar;
	private SessionManager session;
	private String intentUid, intentTimestamp, intentNamaBarang,
		intentDeskripsi, intentDeadline, intentAccountName,
		intentUIDPemberi, intentUIDPenerima, intentRealnamePemberi,
		intentStatus, intentRealnamePenerima, intentPID;
	private String currentUid;
	private JSONArray jsonResponseArrayComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_post_peminjaman);

		session = new SessionManager(this);
		currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

		// initialize toolbar:
		// set judul dan jadikan dia main toolbar di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Detail Post Peminjaman");
		setSupportActionBar(toolbar);

		// initialize layouts:
		pembuatPost = (TextView) findViewById(R.id.pembuatPost);
		tanggal = (TextView) findViewById(R.id.tanggal);
		namaBarang = (TextView) findViewById(R.id.namaBarang);
		deskripsi = (TextView) findViewById(R.id.deskripsi);
		deadline = (TextView) findViewById(R.id.deadline);
		status = (TextView) findViewById(R.id.status);
		partnerName = (TextView) findViewById(R.id.dipinjamkanOleh);

		btnUbahStatus = (LinearLayout) findViewById(R.id.btn_ubahStatus);
		btnLihatProfil = (LinearLayout) findViewById(R.id.btn_lihatProfil);

		// Isi Post Peminjaman
		Intent intent = getIntent();
		intentUid = intent.getStringExtra("UID");
		intentTimestamp = intent.getStringExtra("Timestamp");
		intentAccountName = intent.getStringExtra("RealName");
		intentNamaBarang = intent.getStringExtra("NamaBarang");
		intentDeskripsi = intent.getStringExtra("Deskripsi");
		intentDeadline = intent.getStringExtra("Deadline");
		intentRealnamePemberi = intent.getStringExtra("RealNamePemberi");
		intentUIDPemberi = intent.getStringExtra("UIDPemberi");
		intentUIDPenerima = intent.getStringExtra("UIDPenerima");
		intentStatus = intent.getStringExtra("Status");
		intentRealnamePenerima = intent.getStringExtra("RealNamePenerima");
		intentPID = intent.getStringExtra("PID");

		Log.d("Deadline", intentDeadline);

		// initialize components:
		pembuatPost.setText(intentAccountName);
		tanggal.setText("Diposkan " + UtilityDate.formatTimestampDateOnly(intentTimestamp) + ", jam " + UtilityDate.formatTimestampTimeOnly(intentTimestamp));
		namaBarang.setText(intentNamaBarang);
		deskripsi.setText(intentDeskripsi);
		deadline.setText("Deadline pengembalian tanggal " + UtilityDate.formatTimestampDateOnly(intentDeadline) + ", jam " + UtilityDate.formatTimestampTimeOnly(intentDeadline));
		status.setText("STATUS: " + intentStatus);

		if (currentUid.equalsIgnoreCase(intentUIDPenerima)) {
			btnUbahStatus.setVisibility(View.GONE);
			partnerName.setText("Diberi Pinjam Oleh " + intentRealnamePemberi);
		}
		else {
			partnerName.setText("Dipinjamkan Kepada " + intentRealnamePenerima);
		}

		btnLihatProfil.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				lihatProfil();
			}
		});

		btnUbahStatus.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				ubahStatus();
			}
		});
	}

	public void lihatProfil() {
		TreeMap<String,String> inputData = new TreeMap<>();

		if (currentUid.equalsIgnoreCase(intentUIDPenerima)) {
			inputData.put("ownUID", intentUid);
			inputData.put("targetUID", intentUIDPemberi);

			GetProfilTask task = new GetProfilTask(this, inputData);
			task.execute();
		}
		else {
			inputData.put("ownUID", intentUid);
			inputData.put("targetUID", intentUIDPenerima);

			GetProfilTask task = new GetProfilTask(this, inputData);
			task.execute();
		}
	}

	public void ubahStatus() {
		Intent intent = new Intent(this, UbahStatusActivity.class);
		intent.putExtra("PID", intentPID);

		startActivity(intent);
	}

	/** ==============================================================================
	 * Mengambil data detail post dari server di thread terpisah
	 * ============================================================================== */
	private void loadPeminjamanDetails() {
		final Activity activity = this;
		final Handler handler = new Handler(Looper.getMainLooper());
		Log.d("DEBUG", "Masuk loadPeminjamanDetails");
		Runnable runnable = new Runnable()
		{
			public void run() {
				try {
					// persiapkan data yang mau dikirim ke server
					TreeMap<String,String> dataToSend = new TreeMap<>();
					dataToSend.put("PID", intentPID);
					dataToSend.put("ownUID", session.getUserDetails().get(SessionManager.KEY_UID));

					// kirim data ke server
					String serverResponseComment = UtilityConnection.runPhp("getthreads.php", dataToSend);
					Log.d("DEBUG", serverResponseComment);

					// parse data JSON yang diterima dari server
					JSONObject jsonResponseObjectComment = new JSONObject(serverResponseComment);
					jsonResponseArrayComment = jsonResponseObjectComment.getJSONArray("server_response");

					// update di UI thread
					handler.post(new Runnable()
					{
						public void run() {
							populateComments();
						}
					});
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					Toast.makeText(activity, "Tidak dapat menghubungi server.", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		};

		new Thread(runnable).start();
	}

	/** ==============================================================================
	 * Mengambil data komentar dari server dan menampilkannya pada activity ini
	 * ============================================================================== */
	private void populateComments() {
		LinearLayout commentSectionContainer = (LinearLayout) findViewById(R.id.commentContainer);
		int jsonResponseArrayCommentLength = jsonResponseArrayComment.length();

		ArrayList<CustomThreadBlock> threadBlockArray = new ArrayList<>();
		ArrayList<Comment> commentArray = new ArrayList<>();
		int currentThreadBlockArrayPointer = -1;
		int lastParentUid = -1;

		// kalau tidak ada komentar, cetak pesan tidak ada komentar dan keluar
		if (jsonResponseArrayCommentLength == 0) {
			return;
		}

		// iterate semua komentar di jsonResponseArrayComment,
		// lalu konstruksikan model (CustomThreadBlock dan Comment) yang sesuai
		for (int i = 0; i < jsonResponseArrayCommentLength; i++) {
			try {
				JSONObject commentInstance = jsonResponseArrayComment.getJSONObject(i);

				// untuk UID, bisa null untuk system notification
				// jadi isi nilai default dulu, kalau tidak null, overwrite
				int cid = commentInstance.getInt("CID");
				int uid = Comment.SYSTEM_NOTIFICATION_UID;
				int parentUID = commentInstance.getInt("ParentUID");
				String realName = commentInstance.getString("RealName");
				String timestamp = commentInstance.getString("Timestamp");
				String content = commentInstance.getString("Content");

				if (!commentInstance.isNull("UID")) {
					uid = commentInstance.getInt("UID");
				}

				// jika parentUID sekarang != lastParentUid, berarti harus dimulai thread baru
				// jika lastParentUid == -1, maka artinya belum ada data sebelumnya (ini yang pertama)
				if ((lastParentUid != parentUID) && (lastParentUid != -1)) {
					currentThreadBlockArrayPointer++;

					// hanya perlu action balas
					CustomThreadBlock threadBlock = new CustomThreadBlock(this,
						commentArray, Integer.parseInt(intentPID), lastParentUid,
						CustomThreadBlock.ACTIONS_NONE, Integer.parseInt(intentUid));
					threadBlockArray.add(threadBlock);
					commentArray.clear();
				}

				// in any case, masukkan komentar yang sekarang ke commentArray
				Comment comment = new Comment(realName, timestamp, content, cid, uid);
				commentArray.add(comment);
				lastParentUid = parentUID;
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// thread yang mengandung komentar terakhir belum dimasukkan ke threadBlockArray
		// jadi, masukkan thread tersebut sekarang
		currentThreadBlockArrayPointer++;

		CustomThreadBlock threadBlock = new CustomThreadBlock(this,
			commentArray, Integer.parseInt(intentPID),
			lastParentUid, CustomThreadBlock.ACTIONS_NONE,
			Integer.parseInt(intentUid));
		threadBlockArray.add(threadBlock);
		commentArray.clear();


		// buat view items untuk threads
		int threadBlockArrayLength = threadBlockArray.size();

		for (int i = 0; i < threadBlockArrayLength; i++) {
			CustomThreadBlock threadBlockToPrint = threadBlockArray.get(i);
			commentSectionContainer.addView(threadBlockToPrint.getLinearLayout());
		}
	}
}
