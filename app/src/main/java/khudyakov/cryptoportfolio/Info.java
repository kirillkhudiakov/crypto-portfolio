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

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Info {

    HashMap<String, ArrayList<Timestamp>> quotations;
    Context currentContext;
    String[] cryptoTickers;
    int count;

    public Info(Context context) {
        currentContext = context;
        quotations = new HashMap<>();
        cryptoTickers = context.getResources().getStringArray(R.array.crypto_tickers);
    }

    void setZeroPrices(String ticker) {
        ArrayList<Timestamp> values = new ArrayList<>(2001);
        for (long date = 1365120000; date <= 1537920000; date += 86400) {
            values.add(new Timestamp(date, 0, 0, 0, 0));
        }
        quotations.put(ticker, values);
    }

    void updateQuotations(String ticker) {
        RequestQueue queue = Volley.newRequestQueue(currentContext);
        getInfo(ticker, queue);
    }

    void getInfo(final String ticker, RequestQueue queue) {
        String url = "https://min-api.cryptocompare.com/data/histoday?fsym=" + ticker
                + "&tsym=USD&limit=2000";

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("Data");
                            fillArray(ticker, data);
                        } catch (Exception e) {
                            Log.d("Kirill", "ERROR");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Kirill", "ERROR");
                    }
                });

        queue.add(request);
    }

    void fillArray(String ticker, JSONArray data) {
        ArrayList<Timestamp> values = new ArrayList<>(data.length());

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

        quotations.put(ticker, values);
        count++;
    }

    TreeMap<Long, Float> getPrices(String ticker) {
        TreeMap<Long, Float> prices = new TreeMap<>();
        boolean needToUpdate = false;
        if (!quotations.containsKey(ticker)) {
            needToUpdate = true;
            setZeroPrices(ticker);
        }
        ArrayList<Timestamp> timestamps = quotations.get(ticker);

        for (Timestamp timestamp: timestamps) {
            prices.put(timestamp.date, timestamp.middlePrice());
        }

        if (needToUpdate)
            updateQuotations(ticker);

        return prices;
    }
}
