package de.uni.swt.spring;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.Verwaltung.Gruppenverwaltung;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.data.entity.*;
import de.uni.swt.spring.backend.repositories.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@TestInstance(Lifecycle.PER_CLASS) // erlaubt das @BeforeAll nicht statisch sein muss
class NutzerverwaltungSpringTest {

	@Autowired
	private StudiengangRepository studiengangRepo;
	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private DozentRepository dozentRepo;
	@Autowired
	private UebungsgruppeRepository URepo;
	@Autowired
	private ProjektgruppeRepository PRepo;
	@Autowired
	private LeistungRepository leistungRepo;

	@Autowired
	private Nutzerverwaltung nv;
//	@Autowired
//	private Gruppenverwaltung gv;
	@Autowired
	private Bewertungsverwaltung bv;

	private Student ersterStudent;
	private Student zweiterStudent;
	private Student dritterStudent;
	private Student vierterStudent;

//	private Uebungsgruppe uGruppe01;
//	private Projektgruppe pGruppe01;
	private Studiengang studiengang01;

	private Dozent ersterDozent;
	private Dozent zweiterDozent;

	private Leistungskomplex leistungskomplex01;
	private Leistungsblock leistungsblock01;
	private Leistung ersteLeistung;

	List<Uebungsgruppe> UGruppenList = new ArrayList<Uebungsgruppe>();
	List<Projektgruppe> PGruppenList = new ArrayList<Projektgruppe>();

	private final String idStudent01 = "student01@uni-rostock.test";
	private final String idStudent02 = "student02@uni-rostock.test";
	private final String idStudent03 = "student03@uni-rostock.test";
	private final String idStudent04 = "student04@uni-rostock.test";

	private final String idDozent01 = "dozent01@uni-rostock.test";
	private final String idDozent02 = "dozent02@uni-rostock.test";

//	private final String idUGruppe01 = "Uebungsgruppe 1";
//	private final String idPGruppe01 = "Projektgruppe 1";

	LocalTime termin1 = LocalTime.NOON;
	Wochentag wochentag1 = Wochentag.Montag;

	@BeforeEach
	void setupNutzerverwaltung() {

		// Studiengang muss noch manuell gesetzt werden, bis die
		// konfigurationimplementierung
		// vollständig ist.
		Studiengang studiengangInfo = new Studiengang();
		studiengangInfo.setStudiengang("Informatik B.Sc.");
		studiengangRepo.save(studiengangInfo);

		ersterStudent = nv.erstelleStudent(idStudent01, "Jessie", "Matthams", 217203001, studiengangInfo);
		zweiterStudent = nv.erstelleStudent(idStudent02, "Florence", "Odom", 217203002, studiengangInfo);
		dritterStudent = nv.erstelleStudent(idStudent03, "Jozef", "Rivers", 217203003, studiengangInfo);
		vierterStudent = nv.erstelleStudent(idStudent04, "Aadam", "Ortega", 217203004, studiengangInfo);

		nv.erstelleDozent(idDozent01, "Maverick", "Turnbull");
		ersterDozent = dozentRepo.getOne(idDozent01);
		nv.erstelleDozent(idDozent02, "Amba", "Petty");
		zweiterDozent = dozentRepo.getOne(idDozent02);

//		Uebungsgruppe üGruppe = gv.createUebungsgruppe(idUGruppe01, wochentag1, termin1, ersterDozent);
//		uGruppe01 = URepo.getOne(üGruppe.getId());
//
//		Projektgruppe pGruppe = gv.createProjektgruppe(idPGruppe01, "Gullideckel", 10, 2, 4, true, uGruppe01);
//		pGruppe01 = PRepo.getOne(pGruppe.getId());

//		uGruppe01.addStudent(ersterStudent);
//		pGruppe01.addStudent(ersterStudent);

		leistungskomplex01 = bv.createLeistungskomplex("Leistungskomplex", 50, "Testleistungskomplex");
		bv.createLeistungsblock(leistungskomplex01, "erster Leistungsblock", 100, "Testleistungsblock");
		leistungsblock01 = bv.createLeistungsblock(leistungskomplex01, "Leistungsblock", 50, "Leistungsblock");
		Leistung leistung1 = bv.addEinzelleistung("erste Leistung", 20, "Testleistung", leistungsblock01);
		bv.addErgebnis(5, ersterStudent, leistung1);
	}

