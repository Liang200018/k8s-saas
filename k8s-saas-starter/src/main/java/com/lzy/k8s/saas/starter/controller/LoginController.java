package com.lzy.k8s.saas.starter.controller;

import com.lzy.k8s.saas.client.dto.AccountsDTO;
import com.lzy.k8s.saas.client.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {


    @GetMapping("/accounts/{accountId}")
    public Result<AccountsDTO> getAccountsById(@PathVariable(name = "accountId") String accountId) {
        System.out.println(accountId);
        return null;
    }
}
