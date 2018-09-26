package khudyakov.cryptoportfolio;

import android.util.Log;

import java.util.Date;

public class Timestamp {

    public Date date;
    public float high;
    public float low;
    public float open;
    public float close;

    public Timestamp(int date, float high, float low, float open, float close) {
        this.date = new Date(date);
        Log.d("KIRILL", Integer.toString(date));
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
    }
}
