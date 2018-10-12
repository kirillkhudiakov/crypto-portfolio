package khudyakov.cryptoportfolio;

import android.util.Pair;

import com.github.mikephil.charting.data.CandleEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class Portfolio implements Serializable {

    String name;
    ArrayList<Currency> currencies;
    TreeMap<String, Float> composition;
    ArrayList<Timestamp> quotations;

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
    }

    float getCost() {
        float cost = 0;
        for (Currency currency: currencies) {
            cost += currency.currentCost();
        }
        return cost;
    }

    float getProfit(Period period) {
        float bought = 0;
        float sold = 0;

        for (Currency currency: currencies) {
            Pair<Float, Float> sum = currency.boughtAndSold(period);
            bought += sum.first;
            sold += sum.second;
        }

        return (sold + getCost()) / bought - 1;
    }

    String[] getComposition() {
        String[] composition = new String[currencies.size()];
        for (int i = 0; i < composition.length; i++) {
            String weight = String.format("%.0f%%", currencies.get(i).currentCost() * 100 / getCost());
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

    ArrayList<CandleEntry> getEntries(Period period) {
        ArrayList<CandleEntry> entries = null;
        switch (period) {
            case MONTH:
                entries = monthEntries();
                break;
            case HALF_YEAR:
                entries = halfYearEntries();
                break;
            case YEAR:
                entries = yearEntries();
                break;
            case ALL:
                entries = allTimeEntries();
                break;
        }
        return entries;
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

    ArrayList<CandleEntry> halfYearEntries() {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        int end = quotations.size();
        int begin = end - 180 > 0 ? end - 180 : 0;
        for (int i = 0; begin < end; begin += 6, i++) {
            entries.add(rangedEntry(begin, begin + 6, i));
        }

        return entries;
    }

    ArrayList<CandleEntry> yearEntries() {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        int end = quotations.size();
        int begin = end - 360 > 0 ? end - 360 : 0;
        for (int i = 0; begin < end; begin += 12, i++) {
            entries.add(rangedEntry(begin, begin + 12, i));
        }

        return entries;
    }

    ArrayList<CandleEntry> allTimeEntries() {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        int end = quotations.size();
        int step = 1;

        while (30 * step <= end) {
            step++;
        }
        step--;

        // TODO: Предусмотреть случай, когда end < 16.
        int begin = end - 30 * step;

        for (int i = 0; begin < end; begin += step, i++) {
            entries.add(rangedEntry(begin, begin + step, i));
        }

        return entries;
    }

    CandleEntry rangedEntry(int start, int end, int x) {
        float open = quotations.get(start).open;
        float close = quotations.get(end - 1).close;

        Timestamp highestStamp = Collections.max(quotations.subList(start, end),
                new Comparator<Timestamp>() {
                    @Override
                    public int compare(Timestamp o1, Timestamp o2) {
                        return (int) (o1.high - o2.high);
                    }
                });

        Timestamp lowestStamp = Collections.min(quotations.subList(start, end),
                new Comparator<Timestamp>() {
                    @Override
                    public int compare(Timestamp o1, Timestamp o2) {
                        return (int) (o1.low - o2.low);
                    }
                });

        float high = highestStamp.high;
        float low = lowestStamp.low;

        return new CandleEntry(x, high, low, open, close);
    }
}
