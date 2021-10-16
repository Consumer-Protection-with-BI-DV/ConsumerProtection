package com.jachdev.consumerprotection.ui.prediction;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.utils.DateTimeUtil;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Category;
import com.jachdev.consumerprotection.data.PredictionCategory;
import com.jachdev.consumerprotection.data.PredictionData;
import com.jachdev.consumerprotection.data.PredictionRequest;
import com.jachdev.consumerprotection.data.enums.PredictionType;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.Helper;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PredictionFragment extends BaseFragment {
    public static final String ACTION = "ACTION_PREDICTION";
    private static final String TAG = PredictionFragment.class.getSimpleName();
    private static final String KEY_PREDICTION_TYPE = "KEY_PREDICTION_TYPE";
    private static String[] CATS = null;

    @BindView(R.id.chart)
    LineChart chart;
    @BindView(R.id.pieChart)
    PieChart pieChart;
    @BindView(R.id.et_category_type)
    CustomTextView et_category_type;
    @BindView(R.id.et_sub_category)
    CustomTextView et_sub_category;
    @BindView(R.id.tvXAxisLabel)
    CustomTextView tvXAxisLabel;
    @BindView(R.id.tvYAxisLabel)
    CustomTextView tvYAxisLabel;

    private FragmentEventListener listener;
    private AppService service;
    private PredictionType predictionType;
    private PredictionsData predictionsData;
    private PredictionCategory currentPredictionCategory;
    private PredictionRequest request;

    public static Fragment newInstance(PredictionType type) {
        PredictionFragment fragment = new PredictionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PREDICTION_TYPE, type.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_prediction;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        service = AppApplication.getInstance().getAppService();

        init();

        getEssentials();
    }

    @OnClick(R.id.et_category_type)
    void onClickCategoryType(){

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Choose a category");
        builder.setItems(CATS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_sub_category.setVisibility(View.GONE);
                List<PredictionCategory> categories = predictionsData.getPredictionData(predictionType);

                if(categories != null && !categories.isEmpty()){
                    currentPredictionCategory = categories.get(which);

                    et_category_type.setAnyText(getString(R.string.category_type, currentPredictionCategory.getName()));
                    request.setCategory(currentPredictionCategory.getName());
                    request.setSubCategory("");

                    if(currentPredictionCategory.hasSubCategory()){
                        et_sub_category.setVisibility(View.VISIBLE);
                    }else{

                        getEssentials();
                    }

                    setXYAxis();
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.et_sub_category)
    void onClickSubCategory(){
        List<PredictionCategory.SubCategory> list = currentPredictionCategory.getSubCategory();
        String[] SUBS = new String[list.size()];
        for (int x = 0; x < list.size(); x++){
            SUBS[x] = list.get(x).getName();
        }

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Choose a sub category");
        builder.setItems(SUBS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_sub_category.setAnyText(getString(R.string.sub_category_type, SUBS[which]));
                request.setSubCategory(SUBS[which]);
                getEssentials();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void init() {
        chart.zoom(28f,1f,0f,0f);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        currentPredictionCategory = new PredictionCategory();
        predictionsData = new PredictionsData();
        predictionType = PredictionType.getPredictionType(getArguments().getInt(KEY_PREDICTION_TYPE, 0));
        CATS = predictionsData.getPredictionDataByType(predictionType);

        request = new PredictionRequest();
        et_category_type.setAnyText(getString(R.string.category_type, CATS[0]));
        currentPredictionCategory.setName(CATS[0]);

        if(!predictionsData.getSubs(predictionType, CATS[0]).isEmpty()){

            List<PredictionCategory.SubCategory> subs = predictionsData.getSubs(predictionType, CATS[0]);
            currentPredictionCategory.setSubCategory(subs);
            et_sub_category.setAnyText(getString(R.string.sub_category_type, subs.get(0).getName()));
            et_sub_category.setVisibility(View.VISIBLE);
            request.setSubCategory(subs.get(0).getName());
        }
        request.setCategory(CATS[0]);

        setXYAxis();
    }

    private void setXYAxis() {
        tvXAxisLabel.setAnyText("Time");
        switch (predictionType){
            case IMPORT:
                tvYAxisLabel.setAnyText("Import (Mt)");
                break;
            case PRICE:
                tvYAxisLabel.setAnyText("Price (Rs)");
                break;
            case SALES:
                if(currentPredictionCategory.getName().equalsIgnoreCase(predictionsData.SALES_CATS[2])){
                    tvYAxisLabel.setAnyText("Sales (Ltr)");
                }else{
                    tvYAxisLabel.setAnyText("Sales (Kg)");
                }
                break;
        }
    }

    private void getEssentials() {
        request.setPath(predictionType.name);
        showWaiting();

        service.getServer().getEssentials(request)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        dismissWaiting();
                        switch (response.getCode()){
                            case 0:
                                PredictionData[] data = response.getObjectToType(PredictionData[].class);
                                drawGraph(data);
                                drawPieChart(data);
                                break;
                            default:
                                showError(response.getMessage());
                                chart.clear();
                                pieChart.clear();
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        dismissWaiting();
                        showError(e.getMessage());
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        listener = (FragmentEventListener) context;
        super.onAttach(context);
    }

    private void drawGraph(PredictionData[] data) {
        List<Entry> entries = new ArrayList<Entry>();
        int max = 0;
        int min = 1000000;

        for (int x = 0; x < data.length; x++) {
            PredictionData d = data[x];
            long date = d.getUnix()/1000;
            int value = d.getValue();

            Random r = new Random();
            int valueMin = value - 5;
            int valueMax = value + 5;
            int result = r.nextInt(valueMax-valueMin) + valueMin;

            if(max < result)
                max = result;

            if(min > result)
                min = result;

            if(date > 0 && value > 0){

                Entry entry = new Entry(date, result);
                entries.add(entry);
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(predictionType.getTitle()));
        dataSet.setCubicIntensity(0.5f);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(min-10f);
        yAxis.setAxisMaximum(max+10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new StickyDateAxisValueFormatter());
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend l = chart.getLegend();
        l.setEnabled(true);

        chart.invalidate();
    }

    private void drawPieChart(PredictionData[] data) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "Date";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Jan",getTotalForMonth(data, 0)/30);
        typeAmountMap.put("Feb",getTotalForMonth(data, 1)/30);
        typeAmountMap.put("Mar",getTotalForMonth(data, 2)/30);
        typeAmountMap.put("Apr",getTotalForMonth(data, 3)/30);
        typeAmountMap.put("May",getTotalForMonth(data, 4)/30);
        typeAmountMap.put("Jun",getTotalForMonth(data, 5)/30);
        typeAmountMap.put("Jul",getTotalForMonth(data, 6)/30);
        typeAmountMap.put("Aug",getTotalForMonth(data, 7)/30);
        typeAmountMap.put("Sep",getTotalForMonth(data, 8)/30);
        typeAmountMap.put("Oct",getTotalForMonth(data, 9)/30);
        typeAmountMap.put("Nov",getTotalForMonth(data, 10)/30);
        typeAmountMap.put("Dec",getTotalForMonth(data, 11)/30);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#546e7a"));
        colors.add(Color.parseColor("#424242"));
        colors.add(Color.parseColor("#795548"));
        colors.add(Color.parseColor("#1a237e"));
        colors.add(Color.parseColor("#ff8a65"));
        colors.add(Color.parseColor("#7cb342"));
        colors.add(Color.parseColor("#009688"));
        colors.add(Color.parseColor("#f9a825"));
        colors.add(Color.parseColor("#9575cd"));
        colors.add(Color.parseColor("#e91e63"));
        colors.add(Color.parseColor("#b0bec5"));
        colors.add(Color.parseColor("#263238"));

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);


        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private Integer getTotalForMonth(PredictionData[] data, int i) {
        int total = 0;

        for(int x = 0; x < data.length; x++){
            PredictionData pd = data[x];
            if(pd.getMonth() == i){
                total = total + pd.getValue();
            }
        }

        return total;
    }

    private void showError(String message){

        ChocoBar.builder().setBackgroundColor(Color.parseColor("#FFA61B1B"))
                .setTextSize(18)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(message)
                .setMaxLines(4)
                .centerText()
                .setActionText(getString(R.string.ok))
                .setActivity(getBaseActivity())
                .build()
                .show();
    }

    public interface FragmentEventListener{
    }

    public class StickyDateAxisValueFormatter extends IndexAxisValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            // Convert float value to date string
            // Convert from days back to milliseconds to format time  to show to the user
            long emissionsMilliSince1970Time = (long) value*1000;
            // Show time in local version
            Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd");

            return dateTimeFormat.format(timeMilliseconds);
        }
    }

    public class PredictionsData{

        private final String[] CATS = new String[]{"Rice", "Milk", "Dhal"};
        private final String[] SALES_CATS = new String[]{"Sugar", "Rice", "CoconutOil", "Dhal", "MilkPowder"};
        private final String[] SALES_SUB_CATS = new String[]{"Samba", "Nadu"};
        private final String[] PRICE_CATS = new String[]{"Sugar", "Coconut"};
        private final String[] PRICE_CATS_SUBS = new String[]{"WholeSale_Pettah", "Retail_Pettah", "WholeSale_Dambulla", "Retail_Dambulla"};

        private List<PredictionCategory> imports = new ArrayList<>();
        private List<PredictionCategory> sales = new ArrayList<>();
        private List<PredictionCategory> prices = new ArrayList<>();

        public List<PredictionCategory> getImportPredictions(){
            imports.add(new PredictionCategory(CATS[0], null));
            imports.add(new PredictionCategory(CATS[1], null));
            imports.add(new PredictionCategory(CATS[2], null));

            return imports;
        }

        public List<PredictionCategory> getSalesPredictions(){
            sales.add(new PredictionCategory(SALES_CATS[0], null));
            sales.add(new PredictionCategory(SALES_CATS[1], getSubs(PredictionType.SALES, SALES_CATS[0])));
            sales.add(new PredictionCategory(SALES_CATS[2], null));
            sales.add(new PredictionCategory(SALES_CATS[3], null));
            sales.add(new PredictionCategory(SALES_CATS[4], null));

            return sales;
        }

        public List<PredictionCategory> getPricePredictions(){
            prices.add(new PredictionCategory(PRICE_CATS[0], getSubs(PredictionType.PRICE, PRICE_CATS[0])));
            prices.add(new PredictionCategory(PRICE_CATS[1], getSubs(PredictionType.PRICE, PRICE_CATS[1])));

            return prices;
        }

        private List<PredictionCategory.SubCategory> getSubs(PredictionType type, String cat) {
            List<PredictionCategory.SubCategory> subs = new ArrayList<>();

            if(type == PredictionType.SALES){
                if(cat.equalsIgnoreCase(SALES_CATS[0])){

                    subs.add(new PredictionCategory.SubCategory(SALES_SUB_CATS[0]));
                    subs.add(new PredictionCategory.SubCategory(SALES_SUB_CATS[1]));

                }
            }else if(type == PredictionType.PRICE){
                if(cat.equalsIgnoreCase(PRICE_CATS[0]) || cat.equalsIgnoreCase(PRICE_CATS[1])){

                    subs.add(new PredictionCategory.SubCategory(PRICE_CATS_SUBS[0]));
                    subs.add(new PredictionCategory.SubCategory(PRICE_CATS_SUBS[1]));
                    subs.add(new PredictionCategory.SubCategory(PRICE_CATS_SUBS[2]));
                    subs.add(new PredictionCategory.SubCategory(PRICE_CATS_SUBS[3]));
                }
            }

            return subs;
        }

        public List<PredictionCategory> getPredictionData(PredictionType type) {
            List<PredictionCategory> categories = new ArrayList<>();
            switch (type){
                case IMPORT:
                    categories = getImportPredictions();
                    break;
                case PRICE:
                    categories = getPricePredictions();
                    break;
                case SALES:
                    categories = getSalesPredictions();
                    break;
            }

            return categories;
        }

        public String[] getPredictionDataByType(PredictionType type) {
            String[] cats;
            List<PredictionCategory> categories = null;
            switch (type){
                case IMPORT:
                    categories = getImportPredictions();
                    break;
                case PRICE:
                    categories = getPricePredictions();
                    break;
                case SALES:
                    categories = getSalesPredictions();
                    break;
            }

            if(categories == null || categories.isEmpty())
                return new String[0];

            cats = new String[categories.size()];
            for (int x = 0; x < categories.size(); x++) {
                cats[x] = categories.get(x).getName();
            }

            return cats;
        }
    }
}