package com.orion.sinar_surya.activities.pesanan;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.models.PesananModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PesananAdapter extends RecyclerView.Adapter {
    Context context;
    List<PesananModel> Datas;
    PesananActivity pesananActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public PesananAdapter(PesananActivity pesananActivity, List<PesananModel> pesananModel, int view) {
        this.context = pesananActivity.getApplicationContext();
        this.pesananActivity = pesananActivity;
        this.Datas = pesananModel;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new PesananAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new PesananAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final PesananModel mCurrentItem = Datas.get(position);
            final PesananAdapter.ItemHolder itemHolder = (PesananAdapter.ItemHolder) holder;

            if (!mCurrentItem.getGambar().equals("") ){
                String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                Picasso.get().load(path.replace("%", "%25")).into(itemHolder.imgBarang);
                if (itemHolder.imgBarang.getDrawable() == null){
                    itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                }
            }

            itemHolder.txtNama.setText(mCurrentItem.getNama_barang());
            itemHolder.txtDiskon.setText(String.format("%s%%", Global.FloatToStrFmt(mCurrentItem.getDiskon_pct())));
            itemHolder.txtHargaAwal.setText(Global.FloatToStrFmt(mCurrentItem.getHarga_awal(), true));
            itemHolder.txtHargaAkhir.setText(String.format("%s X %s", mCurrentItem.getQty(), Global.FloatToStrFmt(mCurrentItem.getHarga_akhir(), true)));
            itemHolder.txtSatuan.setText(mCurrentItem.getNama_satuan());

            if (mCurrentItem.getDiskon_pct() > 0){
                itemHolder.txtHargaAwal.setVisibility(View.VISIBLE);
                itemHolder.txtDiskon.setVisibility(View.VISIBLE);
                itemHolder.txtHargaAwal.setPaintFlags(itemHolder.txtHargaAwal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                itemHolder.txtHargaAwal.setVisibility(View.GONE);
                itemHolder.txtDiskon.setVisibility(View.GONE);
            }


        }else if (holder instanceof PesananAdapter.LoadingViewHolder){
            PesananAdapter.LoadingViewHolder loadingViewHolder = (PesananAdapter.LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        TextView txtNama, txtHargaAwal, txtHargaAkhir, txtDiskon, txtSatuan, txtQty;

        public ItemHolder(View itemView) {
            super(itemView);
            imgBarang = itemView.findViewById(R.id.imgBarang);
            txtNama = itemView.findViewById(R.id.txtNamaBarang);
            txtHargaAwal = itemView.findViewById(R.id.txtHargaAwal);
            txtHargaAkhir = itemView.findViewById(R.id.txtHargaAkhir);
            txtDiskon = itemView.findViewById(R.id.txtDiskon);
            txtSatuan = itemView.findViewById(R.id.txtSatuan);
            txtQty = itemView.findViewById(R.id.txtQty);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<PesananModel> PesananModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(PesananModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(PesananModel PesananModel) {
        this.Datas.add(PesananModel);
        notifyItemRangeInserted(Datas.size()-1,Datas.size()-1);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(Datas.size()-1);
            notifyItemRemoved(Datas.size());
        }
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }

}
