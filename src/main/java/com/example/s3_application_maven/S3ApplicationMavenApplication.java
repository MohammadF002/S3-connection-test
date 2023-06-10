package com.example.s3_application_maven;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Random;


@SpringBootApplication
@Slf4j
public class S3ApplicationMavenApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(S3ApplicationMavenApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(final String... args) throws IOException {
        final S3Client client = s3Client();
        putS3Object(client,
                    "bucket",
                    "document",
                    "C:\\Users\\pcadmin\\Desktop\\New folder\\bigFile.txt");
    }
    // This example uses RequestBody.fromFile to avoid loading the whole file into memory.
    public static void putS3Object(S3Client s3, String bucketName, String objectKey, String objectPath) {
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                                                     .bucket(bucketName)
                                                     .key(objectKey)
                                                     .build();

            final File file = new File(objectPath);
            final var requestBody = RequestBody.fromFile(file);
            s3.putObject(putOb, requestBody);
            System.out.println("Successfully placed " + objectKey +" into bucket "+bucketName);

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static S3Client s3Client() {
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

    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }

    public static void listBucketObjects(S3Client s3, String bucketName ) {

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    //convert bytes to kbs.
    private static long calKb(Long val) {
        return val/1024;
    }

    private static void multipartUpload(S3Client s3, String bucketName, String key) throws IOException {

        int mB = 1024 * 1024;
        // snippet-start:[s3.java2.s3_object_operations.upload_multi_part]
        // First create a multipart upload and get the upload id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                                                                                                .bucket(bucketName)
                                                                                                .key(key)
                                                                                                .build();

        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();
        System.out.println(uploadId);

        // Upload all the different parts of the object
        UploadPartRequest uploadPartRequest1 = UploadPartRequest.builder()
                                                                .bucket(bucketName)
                                                                .key(key)
                                                                .uploadId(uploadId)
                                                                .partNumber(1).build();

        String etag1 = s3.uploadPart(uploadPartRequest1, RequestBody.fromByteBuffer(getRandomByteBuffer(5 * mB))).eTag();

        CompletedPart part1 = CompletedPart.builder().partNumber(1).eTag(etag1).build();

        UploadPartRequest uploadPartRequest2 = UploadPartRequest.builder().bucket(bucketName).key(key)
                                                                .uploadId(uploadId)
                                                                .partNumber(2).build();
        String etag2 = s3.uploadPart(uploadPartRequest2, RequestBody.fromByteBuffer(getRandomByteBuffer(3 * mB))).eTag();
        CompletedPart part2 = CompletedPart.builder().partNumber(2).eTag(etag2).build();


        // Finally call completeMultipartUpload operation to tell S3 to merge all uploaded
        // parts and finish the multipart operation.
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                                                                                    .parts(part1, part2)
                                                                                    .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                                              .bucket(bucketName)
                                              .key(key)
                                              .uploadId(uploadId)
                                              .multipartUpload(completedMultipartUpload)
                                              .build();

        s3.completeMultipartUpload(completeMultipartUploadRequest);
        // snippet-end:[s3.java2.s3_object_operations.upload_multi_part]
    }


}
