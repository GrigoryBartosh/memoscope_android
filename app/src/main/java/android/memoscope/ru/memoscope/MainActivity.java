package android.memoscope.ru.memoscope;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!VKSdk.isLoggedIn())
            VKSdk.login(this, VKScope.WALL);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NestedScrollView scrollView = findViewById(R.id.scrollView);
        listView = scrollView.findViewById(R.id.list_view);
        listView.setNestedScrollingEnabled(true);

        Button fromDateButton = findViewById(R.id.from_date);
        Button toDateButton = findViewById(R.id.to_date);
        fromDateButton.setOnClickListener(new DateButtonClickListener());
        toDateButton.setOnClickListener(new DateButtonClickListener());


        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior == null) {
            params.setBehavior(new AppBarLayout.Behavior());
            behavior = (AppBarLayout.Behavior) params.getBehavior();
        }
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });

        adapter = new CustomAdapter(this);
        Log.d("MyListener", "" + adapter.getCount());
        VKParameters parameters = VKParameters.from(VKApiConst.OWNER_ID, -29534144, VKApiConst.COUNT, 100, VKApiConst.EXTENDED, 1);
        VKApi.wall().get(parameters).executeSyncWithListener(new MyListener());
    }

    class MyListener extends VKRequest.VKRequestListener {

        @Override
        public void onComplete(VKResponse response) {
            try {
                JSONArray posts = response.json
                        .getJSONObject("response")
                        .getJSONArray("items");

                Log.d("MyListener", "" + adapter.getCount());
                for (int i = 0; i < posts.length(); i++) {
                    adapter.add(posts.getJSONObject(i));
                }
                Log.d("MyListener", "" + adapter.getCount());
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.d("MyListener", e.getMessage());
            }
        }

        @Override
        public void onError(VKError error) {
            Log.d("MyListener", "onError, code: " + error.errorCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View options = (View)findViewById(R.id.option_layout);
                options.setVisibility(View.VISIBLE);
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                View options = (View)findViewById(R.id.option_layout);
                int vis = hasFocus ? View.VISIBLE : View.GONE;
                options.setVisibility(vis);
            }
        });
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Button firstButton;
        private Button secondButton;
        private Button button;
        private boolean pressedFirst;
        private Locale locale;

        public void setButtons(Button firstButton, Button secondButton, boolean pressedFirst) {
            this.firstButton = firstButton;
            this.secondButton = secondButton;
            this.pressedFirst = pressedFirst;
            this.button = pressedFirst ? firstButton : secondButton;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String dayStr = day < 10? "0" + day : "" + day;
            month++;
            String monthStr = month < 10? "0" + month : "" + month;

            String resultDate = dayStr + "/" + monthStr + "/" + year;
            button.setText(resultDate);
            if (overlappingDates(firstButton.getText(), secondButton.getText())) {
                if (pressedFirst)
                    secondButton.setText(resultDate);
                else
                    firstButton.setText(resultDate);
            }
            //@SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            //button.setText(format.format(new Date(year, month, day)));
        }



        private boolean overlappingDates(CharSequence d1, CharSequence d2) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", locale);
            try {
                Date dateFrom = fmt.parse((String) d1);
                Date dateTo = fmt.parse((String) d2);
                return dateTo.before(dateFrom);
            } catch (ParseException e) {
                return false;
            }
        }
    }

    private Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            return context.getResources().getConfiguration().locale;
        }
    }

    public class DateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;

            Button fromButton = findViewById(R.id.from_date);
            Button toButton = findViewById(R.id.to_date);

            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setButtons(fromButton, toButton, button.getId() == fromButton.getId());
            newFragment.setLocale(getCurrentLocale(getApplicationContext()));
            newFragment.show(getSupportFragmentManager(), "date");
        }
    }
}
