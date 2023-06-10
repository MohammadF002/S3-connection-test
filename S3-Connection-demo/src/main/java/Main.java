import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;

public class Main {
    private static final AWSCredentials CREDENTIALS = new BasicAWSCredentials(
            "7594d98572934cbd92e7fc6f6f07a19d",
            "bbd4d05a2e2a4d08a0fe494a9b5517b6"
    );

    public static final AmazonS3 S3_CLIENT = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(CREDENTIALS))
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://192.168.91.80:8080",
                                                                                  Regions.US_EAST_1.getName()))
            .withPayloadSigningEnabled(true)
            .build();


    public static void main(String[] args) {
        S3_CLIENT.putObject(
                "bucket",
                "bigFile.txt",
                new File("C:\\Users\\pcadmin\\Desktop\\New folder\\bigFile.txt"));
    }
}
