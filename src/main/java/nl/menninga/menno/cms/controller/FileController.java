package nl.menninga.menno.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nl.menninga.menno.cms.service.storage.StorageException;
import nl.menninga.menno.cms.service.storage.StorageService;

@RestController
@CrossOrigin
public class FileController {

	@Autowired
	private StorageService storageService;
	
	@PostMapping("api/file")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> handleFileUpload(@RequestParam("id") Long cmsObjectId, @RequestParam("file") MultipartFile file) {
		try {
			storageService.store(cmsObjectId, file);
		}catch(StorageException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"isFileUploadSucces\": false, \"error\": " + e + ", \"fileName\": \"" + file.getOriginalFilename() + "\" }");
		}
        return ResponseEntity.ok("{ \"isFileUploadSucces\": true, \"fileName\": \"" + file.getOriginalFilename() + "\" }");
    }
	
	@GetMapping("/api/file/{id}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("id") Long cmsObjectId, @PathVariable("filename") String filename) {
        Resource file = storageService.loadAsResource(cmsObjectId, filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
	
	@DeleteMapping("/api/file/{id}/{filename:.+}")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> deleteFile(@PathVariable("id") Long cmsObjectId, @PathVariable("filename") String filename) {
        storageService.deleteObjectFile(cmsObjectId, filename);
        return ResponseEntity.ok("{ \"isFileDeletionSucces\": true, \"fileName\": \"" + filename + "\" }");
    }
}
