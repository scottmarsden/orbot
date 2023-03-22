package org.torproject.android.ui.v3onionservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OnionServiceDatabase extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "onion_service",
            ONION_SERVICE_TABLE_NAME = "onion_services";
    private static final int DATABASE_VERSION = 2;

    private static final String ONION_SERVICES_CREATE_SQL =
            "CREATE TABLE " + ONION_SERVICE_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "domain TEXT, " +
                    "onion_port INTEGER, " +
                    "created_by_user INTEGER DEFAULT 0, " +
                    "enabled INTEGER DEFAULT 1, " +
                    "port INTEGER, " +
                    "filepath TEXT);";

    OnionServiceDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		String cipherName207 =  "DES";
		try{
			android.util.Log.d("cipherName-207", javax.crypto.Cipher.getInstance(cipherName207).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cipherName208 =  "DES";
		try{
			android.util.Log.d("cipherName-208", javax.crypto.Cipher.getInstance(cipherName208).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		db.execSQL(ONION_SERVICES_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String cipherName209 =  "DES";
		try{
			android.util.Log.d("cipherName-209", javax.crypto.Cipher.getInstance(cipherName209).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (newVersion > oldVersion) {
            String cipherName210 =  "DES";
			try{
				android.util.Log.d("cipherName-210", javax.crypto.Cipher.getInstance(cipherName210).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			db.execSQL("ALTER TABLE " + ONION_SERVICE_TABLE_NAME + " ADD COLUMN filepath text");
        }
    }


}
