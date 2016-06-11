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


public class FriendActionTask extends AsyncTask<Void, Object, Void> {
    public static final String PHP_PATH_ACCEPT = "acceptrequest.php";
    public static final String PHP_PATH_REJECT = "rejectrequest.php";
    public static final String PHP_PATH_DELETE = "removefriend.php";
    public static final String PHP_PATH_CANCEL = "cancelrequest.php";
    public static final String PHP_PATH_ADD = "sendrequest.php";


    public static final int ACCEPT = 1;
    public static final int REJECT = 2;
    public static final int DELETE = 3;
    public static final int CANCEL = 4;
    public static final int ADD = 5;

    private Context context;
    private TreeMap<String, String> dataToSend;
    private String phpFilePath;
    private String serverResponse;
    private String partnerName;
    private int postType;

    /** ==============================================================================
     * Constructor kelas CreatePostTask
     * @param context - context dari mana CreatePostTask dipanggil
     * @param postType - DEMAND_SEARCH atau SUPPLY_SEARCH, tergantung jenis post
     * 	yang dibuat.
     * ============================================================================== */
    public FriendActionTask(Context context, int postType, TreeMap<String, String> dataToSend, String partnerName) {
        this.context = context;
        this.postType = postType;
        this.dataToSend = dataToSend;
        this.partnerName = partnerName;
        this.serverResponse = "";

        if (postType == ACCEPT) {
            phpFilePath = PHP_PATH_ACCEPT;
        } else if (postType == REJECT) {
            phpFilePath = PHP_PATH_REJECT;
        } else if (postType == DELETE) {
            phpFilePath = PHP_PATH_DELETE;
        } else if (postType == CANCEL) {
            phpFilePath = PHP_PATH_CANCEL;
        } else if (postType == ADD) {
            phpFilePath = PHP_PATH_ADD;
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

        if (postType == ACCEPT) {
            // cek apakah pengiriman berhasil
            if (serverResponse.equals("true")) {
                Toast.makeText(context, "Anda Menerima Permintaan Pertemanan Dari " + partnerName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Operasi Gagal", Toast.LENGTH_LONG).show();
            }

        } else if (postType == REJECT) {
            // cek apakah pengiriman berhasil
            if (serverResponse.equals("true")) {
                Toast.makeText(context, "Anda Menolak Request Pertemanan Dari " + partnerName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Operasi Gagal", Toast.LENGTH_LONG).show();
            }

        } else if (postType == DELETE) {
            // cek apakah pengiriman berhasil
            if (serverResponse.equals("true")) {
                Toast.makeText(context, "Anda Berhasil Menghapus " + partnerName + " Dari Daftar Pertemanan Anda", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Operasi Gagal", Toast.LENGTH_LONG).show();
            }

        } else if (postType == CANCEL) {
            // cek apakah pengiriman berhasil
            if (serverResponse.equals("true")) {
                Toast.makeText(context, "Anda Menghapus Permintaan Pertemanan " + partnerName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Operasi Gagal", Toast.LENGTH_LONG).show();
            }

        } else if (postType == ADD) {
            // cek apakah pengiriman berhasil
            if (serverResponse.equals("true")) {
                Toast.makeText(context, "Anda Berhasil Melakukan Request Pertemanan Ke " + partnerName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Operasi Gagal", Toast.LENGTH_LONG).show();
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