package com.orion.sinar_surya.activities.product_list;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.models.UrutkanModel;

import java.util.List;

public class UrutkanAdapter extends RecyclerView.Adapter {
    Context context;
    List<UrutkanModel> items;
    Fragment fragment;

    private final String TEXT_URUTKAN_NAMA     = "Nama";
    private final String TEXT_URUTKAN_TERMURAH = "Termurah";
    private final String TEXT_URUTKAN_TERMAHAL = "Termahal";


    public UrutkanAdapter(Context context, List<UrutkanModel> item, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.items = item;
    }

    public void addModels(List<UrutkanModel> items) {
        int pos = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(pos, items.size());
    }

    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.list_item_urutkan, parent, false);
        return new ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final UrutkanModel item = items.get(position);
        final ItemHolder itemHolder = (ItemHolder) holder;

        itemHolder.txtUrutkan.setText(item.getNama());
        if (item.isPilih()){
            itemHolder.imgCeklis.setVisibility(View.VISIBLE);
        }else{
            itemHolder.imgCeklis.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < items.size(); i++) {
                    if (i == position) {
                        items.get(i).setPilih(!items.get(i).isPilih());
                    } else {
                        items.get(i).setPilih(false);
                    }
                }
                notifyDataSetChanged();

                if (fragment instanceof ProductListFragment) {
                    ProductListFragment productListFragment = (ProductListFragment) fragment;
                    productListFragment.OrderBy = SetFilter(item.isPilih() ? item.getNama() : "");
                    productListFragment.DialogFilter.dismiss();
                    if (item.isPilih()){
                        productListFragment.SetTextUrutkan(item.getNama());
                    }else{
                        productListFragment.SetTextUrutkan("Urutkan");
                    }
                    productListFragment.RcvProductList.smoothScrollToPosition(1);
                    productListFragment.Reload();
                } else if (fragment instanceof ProductListPromoFragment) {
                    ProductListPromoFragment productListPromoFragment = (ProductListPromoFragment) fragment;
                    productListPromoFragment.OrderBy = SetFilter(item.getNama());
                    productListPromoFragment.DialogFilter.dismiss();
                    productListPromoFragment.RcvProductList.smoothScrollToPosition(1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtUrutkan;
        ImageView imgCeklis;

        public ItemHolder(View itemView) {
            super(itemView);
            txtUrutkan = itemView.findViewById(R.id.txtUrutkan);
            imgCeklis = itemView.findViewById(R.id.imgCeklis);
        }
    }

    private String SetFilter(String text_urutkan){
        String hasil = "diskon_pct%20DESC"; //default
        if (text_urutkan.equals(TEXT_URUTKAN_NAMA)){
            hasil =  "nama_induk";
        }else if(text_urutkan.equals(TEXT_URUTKAN_TERMURAH)){
            hasil =  "harga_akhir%20ASC";
        }else if(text_urutkan.equals(TEXT_URUTKAN_TERMAHAL)){
            hasil =  "harga_akhir%20DESC";
        }
        return hasil;
    }
}
