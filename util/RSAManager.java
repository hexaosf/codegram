package org.electronic.electronicdocumentsystemjava.util;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class RSAManager {

    @Value("${rsa.key_size}")
    private Integer KEY_SIZE;

    @Value("${rsa.b2f_pub}")
    private String B2F_PUB;

    @Value("${rsa.b2f_pri}")
    private String B2F_PRI;

    @Value("${rsa.f2b_pri}")
    private String F2B_PRI;

    @Value("${rsa.f2b_pub}")
    private String F2B_PUB;

    private final ResourceLoader resourceLoader;

    public RSAManager(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String encrypt(String content) throws Exception {
        return encrypt(content, getB2fPublicKey());
    }

    public String decrypt(String content) throws Exception {
        return decrypt(content, getF2bPrivateKey());
    }

    private static String encrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.encodeBase64String(cipher.doFinal(content.getBytes()));
    }

    private static String decrypt(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedBytes = Base64.decodeBase64(content);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }


    public String getKey(String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        InputStream inputStream = resource.getInputStream();
        String key = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        key = key.replace("-----BEGIN PUBLIC KEY-----", "");
        key = key.replace("-----BEGIN PRIVATE KEY-----", "");
        key = key.replace("-----END PUBLIC KEY-----", "");
        key = key.replace("-----END PRIVATE KEY-----", "");
        key = key.replace("\\s", "");
        key = key.replace("\n", "");
        key = key.replace("\r", "");
        return key;
    }

    public PublicKey getPublicKey(String path) throws Exception {
        String publicKey = getKey(path);
        byte[] buffer = Base64.decodeBase64(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return keyFactory.generatePublic(keySpec);
    }

    public PrivateKey getPrivateKey(String path) throws Exception {
        String privateKey = getKey(path);
        byte[] buffer = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public PublicKey getB2fPublicKey() throws Exception {
        return getPublicKey(B2F_PUB);
    }

    public PrivateKey getB2fPrivateKey() throws Exception {
        return getPrivateKey(B2F_PRI);
    }

    public PublicKey getF2bPublicKey() throws Exception {
        return getPublicKey(F2B_PUB);
    }

    public PrivateKey getF2bPrivateKey() throws Exception {
        return getPrivateKey(F2B_PRI);
    }

}
