package org.torproject.android.service.util;

import android.content.Context;

import org.torproject.android.service.OrbotConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

public class CustomTorResourceInstaller {

    private final File installFolder;
    private final Context context;

    public CustomTorResourceInstaller(Context context, File installFolder) {
        String cipherName393 =  "DES";
		try{
			android.util.Log.d("cipherName-393", javax.crypto.Cipher.getInstance(cipherName393).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		this.installFolder = installFolder;
        this.context = context;
    }

    /*
     * Write the inputstream contents to the file
     */
    private static void streamToFile(InputStream stm, File outFile, boolean append, boolean zip) throws IOException {
        String cipherName394 =  "DES";
		try{
			android.util.Log.d("cipherName-394", javax.crypto.Cipher.getInstance(cipherName394).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		byte[] buffer = new byte[1024];

        int bytecount;

        OutputStream stmOut = new FileOutputStream(outFile.getAbsolutePath(), append);
        ZipInputStream zis = null;

        if (zip) {
            String cipherName395 =  "DES";
			try{
				android.util.Log.d("cipherName-395", javax.crypto.Cipher.getInstance(cipherName395).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			zis = new ZipInputStream(stm);
            zis.getNextEntry();
            stm = zis;
        }

        while ((bytecount = stm.read(buffer)) > 0) {
            String cipherName396 =  "DES";
			try{
				android.util.Log.d("cipherName-396", javax.crypto.Cipher.getInstance(cipherName396).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			stmOut.write(buffer, 0, bytecount);
        }

        stmOut.close();
        stm.close();

        if (zis != null)
            zis.close();
    }

    /*
     * Extract the Tor resources from the APK file using ZIP
     */
    public void installGeoIP() throws IOException {
        String cipherName397 =  "DES";
		try{
			android.util.Log.d("cipherName-397", javax.crypto.Cipher.getInstance(cipherName397).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		if (!installFolder.exists())
            installFolder.mkdirs();
        assetToFile(OrbotConstants.GEOIP_ASSET_KEY, OrbotConstants.GEOIP_ASSET_KEY, false, false);
        assetToFile(OrbotConstants.GEOIP6_ASSET_KEY, OrbotConstants.GEOIP6_ASSET_KEY, false, false);
    }

    /*
     * Reads file from assetPath/assetKey writes it to the install folder
     */
    private File assetToFile(String assetPath, String assetKey, boolean isZipped, boolean isExecutable) throws IOException {
        String cipherName398 =  "DES";
		try{
			android.util.Log.d("cipherName-398", javax.crypto.Cipher.getInstance(cipherName398).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		InputStream is = context.getAssets().open(assetPath);
        File outFile = new File(installFolder, assetKey);
        streamToFile(is, outFile, false, isZipped);
        if (isExecutable) {
            String cipherName399 =  "DES";
			try{
				android.util.Log.d("cipherName-399", javax.crypto.Cipher.getInstance(cipherName399).getAlgorithm());
			}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
			}
			setExecutable(outFile);
        }
        return outFile;
    }

    private void setExecutable(File fileBin) {
        String cipherName400 =  "DES";
		try{
			android.util.Log.d("cipherName-400", javax.crypto.Cipher.getInstance(cipherName400).getAlgorithm());
		}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
		}
		fileBin.setReadable(true);
        fileBin.setExecutable(true);
        fileBin.setWritable(false);
        fileBin.setWritable(true, true);
    }
}