	@Test
	@DisplayName("Studenten Erstellung: Datenzugriff")
	void studentFall10() {
		ersterStudent = studentRepo.getOne(idStudent01);
		assertThat(ersterStudent.getEmail(), containsString("student01@uni-rostock.test"));
		assertThat(ersterStudent.getVorname(), containsString("Jessie"));
		assertThat(ersterStudent.getNachname(), containsString("Matthams"));
		assertThat(ersterStudent.getMatrikelNr(), is(217203001));
		assertThat(ersterStudent.isPasswortChanged(), is(false));
		assertThat(ersterStudent.getFreigeschaltet(), is(false));
		assertThat(ersterStudent.getAktiviert(), is(false));
		// assertThat(ersterStudent.getStudiengang(), containsString("Informatik
		// B.Sc."));
	}

	@Test
	@DisplayName("Student: firstLogin")
	void studentFall20() {
		ersterStudent = studentRepo.getOne(idStudent01);
		nv.firstLogin(ersterStudent);
		assertThat(ersterStudent.getAktiviert(), is(true));
	}

	@Test
	@DisplayName("Student: freigeschaltet")
	void studentFall30() {
		ersterStudent = studentRepo.getOne(idStudent01);
		nv.freischalten(ersterStudent);
		assertThat(ersterStudent.getFreigeschaltet(), is(true));
	}

	@Test
	@DisplayName("Student: changeStudent")
	void studentFall40() {
		ersterStudent = studentRepo.getOne(idStudent01);
		nv.changeStudent(ersterStudent, "Chris P.", "Bacon", 217000001, studiengang01, null, null); // TODO Übungsgruppe addieren
		assertThat(ersterStudent.getEmail(), containsString("student01@uni-rostock.test"));
		assertThat(ersterStudent.getVorname(), containsString("Chris P."));
		assertThat(ersterStudent.getNachname(), containsString("Bacon"));
		assertThat(ersterStudent.getMatrikelNr(), is(217000001));
		assertThat(ersterStudent.isPasswortChanged(), is(false));
		assertThat(ersterStudent.getFreigeschaltet(), is(false));
		assertThat(ersterStudent.getAktiviert(), is(false));
	}

	@Test
	@DisplayName("Student: deleteStudent")
	void studentFall50() {
		ersterStudent = studentRepo.getOne(idStudent01);
		nv.deleteStudent(ersterStudent);
//		assertThat(uGruppe01.getStudentList().contains(ersterStudent), is(false));
//		assertThat(pGruppe01.getStudentList().contains(ersterStudent), is(false));
		assertThat(ersterStudent.getStudentLeistungList().size(), is(0));
		assertThat(studentRepo.existsById(idStudent01), is(false));
	}

	@Test
	@DisplayName("Dozent Erstellung: Datenzugriff")
	void dozentFall10() {
		ersterDozent = dozentRepo.getOne(idDozent01);
		assertThat(ersterDozent.getEmail(), containsString("dozent01@uni-rostock.test"));
		assertThat(ersterDozent.getVorname(), containsString("Maverick"));
		assertThat(ersterDozent.getNachname(), containsString("Turnbull"));
		assertThat(ersterDozent.isPasswortChanged(), is(false));
	}

	@Test
	@DisplayName("Dozent: changeDozent")
	void dozentFall20() {
		ersterDozent = dozentRepo.getOne(idDozent01);
		nv.changeDozent(ersterDozent, "Chris P.", "Bacon", UGruppenList);
		assertThat(ersterDozent.getEmail(), containsString("dozent01@uni-rostock.test"));
		assertThat(ersterDozent.getVorname(), containsString("Chris P."));
		assertThat(ersterDozent.getNachname(), containsString("Bacon"));
		assertThat(ersterDozent.isPasswortChanged(), is(false));
	}

	@Test
	@DisplayName("Dozent: deleteDozent")
	void dozentFall30() {
		ersterDozent = dozentRepo.getOne(idDozent01);
		nv.deleteDozent(ersterDozent);
		assertThat(dozentRepo.existsById(idDozent01), is(false));
	}

	@Test
	@DisplayName("Passwort: passwortVergessen")
	void passwortFall10() {
		ersterStudent = studentRepo.getOne(idStudent01);
		String erstesPasswort = ersterStudent.getPasswort();
		nv.changePasswortStudent(ersterStudent);
		ersterStudent = studentRepo.getOne(idStudent01);
		assertThat(ersterStudent.getPasswort(), is(not(equalTo(erstesPasswort))));
	}

}
