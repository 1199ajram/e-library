package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.AuthorRequest;
import Ziaat.E_library.Dto.AuthorResponse;
import Ziaat.E_library.Exception.AuthorNotFoundException;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author();
        author.setFirstname(request.getFirstname());
        author.setLastname(request.getLastname());
        author.setIsActive(request.getIsActive());
        return mapToResponse(authorRepository.save(author));
    }

    public AuthorResponse updateAuthor(UUID id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setFirstname(request.getFirstname());
        author.setLastname(request.getLastname());
        author.setIsActive(request.getIsActive());
        return mapToResponse(authorRepository.save(author));
    }

    public void deleteAuthor(UUID id) {
        authorRepository.deleteById(id);
    }

    public Author getAuthorById(UUID id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(String.valueOf(id)));
    }

    public Page<Author> getAllAuthors(int page, int size, String sortBy, String sortDir, String search) {
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // If search term is provided, use search query
        if (search != null && !search.trim().isEmpty()) {
            return authorRepository.searchAuthors(search, pageable);
        }

        // Otherwise, return all authors
        return authorRepository.findAll(pageable);
    }

    private AuthorResponse mapToResponse(Author author) {
        AuthorResponse response = new AuthorResponse();
        response.setId(author.getAuthorId());
        response.setFirstname(author.getFirstname());
        response.setLastname(author.getLastname());
        response.setIsActive(author.getIsActive());
        return response;
    }

    public List<Author> getActiveAuthors() {
        return authorRepository.findByIsActiveTrue();
    }

}

