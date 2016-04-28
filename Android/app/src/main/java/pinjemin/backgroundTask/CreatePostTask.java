/** ===================================================================================
 * [CREATE POST TASK]
 * Helper class untuk mengirim data ke web sercive di server (asynchronously)
 * Dipakai untuk kelas CreatePostDemand, CreatePostSupply
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.backgroundTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.TreeMap;

import pinjemin.utility.UtilityConnection;


public class CreatePostTask extends AsyncTask<Void,Object,Void>
{
	private Context context;
	private TreeMap<String,String> dataToSend;
	private String phpFilePath;

	public CreatePostTask(Context context, String phpFilePath, TreeMap<String,String> dataToSend) {
		this.context = context;
		this.phpFilePath = phpFilePath;
		this.dataToSend = dataToSend;
	}

	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		UtilityConnection.runPhp(phpFilePath, dataToSend);
		return null;
	}

	@Override
	protected void onProgressUpdate(Object... object) {}

	@Override
	protected void onPostExecute(Void aVoid) {}

	@Override
	protected void onPreExecute() {}

}