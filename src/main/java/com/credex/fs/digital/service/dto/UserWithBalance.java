package com.credex.fs.digital.service.dto;

import com.credex.fs.digital.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithBalance extends AdminUserDTO {

    private String balance;

    private String walletAddress;

    public UserWithBalance(User user, String balance, String walletAddress) {
        super(user);
        this.balance = balance;
        this.walletAddress = walletAddress;
    }
}
