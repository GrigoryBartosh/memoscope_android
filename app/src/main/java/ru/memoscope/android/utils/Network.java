package ru.memoscope.android.utils;

import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;

import java.util.List;
import java.util.Set;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ru.memoscope.ServerGrpc;
import ru.memoscope.ServerProto;
import ru.memoscope.android.MainActivity;

public class Network {
    private String host;
    private int port;

    private ManagedChannel channel;
    private ServerGrpc.ServerStub stub;
    private ServerGrpc.ServerBlockingStub blockingStub;

    private MainActivity mContext;

    public Network(MainActivity context, String host, int port) {
        mContext = context;
        this.host = host;
        this.port = port;

        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = ServerGrpc.newStub(channel);
        blockingStub = ServerGrpc.newBlockingStub(channel);
    }

    public String getPosts(String requestText, long timeFrom, long timeTo, Iterable<Long> groupIds) {
        ServerProto.FindPostsRequest request = ServerProto.FindPostsRequest.newBuilder()
                .addAllGroupIds(groupIds)
                .setText(requestText)
                .setTimeFrom(timeFrom)
                .setTimeTo(timeTo)
                .build();
        ServerProto.FindPostsResponse response = blockingStub.findPosts(request);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < response.getPostsCount(); i++) {
            ServerProto.PostInfo postInfo = response.getPosts(i);
            builder.append(postInfo.getGroupId()).append("_").append(postInfo.getPostId());
            if (i + 1 != response.getPostsCount())
                builder.append(",");
        }
        return builder.toString();
    }

    public List<Long> getGroups() {
        ServerProto.GetGroupsRequest request = ServerProto.GetGroupsRequest.newBuilder()
                .build();
        ServerProto.GetGroupsResponse response = blockingStub.getGroups(request);
        return response.getGroupIdsList();
    }
}