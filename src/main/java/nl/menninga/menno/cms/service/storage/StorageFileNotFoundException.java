package nl.menninga.menno.cms.service.storage;

public class StorageFileNotFoundException extends StorageException {
	
	private static final long serialVersionUID = -3313497779016914729L;

	public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}