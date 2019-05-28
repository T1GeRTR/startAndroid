package com.tiger.startandroid;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class CursorHelper {
    //Коллекция для тем уроков
    private ArrayList<Map<String, String>> subjectList;
    //Коллекция для уроков
    private ArrayList<Map<String, String>> lessonList;
    //Общая коллекция
    private ArrayList<ArrayList<Map<String, String>>> playList;
    //id плейлистов
    private ArrayList<String> linksList;
    //id элементов списка
    private ArrayList <Integer> idList;
    //Атрибуты
    private Map<String, String> attribute;
    private String[] subjects = new String[37];
    private static int[][] videoIDArr = new int[][]{
            new int[3], new int[6], new int[2], new int[3], new int[3],
            new int[3], new int[2], new int[2], new int[3], new int[6],
            new int[1], new int[1], new int[6], new int[2], new int[5],
            new int[8], new int[3], new int[10], new int[2], new int[6],
            new int[3], new int[1], new int[6], new int[6], new int[9],
            new int[1], new int[2], new int[3], new int[2], new int[3],
            new int[3], new int[3], new int[3], new int[2], new int[3],
            new int[3], new int[3]};
    // названия уроков
    private String[][] lessons = new String[][]{
            new String[3], new String[6], new String[2], new String[3], new String[3],
            new String[3], new String[2], new String[2], new String[3], new String[6],
            new String[1], new String[1], new String[6], new String[2], new String[5],
            new String[8], new String[3], new String[10], new String[2], new String[6],
            new String[3], new String[1], new String[6], new String[6], new String[9],
            new String[1], new String[2], new String[3], new String[2], new String[3],
            new String[3], new String[3], new String[3], new String[2], new String[3],
            new String[3], new String[3]};

    static SQLiteDatabase mDb;

    void cursorHelper(Context context) {
        boolean selectSA = MainActivity.selectSA;
        String groupName;
        String childName;
        DataBaseHelper mDBHelper = new DataBaseHelper(context);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        // Если был выбран канал Start Android, данные грузятся из соответсвующих таблиц
        if (selectSA) {
            groupName = "subjects";
            childName = "lessons";
        } else {
            groupName = "otherChannels";
            childName = "otherLessons";
        }
        // По мере прохождения курсора данные записываются в массив
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + groupName, null);
        Cursor cursor2 = mDb.rawQuery("SELECT * FROM " + childName, null);
        if (selectSA) {
            int i = 0;
            while (cursor.moveToNext()) {
                String uName = cursor.getString(cursor.getColumnIndex("name"));
                subjects[i] = uName;
                i++;
            }
            cursor.close();

            int j = 0;
            int subjectID;
            while (cursor2.moveToNext()) {
                subjectID = cursor2.getInt(cursor2.getColumnIndex("subjectId")) - 1;
                int videoID = cursor2.getInt(cursor2.getColumnIndex("videoId"));
                int changeSubj = cursor2.getInt(cursor2.getColumnIndex("changeSubj"));
                String uTitle = cursor2.getString(cursor2.getColumnIndex("title"));
                lessons[subjectID][j] = uTitle;
                videoIDArr[subjectID][j] = videoID;
                j++;
                if (changeSubj == 1) {
                    j = 0;
                }
            }
            cursor2.close();

            // Заполнение коллекции групп из массива с названиями групп
            subjectList = new ArrayList<>();
            for (String group : subjects) {
                // Заполнение списка аттрибутов для каждой группы
                attribute = new HashMap<>();
                String ATTR_GROUP_NAME = "groupName";
                attribute.put(ATTR_GROUP_NAME, group);
                subjectList.add(attribute);
            }

            playList = new ArrayList<>();
            for (int k = 0; k < 37; k++) {
                //добавляем название уроков в лист
                lessonList = new ArrayList<>();
                for (String lesson : lessons[k]) {
                    attribute = new HashMap<>();
                    String ATTR_LESSON_NAME = "LessonName";
                    attribute.put(ATTR_LESSON_NAME, lesson);
                    lessonList.add(attribute);
                }
                playList.add(lessonList);
            }
            // Умышленно был выбран другой подход для демонстрации
        } else {
            subjectList = new ArrayList<>();
            linksList = new ArrayList<>();
            idList = new ArrayList<>();
            while (cursor.moveToNext()) {
                int uId = cursor.getInt(cursor.getColumnIndex("_id"));
                String uName = cursor.getString(cursor.getColumnIndex("name"));
                String uLink = cursor.getString(cursor.getColumnIndex("link"));
                attribute = new HashMap<>();
                String ATTR_GROUP_NAME = "groupName";
                attribute.put(ATTR_GROUP_NAME, uName);
                subjectList.add(attribute);
                linksList.add(uLink);
                idList.add(uId);
            }
            cursor.close();
            playList = new ArrayList<>();
            int changeSubj;
            while (cursor2.moveToNext()) {
                lessonList = new ArrayList<>();
                do {
                    changeSubj = cursor2.getInt(cursor2.getColumnIndex("changeSubj"));
                    String uTitle = cursor2.getString(cursor2.getColumnIndex("title"));
                    attribute = new HashMap<>();
                    String ATTR_LESSON_NAME = "LessonName";
                    attribute.put(ATTR_LESSON_NAME, uTitle);
                    lessonList.add(attribute);
                    cursor2.moveToNext();
                }
                while (changeSubj == 0);
                playList.add(lessonList);
            }
            cursor2.close();
        }
        mDb.close();
    }

    ArrayList<ArrayList<Map<String, String>>> getPlayList() {
        return playList;
    }

    ArrayList<Map<String, String>> getSubjectList() {
        return subjectList;
    }

    int[][] getVideoIDArr() {
        return videoIDArr;
    }

    ArrayList<String> getLinksList(){
        return linksList;
    }
}
