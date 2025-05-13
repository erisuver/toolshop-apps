package com.orion.sinar_surya.activities.product_list;

import static java.lang.Math.round;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.ZoomImageFull.ZoomImageFullActivity;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.ILoadMore;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.globals.calculateWidth;
import com.orion.sinar_surya.models.ProductListModel;
import com.orion.sinar_surya.models.UrutkanModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListPromoFragment extends Fragment {

    public Dialog DialogFilter;
    public RecyclerView RcvProductList;
    private RecyclerView RcvUrutkan;
    public ProductListAdapter mAdapter;
    private UrutkanAdapter mAdapterUrutkan;

    private int mMaxScrollSize;
    private SwipeRefreshLayout swipe;

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

    private final String TEXT_SEMUA = "";
    //Filter
    String JENIS_BARANG, KLASIFIKASI, UKURAN;

    public String OrderBy = "";

    private final String MODE_TAMPILAN_GRID = "G";
    private final String MODE_TAMPILAN_LIST = "L";

    private FragmentActivity ThisActivity;
    private String ModeTampilan;
    private int NoOfColumns;
    private ImageView imgKlasifikasi;
    private Spinner spnUrutkan;
    private String PATH_GAMBAR;
    private int seqPromo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (JApplication.getInstance().viewTempProduct != null) {
            // Remove the fragment from its previous parent if it has one
            ViewGroup parent = (ViewGroup) JApplication.getInstance().viewTempProduct.getParent();
            if (parent != null) {
                parent.removeView(JApplication.getInstance().viewTempProduct);
            }
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_product_list_promo, container, false);
        v = view;
        CreateView();
        InitView();
        EventView();
        LoadData();
        return view;
    }

    private void CreateView() {
        this.RcvProductList = v.findViewById(R.id.rcvLoad);
        this.swipe = v.findViewById(R.id.swipe_refresh_layout);
        this.imgKlasifikasi = v.findViewById(R.id.imgKlasifikasi);
        this.spnUrutkan = v.findViewById(R.id.spnUrutkan);
    }

    private void InitView() {
        ThisActivity = getActivity();
        ModeTampilan = MODE_TAMPILAN_GRID;
        NoOfColumns = calculateWidth.calculateNoOfColumns(ThisActivity);
        SetJenisTampilan();

        ListItemsUrutkan.clear();
        ListItemsUrutkan.add(new UrutkanModel(TEXT_URUTKAN_NAMA, true));
        ListItemsUrutkan.add(new UrutkanModel(TEXT_URUTKAN_TERMURAH, false));
        ListItemsUrutkan.add(new UrutkanModel(TEXT_URUTKAN_TERMAHAL, false));


        this.DialogFilter = new Dialog(ThisActivity);
        this.DialogFilter.setContentView(R.layout.urutkan_product_list);
        this.RcvUrutkan = DialogFilter.findViewById(R.id.RcvUrutkan);

        RcvUrutkan.setLayoutManager(new LinearLayoutManager(ThisActivity));
        mAdapterUrutkan = new UrutkanAdapter(ThisActivity, ListItemsUrutkan, this);
        RcvUrutkan.setAdapter(mAdapterUrutkan);

        JENIS_BARANG= TEXT_SEMUA;
        KLASIFIKASI = TEXT_SEMUA;
        UKURAN      = TEXT_SEMUA;

        OrderBy = "";

        Bundle extra = getArguments();
        if (extra != null) {
            seqPromo = extra.getInt("seq_promo");
            PATH_GAMBAR = extra.getString("path_gambar");
        }
        if (!PATH_GAMBAR.isEmpty()){
            Picasso.get().load(PATH_GAMBAR.replace("%", "%25")).into(imgKlasifikasi);
        }

        // Inisialisasi data untuk Spinner
        List<String> optionsList = new ArrayList<>();
        optionsList.add(TEXT_URUTKAN_NAMA);
        optionsList.add(TEXT_URUTKAN_TERMURAH);
        optionsList.add(TEXT_URUTKAN_TERMAHAL);
        // Buat adapter dan atur data ke Spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ThisActivity, R.layout.custom_spinner_item, optionsList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUrutkan.setAdapter(arrayAdapter);
    }

    protected void EventView() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!IsLoading) {
                    mAdapter.removeAllModel();
                    RefreshRecyclerView();
                }
            }
        });

        setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
//                mAdapter.addMoel(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mAdapter.removeMoel(ListItems.size() - 1);
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

        spnUrutkan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = (String) parentView.getItemAtPosition(position);
                if (selectedOption.equals(TEXT_URUTKAN_NAMA)){
                    OrderBy =  "nama_induk";
                }else if(selectedOption.equals(TEXT_URUTKAN_TERMURAH)){
                    OrderBy =  "harga_akhir%20ASC";
                }else if(selectedOption.equals(TEXT_URUTKAN_TERMAHAL)){
                    OrderBy =  "harga_akhir%20DESC";
                }
                mAdapter.removeAllModel();
                RefreshRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle ketika tidak ada item yang dipilih
            }
        });


        imgKlasifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getActivity(), ZoomImageFullActivity.class);
                s.putExtra("GAMBAR", PATH_GAMBAR);
                s.putExtra("NAMA", "Promo_"+String.valueOf(seqPromo));
                getActivity().startActivity(s);
            }
        });
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

    private void SetJenisTampilan() {
        if (ModeTampilan.equals(MODE_TAMPILAN_GRID)) {
            mAdapter = new ProductListAdapter(ThisActivity, ListItems, this, R.layout.list_item_product_list_grid);
            RcvProductList.setLayoutManager(new GridLayoutManager(ThisActivity, NoOfColumns, GridLayoutManager.VERTICAL, false));
            linearLayoutManager = (LinearLayoutManager) RcvProductList.getLayoutManager();
            RcvProductList.setAdapter(mAdapter);

        } else if (ModeTampilan.equals(MODE_TAMPILAN_LIST)) {
            mAdapter = new ProductListAdapter(ThisActivity, ListItems, this, R.layout.list_item_product_list);
            RcvProductList.setLayoutManager(new LinearLayoutManager(ThisActivity));
            linearLayoutManager = (LinearLayoutManager) RcvProductList.getLayoutManager();
            RcvProductList.setAdapter(mAdapter);

        }
    }

    private void LoadData() {
        String filter = "?offset=" + mAdapter.getItemCount() + "&database_id=" + SharedPrefsUtils.getIntegerPreference(ThisActivity, "database_id", 0)
                +"&sort="+OrderBy
                +"&seq_promo="+ seqPromo
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
                            Toast.makeText(ThisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            swipe.setRefreshing(false);
                        }
                    }
                    //isi
                    mAdapter.addModels(itemDataModels);
                    setLoaded(false);
                    swipe.setRefreshing(false);
                    if (ListItems.size() <= 0){Toast.makeText(ThisActivity, "Tidak ditemukan", Toast.LENGTH_SHORT).show();}

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(ThisActivity, pesan, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
