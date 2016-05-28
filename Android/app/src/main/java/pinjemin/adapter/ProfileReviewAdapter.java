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

import pinjemin.model.Review;
import pinjemin.R;
import pinjemin.model.PostSupply;
import pinjemin.utility.UtilityDate;


public class ProfileReviewAdapter
	extends RecyclerView.Adapter<ProfileReviewAdapter.RecyclerViewHolder>
{
	private ArrayList<Review> arrayReview = new ArrayList<>();


	/** ==============================================================================
	 * Constructor kelas ProfileReviewAdapter
	 * @param demandPostArray - array yang digunakan untuk menampung post permintaan
	 * ============================================================================== */
	public ProfileReviewAdapter(ArrayList<Review> demandPostArray) {
		this.arrayReview = demandPostArray;
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
			.inflate(R.layout.row_detail_profile_review, parent, false);

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
	public void onBindViewHolder(ProfileReviewAdapter.RecyclerViewHolder holder, int position) {
		// ambil instance Review pada indeks position
		Review review = arrayReview.get(position);
		double ratingDouble = Double.parseDouble(review.getRating());
		String starString = "";

		if (ratingDouble >= 5) starString = "\u2605\u2605\u2605\u2605\u2605 · ";
		else if (ratingDouble >= 4) starString = "\u2605\u2605\u2605\u2605\u2606 · ";
		else if (ratingDouble >= 3) starString = "\u2605\u2605\u2605\u2606\u2606 · ";
		else if (ratingDouble >= 2) starString = "\u2605\u2605\u2606\u2606\u2606 · ";
		else if (ratingDouble >= 1) starString = "\u2606\u2606\u2606\u2606\u2606 · ";
		else starString = "Tidak ada rating · ";

		holder.realName.setText("Pada peminjaman " + review.getNamaBarang()
			+ " dari " + review.getRealName() + ":");
		holder.review.setText(starString + review.getReview());
	}

	/** ==============================================================================
	 * Mendapatkan ArrayList yang dipakai pada kelas ini
	 * @return ArrayList yang dipakai pada kelas ini
	 * ============================================================================== */
	public ArrayList<Review> getarrayList() {
		return arrayReview;
	}

	@Override
	public int getItemCount() {
		return arrayReview.size();
	}


	// -- inner class declaration ---

	/** ==============================================================================
	 * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
	 * ViewHolder mendeskripsikan index view dan metadata-nya di RecyclerView
	 * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
	 * ============================================================================== */
	public static class RecyclerViewHolder extends RecyclerView.ViewHolder
	{
		TextView realName, review, namaBarang;

		public RecyclerViewHolder(View view) {
			super(view);

			// assign TextView instance variables:
			// (assign dengan reference ke objek aslinya)
			realName = (TextView) view.findViewById(R.id.output_realname);
			review = (TextView) view.findViewById(R.id.output_review);
		}
	}
}
