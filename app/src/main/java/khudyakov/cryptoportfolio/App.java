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

        portfolios.add(new Portfolio("Kotleta1"));
        Currency btc = new Currency("BTC");
        btc.addTransaction(new Transaction(1532995200, 1));
        Currency eth = new Currency("ETH");
        eth.addTransaction(new Transaction(1532995200, 1));

        portfolios.add(new Portfolio("Kotleta2"));
        Currency btc2 = new Currency("BTC");
        btc.addTransaction(new Transaction(1532995200, 1));
        Currency eth2 = new Currency("ETH");
        eth.addTransaction(new Transaction(1532995200, 1));

        portfolios.add(new Portfolio("Kotleta3"));
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
