package net.greensnet.service.files;

import com.google.common.io.Files;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    private static final String GIF = "GIF";
    private static final String JPG = "JPG";
    private static final String JPEG = "JPEG";
    private static final String PNG = "PNG";

    @Override
    public boolean isImageFileType(String filename) {
        String filetype = Files.getFileExtension(filename);

        return filetype.toUpperCase().equals(JPG) ||
                filetype.toUpperCase().equals(JPEG) ||
                filetype.toUpperCase().equals(PNG) ||
                filename.toUpperCase().equals(GIF);
    }
}
