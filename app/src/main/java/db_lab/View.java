package db_lab;

import db_lab.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntUnaryOperator;

/**
 * View
 */
public final class View extends JFrame {
    // Palette colori
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blu acceso
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Blu più chiaro
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // Grigio chiaro
    private static final Color TEXT_COLOR = new Color(44, 62, 80);           // Grigio scuro
    private static final Color BORDER_COLOR = new Color(189, 195, 199);      // Grigio neutro
   
    private Optional<Controller> controller;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    
    public View() {
        super("Centro Recupero Animali");
        this.controller = Optional.empty();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        
        // Configurazione JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        this.setLocationRelativeTo(null);
        this.mainPanel.setBackground(BACKGROUND_COLOR);
        this.add(mainPanel);
        this.setVisible(true);   
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }
    
    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void addLabeledField(JPanel panel, String label, JComponent component) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row.setBackground(BACKGROUND_COLOR);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComp.setForeground(TEXT_COLOR);
        labelComp.setPreferredSize(new Dimension(150, 25));
        
        if (component instanceof JTextField) {
            ((JTextField) component).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            component.setPreferredSize(new Dimension(250, 30));
        } else if (component instanceof JPasswordField) {
            ((JPasswordField) component).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            component.setPreferredSize(new Dimension(250, 30));
        } else if (component instanceof JComboBox) {
            ((JComboBox<?>) component).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            component.setPreferredSize(new Dimension(250, 30));
        }
        
        row.add(labelComp);
        row.add(component);
        panel.add(row);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(PRIMARY_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    private JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(PRIMARY_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    // ============ PAGES ============
    
    public void loadingAnimali() {
        JPanel panel = createCenteredPanel();
        JLabel loadingLabel = new JLabel("Caricamento animali...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loadingLabel.setForeground(PRIMARY_COLOR);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(loadingLabel);
        panel.add(Box.createVerticalGlue());
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
        
        JLabel title = createTitleLabel("Centro Recupero Animali");
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        
        JLabel subtitle = new JLabel("Gestione Centro Veterinario");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitle.setForeground(new Color(127, 140, 141));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        
        JTextField email = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);
        addLabeledField(panel, "Email:", email);
        addLabeledField(panel, "Password:", pass);
        panel.add(Box.createVerticalStrut(20));
        
        JButton loginBtn = createButton("Accedi", () -> 
            getController().userSubmittedLogin(email.getText(), new String(pass.getPassword()))
        );
        panel.add(loginBtn);
        
        panel.add(Box.createVerticalStrut(10));
        
        JButton registerBtn = createButton("Registrati", () -> registrazionePage());
        panel.add(registerBtn);
        panel.add(Box.createVerticalGlue());
        
        showPage("login", panel);
    }
    
    public void loginFailed(String reason) {
        genericError(reason);
        loginPage();
    }
    
    // ============ REGISTRAZIONE ============
    
    public void registrazionePage() {
        JPanel panel = createCenteredPanel();
        
        JLabel title = createTitleLabel("Nuova Registrazione");
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        
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
        panel.add(Box.createVerticalGlue());
        
        showPage("registrazione", panel);
    }
    
    public void registrazioneOk() {
        genericMessage("Registrazione completata!");
        loginPage();
    }
    
    public void registrazioneFailed(String reason) {
        genericError(reason);
    }

    public void turniPage(List<?> turni) {
        showGenericListPage("Turni", turni);
    }

    public void mansioniPage(List<?> mansioni) {
        showGenericListPage("Mansioni", mansioni);
    }
    
    // ============ LISTA ANIMALI ============
    
    public void animaliPage(List<Animale> animali, Utente utente) {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel welcome = new JLabel("Benvenuto " + utente.nome + " " + utente.cognome);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcome.setForeground(Color.WHITE);
        header.add(welcome, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> getController().userClickedLogout());
        header.add(logoutBtn, BorderLayout.EAST);
        
        mainContent.add(header, BorderLayout.NORTH);
        
        // Lista animali con scroll
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);
        listPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel count = new JLabel("Animali nel centro: " + animali.size());
        count.setFont(new Font("Segoe UI", Font.BOLD, 14));
        count.setForeground(PRIMARY_COLOR);
        listPanel.add(count);
        listPanel.add(Box.createVerticalStrut(15));
        
        for (Animale a : animali) {
            JPanel animalRow = new JPanel(new BorderLayout());
            animalRow.setBackground(Color.WHITE);
            animalRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 15, 12, 15)
            ));
            animalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            
            JLabel info = new JLabel(a.nome + " - " + a.nomeSpecie + " (età: " + a.eta + " anni)");
            info.setFont(new Font("Segoe UI", Font.BOLD, 13));
            info.setForeground(TEXT_COLOR);
            animalRow.add(info, BorderLayout.CENTER);
            
            JButton detailBtn = createButton("Dettagli", () -> getController().userClickedAnimale(a));
            animalRow.add(detailBtn, BorderLayout.EAST);
            
