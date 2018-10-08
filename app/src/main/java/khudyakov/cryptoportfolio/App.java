package khudyakov.cryptoportfolio;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    static Info info;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Quotes", "App started");
        getInfo();
    }

    void getInfo() {
        info = readFromCache();
        if (info == null) {
            Log.d("Quotes", "Getting data from internet");
            info = new Info(this);
        }
    }

    Info readFromCache() {
        Info data;
        FileInputStream fis;
        ObjectInputStream ois;
        try {
            fis = openFileInput("Quotations");
            ois = new ObjectInputStream(fis);
            data = (Info) ois.readObject();
            data.currentContext = this;
            ois.close();
            fis.close();
            Log.d("Quotes", "Read data from cache");
        } catch (Exception e) {
            data = null;
            Log.d("Quotes", "Exception in Read");
        }

        return data;
    }


}
