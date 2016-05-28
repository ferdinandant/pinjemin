package pinjemin.menu_timeline;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import pinjemin.backgroundTask.DeletePostTask;
import pinjemin.backgroundTask.GetProfilTask;
import pinjemin.comment.CommentActivity;
import pinjemin.comment.CustomThreadBlock;
import pinjemin.model.Comment;
import pinjemin.model.PostDemand;
import pinjemin.R;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityConnection;
import pinjemin.utility.UtilityDate;


public class DetailPostSupplyActivity extends AppCompatActivity
{
	private TextView pembuatPost, tanggal, namaBarang, deskripsi, harga;
	private RelativeLayout calonPeminjam, pemberiPinjam;
	private LinearLayout btnMintaPinjem, btnLihatProfil, btnUbah, btnHapus;
	private Toolbar toolbar;
	private SessionManager session;
	private PostDemand postDemand;

	private String dataAuthorUID, currentUid, intentPid, dataTimestamp, dataNamaBarang,
		dataDeskripsi, dataHarga, dataAuthorRealName;

	private JSONArray jsonResponseArrayPost;
	private JSONArray jsonResponseArrayComment;
	private JSONArray jsonResponseArrayActions;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_post_supply);

		session = new SessionManager(this);
		currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

		// initialize toolbar:
		// set judul dan jadikan dia main toolbar di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Detail Penawaran");
		setSupportActionBar(toolbar);

		// initialize layouts:
		calonPeminjam = (RelativeLayout) findViewById(R.id.calon_peminjam);
		pemberiPinjam = (RelativeLayout) findViewById(R.id.pemberi_pinjam);
		btnMintaPinjem = (LinearLayout) findViewById(R.id.btn_mintaPinjem);
		btnLihatProfil = (LinearLayout) findViewById(R.id.btn_lihatProfil);
		btnUbah = (LinearLayout) findViewById(R.id.btn_ubah);
		btnHapus = (LinearLayout) findViewById(R.id.btn_hapus);

		// initialize components:
		pembuatPost = (TextView) findViewById(R.id.pembuatPost);
		tanggal = (TextView) findViewById(R.id.tanggal);
		namaBarang = (TextView) findViewById(R.id.namaBarang);
		deskripsi = (TextView) findViewById(R.id.deskripsi);
		harga = (TextView) findViewById(R.id.harga);

		// ambil detail post dari intent
		Intent intent = getIntent();
		intentPid = intent.getStringExtra("pid");

		// set default TextView values:
		pembuatPost.setText("");
		tanggal.setText("");
		namaBarang.setText("");
		deskripsi.setText("");
		harga.setText("");
	}

	@Override
	public void onResume() {
		// ambil detail post di background
		super.onResume();
		loadPostDetails();
	}

	/** ==============================================================================
	 * Untuk mengubah text pada TextViews sesuai data yang diambil dari server
	 * ============================================================================== */
	public void configureTextViews() {
		try {
			// ambil detail post dari jsonResponseArrayPost
			JSONObject postInstance = jsonResponseArrayPost.getJSONObject(0);

			// set instance variables
			dataAuthorUID = postInstance.getString("UID");
			dataTimestamp = postInstance.getString("Timestamp");
			dataAuthorRealName = postInstance.getString("RealName");
			dataNamaBarang = postInstance.getString("NamaBarang");
			dataDeskripsi = postInstance.getString("Deskripsi");
			dataHarga = postInstance.getString("Harga");

			// set TextView values:
			// gunakan data yang di-extract dari intent di atas
			String formattedTanggalPost = "Diposkan pada "
				+ UtilityDate.formatTimestampDateOnly(dataTimestamp)
				+ ", jam " + UtilityDate.formatTimestampTimeOnly(dataTimestamp);
			String formattedHarga = "Rp" + String.format("%,d", Integer.parseInt(dataHarga));

			pembuatPost.setText(dataAuthorRealName);
			tanggal.setText(formattedTanggalPost);
			namaBarang.setText(dataNamaBarang);
			deskripsi.setText(dataDeskripsi);

			if (formattedHarga.equals("Rp0")) {
				harga.setText("GRATIS");
			}
			else {
				harga.setText(formattedHarga);
			}
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
		// Jika yang mengakses adalah si pembuat post
		if (dataAuthorUID.equalsIgnoreCase(currentUid)) {
			// tampilkan menu yang relevan
			pemberiPinjam.setVisibility(View.VISIBLE);

			// set listener untuk tombol "ubah"
			btnUbah.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					ubah();
				}
			});

			// set listener untuk tombol "hapus"
			btnHapus.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					hapus();
				}
			});

		}

		// Jika yang mengakses adalah pembaca post
		else {
			// hilangkan menu yang tidak relevan
			calonPeminjam.setVisibility(View.VISIBLE);

			// set listener untuk tombol "minta pinjam"
			btnMintaPinjem.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					mintaPinjem();
				}
			});

			// set listener untuk tombol "lihat profil"
			btnLihatProfil.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view) {
					lihatProfil();
				}
			});
		}
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

				int cid = commentInstance.getInt("CID");
				int uid = commentInstance.getInt("UID");
				int parentUID = commentInstance.getInt("ParentUID");
				String realName = commentInstance.getString("RealName");
				String timestamp = commentInstance.getString("Timestamp");
				String content = commentInstance.getString("Content");

				// jika parentUID sekarang != lastParentUid, berarti harus dimulai thread baru
				// jika lastParentUid == -1, maka artinya belum ada data sebelumnya (ini yang pertama)
				// jika lastParentUid !- -1, masukkan ke threadBlockArray untuk data sebelumnya
				if ((lastParentUid != parentUID) && (lastParentUid != -1)) {
					currentThreadBlockArrayPointer++;
					JSONObject possibleActionCodeJson = jsonResponseArrayActions
						.getJSONObject(currentThreadBlockArrayPointer);
					int possibleActionCode = possibleActionCodeJson.getInt("ThreadAction");

					// target UID untuk post penawaran:
					// kalau ini yang meng-initiate, target parent UID
					// kalau ini yang meng-confirm, target si pembuat post
					int targetUID = -1;
					if (possibleActionCode == CustomThreadBlock.ACTIONS_CAN_INITIATE
						|| possibleActionCode == CustomThreadBlock.ACTIONS_CAN_CANCEL) {
						targetUID = lastParentUid;
					}
					else if (possibleActionCode == CustomThreadBlock.ACTIONS_CAN_CONFIRM) {
						targetUID = Integer.parseInt(dataAuthorUID);
					}

					CustomThreadBlock threadBlock = new CustomThreadBlock(this, commentArray,
						Integer.parseInt(intentPid), lastParentUid, possibleActionCode, targetUID);
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
		try {
			currentThreadBlockArrayPointer++;
			JSONObject possibleActionCodeJson = jsonResponseArrayActions
				.getJSONObject(currentThreadBlockArrayPointer);
			int possibleActionCode = possibleActionCodeJson.getInt("ThreadAction");

			// target UID untuk post penawaran:
			// kalau ini yang meng-initiate, target parent UID
			// kalau ini yang meng-confirm, target si pembuat post
			int targetUID = -1;
			if (possibleActionCode == CustomThreadBlock.ACTIONS_CAN_INITIATE
				|| possibleActionCode == CustomThreadBlock.ACTIONS_CAN_CANCEL) {
				targetUID = lastParentUid;
			}
			else if (possibleActionCode == CustomThreadBlock.ACTIONS_CAN_CONFIRM) {
				targetUID = Integer.parseInt(dataAuthorUID);
			}

			CustomThreadBlock threadBlock = new CustomThreadBlock(this, commentArray,
				Integer.parseInt(intentPid), lastParentUid, possibleActionCode, targetUID);
			threadBlockArray.add(threadBlock);
			commentArray.clear();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		// buat view items untuk threads
		int threadBlockArrayLength = threadBlockArray.size();

		for (int i = 0; i < threadBlockArrayLength; i++) {
			CustomThreadBlock threadBlock = threadBlockArray.get(i);
			commentSectionContainer.addView(threadBlock.getLinearLayout());
		}
	}

	/** ==============================================================================
	 * Mengambil data detail post dari server di thread terpisah
	 * ============================================================================== */
	private void loadPostDetails() {
		final Activity activity = this;
		final Handler handler = new Handler(Looper.getMainLooper());
		Log.d("DEBUG", "Masuk loadPostDetails");
		Runnable runnable = new Runnable()
		{
			public void run() {
				try {
					// persiapkan data yang mau dikirim ke server
					TreeMap<String,String> dataToSend = new TreeMap<>();
					dataToSend.put("PID", intentPid);
					dataToSend.put("ownUID", currentUid);

					// kirim data ke server
					String serverResponsePost = UtilityConnection.runPhp("getpostdetail.php", dataToSend);
					String serverResponseComment = UtilityConnection.runPhp("getthreads.php", dataToSend);
					String serverResponseActions = UtilityConnection.runPhp("getallpossiblethreadaction.php", dataToSend);
					Log.d("DEBUG", serverResponsePost);
					Log.d("DEBUG", serverResponseComment);
					Log.d("DEBUG", serverResponseActions);

					// parse data JSON yang diterima dari server
					JSONObject jsonResponseObjectPost = new JSONObject(serverResponsePost);
					JSONObject jsonResponseObjectComment = new JSONObject(serverResponseComment);
					JSONObject jsonResponseObjectActions = new JSONObject(serverResponseActions);
					jsonResponseArrayPost = jsonResponseObjectPost.getJSONArray("server_response");
					jsonResponseArrayComment = jsonResponseObjectComment.getJSONArray("server_response");
					jsonResponseArrayActions = jsonResponseObjectActions.getJSONArray("server_response");

					// update di UI thread
					handler.post(new Runnable()
					{
						public void run() {
							// berarti post-nya sudah tidak ada
							if (jsonResponseArrayPost.length() == 0) {
								Toast.makeText(activity,
									"Post sudah tidak tersedia.", Toast.LENGTH_LONG).show();
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
					Toast.makeText(activity, "Tidak bisa menghubungi server.", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}


			}
		};
		new Thread(runnable).start();
	}


	// --- action handlers ---

	public void mintaPinjem() {
		Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
		intent.putExtra("type", "create");
		intent.putExtra("pid", intentPid);
		intent.putExtra("ownUid", dataAuthorUID);
		startActivity(intent);
	}

	public void lihatProfil() {
		TreeMap<String,String> input = new TreeMap<>();
		input.put("ownUID", currentUid);
		input.put("targetUID", dataAuthorUID);

		GetProfilTask getProfilTask = new GetProfilTask(this, input);
		getProfilTask.execute();
	}

	public void ubah() {
		Intent intent = new Intent(this, UbahPostSupplyActivity.class);
		intent.putExtra("pid", intentPid);
		intent.putExtra("namabarang", dataNamaBarang);
		intent.putExtra("deskripsi", dataDeskripsi);
		intent.putExtra("harga", dataHarga);

		startActivity(intent);
		finish();
	}

	public void hapus() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Apakah Anda Yakin Untuk Menghapus Post Ini")
			.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TreeMap<String,String> inputSend = new TreeMap<>();
					inputSend.put("PID", intentPid);

					DeletePostTask delete = new DeletePostTask(getApplicationContext(), inputSend);
					delete.execute();
					finish();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

		builder.create().show();
	}
}
