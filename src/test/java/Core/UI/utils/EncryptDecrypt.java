//package Core.UI.utils;
//
//
//import Core.UI.Const;
//import org.apache.logging.log4j.Logger;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
//
///**
// * Created by axg378 on 2/26/19.
// * This will be used for encryption and decryption
// */
//public class EncryptDecrypt {
//
//    private static String characterEncoding       = "UTF-8";
//    private static String cipherTransformation    = "AES/CBC/PKCS5PADDING";
//    private static String aesEncryptionAlgorithem = "AES";
//    private static final Logger LOGGER = PK_UI_Framework.getLogger(EncryptDecrypt.class);
//    /**
//     * Method for Encrypt Plain String Data
//     * @param plainText
//     * @exception
//     * @return encryptedText
//     */
//
//    public static String encryptPassword(String plainText) {
//        String encryptedText = "";
//        try {
//            String encryptionKey = PropMgr.get(Const.ECUKE_AES_KEY);
//            if(encryptionKey==null || encryptionKey.isEmpty()){
//                throw new RuntimeException("Failed to encrypt, no value found for ecuke_aes_key");
//            }
//            Cipher cipher   = Cipher.getInstance(cipherTransformation);
//            byte[] key      = encryptionKey.getBytes(characterEncoding);
//            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
//            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
//            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
//            Base64.Encoder encoder = Base64.getEncoder();
//            encryptedText = encoder.encodeToString(cipherText);
//
//        } catch (Exception e) {
//            LOGGER.error("Failed to encrypt password",e);
//            throw new RuntimeException("Failed to encrypt password",e);
//        }
//        return encryptedText;
//    }
//
//    /**
//     * Method For Get encryptedText and Decrypted provided String
//     * @param encryptedText
//     * @exception
//     * @return decryptedText
//     */
//    public static String decryptPassword(String encryptedText) {
//        if(encryptedText==null || encryptedText.isEmpty()){
//            throw new RuntimeException("Can not decrypt empty text, Please make sure you set value to decrypt");
//        }
//        String decryptedText = "";
//        try {
//            String encryptionKey = PropMgr.get(Const.ECUKE_AES_KEY);
//            if(encryptionKey==null || encryptionKey.isEmpty()){
//                throw new RuntimeException("Failed to decrypt, no value found for ecuke_aes_key");
//            }
//            Cipher cipher = Cipher.getInstance(cipherTransformation);
//            byte[] key = encryptionKey.getBytes(characterEncoding);
//            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
//            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
//            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
//            Base64.Decoder decoder = Base64.getDecoder();
//            byte[] cipherText = decoder.decode(encryptedText.getBytes("UTF8"));
//            decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");
//
//        } catch (Exception e) {
//            LOGGER.error("Failed to decrypt password",e);
//            throw new RuntimeException("Failed to decrypt password",e);
//        }
//        return decryptedText;
//    }
//}
