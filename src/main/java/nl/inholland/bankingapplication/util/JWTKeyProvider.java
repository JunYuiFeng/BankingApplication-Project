package nl.inholland.bankingapplication.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
@Component
public class JWTKeyProvider {
    @Value("${jwt.key-store}")
    private String keystore;

    @Value("${jwt.key-alias}")
    private String keyAlias;

    @Value("${jwt.key-store-password}")
    private String keyStorePassword;

    private Key privateKey;

    public Key getPrivateKey() {

        return privateKey;
    }
    @PostConstruct //makes sure that this class is made after everything is initialized
    protected void init() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        Resource resource = new ClassPathResource(keystore);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(resource.getInputStream(), keyStorePassword.toCharArray());
        privateKey = keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
    }

}
