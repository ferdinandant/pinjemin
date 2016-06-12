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
import pinjemin.comment.UbahDeadlineActivity;
import pinjemin.model.Comment;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;
import pinjemin.utility.UtilityDate;


public class DetailPostPeminjamanActivity extends AppCompatActivity
{
	private TextView pembuatPost, tanggal, namaBarang, deskripsi, status, deadline, partnerName;
	private LinearLayout btnUbahStatus, btnLihatProfil, btnUbahDeadline;
	private Toolbar toolbar;
	private SessionManager session;
	private String intentUid, intentTimestamp, intentNamaBarang,
		intentDeskripsi, intentDeadline, intentAccountName,
		intentUIDPemberi, intentUIDPenerima, intentRealnamePemberi,
		intentStatus, intentRealnamePenerima, intentPID, currentUid;

	private JSONArray jsonResponseArrayComment;
	private JSONArray jsonResponseArrayPost;

	private String dataAuthorUID, dataTimestamp, dataAuthorRealName,
		dataNamaBarang, dataDeadline, dataDeskripsi, dataStatus;
	private String dataRealNamePemberi, dataRealNamePenerima;
	private String dataUIDPemberi, dataUIDPenerima;


	/** ==============================================================================
	 * Inisialisasi fragments dan loaders, dipanggil sebelum activity di-start
	 * ============================================================================== */
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

		btnUbahDeadline = (LinearLayout) findViewById(R.id.btn_ubahDeadline);
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

		// initialize components:
		pembuatPost.setText("");
		tanggal.setText("");
		namaBarang.setText("");
		deskripsi.setText("");
		deadline.setText("");
		status.setText("");
		partnerName.setText("");

		loadPeminjamanDetails();
	}

	/** ==============================================================================
	 * Handler saat activity ini kembali ke foreground
	 * ============================================================================== */
	@Override
	protected void onResume() {
		super.onResume();
		loadPeminjamanDetails();
	}

	/** ==============================================================================
	 * Mengonfigurasi (set text) text views yang ada di activity ini
	 * ============================================================================== */
	public void configureTextViews() {
		try {
			// ambil detail post dari jsonResponseArrayPost
			JSONObject postInstance = jsonResponseArrayPost.getJSONObject(0);

			// set instance variables
			Log.d("DEBUG", "Mengonfigurasi text views...");
			Log.d("DEBUG", postInstance.toString());
			dataAuthorUID = postInstance.getString("UID");
			dataTimestamp = postInstance.getString("Timestamp");
			dataAuthorRealName = postInstance.getString("RealName");
			dataNamaBarang = postInstance.getString("NamaBarang");
			dataDeskripsi = postInstance.getString("Deskripsi");
			dataDeadline = postInstance.getString("Deadline");
			dataStatus = postInstance.getString("Status");
			dataRealNamePemberi = postInstance.getString("RealNamePemberi");
			dataRealNamePenerima = postInstance.getString("RealNamePenerima");
			dataUIDPemberi = postInstance.getString("UIDPemberi");
			dataUIDPenerima = postInstance.getString("UIDPenerima");

			// initialize components:
			pembuatPost.setText(dataAuthorRealName);
			tanggal.setText("Diposkan "
				+ UtilityDate.formatTimestampDateOnly(dataTimestamp)
				+ ", jam " + UtilityDate.formatTimestampTimeOnly(dataTimestamp));
			namaBarang.setText(dataNamaBarang);
			deskripsi.setText(dataDeskripsi);
			deadline.setText(UtilityDate.formatTimestampDateOnly(dataDeadline)
				+ ", jam " + UtilityDate.formatTimestampTimeOnly(dataDeadline));
			status.setText(dataStatus);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/** ==============================================================================
	 * untuk menampilkan action buttons yang sesuai dengan user yang mengakses post
	 * ini, serta assign listener yang sesuai.
	 * ============================================================================== */
	public void configureActionButtons() {
		// pembedaan antara pemilik barang dan peminjam
		if (currentUid.equalsIgnoreCase(dataUIDPemberi)) {
			partnerName.setText("Anda meminjamkan kepada " + dataRealNamePenerima);

			btnUbahStatus.setVisibility(View.VISIBLE);
			btnUbahDeadline.setVisibility(View.VISIBLE);

			// tampilkan tombol untuk mengubah jika statusnya belum dikembalikan
			if (!dataStatus.equals("DIKEMBALIKAN")) {

			}
		}
		else {
			partnerName.setText("Anda diberi pinjam oleh " + dataRealNamePemberi);
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
		btnUbahDeadline.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				ubahDeadline();
			}
		});
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
					String serverResponsePost = UtilityConnection.runPhp("getpeminjamandetail.php", dataToSend);
					String serverResponseComment = UtilityConnection.runPhp("getthreads.php", dataToSend);
					Log.d("DEBUG", serverResponsePost);
					Log.d("DEBUG", serverResponseComment);

					// parse data JSON yang diterima dari server
					JSONObject jsonResponseObjectPost = new JSONObject(serverResponsePost);
					JSONObject jsonResponseObjectComment = new JSONObject(serverResponseComment);
					jsonResponseArrayPost = jsonResponseObjectPost.getJSONArray("server_response");
					jsonResponseArrayComment = jsonResponseObjectComment.getJSONArray("server_response");

					// update di UI thread
					handler.post(new Runnable()
					{
						public void run() {
							// berarti post-nya sudah tidak ada
							if (jsonResponseArrayPost.length() == 0) {
								Toast.makeText(activity,
									"Entri peminjaman sudah tidak tersedia.", Toast.LENGTH_LONG).show();
								finish();
							}
							else {
								configureTextViews();
								configureActionButtons();
								populateComments();
							}
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

		// mulai dengan linear list yang kosong
		// atau kalau tidak, saat di-resume, list-nya numpuk
		commentSectionContainer.removeAllViews();

		ArrayList<CustomThreadBlock> threadBlockArray = new ArrayList<>();
		ArrayList<Comment> commentArray = new ArrayList<>();
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
					// hanya perlu action balas
					CustomThreadBlock threadBlock = new CustomThreadBlock(this,
						commentArray, Integer.parseInt(intentPID), lastParentUid,
						CustomThreadBlock.ACTIONS_NONE, Integer.parseInt(dataAuthorUID));
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
		CustomThreadBlock threadBlock = new CustomThreadBlock(this,
			commentArray, Integer.parseInt(intentPID),
			lastParentUid, CustomThreadBlock.ACTIONS_NONE,
			Integer.parseInt(dataAuthorUID));
		threadBlockArray.add(threadBlock);
		commentArray.clear();

		// buat view items untuk threads
		int threadBlockArrayLength = threadBlockArray.size();

		for (int i = 0; i < threadBlockArrayLength; i++) {
			CustomThreadBlock threadBlockToPrint = threadBlockArray.get(i);
			commentSectionContainer.addView(threadBlockToPrint.getLinearLayout());
		}
	}


	// --- action handlers ---

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

	public void ubahDeadline() {
		Intent intent = new Intent(this, UbahDeadlineActivity.class);
		intent.putExtra("pid", intentPID);
		intent.putExtra("updatePeminjaman", "true");

		startActivity(intent);
	}
}
