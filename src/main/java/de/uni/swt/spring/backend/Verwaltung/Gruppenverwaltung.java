package de.uni.swt.spring.backend.Verwaltung;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Projektgruppe;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.repositories.UebungsgruppeRepository;
import de.uni.swt.spring.backend.repositories.DozentRepository;
import de.uni.swt.spring.backend.repositories.ProjektgruppeRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;
import de.uni.swt.spring.backend.data.entity.Wochentag;

/**
 * 
 * Verwaltungs/Controller - Klasse für die Gruppenverwaltung. 
 *  
 */
@Controller
public class Gruppenverwaltung {
	@Autowired 
	private UebungsgruppeRepository ueRepo;
	@Autowired
	private ProjektgruppeRepository proRepo;
	@Autowired
	private StudentRepository studRepo;
	@Autowired
	private DozentRepository dozRepo;

	@Autowired
	private Nutzerverwaltung nvw;

	public Gruppenverwaltung() {
	}
	
	/**
	 * Erstellt eine neue Übungsgruppe und verknüpft diese mit einem zuständigen Dozenten.
	 * 
	 * @param name
	 * @param tag
	 * @param termin
	 * @param dozent
	 * @return
	 */
    public Uebungsgruppe createUebungsgruppe(
    		String name,
    		Wochentag tag,
    		LocalTime termin,
    		Dozent dozent) {
    	
    	Uebungsgruppe uegruppe = new Uebungsgruppe();
    	uegruppe.setName(name);
        uegruppe.setTag(tag);
        uegruppe.setTermin(termin);
        uegruppe.setDozent(dozent);
        ueRepo.save(uegruppe);
        return uegruppe;
    }

    /**
     * Löscht eine vorhandene Übungsgruppe.
     * 
     * @param uegruppe
     */
    public void deleteUebungsgruppe(Uebungsgruppe uegruppe) {
    	for (Student student : uegruppe.getStudentList()) {
    		student.setUebungsgruppe(null);
    	}
    	List <Uebungsgruppe> newUeGruppeList = new ArrayList <Uebungsgruppe>();
    	newUeGruppeList = uegruppe.getDozent().getUebungsgruppeList();
    	newUeGruppeList.remove(uegruppe);
    	Dozent doz = uegruppe.getDozent();
    	nvw.changeDozent(doz, doz.getVorname(), doz.getNachname(), newUeGruppeList);
    	
    	for (Projektgruppe projgruppe : uegruppe.getProjektgruppeList()) {
    		deleteProjektgruppe(projgruppe);
    	}
    	ueRepo.delete(uegruppe);
    }

    /**
     * 
     * Erstellt eine neue Projektgruppe und verknüft diese mit einer Übungsgruppe.
     * 
     * @param name
     * @param thema
     * @param anzahlMax
     * @param anzahlMin
     * @param anzahlAktuell
     * @param offen
     * @param uebungsgruppe
     * @return
     */
    public Projektgruppe createProjektgruppe(
    		String name,
    		String thema,
    		Integer anzahlMax,
    		Integer anzahlMin,
    		Integer anzahlAktuell,
    		Boolean offen,
    		Uebungsgruppe uebungsgruppe) {
    	
    	Projektgruppe projgruppe =  new Projektgruppe();
    	projgruppe.setName(name);
        projgruppe.setThema(thema);
        projgruppe.setAnzahlMax(anzahlMax);
        projgruppe.setAnzahlMin (anzahlMin);
        projgruppe.setAnzahlAktuell (anzahlAktuell);
        projgruppe.setOffen (offen);
    	projgruppe.setUebungsgruppe (uebungsgruppe); 
        proRepo.save(projgruppe);
        return projgruppe;
    }
    
    public void deleteProjektgruppe(Projektgruppe projgruppe) {
    	for (Student student : projgruppe.getStudentList()) {
    		removeStudentFromProjektgruppe(projgruppe, student);
    	}
    	proRepo.delete(projgruppe);
    }
    
    /**
     * Fügt einen Studenten zu einer Übungsgruppe hinzu.
     * 
     * @param uegruppe		Übungsgruppe
     * @param student		Student
     */
    
