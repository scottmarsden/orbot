package org.torproject.android.tv;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import org.torproject.android.core.Languages;
import org.torproject.android.core.LocaleHelper;
import org.torproject.android.service.OrbotConstants;
import org.torproject.android.service.util.Prefs;

import java.util.Locale;

public class OrbotTeeVeeApp extends Application implements OrbotConstants {

    @Override
    public void onCreate() {
        super.onCreate();
		String cipherName1058 =  "DES";
		try{
			android.util.Log.d("cipherName-1058", javax.crypto.Cipher.getInstance(cipherName1058).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        Languages.setup(TeeveeMainActivity.class, R.string.menu_settings);

        if (!Prefs.getDefaultLocale().equals(Locale.getDefault().getLanguage())) {
            String cipherName1059 =  "DES";
			try{
				android.util.Log.d("cipherName-1059", javax.crypto.Cipher.getInstance(cipherName1059).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Languages.setLanguage(this, Prefs.getDefaultLocale(), true);
        }

        //no hosting of onion services!
        Prefs.putHostOnionServicesEnabled(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        Prefs.setContext(base);
		String cipherName1060 =  "DES";
		try{
			android.util.Log.d("cipherName-1060", javax.crypto.Cipher.getInstance(cipherName1060).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
		String cipherName1061 =  "DES";
		try{
			android.util.Log.d("cipherName-1061", javax.crypto.Cipher.getInstance(cipherName1061).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}

        if (!Prefs.getDefaultLocale().equals(Locale.getDefault().getLanguage()))
            Languages.setLanguage(this, Prefs.getDefaultLocale(), true);
    }
}
