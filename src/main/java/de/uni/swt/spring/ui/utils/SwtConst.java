package de.uni.swt.spring.ui.utils;

/**
 * 
 * Hilfklasse für Konstanten. Hier finden sich die Bezeichnungen von Views, aber auch die
 * regulären Ausdrücke für die Clientseitige validierung.
 *
 */
public class SwtConst {
    public static final String PAGE_ROOT="";
    public static final String PAGE_TEAMS="teams";
    public static final String PAGE_GRUPPEN="gruppen";
    public static final String PAGE_TEAMS_GRUPPEN="teams-gruppen";
    public static final String PAGE_KONFIGURATION="config";
    public static final String PAGE_NUTZER="user";
    public static final String PAGE_PUNKTE_STUDENT ="points";
    public static final String PAGE_PUNKTE_DOZENT ="pointsD";
    public static final String PAGE_ADMIN="admin";
    public static final String PAGE_LOGIN="login";
    public static final String PAGE_PASSWORT_RESET="passreset";
    public static final String PAGE_PASSWORT_VERGABE="passvergabe";

    public static final String TITLE_TEAMS="Teams";
    public static final String TITLE_GRUPPEN="Gruppen";
    public static final String TITLE_TEAMS_GRUPPEN="Teams/Gruppen";
    public static final String TITLE_KONFIGURATION="Konfiguration";
    public static final String TITLE_NUTZER="Studenten";
    public static final String TITLE_PUNKTE="Punkte";
    public static final String TITLE_LOGOUT="Logout";
    public static final String TITLE_ADMIN="Admin";
    public static final String TITLE_NOT_FOUND="Seite nicht gefunden";
    public static final String TITLE_ACCESS_DENIED="Achtung toxisch";
    public static final String TITLE_PASSWORT_RESET="Passwort reset";
    public static final String TITLE_PASSWORT_VERGABE="Passwortvergabe";

    public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";
    
	/*
	 * Jeder Unicode-code-point aus der Klasse Letter, gefolgt von beleibig vielen diakritischen
	 * Zeichen. Dppelnamen erlaubt, mit jeweils 2-16 Unicode Graphemen , getrennt durch Leerzeichen
	 * oder '-'.
	 */
	public static final String REGEX_NAME = "(\\p{L}\\p{M}*){2,16}[ \\-]?((\\p{L}\\p{M}*){2,16})?"; 
	public static final String REGEX_NAME_ERROR = "Kein gültiger Name";
	
	/**
	 * E-Mailadressen nur nach dem Schema der Universität Rostock erlaubt. 
	 */
	public static final String REGEX_EMAIL =
			"[0-9]?[a-zA-Z\\-]{2,16}.[a-zA-Z\\-]{2,16}[0-9]?@uni-rostock.de";
	public static final String REGEX_EMAIL_ERROR =
			"Ungültige E-Mail-Adresse! Gültige Adresse:"
			+ " vorname.nachname@uni-rostock.de";
    
	/*
	 * Matrikelnummern bestehen aus 9 Ziffern, wobei die ersten beiden nicht 0 sein dürfen 
	 */
	public static final String REGEX_MATRIKEL = "[1-9]{2}[0-9]{7}";
	public static final String REGEX_MATRIKEL_ERROR = 
			"ungültige Matrikelnummer";

	/*
	 * Als Studiengang sind nur zwei Worte erlaubt Informatik B.Sc. 
	 */
	public static final String REGEX_STUDIENGANG =".{3,32}"; 
	public static final String REGEX_STUDIENGANG_ERROR =""
			+ "Ein Studiengang besteht aus drei bis 32 Zeichen."; 

	public static final String REGEX_STUDIENGANG_ABK =".{2,7}"; 
	public static final String REGEX_STUDIENGANG_ABK_ERROR =""
			+ "Abkürzung: mind. 2, höchstens 5 Buchstaben"; 
}
