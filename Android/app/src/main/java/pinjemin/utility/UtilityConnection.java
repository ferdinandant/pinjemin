/** ===================================================================================
 * [UTILITY CONNECTION]
 * Helper class untuk mengirim data ke web sercive di server
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class UtilityConnection
{
	public static String HOST_ADDRESS = "http://kemalamru.cloudapp.net/ppl/";

	/** ==============================================================================
	 * Mengirim data (POST parameters) ke server
	 * @param phpFile - target .php file di server yang akan menerima data yang dikirim
	 * @param dataToSend - HashMap atau TreeMap, berisi (key,value) pair data-data
	 *   yang akan di-send. Boleh null jika tidak ada yang perlu dikirim
	 * @return hasil kembalian (String) yang ditulis oleh server. Jika berupa data
	 *   seharusnya server sudah meng-encode-nya dalam bentuk JSON.
	 * ============================================================================== */
	public static String runPhp(String phpFile, Map<String,String> dataToSend) throws IOException {
		URL url = new URL(HOST_ADDRESS + phpFile);

		Log.d("DEBUG", "Menghubungi: " + HOST_ADDRESS + phpFile);

		// jika tidak ada data yang di-send, inisialisasikan TreeMap kosong
		// (untuk mencegah NullPointerException di bawah)
		if (dataToSend == null) {
			dataToSend = new TreeMap<String,String>();
		}

		// buka koneksi ke server
		// setting timeout 10 secs biar nggak deadlock
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);

		// set method pengiriman: POST
		// set supaya koneksi ini bisa input dan output
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);

		// initialize OutputStream dan BufferedWriter untuk output
		OutputStream outputStream = urlConnection.getOutputStream();
		BufferedWriter bufferedWriter =
			new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

		// susun data yang akan dikirim
		Set<String> dataToSendKeySet = dataToSend.keySet();
		Iterator<String> dataToSendKeySetIterator = dataToSendKeySet.iterator();
		String postOut = "";

		// untuk setiap (key,value) pair di keySet,
		// append key dan value ke variabel postOut
		for (int i = 0; dataToSendKeySetIterator.hasNext(); i++) {
			Object key = dataToSendKeySetIterator.next();
			if (i != 0) postOut += "&";
			postOut += key + "=" + URLEncoder.encode(dataToSend.get(key), "UTF-8");
		}

		// kirim data, lalu close resources
		// (close bufferedWriter & outputStream)
		bufferedWriter.write(postOut);
		bufferedWriter.flush();
		bufferedWriter.close();
		outputStream.close();

		// inisialisasi BufferedReader, dkk. untuk input
		String serverResponseString = "";
		String readLine = "";
		BufferedReader bufferedReader =
			new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		// selama masih ada kembalian dari server yang belum dibaca,
		// append apa pun yang dibaca ke serverResponseString
		while ((readLine = bufferedReader.readLine()) != null) {
			serverResponseString += readLine;
		}

		// close resources
		bufferedReader.close();
		urlConnection.disconnect();

		return serverResponseString;
	}

	/** ==============================================================================
	 * Menghapus karakter Unicode BOM (byte order mark) di depan varString.
	 * (BOM sebenarnya untuk memberitahu sistem jenis encoding yang dipakai)
	 * @param varString - String yang akan dihapus BOM-nya
	 * @return String Unicode tanpa BOM
	 * ============================================================================== */
	public static String removeUnicodeBOM(String varString) {
		// Syntax: substring(beginIndex)
		return varString.substring(1);
	}
}
