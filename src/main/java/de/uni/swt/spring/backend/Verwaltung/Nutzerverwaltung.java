package de.uni.swt.spring.backend.Verwaltung;

import de.uni.swt.spring.backend.bohnen.TestBohne;
import de.uni.swt.spring.backend.data.entity.*;
import de.uni.swt.spring.backend.repositories.*;

import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * Verwaltungs/Controller - Klasse für die Nutzerverwaltung.
 *
 */

@Controller
public class Nutzerverwaltung {

    @Autowired
    private StudentRepository studrepo;
    @Autowired
    private DozentRepository dozrepo;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  ProjektgruppeRepository proRepo;

    @Autowired
    TestBohne bohne;

    @Autowired private Bewertungsverwaltung bvw;
    @Autowired private Gruppenverwaltung gvw;

    public Nutzerverwaltung() {
    }

    /**
     * Methode erstellt einen neuen Studenten und verschickt eine E-Mail mit dem Passwort.
     * 
     * @param email				Die UID des Nutzers, darf nicht doppelt vorkommen.
     * @param vorname
     * @param nachname
     * @param matrikelNr
     * @param studiengang		Ein Studiengang-Objekt
     */
    public Student erstelleStudent(String email,
    		String vorname,
    		String nachname,
    		Integer matrikelNr,
    		Studiengang studiengang) {
    	
        Student stud = new Student();
        String passwort = generierePasswort();
        stud.setEmail(email);
        stud.setPasswort(passwordEncoder.encode(passwort));
        stud.setPasswortChanged(false);
        stud.setVorname(vorname);
        stud.setNachname(nachname);
        stud.setMatrikelNr(matrikelNr);
        stud.setFreigeschaltet(false); //TODO: Check auf Einschreibestrategie
        stud.setAktiviert(false);
        stud.setStudiengang(studiengang);
        stud.setRole("Student");
        studrepo.save(stud);
        bohne.sendMail(email, passwort);
        passwort = null; //hoffentlich fängt der garbage collector so früher an ...
        return stud;
    }
    
    /**
     * Entfernt einen Studenten und alle Mitgliedschaften in Gruppen, sowie seine
     * erbrachten Leistungen.
     * 
     * @param student		Student, der entfernt werden soll
     */
    public void deleteStudent(Student student) {

        //entfernt Referenzen auf zu löschenden Studenten in Uebungsgruppe
        Uebungsgruppe UeGruppe = student.getUebungsgruppe();
        if (UeGruppe != null) {
        	gvw.removeStudentFromUebungsgruppe(UeGruppe, student);
        }

        //entfernt Referenzen auf zu löschenden Studenten in Projektgruppe
        Projektgruppe ProGruppe = student.getProjektgruppe();
        if (ProGruppe != null) {
        	gvw.removeStudentFromProjektgruppe(ProGruppe, student);
        }

        //entfernt Referenzen auf StudentLeistungen des zu löschenden Studenten
        for (Iterator<StudentLeistung> it = student.getStudentLeistungList().listIterator(); it.hasNext(); ) {
        	StudentLeistung s = it.next();
			it.remove();
			bvw.deleteErgebnis(s);
        }
        //entfernt zu löschenden Studenten aus StudentenRepo
        studrepo.delete(student);
    }

