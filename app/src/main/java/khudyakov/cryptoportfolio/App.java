package khudyakov.cryptoportfolio;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    static Info info;

    @Override
    public void onCreate() {
        super.onCreate();

        info = new Info(this);
    }
}
