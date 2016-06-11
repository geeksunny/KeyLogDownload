package com.radicalninja.logdownload;

import LogDownload.BuildConfig;
import com.amazonaws.services.kms.model.InvalidCiphertextException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class CipherUtils {

    private static SecretKeySpec key = new SecretKeySpec(BuildConfig.LOG_CRYPTO_BYTES, "AES");
    private static int HEADER_LENGTH = BuildConfig.LOG_CRYPTO_BYTES.length;

    static InputStream readerEncryptedByteStream(File file) throws
            IOException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException {

        FileInputStream fin = new FileInputStream(file);
        byte[] iv = new byte[16];
        byte[] headerBytes = new byte[HEADER_LENGTH];
        if (fin.read(headerBytes) < HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid file length (failed to read the file header)");
        }
        if (headerBytes[0] != 100) {
            throw new IllegalArgumentException("The file header does not conform to our encrypted format.");
        }
        if (fin.read(iv) < 16) {
            throw new IllegalArgumentException("Invalid file length (needs a full block for iv)");
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new CipherInputStream(fin, cipher);
    }

    static BufferedReader readerEncrypted(File file) throws
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IOException {

        InputStream cis = readerEncryptedByteStream(file);
        return new BufferedReader(new InputStreamReader(cis));
    }

    public static void decryptFile(final File source, final File destination)
            throws IOException, FileEncryptionException {
        final BufferedReader in;
        try {
            in = readerEncrypted(source);
        } catch (Exception e) {
            throw new FileEncryptionException("There was an error reading the encrypted file! Maybe its not encrypted?", e);
        }
        final BufferedWriter out = new BufferedWriter(new FileWriter(destination));

        do {
            final String line = in.readLine();
            if (line == null) {
                break;
            }
            out.write(String.format(Locale.US, "%s\n", line));
        } while (true);

        in.close();
        out.close();
    }

    public static boolean isEncryptedFileEmpty(final File file) {
        return file.length() <= 16 + HEADER_LENGTH;
    }

    public static class FileEncryptionException extends Exception {
        public FileEncryptionException(String message) {
            super(message);
        }

        public FileEncryptionException(String message, Throwable cause) {
            super(message, cause);
        }

        public FileEncryptionException(Throwable cause) {
            super(cause);
        }
    }

}
