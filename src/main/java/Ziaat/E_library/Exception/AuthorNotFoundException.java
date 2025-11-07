package Ziaat.E_library.Exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Long id) {
        super("Author not found with id: " + id);
    }

    public AuthorNotFoundException(String message) {
        super(message);
    }
}