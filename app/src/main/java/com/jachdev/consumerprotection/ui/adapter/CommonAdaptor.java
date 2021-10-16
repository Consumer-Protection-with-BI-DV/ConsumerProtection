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
import com.jachdev.consumerprotection.data.FirebasePredictionData;
import com.jachdev.consumerprotection.data.Notification;
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
 * Created by Asanka on 7/30/2021.
 */
public class CommonAdaptor<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SHOP = R.layout.item_shop;
    public static final int VIEW_TYPE_SHOP_MAP = R.layout.item_shop_map;
    public static final int VIEW_TYPE_FIREBASE_PREDICTION = R.layout.item_prediction;
    public static final int VIEW_TYPE_NOTIFICATION = R.layout.item_notification;

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
            case VIEW_TYPE_FIREBASE_PREDICTION:
                view = LayoutInflater.from(parent.getContext()).inflate(VIEW_TYPE_FIREBASE_PREDICTION, parent, false);
                return new PredictionVH(view);
            case VIEW_TYPE_NOTIFICATION:
                view = LayoutInflater.from(parent.getContext()).inflate(VIEW_TYPE_NOTIFICATION, parent, false);
                return new NotificationVH(view);
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
            case VIEW_TYPE_FIREBASE_PREDICTION:
                PredictionVH pvh = (PredictionVH) holder;
                pvh.onBind((FirebasePredictionData) t, position);
                break;
            case VIEW_TYPE_NOTIFICATION:
                NotificationVH nvh = (NotificationVH) holder;
                nvh.onBind((Notification) t, position);
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

    public class NotificationVH extends RecyclerView.ViewHolder {

        Context context;
        @BindView(R.id.tvTitle)
        CustomTextView tvTitle;
        @BindView(R.id.tvContent)
        CustomTextView tvContent;
        int position;

        public NotificationVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();
        }

        public void onBind(Notification item, int position) {
            this.position = position;

            tvTitle.setAnyText(item.getTitle());
            tvContent.setAnyText(item.getDescription());
        }
    }

    public class PredictionVH extends RecyclerView.ViewHolder {

        Context context;
        @BindView(R.id.tvTitle)
        CustomTextView tvTitle;
        @BindView(R.id.tvValue)
        CustomTextView tvValue;
        @BindView(R.id.tvMinMax)
        CustomTextView tvMinMax;
        int position;

        public PredictionVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();
        }

        public void onBind(FirebasePredictionData item, int position) {
            this.position = position;

            tvTitle.setAnyText(item.getName());
            tvValue.setCurrency("Rs", item.getRate());
            String min = String.valueOf(item.getMin());
            String max = String.valueOf(item.getMax());
            tvMinMax.setAnyText(context.getString(R.string.min_max_ratio_10_100, min, max));
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

            tvShopName.setAnyText(item.getName() + " - " + item.getDistance());
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
