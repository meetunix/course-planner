package de.uni.swt.spring.backend.repositories;

import de.uni.swt.spring.backend.data.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin,String> {
    Admin findByEmail(String email);
    List<Admin> findByEmailStartsWithIgnoreCase(String email);
    List<Admin> findByNachname(String nachname);
}
