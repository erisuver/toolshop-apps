package com.orion.sinar_surya.activities.laporan.kartu_piutang;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.models.KartuPiutangModel;
import com.orion.sinar_surya.models.ProductListModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class KartuPiutangAdapter extends RecyclerView.Adapter {
    Context context;
    List<KartuPiutangModel> KartuPiutangModels;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    KartuPiutang kartuPiutang;
    private ProgressDialog Loading;
    private int ViewProductList;

    public KartuPiutangAdapter(Context context, List<KartuPiutangModel> KartuPiutangModels, KartuPiutang kartuPiutang, int view) {
        this.context = context;
        this.KartuPiutangModels = KartuPiutangModels;
        this.kartuPiutang = kartuPiutang;
        this.Loading = new ProgressDialog(context);
        this.ViewProductList = view;
    }

    public void addModels(List<KartuPiutangModel> KartuPiutangModel) {
        int pos = this.KartuPiutangModels.size();
        this.KartuPiutangModels.addAll(KartuPiutangModel);
        notifyItemRangeInserted(pos, KartuPiutangModels.size());
    }

    public void addMoel(KartuPiutangModel KartuPiutangModel) {
        this.KartuPiutangModels.add(KartuPiutangModel);
        notifyItemRangeInserted(KartuPiutangModels.size()-1,KartuPiutangModels.size()-1);
    }
    public void addMoel(int i, KartuPiutangModel KartuPiutangModel) {
        this.KartuPiutangModels.add(i, KartuPiutangModel);
        notifyItemRangeInserted(KartuPiutangModels.size()-1,KartuPiutangModels.size()-1);
    }

    public void removeMoel(int idx) {
        if (KartuPiutangModels.size() > 0){
            this.KartuPiutangModels.remove(KartuPiutangModels.size()-1);
            notifyItemRemoved(KartuPiutangModels.size());
        }
    }

    public void removeAllModel(){
        int LastPosition = KartuPiutangModels.size();
        this.KartuPiutangModels.removeAll(KartuPiutangModels);
        notifyItemRangeRemoved(0, LastPosition);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(ViewProductList, parent, false);
            return new ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final KartuPiutangModel mCurrentItem = KartuPiutangModels.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.txtTgl.setText(mCurrentItem.getTanggal());
            itemHolder.txtNomor.setText(mCurrentItem.getNomor());
            itemHolder.txtJT.setText(mCurrentItem.getTj());

            if (mCurrentItem.getTotal() >= 0) {
                itemHolder.txtNilai.setText(Global.FloatToStrFmt(mCurrentItem.getTotal()));
            } else {
                itemHolder.txtNilai.setText("(" + Global.FloatToStrFmt(Math.abs(mCurrentItem.getTotal())) + ")");
            }

        }else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return KartuPiutangModels.get(position) == null ? VIEW_TYVE_LOADING : VIEW_TYVE_ITEM;
    }

    @Override
    public int getItemCount() {
        return KartuPiutangModels.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtTgl, txtNomor, txtNilai, txtJT;
        ConstraintLayout clIsi;
        public ItemHolder(View itemView) {
            super(itemView);
            txtTgl = itemView.findViewById(R.id.txtTgl);
            txtNomor = itemView.findViewById(R.id.txtNomor);
            txtJT = itemView.findViewById(R.id.txtJT);
            txtNilai = itemView.findViewById(R.id.txtNilai);
            clIsi = itemView.findViewById(R.id.clIsi);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }
}
