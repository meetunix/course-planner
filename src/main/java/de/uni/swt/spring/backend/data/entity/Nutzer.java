package de.uni.swt.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Email;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Nutzer extends AbstractEntity {
    @Id
    @Email
    private String email;
    private String passwort;
    private boolean passwortChanged;
    private String vorname;
    private String nachname;
    private String role;

    public Nutzer() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public boolean isPasswortChanged() {
        return passwortChanged;
    }

    public void setPasswortChanged(boolean passwortChanged) {
        this.passwortChanged = passwortChanged;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
