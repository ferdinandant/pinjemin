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

import pinjemin.menu_timeline.TimelineDemandFragment;
import pinjemin.menu_timeline.TimelineSupplyFragment;
import pinjemin.utility.UtilityConnection;


public class DeletePostTask extends AsyncTask<Void,Object,Void>
{
    public static final String PATH = "deletepost.php";

    private Context context;
    private TreeMap<String,String> dataToSend;
    private String serverResponse;
    private String phpFilePath = PATH;

    /** ==============================================================================
     * Constructor kelas CreatePostTask
     * @param context - context dari mana CreatePostTask dipanggil
     * 	yang dibuat.
     * ============================================================================== */
    public DeletePostTask(Context context, TreeMap<String,String> dataToSend) {
        this.context = context;
        this.dataToSend = dataToSend;
        this.serverResponse = "";

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
        }
        catch (IOException e) {
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

        // cek apakah pengiriman berhasil
        if (serverResponse.equals("true")) {
            Toast.makeText(context, "Post Berhasil Dihapus", Toast.LENGTH_LONG).show();
            TimelineDemandFragment.resetLastRequest();
            TimelineSupplyFragment.resetLastRequest();
        }
        else {
            Toast.makeText(context, "Post Sudah Tidak Ada", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Object... object) {}

    @Override
    protected void onPreExecute() {}

}