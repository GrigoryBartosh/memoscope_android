package android.memoscope.ru.memoscope;

import android.memoscope.ru.memoscope.utils.Network;
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
import android.widget.BaseAdapter;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

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

        VKParameters parameters = VKParameters.from(VKApiConst.OWNER_ID, -92337511, VKApiConst.COUNT, 10, VKApiConst.EXTENDED, 1);
        VKApi.wall()
                .get(parameters)
                .executeSyncWithListener(new GetPostsListener());
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
                sendRequestToServer(query);
                //fakePost();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fakePost() {
        try {
            JSONObject post = new JSONObject("{\"id\":9975266,\"from_id\":-29534144,\"owner_id\":-29534144,\"date\":1541775330,\"marked_as_ads\":0,\"post_type\":\"post\",\"text\":\"Тверская область стала первым российским регионом, который полностью перешёл с аналогового эфирного телевещания на цифровое.\\n\\nПо всей стране аналоговое вещание отключат с 2019 года, хотя, по опросам, 30% телевизоров в стране не могут в цифру\\n\\nhttp:\\/\\/news.lenta.ch\\/vLf9\",\"attachments\":[{\"type\":\"photo\",\"photo\":{\"id\":456753792,\"album_id\":-7,\"owner_id\":-29534144,\"user_id\":100,\"photo_75\":\"https:\\/\\/sun9-4.userapi.com\\/c635106\\/v635106310\\/f234\\/nsuSWGUzuvc.jpg\",\"photo_130\":\"https:\\/\\/sun9-3.userapi.com\\/c635106\\/v635106310\\/f235\\/lxyBdbZ8qgU.jpg\",\"photo_604\":\"https:\\/\\/sun9-3.userapi.com\\/c635106\\/v635106310\\/f236\\/YpAtRUpU6JE.jpg\",\"photo_807\":\"https:\\/\\/sun9-6.userapi.com\\/c635106\\/v635106310\\/f237\\/QZGWkDG4MxE.jpg\",\"width\":800,\"height\":620,\"text\":\"Тверская область стала первым российским регионом, который полностью перешёл с аналогового эфирного телевещания на цифровое.\\n\\nПо всей стране аналоговое вещание отключат с 2019 года, хотя, по опросам, 30% телевизоров в стране не могут в цифру\\n\\nhttp:\\/\\/news.lenta.ch\\/vLf9\",\"date\":1541775330,\"post_id\":9975266,\"access_key\":\"4efb59e9bf2361fe55\"}},{\"type\":\"audio\",\"audio\":{\"id\":456241677,\"owner_id\":2000016863,\"artist\":\"Tony Igy\",\"title\":\"Astronomia\",\"duration\":25,\"date\":1541775531,\"url\":\"https:\\/\\/vk.com\\/mp3\\/audio_api_unavailable.mp3\"}}],\"post_source\":{\"type\":\"api\"},\"comments\":{\"count\":211,\"can_post\":1},\"likes\":{\"count\":2549,\"user_likes\":0,\"can_like\":1,\"can_publish\":1},\"reposts\":{\"count\":33,\"user_reposted\":0}}");
            CustomAdapter adapter = new CustomAdapter(this, Collections.singletonList(post));
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestToServer(String query) {
        String host = getResources().getString(R.string.host);
        int port = getResources().getInteger(R.integer.port);
        new Network(this, host, port).getPosts();
    }

    public class GetPostsListener extends VKRequest.VKRequestListener {

        @Override
        public void onComplete(VKResponse response) {
            try {
                JSONObject responseObject = response.json
                        .getJSONObject("response");
                JSONArray posts = responseObject
                        .getJSONArray("items");

                Log.d("MyListener", posts.toString());

                ArrayList<JSONObject> postsList = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {
                    postsList.add(posts.getJSONObject(i));
                }

                CustomAdapter adapter = new CustomAdapter(MainActivity.this, postsList);

                JSONArray groups = responseObject
                        .getJSONArray("groups");

                for (int i = 0; i < groups.length(); i++) {
                    adapter.addGroup(groups.getJSONObject(i));
                }

                listView.setAdapter(adapter);
                //((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("MyListener", e.getMessage());
            }
        }

        @Override
        public void onError(VKError error) {
            Log.e("MyListener", "onError, code: " + error.errorCode);
        }
    }
}
