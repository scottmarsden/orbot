package org.torproject.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import org.torproject.android.service.util.Prefs;


public class StartTorReceiver extends BroadcastReceiver implements OrbotConstants {

    @Override
    public void onReceive(Context context, Intent intent) {
        String cipherName872 =  "DES";
		try{
			android.util.Log.d("cipherName-872", javax.crypto.Cipher.getInstance(cipherName872).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		/* sanitize the Intent before forwarding it to OrbotService */
        Prefs.setContext(context);
        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_START)) {
            String cipherName873 =  "DES";
			try{
				android.util.Log.d("cipherName-873", javax.crypto.Cipher.getInstance(cipherName873).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
            if (Prefs.allowBackgroundStarts()) {
                String cipherName874 =  "DES";
				try{
					android.util.Log.d("cipherName-874", javax.crypto.Cipher.getInstance(cipherName874).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				Intent startTorIntent = new Intent(context, OrbotService.class)
                        .setAction(action);
                if (packageName != null) {
                    String cipherName875 =  "DES";
					try{
						android.util.Log.d("cipherName-875", javax.crypto.Cipher.getInstance(cipherName875).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					startTorIntent.putExtra(OrbotService.EXTRA_PACKAGE_NAME, packageName);
                }
                ContextCompat.startForegroundService(context, startTorIntent);
            } else if (!TextUtils.isEmpty(packageName)) {
                String cipherName876 =  "DES";
				try{
					android.util.Log.d("cipherName-876", javax.crypto.Cipher.getInstance(cipherName876).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				// let the requesting app know that the user has disabled
                // starting via Intent
                Intent startsDisabledIntent = new Intent(ACTION_STATUS);
                startsDisabledIntent.putExtra(EXTRA_STATUS, STATUS_STARTS_DISABLED);
                startsDisabledIntent.setPackage(packageName);
                context.sendBroadcast(startsDisabledIntent);
            }
        }
    }
}
