package com.orion.sinar_surya.activities.product_list;

import static android.app.Activity.RESULT_OK;
import static java.lang.Math.round;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.ILoadMore;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.globals.calculateWidth;
import com.orion.sinar_surya.models.ProductListModel;
import com.orion.sinar_surya.models.UrutkanModel;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ProductListFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    public Dialog DialogFilter;
    public RecyclerView RcvProductList;
    private RecyclerView RcvUrutkan;
    public ProductListAdapter mAdapter;
    private UrutkanAdapter mAdapterUrutkan;

    public static TextView txtFilter, txtUrutkan, txtJenisTampilan, txtPromo, txtAll;
    private ImageView btnFilter, btnUrutkan, btnJenisTampilan, btnPromo;
    private LinearLayoutCompat layoutFilterBarang, layoutFilterUrutkan, layoutFilterJenisTampilan, layoutFilterPromo, layoutFilterAll;
    private Button btnCekPromo;
    private int mMaxScrollSize;
    private AppBarLayout appbarLayout;
    private SwipeRefreshLayout swipe;
    private SearchView txtSearch;
    private DotsIndicator dotsIndicator;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsing;

    int visibleThreshold = 20;
    int lastVisibleItem, totalItemCount;
    boolean IsLoading;
    ILoadMore loadMore;

    private LinearLayoutManager linearLayoutManager;
    public List<ProductListModel> ListItems = new ArrayList<>();
    private List<UrutkanModel> ListItemsUrutkan = new ArrayList<>();
    private View v;

    private final String TEXT_URUTKAN_NAMA = "Nama";
    private final String TEXT_URUTKAN_TERMURAH = "Termurah";
    private final String TEXT_URUTKAN_TERMAHAL = "Termahal";

    private final String TEXT_SEMUA = "NONE";
    //Filter
    String JENIS_BARANG, KLASIFIKASI, UKURAN;

    public String OrderBy = "";

    private final String MODE_TAMPILAN_GRID = "G";
    private final String MODE_TAMPILAN_LIST = "L";

    private FragmentActivity thisActivity;
    private String ModeTampilan;
    private int NoOfColumns;
    private DotIndicatorPagerAdapter adapter;
    private int CountAllVew;
    final List<View> pageList = new ArrayList<>();
    private ShimmerLayout shimmerLayout;
    private String isFilterPromo = "";
    public Map<Integer, String> seqListMap = new HashMap<>();
    public Map<Integer, String> pathGambarListMap = new HashMap<>();
    private boolean isInitial;
    private List<LinearLayoutCompat> ListLayoutFilter = new ArrayList<>();;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int CurrentIdx = 0;

    //filter kusus promo
    private AutoCompleteTextView spnJenisBarang, spnKlasifikasi, spnUkuran;
    private LinearLayoutCompat layoutFilterBrgDiskon;
    private ImageView imgReset;
    private String JENIS_BARANG_PROMO, KLASIFIKASI_PROMO, UKURAN_PROMO;
    private final List<String> ListJenisBarang = new ArrayList<>();
    private final List<String> ListKlasifikasi = new ArrayList<>();
    private final List<String> ListUkuran = new ArrayList<>();
    private ArrayAdapter<String> AdapterJenisBarang;
    private ArrayAdapter<String> AdapterKlasifikasi;
    private ArrayAdapter<String> AdapterUkuran;

    public ProductListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_product_list, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        createPageList();
        LoadData();
        JApplication.getInstance().viewTempProduct = view;
        return view;
    }

    private void CreateView() {
        this.btnFilter = v.findViewById(R.id.btnFilter);
        this.btnUrutkan = v.findViewById(R.id.btnUrutkan);
        this.RcvProductList = v.findViewById(R.id.rcvLoad);
        this.appbarLayout = v.findViewById(R.id.materialup_appbar);
        this.swipe = v.findViewById(R.id.swipe_refresh_layout);
        this.txtSearch = v.findViewById(R.id.txtSearch);
        this.btnJenisTampilan = v.findViewById(R.id.btnJenisTampilan);
        this.btnPromo = v.findViewById(R.id.btnPromo);
        this.dotsIndicator = v.findViewById(R.id.dots_indicator);
        this.viewPager = v.findViewById(R.id.view_pager);
        this.collapsing = v.findViewById(R.id.collapsingToolbar);
        this.txtFilter = v.findViewById(R.id.txtFilter);
        this.txtUrutkan = v.findViewById(R.id.txtUrutkan);
        this.txtJenisTampilan = v.findViewById(R.id.txtJenisTampilan);
        this.txtPromo = v.findViewById(R.id.txtPromo);
        this.txtAll = v.findViewById(R.id.txtAll);
        this.layoutFilterBarang = v.findViewById(R.id.layoutFilterBarang);
        this.layoutFilterUrutkan = v.findViewById(R.id.layoutFilterUrutkan);
        this.layoutFilterJenisTampilan = v.findViewById(R.id.layoutFilterJenisTampilan);
        this.layoutFilterPromo = v.findViewById(R.id.layoutFilterPromo);
        this.layoutFilterAll = v.findViewById(R.id.layoutFilterAll);
        this.layoutFilterBrgDiskon = v.findViewById(R.id.layoutFilterBrgDiskon);
        this.spnJenisBarang = v.findViewById(R.id.spnJenisBarang);
        this.spnKlasifikasi = v.findViewById(R.id.spnKlasifikasi);
        this.spnUkuran = v.findViewById(R.id.spnUkuran);
        this.imgReset = v.findViewById(R.id.imgReset);
    }

    private void InitClass() {
        isInitial = true;
        thisActivity = getActivity();
        ModeTampilan = MODE_TAMPILAN_GRID;
        NoOfColumns = calculateWidth.calculateNoOfColumns(thisActivity);
        SetJenisTampilan();

        ListItemsUrutkan.clear();
        ListItemsUrutkan.add(new UrutkanModel(TEXT_URUTKAN_NAMA, false));
        ListItemsUrutkan.add(new UrutkanModel(TEXT_URUTKAN_TERMURAH, false));
        ListItemsUrutkan.add(new UrutkanModel(TEXT_URUTKAN_TERMAHAL, false));


        this.DialogFilter = new Dialog(thisActivity);
        this.DialogFilter.setContentView(R.layout.urutkan_product_list);
        this.RcvUrutkan = DialogFilter.findViewById(R.id.RcvUrutkan);

        RcvUrutkan.setLayoutManager(new LinearLayoutManager(thisActivity));
        mAdapterUrutkan = new UrutkanAdapter(thisActivity, ListItemsUrutkan, this);
        RcvUrutkan.setAdapter(mAdapterUrutkan);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        Toolbar toolbar = v.findViewById(R.id.ToolbarAct);
        ((AppCompatActivity) thisActivity).setSupportActionBar(toolbar);

        resetFilter();

        OrderBy = "diskon_pct%20DESC";
        CountAllVew = 0;

        setBoldText(txtAll, true);

        ListLayoutFilter.add(layoutFilterBarang);
        ListLayoutFilter.add(layoutFilterUrutkan);
        ListLayoutFilter.add(layoutFilterJenisTampilan);
        ListLayoutFilter.add(layoutFilterPromo);
        ListLayoutFilter.add(layoutFilterAll);

        layoutFilterBrgDiskon.setVisibility(View.GONE);
        isiCombo();
    }


    protected void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!IsLoading) {
                    Reload();
                }
            }
        });

        setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                //mAdapter.addMoel(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                  //      mAdapter.removeMoel(ListItems.size() - 1);
                        LoadData();
                    }
                }, 1000);

            }
        });

        RcvProductList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RefreshRecyclerView();
            }
        });

        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Reload();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });

        txtSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Reload();
                return false;
            }
        });


        layoutFilterBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(layoutFilterBarang);
                Intent s = new Intent(thisActivity, FilterProductListActivity.class);
                s.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                s.putExtra("JENIS_BARANG", JENIS_BARANG);
                s.putExtra("KLASIFIKASI", KLASIFIKASI);
                s.putExtra("UKURAN", UKURAN);
                startActivityForResult(s, 1);
            }
        });

        layoutFilterPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(layoutFilterPromo);
                Reload();
            }
        });

        layoutFilterUrutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(layoutFilterUrutkan);
                DialogFilter.show();
            }
        });

        layoutFilterJenisTampilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemSelected(layoutFilterJenisTampilan);
                if (ModeTampilan == MODE_TAMPILAN_GRID) {
                    ModeTampilan = MODE_TAMPILAN_LIST;
                } else if (ModeTampilan == MODE_TAMPILAN_LIST) {
                    ModeTampilan = MODE_TAMPILAN_GRID;
                }
                SetJenisTampilan();
                mAdapter.notifyDataSetChanged();
            }
        });

        layoutFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(layoutFilterAll);
                resetFilter();
                Reload();
            }
        });

