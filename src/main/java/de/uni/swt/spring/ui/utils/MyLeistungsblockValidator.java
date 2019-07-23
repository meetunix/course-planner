package de.uni.swt.spring.ui.utils;

import com.vaadin.flow.data.binder.*;

import de.uni.swt.spring.backend.data.entity.*;

/**
 * Serverseitiger Validator zur Prüfung: Ob Leistungsblöcke innerhalb eines Leistungskomplexes
 * die Summer von 100 nicht überschreiten. Implementiert das vaadin-Validator Interface.
 */
public class MyLeistungsblockValidator implements Validator<Integer> {
	
	private Leistungskomplex lk = new Leistungskomplex();
	private Leistungsblock lb = new Leistungsblock();
	private boolean istNeuerBlock;
	
	public MyLeistungsblockValidator(Leistungskomplex lk, Leistungsblock lb, boolean istNeuerBlock) {
		this.lk = lk;
		if (lb != null) {
			this.lb = lb;
		}
		this.istNeuerBlock = istNeuerBlock;
	}
	
    @Override
    public ValidationResult apply(Integer value, ValueContext context) {
    	
    	if (istNeuerBlock) {
    	
			Integer summe = 0;
			
			for(Leistungsblock block : lk.getLeistungsblockList()) {
				summe += block.getGewichtung();
			}
				summe += value;

			if(summe <= 100) {
				return ValidationResult.ok();
			} else {
				return ValidationResult.error(
					"Summe > 100");
			}
		} else {

			Integer summe = 0;
			Integer lbGewicht = lb.getGewichtung();
			
			for(Leistungsblock block : lk.getLeistungsblockList()) {
				summe += block.getGewichtung();
			}
			
				summe -= lbGewicht;
				summe += value;

			if(summe <= 100) {
				return ValidationResult.ok();
			} else {
				return ValidationResult.error(
					"Summe > 100");
			}
			
		}
    }
}