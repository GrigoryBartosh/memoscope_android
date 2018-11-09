package android.memoscope.ru.memoscope;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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
}
