package marvelapi;

/**
 * Created by Lillo on 21/10/2018.
 */

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class AuthHashGenerator {
    String generateHash(String timestamp, String publicKey, String privateKey)
            throws Exception{
        try {
            String value = timestamp + privateKey + publicKey;
            MessageDigest md5Encoder = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5Encoder.digest(value.getBytes(Charset.forName("UTF-8")));

            StringBuilder md5 = new StringBuilder();
            for (int i = 0; i < md5Bytes.length; ++i) {
                md5.append(Integer.toHexString((md5Bytes[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return md5.toString();
        }catch (NoSuchAlgorithmException e) {
            throw new Exception("cannot generate the api key", e);
        }
    }
}
