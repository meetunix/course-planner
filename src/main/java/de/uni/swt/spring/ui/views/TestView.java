package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

import com.opencsv.CSVReader;

import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.Verwaltung.KonfigurationVerwaltung;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.bohnen.TestBohne;
import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.repositories.*;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

@Route(value = "test")
//@Secured("Dozent")
public class TestView extends VerticalLayoutSecured {

	@Autowired private KonfigurationVerwaltung kv;
	@Autowired private Bewertungsverwaltung bv;
	
	@Autowired private KonfigurationRepository konfRepo;
	@Autowired private StudentRepository studRepo;
	@Autowired private DozentRepository dozRepo;
	@Autowired private StudiengangRepository sgRepo;
	@Autowired private LeistungskomplexRepository lkRepo;
	@Autowired private LeistungsblockRepository lbRepo;
	@Autowired private LeistungRepository lRepo;
	
	private final String CSV_STUDIENGÄNGE = "csv/studiengang.csv";
	private final String CSV_STUDENTEN = "csv/studenten.csv";
	private final String CSV_DOZENTEN = "csv/dozenten.csv";
	private final String CSV_LEISTUNGSKOMPLEXE = "csv/leistungskomplexe.csv";
	private final String CSV_LEISTUNGSBLOECKE = "csv/leistungsbloecke.csv";
	private final String CSV_LEISTUNGEN = "csv/leistungen.csv";
	private final String CSV_BEWERTUNG = "csv/bewertung.csv";

    public TestView(@Autowired TestBohne bohne) {
        Button test01 = new Button("Lade Daten");
        test01.addClickListener(e -> {
//            Notification.show(
//                    bohne.getMessage(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString(), "test")
//            );
        	if (ladeTestdaten()) {
        		Notification.show("Testadten wurden erfolgreich geladen");
        		test01.setEnabled(false);
        		}
        });
        add(test01);
    }
    
    
    private boolean ladeTestdaten() {
    	//Studiengänge
    	try {
    		File file = ResourceUtils.getFile("classpath:" + CSV_STUDIENGÄNGE);
    		CSVReader csvReader = new CSVReader(new FileReader(file));
    		String[] next;
    		
    		while ((next = csvReader.readNext()) != null) {
				kv.addStudiengang(next[0], next[1]);
    		}
    	}catch(Exception e) {
    		Notification.show("Studiengänge - Exception");
    		e.printStackTrace();
    	}
					
    	// Studenten anlegen
    	try {
    		File file = ResourceUtils.getFile("classpath:" + CSV_STUDENTEN);
    		CSVReader csvReader = new CSVReader(new FileReader(file));
    		String[] next;
    		
    		while ((next = csvReader.readNext()) != null) {
    			nv.erstelleStudent(next[0], next[1], next[2], Integer.valueOf(next[3]),
    					sgRepo.getOne(next[4]));
    			nv.changePasswortStudent(studRepo.getOne(next[0]), "password");
    		}
    	}catch(Exception e) {
    		Notification.show("Studenten - Exception");
    		e.printStackTrace();
    	}

    	// Dozenten anlegen
    	try {
    		File file = ResourceUtils.getFile("classpath:" + CSV_DOZENTEN);
    		CSVReader csvReader = new CSVReader(new FileReader(file));
    		String[] next;
    		
    		while ((next = csvReader.readNext()) != null) {
    			nv.erstelleDozent(next[0], next[1], next[2]);
    			nv.changePasswortDozent(dozRepo.getOne(next[0]), "password");
    		}
    	}catch(Exception e) {
    		Notification.show("Dozenten - Exception");
    		e.printStackTrace();
    	}

    	// Leistungen anlegen
    	try {
    		//Leistungskomplexe
    		File file = ResourceUtils.getFile("classpath:" + CSV_LEISTUNGSKOMPLEXE);
    		CSVReader csvReader = new CSVReader(new FileReader(file));
    		String[] next;
    		
    		while ((next = csvReader.readNext()) != null) {
    			bv.createLeistungskomplex(next[0], Integer.valueOf(next[2]), next[1]);
    		}
    		
    		//Leistungsblöcke
    		file = ResourceUtils.getFile("classpath:" + CSV_LEISTUNGSBLOECKE);
    		csvReader = new CSVReader(new FileReader(file));
    		next = csvReader.readNext();
    		
    		while ((next = csvReader.readNext()) != null) {
    			bv.createLeistungsblock(
    					lkRepo.getOne(next[1]),
    					next[0],
    					Integer.valueOf(next[3]),
    					next[2]
    			);
    		}

    		//Einzelleistungen
    		file = ResourceUtils.getFile("classpath:" + CSV_LEISTUNGEN);
    		csvReader = new CSVReader(new FileReader(file));
    		next = csvReader.readNext();
    		
    		while ((next = csvReader.readNext()) != null) {
    			bv.addEinzelleistung(
    					next[0],
    					Integer.valueOf(next[3]),
    					next[2],
    					lbRepo.getOne(next[1])
    					);
    		}
    		lkRepo.flush();
    		lbRepo.flush();
    		lRepo.flush();
    		
    	}catch(Exception e) {
    		Notification.show("Leistung - Exception");
    		e.printStackTrace();
    	}

    	// Bewertungen (StudentenLeistung) anlegen
    	try {
    		File file = ResourceUtils.getFile("classpath:" + CSV_BEWERTUNG);
    		CSVReader csvReader = new CSVReader(new FileReader(file));
    		String[] next;

    		next = csvReader.readNext(); // erste Zeile überspringen
    		
    		List<Leistung> leistungen = new ArrayList<>();
    		Leistungsblock leistungsblock;
    		
    		while ((next = csvReader.readNext()) != null) {
    			leistungsblock = lbRepo.getOne(next[2]);
    			leistungen = leistungsblock.getLeistungsList();
    			
    			for (Leistung l : leistungen) {
    				if (l.getName().equals(next[1])) {
    					bv.addErgebnis((float) Integer.valueOf(next[3]),studRepo.getOne(next[0]),l);
    				}
    			}
    			
    		}
    	}catch(Exception e) {
    		Notification.show("Bewertung - Exception");
    		e.printStackTrace();
    	}
    	
    	return true;
    }

}
