package com.radicalninja.logdownload;


import LogDownload.BuildConfig;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic implementation of Amazon S3 functionality.
 */
class AwsController {

    private final AmazonS3 s3;

    public AwsController() {
        final AWSCredentials credentials =
                new BasicAWSCredentials(BuildConfig.AWS_ACCESS_KEY, BuildConfig.AWS_SECRET_KEY);
        s3 = new AmazonS3Client(credentials);
        final Regions region = Regions.fromName(BuildConfig.AWS_BUCKET_REGION.toLowerCase());
        s3.setRegion(Region.getRegion(region));
    }

    public List<File> downloadBucketContents(final String bucketName, final File localDestination) {

        final List<File> bucketFiles = new ArrayList<>();

        // todo: catch AmazonS3Exception here!!
        ObjectListing listing = s3.listObjects(bucketName);
        final List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        while (listing.isTruncated()) {
            listing = s3.listNextBatchOfObjects(listing);
            summaries.addAll(listing.getObjectSummaries());
        }

        for (final S3ObjectSummary summary : summaries) {
            try {
                final File destination = new File(localDestination, summary.getKey());
                if (destination.isFile() && destination.length() == summary.getSize()) {
                    continue;
                }
                final ObjectMetadata object =
                        s3.getObject(new GetObjectRequest(bucketName, summary.getKey()), destination);
                if (object != null) {
                    bucketFiles.add(destination);
                } else {
                    throw new IOException(
                            String.format("An unknown error happened when trying to save %s", summary.getKey()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bucketFiles;
    }

}