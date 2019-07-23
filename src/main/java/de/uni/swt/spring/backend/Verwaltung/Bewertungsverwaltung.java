package de.uni.swt.spring.backend.Verwaltung;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.opencsv.*;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.StudentLeistung;
import de.uni.swt.spring.backend.repositories.LeistungRepository;
import de.uni.swt.spring.backend.repositories.LeistungsblockRepository;
import de.uni.swt.spring.backend.repositories.LeistungskomplexRepository;
import de.uni.swt.spring.backend.repositories.StudentLeistungRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
/**
 * 
 * Verwaltungs/Controller - Klasse zur Bewertung. Hier werden Leistungskomplexe, Leistungsblöcke,
 * Leistungen und Einzelleistungen der Studenten verwaltet.
 *
 */
@Controller
public class Bewertungsverwaltung {

	@Autowired private LeistungsblockRepository leistungsblockRepo;
	@Autowired private LeistungRepository leistungRepo;
	@Autowired private StudentLeistungRepository studentLeistungRepo;
	@Autowired private LeistungskomplexRepository leistungskomplexRepo;
	@Autowired private StudentRepository studentRepository;

	public Bewertungsverwaltung() {}

	/**
	 * Erstellt einen neuen Leistungskomplex, fügt ihn zur Datenbank hinzu und gibt ihn zurück
	 * 
	 * @param name			einzigartiger Schlüßel des Leistungskomplexes
	 * @param hürde			die zu bestehende hürde für die Studenten (in Prozent)
	 * @param beschreibung	Beschreibung des Leistungskomplexes
	 * @return 				den erstellten Leistungskomplex
	 */
    public Leistungskomplex createLeistungskomplex(String name, Integer hürde, String beschreibung) { 
    	Leistungskomplex leistungskomplex = new Leistungskomplex(name, hürde, beschreibung);
    	leistungskomplexRepo.save(leistungskomplex);
    	return leistungskomplex;
    }

    /**
     * Fügt einen Leistungsblock zu dem angegebenen Leistungskomplex an
     * 
     * @param leistungskomplex	der Leistungsblock wird diesem Leistungsblock hinzugefügt
     * @param name 				Leistungsblockname (Schlüßel)
     * @param gewichtung		der Anteil von 
     * @param beschreibung		die Beschreibung des Leistungsblockes
     * @return 					gibt den Erstellten Leistungsblock zurück
     */
    public Leistungsblock createLeistungsblock(
    		Leistungskomplex leistungskomplex,
    		String name,
    		Integer gewichtung,
    		String beschreibung
    	) {
    	Leistungsblock leistungsblock = new Leistungsblock(name, gewichtung, beschreibung, leistungskomplex);
    	leistungsblockRepo.save(leistungsblock);
    	leistungskomplex.addLeistungsblock(leistungsblock);
    	leistungskomplexRepo.save(leistungskomplex);
    	return leistungsblock;
    }

    /**
     * Löscht einen Leistungskomplex aus der Datenbank inklusive die unter ihm bestehenden
     * Leistungsblöcke, Leistungen und StudentLeistungen. Vorsicht beim löschen!
     * 
     * @param leistungskomplex
     */
    public void deleteLeistungskomplex(Leistungskomplex leistungskomplex) {
    	leistungskomplexRepo.delete(leistungskomplex);
    }

    /**
     * Löscht einen Leistungsblock inklusive aller Leistungen und Studentenleistungen
     * die ihm dazugehören. Vorsicht beim löschen!
     * 
     * @param leistungsblock
     */
    public void deleteLeistungsblock(Leistungsblock leistungsblock) {
    	leistungsblockRepo.delete(leistungsblock);
    	Leistungskomplex lk = leistungsblock.getLeistungskomplex();
    	lk.removeLeistungsblock(leistungsblock);	
    	leistungskomplexRepo.save(lk);
    }

    /**
     * Ändert einen bestehenden leistungskomplex
     * 
     * @param leistungskomplex	bestehender Leistungskomplex
     * @param hürde				zu änderne Hürdek
     * @param beschreibung		zu ändernde Beschreibung
     */
    public void changeLeistungskomplex(
    		Leistungskomplex leistungskomplex,
    		Integer hürde,
    		String beschreibung
    	) {
    	leistungskomplex.setBeschreibung(beschreibung);
    	leistungskomplex.setHuerde(hürde);
    	leistungskomplexRepo.save(leistungskomplex);
    }

    /**
     * Ändert einen bestehenden leistungsblock
     * 
     * @param leistungsblock 	bestehender Leistungsblock
     * @param name				zu ändernder Name
     * @param gewichtung		zu ändernde Gewichtung
     * @param beschreibung		zu ändernde Beschreibung
     */
    public void changeLeistungsblock(
    		Leistungsblock leistungsblock,
    		Integer gewichtung,
    		String beschreibung
    	) {
    	leistungsblock.setGewichtung(gewichtung);
    	leistungsblock.setBeschreibung(beschreibung);
    	leistungsblockRepo.saveAndFlush(leistungsblock);
    }

