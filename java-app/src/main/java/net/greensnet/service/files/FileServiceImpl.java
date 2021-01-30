package net.greensnet.service.files;

import com.google.common.collect.Maps;
import net.greensnet.dao.files.FileDetailsRepository;
import net.greensnet.domain.files.FileDetails;
import net.greensnet.domain.files.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileDetailsRepository fileDetailsRepository;
    private final S3FileService s3FileService;

    @Autowired
    public FileServiceImpl(FileDetailsRepository fileDetailsRepository,
                           S3FileService s3FileService) {
        this.fileDetailsRepository = fileDetailsRepository;
        this.s3FileService = s3FileService;
    }

    @Override
    public boolean uploadDocument(MultipartFile originalFile, String filename) {
        File file = null;
        try {
            file = convertMultipartFileToFile(originalFile);

            uploadFile(originalFile.getOriginalFilename(), "documents/" + filename, file, FileType.DOCUMENT);
        } catch (Exception e) {
            logger.error("Could not upload file", e);
            return false;
        } finally {
            if (null != file && file.exists()) {
                file.delete();
            }
        }
        return true;
    }

    @Override
    public List<FileDetails> getAllDocuments() {
        return fileDetailsRepository.findByFileTypeEquals(FileType.DOCUMENT);
    }


    @Override
    public boolean uploadImage(MultipartFile originalFile, String filename, List<ImageSize> sizes) {
        File file = null;
        try {
            file = convertMultipartFileToFile(originalFile);
            Map<String, File> files = getResizedFilesToUpload(filename, sizes, file);

            for (Map.Entry<String, File> entry: files.entrySet()) {
                uploadFile(originalFile.getOriginalFilename(), entry.getKey(), entry.getValue(), FileType.IMAGE);
            }

        } catch (Exception e) {
            logger.error("Could not upload file", e);
            return false;
        } finally {
            if (null != file && file.exists()) {
                file.delete();
            }
        }
        return true;
    }

    private Map<String, File> getResizedFilesToUpload(String filename, List<ImageSize> sizes, File file) {
        Map<String, File> files = Maps.newHashMap();

        if (!sizes.isEmpty()) {
            for (ImageSize size: sizes) {
                Optional<File> resizedFile = size.resize(file);
                if (!resizedFile.isPresent()) {
                    logger.warn("File could not be resized. File: {}, ResizeType: {}", file, size);
                } else {
                    File f = resizedFile.get();
                    files.put(size.renameFile(filename), f);
                }
            }
        } else {
            files.put(filename, file);
        }
        return files;
    }

    private void uploadFile(String originalFileName, String newFilename, File file, FileType type) {
        URL url = s3FileService.uploadPublicFile(file, newFilename);
        FileDetails details = FileDetails.builder().actualFilename(newFilename)
                .originalFilename(originalFileName)
                .awsUrl(url.toString())
                .fileType(type)
                .build();
        fileDetailsRepository.save(details);
        file.delete();
    }

    @Override
    public Optional<String> getUrl(String filename) {
        Optional<FileDetails> details = fileDetailsRepository.findByActualFilenameEquals(filename);
        return details.map(FileDetails::getAwsUrl);
    }

    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
