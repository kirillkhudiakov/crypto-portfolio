package khudyakov.cryptoportfolio;

import com.github.mikephil.charting.data.CandleEntry;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {

    long date;
    float amount;

    public Transaction(long date, float amount) {
        this.date = date;
        this.amount = amount;
    }

    @Override
    public String toString() {
        float a;
        String type;
        if (amount > 0) {
            type = "bought";
            a = amount;
        }
        else {
            type = "sold";
            a = -amount;
        }
        return Float.toString(a) + " " + type + " on " + Long.toString(date);
    }
}
