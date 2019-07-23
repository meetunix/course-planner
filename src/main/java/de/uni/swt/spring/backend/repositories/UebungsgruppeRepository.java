package de.uni.swt.spring.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;

public interface UebungsgruppeRepository extends JpaRepository<Uebungsgruppe, Integer> {
	List<Uebungsgruppe> findByName(String name);
}
