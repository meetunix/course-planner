package de.uni.swt.spring.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;

public interface LeistungRepository extends JpaRepository<Leistung, Integer> {
	List <Leistung> findByLeistungsblock (Leistungsblock leistungsblock);
}
