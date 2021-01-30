package net.greensnet.dao.files;

import net.greensnet.domain.files.FileDetails;
import net.greensnet.domain.files.FileType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileDetailsRepository extends CrudRepository<FileDetails, Long> {

    Optional<FileDetails> findByActualFilenameEquals(String string);

    List<FileDetails> findByFileTypeEquals(FileType fileType);
}
