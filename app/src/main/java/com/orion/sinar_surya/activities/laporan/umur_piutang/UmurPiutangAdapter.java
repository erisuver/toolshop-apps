package com.orion.sinar_surya.activities.laporan.umur_piutang;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.activities.laporan.kartu_piutang.KartuPiutang;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.models.KartuPiutangModel;
import com.orion.sinar_surya.models.UmurPiutangModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UmurPiutangAdapter extends RecyclerView.Adapter {
    Context context;
    List<UmurPiutangModel> UmurPiutangModels;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    UmurPiutang umurPiutang;
    private ProgressDialog Loading;
    private int ViewProductList;

    public UmurPiutangAdapter(Context context, List<UmurPiutangModel> UmurPiutangModels, UmurPiutang umurPiutang, int view) {
        this.context = context;
        this.UmurPiutangModels = UmurPiutangModels;
        this.umurPiutang = umurPiutang;
        this.Loading = new ProgressDialog(context);
        this.ViewProductList = view;
    }

    public void addModels(List<UmurPiutangModel> UmurPiutangModel) {
        int pos = this.UmurPiutangModels.size();
        this.UmurPiutangModels.addAll(UmurPiutangModel);
        notifyItemRangeInserted(pos, UmurPiutangModels.size());
    }

    public void removeModel(int idx) {
        if (UmurPiutangModels.size() > 0){
            this.UmurPiutangModels.remove(UmurPiutangModels.size()-1);
            notifyItemRemoved(UmurPiutangModels.size());
        }
    }

    public void removeAllModel(){
        int LastPosition = UmurPiutangModels.size();
        this.UmurPiutangModels.removeAll(UmurPiutangModels);
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
        if (holder instanceof ItemHolder) {
            final UmurPiutangModel mCurrentItem = UmurPiutangModels.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtTipe.setText(mCurrentItem.getTipe());
            itemHolder.txtTotal.setText(Global.FloatToStrFmt(mCurrentItem.getTotal(), true));

            //detail part
            LinearLayoutManager hs_linearLayout = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
            itemHolder.rcvData.setLayoutManager(hs_linearLayout);
            itemHolder.rcvData.setHasFixedSize(true);
            UmurPiutangDetailAdapter detailAdapter = new UmurPiutangDetailAdapter(this.context, mCurrentItem.getDetail(), R.layout.list_item_det_umur_piutang);
            itemHolder.rcvData.setAdapter(detailAdapter);

            itemHolder.constraintLayoutMaster.setOnClickListener(view1 -> {
                if (mCurrentItem.isIs_total()){

                }else {
                    if (mCurrentItem.getTotal() > 0) {
                        if (itemHolder.layout_master.getVisibility() == View.VISIBLE) {
                            //                    itemHolder.layout_master.setVisibility(View.GONE);
                            ObjectAnimator animator = ObjectAnimator.ofFloat(itemHolder.layout_master, "alpha", 1f, 0f);
                            animator.setDuration(250); // Adjust the duration as needed
                            animator.addListener(new android.animation.AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(android.animation.Animator animation) {
                                    itemHolder.layout_master.setVisibility(View.GONE);
                                }
                            });
                            animator.start();
                            itemHolder.imgMaster.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));

                        } else {
                            itemHolder.layout_master.setVisibility(View.VISIBLE);
                            ObjectAnimator animator = ObjectAnimator.ofFloat(itemHolder.layout_master, "alpha", 0f, 1f);
                            animator.setDuration(250); // Adjust the duration as needed
                            itemHolder.layout_master.setVisibility(View.VISIBLE);
                            animator.start();
                            itemHolder.imgMaster.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
                        }
                    } else {

                        if (mCurrentItem.isExpand()) {
                            mCurrentItem.setExpand(false);
                            itemHolder.imgMaster.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));

                        } else {
                            mCurrentItem.setExpand(true);
                            itemHolder.imgMaster.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
                        }
                    }
                }
            });
            if (mCurrentItem.isIs_total()){
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) itemHolder.txtTotal.getLayoutParams();

                layoutParams.rightMargin = 20;
                itemHolder.txtTotal.setLayoutParams(layoutParams);
                itemHolder.imgMaster.setVisibility(View.GONE);
                itemHolder.layout_master.setVisibility(View.GONE);
            }else{
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) itemHolder.txtTotal.getLayoutParams();

                layoutParams.rightMargin = 0;
                itemHolder.txtTotal.setLayoutParams(layoutParams);
                itemHolder.imgMaster.setVisibility(View.VISIBLE);
//                itemHolder.layout_master.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return UmurPiutangModels.get(position) == null ? VIEW_TYVE_LOADING : VIEW_TYVE_ITEM;
    }

    @Override
    public int getItemCount() {
        return UmurPiutangModels.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtTipe, txtTotal;
        ImageView imgMaster;
        LinearLayout layout_master;
        RecyclerView rcvData;
        ConstraintLayout constraintLayoutMaster;

        public ItemHolder(View itemView) {
            super(itemView);
            txtTipe = itemView.findViewById(R.id.txtTipe);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            imgMaster = itemView.findViewById(R.id.imgMaster);
            layout_master = itemView.findViewById(R.id.layout_master);
            rcvData = itemView.findViewById(R.id.rcvData);
            constraintLayoutMaster = itemView.findViewById(R.id.constraintLayoutMaster);
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
