package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.BookRequest;
import Ziaat.E_library.Dto.BookResponse;
import Ziaat.E_library.Dto.MemberResponse;
import Ziaat.E_library.Dto.minioDto.FileDto;
import Ziaat.E_library.Dto.minioDto.GetContentDto;
import Ziaat.E_library.Exception.BookNotFoundException;
import Ziaat.E_library.Model.*;
import Ziaat.E_library.Repository.*;
import Ziaat.E_library.Utils.Response;
import Ziaat.E_library.Utils.ResponseCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BooksService {

    @Autowired
    private BookRepository booksRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private MinioService minioService;



    @Autowired
    private AuthorRepository authorRepository;

    @Value("${minio.bucket.cover}")
    private String coverBucket;

    @Value("${minio.bucket.attachment}")
    private String attachmentBucket;


    public BookResponse createBook(BookRequest request) {
        Books book = new Books();
        mapRequestToEntity(book, request);
        return mapToResponse(booksRepository.save(book));
    }


    public BookResponse updateBook(UUID id, BookRequest request) {
        Books book = booksRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(String.valueOf(id)));

        mapRequestToEntity(book, request);
        return mapToResponse(booksRepository.save(book));
    }


    public void deleteBook(UUID id) {
        booksRepository.deleteById(id);
    }

    public BookResponse getBookById(UUID id) {
        Books book = booksRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(String.valueOf(id)));
        return mapToResponse(book);
    }


    public Page<BookResponse> getAllBooks(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search,
            UUID categoryId
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Books> booksPage;

        // Search logic
        if (search != null && !search.trim().isEmpty()) {
            booksPage = booksRepository.searchBooks(search, pageable);
        }
        else if (categoryId != null) {
            booksPage = booksRepository.searchBooksByCategoryId(categoryId, pageable);
        }
        else {
            booksPage = booksRepository.findAll(pageable);
        }

        // ---- MAP TO RESPONSE DTO ----
        return booksPage.map(this::mapToResponse);
    }


    public List<BookResponse> getAll() {
        List<Books> members = booksRepository.findAll();
        return members.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private void mapRequestToEntity(Books book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setPageCount(request.getPageCount());
        book.setEdition(request.getEdition());
        book.setPlaceOfPublisher(request.getPlaceOfPublisher());
        book.setClassificationNo(request.getClassificationNo());

        // Handle cover image upload from base64
        if (request.getCoverImageUrl() != null && !request.getCoverImageUrl().isEmpty()) {
            System.out.println("Processing cover image: " + request.getCoverImageName());
            try {
                String coverUrl = minioService.uploadFileFromBase64(
                        request.getCoverImageUrl(),
                        request.getCoverImageName(),
                        "image/png",
                        coverBucket
                );
                book.setCoverImageUrl(coverUrl);
                System.out.println("Cover image uploaded successfully: " + coverUrl);
            } catch (Exception e) {
                System.err.println("Error uploading cover image: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to upload cover image", e);
            }
        }

        // Handle attachment upload from base64
        if (request.getAttachmentUrl() != null && !request.getAttachmentUrl().isEmpty()) {
            System.out.println("Processing attachment: " + request.getAttachmentName());
            try {
                String attachmentUrl = minioService.uploadFileFromBase64(
                        request.getAttachmentUrl(),
                        request.getAttachmentName(),
                        "application/pdf",
                        attachmentBucket
                );
                book.setAttachmentUrl(attachmentUrl);
                System.out.println("Attachment uploaded successfully: " + attachmentUrl);
            } catch (Exception e) {
                System.err.println("Error uploading attachment: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to upload attachment", e);
            }
        }


        // Convert publishDate string to LocalDate
        if (request.getPublishedDate() != null) {
            book.setPublishedDate(LocalDate.parse(request.getPublishedDate()));
        }

        // ----------- PUBLISHER ---------------
        if (request.getPublisherId() != null) {
            UUID publisherId = UUID.fromString(request.getPublisherId());
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new RuntimeException("Publisher not found"));
            book.setPublisher(publisher);
        }

        // ----------- LEVEL ---------------
        if (request.getLevelId() != null) {
            UUID levelId = UUID.fromString(request.getLevelId());
            Level level = levelRepository.findById(levelId)
                    .orElseThrow(() -> new RuntimeException("Level not found"));
            book.setLevel(level);
        }


        // ----------- AUTHORS ---------------
        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<BooksAuthor> booksAuthors = request.getAuthorIds().stream().map(authorIdStr -> {

                UUID authorId = UUID.fromString(authorIdStr);
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found: " + authorIdStr));

                BooksAuthor ba = new BooksAuthor();
                ba.setAuthor(author);
                ba.setBooks(book);
                return ba;

            }).toList();

            book.setBooksAuthors(booksAuthors);
        }
    }

    public String getBookAttachment(UUID bookId,String attachment) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.valueOf(bookId)));

        // Split bucket and object
        String[] parts = attachment.split("/", 2);
        String bucket = parts[0];
        String objectName = parts[1];

        return minioService.getPresignedUrl(bucket, objectName, 10); // URL valid 10 minutes
    }



    private BookResponse mapToResponse(Books book) {
        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId().toString());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setDescription(book.getDescription());
        response.setLanguage(book.getLanguage());
        response.setPageCount(book.getPageCount());
        response.setPublishedDate(book.getPublishedDate());
        response.setCoverImageUrl(book.getCoverImageUrl());
        response.setAttachmentUrl(book.getAttachmentUrl());
        response.setPages(book.getPageCount()); // assuming pages = pageCount
        response.setPublishYear(book.getPublishedDate() != null ? book.getPublishedDate().getYear() : 0);
        response.setEdition(book.getEdition());
        response.setPlaceOfPublisher(book.getPlaceOfPublisher());
        response.setClassificationNo(book.getClassificationNo());


        // publisher
        if (book.getPublisher() != null) {
            response.setPublisherName(book.getPublisher().getFirstname()+" "+book.getPublisher().getLastname());
            response.setPublisherId(String.valueOf(book.getPublisher().getPublisherId()));
        }

        // LEVEL
        if (book.getLevel() != null) {
            Level level = book.getLevel();
            response.setLevelId(level.getLevelId());
            response.setLevelName(level.getLevelName());

            // PROGRAM comes from LEVEL
            if (level.getProgram() != null) {
                Program program = level.getProgram();
                response.setProgramId(program.getProgramId());
                response.setProgramName(program.getProgramName());

                // CATEGORY comes from PROGRAM
                if (program.getCategory() != null) {
                    Category category = program.getCategory();
                    response.setCategoryId(category.getCategoryId());
                    response.setCategoryName(category.getName());
                }
            }
        }




        // Authors
        if (book.getBooksAuthors() != null && !book.getBooksAuthors().isEmpty()) {
            List<BookResponse.AuthorDTO> authors = book.getBooksAuthors().stream().map(ba -> {
                BookResponse.AuthorDTO authorDTO = new BookResponse.AuthorDTO();
                authorDTO.setAuthorId(ba.getAuthor().getAuthorId().toString());
                authorDTO.setName(ba.getAuthor().getFirstname()+" "+ba.getAuthor().getLastname());
                return authorDTO;
            }).toList();
            response.setAuthors(authors);
        }

        // Book Copies
        if (book.getBookCopies() != null && !book.getBookCopies().isEmpty()) {
            List<BookResponse.BookCopyDTO> copies = book.getBookCopies()
                    .stream()
                    .map(copy -> {
                        BookResponse.BookCopyDTO dto = new BookResponse.BookCopyDTO();
                        dto.setCopyId(copy.getCopyId().toString());
                        dto.setBarcode(copy.getBarcode());
                        dto.setLocation(copy.getLocation());
                        dto.setCopyType(copy.getCopyType().name());
                        dto.setStatus(copy.getStatus().name());
                        dto.setCurrentBorrower(copy.getCurrentBorrower());
                        dto.setDueDate(copy.getDueDate());
                        return dto;
                    })
                    .toList();

            response.setBookCopies(copies);
        }

        return response;
    }


    public List<Books> getBooksByLevel(UUID levelId) {
        return booksRepository.findByLevel_LevelId(levelId);
    }

    public List<Books> getBooksByPublisher(UUID publisherId) {
        return booksRepository.findByPublisher_PublisherId(publisherId);
    }

    public Response getObject(GetContentDto request) throws IOException {
        Response<FileDto> resp = new Response<>();
        FileDto data = new FileDto();

        InputStream stream = minioService.getObject(request.getBucketName(), request.getAttachmentUrl());
        String[] fileName = request.getAttachmentUrl().split("[.]");
        String fileExtension = fileName[fileName.length - 1];

        final var bytes = stream.readAllBytes();
        String content = Base64.getEncoder().encodeToString(bytes);

        data.setContent(content);
        data.setFileType(fileExtension);
        resp.setStatus(true);
        resp.setData(data);
        resp.setCode(200);
        resp.setDescription("Object Returned");
        return resp;
    }

    public Response removeObject(String bucket, String objectname) throws IOException {
        var rem = minioService.removeObject(bucket, objectname);
        return new Response<>(true, ResponseCode.SUCCESS, rem, "Object Removed");
    }




}