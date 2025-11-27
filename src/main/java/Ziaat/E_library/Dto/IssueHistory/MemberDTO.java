package Ziaat.E_library.Dto.IssueHistory;

import Ziaat.E_library.Model.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private UUID memberId;
    private String name;
    private String email;
    private String phoneNumber;
    private String memberType;
    // Add other fields you need from Member entity

    public static MemberDTO fromEntity(Member member) {
        if (member == null) return null;

        MemberDTO dto = new MemberDTO();
        dto.setMemberId(member.getMemberId());
        dto.setName(member.getFirstname()+" "+member.getLastname());
        dto.setEmail(member.getEmail());
        dto.setPhoneNumber(member.getPhone());
        // Map other fields as needed

        return dto;
    }
}