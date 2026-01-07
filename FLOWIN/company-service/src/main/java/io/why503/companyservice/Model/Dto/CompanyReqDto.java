// 등록

package io.why503.companyservice.Model.Dto;

import io.why503.companyservice.Model.Ett.Enum.CompanyBank;
import lombok.Getter;

@Getter
public class CompanyReqDto {

    // private Long userSq;

    private CompanyBank companyBank;
    private String account;
    private String companyName;
    private String ownerName;
    private String companyPhone;
    private String companyEmail;
    private String companyAddr;
    private String companyPost;

    private Long amount;
}
