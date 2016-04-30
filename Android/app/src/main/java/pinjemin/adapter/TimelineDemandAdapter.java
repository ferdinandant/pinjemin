/** ===================================================================================
 * [TIMELINE DEMAND ADAPTER]
 * Binding data timeline permintaan dengan RecyclerView yang terkait
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
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


	/** ==============================================================================
	 * Constructor kelas TimelineDemandAdapter
	 * @param demandPostArray - array yang digunakan untuk menampung post permintaan
	 * ============================================================================== */
	public TimelineDemandAdapter(ArrayList<PostDemand> demandPostArray) {
		this.arrayDemand = demandPostArray;
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
			.inflate(R.layout.row_timeline_demand, parent, false);

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
	public void onBindViewHolder(TimelineDemandAdapter.RecyclerViewHolder holder, int position) {
		// ambil instance postDemand pada indeks position
		PostDemand postDemand = arrayDemand.get(position);

		// updateViewHolder (setText)
		holder.namaBarang.setText(postDemand.getNamaBarang());
		holder.accountName.setText(postDemand.getAccountName());
		holder.deskripsi.setText(postDemand.getDeskripsi());
		holder.timestamp.setText(postDemand.getTimestamp());
		holder.batasAkhir.setText(postDemand.getBatasAkhir());
	}

	/** ==============================================================================
	 * Mendapatkan ArrayList yang dipakai pada kelas ini
	 * @return ArrayList yang dipakai pada kelas ini
	 * ============================================================================== */
	public ArrayList<PostDemand> getarrayList() {
		return arrayDemand;
	}

	@Override
	public int getItemCount() {
		return arrayDemand.size();
	}

	// -- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
	 * ViewHolder mendeskripsikan index view dan metadata-nya di RecyclerView
	 * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
	 * ============================================================================== */
	public static class RecyclerViewHolder extends RecyclerView.ViewHolder
	{
		TextView namaBarang, accountName, deskripsi, timestamp, batasAkhir;

		public RecyclerViewHolder(View view) {
			super(view);

			// assign TextView instance variables:
			// (assign dengan reference ke objek aslinya)
			namaBarang = (TextView) view.findViewById(R.id.namaBarang);
			accountName = (TextView) view.findViewById(R.id.accountName);
			deskripsi = (TextView) view.findViewById(R.id.deskripsi);
			timestamp = (TextView) view.findViewById(R.id.timestamp);
			batasAkhir = (TextView) view.findViewById(R.id.batasAkhir);
		}
	}
}
