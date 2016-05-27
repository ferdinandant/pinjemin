package pinjemin.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pinjemin.R;
import pinjemin.model.PostPeminjaman;
import pinjemin.utility.UtilityDate;


public class PeminjamanExpiredAdapter
        extends RecyclerView.Adapter<PeminjamanExpiredAdapter.RecyclerViewHolder> {
    private ArrayList<PostPeminjaman> arrayPeminjaman = new ArrayList<>();
    private String logedinUid;

    /** ==============================================================================
     * Constructor kelas TimelineDemandAdapter
     * @param arrayPeminjaman- array yang digunakan untuk menampung post permintaan
     * ============================================================================== */
    public PeminjamanExpiredAdapter(ArrayList<PostPeminjaman> arrayPeminjaman, String logedinUid) {
        this.arrayPeminjaman = arrayPeminjaman;
        this.logedinUid = logedinUid;
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
                .inflate(R.layout.row_peminjaman_expired, parent, false);

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
    public void onBindViewHolder(PeminjamanExpiredAdapter.RecyclerViewHolder holder, int position) {
        PostPeminjaman peminjaman = arrayPeminjaman.get(position);

        holder.namaBarang.setText(peminjaman.getNamaBarang());

        if (peminjaman.getUid().equalsIgnoreCase(peminjaman.getUidPemberi())) {
            holder.deskripsi.setText("Dipinjamkan Kepada " + peminjaman.getRealnamePenerima());
        } else {
            holder.deskripsi.setText("Diberi Pinjam Oleh " + peminjaman.getRealnamePemberi());
        }

        if (peminjaman.getStatus().equalsIgnoreCase("hilang")) {
            holder.status.setText("Barangnya Hilang");
            holder.status.setTextColor(Color.parseColor("#FFD60306"));

        } else {

            holder.status.setText("Dikembalikan Tanggal " + UtilityDate.formatTimestampDateOnly(peminjaman.getTimestampKembali()) + " " + UtilityDate.formatTimestampTimeOnly(peminjaman.getTimestampKembali()));
        }
    }

    @Override
    public int getItemCount() {
        return arrayPeminjaman.size();
    }


    // -- inner class declaration ---

    /** ==============================================================================
     * Custom implementation kelas RecyclerView.ViewHolder untuk timeline permintaan.
     * ViewHolder mendeskripsikan index view dan metadata-nya di RecyclerView
     * (index != position, Index maksudnya elemen view ke berapa di ViewGroup-nya)
     * ============================================================================== */
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView namaBarang, deskripsi, status, timestamp;

        public RecyclerViewHolder(View view) {
            super(view);

            namaBarang = (TextView) view.findViewById(R.id.output_nama_barang);
            deskripsi = (TextView) view.findViewById(R.id.output_deskripsi);
            status = (TextView) view.findViewById(R.id.output_status);
            timestamp = (TextView) view.findViewById(R.id.output_timestamp);
        }
    }
}
