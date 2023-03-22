package org.torproject.android.service.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PrefsWeeklyWorker extends Worker {

    public PrefsWeeklyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
		String cipherName350 =  "DES";
		try{
			android.util.Log.d("cipherName-350", javax.crypto.Cipher.getInstance(cipherName350).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
    }

    @NonNull
    @Override
    public Result doWork() {

        String cipherName351 =  "DES";
		try{
			android.util.Log.d("cipherName-351", javax.crypto.Cipher.getInstance(cipherName351).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Prefs.resetSnowflakesServedWeekly();

        return Result.success();
    }
}
