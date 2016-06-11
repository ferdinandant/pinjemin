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


public class PeminjamanTask extends AsyncTask<Void, Object, Void> {
    public static final String PHP_PATH_STATUS = "changepeminjamanstatus.php";
    public static final String PHP_PATH_DEADLINE = "changepeminjamandeadline.php";

    public static int CHANGE_STATUS = 1;
    public static int CHANGE_DEADLINE = 2;

    private Context context;
    private TreeMap<String, String> dataToSend;
    private String phpFilePath;
    private String serverResponse;
    private int type;

    /** ==============================================================================
     * Constructor kelas CreatePostTask
     * @param context - context dari mana CreatePostTask dipanggil
     * 	yang dibuat.
     * ============================================================================== */
    public PeminjamanTask(Context context, int type, TreeMap<String, String> dataToSend) {
        this.context = context;
        this.dataToSend = dataToSend;
        this.type = type;
        this.serverResponse = "";

        if (type == CHANGE_STATUS) {
            phpFilePath = PHP_PATH_STATUS;
        } else if (type == CHANGE_DEADLINE) {
            phpFilePath = PHP_PATH_DEADLINE;
        }
    }

    /** ==============================================================================
     * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
     * ============================================================================== */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            serverResponse = UtilityConnection.runPhp(phpFilePath, dataToSend);
            Log.d("FERDEBUG", "--- CreatePostTask ---");
            Log.d("FERDEBUG", "phpFilePath:" + phpFilePath);
            Log.d("FERDEBUG", "dataToSend:" + dataToSend.toString());
            Log.d("FERDEBUG", "serverResponse:" + serverResponse);
        } catch (IOException e) {
            Log.d("FERDEBUG", "ERROR! Tried accessing host: " + phpFilePath);
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
        Log.d("FERDEBUG", "serverResponse:" + serverResponse);

        if (serverResponse.equals("true")) {
            Toast.makeText(context, "Status Peminjaman Berhasil Diubah", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Barang Sudah Dikembalikan", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Object... object) {
    }

    @Override
    protected void onPreExecute() {
    }

}