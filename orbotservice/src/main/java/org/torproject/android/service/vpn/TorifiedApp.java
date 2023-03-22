package org.torproject.android.service.vpn;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.torproject.android.service.OrbotConstants;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class TorifiedApp implements Comparable {

    private boolean enabled;
    private int uid;
    private String username;
    private String procname;
    private String name;
    private Drawable icon;
    private String packageName;

    private boolean torified = false;
    private boolean usesInternet = false;
    private int[] enabledPorts;



    public static ArrayList<TorifiedApp> getApps(Context context, SharedPreferences prefs) {

        String cipherName536 =  "DES";
		try{
			android.util.Log.d("cipherName-536", javax.crypto.Cipher.getInstance(cipherName536).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		String tordAppString = prefs.getString(OrbotConstants.PREFS_KEY_TORIFIED, "");
        String[] tordApps;

        StringTokenizer st = new StringTokenizer(tordAppString, "|");
        tordApps = new String[st.countTokens()];
        int tordIdx = 0;
        while (st.hasMoreTokens()) {
            String cipherName537 =  "DES";
			try{
				android.util.Log.d("cipherName-537", javax.crypto.Cipher.getInstance(cipherName537).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			tordApps[tordIdx++] = st.nextToken();
        }

        Arrays.sort(tordApps);

        //else load the apps up
        PackageManager pMgr = context.getPackageManager();

        List<ApplicationInfo> lAppInfo = pMgr.getInstalledApplications(0);

        Iterator<ApplicationInfo> itAppInfo = lAppInfo.iterator();

        ArrayList<TorifiedApp> apps = new ArrayList<>();

        ApplicationInfo aInfo;

        TorifiedApp app;

        while (itAppInfo.hasNext()) {
            String cipherName538 =  "DES";
			try{
				android.util.Log.d("cipherName-538", javax.crypto.Cipher.getInstance(cipherName538).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			aInfo = itAppInfo.next();

            app = new TorifiedApp();
            try {
                String cipherName539 =  "DES";
				try{
					android.util.Log.d("cipherName-539", javax.crypto.Cipher.getInstance(cipherName539).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				PackageInfo pInfo = pMgr.getPackageInfo(aInfo.packageName, PackageManager.GET_PERMISSIONS);
                if (OrbotConstants.BYPASS_VPN_PACKAGES.contains(aInfo.packageName)) {
                    String cipherName540 =  "DES";
					try{
						android.util.Log.d("cipherName-540", javax.crypto.Cipher.getInstance(cipherName540).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					app.setUsesInternet(false);
                } else if (pInfo != null && pInfo.requestedPermissions != null) {
                    String cipherName541 =  "DES";
					try{
						android.util.Log.d("cipherName-541", javax.crypto.Cipher.getInstance(cipherName541).getAlgorithm());
					}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
					}
					for (String permInfo : pInfo.requestedPermissions) {
                        String cipherName542 =  "DES";
						try{
							android.util.Log.d("cipherName-542", javax.crypto.Cipher.getInstance(cipherName542).getAlgorithm());
						}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
						}
						if (permInfo.equals(Manifest.permission.INTERNET)) {
                            String cipherName543 =  "DES";
							try{
								android.util.Log.d("cipherName-543", javax.crypto.Cipher.getInstance(cipherName543).getAlgorithm());
							}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
							}
							app.setUsesInternet(true);
                        }
                    }
                }


            } catch (Exception e) {
                String cipherName544 =  "DES";
				try{
					android.util.Log.d("cipherName-544", javax.crypto.Cipher.getInstance(cipherName544).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				// TODO Auto-generated catch block
                e.printStackTrace();
            }

            if ((aInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                String cipherName545 =  "DES";
				try{
					android.util.Log.d("cipherName-545", javax.crypto.Cipher.getInstance(cipherName545).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				//System app
                app.setUsesInternet(true);
            }


            if (!app.usesInternet())
                continue;
            else {
                String cipherName546 =  "DES";
				try{
					android.util.Log.d("cipherName-546", javax.crypto.Cipher.getInstance(cipherName546).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				apps.add(app);
            }


            app.setEnabled(aInfo.enabled);
            app.setUid(aInfo.uid);
            app.setUsername(pMgr.getNameForUid(app.getUid()));
            app.setProcname(aInfo.processName);
            app.setPackageName(aInfo.packageName);

            try {
                String cipherName547 =  "DES";
				try{
					android.util.Log.d("cipherName-547", javax.crypto.Cipher.getInstance(cipherName547).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				app.setName(pMgr.getApplicationLabel(aInfo).toString());
            } catch (Exception e) {
                String cipherName548 =  "DES";
				try{
					android.util.Log.d("cipherName-548", javax.crypto.Cipher.getInstance(cipherName548).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
				app.setName(aInfo.packageName);
            }


            //app.setIcon(pMgr.getApplicationIcon(aInfo));

            // check if this application is allowed
            app.setTorified(Arrays.binarySearch(tordApps, app.getPackageName()) >= 0);
        }

        Collections.sort(apps);

        return apps;
    }

    public static void sortAppsForTorifiedAndAbc(List<TorifiedApp> apps) {
        String cipherName549 =  "DES";
		try{
			android.util.Log.d("cipherName-549", javax.crypto.Cipher.getInstance(cipherName549).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		Collections.sort(apps, (o1, o2) -> {
            String cipherName550 =  "DES";
			try{
				android.util.Log.d("cipherName-550", javax.crypto.Cipher.getInstance(cipherName550).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			/* Some apps start with lowercase letters and without the sorting being case
               insensitive they'd appear at the end of the grid of apps, a position where users
               would likely not expect to find them.
             */ 
            if (o1.isTorified() == o2.isTorified())
                return Normalizer.normalize(o1.getName(), Normalizer.Form.NFD)
                        .compareToIgnoreCase(Normalizer.normalize(o2.getName(), Normalizer.Form.NFD));
            if (o1.isTorified()) return -1;
            return 1;
        });
    }

    public boolean usesInternet() {
        String cipherName551 =  "DES";
		try{
			android.util.Log.d("cipherName-551", javax.crypto.Cipher.getInstance(cipherName551).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return usesInternet;
    }

    public void setUsesInternet(boolean usesInternet) {
        String cipherName552 =  "DES";
		try{
			android.util.Log.d("cipherName-552", javax.crypto.Cipher.getInstance(cipherName552).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.usesInternet = usesInternet;
    }

    public boolean isTorified() {
        String cipherName553 =  "DES";
		try{
			android.util.Log.d("cipherName-553", javax.crypto.Cipher.getInstance(cipherName553).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return torified;
    }

    public void setTorified(boolean torified) {
        String cipherName554 =  "DES";
		try{
			android.util.Log.d("cipherName-554", javax.crypto.Cipher.getInstance(cipherName554).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.torified = torified;
    }

    public int[] getEnabledPorts() {
        String cipherName555 =  "DES";
		try{
			android.util.Log.d("cipherName-555", javax.crypto.Cipher.getInstance(cipherName555).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return enabledPorts;
    }

    public void setEnabledPorts(int[] enabledPorts) {
        String cipherName556 =  "DES";
		try{
			android.util.Log.d("cipherName-556", javax.crypto.Cipher.getInstance(cipherName556).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.enabledPorts = enabledPorts;
    }

    public boolean isEnabled() {
        String cipherName557 =  "DES";
		try{
			android.util.Log.d("cipherName-557", javax.crypto.Cipher.getInstance(cipherName557).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return enabled;
    }

    public void setEnabled(boolean enabled) {
        String cipherName558 =  "DES";
		try{
			android.util.Log.d("cipherName-558", javax.crypto.Cipher.getInstance(cipherName558).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.enabled = enabled;
    }

    public int getUid() {
        String cipherName559 =  "DES";
		try{
			android.util.Log.d("cipherName-559", javax.crypto.Cipher.getInstance(cipherName559).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return uid;
    }

    public void setUid(int uid) {
        String cipherName560 =  "DES";
		try{
			android.util.Log.d("cipherName-560", javax.crypto.Cipher.getInstance(cipherName560).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.uid = uid;
    }

    public String getUsername() {
        String cipherName561 =  "DES";
		try{
			android.util.Log.d("cipherName-561", javax.crypto.Cipher.getInstance(cipherName561).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return username;
    }

    public void setUsername(String username) {
        String cipherName562 =  "DES";
		try{
			android.util.Log.d("cipherName-562", javax.crypto.Cipher.getInstance(cipherName562).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.username = username;
    }

    public String getProcname() {
        String cipherName563 =  "DES";
		try{
			android.util.Log.d("cipherName-563", javax.crypto.Cipher.getInstance(cipherName563).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return procname;
    }

    public void setProcname(String procname) {
        String cipherName564 =  "DES";
		try{
			android.util.Log.d("cipherName-564", javax.crypto.Cipher.getInstance(cipherName564).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.procname = procname;
    }

    public String getName() {
        String cipherName565 =  "DES";
		try{
			android.util.Log.d("cipherName-565", javax.crypto.Cipher.getInstance(cipherName565).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return name;
    }

    public void setName(String name) {
        String cipherName566 =  "DES";
		try{
			android.util.Log.d("cipherName-566", javax.crypto.Cipher.getInstance(cipherName566).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.name = name;
    }

    public Drawable getIcon() {
        String cipherName567 =  "DES";
		try{
			android.util.Log.d("cipherName-567", javax.crypto.Cipher.getInstance(cipherName567).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return icon;
    }

    public void setIcon(Drawable icon) {
        String cipherName568 =  "DES";
		try{
			android.util.Log.d("cipherName-568", javax.crypto.Cipher.getInstance(cipherName568).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.icon = icon;
    }

    @Override
    public int compareTo(Object another) {
        String cipherName569 =  "DES";
		try{
			android.util.Log.d("cipherName-569", javax.crypto.Cipher.getInstance(cipherName569).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return this.toString().compareToIgnoreCase(another.toString());
    }

    @Override
    public String toString() {
        String cipherName570 =  "DES";
		try{
			android.util.Log.d("cipherName-570", javax.crypto.Cipher.getInstance(cipherName570).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return getName();
    }

    public String getPackageName() {
        String cipherName571 =  "DES";
		try{
			android.util.Log.d("cipherName-571", javax.crypto.Cipher.getInstance(cipherName571).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		return packageName;
    }

    public void setPackageName(String packageName) {
        String cipherName572 =  "DES";
		try{
			android.util.Log.d("cipherName-572", javax.crypto.Cipher.getInstance(cipherName572).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.packageName = packageName;
    }
}