    /**
     * Methode ändert die Attribute eines Studenten
     * 
     * @param stud			Student, der bearbeitet werden soll
     * @param vorname		neuer Vorname
     * @param nachname		neuer Nachname
     * @param matrikelNr	neue Matrikelnummer
     * @param studiengang	neuer Studiengang
     * @param uebungsgruppe	neue Uebungsgruppe
     * @param projektgruppe neue Projektgruppe
     */
    public void changeStudent(
    		Student stud,
    		String vorname,
    		String nachname,
    		Integer matrikelNr,
    		Studiengang studiengang,
    		Uebungsgruppe uebungsgruppe,
    		Projektgruppe projektgruppe) {

    	stud.setVorname(vorname);
        stud.setNachname(nachname);
        stud.setMatrikelNr(matrikelNr);
        stud.setStudiengang(studiengang);
        studrepo.save(stud);
    	
        //Student einer Übungsgruppe zuordnen
        if (uebungsgruppe != null ) {
        stud = studrepo.getOne(stud.getEmail());
        	//Student ist noch nicht Mitglied einer Übungsgruppe
        	if (stud.getUebungsgruppe() == null) {
				gvw.addStudentToUebungsgruppe(uebungsgruppe, stud);
			//Student ist bereits Mitglied einer Übungsgruppe	
        	}else {
        		//Student befindet sich nicht in der richtigen Übungsruppe
        		if(stud.getUebungsgruppe().getId() != uebungsgruppe.getId()) {
					gvw.removeStudentFromUebungsgruppe(stud.getUebungsgruppe(), stud);
					stud = studrepo.getOne(stud.getEmail());
					gvw.addStudentToUebungsgruppe(uebungsgruppe, stud);
        		}
        	}
        }
        
        //Student einer Projektgruppe zuordnen
        if (projektgruppe != null ) {
        stud = studrepo.getOne(stud.getEmail());
        	//Student ist noch nicht Mitglied einer Projektgruppe
        	if (stud.getProjektgruppe() == null) {
				gvw.addStudentToProjektgruppe(projektgruppe, stud);
			//Student ist bereits Mitglied einer Projektgruppe	
        	}else {
        		//Student befindet sich nicht in der richtigen Projektgruppe
        		if(stud.getProjektgruppe().getId() != projektgruppe.getId()) {
					gvw.removeStudentFromProjektgruppe(stud.getProjektgruppe(), stud);
					stud = studrepo.getOne(stud.getEmail());
					projektgruppe = proRepo.getOne(projektgruppe.getId());
					gvw.addStudentToProjektgruppe(projektgruppe, stud);
        		}
        	}
        }
    }

    /**
     * Methode erstellt einen neuen Dozenten.
     * 
     * @param email			UID des Nutzers, darf nicht mehrfach existieren.
     * @param vorname
     * @param nachname
     */
    public void erstelleDozent(String email, String vorname, String nachname) {
        Dozent doz = new Dozent();
        String passwort = generierePasswort();
        doz.setEmail(email);
        doz.setPasswort(passwordEncoder.encode(passwort));
        doz.setPasswortChanged(false);
        doz.setVorname(vorname);
        doz.setNachname(nachname);
        doz.setRole("Dozent");
        dozrepo.save(doz);
        bohne.sendMail(email, passwort);
        passwort = null; //hoffentlich fängt der garbage collector so früher an ...
    }

    /**
     * Methode löscht einen Dozenten.
     * 
     * @param doz		Dozent
     */
    public void deleteDozent(Dozent doz) {
    	for(Uebungsgruppe ue : doz.getUebungsgruppeList()) {
    		ue.setDozent(null);
    	}
        dozrepo.delete(doz);
        //Referenzen zu Übungs- und Projektgruppen bleiben bestehen
    }
    
    /**
     * Ändert die Parameter eines Dozenten.
     * 
     * @param doz
     * @param vorname
     * @param nachname
     * @param uebungsgruppeList
     */
    public void changeDozent(Dozent doz,
    		String vorname,
    		String nachname,
    		List<Uebungsgruppe> uebungsgruppeList) {
    	
        doz.setVorname(vorname);
        doz.setNachname(nachname);
        doz.setUebungsgruppeList(uebungsgruppeList);
        dozrepo.save(doz);
    }

