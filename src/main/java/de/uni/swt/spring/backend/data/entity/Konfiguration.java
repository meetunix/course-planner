package de.uni.swt.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@Entity
public class Konfiguration extends AbstractEntity {

	public enum EinschreibStrategie {
		Selbstständig, Automatisch
	}

    @Id
    private String lehrgangname;
    private String beschreibung;
	private LocalDate deadline;
    private EinschreibStrategie modus;
    private String globalesThema;

    public Konfiguration() {
		lehrgangname = "swt-planer";
		beschreibung = "";
		deadline = null;
		modus = EinschreibStrategie.Selbstständig;
		globalesThema = "";
    }

    public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	public String getLehrgangname() {
		return lehrgangname;
	}
	public LocalDate getDeadline() {
		return deadline;
	}
	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
	public EinschreibStrategie getModus() {
		return this.modus;
	}
	public void setModus(EinschreibStrategie modus) {
		this.modus = modus;
	}
	public String getGlobalesThema() {
		return globalesThema;
	}
	public void setGlobalesThema(String globalesThema) {
		this.globalesThema = globalesThema;
	}
    
    
}
