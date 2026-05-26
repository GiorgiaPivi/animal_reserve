package db_lab;

import db_lab.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntUnaryOperator;

/**
 * View estesa da JFrame - approccio classico simile a Cluedo
 */
public final class View extends JFrame {

    private Optional<Controller> controller;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public View() {
        super("Centro Recupero Animali");
        this.controller = Optional.empty();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        
        // Configurazione JFrame
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        // Aggiungo il pannello principale al frame
        this.add(mainPanel);
        
        this.setVisible(true);
    }

    public void setController(Controller controller) {
        Objects.requireNonNull(controller);
        this.controller = Optional.of(controller);
    }

    private Controller getController() {
        return controller.orElseThrow();
    }

    // ============ UTILITY METHODS ============
    
    private void showPage(String pageName, JPanel panel) {
        mainPanel.add(panel, pageName);
        cardLayout.show(mainPanel, pageName);
    }

    private JPanel createCenteredPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        return panel;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void addLabeledField(JPanel panel, String label, JComponent component) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(component);
        panel.add(row);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    // ============ PAGES ============

    public void loadingAnimali() {
        JPanel panel = createCenteredPanel();
        JLabel loadingLabel = new JLabel("Caricamento animali...");
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loadingLabel);
        showPage("loading", panel);
    }

    public void genericError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    public void genericMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Messaggio", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============ LOGIN ============

    public void loginPage() {
        JPanel panel = createCenteredPanel();
        
        JLabel title = new JLabel("Centro Recupero Animali");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        JTextField email = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);

        addLabeledField(panel, "Email:", email);
        addLabeledField(panel, "Password:", pass);
        panel.add(Box.createVerticalStrut(20));

        JButton loginBtn = createButton("Accedi", () -> 
            getController().userSubmittedLogin(email.getText(), new String(pass.getPassword())));
        panel.add(loginBtn);
        
        panel.add(Box.createVerticalStrut(10));
        
        JButton registerBtn = createButton("Registrati", () -> registrazionePage());
        panel.add(registerBtn);

        showPage("login", panel);
    }

    public void loginFailed(String reason) {
        genericError(reason);
        loginPage();
    }

    // ============ REGISTRAZIONE ============

    public void registrazionePage() {
        JPanel panel = createCenteredPanel();
        
        JLabel title = new JLabel("Nuova Registrazione");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        JTextField nome = new JTextField(20);
        JTextField cognome = new JTextField(20);
        JTextField email = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);

        addLabeledField(panel, "Nome:", nome);
        addLabeledField(panel, "Cognome:", cognome);
        addLabeledField(panel, "Email:", email);
        addLabeledField(panel, "Password:", pass);
        panel.add(Box.createVerticalStrut(20));

        JButton regBtn = createButton("Registrati", () ->
            getController().userSubmittedRegistrazione(
                nome.getText(), cognome.getText(), 
                email.getText(), new String(pass.getPassword())
            )
        );
        panel.add(regBtn);
        
        panel.add(Box.createVerticalStrut(10));
        
        JButton backBtn = createButton("Torna al Login", () -> loginPage());
        panel.add(backBtn);

        showPage("registrazione", panel);
    }

    public void registrazioneOk() {
        genericMessage("Registrazione completata!");
        loginPage();
    }

    public void registrazioneFailed(String reason) {
        genericError(reason);
    }

    // ============ LISTA ANIMALI ============

    public void animaliPage(List<Animale> animali, Utente utente) {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Benvenuto " + utente.nome + " " + utente.cognome + " [" + utente.ruolo + "]");
        welcome.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(welcome, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> getController().userClickedLogout());
        header.add(logoutBtn, BorderLayout.EAST);
        
        mainContent.add(header, BorderLayout.NORTH);

        // Lista animali con scroll
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        JLabel count = new JLabel("Animali nel centro: " + animali.size());
        count.setFont(new Font("Arial", Font.BOLD, 14));
        listPanel.add(count);
        listPanel.add(Box.createVerticalStrut(20));

        for (Animale a : animali) {
            JPanel animalRow = new JPanel(new BorderLayout());
            animalRow.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            animalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            
            JLabel info = new JLabel("  " + a.nome + " - " + a.nomeSpecie + " (età: " + a.eta + ") - " + a.statoDiSalute);
            animalRow.add(info, BorderLayout.CENTER);
            
            JButton detailBtn = new JButton("Dettagli");
            detailBtn.addActionListener(e -> getController().userClickedAnimale(a));
            animalRow.add(detailBtn, BorderLayout.EAST);
            
            listPanel.add(animalRow);
            listPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Pulsanti azioni
        JPanel actionsPanel = new JPanel(new FlowLayout());
        
        if (utente.isVeterinario()) {
            actionsPanel.add(createButton("Nuovo Animale", () -> getController().userRequestedNuovoAnimale()));
        }
        actionsPanel.add(createButton("Specie", () -> getController().userClickedSpecie()));
        actionsPanel.add(createButton("Recinti", () -> getController().userClickedRecinti()));
        actionsPanel.add(createButton("Trasporti", () -> getController().userClickedTuttiTrasporti()));
        actionsPanel.add(createButton("Statistiche", () -> getController().userClickedStatisticheGenerali()));
        
        mainContent.add(actionsPanel, BorderLayout.SOUTH);

        showPage("animali", mainContent);
    }

    // ============ DETTAGLIO ANIMALE ============

    public void dettaglioAnimale(Animale a, Utente u) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createCenteredPanel();

        JLabel titleLabel = new JLabel("Dettaglio Animale: " + a.nome);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createInfoLabel("Nome: " + a.nome));
        panel.add(createInfoLabel("Specie: " + a.nomeSpecie));
        panel.add(createInfoLabel("Età: " + a.eta));
        panel.add(createInfoLabel("Provenienza: " + a.provenienza));
        panel.add(createInfoLabel("Stato di salute: " + a.statoDiSalute));
        panel.add(createInfoLabel("Data arrivo: " + a.dataArrivo));
        panel.add(createInfoLabel("Descrizione: " + a.descrizione));
        panel.add(Box.createVerticalStrut(20));

        if (u.isVolontario() || u.isVeterinario()) {
            JComboBox<String> nuovoStato = new JComboBox<>(new String[]{"buono", "discreto", "critico"});
            nuovoStato.setSelectedItem(a.statoDiSalute);
            addLabeledField(panel, "Aggiorna stato:", nuovoStato);
            panel.add(createButton("Salva stato", () ->
                getController().userSubmittedAggiornaStato(a.id, (String) nuovoStato.getSelectedItem())));
            panel.add(Box.createVerticalStrut(20));
        }

        if (u.isVeterinario()) {
            panel.add(createButton("Storico controlli", () -> 
                getController().userClickedStoricoControlli(a.id)));
            panel.add(Box.createVerticalStrut(10));

            JComboBox<String> tipo = new JComboBox<>(new String[]{
                "visita di routine", "esami delle feci", "valutazione respiratoria", "valutazione cardiaca"
            });
            JComboBox<String> esito = new JComboBox<>(new String[]{"positivo", "negativo", "da monitorare"});

            addLabeledField(panel, "Tipologia controllo:", tipo);
            addLabeledField(panel, "Esito:", esito);
            panel.add(createButton("Registra controllo", () ->
                getController().userSubmittedNuovoControllo(a.id,
                    (String) tipo.getSelectedItem(),
                    (String) esito.getSelectedItem())));
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Indietro", () -> getController().userClickedBack()));

        JScrollPane scrollPane = new JScrollPane(panel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("dettaglio", outerPanel);
    }

    // ============ SPECIE ============

    public void speciePage(List<Specie> specie, IntUnaryOperator conta) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createCenteredPanel();

        JLabel title = new JLabel("Elenco Specie");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        for (Specie s : specie) {
            panel.add(new JLabel("• " + s.nome + " (" + conta.applyAsInt(s.id) + " animali)"));
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Indietro", () -> getController().userClickedBack()));

        JScrollPane scrollPane = new JScrollPane(panel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("specie", outerPanel);
    }

    // ============ NUOVO ANIMALE ============

    public void nuovoAnimaleForm(List<Specie> specie) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createCenteredPanel();

        JLabel titleLabel = new JLabel("Registra Nuovo Animale");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        JTextField nome = new JTextField(20);
        JTextField eta = new JTextField(20);
        JTextField provenienza = new JTextField(20);
        JTextField stato = new JTextField(20);
        JTextField descr = new JTextField(20);
        JComboBox<Specie> combo = new JComboBox<>(specie.toArray(new Specie[0]));

        addLabeledField(panel, "Nome:", nome);
        addLabeledField(panel, "Età:", eta);
        addLabeledField(panel, "Provenienza:", provenienza);
        addLabeledField(panel, "Stato:", stato);
        addLabeledField(panel, "Descrizione:", descr);
        addLabeledField(panel, "Specie:", combo);

        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Registra", () -> {
            try {
                getController().userSubmittedNuovoAnimale(
                    nome.getText(),
                    Integer.parseInt(eta.getText()),
                    provenienza.getText(),
                    stato.getText(),
                    descr.getText(),
                    ((Specie) combo.getSelectedItem()).id
                );
            } catch (Exception ex) {
                genericError("Errore nei dati inseriti.");
            }
        }));

        JScrollPane scrollPane = new JScrollPane(panel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("nuovoAnimale", outerPanel);
    }

    public void animaleRegistrato(int id) {
        genericMessage("Animale registrato con ID: " + id);
    }

    public void statoAggiornato() {
        genericMessage("Stato aggiornato.");
    }

    public void controlloRegistrato(int id) {
        genericMessage("Controllo registrato con ID: " + id);
    }

    // ============ STORICO CONTROLLI ============

    public void storicoControlliPage(List<ControlloSanitario> controlli) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createCenteredPanel();

        JLabel title = new JLabel("Storico Controlli Sanitari");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        for (ControlloSanitario c : controlli) {
            panel.add(new JLabel("• " + c.data + " - " + c.tipologia + " - " + c.esito));
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Indietro", () -> getController().userClickedBack()));

        JScrollPane scrollPane = new JScrollPane(panel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("storico", outerPanel);
    }

    public void terapiaRegistrata(int id) {
        genericMessage("Terapia registrata con ID: " + id);
    }

    // ============ LISTE GENERICHE ============

    public void movimentazioniPage(List<?> mov) {
        showGenericListPage("Movimentazioni", mov);
    }

    public void trasportiPage(List<?> trasporti) {
        showGenericListPage("Trasporti", trasporti);
    }

    public void terapiePage(List<?> terapie) {
        showGenericListPage("Terapie", terapie);
    }

    public void recintPage(List<?> recinti) {
        showGenericListPage("Recinti", recinti);
    }

    private void showGenericListPage(String title, List<?> items) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createCenteredPanel();

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        for (Object item : items) {
            panel.add(new JLabel("• " + item.toString()));
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Indietro", () -> getController().userClickedBack()));

        JScrollPane scrollPane = new JScrollPane(panel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage(title.toLowerCase(), outerPanel);
    }

    // ============ DETTAGLIO RECINTO ============

    public void dettaglioRecinto(Object recinto, int numAnimali) {
        JPanel panel = createCenteredPanel();

        JLabel titleLabel = new JLabel("Dettaglio Recinto");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        panel.add(new JLabel(recinto.toString()));
        panel.add(new JLabel("Animali presenti: " + numAnimali));
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Indietro", () -> getController().userClickedBack()));

        showPage("dettaglioRecinto", panel);
    }

    // ============ STATISTICHE ============

    public void showStatistiche(Map<String, Object> stats) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createCenteredPanel();

        JLabel title = new JLabel("Statistiche Centro");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        for (Map.Entry<String, Object> e : stats.entrySet()) {
            JLabel stat = new JLabel("• " + e.getKey() + ": " + e.getValue());
            stat.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(stat);
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("Indietro", () -> getController().userClickedBack()));

        JScrollPane scrollPane = new JScrollPane(panel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("statistiche", outerPanel);
    }
}