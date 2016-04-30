/** ===================================================================================
 * [CREATE POST TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas CreatePostDemandActivity, CreatePostSupplyActivity
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.TreeMap;

import pinjemin.utility.UtilityConnection;


public class CreatePostTask extends AsyncTask<Void,Object,Void>
{
	public static final String PHP_PATH_SUPPLY_POST = "createnewpenawaran.php";
	public static final String PHP_PATH_DEMAND_POST = "createnewpermintaan.php";
	public static final int DEMAND_POST = 1;
	public static final int SUPPLY_POST = 2;

	private Context context;
	private TreeMap<String,String> dataToSend;
	private String phpFilePath;
	private String serverResponse;
	private int postType;

	/** ==============================================================================
	 * Constructor kelas CreatePostTask
	 * @param context - context dari mana CreatePostTask dipanggil
	 * @param postType - DEMAND_POST atau SUPPLY_POST, tergantung jenis post
	 * 	yang dibuat.
	 * ============================================================================== */
	public CreatePostTask(Context context, int postType, TreeMap<String,String> dataToSend) {
		this.context = context;
		this.postType = postType;
		this.dataToSend = dataToSend;
		this.serverResponse = "";

		if (postType == DEMAND_POST) {
			phpFilePath = PHP_PATH_DEMAND_POST;
		}
		else if (postType == SUPPLY_POST) {
			phpFilePath = PHP_PATH_SUPPLY_POST;
		}
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			serverResponse = UtilityConnection.runPhp(phpFilePath, dataToSend);
			Log.d("DEBUG", "--- CreatePostTask ---");
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

	private static String asciiToHex(String asciiValue)
	{
		char[] chars = asciiValue.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++)
		{
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan SETELAH doInBackground selesai dijalankan
	 * ============================================================================== */
	@Override
	protected void onPostExecute(Void aVoid) {
		// buang karakter penanda encoding Unicode
		serverResponse = UtilityConnection.removeUnicodeBOM(serverResponse);

		// cek apakah pengiriman berhasil
		if (serverResponse.equals("true")) {
			Toast.makeText(context, "Post telah terkirim.", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(context, "Post gagal dikirim.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onProgressUpdate(Object... object) {}

	@Override
	protected void onPreExecute() {}

}