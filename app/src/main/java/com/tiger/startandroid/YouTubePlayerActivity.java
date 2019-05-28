package com.tiger.startandroid;

import android.os.Bundle;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubePlayerActivity extends YouTubeBaseActivity {
    public int videoId;
    YouTubePlayerView youTubeView;
    String playlist = "PLyfVjOYzujugap6Rf3ETNKkx4v9ePllNK";
    com.google.android.youtube.player.YouTubePlayer.OnInitializedListener mOnInitializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtubeplayer);
        videoId = PlayList.getVideoID();
        youTubeView = findViewById(R.id.youtube_view);
        playlist = PlayList.playlist;

        mOnInitializedListener = new com.google.android.youtube.player.YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider provider, final com.google.android.youtube.player.YouTubePlayer player, boolean b) {
                player.setPlayerStyle(com.google.android.youtube.player.YouTubePlayer.PlayerStyle.DEFAULT);
                player.loadPlaylist(playlist, videoId, 0);
                player.play();
            }

            @Override
            public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        youTubeView.initialize(YouTubeConfig.getYoutubeApiKey(), mOnInitializedListener);
    }
}
