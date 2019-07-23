package de.uni.swt.spring;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import de.uni.swt.spring.backend.Verwaltung.Gruppenverwaltung;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Projektgruppe;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;
import de.uni.swt.spring.backend.data.entity.Wochentag;
import de.uni.swt.spring.backend.repositories.*;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@TestInstance(Lifecycle.PER_CLASS) // erlaubt das @BeforeAll nicht statisch sein muss
class GruppenverwaltungSpringTest {
	
	@Autowired
	private StudiengangRepository SRepo;
	@Autowired
	private Gruppenverwaltung gv;
	@Autowired
	private Nutzerverwaltung nv;
	@Autowired
	private StudentRepository studentRepo;
	@Autowired
	private DozentRepository dozentRepo;
	@Autowired
	private UebungsgruppeRepository URepo;
	@Autowired
	private ProjektgruppeRepository PRepo;
	
	LocalTime termin1 = LocalTime.NOON;
	LocalTime termin2 = LocalTime.MIDNIGHT;
	Wochentag tag1 = Wochentag.Montag;
	Wochentag tag2 = Wochentag.Dienstag;
	
	private Student ersterStudent;
	
	private Dozent ersterDozent;
	private Dozent zweiterDozent;
	
	private Uebungsgruppe ersteUebungsgruppe;
	private Uebungsgruppe zweiteUebungsgruppe;
	private Projektgruppe ersteProjektgruppe;
	private Projektgruppe zweiteProjektgruppe;
	
	private final String idStudent01 = "student01@uni-rostock.test";
	
	private final String idDozent01 = "dozent01@uni-rostock.test";
	private final String idDozent02 = "dozent02@uni-rostock.test";
	
	
	
	
	@BeforeEach
	void setupGruppenverwaltung() {
		
		Studiengang studiengangInfo = new Studiengang();
		studiengangInfo.setStudiengang("Informatik B.Sc.");
		SRepo.save(studiengangInfo);
		
		nv.erstelleStudent(idStudent01, "erster", "Student", 217203001, studiengangInfo);
		ersterStudent = studentRepo.getOne(idStudent01);
		
		nv.erstelleDozent(idDozent01, "Julius", "Caesar");
		ersterDozent = dozentRepo.getOne(idDozent01);
		nv.erstelleDozent(idDozent02, "Nero", "Claudius");
		zweiterDozent = dozentRepo.getOne(idDozent02);
		
		Uebungsgruppe uGruppe01 = gv.createUebungsgruppe("Uebungsgruppe01", tag1, termin1, ersterDozent);
		ersteUebungsgruppe = URepo.getOne(uGruppe01.getId());
		
		Uebungsgruppe uGruppe02 = gv.createUebungsgruppe("Uebungsgruppe02", tag1, termin2, ersterDozent);
		zweiteUebungsgruppe = URepo.getOne(uGruppe02.getId());
		
		Projektgruppe pGruppe01 = gv.createProjektgruppe("Projektgruppe01", "Gullideckel", 10, 0, 0, false, ersteUebungsgruppe);
		ersteProjektgruppe = PRepo.getOne(pGruppe01.getId());
		
		Projektgruppe pGruppe02 = gv.createProjektgruppe("Projektgruppe02", "Gullideckel", 10, 0, 0, true, ersteUebungsgruppe);
		zweiteProjektgruppe = PRepo.getOne(pGruppe02.getId());
	}
	
	
	
	
	
	/** Uebungsgruppen Tests**/
	@Test
	@DisplayName("Uebungsgruppe: createUebungsgruppe")
	void uebungsgruppenFall10() {
		assertThat(ersteUebungsgruppe.getName(), 	containsString("Uebungsgruppe01"));
		assertThat(ersteUebungsgruppe.getTag(), 	is(equalTo((Wochentag.Montag))));
		assertThat(ersteUebungsgruppe.getTermin(), 	is(equalTo((LocalTime.NOON))));
		assertThat(ersteUebungsgruppe.getDozent(), 	is(equalTo((ersterDozent))));
	}
	
	@Test
	@DisplayName("Uebungsgruppe: changeUebungsgruppeGruppeData")
	void uebungsgruppenFall20() {
		gv.changeUebungsgruppeGruppeData(ersteUebungsgruppe, tag2, termin2, zweiterDozent);
		assertThat(ersteUebungsgruppe.getName(), 	containsString("Uebungsgruppe01"));
		assertThat(ersteUebungsgruppe.getTag(), 	is(equalTo(Wochentag.Dienstag)));
		assertThat(ersteUebungsgruppe.getTermin(), 	is(equalTo(LocalTime.MIDNIGHT)));
		assertThat(ersteUebungsgruppe.getDozent(), 	is(equalTo(zweiterDozent)));
	}
	
	@Test
	@DisplayName("Uebungsgruppe: addStudentToUebungsgruppe")
	void uebungsgruppenFall30() {
		gv.addStudentToUebungsgruppe(ersteUebungsgruppe, ersterStudent);
		assertThat(ersteUebungsgruppe.getStudentList().contains(ersterStudent), is(true));
		assertThat(ersteUebungsgruppe.getStudentList(), hasItem(ersterStudent));
		assertThat(ersterStudent.getUebungsgruppe(), is(equalTo(ersteUebungsgruppe)));

	}
	
