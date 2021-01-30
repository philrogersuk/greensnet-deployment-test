package net.greensnet.service.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.net.URL;

@Service
class S3FileServiceImpl implements S3FileService {

    private static final Logger logger = LoggerFactory.getLogger(S3FileServiceImpl.class);

    private final S3Client s3Client;
    private final String bucketName;

    @Autowired
    public S3FileServiceImpl() {
        Region region = Region.EU_WEST_2;
        s3Client = S3Client.builder().region(region).build();
        bucketName = "net-hendonfc-public";
    }

    @Override
    public URL uploadPublicFile(File file, String filename) {
        try {
            PutObjectResponse response = s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName).key(filename)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromFile(file));
            final URL getUrl = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(filename).build());
            logger.info("putObjectResult = " + response);
            logger.info("url = " + getUrl);
            return getUrl;
        } catch (SdkServiceException ase) {
            logger.error("Caught an AmazonServiceException", ase);
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("Key:       " + filename);
            throw ase;
        } catch (SdkClientException ace) {
            logger.error("Caught an AmazonClientException.", ace);
            logger.error("Error Message: {}, {}", filename, ace.getMessage());
            throw ace;
        }
    }
}