    /**
     * Fügt eine einzelleistung in die Datenbank hinzu
     * 
     * @param name
     * @param maxPunkte
     * @param beschreibung
     * @param leistungsblock
     */
    public Leistung addEinzelleistung(
    		String name,
    		Integer maxPunkte,
    		String beschreibung,
    		Leistungsblock leistungsblock
    	) {
    	Leistung leistung = new Leistung(name, maxPunkte, beschreibung, leistungsblock);
    	leistungRepo.save(leistung);
    	leistungsblock.addLeistung(leistung);
    	leistungsblockRepo.save(leistungsblock);
    	return leistung;
    }

    /**
     * Löscht eine Leistung und die darunterliegenden StudentLeistungen aus der
     * Datenbank. Vorsicht beim löschen!
     * 
     * @param leistung
     */
    public void deleteEinzelleistung(Leistung leistung) {
    	leistungRepo.delete(leistung);
    	Leistungsblock lb = leistung.getLeistungsblock();
    	lb.removeLeistung(leistung);
    	leistungsblockRepo.save(lb);
    }

    /**
     * Ändert eine bereits erstelle Leistung
     * 
     * @param leistung
     * @param maxPunkte
     * @param beschreibung
     */
    public void changeEinzelleistung(
    		Leistung leistung,
    		Integer maxPunkte,
    		String beschreibung
    	) {
    	leistung.setBeschreibung(beschreibung);
    	leistung.setMaxPunkte(maxPunkte);
    	leistungRepo.save(leistung);
    }

    /**
     * Erstellt ein Ergebnis und hängt es an eine Leistung dran
     * 
     * @param punktzahl
     * @param student
     * @param leistung
     * @return StudentLeistung
     */
    public StudentLeistung addErgebnis(float punktzahl, Student student, Leistung leistung) {
    	StudentLeistung studentLeistung = new StudentLeistung(punktzahl, student, leistung);

    	//studentLeistung muss vor Objekten gespeichert werden, die darauf referenzieren
    	//Test: Leistung20
    	studentLeistungRepo.save(studentLeistung);

    	leistung.addStudentLeistung(studentLeistung);
    	leistungRepo.save(leistung);

    	student.addStudentLeistung(studentLeistung);
    	studentRepository.save(student);

    	return studentLeistung;
    }

    /**
     * Löscht eine Studentleistung (inklusive Punkte)
     * 
     * @param studentLeistung
     */
    public void deleteErgebnis(StudentLeistung studentLeistung) {
   
    	Leistung l = studentLeistung.getLeistung();
    	if (l != null) {
			l.removeStudentLeistung(studentLeistung);
			leistungRepo.save(l);
    	}

    	Student student =  studentLeistung.getStudent();
    	if (student != null) {
			student.removeStudentleistung(studentLeistung);
			studentRepository.save(student);
    	}
    	studentLeistungRepo.delete(studentLeistung);
    }
    

    /**
     * Ändert die Punktzahl einer StudentLeistung
     * 
     * @param studentLeistung
     * @param punktzahl
     */
    public void changeErgebnis(StudentLeistung studentLeistung, float punktzahl) {
    	studentLeistung.setPunktzahl(punktzahl);
    	studentLeistungRepo.save(studentLeistung);
    }

    /**
     * Berechnet die erreichte Prozentzahl von einem Leistungskomplex vom Studenten
     * 
     * @param leistungskomplex
     * @param student
     * @return					die erreichte Prozentzahl als float (0.0 bis 100.0)
     */
    public float berechneProzentVomKomplex(Leistungskomplex leistungskomplex, Student student) {
    	float prozentErreicht = 0.0f;
    	for (Leistungsblock lb : leistungskomplex.getLeistungsblockList()) {
			Integer maxPunkte = 0;
			float erreichtePunkte = 0;
    		for (Leistung l : lb.getLeistungsList()) {
    			maxPunkte += l.getMaxPunkte();
    			for (StudentLeistung sl : l.getStudentLeistungList()) {
    				if (sl.getStudent().getEmail() == student.getEmail()) {
						erreichtePunkte += sl.getPunktzahl();
    				}
    			}
    		}
    		prozentErreicht += lb.getGewichtung() * (erreichtePunkte / maxPunkte);
    	}
    	if (prozentErreicht > 100f) {
    		return 100f;
    	}else {
    		return Math.round(prozentErreicht * 100.0f) / 100.0f;
    	}
    }

    /**
     * prüft ob der Student alle Leistungskomplexe bestanden hat
     * 
     * @param student
     * @return			gibt true zurück falls er alles bestanden hat, false sonst
     */
    public boolean zugelassen(Student student) {
    	for (Leistungskomplex lk : leistungskomplexRepo.findAll()) {
    		if (berechneProzentVomKomplex(lk, student) < lk.getHuerde()) {
    			return false;
    		}
    	}
    	return true;
    }

    public void zeigePunkteKompletterLehrgang() {

    }

    /**
     * Eine CSV Datei der Studentendaten wird als download freigegeben
     * 
     * @param response
     * @throws Exception
     */
	@GetMapping("/export-users")
	public void export(HttpServletResponse response) throws Exception {

		String filename = "studenten_leistungen.csv";

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

		//create a csv writer
		StatefulBeanToCsv<StudentLeistung> writer = new StatefulBeanToCsvBuilder<StudentLeistung>(
				response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false)
				.build();

		//write all usjrs to csv file
		writer.write(studentLeistungRepo.findAll());
				
	}
}
