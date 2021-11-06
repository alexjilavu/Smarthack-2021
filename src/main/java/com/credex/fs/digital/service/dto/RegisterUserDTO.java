package com.credex.fs.digital.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {

    @NotNull
    private String login;

    private String firstName;

    @Size(min = 4, max = 100)
    private String password;
}
