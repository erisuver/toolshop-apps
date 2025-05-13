package com.orion.sinar_surya.activities.keranjang;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.pesanan.PesananActivity;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.models.KeranjangModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KeranjangAdapter extends RecyclerView.Adapter {
    Context context;
    List<KeranjangModel> Datas;
    KeranjangActivity keranjangActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private boolean isAllChecked = false;
    private int totalItemBeliTemp = 0;
    private double totalPesananTemp = 0;
    private boolean isHitungAwal = true;

    private Runnable runActiveButton;
    private Runnable runNon = new Runnable() {
        @Override
        public void run() {

        }
    };

    public KeranjangAdapter(KeranjangActivity keranjangActivity, List<KeranjangModel> keranjangModels, int view) {
        this.context = keranjangActivity.getApplicationContext();
        this.keranjangActivity = keranjangActivity;
        this.Datas = keranjangModels;
        this.view = view;
        runActiveButton = new Runnable() {
            @Override
            public void run() {
                KeranjangAdapter.this.keranjangActivity.btnBeli.setEnabled(true);
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new KeranjangAdapter.ItemHolder(row);
        } else if (viewType == VIEW_TYVE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new KeranjangAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder) {
            final KeranjangModel mCurrentItem = Datas.get(position);
            final KeranjangAdapter.ItemHolder itemHolder = (KeranjangAdapter.ItemHolder) holder;
            String path = "";
            if (!mCurrentItem.getGambar().equals("")) {
                path = Routes.url_path_gambar_barang() + "/" + mCurrentItem.getGambar();
                Picasso.get().load(path.replace("%", "%25")).into(itemHolder.imgBarang);
                if (itemHolder.imgBarang.getDrawable() == null) {
                    itemHolder.imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
                }
            }

            itemHolder.txtNama.setText(mCurrentItem.getNama_barang());
            itemHolder.txtDiskon.setText(String.format("%s%%", Global.FloatToStrFmt(mCurrentItem.getDiskon_pct())));
            itemHolder.txtHargaAwal.setText(Global.FloatToStrFmt(mCurrentItem.getHarga_awal(), true));
            itemHolder.txtHargaAkhir.setText(Global.FloatToStrFmt(mCurrentItem.getHarga_akhir(), true));
            itemHolder.txtSatuan.setText(mCurrentItem.getNama_satuan());

            if (mCurrentItem.getDiskon_pct() > 0) {
                itemHolder.txtHargaAwal.setVisibility(View.VISIBLE);
                itemHolder.txtDiskon.setVisibility(View.VISIBLE);
                itemHolder.txtHargaAwal.setPaintFlags(itemHolder.txtHargaAwal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                itemHolder.txtHargaAwal.setVisibility(View.GONE);
                itemHolder.txtDiskon.setVisibility(View.GONE);
            }

            itemHolder.txtQty.setText(String.valueOf(mCurrentItem.getQty()));
            itemHolder.chbPilih.setChecked(mCurrentItem.getIs_pilih().equals(JConst.TRUE_STRING));

//            if (position == Datas.size()-1){
//                updateChbSemuaStatus(); //jika posisi terakhir cek apakah item ter checklist semua? jika iya maka chbsemua di true kan
//            }

            // Ganti setOnCheckedChangeListener dengan setOnClickListener
            itemHolder.chbPilih.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isChecked = ((CheckBox) view).isChecked();
                    //update checked pada model
                    mCurrentItem.setIs_pilih(isChecked ? JConst.TRUE_STRING : JConst.FALSE_STRING);

                    // Update seqBarangList based on the isChecked status
                    updateSeqBarangList(isChecked, String.valueOf(mCurrentItem.getSeq_barang()));

                    if (isChecked) {
                        totalItemBeliTemp += mCurrentItem.getQty();
                        totalPesananTemp += (mCurrentItem.getHarga_akhir() * mCurrentItem.getQty());

                        boolean allChecked = true;
                        for (KeranjangModel item : Datas) {
                            if (!item.getIs_pilih().equals(JConst.TRUE_STRING)) {
                                allChecked = false;
                                break;
                            }
                        }
                        keranjangActivity.btnBeli.setEnabled(false);
                        keranjangActivity.chbSemua.setChecked(allChecked); //jika semua terceklist di item maka chbsemua di unceklist
                        keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
                        keranjangActivity.setTotalPesanan(totalPesananTemp);
                        keranjangActivity.updateIsPilih(JConst.TRUE_STRING, mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan(), runActiveButton);
                    } else {
                        keranjangActivity.btnBeli.setEnabled(false);
                        totalItemBeliTemp -= mCurrentItem.getQty();
                        totalPesananTemp -= (mCurrentItem.getHarga_akhir() * mCurrentItem.getQty());

                        keranjangActivity.chbSemua.setChecked(false); //jika ada triger unceklist di item maka chbsemua di unceklist
                        keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
                        keranjangActivity.setTotalPesanan(totalPesananTemp);
                        keranjangActivity.updateIsPilih(JConst.FALSE_STRING, mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan(), runActiveButton);
                    }
                }
            });

            itemHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keranjangActivity.deleteKeranjang(mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());
                    keranjangActivity.LoadData();
                }
            });

            itemHolder.txtMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyTemp = mCurrentItem.getQty() - 1;
                    mCurrentItem.setQty(qtyTemp);
                    itemHolder.txtQty.setText(String.valueOf(mCurrentItem.getQty()));

                    if(itemHolder.chbPilih.isChecked()) {
                        totalItemBeliTemp -= 1;
                        totalPesananTemp -= mCurrentItem.getHarga_akhir();
                        keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
                        keranjangActivity.setTotalPesanan(totalPesananTemp);
                    }
                    if (qtyTemp <= 0) {
                        keranjangActivity.deleteKeranjang(mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());
                        keranjangActivity.LoadData();
                    }else{
                        keranjangActivity.updateQtyKeranjang(qtyTemp, mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());
                    }
                }
            });

            itemHolder.txtPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyTemp = mCurrentItem.getQty() + 1;
                    mCurrentItem.setQty(qtyTemp);
                    itemHolder.txtQty.setText(String.valueOf(mCurrentItem.getQty()));

                    if(itemHolder.chbPilih.isChecked()) {
                        totalItemBeliTemp += 1;
                        totalPesananTemp += mCurrentItem.getHarga_akhir();
                        keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
                        keranjangActivity.setTotalPesanan(totalPesananTemp);
                    }
                    keranjangActivity.updateQtyKeranjang(qtyTemp, mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());
                }
            });

            itemHolder.txtQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if (!focus){
                        /**
                         * PENTING !!!
                         * Jika ada perubahan maka ubah juga di setOnFocusChangeListener atau setOnEditorActionListener
                         * */
                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(itemHolder.txtQty.getText().toString());

                        if (qtyBaru == 0){
                            keranjangActivity.deleteKeranjang(mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());
                            keranjangActivity.LoadData();
                            return;
                        }

                        // Hitung perbedaan qty baru dengan qty sebelumnya
                        int perbedaanQty = qtyBaru - mCurrentItem.getQty();

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);

                        // Periksa apakah item dipilih di keranjang (diperiksa dengan chbPilih.isChecked())
                        if (itemHolder.chbPilih.isChecked()) {
                            // Hitung total item beli
                            totalItemBeliTemp += perbedaanQty;

                            // Hitung total pesanan
                            totalPesananTemp += (perbedaanQty * mCurrentItem.getHarga_akhir());
                            keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
                            keranjangActivity.setTotalPesanan(totalPesananTemp);
                        }
                        keranjangActivity.updateQtyKeranjang(qtyBaru, mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());

                    }
                }
            });

            itemHolder.txtQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                            (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        /**
                         * PENTING !!!
                         * Jika ada perubahan maka ubah juga di setOnFocusChangeListener atau setOnEditorActionListener
                         * */
                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(textView.getText().toString());

                        if (qtyBaru == 0){
                            keranjangActivity.deleteKeranjang(mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());
                            keranjangActivity.LoadData();
                            return true;
                        }

                        // Hitung perbedaan qty baru dengan qty sebelumnya
                        int perbedaanQty = qtyBaru - mCurrentItem.getQty();

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);

                        // Periksa apakah item dipilih di keranjang (diperiksa dengan chbPilih.isChecked())
                        if (itemHolder.chbPilih.isChecked()) {
                            // Hitung total item beli
                            totalItemBeliTemp += perbedaanQty;

                            // Hitung total pesanan
                            totalPesananTemp += (perbedaanQty * mCurrentItem.getHarga_akhir());
                            keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
                            keranjangActivity.setTotalPesanan(totalPesananTemp);
                        }
                        keranjangActivity.updateQtyKeranjang(qtyBaru, mCurrentItem.getSeq_barang(), mCurrentItem.getSeq_satuan());

                        // Sembunyikan keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        // Hapus fokus dari EditText
                        textView.clearFocus();
                        return true;
                    }
                    return false;
                }
            });

        } else if (holder instanceof KeranjangAdapter.LoadingViewHolder) {
            KeranjangAdapter.LoadingViewHolder loadingViewHolder = (KeranjangAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        TextView txtNama, txtHargaAwal, txtHargaAkhir, txtDiskon, txtSatuan, txtMinus, txtPlus;
        EditText txtQty;
        ImageButton btnDelete;
        CheckBox chbPilih;

        public ItemHolder(View itemView) {
            super(itemView);
            imgBarang = itemView.findViewById(R.id.imgBarang);
            txtNama = itemView.findViewById(R.id.txtNamaBarang);
            txtHargaAwal = itemView.findViewById(R.id.txtHargaAwal);
            txtHargaAkhir = itemView.findViewById(R.id.txtHargaAkhir);
            txtDiskon = itemView.findViewById(R.id.txtDiskon);
            txtSatuan = itemView.findViewById(R.id.txtSatuan);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtMinus = itemView.findViewById(R.id.txtMinus);
            txtPlus = itemView.findViewById(R.id.txtPlus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            chbPilih = itemView.findViewById(R.id.chbPilih);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<KeranjangModel> KeranjangModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(KeranjangModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(KeranjangModel KeranjangModel) {
        this.Datas.add(KeranjangModel);
        notifyItemRangeInserted(Datas.size() - 1, Datas.size() - 1);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0) {
            this.Datas.remove(Datas.size() - 1);
            notifyItemRemoved(Datas.size());
        }
    }

    private void updateSeqBarangList(boolean isChecked, String seq) {
        // Get the current seqBarangList value
        String currentSeqBarangList = keranjangActivity.getSeqBarangList();

        // If isChecked is true, add the seq to seqBarangList
        if (isChecked) {
            if (!currentSeqBarangList.isEmpty()) {
                currentSeqBarangList += ",";
            }
            currentSeqBarangList += seq;
        } else {
            // If isChecked is false, remove the seq from seqBarangList
            String[] seqArray = currentSeqBarangList.split(",");
            StringBuilder updatedSeqBarangList = new StringBuilder();

            for (String item : seqArray) {
                if (!item.equals(seq)) {
                    if (updatedSeqBarangList.length() > 0) {
                        updatedSeqBarangList.append(",");
                    }
                    updatedSeqBarangList.append(item);
                }
            }

            currentSeqBarangList = updatedSeqBarangList.toString();
        }

        // Set the updated seqBarangList value back to KeranjangActivity
        keranjangActivity.setSeqBarangList(currentSeqBarangList);
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }

    public void setUncheckedAll() {
        totalItemBeliTemp = 0;
        totalPesananTemp = 0;
        keranjangActivity.btnBeli.setEnabled(false);
        for (int i = 0; i < Datas.size(); i++) {
            Datas.get(i).setIs_pilih(JConst.FALSE_STRING);
            if (i == Datas.size()-1) {
                keranjangActivity.updateIsPilih(JConst.FALSE_STRING, Datas.get(i).getSeq_barang(), Datas.get(i).getSeq_satuan(), runActiveButton);
            }else{
                keranjangActivity.updateIsPilih(JConst.FALSE_STRING, Datas.get(i).getSeq_barang(), Datas.get(i).getSeq_satuan(), runNon);
            }
        }
        notifyDataSetChanged();
        isAllChecked = false;
        keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
        keranjangActivity.setTotalPesanan(totalPesananTemp);
    }

    public void setCheckedAll() {
        totalItemBeliTemp = 0;
        totalPesananTemp = 0;
        keranjangActivity.btnBeli.setEnabled(false);
        for (int i = 0; i < Datas.size(); i++) {
            Datas.get(i).setIs_pilih(JConst.TRUE_STRING);
            totalItemBeliTemp += Datas.get(i).getQty();
            totalPesananTemp += (Datas.get(i).getHarga_akhir() * Datas.get(i).getQty());
            if (i == Datas.size()-1) {
                keranjangActivity.updateIsPilih(JConst.TRUE_STRING, Datas.get(i).getSeq_barang(), Datas.get(i).getSeq_satuan(), runActiveButton);
            }else{
                keranjangActivity.updateIsPilih(JConst.TRUE_STRING, Datas.get(i).getSeq_barang(), Datas.get(i).getSeq_satuan(), runNon);
            }
        }
        notifyDataSetChanged();
        isAllChecked = true;
        keranjangActivity.setTotalItemBeli(totalItemBeliTemp);
        keranjangActivity.setTotalPesanan(totalPesananTemp);
    }

    public boolean isAllChecked() {
        return isAllChecked;
    }

    public int getTotalItemBeliTemp() {
        return totalItemBeliTemp;
    }

    public void setTotalItemBeliTemp(int totalItemBeliTemp) {
        this.totalItemBeliTemp = totalItemBeliTemp;
    }

    public double getTotalPesananTemp() {
        return totalPesananTemp;
    }

    public void setTotalPesananTemp(double totalPesananTemp) {
        this.totalPesananTemp = totalPesananTemp;
    }

    private void hitungUlangChangeQtyEditText(){

    }
}
