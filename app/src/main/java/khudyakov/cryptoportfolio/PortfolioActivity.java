package khudyakov.cryptoportfolio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PortfolioActivity extends AppCompatActivity {

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

    static Portfolio portfolio;
    static Period period;
    static int portfolioId;
    static CandleStickChart chart;
    boolean justCreated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        justCreated = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        Log.d("KIRILL", "PORTFOLIO ACTIVITY CREATED");

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

        int id = getIntent().getIntExtra("Id", -1);
        portfolioId = id;
        portfolio = MainActivity.portfolios.get(id);
        portfolio.getTimeStamps();

        period = Period.MONTH;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PortfolioActivity.this);
                builder.setTitle("Add currency");
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_currency, null);
                final Spinner spinner = dialogView.findViewById(R.id.spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                        (PortfolioActivity.this, R.array.crypto_tickers, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                builder.setView(dialogView);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = spinner.getSelectedItem().toString();
                        portfolio.addCurrency(new Currency(name, App.info));
                        SplashActivity.savePortfolios(PortfolioActivity.this);
                        recreate();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("KIRILL", "PORTFOLIO ACTIVITY PAUSED");
        justCreated = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("KIRILL", "PORTFOLIO ACTIVITY RESUMED");
        if (!justCreated) {
            recreate();
        }
    }

    static void setupCandleStickChart(View rootView) {
        chart = rootView.findViewById(R.id.portfolioChart);

        ArrayList<CandleEntry> entries = portfolio.getEntries(period);
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
        chart.setData(data);
        chart.invalidate();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_portfolio_month:
                if (checked)
                    period = Period.MONTH;
                break;
            case R.id.radio_portfolio_half_year:
                if (checked)
                    period = Period.HALF_YEAR;
                break;
            case R.id.radio_portfolio_year:
                if (checked)
                    period = Period.YEAR;
                break;
            case R.id.radio_portfolio_all:
                if (checked)
                    period = Period.ALL;
                break;
        }

        setupCandleStickChart(view.getRootView().getRootView());
        chart.notifyDataSetChanged();
    }

    static PieChart setupPieChart(Context context) {
        ArrayList<Currency> currencies = portfolio.currencies;
        ArrayList<PieEntry> entries = new ArrayList<>(currencies.size());
        for (Currency currency: currencies) {
            entries.add(new PieEntry(currency.currentCost(), currency.name));
        }

        PieDataSet dataSet = new PieDataSet(entries, portfolio.name + " portfolio composition");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);

        PieChart chart = new PieChart(context);
        chart.setMinimumHeight(1000);
        chart.setData(data);
        chart.invalidate();
        return chart;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portfolio, menu);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView= inflater.inflate(R.layout.portfolio_graph, container, false);

            if (portfolio.isEmpty()) {
                return inflater.inflate(R.layout.empty_porfolio_layout, container, false);
            }

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.portfolio_graph, container, false);
                    PortfolioActivity.setupCandleStickChart(rootView);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.portfolio_overview, container, false);
                    TextView costText = rootView.findViewById(R.id.portfolioCostText);
                    TextView monthProfit = rootView.findViewById(R.id.porfolioMonthProfitText);
                    TextView annualProfit = rootView.findViewById(R.id.porfolioAnnualProfitText);
                    TextView allTimeProfit = rootView.findViewById(R.id.porfolioAllTimeProfitText);
                    costText.setText(String.format("%.0f$", portfolio.getCost()));
                    monthProfit.setText(String.format("%.0f%%", portfolio.getProfit(Period.MONTH) * 100));
                    annualProfit.setText(String.format("%.0f%%", portfolio.getProfit(Period.YEAR) * 100));
                    allTimeProfit.setText(String.format("%.0f%%", portfolio.getProfit(Period.ALL) * 100));

                    Button deletePortfolioButton = rootView.findViewById(R.id.deletePortfolioButton);
                    deletePortfolioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.portfolios.remove(portfolio);
                            SplashActivity.savePortfolios(getContext());
                            getActivity().finish();
                        }
                    });
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.composition_layout, container, false);

                    ListView listView = rootView.findViewById(R.id.compositionList);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, portfolio.getComposition());
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent
                                    (getContext(), CurrencyActivity.class);
                            intent.putExtra("Portfolio", portfolioId);
                            intent.putExtra("Currency", position - 1);
                            startActivity(intent);
                        }
                    });
                    listView.addHeaderView(PortfolioActivity.setupPieChart(getContext()));
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
