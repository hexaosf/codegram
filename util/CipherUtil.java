package org.electronic.electronicdocumentsystemjava.util;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.ECKeyUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class CipherUtil {
    static {
        // 在静态代码块中，添加BouncyCastleProvider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static Map<String, String> getKeyPair() {

        SM2 sm2 = SmUtil.sm2();
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        String privateKey = HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(sm2.getPrivateKey()));
        String publicKey = HexUtil.encodeHexStr(((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false));

        Map<String, String> map = new HashMap<>();
        map.put("public", publicKey);
        map.put("private", privateKey);

        return map;
    }

    public static byte[] sm2Encrypt(byte[] plainText, String privateKeyStr, String publicKeyStr) {
        String publicKeyTmp = publicKeyStr;
        if (publicKeyStr.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKeyTmp = publicKeyStr.substring(2);
        }
        String xhex = publicKeyTmp.substring(0, 64);
        String yhex = publicKeyTmp.substring(64, 128);
        ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        sm2.usePlainEncoding();
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        return sm2.encrypt(plainText, KeyType.PublicKey);
    }

    public static byte[] sm2Decrypt(String cipherText, String privateKeyStr) {
        SM2 sm2 = new SM2(ECKeyUtil.toSm2PrivateParams(privateKeyStr), null);
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        sm2.setEncoding(new PlainDSAEncoding());
        return sm2.decrypt(cipherText, KeyType.PrivateKey);
    }

    public static String sm3Digest(byte[] srcData) {
        SM3Digest digest = new SM3Digest();
        digest.update(srcData, 0, srcData.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return new String(Hex.encode(hash));
    }

    public static byte[] sm4Encrypt(String key, byte[] data) throws Exception {
        return sm4core(Cipher.ENCRYPT_MODE, key, data);
    }

    public static byte[] sm4Decrypt(String key, byte[] data) throws Exception {
        return sm4core(Cipher.DECRYPT_MODE, key, data);
    }

    private static byte[] sm4core(int type, String key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5PADDING", BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(Hex.decode(key), "SM4");
        cipher.init(type, sm4Key);

        return cipher.doFinal(data);
    }
}
