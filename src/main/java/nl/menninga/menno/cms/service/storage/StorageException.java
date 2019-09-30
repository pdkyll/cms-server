package nl.menninga.menno.cms.service.storage;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 2596221567320454635L;

	public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}