package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.tika.Tika;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {
    private final AmazonS3Client s3Client;
    private final String s3BucketName;
    private final Tika tika = new Tika();
    Logger logger = LoggerFactory.getLogger(getClass().getName());

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.s3Client = s3Client;
        this.s3BucketName = s3BucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);
        metadata.setContentLength(blob.inputStream.available());
        logger.info("Storing into S3 name="+blob.name);
        s3Client.putObject(s3BucketName, blob.name, blob.inputStream, metadata);

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        logger.info("Retrieving the file from S3 name="+name);
        if (!s3Client.doesObjectExist(s3BucketName, name)) {
            return Optional.empty();
        }
        S3Object object = s3Client.getObject(s3BucketName, name);
        logger.info("File retrieved...");

        S3ObjectInputStream objectContent = object.getObjectContent();

        byte[] bytes = IOUtils.toByteArray(objectContent);

        return Optional.of(new Blob(
                name,
                new ByteArrayInputStream(bytes),
                tika.detect(bytes)
        ));
    }

    @Override
    public void deleteAll() {

    }
}
