package com.superiorbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@Modulithic(
    systemName = "Superior Bank Core Banking",
    useFullyQualifiedModuleNames = false
)
public class SuperiorBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperiorBankApplication.class, args);
    }
}
