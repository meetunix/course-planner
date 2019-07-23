package de.uni.swt.spring;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import de.uni.swt.spring.backend.Verwaltung.KonfigurationVerwaltung;
import de.uni.swt.spring.backend.data.entity.Konfiguration.EinschreibStrategie;
import de.uni.swt.spring.backend.repositories.*;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@TestInstance(Lifecycle.PER_CLASS) // erlaubt das @BeforeAll nicht statisch sein muss
class KonfigurationsverwaltungSpringTest {

	@Autowired
	private StudiengangRepository studiengangrepo;	
	@Autowired
	private KonfigurationVerwaltung kv;	
	
	@BeforeAll
	private void cleanDB() {
		studiengangrepo.deleteAll();
	}
		
	
	@Test
	@DisplayName("Lehrgang: getLehrgangname()")
	void LehrgangFall10() {
		String lehrgangname = kv.getLehrgangname();
		assertThat(lehrgangname, is(equalTo("swt-planer")));
	}
	
	
	@Test
	@DisplayName("Konfiguration: changeBeschreibung()")
	void KonfiguraionFall10() {
		String lehrgangname = kv.getLehrgangname();
		String beschreibung = "DasLiestDochEhhKeiner";
		kv.changeBeschreibung(lehrgangname, beschreibung);		
		assertThat(beschreibung, is(equalTo(kv.getKonfiguration().getBeschreibung())));
	}
			
	@Test
	@DisplayName("Konfiguration: changeMode()")
	void KonfiguraionFall20() {
		String lehrgangname = kv.getLehrgangname();
		
		//Setzte Einschreibstrategie auf Selbständig
		EinschreibStrategie tmp1 = EinschreibStrategie.Selbstständig;
		kv.changeMode(lehrgangname, tmp1 );	
		EinschreibStrategie tmp2 = kv.getKonfiguration().getModus();		
		assertThat(tmp2, is(equalTo(tmp1)));

		//Setzte Einschreibstrategie auf Automatisch
		tmp1 = EinschreibStrategie.Automatisch;
		kv.changeMode(lehrgangname, tmp1 );	
		tmp2 = kv.getKonfiguration().getModus();		
		assertThat(tmp2, is(equalTo(tmp1)));
		
		//Setzte Einschreibstrategie zurück auf Selbständig
		tmp1 = EinschreibStrategie.Selbstständig;
		kv.changeMode(lehrgangname, tmp1 );	
		tmp2 = kv.getKonfiguration().getModus();		
		assertThat(tmp2, is(equalTo(tmp1)));
	}
	
	@Test
	@DisplayName("Konfiguration: changeDeadline()")
	void KonfiguraionFall30() {
		String lehrgangname = kv.getLehrgangname();
		LocalDate tmp1 = LocalDate.of(2019, 6, 21);
		kv.changeDeadline(lehrgangname, tmp1);
		LocalDate tmp2 = kv.getKonfiguration().getDeadline();
		assertThat(tmp2, is(equalTo(tmp1)));
	}
	
	@Test
	@DisplayName("Konfiguration: changeGlobalesThema()")
	void KonfiguraionFall40() {
		String lehrgangname = kv.getLehrgangname();
		String tmp1 = "GruppeEinsFindetSWTDoof";
		kv.changeGlobalesThema(lehrgangname, tmp1);
		String tmp2 = kv.getKonfiguration().getGlobalesThema();
		assertThat(tmp2, is(equalTo(tmp1)));
	}
	
	@Test
	@DisplayName("Studiengang: addRemoveStudiengang()")
	void studiengangFall10() {
		String eidi = "Test";
		
		//Proofe
		assertThat(studiengangrepo.existsById(eidi), is(false));
		
		//Add Studiengang
		kv.addStudiengang(eidi, "tst");
		assertThat(studiengangrepo.existsById(eidi), is(true));
		
		//Remove Studiengang
		kv.removeStudiengang(eidi);
		assertThat(studiengangrepo.existsById(eidi), is(false));
	}	
		
	@Test
	@DisplayName("Studiengang: changeStudiengang()")
	void studiengangFall20() {
		String eidi = "Test";
		
		//Proofe
		assertThat(studiengangrepo.existsById(eidi), is(false));		
		//Add Studiengang
		kv.addStudiengang(eidi, "tst");
		
		String tmp = studiengangrepo.getOne(eidi).toString();
		assertThat(tmp, is(equalTo("tst")));
		
		//Change KurzName
		kv.changeStudiengang(eidi, eidi, "wurst");
		assertThat(studiengangrepo.existsById(eidi), is(true));
		tmp = studiengangrepo.getOne(eidi).toString();
		assertThat(tmp, is(equalTo("wurst")));
		
		//Change StudiengangName
		tmp = "gurke";
		kv.changeStudiengang(eidi, tmp, "wurst");
		assertThat(studiengangrepo.existsById(eidi), is(false));
		eidi = tmp;
		tmp = studiengangrepo.getOne(eidi).toString();
		assertThat(studiengangrepo.existsById(eidi), is(true));
		assertThat(tmp, is(equalTo("wurst")));
				
		//Change Both
		tmp = "weissbrot";
		kv.changeStudiengang(eidi, tmp, "bread");
		assertThat(studiengangrepo.existsById(eidi), is(false));
		eidi = tmp;
		tmp = studiengangrepo.getOne(eidi).toString();
		assertThat(studiengangrepo.existsById(eidi), is(true));
		assertThat(tmp, is(equalTo("bread")));
						
		//Remove Studiengang
		kv.removeStudiengang(eidi);
		assertThat(studiengangrepo.existsById(eidi), is(false));
	}		
}