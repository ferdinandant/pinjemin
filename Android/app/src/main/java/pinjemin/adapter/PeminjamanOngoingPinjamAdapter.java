package pinjemin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pinjemin.R;
import pinjemin.model.PostPeminjaman;
import pinjemin.utility.UtilityDate;


public class PeminjamanOngoingPinjamAdapter
        extends RecyclerView.Adapter<PeminjamanOngoingPinjamAdapter.RecyclerViewHolder> {
    private ArrayList<PostPeminjaman> arrayPeminjaman = new ArrayList<>();


    /** ==============================================================================
     * Constructor kelas TimelineDemandAdapter
     * @param arrayPeminjaman- array yang digunakan untuk menampung post permintaan
     * ============================================================================== */
    public PeminjamanOngoingPinjamAdapter(ArrayList<PostPeminjaman> arrayPeminjaman) {
        this.arrayPeminjaman = arrayPeminjaman;
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
                .inflate(R.layout.row_peminjaman_ongoing, parent, false);

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
    public void onBindViewHolder(PeminjamanOngoingPinjamAdapter.RecyclerViewHolder holder, int position) {
        PostPeminjaman peminjaman = arrayPeminjaman.get(position);

        holder.namaBarang.setText(peminjaman.getNamaBarang());
        String realname = "Diberi Pinjam Oleh " + peminjaman.getRealnamePemberi();
        holder.realname.setText(realname);
        String tanggal = "pada " + UtilityDate.formatTimestampDateOnly(peminjaman.getTimestampMulai()) + " " + UtilityDate.formatTimestampTimeOnly(peminjaman.getTimestampMulai()) ;
        holder.tanggal.setText(tanggal);
        String status = "Kembalikan Tanggal " + UtilityDate.formatTimestampDateOnly(peminjaman.getDeadline()) + ", jam " + UtilityDate.formatTimestampTimeOnly(peminjaman.getDeadline());
        holder.status.setText(status);
    }

    @Override
    public int getItemCount() {
        return arrayPeminjaman.size();
    }


    // -- inner class declaration ---

    /** ==============================================================================
     * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
     * ViewHolder menrealnamekan index view dan metadata-nya di RecyclerView
     * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
     * ============================================================================== */
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView namaBarang, realname, status, tanggal;

        public RecyclerViewHolder(View view) {
            super(view);

            namaBarang = (TextView) view.findViewById(R.id.output_nama_barang);
            realname = (TextView) view.findViewById(R.id.output_realname);
            status = (TextView) view.findViewById(R.id.output_status);
            tanggal = (TextView) view.findViewById(R.id.output_tanggal);
        }
    }
}
