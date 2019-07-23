package de.uni.swt.spring.backend.Verwaltung;

import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.data.entity.Konfiguration.EinschreibStrategie;
import de.uni.swt.spring.backend.repositories.StudiengangRepository;
import de.uni.swt.spring.backend.repositories.KonfigurationRepository;
import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.data.entity.Konfiguration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import antlr.collections.List;

import javax.persistence.Entity;

/**
 * 
 * Verwaltungs/Controller - Klasse für die Konfigurationsparameter. Lehrgangsname,
 * Einschreibstrategie und die Deadline werden hier verwaltet.
 *
 */
@Controller
public class KonfigurationVerwaltung {
	
	@Autowired
	private KonfigurationRepository configrepo;
	@Autowired
	private StudiengangRepository studiengangrepo; 
	
	/**
	 * Ändert die Beschreibung der Lehrgangskonfiguration
	 * 
	 * @param lehrgangname
	 * @param beschreibung
	 */
	public void changeBeschreibung(String lehrgangname, String beschreibung) {
    	Konfiguration k = getKonfiguration();
		k.setBeschreibung(beschreibung);
		configrepo.save(k);
	}
	
	/**
	 * Ändert den Modus der Lehrgangkonfiguration
	 * @param lehrgangname
	 * @param neuerModus	kann Automatisch oder Selbständig sein
	 */
    public void changeMode(String lehrgangname, EinschreibStrategie neuerModus) {
    	Konfiguration k = getKonfiguration();
    	k.setModus(neuerModus);
    	configrepo.save(k);
    }

    /**
     * Ändert die deadline der Lehrgangskonfiguration
     * @param lehrgangname
     * @param date
     */
    public void changeDeadline(String lehrgangname, LocalDate date) {
    	Konfiguration k = getKonfiguration();
    	k.setDeadline(date);
    	configrepo.save(k);
    }

    /**
     * Ändert das globale Thema der Lehrgangskonfiguration
     * 
     * @param lehrgangname
     * @param globalesThema
     */
    public void changeGlobalesThema(String lehrgangname, String globalesThema) {
    	Konfiguration k = getKonfiguration();
    	k.setGlobalesThema(globalesThema);
    	configrepo.save(k);
    }

    /**
     * Sucht in der Datenbank ob bereits eine Konfiguration vorhanden ist, falls nicht
     * dann wird eine neue Konfiguration erstellt.
     * 
     * @return die Konfiguration aus der Datenbank falls vorhanden, sonst eine neue
     */
    public Konfiguration getKonfiguration() {
		Konfiguration k;
		if (configrepo.existsById(getLehrgangname())){
			k = configrepo.getOne(getLehrgangname());
		}
		else {
			k = new Konfiguration();
			configrepo.save(k);
		}
		return k;
    }

    /**
     * @return den Lehrgangnamen der Konstant ist
     */
    public String getLehrgangname() {
		return "swt-planer";
    }

    /**
     * Fügt einen Studiengang in die Datenbank hinzu
     * 
     * @param studiengang	Name des Studienganges z. B. "Informatik"
     * @param kurzname		Kürzel des Studienganges z. B. "INF"
     */
    public void addStudiengang(String studiengang, String kurzname) {
    	Studiengang st = new Studiengang ();
    	st.setStudiengang (studiengang);
    	st.setKurzname (kurzname);
    	studiengangrepo.save(st);
    }

    /**
     * Entfernt einen Studiengang
     * 
     * @param studiengangToRemove
     */
    public void removeStudiengang(String studiengangToRemove) {
    	//TODO: Warnung ausgeben
    	if(studiengangrepo.existsById(studiengangToRemove)) {
    		Studiengang st = studiengangrepo.getOne(studiengangToRemove);
    		studiengangrepo.delete(st);
    	}    	
    }

    /**
     * Ändert den Namen eines Studienganges
     * 
     * @param studiengangNameAlt
     * @param studiengangNameNeu
     * @param kurzname
     */
    public void changeStudiengang(String studiengangNameAlt, String studiengangNameNeu, String kurzname) {
    	if(studiengangrepo.existsById(studiengangNameAlt)) {
			removeStudiengang(studiengangNameAlt);
    	}       	
		addStudiengang(studiengangNameNeu, kurzname);
    }

    /**
     * @return Alle Studiengänge aus der Datenbank
     */
    public ArrayList<Studiengang> getAlleStudiengänge() {
    	return (ArrayList<Studiengang>)studiengangrepo.findAll();
    }

    public void exportDatensatz() {
    	//TODO: Leistung für jeden Studenten einlesen und als csv Datei ausgeben.
    }
}
