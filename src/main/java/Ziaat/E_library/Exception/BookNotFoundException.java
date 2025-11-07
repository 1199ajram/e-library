package Ziaat.E_library.Exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(UUID id) {
        super("Book not found with id: " + id);
    }

    public BookNotFoundException(String message) {
        super(message);
    }
}
