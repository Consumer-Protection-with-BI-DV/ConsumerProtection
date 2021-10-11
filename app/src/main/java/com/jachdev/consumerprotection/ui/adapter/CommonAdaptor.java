package com.jachdev.consumerprotection.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jachdev.commonlibs.widget.CustomImageView;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AllShopResponse;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.util.Helper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Charitha Ratnayake(charitha@techleadintl.com) on 7/30/2021.
 */
public class CommonAdaptor<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SHOP = R.layout.item_shop;
    public static final int VIEW_TYPE_SHOP_MAP = R.layout.item_shop_map;

    private Context context;
    private CommonAdaptorCallback callback;
    private final List<T> data;
    private int viewType = 0;

    public CommonAdaptor(Context context, List<T> data, CommonAdaptorCallback callback) {
        viewType = VIEW_TYPE_SHOP;
        this.context = context;
        this.data = data;
        this.callback = callback;
    }

    @Override
    public @NotNull RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_SHOP:
                view = LayoutInflater.from(parent.getContext()).inflate(VIEW_TYPE_SHOP, parent, false);
                return new ShopVH(view);
            case VIEW_TYPE_SHOP_MAP:
                view = LayoutInflater.from(parent.getContext()).inflate(VIEW_TYPE_SHOP_MAP, parent, false);
                return new ShopMapVH(view);
            default:
                throw new IllegalArgumentException("Unexpected view type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T t = data.get(position);

        switch (viewType){
            case VIEW_TYPE_SHOP:
                ShopVH vh = (ShopVH) holder;
                vh.onBind((Shop) t, position);
                break;
            case VIEW_TYPE_SHOP_MAP:
                ShopMapVH smvh = (ShopMapVH) holder;
                smvh.onBind((AllShopResponse) t, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void add(T t) {
        data.add(t);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        data.remove(t);
        notifyDataSetChanged();
    }

    public void update(int position, T t) {
        data.add(position, t);
        notifyDataSetChanged();
    }

    public void setItems(List<T> t) {
        if (t == null) return;

        this.data.clear();
        this.data.addAll(t);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return data;
    }

    public void clearData() {
        if(data!=null) {
            data.clear();
            notifyDataSetChanged();
        }
    }

    public class ShopVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvShopName)
        CustomTextView tvShopName;
        @BindView(R.id.tvShopAddress)
        CustomTextView tvShopAddress;
        int position;

        public ShopVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(Shop item, int position) {
            this.position = position;

            tvShopName.setAnyText(item.getName());
            tvShopAddress.setText(item.getAddress());
        }

        @OnClick(R.id.btnCall)
        void onCallClick(){
            Shop shop = (Shop) data.get(position);

            Helper.call(itemView.getContext(), shop.getPhoneNumber());
        }
    }

    public class ShopMapVH extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_logo)
        CustomImageView iv_logo;
        @BindView(R.id.tvShopName)
        CustomTextView tvShopName;
        @BindView(R.id.tvOrganization)
        CustomTextView tvOrganization;
        @BindView(R.id.tvDescription)
        CustomTextView tvDescription;
        @BindView(R.id.tvShopAddress)
        CustomTextView tvShopAddress;
        int position;

        public ShopMapVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(callback != null)
                        callback.onView(VIEW_TYPE_SHOP_MAP, position);
                }
            });
        }

        public void onBind(AllShopResponse item, int position) {
            this.position = position;

            Helper.loadImageByUrl(iv_logo, item.getLogo());

            tvShopName.setAnyText(item.getName());
            tvOrganization.setAnyText(item.getOrgName());
            tvDescription.setAnyText(item.getDescription());
            tvShopAddress.setText(item.getAddress());
        }

        @OnClick(R.id.btnCall)
        void onCallClick(){
            AllShopResponse shop = (AllShopResponse) data.get(position);

            Helper.call(itemView.getContext(), shop.getPhoneNumber());
        }
    }

    public interface CommonAdaptorCallback {

        void onView(int viewType, int position);

    }
}
