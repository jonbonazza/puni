package com.github.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public class SSLUtil {

    public static X509Certificate loadCertificate(String path) throws CertificateException, IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate)cf.generateCertificate(is);
        } finally {
            if (is != null)
                is.close();
        }
    }
}