    /**
     * Methode ändert das Passwort eines Studenten und verschickt eine E-Mail mit einem neuen
     * Passwort.
     * 
     * @param student		Der Student, dessen Passwort geändert werden soll. 
     */
    public void changePasswortStudent(Student student) {
    	String neuesPasswort = generierePasswort();
        student.setPasswort(passwordEncoder.encode(neuesPasswort));
        student.setPasswortChanged(false);
        studrepo.save(student);
        bohne.sendMail(student.getEmail(),neuesPasswort);
        neuesPasswort = null; //hoffentlich fängt der garbage collector so früher an ...
    }

    //für Testdaten
    public void changePasswortStudent(Student student, String passwd) {
        student.setPasswort(passwordEncoder.encode(passwd));
        student.setPasswortChanged(true);
        studrepo.save(student);
    }

    /**
     * Methode ändert das Passwort eines Dozenten und verschickt eine E-Mail mit einem neuen
     * Passwort.
     * 
     * @param dozent		Der Dozent, dessen Passwort geändert werden soll. 
     */
    public void changePasswortDozent(Dozent dozent) {
    	String neuesPasswort = generierePasswort();
        dozent.setPasswort(passwordEncoder.encode(neuesPasswort));
        dozent.setPasswortChanged(false);
        dozrepo.save(dozent);
        bohne.sendMail(dozent.getEmail(),neuesPasswort);
        neuesPasswort = null; //hoffentlich fängt der garbage collector so früher an ...
    }

    public void changePasswortDozent(Dozent dozent, String passwd) {
        dozent.setPasswort(passwordEncoder.encode(passwd));
        dozent.setPasswortChanged(true);
        dozrepo.save(dozent);
    }

    public void changePasswortAdmin(Admin admin) {
        String neuesPasswort = generierePasswort();
        admin.setPasswort(passwordEncoder.encode(neuesPasswort));
        admin.setPasswortChanged(false);
        adminRepository.save(admin);
        bohne.sendMail(admin.getEmail(),neuesPasswort);
        neuesPasswort = null; //hoffentlich fängt der garbage collector so früher an ...
    }

    public void changePasswortAdmin(Admin admin, String passwd) {
        admin.setPasswort(passwordEncoder.encode(passwd));
        admin.setPasswortChanged(true);
        adminRepository.save(admin);
    }

    public void firstLogin(Student stud) {
        stud.setAktiviert(true);
        studrepo.save(stud);
    }

    public void freischalten(Student stud) {
        stud.setFreigeschaltet(true);
        studrepo.save(stud);
    }

    public Nutzer getNutzer(String email) {
        Student student = studrepo.findByEmail(email);
        if (student == null) {
            Dozent dozent = dozrepo.findByEmail(email);
            if (dozent == null) {
                Admin admin = adminRepository.findByEmail(email);
                if (admin == null) {
                    return null;
                }
                return admin;
            }
            return dozent;
        }
        return student;
    }

    /**
     * Generiert ein zwölfstelliges Passwort, bestehend aus 8 Kleinbuchstaben [a-z],
     * 2 Großbuchstaben [A-Z] und zwei Ziffern [0-9] in zufälliger Permutation.
     * 
     * @return pass		Das Passwort als String
     */
    public String generierePasswort() {

        String pass;
    	PasswordGenerator passwortGenerator = new PasswordGenerator();

    	CharacterData kleinbuchstaben = EnglishCharacterData.LowerCase;
    	CharacterRule kRegel = new CharacterRule(kleinbuchstaben);
    	kRegel.setNumberOfCharacters(8);
    	
    	CharacterData großbuchstaben = EnglishCharacterData.UpperCase;
    	CharacterRule gRegel = new CharacterRule(großbuchstaben);
    	gRegel.setNumberOfCharacters(2);
        
    	CharacterData ziffern = EnglishCharacterData.Digit;
        CharacterRule zRegel = new CharacterRule(ziffern);
        zRegel.setNumberOfCharacters(2);
        
        pass = passwortGenerator.generatePassword(12, kRegel, gRegel, zRegel);
        
        return pass;
    }
}
