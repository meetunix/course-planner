package de.uni.swt.spring.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import de.uni.swt.spring.backend.data.entity.Zusammensetzung;

public interface ZusammensetzungRepository extends JpaRepository<Zusammensetzung, Integer> {

}
