/** ===================================================================================
 * [TIMELINE SUPPLY ADAPTER]
 *  Binding data timeline penawaran dengan RecyclerView yang terkait
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

import pinjemin.model.PostSupply;
import pinjemin.R;


public class TimelineSupplyAdapter extends RecyclerView.Adapter<TimelineSupplyAdapter.RecyclerViewHolder>
{
	private ArrayList<PostSupply> arraySupply = new ArrayList<>();

	/** ==============================================================================
	 * Hal yang perlu dilakukan SETELAH doInBackground selesai dijalankan
	 * @param supplyPostArray - array yang digunakan untuk menampung post penawaran
	 * ============================================================================== */
	public TimelineSupplyAdapter(ArrayList<PostSupply> supplyPostArray) {
		this.arraySupply = supplyPostArray;
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
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// buat view baru, inflate dari file xml
		View view = LayoutInflater
			.from(parent.getContext())
			.inflate(R.layout.row_timeline_supply, parent, false);

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
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {
		// ambil instance postSupply pada indeks position
		PostSupply postSupply = arraySupply.get(position);

		// updateViewHolder (setText)
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


	// --- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
	 * ViewHolder mendeskripsikan index view dan metadata-nya di RecyclerView
	 * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
	 * ============================================================================== */
	public static class RecyclerViewHolder extends RecyclerView.ViewHolder
	{
		TextView namaBarang, accountName, deskripsi, timestamp, harga;

		public RecyclerViewHolder(View view) {
			super(view);

			// assign TextView instance variables:
			// (assign dengan reference ke objek aslinya)
			namaBarang = (TextView) view.findViewById(R.id.namaBarang);
			accountName = (TextView) view.findViewById(R.id.accountName);
			deskripsi = (TextView) view.findViewById(R.id.deskripsi);
			timestamp = (TextView) view.findViewById(R.id.timestamp);
			harga = (TextView) view.findViewById(R.id.harga);
		}
	}
}
