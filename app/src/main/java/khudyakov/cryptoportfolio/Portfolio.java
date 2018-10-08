package khudyakov.cryptoportfolio;

import android.util.Pair;

import com.github.mikephil.charting.data.CandleEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

public class Portfolio implements Serializable {

    String name;
    ArrayList<Currency> currencies;
    TreeMap<String, Float> composition;
    ArrayList<Timestamp> quotations;
    float cost;

    public Portfolio(String name) {
        currencies = new ArrayList<>();
        composition = new TreeMap<>();
        this.name = name;
    }

    public boolean isEmpty() {
        return currencies.isEmpty();
    }

    void addCurrency(Currency currency) {
        currencies.add(currency);
        composition.put(currency.name, currency.currentAmount());
        cost += currency.currentCost();
    }

    float getCost() {
        return cost;
    }

    float getProfit() {
        float bought = 0;
        float sold = 0;

        for (Currency currency: currencies) {
            Pair<Float, Float> sum = currency.boughtAndSold();
            bought += sum.first;
            sold += sum.second;
        }

        return (sold + cost) / bought - 1;
    }

    String[] getComposition() {
        String[] composition = new String[currencies.size()];
        for (int i = 0; i < composition.length; i++) {
            String weight = Float.toString(currencies.get(i).currentCost() / cost);
            composition[i] = currencies.get(i).name + "\t" + weight;
        }
        return composition;
    }

    Currency getCurrency(int index) {
        return currencies.get(index);
    }

    void getTimeStamps() {
        long startTime = currencies.get(0).startTime;
        long endTime = currencies.get(0).endTime;
        ArrayList<Timestamp> result = new ArrayList<>();

        for (long date = startTime; date <= endTime; date += 86400) {
            float high = 0, low = 0, open = 0, close = 0;
            for (Currency currency: currencies) {
                Timestamp timestamp = currency.getEntry(date);
                high += timestamp.high;
                low += timestamp.low;
                open += timestamp.open;
                close += timestamp.close;
            }
            result.add(new Timestamp(date, high, low, open, close));
        }
        quotations = result;
    }

    ArrayList<CandleEntry> monthEntries() {
        getTimeStamps();

        ArrayList<CandleEntry> entries = new ArrayList<>();
        int end = quotations.size() - 1;
        int begin = end - 30 >= 0 ? end - 30 : 0;
        for (int i = 0; begin <= end; begin += 1, i++) {
            entries.add(quotations.get(begin).toCandleEntry(i));
        }

        return entries;
    }
}
