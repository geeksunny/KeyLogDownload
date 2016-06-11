package com.radicalninja.logdownload;

import LogDownload.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LogPuller {

    private static final String LOCAL_LOG_DIR = "./logs/";
    private static final String DECRYPTED_FILENAME_PREFIX = "_decrypted_";

    private final String TAG = this.getClass().getSimpleName();

    private final AwsController aws;
    private final File localLogDir = new File(LOCAL_LOG_DIR);

    public LogPuller() {
        aws = new AwsController();
    }

    public boolean doPullOperation() {

        Log.out("Beginning Log Pull Operation.\n");

        // 1. pull files
        // TODO: List files pulled. (also list files skipped?)
        final List<File> files = aws.downloadBucketContents(BuildConfig.AWS_BUCKET_NAME, localLogDir);

        Log.out("Beginning Log Decryption Operation.\n");

        // 2. decerypt files
        // TODO: list all files and their success in decryption
        decryptFileList(files);

        Log.out("Log Pull and Decryption Operation is complete.");

        return true;
    }

    private void decryptFileList(final List<File> files) {
        for (final File file : files) {
            final File parentDirectory = file.getParentFile();
            final String destinationFilename =
                    String.format(Locale.US, "%s%s", DECRYPTED_FILENAME_PREFIX, file.getName());
            final File destination = new File(parentDirectory, destinationFilename);

            Log.out(String.format(Locale.US, "IN FILE  => %s", file.getName()));
            Log.out(String.format(Locale.US, "OUT FILE => %s", destination.getName()));

            try {
                CipherUtils.decryptFile(file, destination);
                Log.out("Decryption operation successful!\n");
            } catch (IOException e) {
                Log.out(e.getMessage());
                Log.out("");
            }
        }
    }

}
