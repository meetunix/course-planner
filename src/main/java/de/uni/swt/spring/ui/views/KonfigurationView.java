package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.Verwaltung.KonfigurationVerwaltung;
import de.uni.swt.spring.backend.data.entity.Konfiguration.EinschreibStrategie;
import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;
import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.repositories.LeistungRepository;
import de.uni.swt.spring.backend.repositories.LeistungsblockRepository;
import de.uni.swt.spring.backend.repositories.LeistungskomplexRepository;
import de.uni.swt.spring.backend.repositories.StudiengangRepository;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.MyDoubleToIntegerConverter;
import de.uni.swt.spring.ui.utils.MyLeistungsblockValidator;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_KONFIGURATION, layout = MainView.class)
@Secured("Dozent")
public class KonfigurationView extends VerticalLayoutSecured {

    private static final long serialVersionUID = 1957783323126817894L;
    private final String WIDTH_NAME = "300px";
    private final String WIDTH_BESCHREIBUNG = "400px";
    private final String WIDTH_NUMBER = "100px";
    VerticalLayout lkAccordions;
    VerticalLayout leistungskomplexLayout = new VerticalLayout();
    Label keineLks;
    @Autowired
    Bewertungsverwaltung bv;
    @Autowired
    KonfigurationVerwaltung kv;
    @Autowired
    private LeistungRepository leistungRepo;
    @Autowired
    private LeistungsblockRepository leistungsblockRepo;
    @Autowired
    private LeistungskomplexRepository leistungskomplexRepo;
    @Autowired
    private StudiengangRepository studiengangRepo;
    private Binder<Leistungskomplex> binderLk = new Binder<>(Leistungskomplex.class);
    private Binder<Leistungsblock> binderLb = new Binder<>(Leistungsblock.class);
    private Binder<Leistung> binderLeistung = new Binder<>(Leistung.class);

    public KonfigurationView() {
        lkAccordions = new VerticalLayout();
        keineLks = new Label("Derzeit keine Leistungskomplexe vorhanden.");
        leistungskomplexLayout.setAlignItems(Alignment.CENTER);
    }

    @PostConstruct // PostConstruct wird nach dem initialisieren von den Autowired variablen aufgerufen
    public void init() {
        // === Comboboxen ===
        HorizontalLayout comboLayout = new HorizontalLayout();
        comboLayout.setWidth("40%");
        ComboBox<EinschreibStrategie> modusSelektion = new ComboBox<>("Enschreibestrategie");
        modusSelektion.setItems(EinschreibStrategie.values());
        modusSelektion.setWidth("50%");
        modusSelektion.setValue(kv.getKonfiguration().getModus());
        modusSelektion.addValueChangeListener(e -> {
            kv.changeMode(kv.getLehrgangname(), modusSelektion.getValue());
        });
        comboLayout.add(modusSelektion);
        DatePicker deadline = new DatePicker("Deadline zum Schließen");
        deadline.setWidth("50%");
        Locale.setDefault(Locale.GERMANY);
        deadline.setLocale(Locale.GERMANY);
//    	deadline.setI18n(DatePickerI18n );
        if (kv.getKonfiguration().getDeadline() == null) {
            deadline.setValue(LocalDate.now());
        } else {
            deadline.setValue(kv.getKonfiguration().getDeadline());
        }
        deadline.addValueChangeListener(e -> {
            kv.changeDeadline(kv.getLehrgangname(), deadline.getValue());
        });
        comboLayout.add(deadline);

        add(comboLayout);

        // === Buttons ===
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("40%");
        Button studiengängeButton = new Button("Studiengänge konfigurieren");
        Button neuerLeistungskomplexButton = new Button("Neuen Leistungskomplex erstellen");

        studiengängeButton.setWidth("50%");
        studiengängeButton.addClickListener(e -> {
            studiengangKonfigurationDialog().open();
        });
        neuerLeistungskomplexButton.setWidth("50%");
        neuerLeistungskomplexButton.addClickListener(event -> {
            neuenLeistungskomplex().open();
        });
        buttonLayout.add(studiengängeButton);
        buttonLayout.add(neuerLeistungskomplexButton);
        buttonLayout.setAlignItems(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);

        add(buttonLayout);

        // === Akkordion ===

        updateLkAccordion();
        leistungskomplexLayout.add(lkAccordions);
        leistungskomplexLayout.setAlignItems(Alignment.CENTER);
        leistungskomplexLayout.setWidth("65%");

        // === Curr VLayout ===

        setAlignItems(Alignment.CENTER);
        add(leistungskomplexLayout);


    }

