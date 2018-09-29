package khudyakov.cryptoportfolio;

import android.util.Log;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.Date;

public class Timestamp {

    public long date;
    public float high;
    public float low;
    public float open;
    public float close;

    public Timestamp(long date, float high, float low, float open, float close) {
        this.date = date;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
    }

    float middlePrice() {
        return (high + low + open + close) / 4;
    }

    CandleEntry toCandleEntry(int index) {
        return new CandleEntry(index, high, low, open, close);
    }
}
