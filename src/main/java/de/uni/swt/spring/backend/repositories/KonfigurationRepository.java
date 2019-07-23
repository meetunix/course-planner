package de.uni.swt.spring.backend.repositories;

import de.uni.swt.spring.backend.data.entity.Konfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KonfigurationRepository extends JpaRepository<Konfiguration, String> {
	List<Konfiguration> findByLehrgangnameStartsWithIgnoreCase(String lehrgangname);
}
