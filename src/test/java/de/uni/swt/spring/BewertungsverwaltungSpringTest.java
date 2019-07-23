package de.uni.swt.spring;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import de.uni.swt.spring.backend.Verwaltung.*;
import de.uni.swt.spring.backend.data.entity.*;
import de.uni.swt.spring.backend.repositories.*;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@TestInstance(Lifecycle.PER_CLASS) // erlaubt das @BeforeAll nicht statisch sein muss
class BewertungsverwaltungSpringTest {

	@Autowired
	private LeistungsblockRepository leistungsblockRepo;
	@Autowired
	private LeistungRepository leistungRepo;
	@Autowired
	private StudentLeistungRepository studentLeistungRepo;
	@Autowired
	private LeistungskomplexRepository leistungskomplexRepo;
	@Autowired
	private StudiengangRepository studiengangRepo;
	@Autowired
	private StudentRepository studentRepo;

	@Autowired
	private Bewertungsverwaltung bv;
	@Autowired
	private Nutzerverwaltung nv;
	@Autowired
	private KonfigurationVerwaltung kv;

	public Leistungskomplex leistungskomplex;
	private Leistungsblock leistungsblockA;
	private Leistungsblock leistungsblockB;

	private Leistung leistung;

	private Student student;

	private  Integer idLeistungA1;
	private  Integer idLeistungA2;
	private  Integer idLeistungB1;
	private  Integer idLeistungB2;

	private final String idLeistungskomplex01 = "Testkomplex01";
	private final String idLeistungsblockA = "lbA";
	private final String idLeistungsblockB = "lbB";

	
	private final String idStudiengang = "Informatik B.Sc.";

	private final String idStudent01 = "student@uni-rostock.test";
	private final Float[] einzelLeistungenDefault = { 17.5f, 22f, 5f, 10f };
	
	@BeforeAll
	private void cleanDB() {
		leistungsblockRepo.deleteAll();
		leistungRepo.deleteAll();
		leistungskomplexRepo.deleteAll();
		studiengangRepo.deleteAll();
		studentLeistungRepo.deleteAll();
	}

	@BeforeEach
	private void setupBewertungsverwaltung() {

		bv.createLeistungskomplex(idLeistungskomplex01, 50, "Beschreibung des Testkomplex");
		leistungskomplex = leistungskomplexRepo.getOne(idLeistungskomplex01);

		bv.createLeistungsblock(leistungskomplex, idLeistungsblockA, 60, "Beschreibung lbA");
		bv.createLeistungsblock(leistungskomplex, idLeistungsblockB, 40, "Beschreibung lbB");
		leistungsblockA = leistungsblockRepo.getOne(idLeistungsblockA);
		leistungsblockB = leistungsblockRepo.getOne(idLeistungsblockB);

		idLeistungA1 = (bv.addEinzelleistung("lA1", 20, "Leistung A1", leistungsblockA)).getId();
		idLeistungA2 = (bv.addEinzelleistung("lA2", 40, "Leistung A2", leistungsblockA)).getId();
		idLeistungB1 = (bv.addEinzelleistung("lB1", 20, "Leistung B1", leistungsblockB)).getId();
		idLeistungB2 = (bv.addEinzelleistung("lB2", 20, "Leistung B2", leistungsblockB)).getId();

		// vollständig ist.
		kv.addStudiengang("Informatik B.Sc.", "INF");

		nv.erstelleStudent(idStudent01, "Thorben-Hendrik",
				"Müller-Meier Schulze",
				300208020,
				studiengangRepo.getOne(idStudiengang));

		// neustes Objekt aus Datenbank lesen
		leistungsblockA = leistungsblockRepo.getOne(idLeistungsblockA);
		leistungsblockB = leistungsblockRepo.getOne(idLeistungsblockB);
		leistungskomplex = leistungskomplexRepo.getOne(idLeistungskomplex01);

	}

	/**
	 * Es wird anhand der überreichten Leistungen jeweils eine StudentenLeistung über
	 * die Benutzerverwaltung zu hinzugefügt.
	 * 
	 * @param leistungen	List Leistungen
	 * @param student		Student
	 * @param ell   		List Float
	 */
	private void fillStudentLeistung(List<Leistung> leistungen, Student student, Float[] ell) {

		if (leistungen.size() != ell.length) {
			System.err.println("Testabbruch: Listen müssen gleich Mächtig sein.");
			System.exit(1);
		}

		int i = 0;
		for (Leistung l : leistungen) {
			bv.addErgebnis(ell[i], student, l);
			i++;
		}
	}

	/**
	 *Erstellt eine Float-Liste aus einem Float-Array
	 * 
	 * @param array		Float[]
	 * @return Liste vom Typ float
	 */
	private List<Float> erstelleLeistungslist(Float[] array) {

		List<Float> liste = new ArrayList<>();
		for (Float elem : array) {
			liste.add(elem);
		}
		return liste;
	}

	@Test
	@DisplayName("Leistungskomplex: Datenzugriff")
	void leistungskomplexFall10() {
		assertThat(leistungskomplex.getBeschreibung(), containsString("Beschreibung"));
		assertThat(leistungskomplex.getHuerde(), is(equalTo(50)));
	}

