package de.uni.swt.spring.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.StudentLeistung;

public interface StudentLeistungRepository extends JpaRepository<StudentLeistung, Integer> {
	List <StudentLeistung> findByStudentAndLeistung(Student student, Leistung leistung);
	List<StudentLeistung> findByLeistung(Leistung leistung);
}