	@Test
	@DisplayName("Uebungsgruppe: removeStudentFromUebungsgruppe")
	void uebungsgruppenFall40() {
		gv.addStudentToUebungsgruppe(ersteUebungsgruppe, ersterStudent);
		gv.removeStudentFromUebungsgruppe(ersteUebungsgruppe, ersterStudent);
		assertThat(ersteUebungsgruppe.getStudentList().contains(ersterStudent), is(false));
		assertThat(ersteUebungsgruppe.getStudentList(), not(hasItem(ersterStudent)));
		
	}
	
	@Test
	@DisplayName("Uebungsgruppe: deleteUebungsgruppe")
	void uebungsgruppenFall50() {
		gv.addStudentToUebungsgruppe(ersteUebungsgruppe, ersterStudent);
		gv.deleteUebungsgruppe(ersteUebungsgruppe);
		assertThat(ersterDozent.getUebungsgruppeList().contains(ersteUebungsgruppe), is(false));
		assertThat(ersterStudent.getUebungsgruppe(),	is(nullValue()));
		assertThat(PRepo.count(), is(0L));
		assertThat(URepo.count(), is(0L));
	}
	
	
	
	/** Projektgruppen Tests**/
	@Test
	@DisplayName("Projektgruppe: createProjektgruppe")
	void projektgruppenFall10() {
		assertThat(ersteProjektgruppe.getName(), 	containsString("Projektgruppe01"));
		assertThat(ersteProjektgruppe.getThema(), 	containsString("Gullideckel"));
		assertThat(ersteProjektgruppe.getAnzahlMax(), 		is(10));
		assertThat(ersteProjektgruppe.getAnzahlMin(), 		is(0));
		assertThat(ersteProjektgruppe.getAnzahlAktuell(), 	is(0));
		assertThat(ersteProjektgruppe.getOffen(), 			is(false));
		assertThat(ersteProjektgruppe.getUebungsgruppe(), 	is(equalTo(ersteUebungsgruppe)));

	}
	
	@Test
	@DisplayName("Projektgruppe: changeProjektgruppeData")
	void projektgruppenFall20() {
		gv.changeProjektgruppeData(ersteProjektgruppe, "Erdbeermarmelade", 11, 0, 0, true, zweiteUebungsgruppe);
		assertThat(ersteProjektgruppe.getName(), 	containsString("Projektgruppe01"));
		assertThat(ersteProjektgruppe.getThema(), 	containsString("Erdbeermarmelade"));
		assertThat(ersteProjektgruppe.getAnzahlMax(), 		is(11));
		assertThat(ersteProjektgruppe.getAnzahlMin(), 		is(0));
		assertThat(ersteProjektgruppe.getAnzahlAktuell(), 	is(0));
		assertThat(ersteProjektgruppe.getOffen(), 			is(true));
		assertThat(ersteProjektgruppe.getUebungsgruppe(), 	is(equalTo(zweiteUebungsgruppe)));
	}
	
	@Test
	@DisplayName("Projektgruppe: addStudentToProjektgruppe")
	void projektgruppenFall30() {
		gv.addStudentToProjektgruppe(ersteProjektgruppe, ersterStudent);
		assertThat(ersteProjektgruppe.getStudentList().contains(ersterStudent), is(true));
		assertThat(ersteProjektgruppe.getStudentList(), hasItem(ersterStudent));
		assertThat(ersterStudent.getProjektgruppe(), is(equalTo(ersteProjektgruppe)));
	}
	
	@Test
	@DisplayName("Projektgruppe: removeStudentFromProjektgruppe")
	void projektgruppenFall40() {
		gv.addStudentToProjektgruppe(ersteProjektgruppe, ersterStudent);
		assertThat(ersteProjektgruppe.getStudentList().contains(ersterStudent), is(true));
		gv.removeStudentFromProjektgruppe(ersteProjektgruppe, ersterStudent);
		assertThat(ersteProjektgruppe.getStudentList().contains(ersterStudent), is(false));
	}
	
	@Test
	@DisplayName("Projektgruppe: deleteProjektgruppe")
	void projektgruppenFall50() {
		assertThat(PRepo.count(), is(2L));
		gv.deleteProjektgruppe(ersteProjektgruppe);
		//assertThat(PRepo.findByName("Projektgruppe01"), is(not("Projektgruppe01")));
		assertThat(ersterStudent.getProjektgruppe(), is(nullValue()));
		assertThat(PRepo.count(), is(1L));
	}
	
	
	
	/** Gruppenstatus Tests**/
	@Test
	@DisplayName("Gruppen: changeOpenStatus")
	void GruppenFall10() {
		gv.changeOpenStatus(true);
		assertThat(ersteProjektgruppe.getOffen(),	is(true));
		assertThat(zweiteProjektgruppe.getOffen(),	is(true));
		gv.changeOpenStatus(false);
		assertThat(ersteProjektgruppe.getOffen(),	is(false));
		assertThat(zweiteProjektgruppe.getOffen(),	is(false));
	}
}