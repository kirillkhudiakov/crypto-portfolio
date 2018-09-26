package khudyakov.cryptoportfolio;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.TreeMap;

public class Currency implements Serializable {

    String name;
    ArrayList<Transaction> transactions;
    TreeMap<Long, Float> quotations;

    public Currency(String name) {
        this.name = name;
        transactions = new ArrayList<>();
        quotations = Info.getQuotations();
    }

    void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    float currentAmount() {
        float amount = 0;
        for (Transaction transaction: transactions) {
            amount += transaction.amount;
        }
        return amount;
    }

    float currentCost() {
        return currentAmount() * quotations.lastEntry().getValue();
    }

    Pair<Float, Float> boughtAndSold() {
        float bought = 0;
        float sold = 0;

        for (Transaction transaction: transactions) {
            float amount = transaction.amount;
            float cost = amount * quotations.get(transaction.date);

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
}
