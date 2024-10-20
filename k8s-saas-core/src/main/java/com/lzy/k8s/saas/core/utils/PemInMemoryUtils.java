package com.lzy.k8s.saas.core.utils;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.utils.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public class PemInMemoryUtils {

    private static final Logger log = LoggerFactory.getLogger(PemInMemoryUtils.class.getName());

    private static final FileRepository fileRepository = new FileRepository();

    private static FileSystem fileSystem;

    private static final Path aws;


    // create aws pem directory
    static {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        aws = fileSystem.getPath("/pem");
        if (!Files.exists(aws)) {
            try {
                Files.createDirectory(aws);
            } catch (IOException e) {
                log.error("PemInMemoryUtils init error, err: ", e);
            }
        }
    }

    public Path getAws() {
        return aws;
    }

    public static Path savePemFile(String accountId, String keyPairName, String keyMaterial) {
        try {
            String fileName=null;
            if (!keyPairName.endsWith(".pem")) {
                fileName = keyPairName + ".pem";
            } else {
                fileName = keyPairName;
            }
            Path accountPath = aws.resolve(accountId);
            if (!Files.exists(accountPath)) {
                Files.createDirectory(accountPath);
            }
            Path filePath = accountPath.resolve(fileName);
            fileRepository.create(accountPath, fileName);
            fileRepository.update(filePath, keyMaterial);
            return filePath;
        } catch (Throwable e) {
            log.error("savePemFile fail, accountId: {}, key pair: {}, err: ", accountId, keyPairName, e);
            return null;
        }
    }

    public static void deletePemFile(Path pemFilePath) {
        try {
            fileRepository.delete(pemFilePath);
            if (!Files.exists(pemFilePath)) {
                log.info("delete PemFile success");
            }
        } catch (Throwable e) {
            log.error("delete PemFile fail, path: {}, err: ", pemFilePath, e);
        }
    }

    public static String readPemFile(Path pemFilePath) {
        try {
            if (Files.exists(pemFilePath)) {
                return fileRepository.read(pemFilePath);
            }
            throw new SystemException(ErrorCode.INVALID_PARAM, "the pem file path not exist");
        } catch (Throwable e) {
            log.error("read PemFile fail, path: {}, err: ", pemFilePath, e);
            return null;
        }
    }

}

