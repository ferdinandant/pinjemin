/** ===================================================================================
 * [CREATE POST TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas CreatePostDemandActivity, CreatePostSupplyActivity
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.TreeMap;

import pinjemin.menu_peminjaman.DetailPostPeminjamanActivity;
import pinjemin.utility.UtilityConnection;


public class CommentActionTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_CREATE_THREAD = "createthread.php";
	public static final String PHP_PATH_REPLY_THREAD = "replythread.php";
	public static final String PHP_PATH_INITIATE_TRANSFER = "initiatetransfer.php";
	public static final String PHP_PATH_CANCEL_TRANSFER = "canceltransfer.php";
	public static final String PHP_PATH_CONFIRM_TRANSFER = "confirmtransfer.php";

	public static final int CREATE_THREAD = 1;
	public static final int REPLY_THREAD = 2;
	public static final int INITIATE_TRANSFER = 3;
	public static final int CANCEL_TRANSFER = 4;
	public static final int CONFIRM_TRANSFER = 5;

	private Activity context;
	private TreeMap<String,String> dataToSend;
	private String phpFilePath;
	private String serverResponse;
	private int postType;

	/** ==============================================================================
	 * Constructor kelas CreatePostTask
	 * @param context - context dari mana CreatePostTask dipanggil
	 * @param postType - DEMAND_SEARCH atau SUPPLY_SEARCH, tergantung jenis post
	 * 	yang dibuat.
	 * ============================================================================== */
	public CommentActionTask(Activity context, int postType, TreeMap<String,String> dataToSend) {
		this.context = context;
		this.postType = postType;
		this.dataToSend = dataToSend;
		this.serverResponse = "";

		if (postType == CREATE_THREAD) {
			phpFilePath = PHP_PATH_CREATE_THREAD;
		}
		else if (postType == REPLY_THREAD) {
			phpFilePath = PHP_PATH_REPLY_THREAD;
		}
		else if (postType == INITIATE_TRANSFER) {
			phpFilePath = PHP_PATH_INITIATE_TRANSFER;
		}
		else if (postType == CANCEL_TRANSFER) {
			phpFilePath = PHP_PATH_CANCEL_TRANSFER;
		}
		else if (postType == CONFIRM_TRANSFER) {
			phpFilePath = PHP_PATH_CONFIRM_TRANSFER;
		}

	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			serverResponse = UtilityConnection.runPhp(phpFilePath, dataToSend);
			Log.d("DEBUG", "--- CommentActionTask ---");
			Log.d("DEBUG", "phpFilePath:" + phpFilePath);
			Log.d("DEBUG", "dataToSend:" + dataToSend.toString());
			Log.d("DEBUG", "serverResponse:" + serverResponse);
		}
		catch (IOException e) {
			Log.d("DEBUG", "ERROR! Tried accessing host: " + phpFilePath);
			e.printStackTrace();
		}
		return null;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SETELAH doInBackground selesai dijalankan
	 * ============================================================================== */
	@Override
	protected void onPostExecute(Void aVoid) {
		// buang karakter penanda encoding Unicode
		serverResponse = UtilityConnection.removeUnicodeBOM(serverResponse);
		Log.d("DEBUG", "serverResponse:" + serverResponse);

		if (postType == CREATE_THREAD) {
			// cek apakah pengiriman berhasil
			if (serverResponse.equals("true")) {
				Toast.makeText(context, "Tanggapan Anda Berhasil Dikirim", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(context, "Tanggapan Anda Gagal Dikirim", Toast.LENGTH_LONG).show();
			}

		}
		else if (postType == REPLY_THREAD) {
			// cek apakah pengiriman berhasil
			if (serverResponse.equals("true")) {
				Toast.makeText(context, "Balasan Anda Berhasil Dikirim", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(context, "Balasan Anda Gagal Dikirim", Toast.LENGTH_LONG).show();
			}

		}
		else if (postType == INITIATE_TRANSFER) {
			// cek apakah pengiriman berhasil
			if (serverResponse.equals("true")) {
				Toast.makeText(context, "Laporan Penyerahan Barang Berhasil Dikirim", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(context, "Laporan Penyerahan Barang Gagal Diirim", Toast.LENGTH_LONG).show();
			}

		}
		else if (postType == CANCEL_TRANSFER) {
			// cek apakah pengiriman berhasil
			if (serverResponse.equals("true")) {
				Toast.makeText(context, "Laporan Penyerahan Barang Berhasil Dihapus", Toast.LENGTH_LONG).show();

				context.recreate();
			}
			else {
				Toast.makeText(context, "Laporan Penyerahan Barang Gagal Dihapus", Toast.LENGTH_LONG).show();
			}

		}
		else if (postType == CONFIRM_TRANSFER) {
			// cek apakah pengiriman berhasil
			if (serverResponse.equals("true")) {
				Toast.makeText(context, "Anda Berhasil Menyetujui Peminjaman Ini", Toast.LENGTH_LONG).show();
				context.finish();

				// buka activity detail peminjaman
				Intent intent = new Intent(context, DetailPostPeminjamanActivity.class);
				intent.putExtra("PID", dataToSend.get("PID"));
				context.startActivity(intent);
			}
			else {
				Toast.makeText(context, "Gagal Memroses Permintaan Anda", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onProgressUpdate(Object... object) {
	}

	@Override
	protected void onPreExecute() {
	}

}