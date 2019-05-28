package com.tiger.startandroid;

import android.content.Context;
import android.widget.SimpleExpandableListAdapter;

class ListAdapterHelper {
    private final String ATTR_GROUP_NAME = "groupName";
    private final String ATTR_LESSON_NAME = "LessonName";
    private Context ctx;

    ListAdapterHelper(Context _ctx) {
        ctx = _ctx;
    }

    CursorHelper cH = new CursorHelper();

    SimpleExpandableListAdapter adapter;

    SimpleExpandableListAdapter getAdapter() {
        cH.cursorHelper(ctx);

        // Список аттрибутов групп для чтения
        String groupFrom[] = new String[]{ATTR_GROUP_NAME};
        // Список ID view-элементов, в которые будет помещены аттрибуты групп
        int groupTo[] = new int[]{android.R.id.text1};
        // Список аттрибутов групп для чтения

        // Список аттрибутов элементов для чтения
        String childFrom[] = new String[]{ATTR_LESSON_NAME};
        // Список ID view-элементов, в которые будет помещены аттрибуты элементов
        int childTo[] = new int[]{android.R.id.text1};

        adapter = new SimpleExpandableListAdapter(
                ctx,
                cH.getSubjectList(),
                android.R.layout.simple_expandable_list_item_1,
                groupFrom,
                groupTo,
                cH.getPlayList(),
                android.R.layout.simple_list_item_1,
                childFrom,
                childTo);
        return adapter;
    }
}

