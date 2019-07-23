package de.uni.swt.spring.backend.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Uebungsgruppe extends AbstractEntity {
	 @Id @GeneratedValue(strategy=GenerationType.AUTO)
	    private Integer id;
	    
	    
	    public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		private String name;
		
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
		
	
	
	private Wochentag tag;
    private LocalTime termin;
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Projektgruppe> projektgruppeList = new ArrayList<Projektgruppe>();
    @ManyToOne
    private Dozent dozent;

	public LocalTime getTermin() {
		return termin;
	}
	public void setTermin(LocalTime termin) {
		this.termin = termin;
	}
	public Dozent getDozent() {
		return dozent;
	}
	public void setDozent(Dozent dozent) {
		this.dozent = dozent;
	}
	public Wochentag getTag() {
		return tag;
	}
	public void setTag(Wochentag tag) {
		this.tag = tag;
	}
	public List<Projektgruppe> getProjektgruppeList() {
		return projektgruppeList;
	}
	public void setProjektgruppeList(List<Projektgruppe> projektgruppeList) {
		this.projektgruppeList = projektgruppeList;
	}
    
	@Override
	public String toString() {
		return String.format("%s", this.getName());
	}
}
