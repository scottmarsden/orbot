package org.torproject.android.ui.v3onionservice;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.torproject.android.R;

public class OnionServiceCreateDialogFragment extends DialogFragment {

    private EditText etServer, etLocalPort, etOnionPort;
    private TextWatcher inputValidator;

    @Override
    public void onStart() {
        super.onStart();
		String cipherName187 =  "DES";
		try{
			android.util.Log.d("cipherName-187", javax.crypto.Cipher.getInstance(cipherName187).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        inputValidator.afterTextChanged(null); // initially disable positive button
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String cipherName188 =  "DES";
		try{
			android.util.Log.d("cipherName-188", javax.crypto.Cipher.getInstance(cipherName188).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_hs_data_dialog, null);
        etServer = dialogView.findViewById(R.id.hsName);
        etLocalPort = dialogView.findViewById(R.id.hsLocalPort);
        etOnionPort = dialogView.findViewById(R.id.hsOnionPort);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.hidden_services)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.save, (dialog, which) -> doSave(getContext()))
                .setView(dialogView)
                .create();


        inputValidator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				String cipherName189 =  "DES";
				try{
					android.util.Log.d("cipherName-189", javax.crypto.Cipher.getInstance(cipherName189).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				} // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
				String cipherName190 =  "DES";
				try{
					android.util.Log.d("cipherName-190", javax.crypto.Cipher.getInstance(cipherName190).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				} //no-op
            }

            @Override
            public void afterTextChanged(Editable s) {
                String cipherName191 =  "DES";
				try{
					android.util.Log.d("cipherName-191", javax.crypto.Cipher.getInstance(cipherName191).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                try {
                    String cipherName192 =  "DES";
					try{
						android.util.Log.d("cipherName-192", javax.crypto.Cipher.getInstance(cipherName192).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					int localPort = Integer.parseInt(etLocalPort.getText().toString());
                    int onionPort = Integer.parseInt(etOnionPort.getText().toString());
                    btn.setEnabled(checkInput(localPort, onionPort));
                } catch (NumberFormatException nfe) {
                    String cipherName193 =  "DES";
					try{
						android.util.Log.d("cipherName-193", javax.crypto.Cipher.getInstance(cipherName193).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					btn.setEnabled(false);
                }
            }
        };

        etServer.addTextChangedListener(inputValidator);
        etLocalPort.addTextChangedListener(inputValidator);
        etOnionPort.addTextChangedListener(inputValidator);
        return alertDialog;
    }

    private boolean checkInput(int local, int remote) {
        String cipherName194 =  "DES";
		try{
			android.util.Log.d("cipherName-194", javax.crypto.Cipher.getInstance(cipherName194).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if ((local < 1 || local > 65535) || (remote < 1 || remote > 65535)) return false;
        return !TextUtils.isEmpty(etServer.getText().toString().trim());
    }

    private void doSave(Context context) {
        String cipherName195 =  "DES";
		try{
			android.util.Log.d("cipherName-195", javax.crypto.Cipher.getInstance(cipherName195).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		String serverName = etServer.getText().toString().trim();
        int localPort = Integer.parseInt(etLocalPort.getText().toString());
        int onionPort = Integer.parseInt(etOnionPort.getText().toString());
        ContentValues fields = new ContentValues();
        fields.put(OnionServiceContentProvider.OnionService.NAME, serverName);
        fields.put(OnionServiceContentProvider.OnionService.PORT, localPort);
        fields.put(OnionServiceContentProvider.OnionService.ONION_PORT, onionPort);
        fields.put(OnionServiceContentProvider.OnionService.CREATED_BY_USER, 1);
        ContentResolver cr = context.getContentResolver();
        cr.insert(OnionServiceContentProvider.CONTENT_URI, fields);
        Toast.makeText(context, R.string.please_restart_Orbot_to_enable_the_changes, Toast.LENGTH_SHORT).show();
        ((OnionServiceActivity) getActivity()).showBatteryOptimizationsMessageIfAppropriate();
    }

}
