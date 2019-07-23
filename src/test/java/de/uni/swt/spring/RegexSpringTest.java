package de.uni.swt.spring;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import de.uni.swt.spring.ui.utils.SwtConst;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class RegexSpringTest {

	@Test
	void RegexFall10() {
		assertThat("Friedrich-Wilhelm".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall11() {
		assertThat("Friedrich Wilhelm".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall12() {
		assertThat("Friedrich".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall13() {
		assertThat("Li".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall14() {
		assertThat("Li La".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall15() {
		assertThat("Li-La".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall16() {
		assertThat("von Hohenzollern".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall17() {
		//Adel hat seine Grenzen
		assertThat("Müller von Wrycz-Rekowski".matches(SwtConst.REGEX_NAME), is(false));
	}
	@Test
	void RegexFall18() {
		assertThat("Müller".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall19() {
		// 1/4 ist nicht in der Klasse "letter"
		assertThat("Åland-¼".matches(SwtConst.REGEX_NAME), is(false));
	}
	@Test
	void RegexFall20() {
		assertThat("Åland-ǢǢǢǢǢǢǢǢǢǢǢǢǢǢǢǢǢ".matches(SwtConst.REGEX_NAME), is(false));
	}
	@Test
	void RegexFall21() {
		// 16 Unicode-Code-points
		assertThat("Åland-ǢǢǢǢǢǢǢǢǢǢǢǢǢǢǢǢ".matches(SwtConst.REGEX_NAME), is(true));
	}
	@Test
	void RegexFall30() {
		assertThat("friedrich.hohenzollern@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall31() {
		assertThat("2friedrich.hohenzollern@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall32() {
		assertThat("friedrich.hohenzollern2@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall33() {
		assertThat("2friedrich.hohenzollern2@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall34() {
		assertThat("2Friedrich.Hohenzollern2@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall35() {
		assertThat("2Friedrich.Hohenzöllern2@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(false));
	}
	@Test
	void RegexFall36() {
		assertThat("Friedrich.Hohenzollern@Uni-Rostock.de".matches(SwtConst.REGEX_EMAIL), is(false));
	}
	@Test
	void RegexFall37() {
		assertThat("Friedrich.vonHohenzollern@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall38() {
		//Eventuell Admins oder Funktionaladressen
		assertThat("friedrich@uni-rostock.de".matches(SwtConst.REGEX_EMAIL), is(true));
	}
	@Test
	void RegexFall50() {
		assertThat("123456789".matches(SwtConst.REGEX_MATRIKEL), is(true));
	}
	@Test
	void RegexFall51() {
		assertThat("023456789".matches(SwtConst.REGEX_MATRIKEL), is(false));
	}
	@Test
	void RegexFall52() {
		assertThat("003456789".matches(SwtConst.REGEX_MATRIKEL), is(false));
	}
	@Test
	void RegexFall53() {
		assertThat("803456789".matches(SwtConst.REGEX_MATRIKEL), is(false));
	}
	@Test
	void RegexFall54() {
		assertThat("210345678".matches(SwtConst.REGEX_MATRIKEL), is(true));
	}
	@Test
	void RegexFall55() {
		assertThat("210000000".matches(SwtConst.REGEX_MATRIKEL), is(true));
	}
	@Test
	void RegexFall56() {
		assertThat("1".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("12".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("123".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("1234".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("12345".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("1234567".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("12345678".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("1234567890".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("12345678901".matches(SwtConst.REGEX_MATRIKEL), is(false));
		assertThat("123456789012".matches(SwtConst.REGEX_MATRIKEL), is(false));
	}

	@Test
	void RegexFall60() {
		assertThat("Informatik B.Sc.".matches(SwtConst.REGEX_STUDIENGANG), is(true));
	}
	@Test
	void RegexFall61() {
		assertThat("Altertumswissenschaften (Master)".matches(SwtConst.REGEX_STUDIENGANG), is(true));
	}
	@Test
	void RegexFall62() {
		assertThat("Öffentliche Verwaltung".matches(SwtConst.REGEX_STUDIENGANG), is(true));
	}

}
