package com.tiger.startandroid;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    public static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static final String YOUTUBE_API_GET = "playlistItems";
    public static final String YOUTUBE_API_PART_PARAM = "part";
    public static final String YOUTUBE_API_PLAYLIST_ID_PARAM = "playlistId";
    public static final String YOUTUBE_API_KEY_PARAM = "key";
    public static final String YOUTUBE_API_MAX_RESULTS = "maxResults";
    public static final String YOUTUBE_API_PAGE_TOKEN = "pageToken";

    // Генерация URL для запроса к YouTube Data API
    public static URL generateURL(String playlistId, String pageToken) {
        Uri buildUri = Uri.parse(YOUTUBE_API_BASE_URL + YOUTUBE_API_GET)
                .buildUpon()
                .appendQueryParameter(YOUTUBE_API_PART_PARAM, "snippet")
                .appendQueryParameter(YOUTUBE_API_PLAYLIST_ID_PARAM, playlistId)
                .appendQueryParameter(YOUTUBE_API_KEY_PARAM, YouTubeConfig.getYoutubeApiKey())
                .appendQueryParameter(YOUTUBE_API_MAX_RESULTS, "50")
                .appendQueryParameter(YOUTUBE_API_PAGE_TOKEN, pageToken)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Запрос по URL
    public static String getResponseFromURL(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
