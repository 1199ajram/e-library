package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.PublisherRequest;
import Ziaat.E_library.Dto.PublisherResponse;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Publisher;
import Ziaat.E_library.Repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public PublisherResponse createPublisher(PublisherRequest request) {
        Publisher publisher = new Publisher();
        publisher.setFirstname(request.getFirstname());
        publisher.setLastname(request.getLastname());
        publisher.setIsActive(request.getIsActive());
        publisher.setAddress(request.getAddress());
        publisher.setEmail(request.getEmail());
        publisher.setCountry(request.getCountry());
        publisher.setContact(request.getContact());
        publisher.setWebsite(request.getWebsite());
        return mapToResponse(publisherRepository.save(publisher));
    }

    public PublisherResponse updatePublisher(UUID id, PublisherRequest request) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        publisher.setFirstname(request.getFirstname());
        publisher.setLastname(request.getLastname());
        publisher.setIsActive(request.getIsActive());
        publisher.setAddress(request.getAddress());
        publisher.setEmail(request.getEmail());
        publisher.setCountry(request.getCountry());
        publisher.setContact(request.getContact());
        publisher.setWebsite(request.getWebsite());
        return mapToResponse(publisherRepository.save(publisher));
    }

    public void deletePublisher(UUID id) {
        publisherRepository.deleteById(id);
    }

    public PublisherResponse getPublisher(UUID id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        return mapToResponse(publisher);
    }

    public Page<Publisher> getAllPublisher(int page, int size, String sortBy, String sortDir, String search) {
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // If search term is provided, use search query
        if (search != null && !search.trim().isEmpty()) {
            return publisherRepository.searchPublisher(search, pageable);
        }

        // Otherwise, return all authors
        return publisherRepository.findAll(pageable);
    }

    public List<Publisher> getActivePublisher() {
        return publisherRepository.findByIsActiveTrue();
    }

    private PublisherResponse mapToResponse(Publisher publisher) {
        PublisherResponse response = new PublisherResponse();
        response.setId(publisher.getPublisherId());
        response.setFirstname(publisher.getFirstname());
        response.setLastname(publisher.getLastname());
        response.setIsActive(publisher.getIsActive());
        response.setAddress(publisher.getAddress());
        response.setEmail(publisher.getEmail());
        response.setCountry(publisher.getCountry());
        response.setContact(publisher.getContact());
        response.setWebsite(publisher.getWebsite());
        return response;
    }
}

