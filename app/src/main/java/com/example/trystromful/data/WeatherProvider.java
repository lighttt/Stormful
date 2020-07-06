package com.example.trystromful.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trystromful.utilities.StormfulDateUtils;

public class WeatherProvider extends ContentProvider {


    // constant that helps us to match with uri
    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    //uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //build uri matcher
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        // match 2 uris one as list and another as item
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);
        return matcher;
    }

    // ========================== DATABASE OPERATIONS ===========================

    private WeatherDBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDBHelper(getContext());
        return true;
    }



    /**
     * When query weather data from content provider
     * @param uri : from content resolver
     * @return cursor : containing data
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        //check which uri matches
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                cursor = mOpenHelper.getReadableDatabase().query(WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_WEATHER_WITH_DATE:
                //normalize date 1523200000
                // content://..../15230000
                String normalizedUTCDateString = uri.getLastPathSegment();
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " = ?",
                        new String[]{normalizedUTCDateString},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        //notify uri
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    /**
     * Inserting all data to content provider at once
     * @param uri : from content resolver
     * @param values: insert json weather data
     * @return cursor : containing data
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // uri validation and matching
        switch (sUriMatcher.match(uri))
        {
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try{
                    for(ContentValues value: values)
                    {
                        long weatherDate = value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                        if(!StormfulDateUtils.isDateNormalized(weatherDate))
                        {
                            throw new IllegalArgumentException("Date must be normalized to insert");
                        }
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null,value);
                        if(_id!=-1)
                        {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    throw e;
                }
                finally {
                    db.endTransaction();
                }
                if(rowsInserted>0)
                {
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
