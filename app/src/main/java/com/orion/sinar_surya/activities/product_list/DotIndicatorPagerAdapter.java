package com.orion.sinar_surya.activities.product_list;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.orion.sinar_surya.R;

import java.util.ArrayList;
import java.util.List;

public class DotIndicatorPagerAdapter extends PagerAdapter {
    private List<View> viewList;
    private ProductListFragment productListFragment;

    DotIndicatorPagerAdapter(ProductListFragment productListFragment) {
        this.viewList = new ArrayList<>();
        this.productListFragment = productListFragment;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View item = viewList.get(position);
        container.addView(item);
        // Tambahkan listener klik di sini
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Membuat sebuah instance dari fragment
                ProductListPromoFragment fragment = new ProductListPromoFragment();

                // Membuat sebuah Bundle dan menambahkan data ke dalamnya
                int seqPromo = Integer.parseInt(productListFragment.seqListMap.get(position));
                String pathGambar = productListFragment.pathGambarListMap.get(position);

                Bundle bundle = new Bundle();
                bundle.putInt("seq_promo", seqPromo);
                bundle.putString("path_gambar", pathGambar);

                // Menetapkan Bundle ke fragment
                fragment.setArguments(bundle);
                // Menambahkan fragment ke dalam container
                productListFragment.getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, fragment)
                        .commit();
            }
        });
        return item;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    void setData(@Nullable List<View> list) {
        this.viewList.clear();
        if (list != null && !list.isEmpty()) {
            this.viewList.addAll(list);
        }

        notifyDataSetChanged();
    }


    @NonNull
    List<View> getData() {
        if (viewList == null) {
            viewList = new ArrayList<>();
        }

        return viewList;
    }
}
