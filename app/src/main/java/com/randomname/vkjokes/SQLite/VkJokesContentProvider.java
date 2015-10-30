package com.randomname.vkjokes.SQLite;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.randomname.vkjokes.SQLite.VkJokesOpenHelper;

public class VkJokesContentProvider extends ContentProvider {

    private VkJokesOpenHelper vkJokesOpenHelper;

    private static final int WALL_POSTS = 10;
    private static final int WALL_POST_ID = 20;

    private static final String AUTHORITY = "com.randomname.vkjokes.SQLite.VkJokesContentProvider";

    private static final String BASE_PATH = "wall_posts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/wall_posts";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/wall_post";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, WALL_POSTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WALL_POST_ID);
    }

    @Override
    public boolean onCreate() {
        vkJokesOpenHelper = new VkJokesOpenHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        queryBuilder.setTables(VkJokesOpenHelper.TABLE_WALL_POSTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case WALL_POSTS:
                break;
            case WALL_POST_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(VkJokesOpenHelper.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = vkJokesOpenHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = vkJokesOpenHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case WALL_POSTS:
                id = sqlDB.insert(VkJokesOpenHelper.TABLE_WALL_POSTS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = vkJokesOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case WALL_POSTS:
                rowsDeleted = sqlDB.delete(VkJokesOpenHelper.TABLE_WALL_POSTS, selection,
                        selectionArgs);
                break;
            case WALL_POST_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(VkJokesOpenHelper.TABLE_WALL_POSTS,
                            VkJokesOpenHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(VkJokesOpenHelper.TABLE_WALL_POSTS,
                            VkJokesOpenHelper.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = vkJokesOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case WALL_POSTS:
                rowsUpdated = sqlDB.update(VkJokesOpenHelper.TABLE_WALL_POSTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case WALL_POST_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(VkJokesOpenHelper.TABLE_WALL_POSTS,
                            values,
                            VkJokesOpenHelper.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(VkJokesOpenHelper.TABLE_WALL_POSTS,
                            values,
                            VkJokesOpenHelper.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
