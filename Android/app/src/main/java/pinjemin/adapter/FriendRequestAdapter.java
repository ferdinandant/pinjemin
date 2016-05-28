package pinjemin.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.FriendTask;
import pinjemin.model.User;


public class FriendRequestAdapter
	extends RecyclerView.Adapter<FriendRequestAdapter.RecyclerViewHolder>
{
	private ArrayList<User> arrayUserRequest = new ArrayList<>();
	private String currentUid;
	private Activity activity;

	/** ==============================================================================
	 * Constructor kelas TimelineDemandAdapter
	 * @param arrayUserRequest- array yang digunakan untuk menampung post permintaan
	 * ============================================================================== */
	public FriendRequestAdapter(ArrayList<User> arrayUserRequest, String currentUid, Activity activity) {
		this.arrayUserRequest = arrayUserRequest;
		this.currentUid = currentUid;
		this.activity = activity;
	}

	/** ==============================================================================
	 * Dipanggil ketika RecyclerView memerlukan ViewHolder baru bertipe viewType
	 * untuk merepresntasikan suatu item.
	 * @param parent - ViewGroup yang akan menampung View baru yang akan dibentuk
	 *   setelah di-bind dengan adapter
	 * @param viewType - (tidak dipakai) view type dari View yang akan dibentuk.
	 *   Biasanya sebagai control variable untuk menentukan jenis ViewHolder yang
	 *   mau di-return (kalau ada banyak jenis ViewHolder).
	 * @return instance RecyclerViewHolder (dideklarasikan di bawah)
	 * ============================================================================== */
	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// buat view baru, inflate dari file xml
		View view = LayoutInflater
			.from(parent.getContext())
			.inflate(R.layout.row_friend_request, parent, false);

		// NOTE: inner class RecyclerViewHolder dideklarasikan di bawah
		RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

		return recyclerViewHolder;
	}

	/** ==============================================================================
	 * Dipanggil RecyclerView untuk menampilkan data di position tertentu.
	 * @param holder - ViewHolder yang akan di-update
	 * @param position - posisi data pada data set adapter
	 * ============================================================================== */
	@Override
	public void onBindViewHolder(FriendRequestAdapter.RecyclerViewHolder holder, int position) {
		final User user = arrayUserRequest.get(position);

		holder.accountName.setText(user.getAccountName());
		holder.realName.setText(user.getRealName());
		holder.btnSetuju.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {

				TreeMap<String,String> inputSend = new TreeMap<>();
				inputSend.put("ownUID", currentUid);
				inputSend.put("partnerUID", user.getUid());

				FriendTask task = new FriendTask(activity.getApplicationContext(), FriendTask.ACCEPT, inputSend, user.getRealName());
				task.execute();
			}
		});
		holder.btnTolak.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				TreeMap<String,String> inputSend = new TreeMap<>();
				inputSend.put("ownUID", currentUid);
				inputSend.put("partnerUID", user.getUid());

				FriendTask task = new FriendTask(activity.getApplicationContext(), FriendTask.REJECT, inputSend, user.getRealName());
				task.execute();

				Log.d("Clicked", "");
			}
		});
	}

	@Override
	public int getItemCount() {
		return arrayUserRequest.size();
	}


	// -- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
	 * ViewHolder mendeskripsikan index view dan metadata-nya di RecyclerView
	 * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
	 * ============================================================================== */
	public static class RecyclerViewHolder extends RecyclerView.ViewHolder
	{
		TextView realName, accountName;
		ImageView btnSetuju, btnTolak;

		public RecyclerViewHolder(View view) {
			super(view);

			// assign TextView instance variables:
			// (assign dengan reference ke objek aslinya)
			realName = (TextView) view.findViewById(R.id.output_realname);
			accountName = (TextView) view.findViewById(R.id.output_accountname);
			btnSetuju = (ImageView) view.findViewById(R.id.btn_setuju);
			btnTolak = (ImageView) view.findViewById(R.id.btn_tolak);
		}
	}
}
