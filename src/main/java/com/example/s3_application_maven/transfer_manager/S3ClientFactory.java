package com.example.s3_application_maven.transfer_manager;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">...</a>
 */

public class S3ClientFactory {
    public static final S3TransferManager transferManager = createCustomTm();
    public static final S3TransferManager transferManagerWithDefaults = createDefaultTm();
    public static final S3Client s3Client;
    private static S3TransferManager createCustomTm(){
        AwsCredentials credentials = AwsBasicCredentials.create("7594d98572934cbd92e7fc6f6f07a19d",
                                                                "bbd4d05a2e2a4d08a0fe494a9b5517b6");


        S3AsyncClient s3AsyncClient =
                S3AsyncClient
                        .crtBuilder()
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .region(Region.US_EAST_1)
                        .targetThroughputInGbps(20.0)
                        .minimumPartSizeInBytes(8 * MB)
                        .endpointOverride(URI.create("http://192.168.91.80:8080"))
                        .build();

        return S3TransferManager.builder()
                        .s3Client(s3AsyncClient)
                        .build();
    }

    private static S3TransferManager createDefaultTm(){
        // snippet-start:[s3.tm.java2.s3clientfactory.create_default_tm]
        // snippet-end:[s3.tm.java2.s3clientfactory.create_default_tm]
        return S3TransferManager.create();
    }

    static {
        s3Client = S3Client.builder()
                           .credentialsProvider(DefaultCredentialsProvider.create())
                           .region(Region.US_EAST_1)
                           .build();
    }

}