// https://docs.aws.amazon.com/AmazonS3/latest/userguide/upload-objects.html
package com.example.s3_application_maven;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

public class UploadObject1 {

    private static S3AsyncClient createCustomTm(){
        AwsCredentials credentials = AwsBasicCredentials.create("7594d98572934cbd92e7fc6f6f07a19d",
                                                                "bbd4d05a2e2a4d08a0fe494a9b5517b6");


        return S3AsyncClient
                .crtBuilder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * MB)
                .endpointOverride(URI.create("http://192.168.91.80:8080"))
                .build();
    }

    public static S3Client createS3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create("7594d98572934cbd92e7fc6f6f07a19d",
                                                                "bbd4d05a2e2a4d08a0fe494a9b5517b6");

        return S3Client
                .builder()
                .httpClientBuilder(ApacheHttpClient.builder()
                                                   .maxConnections(100)
                                                   .connectionTimeout(Duration.ofSeconds(5)))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create("http://192.168.91.80:8080"))
                .region(Region.US_EAST_1)
                .build();
    }


    public static void main(String[] args) {

        S3AsyncClient client = createCustomTm();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                         .bucket("bucket")
                                                         .key("bigFile")
                                                         .build();

        // Put the object into the bucket
        CompletableFuture<PutObjectResponse> future =
                client.putObject(objectRequest,
                                 AsyncRequestBody.fromFile(Paths.get("C:\\Users\\pcadmin\\Desktop\\New folder\\bigFile.txt")));
        future.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                    System.out.println("Object uploaded. Details: " + resp);
                } else {
                    // Handle error
                    err.printStackTrace();
                }
            } finally {
                // Only close the client when you are completely done with it
                client.close();
            }
        });

        future.join();
    }
}