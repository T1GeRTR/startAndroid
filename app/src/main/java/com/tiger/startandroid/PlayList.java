package com.tiger.startandroid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import static com.tiger.startandroid.CursorHelper.mDb;
import static com.tiger.startandroid.NetworkUtils.*;

public class PlayList extends Activity implements View.OnClickListener {
    private final String START_ANDROID_ID = "PLyfVjOYzujugap6Rf3ETNKkx4v9ePllNK";
    ExpandableListView listView;
    static int videoID = 0;
    ListAdapterHelper ah;
    static SimpleExpandableListAdapter adapter;
    CursorHelper cH;
    FloatingActionButton fab;
    static FloatingActionButton fabAdd;
    static EditText etPlayListId;
    CardView dialog;
    ImageView logo;
    TextView tvTitle;
    ImageView btnBack;
    CardView noConnection;
    ImageView refreshNetwork;
    static ProgressBar pbLoadingIndicator;
    static boolean selectSA;
    static String playlist;
    static String playListId;
    Animation hide_dialog;
    Animation rotate_fab_r;
    Animation shake;
    Animation show_dialog;
    Animation rotate_fab;
    Animation rotate_refresh;
    Animation show_btn_back;
    Animation hide_btn_back;
    AddPlayList addPlayList;
    NetworkMonitor networkMonitor;
    boolean isNetworkOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        addPlayList = new AddPlayList();
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);
        noConnection = findViewById(R.id.noConnection);
        noConnection.setOnClickListener(this);
        refreshNetwork = findViewById(R.id.refreshNetwork);
        etPlayListId = findViewById(R.id.etPlayListId);
        dialog = findViewById(R.id.dialog);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        pbLoadingIndicator = findViewById(R.id.pg_loading_indicator);
        networkMonitor = new NetworkMonitor();
        isNetworkOnline = networkMonitor.isNetworkOnline(this);
        noConnection.setVisibility(isNetworkOnline ? View.INVISIBLE : View.VISIBLE);
        selectSA = MainActivity.selectSA;
        if (selectSA) {
            logo.setImageResource(R.drawable.start_android_toolbar);
            tvTitle.setText(R.string.start_android);
            fab.hide();
        } else {
            registerForContextMenu(listView);
            logo.setImageResource(R.drawable.toolbar_other);
            tvTitle.setText("Другие каналы");
            fab.show();
        }
        cH = new CursorHelper();
        cH.cursorHelper(this);
        // Создаем адаптер
        ah = new ListAdapterHelper(this);
        adapter = ah.getAdapter();
        listView.setAdapter(adapter);
        // Нажатие на элемент
        listView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (selectSA) {
                    videoID = cH.getVideoIDArr()[groupPosition][childPosition];
                    playlist = START_ANDROID_ID;
                } else {
                    videoID = childPosition;
                    playlist = cH.getLinksList().get(groupPosition);
                }
                CheckConnection();
                if (isNetworkOnline) {
                    startActivity(new Intent(PlayList.this, YouTubePlayerActivity.class));
                }
                return false;
            }
        });
        // Изменение текста в асинхронном потоке
        etPlayListId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!etPlayListId.getText().toString().equals("")) {
                    if (addPlayList.isFinished) {
                        Toast.makeText(getApplicationContext(), "Добавлена группа " +
                                etPlayListId.getText().toString(), Toast.LENGTH_LONG).show();
                        PlayList.super.recreate();
                    } else if (addPlayList.getStatus() == AsyncTask.Status.RUNNING) {
                        Toast.makeText(getApplicationContext(), AddPlayList.ERROR, Toast.LENGTH_LONG).show();
                        PlayList.super.recreate();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnBack.setVisibility(View.VISIBLE);
        show_btn_back = AnimationUtils.loadAnimation(getApplication(), R.anim.show_btn_back);
        btnBack.startAnimation(show_btn_back);
    }

    @Override
    public void onClick(View v) {
        show_dialog = AnimationUtils.loadAnimation(getApplication(), R.anim.show_dialog);
        hide_dialog = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_dialog);
        rotate_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_rotate);
        rotate_fab_r = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_rotate_r);
        shake = AnimationUtils.loadAnimation(getApplication(), R.anim.shake);
        rotate_refresh = AnimationUtils.loadAnimation(getApplication(), R.anim.refresh_rotate);
        hide_btn_back = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_btn_back);
        switch (v.getId()) {
            // Кнопка +
            case R.id.fab:
                if (dialog.isShown()) {
                    hideDialog(hide_dialog, rotate_fab_r);
                } else {
                    showDialog(show_dialog, rotate_fab);
                }
                break;
                // Подтвержение айди
            case R.id.fab_add:
                CheckConnection();
                if (addPlayList.getStatus() == AsyncTask.Status.PENDING && isNetworkOnline) {
                    if (!etPlayListId.getText().toString().isEmpty()) {
                        playListId = etPlayListId.getText().toString();
                        URL generatedUrl = generateURL(playListId, "");
                        addPlayList.execute(generatedUrl).toString();
                        pbLoadingIndicator.setVisibility(View.VISIBLE);
                        fabAdd.setClickable(false);
                    } else {
                        dialog.startAnimation(shake);
                    }
                }
                break;
                // Возврат на главный экран
            case R.id.btn_back:
                btnBack.setVisibility(View.INVISIBLE);
                btnBack.startAnimation(hide_btn_back);
                onBackPressed();
                break;
                // Нажатие на вложенный список
            case R.id.listView:
                startActivity(new Intent(this, YouTubePlayerActivity.class));
                break;
                // Обновление состояния подключения к интернету
            case R.id.noConnection:
                refreshNetwork.startAnimation(rotate_refresh);
                CheckConnection();
                break;
        }
    }
    // Открытие базы данных
    void openDB() {
        try {
            DataBaseHelper mDB = new DataBaseHelper(this);
            mDb = mDB.getWritableDatabase();
        } catch (
                SQLException mSQLException) {
            throw mSQLException;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) { // на будущее оставлю switch
            // Удаление выбранного пункта
            case R.id.delete:
                openDB();
                Cursor cursor;
                // Если выбран дочерний элемент
                boolean isChild = ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD;
                String tableName;
                int group = ExpandableListView.getPackedPositionGroup(info.packedPosition) + 1;
                String toastText;
                if (isChild){
                    String whereCondition = "channelId = " + group;
                    cursor = mDb.rawQuery("SELECT * FROM otherLessons WHERE " + whereCondition + " ORDER BY _id", null);
                    tableName = "otherLessons";
                    toastText = "Урок удален";
                }else {
                    cursor = mDb.rawQuery("SELECT * FROM otherChannels", null);
                    tableName = "otherChannels";
                    toastText = "Группа удалена";
                }
                // Поскольку айди в базе могут быть не подряд создан счетчик, который определит айди строки в БД
                int i = 0;
                while (!cursor.isAfterLast()) {
                    // Когда курсор достигнет нужной строки он передаст необходимый айди строки для удаления
                    if (i == info.id + 1) {
                            mDb.delete(tableName, "_id = " + cursor.getInt(cursor.getColumnIndex("_id")), null);
                        if (isChild) {
                            // Если выбрано последнее видео, нужно установить флаг об окончании списка предыдущему
                            if (cursor.getInt(cursor.getColumnIndex("changeSubj")) == 1) {
                                int lastVideo = cursor.getInt(cursor.getColumnIndex("_id")) - 1;
                                ContentValues cv = new ContentValues();
                                cv.put("changeSubj", 1);
                                mDb.update("otherLessons", cv, "_id = " + lastVideo, null);
                            }
                            // Если был выбран родительский элемент, нужно удалить также и список видео
                        }else {
                            mDb.delete("otherLessons", "channelId = " + cursor.getInt(cursor.getColumnIndex("_id")), null);
                        }
                        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                        // Перезапуск активити, чтобы отобразить изменения
                        PlayList.super.recreate();
                        break;
                    } else {
                        i++;
                        cursor.moveToNext();
                    }
                }
                cursor.close();
                mDb.close();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    // Для YouTube API
    static int getVideoID() {
        return videoID;
    }

    static String getPlayListId() {
        return playListId;
    }
    // Проверка интернет-соединения
    public void CheckConnection(){
        isNetworkOnline = networkMonitor.isNetworkOnline(this);
        noConnection.setVisibility(isNetworkOnline ? View.INVISIBLE : View.VISIBLE);
        if (!isNetworkOnline){
            Toast.makeText(getApplicationContext(),"Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }
    }
    // Отображение окна ввода айди
    private void showDialog(Animation show_dialog, Animation rotate_fab) {
        dialog.startAnimation(show_dialog);
        dialog.setVisibility(View.VISIBLE);
        fab.startAnimation(rotate_fab);
        fab.setImageResource(R.drawable.ic_cancel);
    }
    // Скрытие окна ввода айди
    void hideDialog(Animation hide_dialog, Animation rotate_fab_r) {
        dialog.startAnimation(hide_dialog);
        dialog.setVisibility(View.INVISIBLE);
        fab.startAnimation(rotate_fab_r);
        fab.setImageResource(R.drawable.ic_add);
    }
    // Разрыв соединения с БД
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }
}
