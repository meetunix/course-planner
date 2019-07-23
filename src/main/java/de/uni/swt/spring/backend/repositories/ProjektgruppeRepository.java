package de.uni.swt.spring.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uni.swt.spring.backend.data.entity.Projektgruppe;
import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;

public interface ProjektgruppeRepository extends JpaRepository<Projektgruppe, Integer> {
	List<Projektgruppe> findByName(String name);
	List<Projektgruppe> findByUebungsgruppe(Uebungsgruppe gruppe);
}
