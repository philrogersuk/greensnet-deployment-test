package net.greensnet.service.files;

import java.io.File;
import java.net.URL;

interface S3FileService {
    URL uploadPublicFile(File file, String filename);
}
