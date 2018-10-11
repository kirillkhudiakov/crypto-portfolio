package khudyakov.cryptoportfolio;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CurrencyActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static Currency currency;
    static CandleStickChart currencyChart;
    static Period period;
    static Portfolio portfolio;
    static int portfolioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        portfolioId = getIntent().getIntExtra("Portfolio", -1);
        int currencyId = getIntent().getIntExtra("Currency", -1);
        portfolio = MainActivity.portfolios.get(portfolioId);
        currency = portfolio.getCurrency(currencyId);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CurrencyActivity.this);
                builder.setTitle("Add transaction");
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_transaction, null);
                builder.setView(dialogView);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatePicker datePicker = dialogView.findViewById(R.id.newDatePicker);
                        EditText newAmountText = dialogView.findViewById(R.id.newAmountText);
                        long date = getUnixTime(datePicker);
                        float amount = Float.parseFloat(newAmountText.getText().toString());
                        RadioButton sellRadioButton = dialogView.findViewById(R.id.sellRadio);
                        if (sellRadioButton.isChecked()) {
                            amount *= -1;
                        }

                        currency.addTransaction(new Transaction(date, amount));
                        Activity currentActivity = CurrencyActivity.this;
                        SplashActivity.savePortfolios(currentActivity);
                        currentActivity.recreate();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        period = Period.MONTH;
    }

    static long getUnixTime(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        TimeZone timeZone = TimeZone.getTimeZone("Etc/GMT");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_currency_month:
                if (checked)
                    period = Period.MONTH;
                break;
            case R.id.radio_currency_half_year:
                if (checked)
                    period = Period.HALF_YEAR;
                break;
            case R.id.radio_currency_year:
                if (checked)
                    period = Period.YEAR;
                break;
            case R.id.radio_currency_all:
                if (checked)
                    period = Period.ALL;
                break;
        }

        setupCandleStickChart(view.getRootView().getRootView());
        currencyChart.notifyDataSetChanged();
    }

    static void setupCandleStickChart(View rootView) {
        currencyChart = rootView.findViewById(R.id.currencyChart);

        ArrayList<CandleEntry> entries = currency.getEntries(period);
        CandleDataSet dataSet = new CandleDataSet(entries, "The Chort");
        dataSet.setColor(Color.rgb(80, 80, 80));
        dataSet.setShadowColor(Color.DKGRAY);
        dataSet.setShadowWidth(0.7f);
        dataSet.setDecreasingColor(Color.RED);
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(Color.rgb(122, 242, 84));
        dataSet.setIncreasingPaintStyle(Paint.Style.STROKE);
        dataSet.setNeutralColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);
        CandleData data = new CandleData(dataSet);
        currencyChart.setData(data);
        currencyChart.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_currency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView= inflater.inflate(R.layout.portfolio_graph, container, false);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.currency_graph_layout, container, false);
                    CurrencyActivity.setupCandleStickChart(rootView);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.currency_overview_layout, container, false);
                    TextView amountText = rootView.findViewById(R.id.currencyAmountText);
                    amountText.setText(Float.toString(CurrencyActivity.currency.currentAmount()));
                    TextView costText = rootView.findViewById(R.id.currencyCostText);
                    costText.setText(String.format("%.0f$", CurrencyActivity.currency.currentCost()));
                    TextView profitText = rootView.findViewById(R.id.currencyProfitText);
                    profitText.setText(String.format("%.0f%%", currency.profit(Period.ALL) * 100));
                    TextView dailyChangeText = rootView.findViewById(R.id.dailyChangeText);
                    dailyChangeText.setText(String.format("%.0f%%", currency.dailyChange() * 100));

                    Button deleteButton = rootView.findViewById(R.id.deleteCurrencyButton);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            portfolio.currencies.remove(currency);
                            SplashActivity.savePortfolios(getActivity());
//                            Intent intent = new Intent(getContext(), PortfolioActivity.class);
//                            intent.putExtra("Id", portfolioId);
//                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.transaction_layout, container, false);
                    ListView transactionsList = rootView.findViewById(R.id.transactionsList);
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1, currency.getTransactions());
                    transactionsList.setAdapter(adapter);

                    transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Edit transaction");

                            final Transaction transaction = currency.transactions.get(position);
                            View dialogView = inflater.inflate(R.layout.dialog_new_transaction, null);

                            final DatePicker datePicker = dialogView.findViewById(R.id.newDatePicker);
                            Date date = transaction.getDateObject();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(transaction.date * 1000);
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            datePicker.updateDate(year, month, day);

                            final EditText editAmountText = dialogView.findViewById(R.id.newAmountText);
                            editAmountText.setText(Float.toString(Math.abs(transaction.amount)));

                            final RadioButton buyRadio = dialogView.findViewById(R.id.buyRadio);
                            final RadioButton sellRadio = dialogView.findViewById(R.id.sellRadio);
                            if (transaction.amount < 0) {
                                buyRadio.setChecked(false);
                                sellRadio.setChecked(true);
                            }

                            builder.setView(dialogView);
                            builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    long newDate = getUnixTime(datePicker);
                                    float newAmount = Float.parseFloat(editAmountText.getText().toString());
                                    if (sellRadio.isChecked()) {
                                        newAmount *= -1;
                                    }
                                    Transaction newTransaction = new Transaction(newDate, newAmount);
                                    currency.replaceTransaction(transaction, newTransaction);
                                    adapter.notifyDataSetChanged();
                                    Activity currentActivity = getActivity();
                                    SplashActivity.savePortfolios(currentActivity);
                                    currentActivity.recreate();
                                }
                            });
                            builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currency.removeTransaction(transaction);
                                    SplashActivity.savePortfolios(getActivity());
                                    adapter.notifyDataSetChanged();
                                    Activity currentActivity = getActivity();
                                    SplashActivity.savePortfolios(currentActivity);
                                    currentActivity.recreate();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                    break;
            }
//            View rootView = inflater.inflate(R.layout.fragment_portfolio, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
