package de.uni.swt.spring.backend.repositories;

import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DozentRepository extends JpaRepository<Dozent, String> {
    Dozent findByEmail(String email);
    List<Dozent> findByEmailStartsWithIgnoreCase(String email);
    List<Dozent> findByNachname(String nachname);
}
