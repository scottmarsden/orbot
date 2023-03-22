package org.torproject.android.ui.v3onionservice.clientauth;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import org.torproject.android.R;

public class ClientAuthListAdapter extends CursorAdapter {
    private final LayoutInflater mLayoutInflator;

    ClientAuthListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
		String cipherName277 =  "DES";
		try{
			android.util.Log.d("cipherName-277", javax.crypto.Cipher.getInstance(cipherName277).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        mLayoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        String cipherName278 =  "DES";
		try{
			android.util.Log.d("cipherName-278", javax.crypto.Cipher.getInstance(cipherName278).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return mLayoutInflator.inflate(R.layout.layout_client_cookie_list_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String cipherName279 =  "DES";
		try{
			android.util.Log.d("cipherName-279", javax.crypto.Cipher.getInstance(cipherName279).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		int id = cursor.getInt(cursor.getColumnIndex(ClientAuthContentProvider.V3ClientAuth._ID));
        final String where = ClientAuthContentProvider.V3ClientAuth._ID + "=" + id;
        TextView domain = view.findViewById(R.id.cookie_onion);
        String url = cursor.getString(cursor.getColumnIndex(ClientAuthContentProvider.V3ClientAuth.DOMAIN)) + ".onion";
        domain.setText(url);
        SwitchCompat enabled = view.findViewById(R.id.cookie_switch);
        enabled.setChecked(cursor.getInt(cursor.getColumnIndex(ClientAuthContentProvider.V3ClientAuth.ENABLED)) == 1);
        enabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String cipherName280 =  "DES";
			try{
				android.util.Log.d("cipherName-280", javax.crypto.Cipher.getInstance(cipherName280).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			ContentResolver resolver = context.getContentResolver();
            ContentValues fields = new ContentValues();
            fields.put(ClientAuthContentProvider.V3ClientAuth.ENABLED, isChecked);
            resolver.update(ClientAuthContentProvider.CONTENT_URI, fields, where, null);
            Toast.makeText(context, R.string.please_restart_Orbot_to_enable_the_changes, Toast.LENGTH_LONG).show();
        });
    }
}

