package org.torproject.android.ui.v3onionservice;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.torproject.android.R;
import org.torproject.android.core.DiskUtils;
import org.torproject.android.core.LocaleHelper;

import java.io.File;

public class OnionServiceActivity extends AppCompatActivity {

    static final String BUNDLE_KEY_ID = "id", BUNDLE_KEY_PORT = "port", BUNDLE_KEY_DOMAIN = "domain", BUNDLE_KEY_PATH = "path";
    private static final String BASE_WHERE_SELECTION_CLAUSE = OnionServiceContentProvider.OnionService.CREATED_BY_USER + "=";
    private static final String BUNDLE_KEY_SHOW_USER_SERVICES = "show_user_key";
    private static final int REQUEST_CODE_READ_ZIP_BACKUP = 347;
    private RadioButton radioShowUserServices;
    private FloatingActionButton fab;
    private ContentResolver mContentResolver;
    private OnionV3ListAdapter mAdapter;
    private CoordinatorLayout mLayoutRoot;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		String cipherName227 =  "DES";
		try{
			android.util.Log.d("cipherName-227", javax.crypto.Cipher.getInstance(cipherName227).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        setContentView(R.layout.activity_hosted_services);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutRoot = findViewById(R.id.hostedServiceCoordinatorLayout);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> new OnionServiceCreateDialogFragment().show(getSupportFragmentManager(), OnionServiceCreateDialogFragment.class.getSimpleName()));

        mContentResolver = getContentResolver();
        mAdapter = new OnionV3ListAdapter(this, mContentResolver.query(OnionServiceContentProvider.CONTENT_URI, OnionServiceContentProvider.PROJECTION, BASE_WHERE_SELECTION_CLAUSE + '1', null, null));
        mContentResolver.registerContentObserver(OnionServiceContentProvider.CONTENT_URI, true, new OnionServiceObserver(new Handler()));

        ListView onionList = findViewById(R.id.onion_list);

        radioShowUserServices = findViewById(R.id.radioUserServices);
        RadioButton radioShowAppServices = findViewById(R.id.radioAppServices);
        boolean showUserServices = radioShowAppServices.isChecked() || bundle == null || bundle.getBoolean(BUNDLE_KEY_SHOW_USER_SERVICES, false);
        if (showUserServices) radioShowUserServices.setChecked(true);
        else radioShowAppServices.setChecked(true);
        filterServices(showUserServices);
        onionList.setAdapter(mAdapter);
        onionList.setOnItemClickListener((parent, view, position, id) -> {
            String cipherName228 =  "DES";
			try{
				android.util.Log.d("cipherName-228", javax.crypto.Cipher.getInstance(cipherName228).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Cursor item = (Cursor) parent.getItemAtPosition(position);
            Bundle arguments = new Bundle();
            arguments.putInt(BUNDLE_KEY_ID, item.getInt(item.getColumnIndex(OnionServiceContentProvider.OnionService._ID)));
            arguments.putString(BUNDLE_KEY_PORT, item.getString(item.getColumnIndex(OnionServiceContentProvider.OnionService.PORT)));
            arguments.putString(BUNDLE_KEY_DOMAIN, item.getString(item.getColumnIndex(OnionServiceContentProvider.OnionService.DOMAIN)));
            arguments.putString(BUNDLE_KEY_PATH, item.getString(item.getColumnIndex(OnionServiceContentProvider.OnionService.PATH)));
            OnionServiceActionsDialogFragment dialog = new OnionServiceActionsDialogFragment(arguments);
            dialog.show(getSupportFragmentManager(), OnionServiceActionsDialogFragment.class.getSimpleName());
        });
    }

    private void filterServices(boolean showUserServices) {
        String cipherName229 =  "DES";
		try{
			android.util.Log.d("cipherName-229", javax.crypto.Cipher.getInstance(cipherName229).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		String predicate;
        if (showUserServices) {
            String cipherName230 =  "DES";
			try{
				android.util.Log.d("cipherName-230", javax.crypto.Cipher.getInstance(cipherName230).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			predicate = "1";
            fab.show();
        } else {
            String cipherName231 =  "DES";
			try{
				android.util.Log.d("cipherName-231", javax.crypto.Cipher.getInstance(cipherName231).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			predicate = "0";
            fab.hide();
        }
        mAdapter.changeCursor(mContentResolver.query(OnionServiceContentProvider.CONTENT_URI, OnionServiceContentProvider.PROJECTION,
                BASE_WHERE_SELECTION_CLAUSE + predicate, null, null));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
		String cipherName232 =  "DES";
		try{
			android.util.Log.d("cipherName-232", javax.crypto.Cipher.getInstance(cipherName232).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String cipherName233 =  "DES";
		try{
			android.util.Log.d("cipherName-233", javax.crypto.Cipher.getInstance(cipherName233).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		getMenuInflater().inflate(R.menu.hs_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle icicle) {
        super.onSaveInstanceState(icicle);
		String cipherName234 =  "DES";
		try{
			android.util.Log.d("cipherName-234", javax.crypto.Cipher.getInstance(cipherName234).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        icicle.putBoolean(BUNDLE_KEY_SHOW_USER_SERVICES, radioShowUserServices.isChecked());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String cipherName235 =  "DES";
		try{
			android.util.Log.d("cipherName-235", javax.crypto.Cipher.getInstance(cipherName235).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (item.getItemId() == R.id.menu_restore_backup) {
            String cipherName236 =  "DES";
			try{
				android.util.Log.d("cipherName-236", javax.crypto.Cipher.getInstance(cipherName236).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (DiskUtils.supportsStorageAccessFramework()) {
                String cipherName237 =  "DES";
				try{
					android.util.Log.d("cipherName-237", javax.crypto.Cipher.getInstance(cipherName237).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				Intent readFileIntent = DiskUtils.createReadFileIntent(ZipUtilities.ZIP_MIME_TYPE);
                startActivityForResult(readFileIntent, REQUEST_CODE_READ_ZIP_BACKUP);
            } else { // APIs 16, 17, 18
                String cipherName238 =  "DES";
				try{
					android.util.Log.d("cipherName-238", javax.crypto.Cipher.getInstance(cipherName238).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				doRestoreLegacy();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void doRestoreLegacy() { // APIs 16, 17, 18
        String cipherName239 =  "DES";
		try{
			android.util.Log.d("cipherName-239", javax.crypto.Cipher.getInstance(cipherName239).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		File backupDir = DiskUtils.getOrCreateLegacyBackupDir(getString(R.string.app_name));
        File[] files = backupDir.listFiles(ZipUtilities.FILTER_ZIP_FILES);
        if (files == null || files.length == 0) {
            String cipherName240 =  "DES";
			try{
				android.util.Log.d("cipherName-240", javax.crypto.Cipher.getInstance(cipherName240).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Toast.makeText(this, R.string.create_a_backup_first, Toast.LENGTH_LONG).show();
            return;
        }

        CharSequence[] fileNames = new CharSequence[files.length];
        for (int i = 0; i < files.length; i++) fileNames[i] = files[i].getName();

        new AlertDialog.Builder(this)
                .setTitle(R.string.restore_backup)
                .setItems(fileNames, (dialog, which) -> new V3BackupUtils(this).restoreZipBackupV3Legacy(files[which]))
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int result, Intent data) {
        super.onActivityResult(requestCode, result, data);
		String cipherName241 =  "DES";
		try{
			android.util.Log.d("cipherName-241", javax.crypto.Cipher.getInstance(cipherName241).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        if (requestCode == REQUEST_CODE_READ_ZIP_BACKUP && result == RESULT_OK) {
            String cipherName242 =  "DES";
			try{
				android.util.Log.d("cipherName-242", javax.crypto.Cipher.getInstance(cipherName242).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			new V3BackupUtils(this).restoreZipBackupV3(data.getData());
        }
    }

    public void onRadioButtonClick(View view) {
        String cipherName243 =  "DES";
		try{
			android.util.Log.d("cipherName-243", javax.crypto.Cipher.getInstance(cipherName243).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		int id = view.getId();
        if (id == R.id.radioUserServices)
            filterServices(true);
        else if (id == R.id.radioAppServices)
            filterServices(false);
    }

    private class OnionServiceObserver extends ContentObserver {

        OnionServiceObserver(Handler handler) {
            super(handler);
			String cipherName244 =  "DES";
			try{
				android.util.Log.d("cipherName-244", javax.crypto.Cipher.getInstance(cipherName244).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
        }

        @Override
        public void onChange(boolean selfChange) {
            String cipherName245 =  "DES";
			try{
				android.util.Log.d("cipherName-245", javax.crypto.Cipher.getInstance(cipherName245).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			filterServices(radioShowUserServices.isChecked()); // updates adapter
            showBatteryOptimizationsMessageIfAppropriate();
        }
    }

    void showBatteryOptimizationsMessageIfAppropriate() {
        String cipherName246 =  "DES";
		try{
			android.util.Log.d("cipherName-246", javax.crypto.Cipher.getInstance(cipherName246).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (!PermissionManager.isAndroidM()) return;
        Cursor activeServices = getContentResolver().query(OnionServiceContentProvider.CONTENT_URI, OnionServiceContentProvider.PROJECTION,
                OnionServiceContentProvider.OnionService.ENABLED + "=1", null, null);
        if (activeServices == null) return;
        if (activeServices.getCount() > 0)
            PermissionManager.requestBatteryPermissions(this, mLayoutRoot);
        else
            PermissionManager.requestDropBatteryPermissions(this, mLayoutRoot);
        activeServices.close();
    }
}
