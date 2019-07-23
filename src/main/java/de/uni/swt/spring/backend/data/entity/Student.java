package de.uni.swt.spring.backend.data.entity;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static de.uni.swt.spring.backend.data.Rolle.STUDENT;

@Entity
public class Student extends Nutzer {
    private Integer matrikelNr;
    @Lob
    private Uebungsgruppe wunschtermin;
    private Boolean freigeschaltet;
    private Boolean aktiviert;
	@ManyToOne
    private Uebungsgruppe uebungsgruppe;
    @ManyToOne
    private Projektgruppe projektgruppe;
    @ManyToOne
    private Studiengang studiengang;
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private List<StudentLeistung>studentLeistungList = new ArrayList<>();


    public Integer getMatrikelNr() {
		return matrikelNr;
	}
	public void setMatrikelNr(Integer matrikelNr) {
		this.matrikelNr = matrikelNr;
	}
	public Uebungsgruppe getWunschtermin() {
		return wunschtermin;
	}
	public void setWunschtermin(Uebungsgruppe wunschtermin) {
		this.wunschtermin = wunschtermin;
	}
	public Boolean getFreigeschaltet() {
		return freigeschaltet;
	}
	public void setFreigeschaltet(Boolean freigeschaltet) {
		this.freigeschaltet = freigeschaltet;
	}
	public Boolean getAktiviert() {
		return aktiviert;
	}
	public void setAktiviert(Boolean aktiviert) {
		this.aktiviert = aktiviert;
	}
	public Uebungsgruppe getUebungsgruppe() {
		return uebungsgruppe;
	}
	public void setUebungsgruppe(Uebungsgruppe uebungsgruppe) {
		this.uebungsgruppe = uebungsgruppe;
	}
	public Projektgruppe getProjektgruppe() {
		return projektgruppe;
	}
	public void setProjektgruppe(Projektgruppe projektgruppe) {
		this.projektgruppe = projektgruppe;
	}
	public Studiengang getStudiengang() {
		return studiengang;
	}
	public void setStudiengang(Studiengang studiengang) {
		this.studiengang = studiengang;
	}
	public List<StudentLeistung> getStudentLeistungList(){
		return studentLeistungList;
	}
	public void setStudentLeistungList(List <StudentLeistung> studentLeistungList) {
		this.studentLeistungList = studentLeistungList;
	}
	public void addStudentLeistung(StudentLeistung studentLeistung) {
		this.studentLeistungList.add(studentLeistung);
	}
	public void removeStudentleistung(StudentLeistung studentLeistung) {
		this.studentLeistungList.remove(studentLeistung);
	}
}
