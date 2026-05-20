package db_lab;

import db_lab.model.Animale;
import db_lab.model.ControlloSanitario;
import db_lab.model.Specie;
import db_lab.model.Utente;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class View {

    private Optional<Controller> controller;
    private final JFrame mainFrame;

    public View(Runnable onClose) {
        this.controller = Optional.empty();
        this.mainFrame = this.setupMainFrame(onClose);
    }

    private JFrame setupMainFrame(Runnable onClose) {
        var frame = new JFrame("Centro Recupero Animali");
        var padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        ((JComponent) frame.getContentPane()).setBorder(padding);
        frame.setMinimumSize(new Dimension(400, 150));
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose.run();
                System.exit(0);
            }
        });
        return frame;
    }

    private Controller getController() {
        if (this.controller.isPresent()) {
            return this.controller.get();
        } else {
            throw new IllegalStateException(
                """
                Il Controller della View non è definito. Ricordati di chiamare
                `setController` prima di avviare l'applicazione.
                """
            );
        }
    }

    public void setController(Controller controller) {
        Objects.requireNonNull(controller, "setController chiamato con null");
        this.controller = Optional.of(controller);
    }

    // ------- Pagine di stato -------

    public void loadingAnimali() {
        freshPane(cp -> cp.add(new JLabel("Caricamento animali...", SwingConstants.CENTER)));
    }

    public void genericError(String message) {
        freshPane(cp -> {
            cp.add(new JLabel("Errore: " + message, SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Indietro", () -> this.getController().userClickedBack()));
        });
    }

    // ------- Login / Registrazione -------

    public void loginPage() {
        freshPane(cp -> {
            cp.add(new JLabel("Centro Recupero Animali", SwingConstants.CENTER));
            cp.add(new JLabel(" "));

            var emailField = new JTextField(20);
            var passwordField = new JTextField(20);

            cp.add(new JLabel("Email:"));
            cp.add(emailField);
            cp.add(new JLabel("Password:"));
            cp.add(passwordField);
            cp.add(new JLabel(" "));

            cp.add(button("Accedi", () -> this.getController()
                .userSubmittedLogin(emailField.getText(), passwordField.getText())));
            cp.add(button("Registrati come visitatore", () ->
                this.getController().userRequestedInitialPage()
                // mostra form registrazione
            ));
        });
    }

    public void loginFailed(String reason) {
        freshPane(cp -> {
            cp.add(new JLabel("Accesso fallito: " + reason, SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Riprova", () -> this.getController().userRequestedInitialPage()));
        });
    }

    public void registrazioneOk() {
        freshPane(cp -> {
            cp.add(new JLabel("Registrazione completata! Ora puoi accedere.", SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Vai al login", () -> this.getController().userRequestedInitialPage()));
        });
    }

    public void registrazioneFailed(String reason) {
        freshPane(cp -> {
            cp.add(new JLabel("Registrazione fallita: " + reason, SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Riprova", () -> this.getController().userRequestedInitialPage()));
        });
    }

    // ------- Lista Animali -------

    public void animaliPage(List<Animale> animali, Utente utente) {
        freshPane(cp -> {
            cp.add(new JLabel("Benvenuto/a " + utente.nome + " " + utente.cognome
                + " [" + utente.ruolo + "]", SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(new JLabel("Animali nel centro (" + animali.size() + "):", SwingConstants.LEFT));
            cp.add(new JLabel(" "));

            for (var a : animali) {
                var label = "- " + a.nome + " [" + a.nomeSpecie + "] - " + a.statoDiSalute;
                cp.add(clickableLabel(label, () -> this.getController().userClickedAnimale(a)));
            }

            cp.add(new JLabel(" "));

            // Filtri e ricerca
            var searchField = new JTextField(15);
            cp.add(new JLabel("Cerca per nome:"));
            cp.add(searchField);
            cp.add(button("Cerca", () -> this.getController()
                .userSubmittedSearch(searchField.getText())));

            var stati = new JComboBox<>(new String[]{"tutti", "buono", "discreto", "critico"});
            cp.add(new JLabel("Filtra per stato:"));
            cp.add(stati);
            cp.add(button("Filtra", () -> {
                var sel = (String) stati.getSelectedItem();
                if ("tutti".equals(sel)) {
                    this.getController().userClickedReloadAnimali();
                } else {
                    this.getController().userSelectedFiltroStato(sel);
                }
            }));

            cp.add(new JLabel(" "));
            cp.add(button("Visualizza specie", () -> this.getController().userClickedSpecie()));
            cp.add(button("Ricarica", () -> this.getController().userClickedReloadAnimali()));

            if (utente.isVolontario() || utente.isVeterinario()) {
                cp.add(new JLabel(" "));
                cp.add(button("Registra nuovo animale",
                    () -> this.getController().userRequestedNuovoAnimale()));
            }
            if (utente.isVeterinario()) {
                cp.add(button("Animali da controllare",
                    () -> this.getController().userClickedAnimaliDaControllare()));
            }

            cp.add(new JLabel(" "));
            cp.add(button("Logout", () -> this.getController().userClickedLogout()));
        });
    }

    // ------- Dettaglio Animale -------

    public void dettaglioAnimale(Animale animale, Utente utente) {
        freshPane(cp -> {
            cp.add(new JLabel("Animale: " + animale.nome));
            cp.add(new JLabel("Specie: " + animale.nomeSpecie));
            cp.add(new JLabel("Età: " + animale.eta + " anni"));
            cp.add(new JLabel("Provenienza: " + animale.provenienza));
            cp.add(new JLabel("Stato di salute: " + animale.statoDiSalute));
            cp.add(new JLabel("Data arrivo: " + animale.dataArrivo));
            cp.add(new JLabel("Descrizione: " + animale.descrizione));
            cp.add(new JLabel(" "));

            if (utente.isVolontario() || utente.isVeterinario()) {
                var nuovoStato = new JComboBox<>(new String[]{"buono", "discreto", "critico"});
                nuovoStato.setSelectedItem(animale.statoDiSalute);
                cp.add(new JLabel("Aggiorna stato:"));
                cp.add(nuovoStato);
                cp.add(button("Salva stato", () -> this.getController()
                    .userSubmittedAggiornaStato(animale.id, (String) nuovoStato.getSelectedItem())));
                cp.add(new JLabel(" "));
            }

            if (utente.isVeterinario()) {
                cp.add(button("Storico controlli",
                    () -> this.getController().userClickedStoricoControlli(animale.id)));

                cp.add(new JLabel(" "));
                cp.add(new JLabel("Nuovo controllo:"));
                var tipologie = new JComboBox<>(new String[]{
                    "visita di routine", "esami delle feci",
                    "valutazione respiratoria", "valutazione cardiaca"});
                var esiti = new JComboBox<>(new String[]{"positivo", "negativo", "da monitorare"});
                cp.add(new JLabel("Tipologia:")); cp.add(tipologie);
                cp.add(new JLabel("Esito:")); cp.add(esiti);
                cp.add(button("Registra controllo", () -> this.getController()
                    .userSubmittedNuovoControllo(animale.id,
                        (String) tipologie.getSelectedItem(),
                        (String) esiti.getSelectedItem())));
            }

            cp.add(new JLabel(" "));
            cp.add(button("Indietro", () -> this.getController().userClickedBack()));
        });
    }

    // ------- Specie -------

    public void speciePage(List<Specie> specie, IntUnaryOperator contaAnimali) {
        freshPane(cp -> {
            cp.add(new JLabel("Specie nel centro:", SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            for (var s : specie) {
                int count = contaAnimali.applyAsInt(s.id);
                cp.add(new JLabel("- " + s.nome + " (" + count + " animali)"));
            }
            cp.add(new JLabel(" "));
            cp.add(button("Indietro", () -> this.getController().userClickedBack()));
        });
    }

    // ------- Form Nuovo Animale -------

    public void nuovoAnimaleForm(List<Specie> specie) {
        freshPane(cp -> {
            cp.add(new JLabel("Registra nuovo animale", SwingConstants.CENTER));
            cp.add(new JLabel(" "));

            var nomeField = new JTextField(15);
            var etaField = new JTextField(5);
            var provenienzaField = new JTextField(15);
            var descrizioneField = new JTextField(20);
            var stati = new JComboBox<>(new String[]{"buono", "discreto", "critico"});
            var specieBox = new JComboBox<>(specie.stream().map(s -> s.nome).toArray(String[]::new));

            cp.add(new JLabel("Nome:")); cp.add(nomeField);
            cp.add(new JLabel("Età:")); cp.add(etaField);
            cp.add(new JLabel("Provenienza:")); cp.add(provenienzaField);
            cp.add(new JLabel("Descrizione:")); cp.add(descrizioneField);
            cp.add(new JLabel("Stato:")); cp.add(stati);
            cp.add(new JLabel("Specie:")); cp.add(specieBox);
            cp.add(new JLabel(" "));

            cp.add(button("Registra", () -> {
                try {
                    int eta = Integer.parseInt(etaField.getText().trim());
                    int idxSpecie = specieBox.getSelectedIndex();
                    this.getController().userSubmittedNuovoAnimale(
                        nomeField.getText(), eta, provenienzaField.getText(),
                        (String) stati.getSelectedItem(),
                        descrizioneField.getText(),
                        specie.get(idxSpecie).id
                    );
                } catch (NumberFormatException ex) {
                    cp.add(new JLabel("Età non valida!"));
                    cp.validate(); cp.repaint(); mainFrame.pack();
                }
            }));
            cp.add(button("Annulla", () -> this.getController().userClickedBack()));
        });
    }

    // ------- Feedback operazioni -------

    public void animaleRegistrato(int id) {
        freshPane(cp -> {
            cp.add(new JLabel("Animale registrato con ID: " + id, SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Torna alla lista", () -> this.getController().userClickedReloadAnimali()));
        });
    }

    public void statoAggiornato() {
        // Il controller chiama subito loadAnimaliPage, non serve una pagina dedicata
    }

    public void controlloRegistrato(int id) {
        freshPane(cp -> {
            cp.add(new JLabel("Controllo registrato con ID: " + id, SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Indietro", () -> this.getController().userClickedBack()));
        });
    }

    public void terapiaRegistrata(int id) {
        freshPane(cp -> {
            cp.add(new JLabel("Terapia prescritta con ID: " + id, SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            cp.add(button("Indietro", () -> this.getController().userClickedBack()));
        });
    }

    // ------- Storico controlli -------

    public void storicoControlliPage(List<ControlloSanitario> controlli) {
        freshPane(cp -> {
            cp.add(new JLabel("Storico controlli sanitari:", SwingConstants.CENTER));
            cp.add(new JLabel(" "));
            if (controlli.isEmpty()) {
                cp.add(new JLabel("Nessun controllo registrato.", SwingConstants.CENTER));
            } else {
                for (var c : controlli) {
                    cp.add(new JLabel("• " + c.data + " " + c.ora
                        + " | " + c.tipologia + " → " + c.esito));
                }
            }
            cp.add(new JLabel(" "));
            cp.add(button("Indietro", () -> this.getController().userClickedBack()));
        });
    }

    // ------- Private helpers -------

    private JButton button(String label, Runnable action) {
        var btn = new JButton(label);
        btn.addActionListener(event -> {
            btn.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                action.run();
                btn.setEnabled(true);
            });
        });
        return btn;
    }

    private JLabel clickableLabel(String labelText, Runnable action) {
        var label = new JLabel(labelText);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(action::run);
            }
        });
        return label;
    }

    private void freshPane(Consumer<Container> consumer) {
        var cp = this.mainFrame.getContentPane();
        cp.removeAll();
        consumer.accept(cp);
        cp.validate();
        cp.repaint();
        this.mainFrame.pack();
    }
}
