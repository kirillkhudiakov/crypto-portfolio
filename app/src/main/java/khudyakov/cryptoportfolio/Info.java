package khudyakov.cryptoportfolio;

import android.content.Context;
import android.util.Log;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Info {

    ArrayList<Timestamp> values;
    Context currentContext;
    CandleStickChart chart;

    public Info(Context context, CandleStickChart chart) {
        currentContext = context;
        this.chart = chart;
    }

    void getInfo() {
        RequestQueue queue = Volley.newRequestQueue(currentContext);
        String url = "https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=9";

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("Data");
                            fillArray(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(request);
    }

    void fillArray(JSONArray data) {
        values = new ArrayList<>(data.length());

        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject json = data.getJSONObject(i);

                int time = json.getInt("time");
                float high = (float) json.getDouble("high");
                float low = (float) json.getDouble("low");
                float open = (float) json.getDouble("open");
                float close = (float) json.getDouble("close");

                values.add(new Timestamp(time, high, low, open, close));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getData();
    }

    void getData() {
        ArrayList<CandleEntry> entries = new ArrayList<>(values.size());
        ArrayList<String> labels = new ArrayList<>(values.size());

        for (int i = 0; i < values.size(); i++) {
            entries.add(new CandleEntry(i, values.get(i).high, values.get(i).low,
                    values.get(i).open, values.get(i).close));
            labels.add(Integer.toString(i));
        }

        CandleDataSet dataSet = new CandleDataSet(entries, "BTC price");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        CandleData data = new CandleData(dataSet);
        chart.setData(data);
    }
}
