package com.orion.sinar_surya.activities.riwayat_pesanan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.pesanan.PesananActivity;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.models.RiwayatPesananModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RiwayatPesananAdapter extends RecyclerView.Adapter {
    Context context;
    List<RiwayatPesananModel> Datas;
    RiwayatPesananFragment riwayatPesananFragment;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public RiwayatPesananAdapter(RiwayatPesananFragment riwayatPesananFragment, List<RiwayatPesananModel> riwayatPesananModel, int view) {
        this.context = riwayatPesananFragment.getContext();
        this.riwayatPesananFragment = riwayatPesananFragment;
        this.Datas = riwayatPesananModel;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new RiwayatPesananAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new RiwayatPesananAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final RiwayatPesananModel mCurrentItem = Datas.get(position);
            final RiwayatPesananAdapter.ItemHolder itemHolder = (RiwayatPesananAdapter.ItemHolder) holder;

            //settext
            itemHolder.txtTglOrder.setText(mCurrentItem.getTgl_order());
            itemHolder.txtNoFaktur.setText(mCurrentItem.getNo_faktur());
            itemHolder.txtNomorOrder.setText(mCurrentItem.getNo_order());
            itemHolder.txtQty.setText(String.valueOf(mCurrentItem.getQty()));
            itemHolder.txtTotal.setText(Global.FloatToStrFmt(mCurrentItem.getTotal(),true));

            //default
            itemHolder.txtBelumProses.setVisibility(View.GONE);
            itemHolder.txtProses.setVisibility(View.GONE);
            itemHolder.txtSelesai.setVisibility(View.GONE);
            itemHolder.txtBatal.setVisibility(View.GONE);
            itemHolder.txtLunas.setVisibility(View.GONE);
            itemHolder.txtDariApps.setVisibility(View.GONE);
            itemHolder.txtTolak.setVisibility(View.GONE);

            if (mCurrentItem.getIs_belum_proses().equals(JConst.TRUE_STRING)){
                itemHolder.txtBelumProses.setVisibility(View.VISIBLE);
                itemHolder.txtProses.setVisibility(View.GONE);
                itemHolder.txtSelesai.setVisibility(View.GONE);
                itemHolder.txtBatal.setVisibility(View.GONE);
                itemHolder.txtTolak.setVisibility(View.GONE);
            }
            if (mCurrentItem.getIs_proses().equals(JConst.TRUE_STRING)){
                itemHolder.txtBelumProses.setVisibility(View.GONE);
                itemHolder.txtProses.setVisibility(View.VISIBLE);
                itemHolder.txtSelesai.setVisibility(View.GONE);
                itemHolder.txtBatal.setVisibility(View.GONE);
                itemHolder.txtTolak.setVisibility(View.GONE);
            }
            if (mCurrentItem.getIs_selesai().equals(JConst.TRUE_STRING)){
                itemHolder.txtBelumProses.setVisibility(View.GONE);
                itemHolder.txtProses.setVisibility(View.GONE);
                itemHolder.txtSelesai.setVisibility(View.VISIBLE);
                itemHolder.txtBatal.setVisibility(View.GONE);
                itemHolder.txtTolak.setVisibility(View.GONE);
            }
            if (mCurrentItem.getIs_batal().equals(JConst.TRUE_STRING)){
                itemHolder.txtBelumProses.setVisibility(View.GONE);
                itemHolder.txtProses.setVisibility(View.GONE);
                itemHolder.txtSelesai.setVisibility(View.GONE);
                itemHolder.txtBatal.setVisibility(View.VISIBLE);
                itemHolder.txtTolak.setVisibility(View.GONE);
            }
            if (mCurrentItem.getIs_tolak().equals(JConst.TRUE_STRING)){
                itemHolder.txtBelumProses.setVisibility(View.GONE);
                itemHolder.txtProses.setVisibility(View.GONE);
                itemHolder.txtSelesai.setVisibility(View.GONE);
                itemHolder.txtBatal.setVisibility(View.GONE);
                itemHolder.txtTolak.setVisibility(View.VISIBLE);
            }

            if (mCurrentItem.getIs_lunas().equals(JConst.TRUE_STRING)){
                itemHolder.txtLunas.setVisibility(View.VISIBLE);
            }

            if (mCurrentItem.getIs_dari_apps().equals(JConst.TRUE_STRING)){
                itemHolder.txtDariApps.setVisibility(View.VISIBLE);
            }

            itemHolder.itemView.setOnClickListener(view1 -> {
                String status = "";
                if (itemHolder.txtBelumProses.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_BELUM_DIPROSES;
                }
                if (itemHolder.txtProses.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_DIPROSES;
                }
                if (itemHolder.txtSelesai.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_SELESAI;
                }
                if (itemHolder.txtLunas.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_LUNAS;
                }
                if (itemHolder.txtBatal.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_DIBATALKAN;
                }
                if (itemHolder.txtDariApps.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_DARI_APPS;
                }
                if (itemHolder.txtTolak.getVisibility() == View.VISIBLE){
                    status += JConst.TEXT_STATUS_DITOLAK;
                }
                JApplication.getInstance().isNeedResume = true;
                Intent intent = new Intent(context, PesananActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("seq_pesanan", mCurrentItem.getSeq());
                intent.putExtra("is_dari_apps", mCurrentItem.getIs_dari_apps());
                intent.putExtra("tgl_order", mCurrentItem.getTgl_order());
                intent.putExtra("no_faktur", mCurrentItem.getNo_faktur());
                intent.putExtra("no_order", mCurrentItem.getNo_order());
                intent.putExtra("status", status);
                context.startActivity(intent);
            });

        }else if (holder instanceof RiwayatPesananAdapter.LoadingViewHolder){
            RiwayatPesananAdapter.LoadingViewHolder loadingViewHolder = (RiwayatPesananAdapter.LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtTglOrder, txtNoFaktur, txtNomorOrder, txtQty, txtTotal;
        TextView txtBelumProses, txtProses, txtSelesai, txtLunas, txtBatal, txtDariApps, txtTolak;

        public ItemHolder(View itemView) {
            super(itemView);
            txtTglOrder = itemView.findViewById(R.id.txtTglOrder);
            txtNoFaktur = itemView.findViewById(R.id.txtNoFaktur);
            txtNomorOrder = itemView.findViewById(R.id.txtNomorOrder);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtBelumProses = itemView.findViewById(R.id.txtBelumProses);
            txtProses = itemView.findViewById(R.id.txtProses);
            txtSelesai = itemView.findViewById(R.id.txtSelesai);
            txtLunas = itemView.findViewById(R.id.txtLunas);
            txtBatal = itemView.findViewById(R.id.txtBatal);
            txtDariApps = itemView.findViewById(R.id.txtDariApps);
            txtTolak = itemView.findViewById(R.id.txtTolak);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<RiwayatPesananModel> RiwayatPesananModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(RiwayatPesananModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(RiwayatPesananModel RiwayatPesananModel) {
        this.Datas.add(RiwayatPesananModel);
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
