package de.uni.swt.spring.backend.repositories;

import de.uni.swt.spring.backend.data.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByEmail(String email);
    List<Student> findByEmailStartsWithIgnoreCase(String email);
    List<Student> findByFreigeschaltetTrue();
    List<Student> findByFreigeschaltetFalse();
    List<Student> findByEmailContainingIgnoreCaseOrVornameContainingIgnoreCaseOrNachnameContainingIgnoreCase(String email, String vorname, String nachname);
}
