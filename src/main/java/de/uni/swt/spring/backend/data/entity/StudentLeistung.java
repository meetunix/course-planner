package de.uni.swt.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;



@Entity 
public class StudentLeistung extends AbstractEntity {

	// Variablen

    private Float punktzahl = 0.0f;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Leistung leistung;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

   
    // Konstruktor

	public StudentLeistung(Float punktzahl, Student student, Leistung leistung) {
		this.setPunktzahl(punktzahl);
		this.setStudent(student);
		this.setLeistung(leistung);
//		sEmailLName = student.getEmail() + "_" + leistung.getName();
	}

    public StudentLeistung() {}

	// Getter und Setter


    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public Float getPunktzahl() {
		return punktzahl;
	}
	public void setPunktzahl(Float punktzahl) {
		this.punktzahl = punktzahl;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Leistung getLeistung() {
		return leistung;
	}
	public void setLeistung(Leistung leistung) {
		this.leistung = leistung;
	}

    
}
