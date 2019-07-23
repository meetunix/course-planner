package de.uni.swt.spring.backend.data.entity;

import javax.persistence.Entity;

import static de.uni.swt.spring.backend.data.Rolle.ADMIN;

@Entity
public class Admin extends Nutzer {
    private String test;
}
