/** ===================================================================================
 * [TIMELINE DEMAND ADAPTER]
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactor & Documentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pinjemin.model.PostSupply;
import pinjemin.R;


/**
 * Created by K-A-R on 08/04/2016.
 */
public class TimelineSupplyAdapter extends RecyclerView.Adapter<TimelineSupplyAdapter.RecyclerViewHolder>
{

	private ArrayList<PostSupply> arraySupply = new ArrayList<>();

	public TimelineSupplyAdapter(ArrayList<PostSupply> arraySupply) {
		this.arraySupply = arraySupply;
	}

	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view =
			LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timeline_supply, parent, false);
		RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

		return recyclerViewHolder;
	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {

		PostSupply postSupply = arraySupply.get(position);

		holder.namaBarang.setText(postSupply.getNamaBarang());
		holder.accountName.setText(postSupply.getAccountName());
		holder.deskripsi.setText(postSupply.getDeskripsi());
		holder.timestamp.setText(postSupply.getTimestamp());
		holder.harga.setText(postSupply.getHarga());
	}

	@Override
	public int getItemCount() {
		return arraySupply.size();
	}

	public static class RecyclerViewHolder extends RecyclerView.ViewHolder
	{

		TextView namaBarang, accountName, deskripsi, timestamp, harga;

		public RecyclerViewHolder(View view) {
			super(view);

			namaBarang = (TextView) view.findViewById(R.id.namaBarang);
			accountName = (TextView) view.findViewById(R.id.accountName);
			deskripsi = (TextView) view.findViewById(R.id.deskripsi);
			timestamp = (TextView) view.findViewById(R.id.timestamp);
			harga = (TextView) view.findViewById(R.id.harga);
		}
	}
}
