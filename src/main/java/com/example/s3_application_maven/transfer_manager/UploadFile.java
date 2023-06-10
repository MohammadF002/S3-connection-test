package com.example.s3_application_maven.transfer_manager;

import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.net.URL;
import java.nio.file.Paths;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">...</a>
 */

public class UploadFile {
    public static final String bucketName = "bucket";
    public static final String key = "document";
    public static String filePath = "src/main/resources/static/hello.txt";

    public static void main(String[] args) {

        System.out.println(uploadFile(S3ClientFactory.transferManager, bucketName, key, filePath));
    }

    public static String uploadFile(S3TransferManager transferManager, String bucketName,
                             String key, String filePath) {
        UploadFileRequest uploadFileRequest =
                UploadFileRequest.builder()
                                 .putObjectRequest(b -> b.bucket(bucketName).key(key))
                                 .addTransferListener(LoggingTransferListener.create())
                                 .source(Paths.get(filePath))
                                 .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        return uploadResult.response().eTag();
    }

    private void setUp(){
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        // get the file system path to the provided file to upload
        URL resource = UploadFile.class.getClassLoader().getResource("image.png");
        filePath = resource.getPath();
    }

}
