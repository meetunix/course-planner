package de.uni.swt.spring.backend.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Leistung extends AbstractEntity {

	// Variablen

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	private String name;
	private Integer maxPunkte;
	private String beschreibung;
	@ManyToOne
	private Leistungsblock leistungsblock;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<StudentLeistung>studentLeistungList = new ArrayList<>();

	// Konstruktoren

	/**
	* @param name					ein unique (einzigartiger?) Schlüssel
	* @param maxPunkte				die maximale anzahl an Punkten die erreicht werden kann
	* @param beschreibung			eine Beschreibung für die Leistung
	* @param elternLeistungsblock	der Leistungsblock der dieser Leistung enthält
	*/
	public Leistung(
		String name,
		Integer maxPunkte,
		String beschreibung,
		Leistungsblock elternLeistungsblock
	) {
		this.setName(name);
		this.setMaxPunkte(maxPunkte);
		this.setBeschreibung(beschreibung);
		this.setLeistungsblock(elternLeistungsblock);
	}

	public Leistung() {
	}

	// Public Methoden

	/**
	 * @param studentLeistung	eine initialisierte StudentLeistung
	 */
	public void addStudentLeistung(StudentLeistung studentLeistung) {
			this.studentLeistungList.add(studentLeistung);
	}
	public boolean removeStudentLeistung(StudentLeistung studentLeistung) {
		return this.studentLeistungList.remove(studentLeistung);
	}

	// Getter und setter

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMaxPunkte() {
		return maxPunkte;
	}

	public void setMaxPunkte(Integer maxPunkte) {
		this.maxPunkte = maxPunkte;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public Leistungsblock getLeistungsblock() {
		return leistungsblock;
	}

	public void setLeistungsblock(Leistungsblock leistungsblock) {
		this.leistungsblock = leistungsblock;
	}

	public List<StudentLeistung> getStudentLeistungList() {
		return studentLeistungList;
	}
}
