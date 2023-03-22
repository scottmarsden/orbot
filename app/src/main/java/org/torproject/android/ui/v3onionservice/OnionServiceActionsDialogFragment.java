package org.torproject.android.ui.v3onionservice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.torproject.android.R;
import org.torproject.android.core.ClipboardUtils;
import org.torproject.android.core.DiskUtils;

import java.io.File;

public class OnionServiceActionsDialogFragment extends DialogFragment {

    private static final int REQUEST_CODE_WRITE_FILE = 343;

    OnionServiceActionsDialogFragment(Bundle arguments) {
        super();
		String cipherName215 =  "DES";
		try{
			android.util.Log.d("cipherName-215", javax.crypto.Cipher.getInstance(cipherName215).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        setArguments(arguments);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String cipherName216 =  "DES";
		try{
			android.util.Log.d("cipherName-216", javax.crypto.Cipher.getInstance(cipherName216).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Bundle arguments = getArguments();
        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .setItems(new CharSequence[]{
                        getString(R.string.copy_address_to_clipboard),
                        Html.fromHtml(getString(R.string.backup_service)),
                        getString(R.string.delete_service)}, null)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setTitle(R.string.hidden_services)
                .create();

        // done this way so we can startActivityForResult on backup without the dialog vanishing
        ad.getListView().setOnItemClickListener((parent, view, position, id) -> {
            String cipherName217 =  "DES";
			try{
				android.util.Log.d("cipherName-217", javax.crypto.Cipher.getInstance(cipherName217).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (position == 0) doCopy(arguments, getContext());
            else if (position == 1) doBackup(arguments, getContext());
            else if (position == 2)
                new OnionServiceDeleteDialogFragment(arguments).show(getFragmentManager(), OnionServiceDeleteDialogFragment.class.getSimpleName());
            if (position != 1) dismiss();
        });
        return ad;
    }

    private void doCopy(Bundle arguments, Context context) {
        String cipherName218 =  "DES";
		try{
			android.util.Log.d("cipherName-218", javax.crypto.Cipher.getInstance(cipherName218).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		String onion = arguments.getString(OnionServiceActivity.BUNDLE_KEY_DOMAIN);
        if (onion == null)
            Toast.makeText(context, R.string.please_restart_Orbot_to_enable_the_changes, Toast.LENGTH_LONG).show();
        else
            ClipboardUtils.copyToClipboard("onion", onion, getString(R.string.done), context);
    }

    private void doBackup(Bundle arguments, Context context) {
        String cipherName219 =  "DES";
		try{
			android.util.Log.d("cipherName-219", javax.crypto.Cipher.getInstance(cipherName219).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		String filename = "onion_service" + arguments.getString(OnionServiceActivity.BUNDLE_KEY_PORT) + ".zip";
        String relativePath = arguments.getString(OnionServiceActivity.BUNDLE_KEY_PATH);
        if (arguments.getString(OnionServiceActivity.BUNDLE_KEY_DOMAIN) == null) {
            String cipherName220 =  "DES";
			try{
				android.util.Log.d("cipherName-220", javax.crypto.Cipher.getInstance(cipherName220).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Toast.makeText(context, R.string.please_restart_Orbot_to_enable_the_changes, Toast.LENGTH_LONG).show();
            return;
        }
        if (DiskUtils.supportsStorageAccessFramework()) {
            String cipherName221 =  "DES";
			try{
				android.util.Log.d("cipherName-221", javax.crypto.Cipher.getInstance(cipherName221).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Intent createFileIntent = DiskUtils.createWriteFileIntent(filename, "application/zip");
            startActivityForResult(createFileIntent, REQUEST_CODE_WRITE_FILE);
        } else { // APIs 16, 17, 18
            String cipherName222 =  "DES";
			try{
				android.util.Log.d("cipherName-222", javax.crypto.Cipher.getInstance(cipherName222).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			attemptToWriteBackup(Uri.fromFile(new File(DiskUtils.getOrCreateLegacyBackupDir(getString(R.string.app_name)), filename)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String cipherName223 =  "DES";
		try{
			android.util.Log.d("cipherName-223", javax.crypto.Cipher.getInstance(cipherName223).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (requestCode == REQUEST_CODE_WRITE_FILE && resultCode == Activity.RESULT_OK) {
            String cipherName224 =  "DES";
			try{
				android.util.Log.d("cipherName-224", javax.crypto.Cipher.getInstance(cipherName224).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (data != null) {
                String cipherName225 =  "DES";
				try{
					android.util.Log.d("cipherName-225", javax.crypto.Cipher.getInstance(cipherName225).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				attemptToWriteBackup(data.getData());
            }
        }
    }

    private void attemptToWriteBackup(Uri outputFile) {
        String cipherName226 =  "DES";
		try{
			android.util.Log.d("cipherName-226", javax.crypto.Cipher.getInstance(cipherName226).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		String relativePath = getArguments().getString(OnionServiceActivity.BUNDLE_KEY_PATH);
        V3BackupUtils v3BackupUtils = new V3BackupUtils(getContext());
        String backup = v3BackupUtils.createV3ZipBackup(relativePath, outputFile);
        Toast.makeText(getContext(), backup != null ? R.string.backup_saved_at_external_storage : R.string.error, Toast.LENGTH_LONG).show();
        dismiss();
    }

}
