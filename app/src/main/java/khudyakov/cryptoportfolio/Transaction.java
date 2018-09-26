package khudyakov.cryptoportfolio;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {

    long date;
    float amount;

    public Transaction(long date, float amount) {
        this.date = date;
        this.amount = amount;
    }
}
