package khudyakov.cryptoportfolio;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    static List<Portfolio> portfolios;

    @Override
    public void onCreate() {
        super.onCreate();

        portfolios = new ArrayList<>();
        Currency btc = new Currency("BTC");
        btc.addTransaction(new Transaction(1532995200, 1));
        Currency eth = new Currency("ETH");
        eth.addTransaction(new Transaction(1532995200, 1));
        Currency btc2 = new Currency("BTC");
        btc.addTransaction(new Transaction(1532995200, 1));
        Currency eth2 = new Currency("ETH");
        eth.addTransaction(new Transaction(1532995200, 1));
        Currency btc3 = new Currency("BTC");
        btc.addTransaction(new Transaction(1532995200, 1));
        Currency eth3 = new Currency("ETH");
        eth.addTransaction(new Transaction(1532995200, 1));

        Portfolio portfolio1 = new Portfolio("Kotleta1");
        Portfolio portfolio2 = new Portfolio("Kotleta2");
        Portfolio portfolio3 = new Portfolio("Kotleta3");
        portfolio1.addCurrency(btc);
        portfolio1.addCurrency(eth);
        portfolio2.addCurrency(btc);
        portfolio2.addCurrency(eth);
        portfolio3.addCurrency(btc);
        portfolio3.addCurrency(eth);

        portfolios.add(portfolio1);
        portfolios.add(portfolio2);
        portfolios.add(portfolio3);
    }

    static String[] portfoliosNames() {
        int size = portfolios.size();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = portfolios.get(i).name;
        }
        return names;
    }
}
