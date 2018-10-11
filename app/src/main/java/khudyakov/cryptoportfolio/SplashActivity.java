package khudyakov.cryptoportfolio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    if (!App.info.ready()) {
                        App.info.updateQuotations();
                        while (!App.info.ready()) {
                            sleep(1000);
                        }
                        writeData();
                    }
                    configurePortfolios();
                    savePortfolios();
                } catch (Exception e) {
                    Log.d("Kirill", e.getMessage());
                } finally {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            void writeData() {
                FileOutputStream fos;
                ObjectOutputStream oos;
                try {
                    fos = SplashActivity.this.openFileOutput("Quotations", Context.MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(App.info);
                    oos.close();
                    fos.close();
                    Log.d("Quotes", "Data is written");
                } catch (Exception e) {
                    Log.d("Quotes", e.toString());
                }
            }

            void configurePortfolios() {
                FileInputStream fis;
                ObjectInputStream ois;
                try {
                    fis = SplashActivity.this.openFileInput("Portfolios");
                    ois = new ObjectInputStream(fis);
                    MainActivity.portfolios = (ArrayList<Portfolio>) ois.readObject();
                    Log.d("KIRILL", "READ FROM CACHE");
                } catch (Exception e) {
                    MainActivity.portfolios = new ArrayList<>();
                    Currency btc = new Currency("BTC", App.info);
                    btc.addTransaction(new Transaction(1532995200, 1));
                    Currency eth = new Currency("ETH", App.info);
                    eth.addTransaction(new Transaction(1532995200, 1));
                    Currency xrp = new Currency("XRP", App.info);
                    Currency bch = new Currency("BCH", App.info);
                    xrp.addTransaction(new Transaction(1532995200, 5));
                    bch.addTransaction(new Transaction(1532995200, 1));

                    Portfolio portfolio1 = new Portfolio("Kotleta1");
                    portfolio1.addCurrency(btc);
                    portfolio1.addCurrency(eth);
                    portfolio1.addCurrency(xrp);
                    portfolio1.addCurrency(bch);

                    MainActivity.portfolios.add(portfolio1);

                    Portfolio portfolio2 = new Portfolio("BTC only");
                    portfolio2.addCurrency(btc);
                    MainActivity.portfolios.add(portfolio2);

                    Portfolio portfolio3 = new Portfolio("BCH and ETH");
                    portfolio3.addCurrency(bch);
                    portfolio3.addCurrency(eth);
                    MainActivity.portfolios.add(portfolio3);
                    Log.d("KIRILL", "PORTFOLIOS CREATED");
                }
            }

            void savePortfolios() {
                try {
                    FileOutputStream fos = SplashActivity.this.openFileOutput("Portfolios", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(MainActivity.portfolios);
                    oos.close();
                    fos.close();
                    Log.d("KIRILL", "PORTFOLIOS SAVED");
                } catch (Exception e) {
                    Log.d("KIRILL", "EXCEPTION WHEN SAVE PORTFOLIOS");
                }
            }
        };
        splashThread.start();
    }

    static void savePortfolios(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("Portfolios", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MainActivity.portfolios);
            oos.close();
            fos.close();
            Log.d("KIRILL", "PORTFOLIOS SAVED");
        } catch (Exception e) {
            Log.d("KIRILL", "EXCEPTION WHEN SAVE PORTFOLIOS");
        }
    }
}
