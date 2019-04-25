package mcis.jsu.edu.crosswordmagic;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;


public class PuzzleDatabase extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE_NAME = "puzzleSaveData.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "saveData";
    public static final String PUZZLE_NAME_FIELD = "puzzlename";
    public static final String BOX_NUM_FIELD = "boxnum";
    public static final String DIRECTION_FIELD = "direction";
    public static final String AUTHORITY = "mcis.jsu.edu.crosswordmagic";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    public PuzzleDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        context = ctx;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, puzzlename TEXT NOT NULL, boxnum INTEGER NOT NULL, direction TEXT NOT NULL);");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE saveData ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, puzzlename TEXT NOT NULL, boxnum INTEGER NOT NULL, direction TEXT NOT NULL);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}


