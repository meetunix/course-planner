package de.uni.swt.spring.backend.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Leistungskomplex extends AbstractEntity {

	// Variablen

	@Id
	private String name;
	private Integer huerde;
	private String beschreibung;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Leistungsblock> leistungsblockList = new ArrayList<>();

	// Konstruktoren

	public Leistungskomplex(String name, Integer huerde, String beschreibung) {
        this.setName(name);
        this.setHuerde(huerde);
        this.setBeschreibung(beschreibung);
	}
	public Leistungskomplex() {}

	// Public Methoden

	public void addLeistungsblock(Leistungsblock leistungsblock) {
		//leistungsblock.setLeistungskomplex(this);
		leistungsblockList.add(leistungsblock);
	}

	public boolean removeLeistungsblock(Leistungsblock leistungsblock) {
		return leistungsblockList.remove(leistungsblock);
	}

	/**
	 * Prüft ob die Gewichtungen der Leistungsblöcke sich zu 100% addieren lassen
	 * @return true falls ja, sonst false
	 */
	public boolean istKorrekt() {
		float gewichtung = 0.0f;
		for (Leistungsblock leistungsblock : leistungsblockList) {
			gewichtung += leistungsblock.getGewichtung();
		}
		if (gewichtung == 100.0f) // TODO: richtiges float comparison
			return true;
		else 
			return false;
	}

	// Getter und Setter
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getHuerde() {
		return huerde;
	}

	public void setHuerde(Integer huerde) {
		this.huerde = huerde;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public List<Leistungsblock> getLeistungsblockList() {
		return leistungsblockList;
	}
}