	@Test
	@DisplayName("Leistungskomplex: deleteLeistungskomplex()")
	void leistungskomplexFall20() {
		bv.deleteLeistungskomplex(leistungskomplex);
		assertThat(leistungskomplexRepo.existsById(idLeistungskomplex01), is(false));
	}

	@Test
	@DisplayName("Leistungskomplex: changeLeistungskomplex()")
	void leistungskomplexFall30() {
		bv.changeLeistungskomplex(leistungskomplex, 75, "Neue Beschreibung");

		assertThat(leistungskomplexRepo.existsById(idLeistungskomplex01), is(true));
		leistungskomplex = leistungskomplexRepo.getOne(idLeistungskomplex01);
		assertThat(leistungskomplex.getBeschreibung(), containsString("Neue Beschreibung"));
		assertThat(leistungskomplex.getHuerde(), is(equalTo(75)));
	}

	@Test
	@DisplayName("Leistungskomplex: istKorrekt()")
	void leistungskomplexFall40() {
		assertThat(leistungskomplex.getLeistungsblockList().size(), is(2));
		assertThat(leistungskomplex.istKorrekt(), is(true));
	}

	@Test
	@DisplayName("Leistungskomplex: istKorrekt()")
	void leistungskomplexFall41() {
		// Änderung der Gewichtung
		leistungsblockA.setGewichtung(61);
		assertThat(leistungskomplex.istKorrekt(), is(not(true)));
		leistungsblockA.setGewichtung(59);
		assertThat(leistungskomplex.istKorrekt(), is(not(true)));
		leistungsblockA.setGewichtung(-1);
		assertThat(leistungskomplex.istKorrekt(), is(not(true)));
	}

	@Test
	@DisplayName("Leistungsblock: changeLeistungsblock()")
	void leistungsblockFall10() {
		bv.changeLeistungsblock(leistungsblockA, 33, "neue Beschreibung");
		assertThat(leistungsblockRepo.existsById(idLeistungsblockA), is(true));
		leistungsblockA = leistungsblockRepo.getOne(idLeistungsblockA);
		assertThat(leistungsblockA.getBeschreibung(), containsString("neue"));
		assertThat(leistungsblockA.getGewichtung(), is(equalTo(33)));
	}

	@Test
	@DisplayName("Leistungsblock: deleteLeistungsblock()")
	void leistungsblockFall20() {
		bv.deleteLeistungsblock(leistungsblockB);
		assertThat(leistungsblockRepo.existsById(idLeistungsblockB), is(false));
		assertThat(leistungsblockRepo.existsById(idLeistungsblockA), is(not(false)));
	}

	@Test
	@DisplayName("Leistungsblock: removeLeistung()")
	void leistungsblockFall30() {
		assertThat(leistungsblockA.getLeistungsList().size(), is(2));
		bv.deleteEinzelleistung(leistungRepo.getOne(idLeistungA1));
		leistungsblockA = leistungsblockRepo.getOne(idLeistungsblockA);
		assertThat(leistungsblockA.getLeistungsList().size(), is(1));
		assertThat(leistungRepo.existsById(idLeistungA1), is(false));
		assertThat(leistungRepo.existsById(idLeistungA2), is(true));
	}

	@Test
	@DisplayName("Leistung: changeEinzelleistung()")
	void leistungFall10() {
		leistung = leistungRepo.getOne(idLeistungB1);
		bv.changeEinzelleistung(leistung, 50, "Leistung B1 neu");
		leistung = leistungRepo.getOne(idLeistungB1);
		assertThat(leistung.getBeschreibung(), containsString("neu"));
		assertThat(leistung.getMaxPunkte(), is(equalTo(50)));
	}

	@Test
	@DisplayName("Leistung: addErgebnis()")
	void leistungFall20() {

		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, einzelLeistungenDefault);

		// Prüfung ob 4 StudentLeistungen hinzugefügt wurden
		List<StudentLeistung> studentLeistungen = student.getStudentLeistungList();
		assertThat(studentLeistungen.size(), is(equalTo(4)));

		// Prüfung ob Einzelleistungen korrekt in Leistungsliste eingetragen wurden
		List<Float> tempEinzelLeistungen = erstelleLeistungslist(einzelLeistungenDefault);

