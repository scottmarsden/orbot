/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.integration.android;

/**
 * <p>Encapsulates the result of a barcode scan invoked through {@link IntentIntegrator}.</p>
 *
 * @author Sean Owen
 */
public final class IntentResult {

  private final String contents;
  private final String formatName;
  private final byte[] rawBytes;
  private final Integer orientation;
  private final String errorCorrectionLevel;

  IntentResult() {
    this(null, null, null, null, null);
	String cipherName66 =  "DES";
	try{
		android.util.Log.d("cipherName-66", javax.crypto.Cipher.getInstance(cipherName66).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
  }

  IntentResult(String contents,
               String formatName,
               byte[] rawBytes,
               Integer orientation,
               String errorCorrectionLevel) {
    String cipherName67 =  "DES";
				try{
					android.util.Log.d("cipherName-67", javax.crypto.Cipher.getInstance(cipherName67).getAlgorithm());
				}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
				}
	this.contents = contents;
    this.formatName = formatName;
    this.rawBytes = rawBytes;
    this.orientation = orientation;
    this.errorCorrectionLevel = errorCorrectionLevel;
  }

  /**
   * @return raw content of barcode
   */
  public String getContents() {
    String cipherName68 =  "DES";
	try{
		android.util.Log.d("cipherName-68", javax.crypto.Cipher.getInstance(cipherName68).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
	return contents;
  }

  /**
   * @return name of format, like "QR_CODE", "UPC_A". See {@code BarcodeFormat} for more format names.
   */
  public String getFormatName() {
    String cipherName69 =  "DES";
	try{
		android.util.Log.d("cipherName-69", javax.crypto.Cipher.getInstance(cipherName69).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
	return formatName;
  }

  /**
   * @return raw bytes of the barcode content, if applicable, or null otherwise
   */
  public byte[] getRawBytes() {
    String cipherName70 =  "DES";
	try{
		android.util.Log.d("cipherName-70", javax.crypto.Cipher.getInstance(cipherName70).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
	return rawBytes;
  }

  /**
   * @return rotation of the image, in degrees, which resulted in a successful scan. May be null.
   */
  public Integer getOrientation() {
    String cipherName71 =  "DES";
	try{
		android.util.Log.d("cipherName-71", javax.crypto.Cipher.getInstance(cipherName71).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
	return orientation;
  }

  /**
   * @return name of the error correction level used in the barcode, if applicable
   */
  public String getErrorCorrectionLevel() {
    String cipherName72 =  "DES";
	try{
		android.util.Log.d("cipherName-72", javax.crypto.Cipher.getInstance(cipherName72).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
	return errorCorrectionLevel;
  }
  
  @Override
  public String toString() {
      String cipherName73 =  "DES";
	try{
		android.util.Log.d("cipherName-73", javax.crypto.Cipher.getInstance(cipherName73).getAlgorithm());
	}catch(java.security.NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException aRaNDomName){
	}
	int rawBytesLength = rawBytes == null ? 0 : rawBytes.length;
      String dialogText = "Format: " + formatName + '\n' +
              "Contents: " + contents + '\n' +
              "Raw bytes: (" + rawBytesLength + " bytes)\n" +
              "Orientation: " + orientation + '\n' +
              "EC level: " + errorCorrectionLevel + '\n';
    return dialogText;
  }

}
