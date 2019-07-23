package de.uni.swt.spring.backend.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;


import java.util.ArrayList;
import java.util.List;

@Entity
public class Projektgruppe extends AbstractEntity {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> studentList = new ArrayList<Student>();

	public List<Student> getStudentList() {
		return studentList;
	}
	public void addStudent(Student student) {
		studentList.add(student);
	}
	public void removeStudentFromList(Student student) {
		this.studentList.remove(student);
	}
	
	
	
	
    private String thema;
    private Integer anzahlMax;
    private Integer anzahlMin;
    private Integer anzahlAktuell;
    private Boolean offen;
	@ManyToOne
    private Uebungsgruppe uebungsgruppe;
	@OneToOne(cascade=CascadeType.ALL)
    private Zusammensetzung zusammensetzung = new Zusammensetzung(); 

    public Boolean getOffen() {
		return offen;
	}
	public void setOffen(Boolean offen) {
		this.offen = offen;
	}
	public String getThema() {
		return thema;
	}
	public void setThema(String thema) {
		this.thema = thema;
	}
	public Integer getAnzahlMax() {
		return anzahlMax;
	}
	public void setAnzahlMax(Integer anzahlMax) {
		this.anzahlMax = anzahlMax;
	}
	public Integer getAnzahlMin() {
		return anzahlMin;
	}
	public void setAnzahlMin(Integer anzahlMin) {
		this.anzahlMin = anzahlMin;
	}
	public Integer getAnzahlAktuell() {
		return anzahlAktuell;
	}
	public void setAnzahlAktuell(Integer anzahlAktuell) {
		this.anzahlAktuell = anzahlAktuell;
	}
	public Uebungsgruppe getUebungsgruppe() {
		return uebungsgruppe;
	}
	public void setUebungsgruppe(Uebungsgruppe uebungsgruppe) {
		this.uebungsgruppe = uebungsgruppe;
	}
	public Zusammensetzung getZusammensetzung() {
		return zusammensetzung;
	}
	public void setZusammensetzung(Zusammensetzung zusammensetzung) {
		this.zusammensetzung = zusammensetzung;
	}
	@Override
	public String toString() {
		return String.format("%s", this.getName());
	}
}
