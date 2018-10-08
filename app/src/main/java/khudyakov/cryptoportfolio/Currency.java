package khudyakov.cryptoportfolio;

import android.util.Pair;

import com.github.mikephil.charting.data.CandleEntry;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Currency implements Serializable {

    String name;
    ArrayList<Transaction> transactions;
    TreeMap<Long, Float> prices;
    TreeMap<Long, Float> values;
    ArrayList<Timestamp> quotations;
    long startTime;
    long endTime;

    public Currency(String name, Info info) {
        this.name = name;
        transactions = new ArrayList<>();
        prices = info.getPrices(name);
        quotations = info.quotations.get(name);

        values = new TreeMap<>();
        for (Long time: prices.keySet()) {
            values.put(time, 0F);
        }
        startTime = prices.firstKey();
        endTime = prices.lastKey();
    }

    void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        for (Map.Entry<Long, Float> entry: values.entrySet()) {
            if (entry.getKey() >= transaction.date) {
                entry.setValue(entry.getValue() + transaction.amount);
            }
        }
    }

    ArrayList<Timestamp> getEntries() {
        ArrayList<Timestamp> entries = new ArrayList<>();
        for (Timestamp t: quotations) {
            float amount = values.get(t.date);
            float high = t.high * amount;
            float low = t.low * amount;
            float open = t.open * amount;
            float close = t.open * amount;
            entries.add(new Timestamp(t.date, high, low, open, close));
        }
        return entries;
    }

    Timestamp getEntry(long date) {
        Timestamp quotation = find(date);
        float amount = values.get(date);
        float high = quotation.high * amount;
        float low = quotation.low * amount;
        float open = quotation.open * amount;
        float close = quotation.close * amount;
        return new Timestamp(date, high, low, open, close);
    }

    Timestamp find(long date) {
        Timestamp result = null;
        for (Timestamp timestamp: quotations) {
            if (timestamp.date == date)
                result = timestamp;
        }
        return result;
    }

    float currentAmount() {
        float amount = 0;
        for (Transaction transaction : transactions) {
            amount += transaction.amount;
        }
        return amount;
    }

    float currentCost() {
        return currentAmount() * App.info.getPrices(name).lastEntry().getValue();
    }

    Pair<Float, Float> boughtAndSold() {
        float bought = 0;
        float sold = 0;

        for (Transaction transaction : transactions) {
            float amount = transaction.amount;
            float cost = amount * App.info.getPrices(name).get(transaction.date);

            if (amount > 0)
                bought += cost;
            else
                sold += cost;
        }

        return new Pair<>(bought, sold);
    }

    float profit() {
        Pair<Float, Float> summary = boughtAndSold();

        float bought = summary.first;
        float sold = summary.second;

        return (sold + currentCost()) / bought - 1;
    }

    String[] getTransactions() {
        int size = transactions.size();
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = transactions.get(i).toString();
        }
        return array;
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
