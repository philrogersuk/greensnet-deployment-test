package net.greensnet.web.admin;

import net.greensnet.domain.files.FileDetails;
import net.greensnet.service.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class DocumentController {

    private static final String DOCUMENTS_LIST_PAGE = "th_admin/documents/list";

    private static final String DOCUMENTS = "documents";

    private final FileService fileService;

    @Autowired
    public DocumentController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/AdminDocuments")
    public String getAllIDocuments(Model model) {
        List<FileDetails> items = fileService.getAllDocuments();

        model.addAttribute(DOCUMENTS, items);

        return DOCUMENTS_LIST_PAGE;
    }

    @PostMapping("/AdminDocuments")
    public String addDocument(Model model,
                              @RequestParam(value="filename") String targetFilename,
                              @RequestParam(value="document") MultipartFile document) {
        fileService.uploadDocument(document, targetFilename);

        return getAllIDocuments(model);
    }
}
