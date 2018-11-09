package android.memoscope.ru.memoscope;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter adapter;

    private List<String> supportedPubList = Arrays.asList("1", "2", "3", "4");//new ArrayList<>();
    private List<String> pubList = new ArrayList<>(supportedPubList);


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
        String currentDate = currentDateString();
        fromDateButton.setText(currentDate);
        toDateButton.setOnClickListener(new DateButtonClickListener());
        toDateButton.setText(currentDate);


        Spinner filterSpinner = findViewById(R.id.filter_spinner);
        filterSpinner.setOnItemSelectedListener(new FilterSelectListener());

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

    private String currentDateString() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", getCurrentLocale(this));
        return dateFormat.format(date);
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

    public class FilterSelectListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = String.valueOf(((TextView)view).getText());
            switch (selected) {
                case "Все": {
                    pubList = new ArrayList<>(supportedPubList);
                    break;
                }
                case "Мои": {
                    pubList = getMySubs();
                    break;
                }
                case "Выбор": {
                    FragmentManager fm = getSupportFragmentManager();
                    CustomPubsDialogFragment pubsDialogFragment = CustomPubsDialogFragment.newInstance("Some Title");
                    pubsDialogFragment.setPubs(pubList, supportedPubList);
                    pubsDialogFragment.show(fm, "fragment_pubs");
                    break;
                }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private List<String> getMySubs() {
        List<String> userSubs = Arrays.asList("1", "3");
        userSubs.retainAll(supportedPubList);
        return userSubs;
    }
}
