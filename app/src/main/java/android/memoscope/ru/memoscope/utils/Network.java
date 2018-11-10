package android.memoscope.ru.memoscope.utils;

import android.content.Context;
import android.content.Intent;
import android.memoscope.ru.memoscope.MainActivity;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ru.memoscope.ServerGrpc;
import ru.memoscope.ServerProto;

public class Network {
    private String host;
    private int port;
    private ManagedChannel channel;
    private ServerGrpc.ServerStub stub;
    private MainActivity mContext;

    public Network(MainActivity context, String host, int port) {
        mContext = context;
        this.host = host;
        this.port = port;
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = ServerGrpc.newStub(channel);
    }

    public void getPosts() {
        ServerProto.FindPostsRequest request = ServerProto.FindPostsRequest.newBuilder()
                .addGroupIds(-29534144)
                .setText("Sanya loh")
                .setTimeFrom(0)
                .setTimeTo(1000)
                .build();
        stub.findPosts(request, new ResponseObserver());
    }

    class ResponseObserver implements StreamObserver<ServerProto.FindPostsResponse> {
        @Override
        public void onNext(ServerProto.FindPostsResponse value) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < value.getPostsCount(); i++) {
                ServerProto.PostInfo postInfo = value.getPosts(i);
                builder.append(postInfo.getGroupId()).append("_").append(postInfo.getPostId());
                if (i + 1 != value.getPostsCount())
                    builder.append(",");
            }
            String posts = builder.toString();
            Log.d("NetworkTag", posts);
            VKParameters parameters = VKParameters.from(VKApiConst.POSTS, posts, VKApiConst.EXTENDED, 1);
            VKApi.wall()
                    .getById(parameters)
                    .executeSyncWithListener(mContext.new GetPostsListener());
        }

        @Override
        public void onError(Throwable t) {
            Log.d("NetworkTag", "error((");
        }

        @Override
        public void onCompleted() {

        }
    }
}