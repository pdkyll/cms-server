package nl.menninga.menno.cms.service.storage;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	void createDirectories() throws StorageException;
	void createDirectories(Long cmsObjectId) throws StorageException;
    void store(Long cmsObjectId, MultipartFile file) throws StorageException;
    Stream<Path> loadAll(Long cmsObjectId) throws StorageException;
    Path load(Long cmsObjectId, String filename);
    Resource loadAsResource(Long cmsObjectId, String filename) throws StorageFileNotFoundException;
    void deleteAll();
    void deleteAllObjectFiles(Long cmsObjectId);
    void deleteObjectFile(Long cmsObjectId, String filename);
}