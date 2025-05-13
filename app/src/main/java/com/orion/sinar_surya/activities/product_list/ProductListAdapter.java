package com.orion.sinar_surya.activities.product_list;

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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.models.ProductListModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter {
    Context context;
    List<ProductListModel> ProductListModels;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    Fragment fragment;
    private ProgressDialog Loading;
    private int ViewProductList;

    public ProductListAdapter(Context context, List<ProductListModel> ProductListModels, Fragment fragment, int view) {
        this.context = context;
        this.ProductListModels = ProductListModels;
        this.fragment = fragment;
        this.Loading = new ProgressDialog(context);
        this.ViewProductList = view;
    }

    public void addModels(List<ProductListModel> ProductListModels) {
        int pos = this.ProductListModels.size();
        this.ProductListModels.addAll(ProductListModels);
        notifyItemRangeInserted(pos, ProductListModels.size());
    }

    public void addMoel(ProductListModel productListModel) {
        this.ProductListModels.add(productListModel);
        notifyItemRangeInserted(ProductListModels.size()-1,ProductListModels.size()-1);
    }

    public void removeMoel(int idx) {
        if (ProductListModels.size() > 0){
            this.ProductListModels.remove(ProductListModels.size()-1);
            notifyItemRemoved(ProductListModels.size());
        }
    }

    public void removeAllModel(){
        int LastPosition = ProductListModels.size();
        this.ProductListModels.removeAll(ProductListModels);
        notifyItemRangeRemoved(0, LastPosition);
    }

    public void SetJenisView(int view){
        this.ViewProductList = view;
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
            final ProductListModel mCurrentItem = ProductListModels.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            if (ViewProductList == R.layout.list_item_product_list_grid){
                if (!mCurrentItem.getGambar().equals("") ){
                    String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                    Picasso.get().load(path.replace("%", "%25")).into(itemHolder.imgBarang);
                    if (itemHolder.imgBarang.getDrawable() == null){
                        itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                    }
                }else{
                    itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                }
            }else if(ViewProductList == R.layout.list_item_product_list){
                if (!mCurrentItem.getGambar().equals("") ){
                    String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                    Picasso.get().load(path.replace("%", "%25")).into(itemHolder.imgBarang);
                    if (itemHolder.imgBarang.getDrawable() == null){
                        itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                    }
                }else{
                    itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                }
            }

            itemHolder.txtNama.setText(mCurrentItem.getNama());
            itemHolder.txtDiskon.setText(String.format("%s%%", Global.FloatToStrFmt(mCurrentItem.getDiskon_pct())));
            itemHolder.txtHargaAwal.setText(Global.FloatToStrFmt(mCurrentItem.getHarga_awal(), true));
            itemHolder.txtHargaAkhir.setText(Global.FloatToStrFmt(mCurrentItem.getHarga_akhir(), true));

            if (mCurrentItem.getDiskon_pct() > 0){
                itemHolder.txtHargaAwal.setVisibility(View.VISIBLE);
                itemHolder.txtDiskon.setVisibility(View.VISIBLE);
                itemHolder.txtHargaAwal.setPaintFlags(itemHolder.txtHargaAwal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                itemHolder.txtHargaAwal.setVisibility(View.GONE);
                itemHolder.txtDiskon.setVisibility(View.GONE);
            }

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                    Intent intent = new Intent(context, ProductListDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("nama_induk", mCurrentItem.getNama());
                    intent.putExtra("path_gambar_induk", path);
                    context.startActivity(intent);
                }
            });

        }else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ProductListModels.get(position) == null ? VIEW_TYVE_LOADING : VIEW_TYVE_ITEM;
    }

    @Override
    public int getItemCount() {
        return ProductListModels.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHargaAwal, txtHargaAkhir, txtDiskon;
        ImageView imgBarang;
        public ItemHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaBarang);
            txtHargaAwal = itemView.findViewById(R.id.txtHargaAwal);
            txtHargaAkhir = itemView.findViewById(R.id.txtHargaAkhir);
            txtDiskon = itemView.findViewById(R.id.txtDiskon);
            imgBarang = itemView.findViewById(R.id.ImgBarang);
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
