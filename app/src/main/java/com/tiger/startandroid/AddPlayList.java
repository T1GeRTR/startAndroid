package com.tiger.startandroid;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;

import static com.tiger.startandroid.CursorHelper.mDb;
import static com.tiger.startandroid.NetworkUtils.getResponseFromURL;
import static com.tiger.startandroid.PlayList.*;

class AddPlayList extends AsyncTask<URL, Void, String> {
    boolean isFinished;
    static final String ERROR = "Ошибка, неверный ID";

    @Override
    protected String doInBackground(URL... urls) {
        isFinished = false;
        String response = null;
        try {
            response = getResponseFromURL(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        String channelTitle = null;
        String videoTitle;
        PlayList playList = new PlayList();
        playList.openDB();
        try {
            // Проверка на успешность запроса к YouTube Data API
            if (response != null && !response.equals("")) {
                JSONObject json = new JSONObject(response);
                JSONArray jsonArray = json.getJSONArray("items");
                if (!jsonArray.isNull(0)) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject videosInfo = jsonArray.getJSONObject(i);
                        JSONObject videoInfo = videosInfo.getJSONObject("snippet");
                        videoTitle = videoInfo.getString("title");
                        int changeSubj = (i == jsonArray.length() - 1) ? 1 : 0;
                        long channelId = 0;
                        if (i == 0) {
                            // Запись данных о playlist
                            channelTitle = videoInfo.getString("channelTitle");
                            ContentValues cv = new ContentValues();
                            cv.put("name", channelTitle);
                            cv.put("link", PlayList.getPlayListId());
                            channelId = mDb.insert("otherChannels", null, cv);
                        }
                        // Запись списка видео
                        if (videoTitle != null) {
                            ContentValues cv = new ContentValues();
                            cv.put("title", videoTitle);
                            cv.put("channelId", channelId);
                            cv.put("changeSubj", changeSubj);
                            mDb.insert("otherLessons", null, cv);
                        }
                        // Строка в БД, указывающая на конец playlist
                        if (i == jsonArray.length() - 1) {
                            ContentValues cv = new ContentValues();
                            cv.put("title", "----");
                            cv.put("channelId", channelId);
                            cv.put("changeSubj", 0);
                            mDb.insert("otherLessons", null, cv);
                        }
                    }
                    // Флаг, указывающий на окончание работы потока
                    isFinished = true;
                    etPlayListId.setText(channelTitle);
                    etPlayListId.setText("");
                    isFinished = false;
                }
            } else {
                etPlayListId.setText(etPlayListId.getText() + " ");
                isFinished = false;
            }
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            mDb.close();
            fabAdd.setClickable(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