    public Dialog studiengangKonfigurationDialog() {
        // === Dialog default ===

        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        Grid<Studiengang> studiengängeGrid = new Grid<>(Studiengang.class);
        studiengängeGrid.setHeightByRows(true);
        dialog.setCloseOnOutsideClick(true);

        // === Dialogtitel ===

//		HorizontalLayout titel = new HorizontalLayout();
//		titel.setPadding(false);
//		titel.setSpacing(false);
//		titel.setMargin(false);
//        titel.getElement().getStyle().set("padding", "none");
//        titel.getElement().getStyle().set("margin", "none");
//        titel.getElement().getStyle().set("spacing", "none");
//        titel.getStyle().set("background-color", "var(--lumo-primary-color)");
//        titel.add(new Text("Studiengänge konf"));
//        titel.addMenuItem(new AppLayoutMenuItem(new Text("Studiengänge konfigurieren")));
//        dialog.getElement().getStyle().set("background-color", "var(--lumo-primary-color)");
//        dialog.add(titel);

        // === Neuen Studiengang hinzufügen ===

        HorizontalLayout neuerSGLayout = new HorizontalLayout();
        TextField sgNameInput = new TextField("Studiengangname", "Informatik");
        TextField sgAbkInput = new TextField("Abkürzung", "INF");
        Button sgHinzufügen = new Button("Hinzufügen");
        sgHinzufügen.addClickShortcut(Key.ENTER);
        sgHinzufügen.setEnabled(false);

        Binder<Studiengang> binderSg = new Binder<>(Studiengang.class);

        sgNameInput.setValueChangeMode(ValueChangeMode.EAGER);
        sgNameInput.setWidth("300px");
        sgNameInput.addValueChangeListener(e -> {
            if (!sgNameInput.isEmpty() && !sgAbkInput.isEmpty()) {
                sgHinzufügen.setEnabled(true);
            } else {
                sgHinzufügen.setEnabled(false);
            }
        });
        sgAbkInput.setValueChangeMode(ValueChangeMode.EAGER);
        sgAbkInput.setWidth("100px");
        sgAbkInput.addValueChangeListener(e -> {
            if (!sgNameInput.isEmpty() && !sgAbkInput.isEmpty()) {
                sgHinzufügen.setEnabled(true);
            } else {
                sgHinzufügen.setEnabled(false);
            }
        });

        sgHinzufügen.addClickListener(e -> {
            kv.addStudiengang(sgNameInput.getValue(), sgAbkInput.getValue());
            studiengängeGrid.setItems(studiengangRepo.findAll());
        });


        binderSg.forField(sgNameInput)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_STUDIENGANG),
                        SwtConst.REGEX_STUDIENGANG_ERROR)
                .bind(Studiengang::getStudiengang, Studiengang::setStudiengang);

        binderSg.forField(sgAbkInput)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_STUDIENGANG_ABK),
                        SwtConst.REGEX_STUDIENGANG_ABK_ERROR)
                .bind(Studiengang::getKurzname, Studiengang::setKurzname);

        neuerSGLayout.setAlignItems(Alignment.BASELINE);
        neuerSGLayout.add(sgNameInput);
        neuerSGLayout.add(sgAbkInput);
        neuerSGLayout.add(sgHinzufügen);

        dialogLayout.add(neuerSGLayout);

        // === Studiengänge grid

        studiengängeGrid.setItems(studiengangRepo.findAll());
        dialogLayout.add(studiengängeGrid);
        studiengängeGrid.setColumns("studiengang", "kurzname");
        studiengängeGrid.addComponentColumn(sg -> entfernenButton(studiengängeGrid, sg))
                .setHeader("")
                .setId("entfernen");
        studiengängeGrid.getColumnByKey("studiengang").setWidth("45%");
        studiengängeGrid.getColumnByKey("kurzname").setWidth("20%");

        // === Zürück button ===

        Button zurückButton = new Button("Zurück");
        zurückButton.addClickListener(e -> {
            dialog.close();
        });
        dialogLayout.add(zurückButton);

        // === Ending stuff ===

        dialog.add(dialogLayout);
        return dialog;
    }


    private Button entfernenButton(Grid<Studiengang> grid, Studiengang sg) {
        Button button = new Button("Entfernen");
        button.addClickListener(e -> {
            studiengangRepo.delete(sg);
            grid.setItems(studiengangRepo.findAll());
        });
        return button;
    }

    private void updateLkAccordion() {

        if (leistungskomplexRepo.findAll().isEmpty()) {
            keineLks = new Label("Derzeit keine Leistungskomplexe vorhanden.");
            leistungskomplexLayout.add(keineLks);
        } else {
            leistungskomplexLayout.remove(keineLks);
        }
        VerticalLayout newLkAccordions = new VerticalLayout();
        for (Leistungskomplex lk : leistungskomplexRepo.findAll()) {
            Accordion lkAccordion = new Accordion();
            lkAccordion.setWidthFull();
            lkAccordion.setMinWidth("400px");
            lkAccordion.close();
            VerticalLayout lkLayout = new VerticalLayout();
            HorizontalLayout lkKonfigLayout = new HorizontalLayout();

            NumberField hürdeTextField = new NumberField("Hürde");
            hürdeTextField.setWidth(WIDTH_NUMBER);
            hürdeTextField.setValue((double) lk.getHuerde());
            hürdeTextField.setValueChangeMode(ValueChangeMode.EAGER);
			hürdeTextField.setSuffixComponent(new Span("%"));

            TextField beschreibungTextField = new TextField("Beschreibung");
            beschreibungTextField.setWidth(WIDTH_BESCHREIBUNG);
            beschreibungTextField.setValue(lk.getBeschreibung());
            beschreibungTextField.setValueChangeMode(ValueChangeMode.EAGER);
            lkKonfigLayout.add(hürdeTextField);
            lkKonfigLayout.add(beschreibungTextField);

            Button lkSpeichernButton = new Button("Speichern");
            lkSpeichernButton.addClickListener(e -> {
                if (hürdeTextField.isInvalid()) {
                    Notification.show("Geben Sie erst eine gültige Hürde ein");
                    hürdeTextField.setInvalid(true);
                } else if (beschreibungTextField.isInvalid()) {
                    Notification.show("Geben Sie erst eine gültige Beschreibung ein");
                    beschreibungTextField.setInvalid(true);

                } else {
                    bv.changeLeistungskomplex(
                            leistungskomplexRepo.getOne(lk.getName()),
                            (int) Math.round(hürdeTextField.getValue()),
                            beschreibungTextField.getValue()
                    );
                    Notification.show("Leistungskomplex wurde angepasst");
                }
            });

            lkKonfigLayout.add(lkSpeichernButton);
            Button lkLöschenButton = new Button("Löschen");
            lkLöschenButton.addClickListener(e -> {
                bv.deleteLeistungskomplex(leistungskomplexRepo.getOne(lk.getName()));
                Notification.show("Leistungskomplex wurde entfernt");
                updateLkAccordion();
            });
            lkKonfigLayout.add(lkLöschenButton);

            Button neuerLbButton = new Button("Neuen Leistungsblock erstellen");
            neuerLbButton.addClickListener(e -> {
                //neuenLeistungsblock(lk).open();
                neuenLeistungsblock(leistungskomplexRepo.getOne(lk.getName())).open();
                updateLkAccordion();
            });

            //Eingabevalidierung
            ArrayList<NumberField> nfsollenNichtLeerSein = new ArrayList<NumberField>();
            nfsollenNichtLeerSein.add(hürdeTextField);


            binderLk.forField(hürdeTextField)
                    .asRequired("Pflicht")
                    .withConverter(new MyDoubleToIntegerConverter())
                    .withValidator(v -> v >= 1, "Minimal: 1")
                    .withValidator(v -> v <= 100, "Maximal: 100")
                    .bind(Leistungskomplex::getHuerde, Leistungskomplex::setHuerde);

            binderLk.forField(beschreibungTextField)
                    .withValidator(v -> v.length() < 48, "Beschreibunf ist zu lang")
                    .bind(Leistungskomplex::getBeschreibung, Leistungskomplex::setBeschreibung);

//			hürdeTextField.addValueChangeListener(e -> {
//				lkSpeichernButton.setEnabled(checkEnableButton(null, nfsollenNichtLeerSein));
//			});
//			beschreibungTextField.addValueChangeListener(e -> {
//				lkSpeichernButton.setEnabled(checkEnableButton(null, nfsollenNichtLeerSein));
//			});

            lkKonfigLayout.setAlignItems(Alignment.BASELINE);

            lkKonfigLayout.add(neuerLbButton);
            lkLayout.add(lkKonfigLayout);


            for (Leistungsblock lb : lk.getLeistungsblockList()) {
                Accordion lbAccordion = new Accordion();
                lbAccordion.close();

                VerticalLayout lbLayout = new VerticalLayout();
                HorizontalLayout lbKonfigLayout = new HorizontalLayout();
                NumberField lbGewichtung = new NumberField("Gewichtung");
                lbGewichtung.setWidth(WIDTH_NUMBER);
                lbGewichtung.setValue((double) lb.getGewichtung());
                lbGewichtung.setValueChangeMode(ValueChangeMode.EAGER);
                lbGewichtung.setSuffixComponent(new Span("%"));
                lbKonfigLayout.add(lbGewichtung);


                TextField lbBeschreibung = new TextField("Beschreibung");
                lbBeschreibung.setWidth(WIDTH_BESCHREIBUNG);
                lbBeschreibung.setValue(lb.getBeschreibung());
                lbBeschreibung.setValueChangeMode(ValueChangeMode.EAGER);
                lbKonfigLayout.add(lbBeschreibung);

                Button lbSpeichernButton = new Button("Speichern");
                lbSpeichernButton.addClickListener(e -> {
                    if (lbGewichtung.isInvalid()) {
                        Notification.show("Bitte geben sie erst eine gültige Gewichtung ein!");
                        lbGewichtung.setInvalid(true);
                    } else if (lbBeschreibung.isInvalid()) {
                        Notification.show("Bitte geben sie erst eine gültige Beschreibung ein!");
                        lbBeschreibung.setInvalid(true);
                    } else {
                        bv.changeLeistungsblock(
                                leistungsblockRepo.getOne(lb.getName()),
                                (int) Math.round(lbGewichtung.getValue()),
                                lbBeschreibung.getValue()
                        );
                        Notification.show("Leistungsblock wurde angepasst");
                    }
                });
                lbKonfigLayout.add(lbSpeichernButton);

                Button lbLöschenButton = new Button("Löschen");
                lbLöschenButton.addClickListener(e -> {
                    //TODO - löschen funktioniert nicht, nach dem LB geändert wurde.
                    bv.deleteLeistungsblock(lb);
                    //bv.deleteLeistungsblock(leistungsblockRepo.getOne(lb.getName()));
                    Notification.show("Leistungsblock wurde entfernt");
                    updateLkAccordion();
                });
                lbKonfigLayout.add(lbLöschenButton);

                Button neuerLButton = new Button("Neue Leistung erstellen");
                neuerLButton.addClickListener(e -> {
                    neueLeistung(lb).open();
                    updateLkAccordion();
                });

                //Eingabevalidierung
                nfsollenNichtLeerSein.clear();
                nfsollenNichtLeerSein.add(lbGewichtung);

                binderLb.forField(lbGewichtung)
                        .asRequired("Pflicht")
                        .withConverter(new MyDoubleToIntegerConverter())
                        .withValidator(v -> v >= 1, "Minimal: 1")
                        .withValidator(v -> v <= 100, "Maximal: 100")
                        .withValidator(new MyLeistungsblockValidator(lk, lb, false))
                        .bind(Leistungsblock::getGewichtung, Leistungsblock::setGewichtung);

                binderLb.forField(lbBeschreibung)
                        .withValidator(v -> v.length() < 48, "Beschreibung ist zu lang")
                        .bind(Leistungsblock::getBeschreibung, Leistungsblock::setBeschreibung);

//				hürdeTextField.addValueChangeListener(e -> {
//					lbSpeichernButton.setEnabled(checkEnableButton(null, nfsollenNichtLeerSein));
//				});
//				beschreibungTextField.addValueChangeListener(e -> {
//					lbSpeichernButton.setEnabled(checkEnableButton(null, nfsollenNichtLeerSein));
//				});

                lbKonfigLayout.setAlignItems(Alignment.BASELINE);

                lbKonfigLayout.add(neuerLButton);
                lbLayout.add(lbKonfigLayout);

                for (Leistung l : lb.getLeistungsList()) {
                    Accordion lAccordion = new Accordion();
                    lAccordion.close();

                    VerticalLayout lLayout = new VerticalLayout();
                    HorizontalLayout lKonfigLayout = new HorizontalLayout();

                    NumberField lMaxPunkte = new NumberField("Max. Punkte");
                    lMaxPunkte.setValue((double) l.getMaxPunkte());
                    lMaxPunkte.setWidth(WIDTH_NUMBER);
                    lMaxPunkte.setValueChangeMode(ValueChangeMode.EAGER);

                    TextField lBeschreibung = new TextField("Beschreibung");
                    lBeschreibung.setValue(lb.getBeschreibung());
                    lBeschreibung.setWidth(WIDTH_BESCHREIBUNG);
                    lBeschreibung.setValueChangeMode(ValueChangeMode.EAGER);

                    lKonfigLayout.add(lMaxPunkte);
                    lKonfigLayout.add(lBeschreibung);

                    Button lSpeichernButton = new Button("Speichern");
                    lSpeichernButton.addClickListener(e -> {
                        if (lMaxPunkte.isInvalid()) {
                            Notification.show("Bitte geben Sie erst eine gültige Maximalpunktzahl ein!");
                            lMaxPunkte.setInvalid(true);
                        } else if (lBeschreibung.isInvalid()) {
                            Notification.show("Bitte geben Sie erst eine gültige Beschreibung ein!");
                            lBeschreibung.setInvalid(true);
                        } else {
                            bv.changeEinzelleistung(leistungRepo.getOne(l.getId()),
                                    (int) Math.round(lMaxPunkte.getValue()),
                                    lBeschreibung.getValue());
                            Notification.show("Leistung wurde angepasst.");
                        }
                    });
                    lKonfigLayout.add(lSpeichernButton);

                    Button lLöschenButton = new Button("Löschen");
                    lLöschenButton.addClickListener(e -> {
                        //TODO AUch die Leistung lässt sich nach bearbeitung nicht entfernen
                        //bv.deleteEinzelleistung(leistungRepo.getOne(l.getId()));
                        bv.deleteEinzelleistung(l);
                        Notification.show("Leistung wurde entfernt.");
                        updateLkAccordion();
                    });
                    lKonfigLayout.add(lLöschenButton);

                    //Eingabevalidierung
                    nfsollenNichtLeerSein.clear();
                    nfsollenNichtLeerSein.add(lMaxPunkte);

                    binderLeistung.forField(lMaxPunkte)
                            .asRequired("Pflicht")
                            .withConverter(new MyDoubleToIntegerConverter())
                            .withValidator(v -> v >= 1, "Minimal: 1")
                            .withValidator(v -> v <= 1000, "Maximal: 1000")
                            .bind(Leistung::getMaxPunkte, Leistung::setMaxPunkte);

                    binderLeistung.forField(lBeschreibung)
                            .withValidator(v -> v.length() < 48, "Beschreibung ist zu lang")
                            .bind(Leistung::getBeschreibung, Leistung::setBeschreibung);


                    lKonfigLayout.setAlignItems(Alignment.BASELINE);

                    lLayout.add(lKonfigLayout);
                    lAccordion.add("Leistung: " + l.getName(), lLayout)
                            .addThemeVariants(DetailsVariant.FILLED);
                    lbLayout.add(lAccordion);
                }
                lbAccordion.add("Leistungsblock: " + lb.getName(), lbLayout)
                        .addThemeVariants(DetailsVariant.FILLED);

                lkLayout.add(lbAccordion);
            }
            lkAccordion.add("Leistungskomplex: " + lk.getName(), lkLayout)
                    .addThemeVariants(DetailsVariant.FILLED);
            newLkAccordions.add(lkAccordion);
        }
        leistungskomplexLayout.replace(lkAccordions, newLkAccordions);
        lkAccordions.setAlignItems(Alignment.CENTER);
        lkAccordions.setHorizontalComponentAlignment(Alignment.CENTER);
        lkAccordions = newLkAccordions;
        leistungskomplexLayout.setAlignItems(Alignment.CENTER);
    }

    private Dialog neuenLeistungskomplex() {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();

        TextField name = new TextField("Leistungskomplexname");
        name.focus();
        NumberField hürde = new NumberField();
        hürde.setSuffixComponent(new Span("%"));
        hürde.setLabel("Hürde");

        TextField beschreibung = new TextField("Beschreibung");


        Button zurück = new Button("Zurück");
        zurück.addClickListener(e -> {
            dialog.close();
        });

        Button erstellen = new Button("Erstellen");
        erstellen.addClickShortcut(Key.ENTER);
        erstellen.setEnabled(false);

        ArrayList<TextField> tfsollenNichtLeerSein = new ArrayList<TextField>();
        tfsollenNichtLeerSein.add(name);
        ArrayList<NumberField> nfsollenNichtLeerSein = new ArrayList<NumberField>();
        nfsollenNichtLeerSein.add(hürde);

        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.setWidth(WIDTH_NAME);

        hürde.setValueChangeMode(ValueChangeMode.EAGER);
        hürde.setWidth(WIDTH_NUMBER);

        beschreibung.setValueChangeMode(ValueChangeMode.EAGER);
        beschreibung.setWidth(WIDTH_BESCHREIBUNG);

        erstellen.addClickListener(e -> {
            if (istLeistungskomplexFrei(name.getValue()) && !hürde.isInvalid()) {
                bv.createLeistungskomplex(
                        name.getValue(), (int) Math.round(hürde.getValue()), beschreibung.getValue()
                );
                dialog.close();
                updateLkAccordion();
                Notification.show("Der Leistungskomplex wurde erstellt");
            } else if (hürde.isInvalid()) {
                hürde.setErrorMessage("ungültig");
                Notification.show("Geben Sie bitte eine gültige Bestehenshürde an.");
            } else {
                name.setInvalid(true);
                name.setErrorMessage("Leistungskomplexname schon vergeben");
                Notification.show("Es existiert bereits ein Leistungskomplex mit diesem Namen");
            }
        });

        binderLk.forField(hürde)
                .asRequired("Pflicht")
                .withConverter(new MyDoubleToIntegerConverter())
                .withValidator(v -> v >= 1, "Minimal: 1")
                .withValidator(v -> v <= 100, "Maximal: 100")
                .bind(Leistungskomplex::getHuerde, Leistungskomplex::setHuerde);

        binderLk.forField(name)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.length() > 3, "Name ist zu kurz")
                .withValidator(v -> v.length() < 24, "Name ist zu lang")
                .bind(Leistungskomplex::getName, Leistungskomplex::setName);

        binderLk.forField(beschreibung)
                .withValidator(v -> v.length() < 48, "Beschreibunf ist zu lang")
                .bind(Leistungskomplex::getBeschreibung, Leistungskomplex::setBeschreibung);

        name.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        beschreibung.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        hürde.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        HorizontalLayout buttons = new HorizontalLayout(zurück, erstellen);

        dialogLayout.add(name, beschreibung, hürde, buttons);

        dialog.add(dialogLayout);
        return dialog;
    }

    private Dialog neuenLeistungsblock(Leistungskomplex lk) {

        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();

        TextField name = new TextField("Leistungsblockname");
        name.focus();
        NumberField gewichtung = new NumberField();
        gewichtung.setLabel("Gewicht (%)");
        TextField beschreibung = new TextField("Beschreibung");

        Button zurück = new Button("Zurück");
        zurück.addClickListener(e -> {
            dialog.close();
        });


        Button erstellen = new Button("Erstellen");
        erstellen.addClickShortcut(Key.ENTER);
        erstellen.setEnabled(false);

        ArrayList<TextField> tfsollenNichtLeerSein = new ArrayList<TextField>();
        tfsollenNichtLeerSein.add(name);
        ArrayList<NumberField> nfsollenNichtLeerSein = new ArrayList<NumberField>();
        nfsollenNichtLeerSein.add(gewichtung);

        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.setWidth(WIDTH_NAME);

        gewichtung.setValueChangeMode(ValueChangeMode.EAGER);
        gewichtung.setWidth(WIDTH_NUMBER);

        beschreibung.setValueChangeMode(ValueChangeMode.EAGER);
        beschreibung.setWidth(WIDTH_BESCHREIBUNG);

        erstellen.addClickListener(e -> {
            if (istLeistungsblockFrei(name.getValue()) && !gewichtung.isInvalid()) {
                bv.createLeistungsblock(
                        lk, name.getValue(), (int) Math.round(gewichtung.getValue()), beschreibung.getValue()
                );
                dialog.close();
                updateLkAccordion();
                Notification.show("Der Leistungsblock wurde erstellt");
            } else if (gewichtung.isInvalid()) {
                gewichtung.setInvalid(true);
                Notification.show("Die Gewichtung der Leistungsblöcke innerhalb eines "
                        + "Leistungskomplexes darf die Summe von 100 nicht überschreiten.\n"
                        + "entfernen sie ggf. andere Leistungsblöcke.");
            } else {
                name.setInvalid(true);
                name.setErrorMessage("Leistungsblockname schon vergeben");
                Notification.show("Es existiert bereits ein Leistungsblock mit diesem Namen");
            }
        });

        binderLb.forField(gewichtung)
                .asRequired("Pflicht")
                .withConverter(new MyDoubleToIntegerConverter())
                .withValidator(v -> v >= 1, "Minimal: 1")
                .withValidator(v -> v <= 100, "Maximal: 100")
                .withValidator(new MyLeistungsblockValidator(lk, null, true))
                .bind(Leistungsblock::getGewichtung, Leistungsblock::setGewichtung);

        binderLb.forField(name)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.length() > 3, "Name ist zu kurz")
                .withValidator(v -> v.length() < 24, "Name ist zu lang")
                .bind(Leistungsblock::getName, Leistungsblock::setName);

        binderLb.forField(beschreibung)
                .withValidator(v -> v.length() < 48, "Beschreibung ist zu lang")
                .bind(Leistungsblock::getBeschreibung, Leistungsblock::setBeschreibung);

        beschreibung.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        gewichtung.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        name.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        HorizontalLayout buttons = new HorizontalLayout(zurück, erstellen);

        dialogLayout.add(name, beschreibung, gewichtung, buttons);

        dialog.add(dialogLayout);
        return dialog;
    }


    private Dialog neueLeistung(Leistungsblock lb) {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();

        TextField name = new TextField("Leistungsname");
        name.focus();
        name.setWidth(WIDTH_NAME);
        NumberField maxPunkte = new NumberField();
        maxPunkte.setLabel("Max. Punkte");
        maxPunkte.setWidth(WIDTH_NUMBER);
        TextField beschreibung = new TextField("Beschreibung");
        beschreibung.setWidth(WIDTH_BESCHREIBUNG);

        Button zurück = new Button("Zurück");
        zurück.addClickListener(e -> {
            dialog.close();
        });

        Button erstellen = new Button("Erstellen");
        erstellen.addClickShortcut(Key.ENTER);
        erstellen.setEnabled(false);

        ArrayList<TextField> tfsollenNichtLeerSein = new ArrayList<TextField>();
        tfsollenNichtLeerSein.add(name);
        ArrayList<NumberField> nfsollenNichtLeerSein = new ArrayList<NumberField>();
        nfsollenNichtLeerSein.add(maxPunkte);

        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });
        maxPunkte.setValueChangeMode(ValueChangeMode.EAGER);
        maxPunkte.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });
        beschreibung.setValueChangeMode(ValueChangeMode.EAGER);
        beschreibung.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(tfsollenNichtLeerSein, nfsollenNichtLeerSein));
        });

        erstellen.addClickListener(e -> {
            bv.addEinzelleistung(name.getValue(),
                    (int) Math.round(maxPunkte.getValue()), beschreibung.getValue(), lb
            );
            Notification.show("Die Leistung wurde erstellt.");
            dialog.close();
            updateLkAccordion();
        });

        //Eingabevalidierung

        binderLeistung.forField(maxPunkte)
                .asRequired("Pflicht")
                .withConverter(new MyDoubleToIntegerConverter())
                .withValidator(v -> v >= 1, "Minimal: 1")
                .withValidator(v -> v <= 1000, "Maximal: 1000")
                .bind(Leistung::getMaxPunkte, Leistung::setMaxPunkte);

        binderLeistung.forField(beschreibung)
                .withValidator(v -> v.length() < 48, "Beschreibung ist zu lang")
                .bind(Leistung::getBeschreibung, Leistung::setBeschreibung);

        binderLeistung.forField(name)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.length() > 3, "Name ist zu kurz")
                .withValidator(v -> v.length() < 24, "Name ist zu lang")
                .bind(Leistung::getName, Leistung::setName);

        HorizontalLayout buttons = new HorizontalLayout(zurück, erstellen);

        dialogLayout.add(name, beschreibung, maxPunkte, buttons);

        dialog.add(dialogLayout);
        return dialog;
    }

    /**
     * Methode prüft, ob die übergeben Listen von Vaadin-Feldern lee oder als ungültig markiert sind.
     *
     * @param textFields
     * @param numFields
     * @return true, wenn alle Felder weder ungültig noch leer sind.
     */
    private Boolean checkEnableButton(ArrayList<TextField> textFields, ArrayList<NumberField> numFields) {
        if (textFields != null) {
            for (TextField comp : textFields) {
                if (comp.isEmpty() || comp.isInvalid()) {
                    return false;
                }
            }
        }

        if (numFields != null) {
            for (NumberField comp : numFields) {
                if (comp.isEmpty() || comp.isInvalid()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean istLeistungskomplexFrei(String lkName) {
        return !leistungskomplexRepo.findById(lkName).isPresent();
    }

    private boolean istLeistungsblockFrei(String lbName) {
        return !leistungsblockRepo.findById(lbName).isPresent();
    }
}
