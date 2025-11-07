package Ziaat.E_library.Dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PublisherResponse {
    private UUID id;
    private String firstname;
    private String lastname;
    private Boolean isActive;
    private String address;
    private String website;
    private String email;
    private String contact;
    private String country;
}