            listPanel.add(animalRow);
            listPanel.add(Box.createVerticalStrut(8));
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainContent.add(scrollPane, BorderLayout.CENTER);
        
        // Pulsanti azioni
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionsPanel.setBackground(BACKGROUND_COLOR);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        if (utente.isVeterinario()) {
            actionsPanel.add(createButton("+ Nuovo Animale", () -> getController().userRequestedNuovoAnimale()));
        }
        actionsPanel.add(createButton("Specie", () -> getController().userClickedSpecie()));
        actionsPanel.add(createButton("Recinti", () -> getController().userClickedRecinti()));
        actionsPanel.add(createButton("Trasporti", () -> getController().userClickedTuttiTrasporti()));
        actionsPanel.add(createButton("Statistiche", () -> getController().userClickedStatisticheGenerali()));

        if (utente.isVeterinario() || utente.isVolontario()) {
            actionsPanel.add(createButton("Turni", () -> getController().userClickedTurni()));
            actionsPanel.add(createButton("Mansioni", () -> getController().userClickedMansioni()));
        }

        if (utente.ruolo.equalsIgnoreCase("admin")) {
            actionsPanel.add(createButton("⚙️ Admin Panel", () -> getController().userClickedAdminPanel()));
        }
        
        mainContent.add(actionsPanel, BorderLayout.SOUTH);
        showPage("animali", mainContent);
    }
    public void adminPanelPage() {
        JPanel panel = createCenteredPanel();
        
        JLabel title = createTitleLabel("Pannello Amministrazione");
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        
        panel.add(createButton("Crea Nuovo Turno", () -> getController().userRequestedNuovoTurno()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Visualizza Turni", () -> getController().adminClickedAllTurni()));
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("Crea Nuova Mansione", () -> getController().userRequestedNuovaMansione()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Visualizza Mansioni", () -> getController().adminClickedAllMansioni()));
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("Gestisci Specie", () -> getController().userClickedSpecie()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Gestisci Recinti", () -> getController().userClickedRecinti()));
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        showPage("adminPanel", panel);
    }

    public void nuovoTurnoForm() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Crea Nuovo Turno");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        JSpinner dataSpinner = new JSpinner(new SpinnerDateModel());
        JComboBox<String> fasciaCombo = new JComboBox<>(new String[]{"mattina", "pomeriggio", "notte"});
        
        addLabeledField(panel, "Data:", dataSpinner);
        addLabeledField(panel, "Fascia:", fasciaCombo);
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("✓ Crea Turno", () -> {
            try {
                java.util.Date utilDate = (java.util.Date) dataSpinner.getValue();
                LocalDate data = new java.sql.Date(utilDate.getTime()).toLocalDate();
                String fascia = (String) fasciaCombo.getSelectedItem();
                getController().adminCreatedTurno(data, fascia);
            } catch (Exception ex) {
                genericError("Errore nei dati inseriti.");
            }
        }));
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("← Annulla", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("nuovoTurno", outerPanel);
}

    public void nuovaMansioneForm() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Crea Nuova Mansione");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        JTextField descrizione = new JTextField(30);
        addLabeledField(panel, "Descrizione:", descrizione);
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("✓ Crea Mansione", () -> {
            try {
                if (descrizione.getText().isEmpty()) {
                    genericError("La descrizione è obbligatoria.");
                    return;
                }
                getController().adminCreatedMansione(descrizione.getText());
            } catch (Exception ex) {
                genericError("Errore nei dati inseriti.");
            }
        }));
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("← Annulla", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("nuovaMansione", outerPanel);
    }
    
    // ============ DETTAGLIO ANIMALE ============
    
    public void dettaglioAnimale(Animale a, Utente u) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Dettaglio Animale: " + a.nome);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Pannello informazioni
        JPanel infoBox = new JPanel();
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setBackground(Color.WHITE);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        infoBox.add(createInfoLabel("Nome: " + a.nome));
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(createInfoLabel("Specie: " + a.nomeSpecie));
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(createInfoLabel("Età: " + a.eta + " anni"));
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(createInfoLabel("Provenienza: " + a.provenienza));
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(createInfoLabel("Stato di salute: " + a.statoDiSalute));
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(createInfoLabel("Data arrivo: " + a.dataArrivo));
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(createInfoLabel("Descrizione: " + a.descrizione));
        
        panel.add(infoBox);
        panel.add(Box.createVerticalStrut(20));
        
        if (u.isVolontario() || u.isVeterinario()) {
            JLabel updateLabel = createSubtitleLabel("Aggiorna Stato di Salute");
            panel.add(updateLabel);
            panel.add(Box.createVerticalStrut(10));
            
            JComboBox<String> nuovoStato = new JComboBox<>(new String[]{"buono", "discreto", "cattivo"});
            nuovoStato.setSelectedItem(a.statoDiSalute);
            addLabeledField(panel, "Nuovo Stato:", nuovoStato);
            panel.add(Box.createVerticalStrut(10));
            panel.add(createButton("Salva stato", () ->
                getController().userSubmittedAggiornaStato(a.id, (String) nuovoStato.getSelectedItem())
            ));
            panel.add(Box.createVerticalStrut(20));
        }
        
        if (u.isVeterinario()) {
            JLabel checkupLabel = createSubtitleLabel("Registra Controllo Sanitario");
            panel.add(checkupLabel);
            panel.add(Box.createVerticalStrut(10));
            
            JComboBox<String> tipo = new JComboBox<>(new String[]{
                "visita di routine", "esami delle feci", "valutazione respiratoria", "valutazione muscolare"
            });
            JComboBox<String> esito = new JComboBox<>(new String[]{"positivo", "negativo", "dubbio"});
            addLabeledField(panel, "Tipologia:", tipo);
            addLabeledField(panel, "Esito:", esito);
            panel.add(Box.createVerticalStrut(10));
            panel.add(createButton("Registra controllo", () ->
                getController().userSubmittedNuovoControllo(a.id,
                    (String) tipo.getSelectedItem(),
                    (String) esito.getSelectedItem())
            ));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createButton("Storico controlli", () -> 
                getController().userClickedStoricoControlli(a.id)));
        }
        
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("dettaglio", outerPanel);
    }
    
    // ============ SPECIE ============
    
    public void speciePage(List<Specie> specie, IntUnaryOperator conta) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel title = createTitleLabel("Elenco Specie");
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel specieBox = new JPanel();
        specieBox.setLayout(new BoxLayout(specieBox, BoxLayout.Y_AXIS));
        specieBox.setBackground(Color.WHITE);
        specieBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        for (Specie s : specie) {
            JLabel specieLabel = new JLabel( s.nome + " (" + conta.applyAsInt(s.id) + " animali)");
            specieLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            specieLabel.setForeground(TEXT_COLOR);
            specieBox.add(specieLabel);
            specieBox.add(Box.createVerticalStrut(8));
        }
        
        panel.add(specieBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("specie", outerPanel);
    }
    
    // ============ NUOVO ANIMALE ============
    
    public void nuovoAnimaleForm(List<Specie> specie) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Registra Nuovo Animale");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
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
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("← Annulla", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
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
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel title = createTitleLabel("Storico Controlli Sanitari");
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel controlliBox = new JPanel();
        controlliBox.setLayout(new BoxLayout(controlliBox, BoxLayout.Y_AXIS));
        controlliBox.setBackground(Color.WHITE);
        controlliBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        for (ControlloSanitario c : controlli) {
            JLabel controlLabel = new JLabel( c.data + " - " + c.tipologia + " [" + c.esito + "]");
            controlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            controlLabel.setForeground(TEXT_COLOR);
            controlliBox.add(controlLabel);
            controlliBox.add(Box.createVerticalStrut(8));
        }
        
        panel.add(controlliBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
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
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel(title);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel itemsBox = new JPanel();
        itemsBox.setLayout(new BoxLayout(itemsBox, BoxLayout.Y_AXIS));
        itemsBox.setBackground(Color.WHITE);
        itemsBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        for (Object item : items) {
            JLabel itemLabel = new JLabel( item.toString());
            itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            itemLabel.setForeground(TEXT_COLOR);
            itemsBox.add(itemLabel);
            itemsBox.add(Box.createVerticalStrut(8));
        }
        
        panel.add(itemsBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage(title.toLowerCase(), outerPanel);
    }
    
    // ============ DETTAGLIO RECINTO ============
    
    public void dettaglioRecinto(Object recinto, int numAnimali) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Dettaglio Recinto");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel recintoBox = new JPanel();
        recintoBox.setLayout(new BoxLayout(recintoBox, BoxLayout.Y_AXIS));
        recintoBox.setBackground(Color.WHITE);
        recintoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel recintoInfo = createInfoLabel(recinto.toString());
        recintoBox.add(recintoInfo);
        recintoBox.add(Box.createVerticalStrut(15));
        
        JLabel animaliInfo = createInfoLabel(" Animali presenti: " + numAnimali);
        recintoBox.add(animaliInfo);
        
        panel.add(recintoBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        showPage("dettaglioRecinto", panel);
    }
    
    // ============ STATISTICHE ============
    
    public void showStatistiche(Map<String, Object> stats) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel title = createTitleLabel("Statistiche Centro");
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel statsBox = new JPanel();
        statsBox.setLayout(new BoxLayout(statsBox, BoxLayout.Y_AXIS));
        statsBox.setBackground(Color.WHITE);
        statsBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        for (Map.Entry<String, Object> e : stats.entrySet()) {
            JLabel stat = new JLabel( e.getKey() + ": " + e.getValue());
            stat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            stat.setForeground(TEXT_COLOR);
            statsBox.add(stat);
            statsBox.add(Box.createVerticalStrut(8));
        }
        
        panel.add(statsBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("statistiche", outerPanel);
    }
}