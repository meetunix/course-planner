package de.uni.swt.spring.backend.repositories;

import de.uni.swt.spring.backend.data.entity.Studiengang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudiengangRepository extends JpaRepository<Studiengang, String> {
	List <Studiengang> findByStudiengangIgnoreCase(String studiengang);
}
