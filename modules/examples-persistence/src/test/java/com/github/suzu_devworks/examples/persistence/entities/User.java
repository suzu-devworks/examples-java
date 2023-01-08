package com.github.suzu_devworks.examples.persistence.entities;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    private BigInteger id;

    private String name;

    private String email;

    private String password;

}
