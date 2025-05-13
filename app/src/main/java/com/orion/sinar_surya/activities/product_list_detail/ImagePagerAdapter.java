package com.orion.sinar_surya.activities.product_list_detail;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.ZoomImageFull.ZoomImageFullActivity;
import com.orion.sinar_surya.models.ProductListDetailModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private ProductListDetailActivity productListDetailActivity;
    private List<ProductListDetailModel> productListDetailModels;

    public ImagePagerAdapter(ProductListDetailActivity productListDetailActivity, List<ProductListDetailModel> productListDetailModels) {
        this.context = productListDetailActivity.getApplicationContext();
        this.productListDetailActivity = productListDetailActivity;
        this.productListDetailModels = productListDetailModels;
    }

    @Override
    public int getCount() {
        return productListDetailModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image, container, false);
        ProductListDetailModel mCurrentItem = productListDetailModels.get(position);

        ImageView imageView = view.findViewById(R.id.imageView);

        if (!mCurrentItem.getGambar().equals("") ){
            String path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
            Picasso.get().load(path.replace("%", "%25")).into(imageView);
            if (imageView.getDrawable() == null){
                imageView.setImageResource(R.drawable.gambar_tidak_tersedia);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pathGambar = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                Intent s = new Intent(productListDetailActivity, ZoomImageFullActivity.class);
                s.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                s.putExtra("GAMBAR", pathGambar);
                s.putExtra("NAMA", "gambar" + mCurrentItem.getSeq());
                productListDetailActivity.startActivity(s);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void removeAllModel(){
        this.productListDetailModels.removeAll(productListDetailModels);
        notifyDataSetChanged();
    }
    public void addModels(List<ProductListDetailModel> ProductListDetailModel) {
        this.productListDetailModels.addAll(ProductListDetailModel);
        notifyDataSetChanged();
    }
}