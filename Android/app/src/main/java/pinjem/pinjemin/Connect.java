package pinjem.pinjemin;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ferdinand on 4/10/2016.
 */
public class Connect {
    public static final String URL_BASE = "kemalamru.cloudapp.net/ppl/";

    /** ==============================================================================
     * Mengirimkan parameter ke suatu file php di server
     * -------------------------------------------------------------------------------
     * @param: phpFile - php file yang ingin dieksekusi
     * @param: data - parameters dalam TreeMap
     * @return: string text kembalian PHP
     * ============================================================================== */
    public static String submitPhp(String phpFile, Map<String,String> data) throws IOException {
        URL url = new URL(URL_BASE + phpFile);
        HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
        urlConnect.setConnectTimeout(10000);

        urlConnect.setRequestMethod("POST");
        urlConnect.setDoOutput(true);
        urlConnect.setDoInput(true);

        //send the data
        DataOutputStream os = new DataOutputStream(urlConnect.getOutputStream());
        Set<String> keySet = data.keySet();
        Iterator<String> it = keySet.iterator();
        String postOut = "";
        for (int i = 0; it.hasNext(); i++) {
            Object key = it.next();
            if (i != 0) postOut += "&";
            postOut += key + "=" + URLEncoder.encode(data.get(key),"UTF-8");
        }
        os.writeBytes(postOut);
        os.flush();
        os.close();

        //fetch any feedbacks
        BufferedReader br = new BufferedReader(
                new InputStreamReader (urlConnect.getInputStream()));
        String toReturn = "";
        String line = "";
        while ((line = br.readLine()) != null)
            toReturn += line;

        urlConnect.disconnect();
        return toReturn;
    }
}
