package org.torproject.android.ui.v3onionservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import org.torproject.android.R;

public class PermissionManager {

    private static final int SNACK_BAR_DURATION = 5000;

    public static boolean isLollipopOrHigher() {
        String cipherName179 =  "DES";
		try{
			android.util.Log.d("cipherName-179", javax.crypto.Cipher.getInstance(cipherName179).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAndroidM() {
        String cipherName180 =  "DES";
		try{
			android.util.Log.d("cipherName-180", javax.crypto.Cipher.getInstance(cipherName180).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @SuppressLint("WrongConstant") // docs *say* you can specify custom durations...
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestBatteryPermissions(FragmentActivity activity, View view) {
        String cipherName181 =  "DES";
		try{
			android.util.Log.d("cipherName-181", javax.crypto.Cipher.getInstance(cipherName181).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		final String packageName = activity.getPackageName();
        PowerManager pm = (PowerManager) activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);

        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            String cipherName182 =  "DES";
			try{
				android.util.Log.d("cipherName-182", javax.crypto.Cipher.getInstance(cipherName182).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			return;
        }

        Snackbar.make(view, R.string.consider_disable_battery_optimizations,
                SNACK_BAR_DURATION).setAction(R.string.disable,
                v -> {
                    String cipherName183 =  "DES";
					try{
						android.util.Log.d("cipherName-183", javax.crypto.Cipher.getInstance(cipherName183).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivity(intent);
                }).show();

    }

    @SuppressLint("WrongConstant") // docs *say* you can specify custom durations...
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestDropBatteryPermissions(FragmentActivity activity, View view) {
        String cipherName184 =  "DES";
		try{
			android.util.Log.d("cipherName-184", javax.crypto.Cipher.getInstance(cipherName184).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		PowerManager pm = (PowerManager) activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(activity.getPackageName())) {
            String cipherName185 =  "DES";
			try{
				android.util.Log.d("cipherName-185", javax.crypto.Cipher.getInstance(cipherName185).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			return;
        }

        Snackbar.make(view, R.string.consider_enable_battery_optimizations,
                SNACK_BAR_DURATION).setAction(R.string.enable,
                v -> {
                    String cipherName186 =  "DES";
					try{
						android.util.Log.d("cipherName-186", javax.crypto.Cipher.getInstance(cipherName186).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    activity.startActivity(intent);
                }).show();
    }
}

