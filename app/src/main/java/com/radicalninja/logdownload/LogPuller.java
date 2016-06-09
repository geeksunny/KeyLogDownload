package com.radicalninja.logdownload;

import LogDownload.BuildConfig;

import java.io.File;
import java.util.List;

public class LogPuller {

    private static final String LOCAL_LOG_DIR = "./logs/";

    private final AwsController aws;

    public LogPuller() {
        aws = new AwsController();
    }

    public boolean doPullOperation() {

        // 1. pull files
        final File destination = new File(LOCAL_LOG_DIR);
        final List<File> files = aws.downloadBucketContents(BuildConfig.AWS_BUCKET_NAME, destination);
        // 2. decerypt files

        return true;
    }

}
