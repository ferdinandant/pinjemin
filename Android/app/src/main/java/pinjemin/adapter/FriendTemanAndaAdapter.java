/**
 * ===================================================================================
 * [TIMELINE DEMAND ADAPTER]
 * Binding data timeline permintaan dengan RecyclerView yang terkait
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * ===================================================================================
 */

package pinjemin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pinjemin.R;
import pinjemin.model.User;


public class FriendTemanAndaAdapter
        extends RecyclerView.Adapter<FriendTemanAndaAdapter.RecyclerViewHolder> {
    private ArrayList<User> arrayUserTemanAnda = new ArrayList<>();


    /**
     * ==============================================================================
     * Constructor kelas TimelineDemandAdapter
     *
     * @param arryUserTemanAnda - array yang digunakan untuk menampung post permintaan
     *                            ==============================================================================
     */
    public FriendTemanAndaAdapter(ArrayList<User> arryUserTemanAnda) {
        this.arrayUserTemanAnda = arrayUserTemanAnda;
    }

    /**
     * ==============================================================================
     * Dipanggil ketika RecyclerView memerlukan ViewHolder baru bertipe viewType
     * untuk merepresntasikan suatu item.
     *
     * @param parent   - ViewGroup yang akan menampung View baru yang akan dibentuk
     *                 setelah di-bind dengan adapter
     * @param viewType - (tidak dipakai) view type dari View yang akan dibentuk.
     *                 Biasanya sebagai control variable untuk menentukan jenis ViewHolder yang
     *                 mau di-return (kalau ada banyak jenis ViewHolder).
     * @return instance RecyclerViewHolder (dideklarasikan di bawah)
     * ==============================================================================
     */
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

    /**
     * ==============================================================================
     * Dipanggil RecyclerView untuk menampilkan data di position tertentu.
     *
     * @param holder   - ViewHolder yang akan di-update
     * @param position - posisi data pada data set adapter
     *                 ==============================================================================
     */
    @Override
    public void onBindViewHolder(FriendTemanAndaAdapter.RecyclerViewHolder holder, int position) {
        // ambil instance postDemand pada indeks position
        User user = arrayUserTemanAnda.get(position);

        holder.accountName.setText(user.getAccountName());
        holder.realName.setText(user.getRealName());
    }

    @Override
    public int getItemCount() {
        return arrayUserTemanAnda.size();
    }


    // -- inner class declaration ---

    /**
     * ==============================================================================
     * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
     * ViewHolder mendeskripsikan index view dan metadata-nya di RecyclerView
     * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
     * ==============================================================================
     */
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView realName, accountName;

        public RecyclerViewHolder(View view) {
            super(view);

            // assign TextView instance variables:
            // (assign dengan reference ke objek aslinya)

            realName = (TextView) view.findViewById(R.id.output_realname);
            accountName = (TextView) view.findViewById(R.id.output_accountname);
        }
    }
}
