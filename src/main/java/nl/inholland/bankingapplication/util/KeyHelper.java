package nl.inholland.bankingapplication.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class KeyHelper {
    private KeyHelper() {

    }
    @Value("${jwt.key-store-type}")
    private  static String keyStoreType;


    public static Key generateKey(String alias, String keystore, String password) throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        //checks  if the credentials are correct (inside the inholland.p12 files) before the JWT is generated to see if the values matches
        Resource resource = new ClassPathResource(keystore);
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(resource.getInputStream(), password.toCharArray());
        return keyStore.getKey(alias, password.toCharArray()); }




}
