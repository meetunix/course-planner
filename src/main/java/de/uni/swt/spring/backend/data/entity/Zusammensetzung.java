package de.uni.swt.spring.backend.data.entity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Zusammensetzung extends AbstractEntity{

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @ElementCollection
    @CollectionTable(name="studiengang_max")
    @MapKeyColumn(name="studiengang")
    private Map<String, Integer> maxMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name="studiengang_min")
    @MapKeyColumn(name="studiengang")
    private Map<String, Integer> minMap = new HashMap<>();

	@OneToOne(cascade=CascadeType.ALL)
    private Projektgruppe projektgruppe;

    /**
     * Fügt einen Eintrag für Studiengang s mit min und max Werten zur Zusammensetzung hinzu.
     * @param s
     * @param minAnzahl
     * @param maxAnzahl
     */
	public void add(Studiengang s, int minAnzahl, int maxAnzahl) {
    	maxMap.put(s.getStudiengang(), maxAnzahl);
    	minMap.put(s.getStudiengang(), minAnzahl);
    	
    }
	
	/**
	 * Löscht die min/max Zahlen für einen Studiengang
	 * @param s
	 */
	public void remove(Studiengang s) {
		maxMap.remove(s.getStudiengang());
		minMap.remove(s.getStudiengang());
	}

	/**
	 * Prüft ob es noch Platz in der Zusammensetzung für einen Studenten
	 * des Studienganges s gibt
	 * @param s
	 * @param anzahl
	 * @return
	 */
	public boolean nochPlatz(Studiengang s, Projektgruppe team) {
		if (maxMap.containsKey(s.getStudiengang())) {
			Integer curr = maxMap.get(s.getStudiengang());
			int studentMitStudiengangCount = 0;
			for (Student student : team.getStudentList()) {
				if (student.getStudiengang().getStudiengang() == s.getStudiengang())
					studentMitStudiengangCount++;
			}
			if (curr > studentMitStudiengangCount) {
				return true;
			}
		}
		else {
			return true;
		}
		return false;
	}
	/**
	 * Kriegt die minimale Anzahl an Studenten für Studiengang in dieser Zusammensetzung
	 * @param s
	 * @return
	 */
	public Integer getMin(Studiengang s) {
		Integer num = minMap.get(s.getStudiengang());
		return num == null ? 0 : num;
	}

	/**
	 * Kriegt die maximale Anzahl an Studenten für Studiengang in dieser Zusammensetzung
	 * @param s
	 * @return
	 */
	public Integer getMax(Studiengang s) {
		Integer num = maxMap.get(s.getStudiengang());
		return num;
	}

    // Setters und getters

	public Integer getId() {
		return id;
	}
    public Projektgruppe getProjektgruppe() {
		return projektgruppe;
	}

	public void setProjektgruppe(Projektgruppe projektgruppe) {
		this.projektgruppe = projektgruppe;
	}
}
