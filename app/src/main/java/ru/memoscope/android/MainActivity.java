package ru.memoscope.android;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ru.memoscope.android.recyclerview.CustomAdapter;
import ru.memoscope.android.recyclerview.ShadowVerticalSpaceItemDecorator;
import ru.memoscope.android.recyclerview.VerticalSpaceItemDecorator;
import ru.memoscope.android.utils.Network;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listView;

    private ArrayList<Pub> supportedPubList = new ArrayList<>();
    private Set<Integer> pubSet = new HashSet<>();

    private final List<JSONObject> fakePosts;
    private final SparseArray<JSONObject> fakeGroups;

    {
        JSONObject post = null;
        try {
            post = new JSONObject("{\"id\":9975266,\"from_id\":-29534144,\"owner_id\":-29534144,\"date\":1541775330,\"marked_as_ads\":0,\"post_type\":\"post\",\"text\":\"Тверская область стала первым российским регионом, который полностью перешёл с аналогового эфирного телевещания на цифровое.\\n\\nПо всей стране аналоговое вещание отключат с 2019 года, хотя, по опросам, 30% телевизоров в стране не могут в цифру\\n\\nhttp:\\/\\/news.lenta.ch\\/vLf9\",\"attachments\":[{\"type\":\"photo\",\"photo\":{\"id\":456753792,\"album_id\":-7,\"owner_id\":-29534144,\"user_id\":100,\"photo_75\":\"https:\\/\\/sun9-4.userapi.com\\/c635106\\/v635106310\\/f234\\/nsuSWGUzuvc.jpg\",\"photo_130\":\"https:\\/\\/sun9-3.userapi.com\\/c635106\\/v635106310\\/f235\\/lxyBdbZ8qgU.jpg\",\"photo_604\":\"https:\\/\\/sun9-3.userapi.com\\/c635106\\/v635106310\\/f236\\/YpAtRUpU6JE.jpg\",\"photo_807\":\"https:\\/\\/sun9-6.userapi.com\\/c635106\\/v635106310\\/f237\\/QZGWkDG4MxE.jpg\",\"width\":800,\"height\":620,\"text\":\"Тверская область стала первым российским регионом, который полностью перешёл с аналогового эфирного телевещания на цифровое.\\n\\nПо всей стране аналоговое вещание отключат с 2019 года, хотя, по опросам, 30% телевизоров в стране не могут в цифру\\n\\nhttp:\\/\\/news.lenta.ch\\/vLf9\",\"date\":1541775330,\"post_id\":9975266,\"access_key\":\"4efb59e9bf2361fe55\"}},{\"type\":\"audio\",\"audio\":{\"id\":456241677,\"owner_id\":2000016863,\"artist\":\"Tony Igy\",\"title\":\"Astronomia\",\"duration\":25,\"date\":1541775531,\"url\":\"https:\\/\\/vk.com\\/mp3\\/audio_api_unavailable.mp3\"}}],\"post_source\":{\"type\":\"api\"},\"comments\":{\"count\":211,\"can_post\":1},\"likes\":{\"count\":2549,\"user_likes\":0,\"can_like\":1,\"can_publish\":1},\"reposts\":{\"count\":33,\"user_reposted\":0}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fakePosts = Arrays.asList(post, post, post);
        fakeGroups = new SparseArray<>();
        try {
            JSONObject group = new JSONObject("{\n" +
                    "\"id\": 29534144,\n" +
                    "\"name\": \"Лентач\",\n" +
                    "\"screen_name\": \"oldlentach\",\n" +
                    "\"is_closed\": 0,\n" +
                    "\"type\": \"page\",\n" +
                    "\"is_admin\": 0,\n" +
                    "\"is_member\": 1,\n" +
                    "\"photo_50\": \"https://sun9-9.us...tyoY-PY-8.jpg?ava=1\",\n" +
                    "\"photo_100\": \"https://sun9-6.us...H1ZGSkcOE.jpg?ava=1\",\n" +
                    "\"photo_200\": \"https://sun9-6.us...aAIay7114.jpg?ava=1\"\n" +
                    "}");
            fakeGroups.put(-29534144, group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!VKSdk.isLoggedIn())
            VKSdk.login(this, VKScope.WALL);

        NestedScrollView scrollView = findViewById(R.id.scrollView);

        CustomAdapter adapter = new CustomAdapter(this, fakePosts, fakeGroups);

        listView = scrollView.findViewById(R.id.list_view);
        listView.setNestedScrollingEnabled(true);
        listView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator =
                new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator =
                new ShadowVerticalSpaceItemDecorator(this, R.drawable.drop_shadow);


        // 7. Set the LayoutManager
        listView.setLayoutManager(layoutManager);

        // 8. Set the ItemDecorators
        listView.addItemDecoration(shadowItemDecorator);
        listView.addItemDecoration(itemDecorator);

        listView.setAdapter(adapter);

        VKParameters parameters = VKParameters.from(VKApiConst.OWNER_ID, -29534144, VKApiConst.COUNT, 100, VKApiConst.EXTENDED, 1);
        VKApi.wall()
                .get(parameters)
                .executeSyncWithListener(new GetPostsListener());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button fromDateButton = findViewById(R.id.from_date);
        Button toDateButton = findViewById(R.id.to_date);
        fromDateButton.setOnClickListener(new DateButtonClickListener());
        String currentDate = currentDateString();
        fromDateButton.setText(currentDate);
        toDateButton.setOnClickListener(new DateButtonClickListener());
        toDateButton.setText(currentDate);

        supportedPubList.add(new Pub(1, "Лентач", "https://pp.userapi.com/c637622/v637622257/5b5bf/xjT2gn-8MU4.jpg"));
        supportedPubList.add(new Pub(2, "Абстрактные мемы для элиты всех сортов", "https://pp.userapi.com/c637622/v637622257/5b5bf/xjT2gn-8MU4.jpg"));

        pubSet.add(1);
        pubSet.add(2);

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
    }

    private String currentDateString() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", getCurrentLocale(this));
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View options = findViewById(R.id.option_layout);
                options.setVisibility(View.VISIBLE);
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                View options = findViewById(R.id.option_layout);
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fakePost() {
        ((CustomAdapter)listView.getAdapter()).update(fakePosts, fakeGroups);
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

                SparseArray<JSONObject> groupsMap = new SparseArray<>();

                JSONArray groups = responseObject
                        .getJSONArray("groups");

                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    int id = -group.getInt("id");
                    groupsMap.put(id, group);
                }

                ((CustomAdapter) listView.getAdapter()).update(postsList, groupsMap);
            } catch (JSONException e) {
                Log.e("MyListener", e.getMessage());
            }
        }

        public void onError(VKError error) {
            Log.e("MyListener", "onError, code: " + error.errorCode);
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

    public class FilterSelectListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = String.valueOf(((TextView)view).getText());
            switch (selected) {
                case "Все": {
                    pubSet = new HashSet<>(Arrays.asList(1, 2));
                    break;
                }
                case "Мои": {
                    pubSet = getMySubs();
                    break;
                }
                case "Выбор": {
                    FragmentManager fm = getSupportFragmentManager();
                    CustomPubsDialogFragment pubsDialogFragment = CustomPubsDialogFragment.newInstance("Some Title");
                    pubsDialogFragment.setPubs(pubSet, supportedPubList);
                    pubsDialogFragment.show(fm, "fragment_pubs");
                    break;
                }

            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private Set<Integer> getMySubs() {
        return new HashSet<>(Arrays.asList(1));
    }
}
