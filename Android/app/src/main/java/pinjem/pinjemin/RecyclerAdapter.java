package pinjem.pinjemin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by K-A-R on 08/04/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList<PostSupply> arraySupply = new ArrayList<>();
    ArrayList<PostDemand> arrayDemand= new ArrayList<>();
    String objecType;

    public RecyclerAdapter(ArrayList<PostSupply> arraySupply, String objecType, int test) {
        this.arraySupply = arraySupply;
        this.objecType = objecType;
    }

    public RecyclerAdapter(ArrayList<PostDemand> arrayDemand, String objecType) {
        this.arrayDemand= arrayDemand;
        this.objecType = objecType;
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timeline, parent, false);;

        if (objecType.equalsIgnoreCase("postSupply")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timeline, parent, false);

        } else if (objecType.equalsIgnoreCase("postDemand")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timeline, parent, false);
        }

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, objecType);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        if (objecType.equalsIgnoreCase("postSupply")) {
            PostSupply postSupply = arraySupply.get(position);

            holder.namaBarang.setText(postSupply.getNamaBarang());
            holder.deskripsi.setText(postSupply.getDeskripsi());
            holder.tanggal.setText(postSupply.getTanggal());

        } else if (objecType.equalsIgnoreCase("postDemand")) {
            PostDemand postDemand = arrayDemand.get(position);

            holder.namaBarang.setText(postDemand.getNamaBarang());
            holder.deskripsi.setText(postDemand.getDeskripsi());
            holder.tanggal.setText(postDemand.getTanggal());
        }
    }

    @Override
    public int getItemCount() {
        if (objecType.equalsIgnoreCase("postSupply")) return arraySupply.size();
        else if (objecType.equalsIgnoreCase("postDemand")) return arrayDemand.size();
        return 0;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView namaBarang, deskripsi, tanggal;

        public RecyclerViewHolder(View view, String objectType) {
            super(view);

            if (objectType.equalsIgnoreCase("postSupply")) {
                namaBarang = (TextView) view.findViewById(R.id.namaBarang);
                deskripsi = (TextView) view.findViewById(R.id.deskripsi);
                tanggal = (TextView) view.findViewById(R.id.tanggal);

            } else if (objectType.equalsIgnoreCase("postDemand")) {
                namaBarang = (TextView) view.findViewById(R.id.namaBarang);
                deskripsi = (TextView) view.findViewById(R.id.deskripsi);
                tanggal = (TextView) view.findViewById(R.id.tanggal);
            }
        }
    }
}
