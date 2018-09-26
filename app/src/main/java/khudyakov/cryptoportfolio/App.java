package khudyakov.cryptoportfolio;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    static List<Portfolio> portfolios;
    static Info info;

    @Override
    public void onCreate() {
        super.onCreate();

        info = new Info(this);

        portfolios = new ArrayList<>();
        Currency btc = new Currency("BTC", info);
        btc.addTransaction(new Transaction(1532995200, 1));
        Currency eth = new Currency("ETH", info);
        eth.addTransaction(new Transaction(1532995200, 1));
        eth.addTransaction(new Transaction(1532995200, 1));

        Portfolio portfolio1 = new Portfolio("Kotleta1");
        portfolio1.addCurrency(btc);
        portfolio1.addCurrency(eth);

        portfolios.add(portfolio1);
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
