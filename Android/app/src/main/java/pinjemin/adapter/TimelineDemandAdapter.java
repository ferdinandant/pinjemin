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

import pinjemin.model.PostDemand;
import pinjemin.R;


public class TimelineDemandAdapter
	extends RecyclerView.Adapter<TimelineDemandAdapter.RecyclerViewHolder>
{

	private ArrayList<PostDemand> arrayDemand = new ArrayList<>();

	public TimelineDemandAdapter(ArrayList<PostDemand> arrayDemand) {
		this.arrayDemand = arrayDemand;
	}

	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view =
			LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timeline_demand, parent, false);

		RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

		return recyclerViewHolder;
	}

	@Override
	public void onBindViewHolder(TimelineDemandAdapter.RecyclerViewHolder holder, int position) {
		PostDemand postDemand = arrayDemand.get(position);

		holder.namaBarang.setText(postDemand.getNamaBarang());
		holder.accountName.setText(postDemand.getAccountName());
		holder.deskripsi.setText(postDemand.getDeskripsi());
		holder.timestamp.setText(postDemand.getTimestamp());
		holder.batasAkhir.setText(postDemand.getBatasAkhir());
	}

	@Override
	public int getItemCount() {
		return arrayDemand.size();
	}

	public static class RecyclerViewHolder extends RecyclerView.ViewHolder
	{

		TextView namaBarang, accountName, deskripsi, timestamp, batasAkhir;

		public RecyclerViewHolder(View view) {
			super(view);

			namaBarang = (TextView) view.findViewById(R.id.namaBarang);
			accountName = (TextView) view.findViewById(R.id.accountName);
			deskripsi = (TextView) view.findViewById(R.id.deskripsi);
			timestamp = (TextView) view.findViewById(R.id.timestamp);
			batasAkhir = (TextView) view.findViewById(R.id.batasAkhir);
		}
	}
}
