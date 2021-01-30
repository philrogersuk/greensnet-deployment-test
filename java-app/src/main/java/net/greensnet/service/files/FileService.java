package net.greensnet.service.files;

import net.greensnet.domain.files.FileDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FileService {

    boolean uploadDocument(MultipartFile originalFile, String filename);
    boolean uploadImage(MultipartFile file, String filename, List<ImageSize> sizes);

    List<FileDetails> getAllDocuments();

    Optional<String> getUrl(String filename);
}
