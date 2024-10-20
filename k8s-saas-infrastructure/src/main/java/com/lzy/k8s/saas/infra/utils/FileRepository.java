package com.lzy.k8s.saas.infra.utils;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
public class FileRepository {

    public Path create(Path path, String fileName) {
        Path filePath = path.resolve(fileName);
        try {
            Files.createFile(filePath);
            return filePath;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public String read(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public String update(Path path, String newContent) {
        try {
            Files.write(path, newContent.getBytes());
            return newContent;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
