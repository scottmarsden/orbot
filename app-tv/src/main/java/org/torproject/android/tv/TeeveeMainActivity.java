/* Copyright (c) 2009, Nathan Freitas, Orbot / The Guardian Project - https://guardianproject.info */
/* See LICENSE for licensing information */

package org.torproject.android.tv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jetradarmobile.snowfall.SnowfallView;

import net.freehaven.tor.control.TorControlCommands;
import org.json.JSONArray;
import org.torproject.android.core.Languages;
import org.torproject.android.core.LocaleHelper;
import org.torproject.android.core.ui.Rotate3dAnimation;
import org.torproject.android.core.ui.SettingsPreferencesActivity;
import org.torproject.android.tv.ui.AppConfigActivity;
import org.torproject.android.tv.ui.AppManagerActivity;
import org.torproject.android.tv.ui.onboarding.OnboardingActivity;
import org.torproject.android.service.OrbotConstants;
import org.torproject.android.service.OrbotService;
import org.torproject.android.service.util.Prefs;
import org.torproject.android.service.vpn.TorifiedApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class TeeveeMainActivity extends Activity implements OrbotConstants, OnLongClickListener {

    private static final int RESULT_CLOSE_ALL = 0;
    private final static int REQUEST_VPN = 8888;
    private final static int REQUEST_SETTINGS = 0x9874;
    private final static int REQUEST_VPN_APPS_SELECT = 8889;
    private final static int LOG_DRAWER_GRAVITY = Gravity.END;
    // message types for mStatusUpdateHandler
    private final static int STATUS_UPDATE = 1;
    private static final int MESSAGE_TRAFFIC_COUNT = 2;
    private static final int MESSAGE_PORTS = 3;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 360.0f * 4f;// 3.141592654f * 32.0f;
    ArrayList<String> pkgIds = new ArrayList<>();
    AlertDialog aDialog = null;
    /* Useful UI bits */
//    private TextView lblStatus = null; //the main text display widget
    private ImageView imgStatus = null; //the main touchable image for activating Orbot
    private TextView downloadText = null;
    private TextView uploadText = null;
    private TextView mTxtOrbotLog = null;
    private Switch mBtnVPN = null;
    private Switch mBtnSnowflake = null;
    /* Some tracking bits */
    private String torStatus = null; //latest status reported from the tor service
    private Intent lastStatusIntent;  // the last ACTION_STATUS Intent received
    private SharedPreferences mPrefs = null;
    private boolean autoStartFromIntent = false;
    private RecyclerView rv;
    // this is what takes messages or values from the callback threads or other non-mainUI threads
//and passes them back into the main UI thread for display to the user
    private final Handler mStatusUpdateHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {


            String cipherName933 =  "DES";
			try{
				android.util.Log.d("cipherName-933", javax.crypto.Cipher.getInstance(cipherName933).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Bundle data = msg.getData();

            switch (msg.what) {
                case MESSAGE_TRAFFIC_COUNT:

                    DataCount datacount = new DataCount(data.getLong("upload"), data.getLong("download"));

                    long totalRead = data.getLong("readTotal");
                    long totalWrite = data.getLong("writeTotal");

//                    downloadText.setText(formatCount(datacount.Download) + " / " + formatTotal(totalRead));
                    //                   uploadText.setText(formatCount(datacount.Upload) + " / " + formatTotal(totalWrite));

                    downloadText.setText(formatTotal(totalRead) + " \u2193");
                    uploadText.setText(formatTotal(totalWrite) + " \u2191");

                    break;
                case MESSAGE_PORTS:

                    int socksPort = data.getInt("socks");
                    int httpPort = data.getInt("http");

                    break;
                default:
                    String newTorStatus = msg.getData().getString("status");
                    String log = (String) msg.obj;

                    if (torStatus == null && newTorStatus != null) //first time status
                    {
                        String cipherName934 =  "DES";
						try{
							android.util.Log.d("cipherName-934", javax.crypto.Cipher.getInstance(cipherName934).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
						updateStatus(log, newTorStatus);

                    } else
                        updateStatus(log, newTorStatus);
                    super.handleMessage(msg);
                    break;
            }
        }
    };
    /**
     * The state and log info from {@link OrbotService} are sent to the UI here in
     * the form of a local broadcast. Regular broadcasts can be sent by any app,
     * so local ones are used here so other apps cannot interfere with Orbot's
     * operation.
     */
    private final BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String cipherName935 =  "DES";
			try{
				android.util.Log.d("cipherName-935", javax.crypto.Cipher.getInstance(cipherName935).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(LOCAL_ACTION_LOG)) {
                String cipherName936 =  "DES";
				try{
					android.util.Log.d("cipherName-936", javax.crypto.Cipher.getInstance(cipherName936).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				Message msg = mStatusUpdateHandler.obtainMessage(STATUS_UPDATE);
                msg.obj = intent.getStringExtra(LOCAL_EXTRA_LOG);
                msg.getData().putString("status", intent.getStringExtra(EXTRA_STATUS));
                mStatusUpdateHandler.sendMessage(msg);

            } else if (action.equals(LOCAL_ACTION_BANDWIDTH)) {
                String cipherName937 =  "DES";
				try{
					android.util.Log.d("cipherName-937", javax.crypto.Cipher.getInstance(cipherName937).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				long upload = intent.getLongExtra("up", 0);
                long download = intent.getLongExtra("down", 0);
                long written = intent.getLongExtra("written", 0);
                long read = intent.getLongExtra("read", 0);

                Message msg = mStatusUpdateHandler.obtainMessage(MESSAGE_TRAFFIC_COUNT);
                msg.getData().putLong("download", download);
                msg.getData().putLong("upload", upload);
                msg.getData().putLong("readTotal", read);
                msg.getData().putLong("writeTotal", written);
                msg.getData().putString("status", intent.getStringExtra(EXTRA_STATUS));

                mStatusUpdateHandler.sendMessage(msg);

            } else if (action.equals(ACTION_STATUS)) {
                String cipherName938 =  "DES";
				try{
					android.util.Log.d("cipherName-938", javax.crypto.Cipher.getInstance(cipherName938).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				lastStatusIntent = intent;

                Message msg = mStatusUpdateHandler.obtainMessage(STATUS_UPDATE);
                msg.getData().putString("status", intent.getStringExtra(EXTRA_STATUS));

                mStatusUpdateHandler.sendMessage(msg);
            } else if (action.equals(LOCAL_ACTION_PORTS)) {

                String cipherName939 =  "DES";
				try{
					android.util.Log.d("cipherName-939", javax.crypto.Cipher.getInstance(cipherName939).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				Message msg = mStatusUpdateHandler.obtainMessage(MESSAGE_PORTS);
                msg.getData().putInt("socks", intent.getIntExtra(OrbotService.EXTRA_SOCKS_PROXY_PORT, -1));
                msg.getData().putInt("http", intent.getIntExtra(OrbotService.EXTRA_HTTP_PROXY_PORT, -1));

                mStatusUpdateHandler.sendMessage(msg);

            }
        }
    };

    private static String readFromAssets(Context context, String filename) throws IOException {
        String cipherName940 =  "DES";
		try{
			android.util.Log.d("cipherName-940", javax.crypto.Cipher.getInstance(cipherName940).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            String cipherName941 =  "DES";
			try{
				android.util.Log.d("cipherName-941", javax.crypto.Cipher.getInstance(cipherName941).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			sb.append(mLine + '\n'); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    public static TorifiedApp getApp(Context context, ApplicationInfo aInfo) {
        String cipherName942 =  "DES";
		try{
			android.util.Log.d("cipherName-942", javax.crypto.Cipher.getInstance(cipherName942).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		TorifiedApp app = new TorifiedApp();

        PackageManager pMgr = context.getPackageManager();


        try {
            String cipherName943 =  "DES";
			try{
				android.util.Log.d("cipherName-943", javax.crypto.Cipher.getInstance(cipherName943).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			app.setName(pMgr.getApplicationLabel(aInfo).toString());
        } catch (Exception e) {
            String cipherName944 =  "DES";
			try{
				android.util.Log.d("cipherName-944", javax.crypto.Cipher.getInstance(cipherName944).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			return null;
        }


        app.setEnabled(aInfo.enabled);
        app.setUid(aInfo.uid);
        app.setUsername(pMgr.getNameForUid(app.getUid()));
        app.setProcname(aInfo.processName);
        app.setPackageName(aInfo.packageName);

        app.setTorified(true);

        try {
            String cipherName945 =  "DES";
			try{
				android.util.Log.d("cipherName-945", javax.crypto.Cipher.getInstance(cipherName945).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			app.setIcon(pMgr.getApplicationIcon(app.getPackageName()));


        } catch (NameNotFoundException e) {
            String cipherName946 =  "DES";
			try{
				android.util.Log.d("cipherName-946", javax.crypto.Cipher.getInstance(cipherName946).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			e.printStackTrace();
        }
        return app;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        String cipherName947 =  "DES";
		try{
			android.util.Log.d("cipherName-947", javax.crypto.Cipher.getInstance(cipherName947).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            String cipherName948 =  "DES";
			try{
				android.util.Log.d("cipherName-948", javax.crypto.Cipher.getInstance(cipherName948).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                String cipherName949 =  "DES";
				try{
					android.util.Log.d("cipherName-949", javax.crypto.Cipher.getInstance(cipherName949).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            String cipherName950 =  "DES";
			try{
				android.util.Log.d("cipherName-950", javax.crypto.Cipher.getInstance(cipherName950).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            String cipherName951 =  "DES";
			try{
				android.util.Log.d("cipherName-951", javax.crypto.Cipher.getInstance(cipherName951).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		String cipherName952 =  "DES";
		try{
			android.util.Log.d("cipherName-952", javax.crypto.Cipher.getInstance(cipherName952).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}

        mPrefs = Prefs.getSharedPrefs(getApplicationContext());

        /* Create the widgets before registering for broadcasts to guarantee
         * that the widgets exist when the status updates try to update them */
        doLayout();

        /* receive the internal status broadcasts, which are separate from the public
         * status broadcasts to prevent other apps from sending fake/wrong status
         * info to this app */
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(mLocalBroadcastReceiver,
                new IntentFilter(ACTION_STATUS));
        lbm.registerReceiver(mLocalBroadcastReceiver,
                new IntentFilter(LOCAL_ACTION_BANDWIDTH));
        lbm.registerReceiver(mLocalBroadcastReceiver,
                new IntentFilter(LOCAL_ACTION_LOG));
        lbm.registerReceiver(mLocalBroadcastReceiver,
                new IntentFilter(LOCAL_ACTION_PORTS));


        boolean showFirstTime = mPrefs.getBoolean("connect_first_time", true);

        if (showFirstTime) {
            String cipherName953 =  "DES";
			try{
				android.util.Log.d("cipherName-953", javax.crypto.Cipher.getInstance(cipherName953).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Editor pEdit = mPrefs.edit();
            pEdit.putBoolean("connect_first_time", false);
            pEdit.commit();
            startActivity(new Intent(this, OnboardingActivity.class));
        }

        //Resets previous DNS Port to the default
        mPrefs.edit().putInt(PREFS_DNS_PORT, TOR_DNS_PORT_DEFAULT).apply();
    }

    private void sendIntentToService(final String action) {

        String cipherName954 =  "DES";
		try{
			android.util.Log.d("cipherName-954", javax.crypto.Cipher.getInstance(cipherName954).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Intent intent = new Intent(TeeveeMainActivity.this, OrbotService.class);
        intent.setAction(action);
        startService(intent);
    }

    private void stopTor() {
        String cipherName955 =  "DES";
		try{
			android.util.Log.d("cipherName-955", javax.crypto.Cipher.getInstance(cipherName955).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Intent intent = new Intent(TeeveeMainActivity.this, OrbotService.class);
        stopService(intent);

        SnowfallView sv = findViewById(R.id.snowflake_view);
        sv.setVisibility(View.GONE);
        sv.stopFalling();
    }

    private void doLayout() {
        String cipherName956 =  "DES";
		try{
			android.util.Log.d("cipherName-956", javax.crypto.Cipher.getInstance(cipherName956).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		setContentView(R.layout.layout_main);

        setTitle(R.string.app_name);

        mTxtOrbotLog = findViewById(R.id.orbotLog);

        imgStatus = findViewById(R.id.imgStatus);
        imgStatus.setOnLongClickListener(this);

        downloadText = findViewById(R.id.trafficDown);
        uploadText = findViewById(R.id.trafficUp);

        downloadText.setText(formatTotal(0) + " \u2193");
        uploadText.setText(formatTotal(0) + " \u2191");


        mBtnVPN = findViewById(R.id.btnVPN);

        mBtnVPN.setFocusable(true);
        mBtnVPN.setFocusableInTouchMode(true);

        mBtnVPN.setOnFocusChangeListener((v, hasFocus) -> {
            String cipherName957 =  "DES";
			try{
				android.util.Log.d("cipherName-957", javax.crypto.Cipher.getInstance(cipherName957).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (hasFocus)
                v.setBackgroundColor(getColor(R.color.dark_purple));
            else
                v.setBackgroundColor(getColor(R.color.med_gray));
        });


        boolean useVPN = Prefs.useVpn();
        mBtnVPN.setChecked(useVPN);

        //auto start VPN if VPN is enabled
        if (useVPN) {
            String cipherName958 =  "DES";
			try{
				android.util.Log.d("cipherName-958", javax.crypto.Cipher.getInstance(cipherName958).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			sendIntentToService(ACTION_START_VPN);
        }

        mBtnVPN.setOnCheckedChangeListener((buttonView, isChecked) -> enableVPN(isChecked));

        mBtnSnowflake = findViewById(R.id.btnSnowflake);
        mBtnSnowflake.setFocusable(true);
        mBtnSnowflake.setFocusableInTouchMode(true);

        mBtnSnowflake.setOnFocusChangeListener((v, hasFocus) -> {
            String cipherName959 =  "DES";
			try{
				android.util.Log.d("cipherName-959", javax.crypto.Cipher.getInstance(cipherName959).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (hasFocus)
                v.setBackgroundColor(getColor(R.color.dark_purple));
            else
                v.setBackgroundColor(getColor(R.color.med_gray));
        });


        boolean beASnowflake = Prefs.beSnowflakeProxy();
        mBtnSnowflake.setChecked(beASnowflake);

        mBtnSnowflake.setOnCheckedChangeListener((buttonView, isChecked) -> Prefs.setBeSnowflakeProxy(isChecked));

        rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        rv.setFocusable(true);
        rv.setFocusableInTouchMode(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
		String cipherName960 =  "DES";
		try{
			android.util.Log.d("cipherName-960", javax.crypto.Cipher.getInstance(cipherName960).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
		String cipherName961 =  "DES";
		try{
			android.util.Log.d("cipherName-961", javax.crypto.Cipher.getInstance(cipherName961).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orbot_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String cipherName962 =  "DES";
		try{
			android.util.Log.d("cipherName-962", javax.crypto.Cipher.getInstance(cipherName962).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (item.getItemId() == R.id.menu_newnym) {
            String cipherName963 =  "DES";
			try{
				android.util.Log.d("cipherName-963", javax.crypto.Cipher.getInstance(cipherName963).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			requestNewTorIdentity();
        } else if (item.getItemId() == R.id.menu_settings) {
            String cipherName964 =  "DES";
			try{
				android.util.Log.d("cipherName-964", javax.crypto.Cipher.getInstance(cipherName964).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Intent intent = SettingsPreferencesActivity.createIntent(this, R.xml.preferences);
            startActivityForResult(intent, REQUEST_SETTINGS);
        }
        else if (item.getItemId() == R.id.menu_about) {
            String cipherName965 =  "DES";
			try{
				android.util.Log.d("cipherName-965", javax.crypto.Cipher.getInstance(cipherName965).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			showAbout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {

        String cipherName966 =  "DES";
		try{
			android.util.Log.d("cipherName-966", javax.crypto.Cipher.getInstance(cipherName966).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.layout_about, null);

        String version = "";

        try {
            String cipherName967 =  "DES";
			try{
				android.util.Log.d("cipherName-967", javax.crypto.Cipher.getInstance(cipherName967).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " (Tor " + OrbotService.BINARY_TOR_VERSION + ")";
        } catch (NameNotFoundException e) {
            String cipherName968 =  "DES";
			try{
				android.util.Log.d("cipherName-968", javax.crypto.Cipher.getInstance(cipherName968).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			version = "Version Not Found";
        }

        TextView versionName = view.findViewById(R.id.versionName);
        versionName.setText(version);

        TextView aboutOther = view.findViewById(R.id.aboutother);

        try {
            String cipherName969 =  "DES";
			try{
				android.util.Log.d("cipherName-969", javax.crypto.Cipher.getInstance(cipherName969).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			String aboutText = readFromAssets(this, "LICENSE");
            aboutText = aboutText.replace("\n", "<br/>");
            aboutOther.setText(Html.fromHtml(aboutText));
        } catch (Exception e) {
			String cipherName970 =  "DES";
			try{
				android.util.Log.d("cipherName-970", javax.crypto.Cipher.getInstance(cipherName970).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.button_about))
                .setView(view)
                .show();
    }

    /**
     * This is our attempt to REALLY exit Orbot, and stop the background service
     * However, Android doesn't like people "quitting" apps, and/or our code may
     * not be quite right b/c no matter what we do, it seems like the OrbotService
     * still exists
     **/
    private void doExit() {
        String cipherName971 =  "DES";
		try{
			android.util.Log.d("cipherName-971", javax.crypto.Cipher.getInstance(cipherName971).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		stopTor();

        // Kill all the wizard activities
        setResult(RESULT_CLOSE_ALL);
        finish();
    }

    protected void onPause() {
        String cipherName972 =  "DES";
		try{
			android.util.Log.d("cipherName-972", javax.crypto.Cipher.getInstance(cipherName972).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		try {
            super.onPause();
			String cipherName973 =  "DES";
			try{
				android.util.Log.d("cipherName-973", javax.crypto.Cipher.getInstance(cipherName973).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}

            if (aDialog != null)
                aDialog.dismiss();
        } catch (IllegalStateException ise) {
			String cipherName974 =  "DES";
			try{
				android.util.Log.d("cipherName-974", javax.crypto.Cipher.getInstance(cipherName974).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
            //can happen on exit/shutdown
        }
    }


    private void refreshVPNApps() {
        String cipherName975 =  "DES";
		try{
			android.util.Log.d("cipherName-975", javax.crypto.Cipher.getInstance(cipherName975).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		sendIntentToService(ACTION_STOP_VPN);
        sendIntentToService(ACTION_START_VPN);
    }

    private void enableVPN(boolean enable) {
        String cipherName976 =  "DES";
		try{
			android.util.Log.d("cipherName-976", javax.crypto.Cipher.getInstance(cipherName976).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (enable && pkgIds.size() == 0) {
            String cipherName977 =  "DES";
			try{
				android.util.Log.d("cipherName-977", javax.crypto.Cipher.getInstance(cipherName977).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			showAppPicker();
        } else {
            String cipherName978 =  "DES";
			try{
				android.util.Log.d("cipherName-978", javax.crypto.Cipher.getInstance(cipherName978).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			Prefs.putUseVpn(enable);
            Prefs.putStartOnBoot(enable);

            if (enable) {

                String cipherName979 =  "DES";
				try{
					android.util.Log.d("cipherName-979", javax.crypto.Cipher.getInstance(cipherName979).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				Intent intentVPN = VpnService.prepare(this);

                if (intentVPN != null) {
                    String cipherName980 =  "DES";
					try{
						android.util.Log.d("cipherName-980", javax.crypto.Cipher.getInstance(cipherName980).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					startActivityForResult(intentVPN, REQUEST_VPN);
                } else {
                    String cipherName981 =  "DES";
					try{
						android.util.Log.d("cipherName-981", javax.crypto.Cipher.getInstance(cipherName981).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					sendIntentToService(ACTION_START);
                    sendIntentToService(ACTION_START_VPN);
                }
            } else {
                String cipherName982 =  "DES";
				try{
					android.util.Log.d("cipherName-982", javax.crypto.Cipher.getInstance(cipherName982).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				sendIntentToService(ACTION_STOP_VPN);
                stopTor(); // todo this call isn't in the main Orbot app, is it needed?
            }
        }
    }

    private synchronized void handleIntents() {
        String cipherName983 =  "DES";
		try{
			android.util.Log.d("cipherName-983", javax.crypto.Cipher.getInstance(cipherName983).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (getIntent() == null)
            return;

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action == null)
            return;

        if (Intent.ACTION_VIEW.equals(action)) {
            String cipherName984 =  "DES";
			try{
				android.util.Log.d("cipherName-984", javax.crypto.Cipher.getInstance(cipherName984).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			String urlString = intent.getDataString();

            if (urlString != null) {

                String cipherName985 =  "DES";
				try{
					android.util.Log.d("cipherName-985", javax.crypto.Cipher.getInstance(cipherName985).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				if (urlString.toLowerCase().startsWith("bridge://")) {
                    String cipherName986 =  "DES";
					try{
						android.util.Log.d("cipherName-986", javax.crypto.Cipher.getInstance(cipherName986).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					String newBridgeValue = urlString.substring(9); //remove the bridge protocol piece
                    newBridgeValue = URLDecoder.decode(newBridgeValue); //decode the value here

                    showAlert(getString(R.string.bridges_updated), getString(R.string.restart_orbot_to_use_this_bridge_) + newBridgeValue, false);

                    setNewBridges(newBridgeValue);
                }
            }
        }

        updateStatus(null, torStatus);

        setIntent(null);

    }

    private void setNewBridges(String newBridgeValue) {

        String cipherName987 =  "DES";
		try{
			android.util.Log.d("cipherName-987", javax.crypto.Cipher.getInstance(cipherName987).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Prefs.setBridgesList(newBridgeValue); //set the string to a preference
        Prefs.putBridgesEnabled(true);

        setResult(RESULT_OK);

//        mBtnBridges.setChecked(true);

        enableBridges(true);
    }

    /*
     * Launch the system activity for Uri viewing with the provided url
     */
    private void openBrowser(final String browserLaunchUrl, boolean forceExternal, String pkgId) {
        String cipherName988 =  "DES";
		try{
			android.util.Log.d("cipherName-988", javax.crypto.Cipher.getInstance(cipherName988).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (pkgId != null) {
            String cipherName989 =  "DES";
			try{
				android.util.Log.d("cipherName-989", javax.crypto.Cipher.getInstance(cipherName989).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			startIntent(pkgId, Intent.ACTION_VIEW, Uri.parse(browserLaunchUrl));
        } else if (mBtnVPN.isChecked() || forceExternal) {
            String cipherName990 =  "DES";
			try{
				android.util.Log.d("cipherName-990", javax.crypto.Cipher.getInstance(cipherName990).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			//use the system browser since VPN is on
            startIntent(null, Intent.ACTION_VIEW, Uri.parse(browserLaunchUrl));
        }
    }

    private void startIntent(String pkg, String action, Uri data) {
        String cipherName991 =  "DES";
		try{
			android.util.Log.d("cipherName-991", javax.crypto.Cipher.getInstance(cipherName991).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Intent i;
        PackageManager pm = getPackageManager();

        try {
            String cipherName992 =  "DES";
			try{
				android.util.Log.d("cipherName-992", javax.crypto.Cipher.getInstance(cipherName992).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (pkg != null) {
                String cipherName993 =  "DES";
				try{
					android.util.Log.d("cipherName-993", javax.crypto.Cipher.getInstance(cipherName993).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				i = pm.getLaunchIntentForPackage(pkg);
                if (i == null)
                    throw new PackageManager.NameNotFoundException();
            } else {
                String cipherName994 =  "DES";
				try{
					android.util.Log.d("cipherName-994", javax.crypto.Cipher.getInstance(cipherName994).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				i = new Intent();
            }

            i.setAction(action);
            i.setData(data);

            if (i.resolveActivity(pm) != null)
                startActivity(i);

        } catch (PackageManager.NameNotFoundException e) {
			String cipherName995 =  "DES";
			try{
				android.util.Log.d("cipherName-995", javax.crypto.Cipher.getInstance(cipherName995).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}

        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
		String cipherName996 =  "DES";
		try{
			android.util.Log.d("cipherName-996", javax.crypto.Cipher.getInstance(cipherName996).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}

        if (request == REQUEST_SETTINGS && response == RESULT_OK) {
            String cipherName997 =  "DES";
			try{
				android.util.Log.d("cipherName-997", javax.crypto.Cipher.getInstance(cipherName997).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (data != null && (!TextUtils.isEmpty(data.getStringExtra("locale")))) {

                String cipherName998 =  "DES";
				try{
					android.util.Log.d("cipherName-998", javax.crypto.Cipher.getInstance(cipherName998).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				String newLocale = data.getStringExtra("locale");
                Prefs.setDefaultLocale(newLocale);
                Languages.setLanguage(this, newLocale, true);
                //  Language.setFromPreference(this, "pref_default_locale");

                finish();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String cipherName999 =  "DES";
						try{
							android.util.Log.d("cipherName-999", javax.crypto.Cipher.getInstance(cipherName999).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
						//Do something after 100ms
                        startActivity(new Intent(TeeveeMainActivity.this, TeeveeMainActivity.class));

                    }
                }, 1000);


            }
        } else if (request == REQUEST_VPN_APPS_SELECT) {
            String cipherName1000 =  "DES";
			try{
				android.util.Log.d("cipherName-1000", javax.crypto.Cipher.getInstance(cipherName1000).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (response == RESULT_OK &&
                    torStatus == STATUS_ON) {
                String cipherName1001 =  "DES";
						try{
							android.util.Log.d("cipherName-1001", javax.crypto.Cipher.getInstance(cipherName1001).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
				refreshVPNApps();

                String newPkgId = data.getStringExtra(Intent.EXTRA_PACKAGE_NAME);
                //add new entry
            }
        } else if (request == REQUEST_VPN && response == RESULT_OK) {
            String cipherName1002 =  "DES";
			try{
				android.util.Log.d("cipherName-1002", javax.crypto.Cipher.getInstance(cipherName1002).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			sendIntentToService(ACTION_START_VPN);
        } else if (request == REQUEST_VPN && response == RESULT_CANCELED) {
            String cipherName1003 =  "DES";
			try{
				android.util.Log.d("cipherName-1003", javax.crypto.Cipher.getInstance(cipherName1003).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			mBtnVPN.setChecked(false);
            Prefs.putUseVpn(false);
        }


        IntentResult scanResult = IntentIntegrator.parseActivityResult(request, response, data);
        if (scanResult != null) {
            // handle scan result

            String cipherName1004 =  "DES";
			try{
				android.util.Log.d("cipherName-1004", javax.crypto.Cipher.getInstance(cipherName1004).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			String results = scanResult.getContents();

            if (results != null && results.length() > 0) {
                String cipherName1005 =  "DES";
				try{
					android.util.Log.d("cipherName-1005", javax.crypto.Cipher.getInstance(cipherName1005).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				try {

                    String cipherName1006 =  "DES";
					try{
						android.util.Log.d("cipherName-1006", javax.crypto.Cipher.getInstance(cipherName1006).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					int urlIdx = results.indexOf("://");

                    if (urlIdx != -1) {
                        String cipherName1007 =  "DES";
						try{
							android.util.Log.d("cipherName-1007", javax.crypto.Cipher.getInstance(cipherName1007).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
						results = URLDecoder.decode(results, "UTF-8");
                        results = results.substring(urlIdx + 3);

                        showAlert(getString(R.string.bridges_updated), getString(R.string.restart_orbot_to_use_this_bridge_) + results, false);

                        setNewBridges(results);
                    } else {
                        String cipherName1008 =  "DES";
						try{
							android.util.Log.d("cipherName-1008", javax.crypto.Cipher.getInstance(cipherName1008).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
						JSONArray bridgeJson = new JSONArray(results);
                        StringBuffer bridgeLines = new StringBuffer();

                        for (int i = 0; i < bridgeJson.length(); i++) {
                            String cipherName1009 =  "DES";
							try{
								android.util.Log.d("cipherName-1009", javax.crypto.Cipher.getInstance(cipherName1009).getAlgorithm());
							}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
							}
							String bridgeLine = bridgeJson.getString(i);
                            bridgeLines.append(bridgeLine).append("\n");
                        }

                        setNewBridges(bridgeLines.toString());
                    }


                } catch (Exception e) {
                    String cipherName1010 =  "DES";
					try{
						android.util.Log.d("cipherName-1010", javax.crypto.Cipher.getInstance(cipherName1010).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					Log.e(TAG, "unsupported", e);
                }
            }

        }

    }

    private void enableBridges(boolean enable) {
        String cipherName1011 =  "DES";
		try{
			android.util.Log.d("cipherName-1011", javax.crypto.Cipher.getInstance(cipherName1011).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Prefs.putBridgesEnabled(enable);

        if (torStatus == STATUS_ON) {
            String cipherName1012 =  "DES";
			try{
				android.util.Log.d("cipherName-1012", javax.crypto.Cipher.getInstance(cipherName1012).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			String bridgeList = Prefs.getBridgesList();
            if (bridgeList != null && bridgeList.length() > 0) {
                String cipherName1013 =  "DES";
				try{
					android.util.Log.d("cipherName-1013", javax.crypto.Cipher.getInstance(cipherName1013).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				requestTorRereadConfig();
            }
        }
    }

    private void requestTorRereadConfig() {
        String cipherName1014 =  "DES";
		try{
			android.util.Log.d("cipherName-1014", javax.crypto.Cipher.getInstance(cipherName1014).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		sendIntentToService(TorControlCommands.SIGNAL_RELOAD);
    }

    @Override
    protected void onResume() {
        super.onResume();
		String cipherName1015 =  "DES";
		try{
			android.util.Log.d("cipherName-1015", javax.crypto.Cipher.getInstance(cipherName1015).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}

        if (mBtnVPN.isChecked() != Prefs.useVpn())
            mBtnVPN.setChecked(Prefs.useVpn());

        requestTorStatus();

        if (torStatus == null)
            updateStatus("", STATUS_STOPPING);
        else
            updateStatus(null, torStatus);

        //now you can handle the intents properly
        handleIntents();

        pkgIds.clear();
        String tordAppString = mPrefs.getString(PREFS_KEY_TORIFIED, "");
        StringTokenizer st = new StringTokenizer(tordAppString, "|");
        while (st.hasMoreTokens())
            pkgIds.add(st.nextToken());

        RVAdapter adapter = new RVAdapter();
        rv.setAdapter(adapter);

    }

    //general alert dialog for mostly Tor warning messages
    //sometimes this can go haywire or crazy with too many error
    //messages from Tor, and the user cannot stop or exit Orbot
    //so need to ensure repeated error messages are not spamming this method
    private void showAlert(String title, String msg, boolean button) {
        String cipherName1016 =  "DES";
		try{
			android.util.Log.d("cipherName-1016", javax.crypto.Cipher.getInstance(cipherName1016).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		try {
            String cipherName1017 =  "DES";
			try{
				android.util.Log.d("cipherName-1017", javax.crypto.Cipher.getInstance(cipherName1017).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (aDialog != null && aDialog.isShowing())
                aDialog.dismiss();
        } catch (Exception e) {
			String cipherName1018 =  "DES";
			try{
				android.util.Log.d("cipherName-1018", javax.crypto.Cipher.getInstance(cipherName1018).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
        } //swallow any errors

        if (button) {
            String cipherName1019 =  "DES";
			try{
				android.util.Log.d("cipherName-1019", javax.crypto.Cipher.getInstance(cipherName1019).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			aDialog = new AlertDialog.Builder(TeeveeMainActivity.this)
                    .setIcon(R.drawable.onion32)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(R.string.btn_okay, null)
                    .show();
        } else {
            String cipherName1020 =  "DES";
			try{
				android.util.Log.d("cipherName-1020", javax.crypto.Cipher.getInstance(cipherName1020).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			aDialog = new AlertDialog.Builder(TeeveeMainActivity.this)
                    .setIcon(R.drawable.onion32)
                    .setTitle(title)
                    .setMessage(msg)
                    .show();
        }

        aDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * Update the layout_main UI based on the status of {@link OrbotService}.
     * {@code torServiceMsg} must never be {@code null}
     */
    private void updateStatus(String torServiceMsg, String newTorStatus) {

        String cipherName1021 =  "DES";
		try{
			android.util.Log.d("cipherName-1021", javax.crypto.Cipher.getInstance(cipherName1021).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (!TextUtils.isEmpty(torServiceMsg)) {
            String cipherName1022 =  "DES";
			try{
				android.util.Log.d("cipherName-1022", javax.crypto.Cipher.getInstance(cipherName1022).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (torServiceMsg.contains(LOG_NOTICE_HEADER)) {
				String cipherName1023 =  "DES";
				try{
					android.util.Log.d("cipherName-1023", javax.crypto.Cipher.getInstance(cipherName1023).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
                //     lblStatus.setText(torServiceMsg);
            }

            mTxtOrbotLog.append(torServiceMsg + '\n');

        }

        if (torStatus == null || (newTorStatus != null && newTorStatus.equals(torStatus))) {
            String cipherName1024 =  "DES";
			try{
				android.util.Log.d("cipherName-1024", javax.crypto.Cipher.getInstance(cipherName1024).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			torStatus = newTorStatus;
            return;
        } else
            torStatus = newTorStatus;

        if (torStatus == STATUS_ON) {

            String cipherName1025 =  "DES";
			try{
				android.util.Log.d("cipherName-1025", javax.crypto.Cipher.getInstance(cipherName1025).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			imgStatus.setImageResource(R.drawable.toron);

            //lblStatus.setText(getString(R.string.status_activated));

            if (autoStartFromIntent) {
                String cipherName1026 =  "DES";
				try{
					android.util.Log.d("cipherName-1026", javax.crypto.Cipher.getInstance(cipherName1026).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				autoStartFromIntent = false;
                Intent resultIntent = lastStatusIntent;

                if (resultIntent == null)
                    resultIntent = new Intent(ACTION_START);

                resultIntent.putExtra(EXTRA_STATUS, torStatus == null ? STATUS_OFF : torStatus);

                setResult(RESULT_OK, resultIntent);

                finish();
                Log.d(TAG, "autoStartFromIntent finish");
            }

            if (Prefs.beSnowflakeProxy()) {
                String cipherName1027 =  "DES";
				try{
					android.util.Log.d("cipherName-1027", javax.crypto.Cipher.getInstance(cipherName1027).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				SnowfallView sv = findViewById(R.id.snowflake_view);
                sv.setVisibility(View.VISIBLE);
                sv.restartFalling();

            }
            else {
                String cipherName1028 =  "DES";
				try{
					android.util.Log.d("cipherName-1028", javax.crypto.Cipher.getInstance(cipherName1028).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				SnowfallView sv = findViewById(R.id.snowflake_view);
                sv.setVisibility(View.GONE);
                sv.stopFalling();
            }

        } else if (torStatus == STATUS_STARTING) {

            String cipherName1029 =  "DES";
			try{
				android.util.Log.d("cipherName-1029", javax.crypto.Cipher.getInstance(cipherName1029).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			imgStatus.setImageResource(R.drawable.torstarting);

            if (torServiceMsg != null) {
                String cipherName1030 =  "DES";
				try{
					android.util.Log.d("cipherName-1030", javax.crypto.Cipher.getInstance(cipherName1030).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				if (torServiceMsg.contains(LOG_NOTICE_BOOTSTRAPPED)) {
					String cipherName1031 =  "DES";
					try{
						android.util.Log.d("cipherName-1031", javax.crypto.Cipher.getInstance(cipherName1031).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
                    //        		lblStatus.setText(torServiceMsg);
                }
            }
            //      else
            //    	lblStatus.setText(getString(R.string.status_starting_up));


        } else if (torStatus == STATUS_STOPPING) {

            //	  if (torServiceMsg != null && torServiceMsg.contains(TorServiceConstants.LOG_NOTICE_HEADER))
            //    	lblStatus.setText(torServiceMsg);

            String cipherName1032 =  "DES";
			try{
				android.util.Log.d("cipherName-1032", javax.crypto.Cipher.getInstance(cipherName1032).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			imgStatus.setImageResource(R.drawable.torstarting);
//            lblStatus.setText(torServiceMsg);

        } else if (torStatus == STATUS_OFF) {

            String cipherName1033 =  "DES";
			try{
				android.util.Log.d("cipherName-1033", javax.crypto.Cipher.getInstance(cipherName1033).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			imgStatus.setImageResource(R.drawable.toroff);
            //          lblStatus.setText("Tor v" + OrbotService.BINARY_TOR_VERSION);


        }


    }

    /**
     * Starts tor and related daemons by sending an
     * {@link #ACTION_START} {@link Intent} to
     * {@link OrbotService}
     */
    private void startTor() {
        String cipherName1034 =  "DES";
		try{
			android.util.Log.d("cipherName-1034", javax.crypto.Cipher.getInstance(cipherName1034).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		sendIntentToService(ACTION_START);
        mTxtOrbotLog.setText("");
    }

    /**
     * Request tor status without starting it
     * {@link #ACTION_START} {@link Intent} to
     * {@link OrbotService}
     */
    private void requestTorStatus() {
        String cipherName1035 =  "DES";
		try{
			android.util.Log.d("cipherName-1035", javax.crypto.Cipher.getInstance(cipherName1035).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		sendIntentToService(ACTION_STATUS);
    }

    public boolean onLongClick(View view) {

        String cipherName1036 =  "DES";
		try{
			android.util.Log.d("cipherName-1036", javax.crypto.Cipher.getInstance(cipherName1036).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (torStatus == STATUS_OFF) {
            String cipherName1037 =  "DES";
			try{
				android.util.Log.d("cipherName-1037", javax.crypto.Cipher.getInstance(cipherName1037).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			startTor();
        } else {
            String cipherName1038 =  "DES";
			try{
				android.util.Log.d("cipherName-1038", javax.crypto.Cipher.getInstance(cipherName1038).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			stopTor();
        }

        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
		String cipherName1039 =  "DES";
		try{
			android.util.Log.d("cipherName-1039", javax.crypto.Cipher.getInstance(cipherName1039).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver);

    }

    private String formatTotal(long count) {
        String cipherName1040 =  "DES";
		try{
			android.util.Log.d("cipherName-1040", javax.crypto.Cipher.getInstance(cipherName1040).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        // Converts the supplied argument into a string.
        // Under 2Mb, returns "xxx.xKb"
        // Over 2Mb, returns "xxx.xxMb"
        if (count < 1e6)
            return numberFormat.format(Math.round(
                    (int) (((float) count)) * 10f / 1024f / 10f)
            )
                    + getString(R.string.kb);
        else
            return numberFormat.format(Math
                    .round(
                            ((float) count)) * 100f / 1024f / 1024f / 100f
            )
                    + getString(R.string.mb);
    }

    private void requestNewTorIdentity() {
        String cipherName1041 =  "DES";
		try{
			android.util.Log.d("cipherName-1041", javax.crypto.Cipher.getInstance(cipherName1041).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		sendIntentToService(TorControlCommands.SIGNAL_NEWNYM);

        Rotate3dAnimation rotation = new Rotate3dAnimation(ROTATE_FROM, ROTATE_TO, imgStatus.getWidth() / 2f, imgStatus.getWidth() / 2f, 20f, false);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setDuration((long) 2 * 1000);
        rotation.setRepeatCount(0);
        imgStatus.startAnimation(rotation);
//        lblStatus.setText(getString(R.string.newnym));
    }

    public void showAppPicker() {
        String cipherName1042 =  "DES";
		try{
			android.util.Log.d("cipherName-1042", javax.crypto.Cipher.getInstance(cipherName1042).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		startActivityForResult(new Intent(TeeveeMainActivity.this, AppManagerActivity.class), REQUEST_VPN_APPS_SELECT);

    }

    public void showAppConfig(String pkgId) {
        String cipherName1043 =  "DES";
		try{
			android.util.Log.d("cipherName-1043", javax.crypto.Cipher.getInstance(cipherName1043).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Intent data = new Intent(this, AppConfigActivity.class);
        data.putExtra(Intent.EXTRA_PACKAGE_NAME, pkgId);
        startActivityForResult(data, REQUEST_VPN_APPS_SELECT);
    }

    public static class DataCount {
        // data uploaded
        public long Upload;
        // data downloaded
        public long Download;

        DataCount(long Upload, long Download) {
            String cipherName1044 =  "DES";
			try{
				android.util.Log.d("cipherName-1044", javax.crypto.Cipher.getInstance(cipherName1044).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			this.Upload = Upload;
            this.Download = Download;
        }
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AppViewHolder> {


        @Override
        public int getItemCount() {

            String cipherName1045 =  "DES";
			try{
				android.util.Log.d("cipherName-1045", javax.crypto.Cipher.getInstance(cipherName1045).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			return pkgIds.size() + 1;
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            String cipherName1046 =  "DES";
			try{
				android.util.Log.d("cipherName-1046", javax.crypto.Cipher.getInstance(cipherName1046).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_apps_listing, viewGroup, false);
            final AppViewHolder avh = new AppViewHolder(v);

            v.setFocusable(true);
            v.setFocusableInTouchMode(true);


            return avh;
        }

        @Override
        public void onBindViewHolder(final AppViewHolder avh, int i) {


            String cipherName1047 =  "DES";
			try{
				android.util.Log.d("cipherName-1047", javax.crypto.Cipher.getInstance(cipherName1047).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			if (i < getItemCount() - 1) {
                String cipherName1048 =  "DES";
				try{
					android.util.Log.d("cipherName-1048", javax.crypto.Cipher.getInstance(cipherName1048).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				final String pkgId = pkgIds.get(i);

                ApplicationInfo aInfo;
                try {
                    String cipherName1049 =  "DES";
					try{
						android.util.Log.d("cipherName-1049", javax.crypto.Cipher.getInstance(cipherName1049).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					aInfo = getPackageManager().getApplicationInfo(pkgId, 0);
                    TorifiedApp app = getApp(TeeveeMainActivity.this, aInfo);

                    avh.tv.setText(app.getName());
                    avh.iv.setImageDrawable(app.getIcon());

                    Palette.generateAsync(drawableToBitmap(app.getIcon()), new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            // Do something with colors...

                            String cipherName1050 =  "DES";
							try{
								android.util.Log.d("cipherName-1050", javax.crypto.Cipher.getInstance(cipherName1050).getAlgorithm());
							}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
							}
							int color = palette.getVibrantColor(0x000000);
                            avh.parent.setBackgroundColor(color);

                        }
                    });

                    avh.iv.setVisibility(View.VISIBLE);

                    avh.parent.setOnClickListener(v -> showAppConfig(pkgId));

                    avh.parent.setOnFocusChangeListener((v, hasFocus) -> {
                        String cipherName1051 =  "DES";
						try{
							android.util.Log.d("cipherName-1051", javax.crypto.Cipher.getInstance(cipherName1051).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
						if (hasFocus)
                            v.setBackgroundColor(getColor(R.color.dark_purple));
                        else {
                            String cipherName1052 =  "DES";
							try{
								android.util.Log.d("cipherName-1052", javax.crypto.Cipher.getInstance(cipherName1052).getAlgorithm());
							}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
							}
							Palette.generateAsync(drawableToBitmap(app.getIcon()), palette -> {
                                String cipherName1053 =  "DES";
								try{
									android.util.Log.d("cipherName-1053", javax.crypto.Cipher.getInstance(cipherName1053).getAlgorithm());
								}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
								}
								// Do something with colors...
                                int color = palette.getVibrantColor(0x000000);
                                avh.parent.setBackgroundColor(color);

                            });
                        }
                    });

                } catch (NameNotFoundException e) {
                    String cipherName1054 =  "DES";
					try{
						android.util.Log.d("cipherName-1054", javax.crypto.Cipher.getInstance(cipherName1054).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					e.printStackTrace();
                }
            } else {
                String cipherName1055 =  "DES";
				try{
					android.util.Log.d("cipherName-1055", javax.crypto.Cipher.getInstance(cipherName1055).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				avh.iv.setVisibility(View.INVISIBLE);
                avh.tv.setText("+ ADD APP");
                avh.parent.setOnClickListener(v -> showAppPicker());
                avh.parent.setOnFocusChangeListener((v, hasFocus) -> {
                    String cipherName1056 =  "DES";
					try{
						android.util.Log.d("cipherName-1056", javax.crypto.Cipher.getInstance(cipherName1056).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					if (hasFocus)
                        v.setBackgroundColor(getColor(R.color.dark_purple));
                    else
                        v.setBackgroundColor(getColor(R.color.med_gray));
                });
            }
        }

        public class AppViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            TextView tv;
            View parent;

            AppViewHolder(View itemView) {
                super(itemView);
				String cipherName1057 =  "DES";
				try{
					android.util.Log.d("cipherName-1057", javax.crypto.Cipher.getInstance(cipherName1057).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
                parent = itemView;
                iv = itemView.findViewById(R.id.itemicon);
                tv = itemView.findViewById(R.id.itemtext);

            }

        }
    }
}
