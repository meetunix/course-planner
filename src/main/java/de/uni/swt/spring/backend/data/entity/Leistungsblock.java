package de.uni.swt.spring.backend.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Leistungsblock extends AbstractEntity {
	
	// Variablen

	@Id
	private String name;
	private Integer gewichtung;
	private String beschreibung;
	@ManyToOne
	private Leistungskomplex leistungskomplex;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Leistung> leistungsList = new ArrayList<>();

	// Konstruktoren

	public Leistungsblock(
		String name, 
		Integer gewichtung, 
		String beschreibung, 
		Leistungskomplex leistungskomplex
	) {
		this.setName(name);
		this.setGewichtung(gewichtung);
		this.setBeschreibung(beschreibung);
		this.setLeistungskomplex(leistungskomplex);
	}

	public Leistungsblock() {}

	// Public Methoden

	public void addLeistung(Leistung leistung) {
		leistungsList.add(leistung);
	}

	public boolean removeLeistung(Leistung leistung) {
		return leistungsList.remove(leistung);
	}

	// Getter und Setter

	public Integer getGewichtung() {
		return gewichtung;
	}

	public void setGewichtung(Integer gewichtung) {
		this.gewichtung = gewichtung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public Leistungskomplex getLeistungskomplex() {
		return leistungskomplex;
	}

	public void setLeistungskomplex(Leistungskomplex leistungskomplex) {
		this.leistungskomplex = leistungskomplex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Leistung> getLeistungsList() {
		return leistungsList;
	}
	
	
}
