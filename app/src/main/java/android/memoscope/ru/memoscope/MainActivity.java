package android.memoscope.ru.memoscope;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!VKSdk.isLoggedIn())
            VKSdk.login(this, VKScope.WALL);

        listView = findViewById(R.id.list_view);

        adapter = new CustomAdapter(this);
        Log.d("MyListener", "" + adapter.getCount());
        VKParameters parameters = VKParameters.from(VKApiConst.OWNER_ID, -29534144, VKApiConst.COUNT, 100);
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
}
