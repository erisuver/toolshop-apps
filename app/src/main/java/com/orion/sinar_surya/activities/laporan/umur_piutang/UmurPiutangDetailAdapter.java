package com.orion.sinar_surya.activities.laporan.umur_piutang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.models.UmurPiutangDetailModel;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UmurPiutangDetailAdapter extends RecyclerView.Adapter {
    Context context;
    List<UmurPiutangDetailModel> Datas;
    private List<UmurPiutangDetailModel> dataFiltererd;
    private int view;

    public UmurPiutangDetailAdapter(Context context, List<UmurPiutangDetailModel> datas, int view) {
        this.context = context;
        Datas = datas;
        this.dataFiltererd = new ArrayList<UmurPiutangDetailModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;

    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public TextView txtNoFaktur, txtJT, txtNilai, txtUmur;
        public ItemHolder(View itemView) {
            super(itemView);
            txtNoFaktur = itemView.findViewById(R.id.txtNoFaktur);
            txtJT = itemView.findViewById(R.id.txtJT);
            txtNilai = itemView.findViewById(R.id.txtNilai);
            txtUmur = itemView.findViewById(R.id.txtUmur);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new UmurPiutangDetailAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final UmurPiutangDetailModel data = dataFiltererd.get(position);
        final UmurPiutangDetailAdapter.ItemHolder itemHolder = (UmurPiutangDetailAdapter.ItemHolder) holder;
        itemHolder.txtNoFaktur.setText(data.getNoFaktur());
        itemHolder.txtJT.setText(data.getTglJT());
        itemHolder.txtNilai.setText(Global.FloatToStrFmt(data.getNilai(), true));
        itemHolder.txtUmur.setText(String.valueOf(data.getUmur()));
    }

    public void addModel(UmurPiutangDetailModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<UmurPiutangDetailModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltererd.size();
        this.dataFiltererd.removeAll(dataFiltererd);
        notifyItemRangeRemoved(0, LastPosition);
    }

}