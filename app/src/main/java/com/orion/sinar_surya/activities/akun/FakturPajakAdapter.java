package com.orion.sinar_surya.activities.akun;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.pesanan.PesananActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.models.FakturPajakModel;

import java.util.List;

public class FakturPajakAdapter extends RecyclerView.Adapter {
    Context context;
    List<FakturPajakModel> Datas;
    FakturPajakActivity fakturPajakActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public FakturPajakAdapter(FakturPajakActivity fakturPajakActivity, List<FakturPajakModel> fakturPajakModel, int view) {
        this.context = fakturPajakActivity.getBaseContext();
        this.fakturPajakActivity = fakturPajakActivity;
        this.Datas = fakturPajakModel;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new FakturPajakAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new FakturPajakAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final FakturPajakModel mCurrentItem = Datas.get(position);
            final FakturPajakAdapter.ItemHolder itemHolder = (FakturPajakAdapter.ItemHolder) holder;

            //settext
            itemHolder.txtTglOrder.setText(mCurrentItem.getTgl_faktur());
            itemHolder.txtNoFaktur.setText(mCurrentItem.getNo_faktur());
            itemHolder.txtNomorOrder.setText(mCurrentItem.getNo_order());
            itemHolder.txtTotal.setText(Global.FloatToStrFmt(mCurrentItem.getTotal(),true));

            itemHolder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fakturPajakActivity.downlaodFile(Routes.url_path_file_faktur()+'/'+mCurrentItem.getAttachment(), mCurrentItem.getAttachment());
                }
            });

        }else if (holder instanceof FakturPajakAdapter.LoadingViewHolder){
            FakturPajakAdapter.LoadingViewHolder loadingViewHolder = (FakturPajakAdapter.LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtTglOrder, txtNoFaktur, txtNomorOrder, txtTotal;
        Button btnDownload;

        public ItemHolder(View itemView) {
            super(itemView);
            txtTglOrder = itemView.findViewById(R.id.txtTglOrder);
            txtNoFaktur = itemView.findViewById(R.id.txtNoFaktur);
            txtNomorOrder = itemView.findViewById(R.id.txtNomorOrder);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<FakturPajakModel> FakturPajakModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(FakturPajakModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(FakturPajakModel FakturPajakModel) {
        this.Datas.add(FakturPajakModel);
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
