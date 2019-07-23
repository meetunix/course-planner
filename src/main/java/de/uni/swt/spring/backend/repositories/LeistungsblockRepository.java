package de.uni.swt.spring.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;

public interface LeistungsblockRepository extends JpaRepository<Leistungsblock, String>{
	List <Leistungsblock> findByLeistungskomplex(Leistungskomplex leistungskomplex);
}
