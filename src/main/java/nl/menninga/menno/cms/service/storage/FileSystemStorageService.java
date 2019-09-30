package nl.menninga.menno.cms.service.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService{

	@Value("${file.upload.dir:/tmp/object_files}")
	private Path uploadPath;

	@Override
	public void createDirectories() {
		try {
            Files.createDirectories(uploadPath);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
	}

	@Override
	public void createDirectories(Long cmsObjectId) throws StorageException {
		try {
            Files.createDirectories(uploadPath.resolve(cmsObjectId.toString()));
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
	}

	@Override
	public void store(Long cmsObjectId, MultipartFile file) throws StorageException{
		createDirectories(cmsObjectId);
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.uploadPath.resolve(cmsObjectId.toString() + File.separator + filename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
	}

	@Override
	public Stream<Path> loadAll(Long cmsObjectId) throws StorageException {
		try {
			Path objectPath = this.uploadPath.resolve(cmsObjectId.toString());
            return Files.walk(objectPath, 1)
                .filter(path -> !path.equals(this.uploadPath))
                .map(this.uploadPath::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
	}

	@Override
	public Path load(Long cmsObjectId, String filename) {
		return uploadPath.resolve(cmsObjectId.toString() + File.separator + filename);
	}

	@Override
	public Resource loadAsResource(Long cmsObjectId, String filename) throws StorageFileNotFoundException {
		try {
            Path file = load(cmsObjectId, filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(uploadPath.toFile());
	}

	@Override
	public void deleteAllObjectFiles(Long cmsObjectId) {
		FileSystemUtils.deleteRecursively(uploadPath.resolve(cmsObjectId.toString()).toFile());
	}

	@Override
	public void deleteObjectFile(Long cmsObjectId, String filename) {
		FileSystemUtils.deleteRecursively(uploadPath.resolve(cmsObjectId.toString() + File.separator + filename).toFile());
	}
}
