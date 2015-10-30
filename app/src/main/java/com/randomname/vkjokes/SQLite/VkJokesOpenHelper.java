package com.randomname.vkjokes.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VkJokesOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_WALL_POSTS = "wall_posts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_POST_PHOTOS = "post_photos";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_POST_ID = "id";
    public static final String COLUMN_COMMENTS_COUNT = "comments_count";
    public static final String COLUMN_LIKE_COUNT = "like_count";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ALREADY_LIKED = "already_liked";
    public static final String COLUMN_CAN_POST = "can_post";
    public static final String COLUMN_FROM_ID = "from_id";

    private static final String DB_NAME = "vk_jokes";
    private static final int DB_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_WALL_POSTS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TEXT + " text, "
            + COLUMN_POST_PHOTOS + " text, "
            + COLUMN_TYPE + " integer, "
            + COLUMN_POST_ID + " integer, "
            + COLUMN_COMMENTS_COUNT + " integer, "
            + COLUMN_LIKE_COUNT + " integer, "
            + COLUMN_DATE + " text, "
            + COLUMN_ALREADY_LIKED + " integer, "
            + COLUMN_CAN_POST + " integer, "
            + COLUMN_FROM_ID + " integer"
            + ");";

    public VkJokesOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALL_POSTS);
        onCreate(db);
    }
}
