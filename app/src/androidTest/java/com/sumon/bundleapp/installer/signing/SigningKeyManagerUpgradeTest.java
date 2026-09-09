package com.sumon.bundleapp.installer.signing;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.test.InstrumentationTestCase;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

public class SigningKeyManagerUpgradeTest extends InstrumentationTestCase {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String CURRENT_ALIAS = "bai_apk_signing_key";
    private static final String LEGACY_ALIAS = "sai_apk_signing_key";

    public void testGetOrCreateReturnsLegacyCertificateWhenOnlyLegacyKeyExists() throws Exception {
        KeyStore keyStore = loadKeyStore();
        keyStore.deleteEntry(CURRENT_ALIAS);
        keyStore.deleteEntry(LEGACY_ALIAS);

        try {
            generateLegacyKey();
            Certificate legacyCertificate = loadKeyStore().getCertificate(LEGACY_ALIAS);
            assertNotNull(legacyCertificate);

            SigningKey result = SigningKeyManager.getInstance().getOrCreate();

            assertTrue(Arrays.equals(legacyCertificate.getEncoded(), result.certificate().getEncoded()));
            assertFalse(loadKeyStore().containsAlias(CURRENT_ALIAS));
        } finally {
            keyStore = loadKeyStore();
            keyStore.deleteEntry(CURRENT_ALIAS);
            keyStore.deleteEntry(LEGACY_ALIAS);
        }
    }

    private static void generateLegacyKey() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Calendar expiry = (Calendar) calendar.clone();
        expiry.add(Calendar.YEAR, 1);

        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(LEGACY_ALIAS, KeyProperties.PURPOSE_SIGN)
                .setKeySize(2048)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setCertificateSubject(new X500Principal("CN=Legacy SAI"))
                .setCertificateSerialNumber(BigInteger.ONE)
                .setCertificateNotBefore(calendar.getTime())
                .setCertificateNotAfter(expiry.getTime())
                .build();

        KeyPairGenerator generator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
        generator.initialize(spec);
        generator.generateKeyPair();
    }

    private static KeyStore loadKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return keyStore;
    }
}
