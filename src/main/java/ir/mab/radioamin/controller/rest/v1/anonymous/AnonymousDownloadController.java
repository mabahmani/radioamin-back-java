package ir.mab.radioamin.controller.rest.v1.anonymous;

import ir.mab.radioamin.service.FileStorageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AnonymousDownloadController {

    FileStorageService fileStorageService;

    @Autowired
    public AnonymousDownloadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/uploads/**")
    void downloadFile(HttpServletRequest request, HttpServletResponse response){

        Resource resource = fileStorageService.loadFileAsResource(request.getServletPath());

        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        try {
            response.setContentType(contentType);
            IOUtils.copy(resource.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
    }
}
