package net.greensnet.service.files;

import com.google.common.io.Files;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.util.Optional;

public interface ImageService {
    boolean isImageFileType(String filename);

    static Optional<File> resizeImageToWidth(File file, int width) {
        try {
            File resized = File.createTempFile(Files.getNameWithoutExtension(file.getName()) + "_w" + width,
                    '.' + Files.getFileExtension(file.getName()));

            Thumbnails.of(file)
                    .width(width)
                    .keepAspectRatio(true)
                    .toFile(resized);

            return Optional.of(resized);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    static Optional<File> resizeImageToHeight(File file, int height) {
        try {
            File resized = File.createTempFile(Files.getNameWithoutExtension(file.getName()) + "_h" + height,
                    '.' + Files.getFileExtension(file.getName()));

            Thumbnails.of(file)
                    .height(height)
                    .keepAspectRatio(true)
                    .toFile(resized);

            return Optional.of(resized);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
