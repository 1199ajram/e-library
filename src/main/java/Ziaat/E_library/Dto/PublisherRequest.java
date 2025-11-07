package Ziaat.E_library.Dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class PublisherRequest {
    private String firstname;
    private String lastname;
    private Boolean isActive;
    private String address;
    private String website;
    private String email;
    private String contact;
    private String country;
}

