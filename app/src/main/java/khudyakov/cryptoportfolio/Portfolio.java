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
        this.name = name;
    }

    void addCurrency(Currency currency) {
        currencies.add(currency);
        composition.put(currency.name, currency.currentAmount());
        cost += currency.currentCost();
    }

    TreeMap<String, Float> getComposition() {
        return composition;
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
}
