package org.superbiz.moviefun.blobstore;

import org.apache.tika.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Entity(name = "Blob1")
@NamedQuery(name = "Blob.findByName",
        query = "select b from Blob1 b where b.name = ?1")
public class Blob implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public String name;
    @Transient
    public InputStream inputStream;
    @Column(columnDefinition = "MEDIUMBLOB")
    public byte[] file;
    public String contentType;


    public Blob(String name, InputStream inputStream, String contentType) {
        this.name = name;
        this.inputStream = inputStream;
        this.contentType = contentType;
        byte[] imageBytes = new byte[0];
        try {
            imageBytes = IOUtils.toByteArray(inputStream);
            this.file = imageBytes;
        } catch (IOException e) {
        }
    }

    public Blob(Blob blobDbResult) {
        this.name = blobDbResult.name;
        this.contentType = blobDbResult.contentType;
        this.file = blobDbResult.file;
        this.inputStream = new ByteArrayInputStream(blobDbResult.file);
    }

    public Blob() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
