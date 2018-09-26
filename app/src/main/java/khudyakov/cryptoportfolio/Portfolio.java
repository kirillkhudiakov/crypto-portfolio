package khudyakov.cryptoportfolio;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

public class Portfolio implements Serializable {

    String name;
    ArrayList<Currency> currencies;
    TreeMap<String, Float> composition;
    float cost;

    public Portfolio(String name) {
        currencies = new ArrayList<>();
        composition = new TreeMap<>();
        this.name = name;
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
}
