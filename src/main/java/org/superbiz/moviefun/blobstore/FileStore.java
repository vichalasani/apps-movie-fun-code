package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {
    private final Tika tika = new Tika();

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] imageBytes = IOUtils.toByteArray(blob.inputStream);
            outputStream.write(imageBytes);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File targetFile = new File(name);

        if (!targetFile.exists()) {
            return Optional.empty();
        }

        return Optional.of(new Blob(
                name,
                new FileInputStream(targetFile),
                tika.detect(targetFile)
        ));
    }

    @Override
    public void deleteAll() {

    }
}
