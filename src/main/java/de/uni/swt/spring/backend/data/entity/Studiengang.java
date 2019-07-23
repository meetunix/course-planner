package de.uni.swt.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Studiengang extends AbstractEntity {
    @Id
    private String studiengang;
    private String kurzname;
	public String getStudiengang() {
		return studiengang;
	}
	public void setStudiengang(String studiengang) {
		this.studiengang = studiengang;
	}
	public String getKurzname() {
		return kurzname;
	}
	public void setKurzname(String kurzname) {
		this.kurzname = kurzname;
	}
    
    @Override
    public String toString() {
    	return String.format("%s", kurzname);
    }
}
