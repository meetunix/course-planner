INSERT INTO STUDENT (email,version,passwort,passwort_changed,role,aktiviert,freigeschaltet,matrikel_nr,vorname,nachname) VALUES
    ('test@test.de',0,'$2a$10$rlDfb.y3u7ErBETu/S39su/sKcn7dWttdgCDzqPS8RPUP3TAVc0eC',TRUE,'student',TRUE,TRUE,217202561,'Thomas','Müller');

INSERT INTO DOZENT (email,version,passwort,passwort_changed,role,vorname,nachname) VALUES
    ('dorsch@swt.de',0,'$2a$10$rlDfb.y3u7ErBETu/S39su/sKcn7dWttdgCDzqPS8RPUP3TAVc0eC',TRUE,'dozent','irgendwo','nirgendwo');

-- Leistungskomplexe 
INSERT INTO LEISTUNGSKOMPLEX (name,version,beschreibung,huerde) values
    ('Testate', 0, 'der zulassungskiller', 40.0);
INSERT INTO LEISTUNGSKOMPLEX (name,version,beschreibung,huerde) values
    ('Hausaufgaben', 0, 'die studenten haben ja sonst gar nicht mehr zu tun', 50.0);
INSERT INTO LEISTUNGSKOMPLEX (name,version,beschreibung,huerde) values
    ('Projekt', 0, 'zwanzig prozent der pruefungsnot... ach ne warte', 50.0);

-- Leistungsblock

-- Vim Kommando für Leistungsblock
-- "oPjsw'f,wsw'f,a 0,wst,'f,0s$)>>j
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Testat1', 'Testate', 0, 'erstes Testat', 50.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Testat2', 'Testate', 0, 'zweites Testat', 50.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Hausaufgabe1', 'Hausaufgaben', 0, 'hier', 20.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Hausaufgabe2', 'Hausaufgaben', 0, 'koennte', 20.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Hausaufgabe3', 'Hausaufgaben', 0, 'ihre', 20.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Hausaufgabe4', 'Hausaufgaben', 0, 'werbung', 20.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Hausaufgabe5', 'Hausaufgaben', 0, 'stehen', 20.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Lastenheft', 'Projekt', 0, 'lastenheft', 35.0);
INSERT INTO LEISTUNGSBLOCK (name,leistungskomplex_name,version,beschreibung,gewichtung) values
    ('Pflichtenheft', 'Projekt', 0, 'pflichtenheft', 65.0);

-- Leistungsblock verlinkungen
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Testat1', 'Testate');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Testat2', 'Testate');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Hausaufgabe1', 'Hausaufgaben');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Hausaufgabe2', 'Hausaufgaben');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Hausaufgabe3', 'Hausaufgaben');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Hausaufgabe4', 'Hausaufgaben');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Hausaufgabe5', 'Hausaufgaben');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Lastenheft', 'Projekt');
INSERT INTO LEISTUNGSKOMPLEX_LEISTUNGSBLOCK_LIST (leistungsblock_list_name, leistungskomplex_name) values
    ('Pflichtenheft', 'Projekt');

-- Leistung

-- Vim Kommando für Leistung:
--0"lPj"iPa, 0"iyiwf,wsw'f,wsw'f,a 0,wc2t,''f,0s$)>>A ;dT)j
--" Jetzt nur noch 24@@ drücken ;) 

INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100001,'Aufgabe1', 'Testat1', 0, '', 20) ;
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100002, 'Aufgabe2', 'Testat1', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100003, 'Aufgabe3', 'Testat1', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100004, 'Aufgabe4', 'Testat1', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100005, 'Aufgabe1', 'Testat2', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100006, 'Aufgabe2', 'Testat2', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100007, 'Aufgabe3', 'Testat2', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100008, 'Aufgabe4', 'Testat2', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100009, 'Aufgabe1', 'Hausaufgabe1', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100010, 'Aufgabe2', 'Hausaufgabe1', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100011, 'Aufgabe1', 'Hausaufgabe2', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100012, 'Aufgabe2', 'Hausaufgabe2', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100013, 'Aufgabe1', 'Hausaufgabe3', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100014, 'Aufgabe2', 'Hausaufgabe3', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100015, 'Aufgabe1', 'Hausaufgabe4', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100016, 'Aufgabe2', 'Hausaufgabe4', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100017, 'Aufgabe1', 'Hausaufgabe5', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100018, 'Aufgabe2', 'Hausaufgabe5', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100019, 'Teilbereich1', 'Lastenheft', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100020, 'Teilbereich2', 'Lastenheft', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100021, 'Teilbereich3', 'Lastenheft', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100022, 'Teilbereich1', 'Pflichtenheft', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100023, 'Teilbereich2', 'Pflichtenheft', 0, '', 20);
INSERT INTO LEISTUNG (id,name,leistungsblock_name,version,beschreibung,max_punkte) values
    (100024, 'Teilbereich3', 'Pflichtenheft', 0, '', 20);

-- Leistungsblock_leistung_list

INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100001, 'Testat1');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100002, 'Testat1');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100003, 'Testat1');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100004, 'Testat1');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100005, 'Testat2');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100006, 'Testat2');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100007, 'Testat2');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100008, 'Testat2');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100009, 'Hausaufgabe1');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100010, 'Hausaufgabe1');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100011, 'Hausaufgabe2');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100012, 'Hausaufgabe2');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100013, 'Hausaufgabe3');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100014, 'Hausaufgabe3');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100015, 'Hausaufgabe4');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100016, 'Hausaufgabe4');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100017, 'Hausaufgabe5');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100018, 'Hausaufgabe5');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100019, 'Lastenheft');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100020, 'Lastenheft');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100021, 'Lastenheft');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100022, 'Pflichtenheft');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100023, 'Pflichtenheft');
INSERT INTO LEISTUNGSBLOCK_LEISTUNGS_LIST (leistungs_list_id,leistungsblock_name) values
    (100024, 'Pflichtenheft');

INSERT INTO STUDIENGANG (studiengang,version,kurzname) values
    ('Informatik', 0, 'INF');
INSERT INTO STUDIENGANG (studiengang,version,kurzname) values
    ('Wirtschaftsinformatik', 0, 'WIN');

--INSERT INTO GRUPPE (dtype,name,version,anzahl_aktuell,anzahl_max,anzahl_min,offen,thema,tag,termin,uebungsgruppe_name,dozent_email) values
    --('Uebungsgruppe', 'Gruppe1', 0, null, null, null, null, null, 0, '07:00:00', null, 'dorsch@swt.de');

--INSERT INTO GRUPPE (dtype,name,version,anzahl_aktuell,anzahl_max,anzahl_min,offen,thema,tag,termin,uebungsgruppe_name,dozent_email) values
    --('Projektgruppe', 'Team1', 0, 1, 7, null, TRUE, 'Verbesserung in der Weltgeschichte fur Noobs', null, null, 'Gruppe1', null);

--INSERT INTO GRUPPE_STUDENT_LIST (gruppe_name,student_list_email) values
    --('Team1', 'test@test.de');

--INSERT INTO GRUPPE_PROJEKTGRUPPE_LIST (uebungsgruppe_name, projektgruppe_list_name) values
    --('Gruppe1', 'Team1');