    public void addStudentToUebungsgruppe(Uebungsgruppe uegruppe, Student student) {
    	uegruppe.addStudent(student);
    	ueRepo.save(uegruppe);
    	student.setUebungsgruppe(uegruppe);
    	studRepo.save(student);
    }
   
    /**
     * Fügt einen Studenten zu einer Projektgruppe hinzu
     * 
     * @param projgruppe	Projektgruppe
     * @param student		Student
     */
    
    public void addStudentToProjektgruppe(Projektgruppe projgruppe, Student student) {
    	projgruppe.addStudent(student);
    	projgruppe.setAnzahlAktuell(projgruppe.getAnzahlAktuell() + 1);
    	proRepo.save(projgruppe);
    	student.setProjektgruppe (projgruppe);
    	studRepo.save(student);
    }
    
    /**
     * Löscht einen Student aus einer Übungsgruppe
     * 
     * @param uegruppe		Übungsgruppe aus der student entfernt werden soll
     * @param student		Student der aus der Übungsgruppe entfernt werden soll
     */
    public void removeStudentFromUebungsgruppe(Uebungsgruppe uegruppe, Student student) {
    	
		student.setUebungsgruppe(null);
		studRepo.save(student);
    	
    	//korrekten Studenten in Übungsgruppe finden
    	for (Student s: uegruppe.getStudentList()) {
    		if (s.getEmail() == student.getEmail()) {
    			student = s;
    		}
    	}
		uegruppe.removeStudentFromList(student);
		ueRepo.save(uegruppe);
    }
    
    /**
     * Löscht einen Student aus einer Projektgruppe
     * 
     * @param uegruppe		Projektgruppe aus der student entfernt werden soll
     * @param student		Student, der aus der Projektgruppe entfernt werden soll
     */
    public void removeStudentFromProjektgruppe(Projektgruppe projgruppe, Student student) {

    	student.setProjektgruppe(null);
    	studRepo.save(student);	
    	
    	//korrekten Studenten in Projektgruppe finden
    	for (Student s: projgruppe.getStudentList()) {
    		if (s.getEmail() == student.getEmail()) {
    			student = s;
    		}
    	}

    	projgruppe.removeStudentFromList(student);
    	projgruppe.setAnzahlAktuell(projgruppe.getAnzahlAktuell() - 1);
    	proRepo.save(projgruppe);
    }
    
    /**
     * 
     * Verändert die Parameter einer Projektgruppe
     * 
     * @param projgruppe
     * @param thema
     * @param anzahlMax
     * @param anzahlMin
     * @param anzahlAktuell
     * @param offen
     * @param uebungsgruppe
     */

    public void changeProjektgruppeData(
    		Projektgruppe projgruppe,
    		String thema,
    		Integer anzahlMax,
    		Integer anzahlMin,
    		Integer anzahlAktuell,
    		Boolean offen,
    		Uebungsgruppe uebungsgruppe) {
    	
    	
    	projgruppe.setThema (thema);
    	projgruppe.setAnzahlMax (anzahlMax);
    	projgruppe.setAnzahlMin (anzahlMin);
    	projgruppe.setAnzahlAktuell (anzahlAktuell);
    	projgruppe.setOffen (offen);
    	projgruppe.setUebungsgruppe (uebungsgruppe);
    	proRepo.save(projgruppe);
    }

    /**
     * Verändert die Parameter einer Übungsgruppe.
     * 
     * @param uegruppe
     * @param tag
     * @param termin
     * @param dozent
     */
    public void changeUebungsgruppeGruppeData(
    		Uebungsgruppe uegruppe,
    		Wochentag tag,
    		LocalTime termin,
    		Dozent dozent) {
    	
    	if(uegruppe.getDozent() != dozent) {
    		Dozent doz = dozRepo.findByEmail(uegruppe.getDozent().getEmail());
    		doz.getUebungsgruppeList().remove(uegruppe);
    		dozRepo.save(doz);
    	}
    	uegruppe.setTag(tag);
    	uegruppe.setTermin(termin);
    	uegruppe.setDozent(dozent);
    	ueRepo.save(uegruppe);
    }

    public void checkAutoclose() {
    	
    }

    public void changeOpenStatus(Boolean open) { //Alle Gruppen oeffnen schliessen
    	for(Projektgruppe p : proRepo.findAll()) {
    		p.setOffen(open);
    		proRepo.save(p);
    	}
    }
}