		for (StudentLeistung sl : studentLeistungen) {
			assertThat(tempEinzelLeistungen, hasItem(sl.getPunktzahl()));
			tempEinzelLeistungen.remove(sl.getPunktzahl());
		}
	}

	@Test
	@DisplayName("Leistung: deleteErgebnis()")
	void LeistungFall30() {

		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, einzelLeistungenDefault);

		// Löschen der Leistung mit 17.5f Punkten
		List<StudentLeistung> studentLeistungen = student.getStudentLeistungList();
		StudentLeistung studentLeistung = studentLeistungen.get(0);
		Leistung leistung;

		if (Math.round(studentLeistung.getPunktzahl()) == Math.round(einzelLeistungenDefault[0])) {
		
			// Prüfen, ob alle Studentleistungen beim Studenten existieren
			studentLeistungen = student.getStudentLeistungList();
			assertThat(studentLeistungen.size(), is(equalTo(4)));

			// Prüfen, ob eine StudentLeistung in zugehöriger Leistung existiert
			leistung = studentLeistung.getLeistung();
			studentLeistungen = leistung.getStudentLeistungList();
			assertThat(studentLeistungen.size(), is(equalTo(1)));

			// löschen der studentLeistung
			bv.deleteErgebnis(studentLeistung);
			student = studentRepo.getOne(idStudent01);

			// Prüfen, ob die StudentLeistung aus Liste beim Studenten entfernt wurde
			studentLeistungen = student.getStudentLeistungList();
			assertThat(studentLeistungen.size(), is(equalTo(3)));

			// Prüfen, ob die StudentLeistung aus Liste bei zugehöriger Leistung entfernt
			// wurde
			leistung = studentLeistung.getLeistung();
			studentLeistungen = leistung.getStudentLeistungList();
			assertThat(studentLeistungen.size(), is(equalTo(0)));

		}
	}
	
	@Test
	@DisplayName("Leistung: berechneProzentVomKomplex()")
	void leistungFall40() {
		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, einzelLeistungenDefault);

		assertThat(bv.berechneProzentVomKomplex(leistungskomplex, student),is(54.5f));
	}
	
	@Test
	@DisplayName("Leistung: berechneProzentVomKomplex()")
	void leistungFall41() {
		Float[] ell = {7.5f,5f,5f,4.75f};
		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, ell);

		assertThat(bv.berechneProzentVomKomplex(leistungskomplex, student),is(22.25f));
	}
	
	@Test
	@DisplayName("Leistung: berechneProzentVomKomplex()")
	void leistungFall42() {
		Float[] ell = {0f,0f,0f,0f};
		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, ell);

		assertThat(bv.berechneProzentVomKomplex(leistungskomplex, student),is(0f));
	}
	
	@Test
	@DisplayName("Leistung: berechneProzentVomKomplex()")
	void leistungFall43() {
		Float[] ell = {20f,40f,20f,20f};
		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, ell);

		assertThat(bv.berechneProzentVomKomplex(leistungskomplex, student),is(100f));
	}
	
	@Test
	@DisplayName("Leistung: berechneProzentVomKomplex()")
	void leistungFall44() {
		Float[] ell = {21f,40f,20f,20f};
		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, ell);

		assertThat(bv.berechneProzentVomKomplex(leistungskomplex, student),is(100f));
	}
	
	@Test
	@DisplayName("Leistung: zugelassen()")
	void leistungFall45() {
		// Student wird bewertet
		student = studentRepo.getOne(idStudent01);
		List<Leistung> leistungen = leistungRepo.findAll();
		fillStudentLeistung(leistungen, student, einzelLeistungenDefault);
		
		// Student wird mit einem weiteren Leistungskomplex gequält
		Leistungskomplex leistungsKomplex2 = bv.createLeistungskomplex("lk2", 40, "lkFoo");
		Leistungsblock leistungsblockC = bv.createLeistungsblock(leistungsKomplex2, "lbC", 50, "lbBar");
		Leistungsblock leistungsblockD = bv.createLeistungsblock(leistungsKomplex2, "lbD", 50, "lbBar");
		
		Leistung leistungC1 = bv.addEinzelleistung("lC1", 12, "baz", leistungsblockC);
		Leistung leistungC2 = bv.addEinzelleistung("lC1", 18, "fubar", leistungsblockC);
		Leistung leistungD1 = bv.addEinzelleistung("lD1", 12, "raboo", leistungsblockD);
		Leistung leistungD2 = bv.addEinzelleistung("lD2", 18, "razz", leistungsblockD);
		Float[] ergebnisse = {12f,18f,3f,8f};
		
		leistungen.clear();
		leistungen.add(leistungC1); leistungen.add(leistungC2); 
		leistungen.add(leistungD1); leistungen.add(leistungD2); 
		fillStudentLeistung(leistungen, student, ergebnisse);
		
		assertThat(bv.zugelassen(student),is(true));
	}

	// TODO: Test zieht noch in die Testklasse NutzerverwaltungSpringTest um
	@Test
	@DisplayName("Passwort: Passwortqualität")
	public void passwortFall10() {
		
		String passwort = null;
		String passwortAlt = null;
		
		for( int i = 0; i <= 10000; i++) {
			passwort = nv.generierePasswort();

			int ziffern = 0;
			int kleinbuchstaben = 0;
			int großbuchstaben = 0;

			for (char c : passwort.toCharArray()) {
				if (c >= 48 && c <= 57) {
					ziffern++;
				}
				if (c >= 65 && c <= 90) {
					großbuchstaben++;
				}
				if (c >= 97 && c <= 122) {
					kleinbuchstaben++;
				}
			}
			assertThat(passwortAlt, is(not(equalTo(passwort))));
			assertThat(ziffern, is(equalTo(2)));
			assertThat(kleinbuchstaben, is(equalTo(8)));
			assertThat(großbuchstaben, is(equalTo(2)));
		}
	}
}
