package pinjemin.backgroundTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.sql.RowSetEvent;

import pinjemin.R;
import pinjemin.adapter.PeminjamanOngoingPinjamAdapter;
import pinjemin.adapter.ProfileReviewAdapter;
import pinjemin.model.PostPeminjaman;
import pinjemin.model.Review;
import pinjemin.model.User;
import pinjemin.session.SessionManager;
import pinjemin.user.DetailProfilActivity;
import pinjemin.utility.UtilityConnection;


/**
 * Created by K-A-R on 30/04/2016.
 */
public class ReviewTask extends AsyncTask<Void,Object,Void>
{
	
	private Context context;
	private Activity activity;
	private TreeMap<String,String> input;
	
	private String phpFilePath = "getuserreviews.php";
	
	private SessionManager session;

	// bagian RecyclerView:
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	
	private ArrayList<Review> arrayReview = new ArrayList<>();
	
	public ReviewTask(Context context, TreeMap<String,String> input) {
		this.context = context;
		this.activity = (Activity) context;
		this.input = input;
		
		session = new SessionManager(activity);
	}
	
	@Override
	protected void onPreExecute() {
		arrayReview = new ArrayList<>();
		adapter = new ProfileReviewAdapter(arrayReview);
		
		layoutManager = new LinearLayoutManager(context);
		
		// configure recycler view barang yang di pinjam
		recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerViewReview);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(adapter);
	}
	
	/** ==============================================================================
	 * Hal yang perlu dilakukan saat subclass AsyncTask ini di-execute
	 * ============================================================================== */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			String serverResponse = UtilityConnection.runPhp(phpFilePath, input);

			Log.d("Server Response", serverResponse);

			// parse data JSON yang diterima dari server (berisi daftar post)
			JSONObject jsonResponseObject = new JSONObject(serverResponse);
			JSONArray jsonResponseArray = jsonResponseObject.getJSONArray("server_response");
			int jsonResponseArrayLength = jsonResponseArray.length();
			
			for (int i = 0; i < jsonResponseArrayLength; i++) {
				JSONObject postInstance = jsonResponseArray.getJSONObject(i);
				
				String namaBarang = postInstance.getString("NamaBarang");
				String rating = postInstance.getString("Rating");
				String review = postInstance.getString("Review");
				String realName = postInstance.getString("RealName");
				
				Review newReview = new Review(namaBarang, review, rating, realName);
				publishProgress(newReview);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/** ==============================================================================
	 * Hal yang perlu dilakukan SELAMA subclass AsyncTask ini di-execute
	 * @param object - normalnya berisi instance yang baru saja di-parse dari server
	 * =============================================================================== */
	@Override
	protected void onProgressUpdate(Object... object) {
		arrayReview.add((Review) object[0]);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPostExecute(Void aVoid) {

	}
	
}
