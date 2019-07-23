package de.uni.swt.spring.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;

public interface LeistungskomplexRepository extends JpaRepository<Leistungskomplex, String> {

}
