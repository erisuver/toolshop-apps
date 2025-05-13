package com.orion.sinar_surya.activities.product_list_detail;

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

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.product_list.ProductListAdapter;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.models.ProductListDetailModel;
import com.orion.sinar_surya.models.ProductListModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListDetailAdapter  extends RecyclerView.Adapter {
    Context context;
    List<ProductListDetailModel> productListDetailModels;
    ProductListDetailActivity productListDetailActivity;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private int view;
    private int selectedItemPosition = RecyclerView.NO_POSITION;

    public ProductListDetailAdapter(Context context, List<ProductListDetailModel> productListDetailModels, int view, ProductListDetailActivity productListDetailActivity) {
        this.context = context;
        this.productListDetailModels = productListDetailModels;
        this.view = view;
        this.productListDetailActivity = productListDetailActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new ProductListDetailAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new ProductListDetailAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final ProductListDetailModel mCurrentItem = productListDetailModels.get(position);
            final ProductListDetailAdapter.ItemHolder itemHolder = (ProductListDetailAdapter.ItemHolder) holder;

            if (!mCurrentItem.getGambar().equals("") ){
                String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                Picasso.get().load(path.replace("%", "%25")).into(itemHolder.imgBarang);
                if (itemHolder.imgBarang.getDrawable() == null){
                    itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                }
            }

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                    productListDetailActivity.setGambar(path);
                    productListDetailActivity.updateUI(mCurrentItem.getSeq(), 0);

                    productListDetailActivity.setActiveImage(position);
                }
            });

        }else if (holder instanceof ProductListDetailAdapter.LoadingViewHolder){
            ProductListDetailAdapter.LoadingViewHolder loadingViewHolder = (ProductListDetailAdapter.LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return productListDetailModels.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        public ItemHolder(View itemView) {
            super(itemView);
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

    public void addModels(List<ProductListDetailModel> ProductListDetailModel) {
        int pos = this.productListDetailModels.size();
        this.productListDetailModels.addAll(ProductListDetailModel);
        notifyItemRangeInserted(pos, productListDetailModels.size());
    }

    public void addModel(ProductListDetailModel ProductListDetailModel) {
        this.productListDetailModels.add(ProductListDetailModel);
        notifyItemRangeInserted(productListDetailModels.size()-1,productListDetailModels.size()-1);
    }

    public void removeModel(int idx) {
        if (productListDetailModels.size() > 0){
            this.productListDetailModels.remove(productListDetailModels.size()-1);
            notifyItemRemoved(productListDetailModels.size());
        }
    }

    public void removeAllModel(){
        int LastPosition = productListDetailModels.size();
        this.productListDetailModels.removeAll(productListDetailModels);
        notifyItemRangeRemoved(0, LastPosition);
    }
}
