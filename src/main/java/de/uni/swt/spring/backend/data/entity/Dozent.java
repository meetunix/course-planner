package de.uni.swt.spring.backend.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;


import java.util.ArrayList;
import java.util.List;

import static de.uni.swt.spring.backend.data.Rolle.DOZENT;

@Entity
public class Dozent extends Nutzer {
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Uebungsgruppe> uebungsgruppeList;

    public Dozent() {
    	uebungsgruppeList = new ArrayList<Uebungsgruppe>();
    }

	
	public List<Uebungsgruppe> getUebungsgruppeList() {
		return uebungsgruppeList;
	}

	public void setUebungsgruppeList(List<Uebungsgruppe> uebungsgruppeList) {
		this.uebungsgruppeList = uebungsgruppeList;
	}
    
    
	@Override
	public String toString() {
		return String.format("%s %s", this.getVorname(), this.getNachname());
	}
}