//        spnJenisBarang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                JENIS_BARANG_PROMO = parent.getItemAtPosition(position).toString();
//                Reload();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        spnJenisBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnJenisBarang.showDropDown();
            }
        });

        spnJenisBarang.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                JENIS_BARANG_PROMO = parent.getItemAtPosition(position).toString();
                if (JENIS_BARANG_PROMO.equals(TEXT_SEMUA)){
                    spnJenisBarang.setText("");
                }
                Reload();
            }
        });

        spnKlasifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnKlasifikasi.showDropDown();
            }
        });

        spnKlasifikasi.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                KLASIFIKASI_PROMO = parent.getItemAtPosition(position).toString();
                if (KLASIFIKASI_PROMO.equals(TEXT_SEMUA)){
                    spnKlasifikasi.setText("");
                }
                Reload();
            }
        });

        spnUkuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnUkuran.showDropDown();
            }
        });

        spnUkuran.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                UKURAN_PROMO = parent.getItemAtPosition(position).toString();
                if (UKURAN_PROMO.equals(TEXT_SEMUA)){
                    spnUkuran.setText("");
                }
                Reload();
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCombo();
                Reload();
            }
        });
    }

    private void setBoldText(TextView textView, boolean isBold) {
        if (isBold) {
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }

    private TextView getTextViewFromLayout(LinearLayoutCompat layout) {
        TextView textView = null;
        switch (layout.getId()) {
            case R.id.layoutFilterAll:
                textView = txtAll;
                break;
            case R.id.layoutFilterPromo:
                textView = txtPromo;
                break;
            case R.id.layoutFilterUrutkan:
                textView = txtUrutkan;
                break;
            case R.id.layoutFilterJenisTampilan:
                textView = txtJenisTampilan;
                break;
            case R.id.layoutFilterBarang:
                textView = txtFilter;
                break;
        }
        return textView;
    }

    private void setItemSelected(LinearLayoutCompat selectedLayout) {
        for (LinearLayoutCompat layout : ListLayoutFilter) {
            if (layout == selectedLayout) {
                // Set warna latar belakang yang dipilih
                selectedLayout.setBackgroundColor(getResources().getColor(R.color.sinar_surya_color_5));

                // Set text menjadi bold
                setBoldText(getTextViewFromLayout(selectedLayout), true);

                // cek jika yang dipilih promo maka munculkan filter kusus promo
                if (selectedLayout == layoutFilterPromo){
                    //munculkan
                    ObjectAnimator animator = ObjectAnimator.ofFloat(layoutFilterBrgDiskon, "alpha", 0f, 1f);
                    animator.setDuration(250); // Adjust the duration as needed
                    layoutFilterBrgDiskon.setVisibility(View.VISIBLE);
                    animator.start();
                    resetCombo();
                    isFilterPromo = "T";
                }else{
                    ObjectAnimator animator = ObjectAnimator.ofFloat(layoutFilterBrgDiskon, "alpha", 1f, 0f);
                    animator.setDuration(250); // Adjust the duration as needed
                    animator.addListener(new android.animation.AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(android.animation.Animator animation) {
                            layoutFilterBrgDiskon.setVisibility(View.GONE);
                        }
                    });
                    animator.start();
                    resetCombo();
                    isFilterPromo = "";
                }
            } else {
                // Reset warna latar belakang item lainnya ke warna default
                layout.setBackgroundColor(getResources().getColor(R.color.white));
                // Set text menjadi normal
                setBoldText(getTextViewFromLayout(layout), false);
            }
        }
    }

    public void resetFilter(){
        JENIS_BARANG= TEXT_SEMUA;
        KLASIFIKASI = TEXT_SEMUA;
        UKURAN      = TEXT_SEMUA;
    }

    public void RefreshRecyclerView() {
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!IsLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (loadMore != null) {
                loadMore.onLoadMore();
                setLoaded(true);
            }
        }
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public void setLoaded(boolean loading) {
        IsLoading = loading;
    }

    public void SetTextUrutkan(String Text) {
        txtUrutkan.setText(Text);
    }

    public void SetTextStyle(int style) {
        txtUrutkan.setTypeface(null, style);
    }

    private void SetJenisTampilan() {
        if (ModeTampilan.equals(MODE_TAMPILAN_GRID)) {
            mAdapter = new ProductListAdapter(thisActivity, ListItems, this, R.layout.list_item_product_list_grid);
            RcvProductList.setLayoutManager(new GridLayoutManager(thisActivity, NoOfColumns, GridLayoutManager.VERTICAL, false));
            linearLayoutManager = (LinearLayoutManager) RcvProductList.getLayoutManager();
            RcvProductList.setAdapter(mAdapter);

            txtJenisTampilan.setText("List");
            btnJenisTampilan.setImageResource(R.drawable.ic_list);
        } else if (ModeTampilan.equals(MODE_TAMPILAN_LIST)) {
            mAdapter = new ProductListAdapter(thisActivity, ListItems, this, R.layout.list_item_product_list);
            RcvProductList.setLayoutManager(new LinearLayoutManager(thisActivity));
            linearLayoutManager = (LinearLayoutManager) RcvProductList.getLayoutManager();
            RcvProductList.setAdapter(mAdapter);

            txtJenisTampilan.setText("Grid");
            btnJenisTampilan.setImageResource(R.drawable.ic_grid);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }
    }

    private void LoadData() {
        String filterJenisBarang = "", filterKlasifikasi = "",filterUkuran = "";
        if (!isFilterPromo.equals(JConst.TRUE_STRING)) {
            filterJenisBarang = JENIS_BARANG.equals(TEXT_SEMUA) ? "" : JENIS_BARANG;
            filterKlasifikasi = KLASIFIKASI.equals(TEXT_SEMUA) ? "" : KLASIFIKASI;
            filterUkuran = UKURAN.equals(TEXT_SEMUA) ? "" : UKURAN;
        }else{
            filterJenisBarang = JENIS_BARANG_PROMO.equals(TEXT_SEMUA) ? "" : JENIS_BARANG_PROMO;
            filterKlasifikasi = KLASIFIKASI_PROMO.equals(TEXT_SEMUA) ? "" : KLASIFIKASI_PROMO;
            filterUkuran = UKURAN_PROMO.equals(TEXT_SEMUA) ? "" : UKURAN_PROMO;
        }

        String filter = "?offset=" + mAdapter.getItemCount() + "&database_id=" + SharedPrefsUtils.getIntegerPreference(thisActivity, "database_id", 0)
                        +"&sort="+OrderBy
                        +"&is_diskon="+isFilterPromo
                        +"&search="+ txtSearch.getQuery()
                        +"&jenis_barang="+ filterJenisBarang
                        +"&klasifikasi="+ filterKlasifikasi
                        +"&ukuran="+ filterUkuran
                        +"&customer_seq=" + SharedPrefsUtils.getIntegerPreference(getContext(), "customer_seq", 0);

        String url = Routes.url_get_barang_for_rekap() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                List<ProductListModel> itemDataModels = new ArrayList<>();
                itemDataModels.clear();

                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    for (int i = 0; i < ArrResults.length(); i++) {
                        try {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            ProductListModel Data = new ProductListModel(
                                    obj.getString("nama_induk"),
                                    obj.getString("file_gambar"),
                                    obj.getDouble("harga_awal"),
                                    obj.getDouble("harga_akhir"),
                                    obj.getDouble("diskon_pct")
                            );
                            itemDataModels.add(Data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(thisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            swipe.setRefreshing(false);
                        }
                    }
                    //isi
                    mAdapter.addModels(itemDataModels);
                    setLoaded(false);
                    swipe.setRefreshing(false);
                    if (ListItems.size() <= 0){Toast.makeText(thisActivity, "Tidak ditemukan", Toast.LENGTH_SHORT).show();}

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(thisActivity, pesan, Toast.LENGTH_SHORT).show();
                    swipe.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                swipe.setRefreshing(false);
            }
        }, error -> {
            swipe.setRefreshing(false);
            Runnable runReconnect = new Runnable() {
                @Override
                public void run() {
                    LoadData();
                }
            };
            Global.handleError(getActivity(), error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    @NonNull
    private List<View> createPageList() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(thisActivity, "database_id", 0);

        String url = Routes.url_get_setting_promo_mst() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                int CountView = 0;
                seqListMap.clear();
                pathGambarListMap.clear();
                pageList.clear();

                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    for (int i = 0; i < ArrResults.length(); i++) {
                        try {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            String path = Routes.url_path_gambar_promo() + "/" + obj.getString("file_gambar");
                            pageList.add(createPageView(0, path));
                            pathGambarListMap.put(i, path);
                            seqListMap.put(i, obj.getString("seq"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(thisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                        CountView += 1;
                    }
                    CountAllVew = CountView;
                    CurrentIdx = CountView;

                    adapter = new DotIndicatorPagerAdapter(ProductListFragment.this);
                    adapter.setData(pageList);
                    viewPager.setAdapter(adapter);
                    viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                    dotsIndicator.setViewPager(viewPager);
//                    startPageSwitcher();

                    if (CountView == 0){
//                        btnCekPromo.setVisibility(View.INVISIBLE);
                        collapsing.setVisibility(View.GONE);
                    }else{
//                        btnCekPromo.setVisibility(View.VISIBLE);
                        collapsing.setVisibility(View.VISIBLE);
                    }

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(thisActivity, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
        return pageList;
    }

    @NonNull
    private View createPageView(int color, String path) {
        ViewGroup container = null;
        View view = View.inflate(thisActivity, R.layout.layout_carousel, container);
        ImageView ImgCarousel = view.findViewById(R.id.ImgCarousel);
        Picasso.get().load(path.replace("%", "%25")).into(ImgCarousel);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startPageSwitcher();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPageSwitcher();
    }

    private void startPageSwitcher() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (CountAllVew > 0) {
                    if (CurrentIdx == CountAllVew) {
                        adapter = new DotIndicatorPagerAdapter(ProductListFragment.this);
                        adapter.setData(pageList);
                        viewPager.setAdapter(adapter);
                        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                        dotsIndicator.setViewPager(viewPager);
                        CurrentIdx = 0;
                    } else {
                        CurrentIdx = viewPager.getCurrentItem() + 1;
                    }
                    viewPager.setCurrentItem(CurrentIdx, true);
                }
                handler.postDelayed(this, 4000); // Ubah sesuai dengan interval yang Anda inginkan (dalam milidetik)
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void stopPageSwitcher() {
        handler.removeCallbacks(runnable);
    }


    public void Reload() {
        swipe.setRefreshing(true);
        mAdapter.removeAllModel();
        RefreshRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extra = data.getExtras();
                JENIS_BARANG = extra.getString("JENIS_BARANG");
                KLASIFIKASI  = extra.getString("KLASIFIKASI");
                UKURAN       = extra.getString("UKURAN");
                RcvProductList.scrollToPosition(0);
                Reload();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (isInitial) {
            txtSearch.setQuery("", false);
            isInitial = false;
        }
    }


    private void resetCombo() {
        JENIS_BARANG_PROMO  = "";
        KLASIFIKASI_PROMO   = "";
        UKURAN_PROMO        = "";

        spnJenisBarang.setText(JENIS_BARANG_PROMO);
        spnKlasifikasi.setText(KLASIFIKASI_PROMO);
        spnUkuran.setText(UKURAN_PROMO);

        AdapterJenisBarang.notifyDataSetChanged();
        AdapterKlasifikasi .notifyDataSetChanged();
        AdapterUkuran.notifyDataSetChanged();
    }

    private void isiCombo() {
        JENIS_BARANG_PROMO  = "";
        KLASIFIKASI_PROMO   = "";
        UKURAN_PROMO        = "";

        AdapterJenisBarang = new ArrayAdapter<String>(thisActivity, R.layout.custom_spinner_item, ListJenisBarang);
        AdapterJenisBarang.setDropDownViewResource(R.layout.custom_spinner_item);
        spnJenisBarang.setAdapter(AdapterJenisBarang);

        AdapterKlasifikasi = new ArrayAdapter<String>(thisActivity, R.layout.custom_spinner_item, ListKlasifikasi);
        AdapterKlasifikasi.setDropDownViewResource(R.layout.custom_spinner_item);
        spnKlasifikasi.setAdapter(AdapterKlasifikasi);

        AdapterUkuran = new ArrayAdapter<String>(thisActivity, R.layout.custom_spinner_item, ListUkuran);
        AdapterUkuran.setDropDownViewResource(R.layout.custom_spinner_item);
        spnUkuran.setAdapter(AdapterUkuran);

        setJenisBarang();
        setKlasifikasi();
        setUkuran();
    }

    private void setJenisBarang() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(thisActivity, "database_id", 0);
        String url = Routes.url_get_jenis_barang() + filter;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals(JConst.STATUS_API_SUCCESS)) {
                            ListJenisBarang.clear();
                            ListJenisBarang.add(TEXT_SEMUA);

                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                ListJenisBarang.add(obj.getString("nama"));
                            }

                            AdapterJenisBarang.notifyDataSetChanged();
                            
                        } else if (status.equals(JConst.STATUS_API_FAILED)) {
                            String pesan = response.getString("error");
                            Toast.makeText(thisActivity, pesan, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("ERROR REQUEST", error.getMessage())
        );

        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private void setKlasifikasi() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(thisActivity, "database_id", 0);
        String url = Routes.url_get_klasifikasi() + filter;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals(JConst.STATUS_API_SUCCESS)) {
                            ListKlasifikasi.clear();
                            ListKlasifikasi.add(TEXT_SEMUA);

                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                ListKlasifikasi.add(obj.getString("nama"));
                            }

                            AdapterKlasifikasi.notifyDataSetChanged();

                        } else if (status.equals(JConst.STATUS_API_FAILED)) {
                            String pesan = response.getString("error");
                            Toast.makeText(thisActivity, pesan, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private void setUkuran() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(thisActivity, "database_id", 0);
        String url = Routes.url_get_ukuran() + filter;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals(JConst.STATUS_API_SUCCESS)) {
                            ListUkuran.clear();
                            ListUkuran.add(TEXT_SEMUA);

                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                ListUkuran.add(obj.getString("nama"));
                            }

                            AdapterUkuran.notifyDataSetChanged();

                        } else if (status.equals(JConst.STATUS_API_FAILED)) {
                            String pesan = response.getString("error");
                            Toast.makeText(thisActivity, pesan, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }


}