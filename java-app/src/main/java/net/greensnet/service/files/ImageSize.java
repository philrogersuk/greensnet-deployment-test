package net.greensnet.service.files;

import java.io.File;
import java.util.Optional;
import java.util.function.BiFunction;

public class ImageSize {
    private final Type type;
    private final int size;

    public ImageSize(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public ImageSize(Type type) {
        this.type = type;
        this.size = -1;
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public Optional<File> resize(File file) {
        return type.resize(file, size);
    }

    public String renameFile(String filename) {
        String[] name = filename.split("\\.");
        return name[0] + generateNewFileSuffix() + "." + name[1];
    }

    public String generateNewFileSuffix() {
        switch(type) {
            case WIDTH:
                return "_w" + size;
            case HEIGHT:
                return "_h" + size;
            default:
                return "";
        }
    }

    public enum Type {
        WIDTH (ImageService::resizeImageToWidth),
        HEIGHT (ImageService::resizeImageToHeight),
        ORIGINAL  ((file, size) -> Optional.of(file));

        private final BiFunction<File, Integer, Optional<File>> resize;

        Type(BiFunction<File, Integer, Optional<File>> resize) {
            this.resize = resize;
        }

        public Optional<File> resize(File file, int size) {
            return resize.apply(file, size);
        }
    }
}
