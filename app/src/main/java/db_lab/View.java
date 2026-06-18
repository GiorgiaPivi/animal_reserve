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
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(40, 50, 40, 50)
        ));
        card.setMaximumSize(new Dimension(400, 500));

        JLabel title = createTitleLabel("Centro Recupero Animali");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Gestione Centro Veterinario");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(new Color(127, 140, 141));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));

        JTextField email = new JTextField();
        email.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        email.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        email.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        JPasswordField pass = new JPasswordField();
        pass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(TEXT_COLOR);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(email);
        card.add(Box.createVerticalStrut(15));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(pass);
        card.add(Box.createVerticalStrut(25));

        JButton loginBtn = new JButton("Accedi");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(PRIMARY_COLOR);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginBtn.addActionListener(e ->
            getController().userSubmittedLogin(email.getText(), new String(pass.getPassword()))
        );

        JButton registerBtn = new JButton("Registrati");
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerBtn.setForeground(PRIMARY_COLOR);
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(10, 0, 10, 0)
        ));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.addActionListener(e -> registrazionePage());

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);

        outerPanel.add(card);
        showPage("login", outerPanel);
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

    public void mansioniPage(List<?> mansioni, Optional<Utente> utente) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Mansioni");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        List<?> mansioniFiltratre = mansioni;
        if (utente.isPresent() && !utente.get().ruolo.equalsIgnoreCase("admin")) {
            String tipoRuolo = utente.get().ruolo.equalsIgnoreCase("veterinario") ? "veterinario" : "volontario";
            mansioniFiltratre = mansioni.stream()
                .filter(m -> {
                    if (m instanceof Mansione) {
                        return ((Mansione) m).tipoMansione.equals(tipoRuolo);
                    }
                    return true;
                })
                .toList();
        }
        
        JPanel mansioniBox = new JPanel();
        mansioniBox.setLayout(new BoxLayout(mansioniBox, BoxLayout.Y_AXIS));
        mansioniBox.setBackground(Color.WHITE);
        mansioniBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // USA mansioniFiltratre INVECE DI mansioni:
        for (Object mansione : mansioniFiltratre) {
            JLabel mansioneLabe = new JLabel(mansione.toString());
            mansioneLabe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            mansioneLabe.setForeground(TEXT_COLOR);
            mansioniBox.add(mansioneLabe);
            mansioniBox.add(Box.createVerticalStrut(8));
        }
        
        panel.add(mansioniBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        showPage("mansioni", outerPanel);
    }
    
    // ============ LISTA ANIMALI ============
    
    public void animaliPage(List<Animale> animali, Utente utente) {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel welcome = new JLabel("Benvenuto " + utente.nome + " " + utente.cognome);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcome.setForeground(Color.WHITE);
        header.add(welcome, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> getController().userClickedLogout());
        header.add(logoutBtn, BorderLayout.EAST);

        mainContent.add(header, BorderLayout.NORTH);

        // ---- LISTA ANIMALI ----
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);
        listPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel count = new JLabel("Animali nel centro: " + animali.size());
        count.setFont(new Font("Segoe UI", Font.BOLD, 15));
        count.setForeground(PRIMARY_COLOR);
        count.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.add(count);
        listPanel.add(Box.createVerticalStrut(12));

        for (Animale a : animali) {
            JPanel row = new JPanel(new BorderLayout(15, 0));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 18, 10, 12)
            ));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setBackground(Color.WHITE);

            JLabel nome = new JLabel(a.nome + " — " + a.nomeSpecie);
            nome.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nome.setForeground(TEXT_COLOR);

            JLabel dettagli = new JLabel("Età: " + a.eta + " anni  •  Stato: " + a.statoDiSalute);
            dettagli.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            dettagli.setForeground(new Color(130, 140, 150));

            textPanel.add(nome, BorderLayout.NORTH);
            textPanel.add(dettagli, BorderLayout.SOUTH);

            JButton btn = createButton("Dettagli →", () -> getController().userClickedAnimale(a));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            row.add(textPanel, BorderLayout.CENTER);
            row.add(btn, BorderLayout.EAST);

            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(6));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // ---- BARRA AZIONI ----
        JPanel actionsBar = new JPanel(new BorderLayout());
        actionsBar.setBackground(Color.WHITE);
        actionsBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(12, 25, 12, 25)
        ));

        // Gruppo sinistra — operazioni principali
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(Color.WHITE);
        if (utente.isVeterinario()) {
            left.add(createButton("+ Nuovo Animale", () -> getController().userRequestedNuovoAnimale()));
        }
        left.add(createButton("Specie", () -> getController().userClickedSpecie()));
        left.add(createButton("Recinti", () -> getController().userClickedRecinti()));
        left.add(createButton("Trasporti", () -> getController().userClickedTuttiTrasporti()));
        left.add(createButton("Statistiche", () -> getController().userClickedStatisticheGenerali()));

        // Gruppo destra — funzioni staff
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(Color.WHITE);
        if (utente.isVeterinario()) {
            right.add(createButton("Animali da Controllare", () -> getController().userClickedAnimaliDaControllare()));
        }
        if (utente.isVeterinario() || utente.isVolontario()) {
            right.add(createButton("Turni", () -> getController().userClickedTurni()));
            right.add(createButton("Mansioni", () -> getController().userClickedMansioni()));
        }
        if (utente.ruolo.equalsIgnoreCase("admin")) {
            right.add(createButton("⚙ Admin Panel", () -> getController().userClickedAdminPanel()));
        }

        actionsBar.add(left, BorderLayout.WEST);
        actionsBar.add(right, BorderLayout.EAST);
        mainContent.add(actionsBar, BorderLayout.SOUTH);

        showPage("animali", mainContent);
    }
    public void adminPanelPage() {
        JPanel panel = createCenteredPanel();
        
        JLabel title = createTitleLabel("Pannello Amministrazione");
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        
        panel.add(createButton("Assegna Turno", () -> getController().userRequestedNuovoTurno()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Visualizza Turni", () -> getController().adminClickedAllTurni()));
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("Crea e Assegna Mansione", () -> getController().userRequestedNuovaMansione()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Visualizza Mansioni", () -> getController().adminClickedAllMansioni()));
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("+ Crea Specie", () -> getController().userRequestedNuovaSpecie()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Gestisci Specie", () -> getController().userClickedSpecie()));
        panel.add(Box.createVerticalStrut(20));

        panel.add(createButton("+ Crea Personale", () -> getController().userRequestedNuovoPersonale()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButton("Visualizza Personale", () -> getController().adminClickedPersonale()));
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());
        
        showPage("adminPanel", panel);
    }

    public void nuovoTurnoForm() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Assegna Turno");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        JSpinner dataSpinner = new JSpinner(new SpinnerDateModel());
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));
        JComboBox<String> fasciaCombo = new JComboBox<>(new String[]{"mattina", "pomeriggio"});
        JSpinner idUtenteSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 1000, 1));
        
        addLabeledField(panel, "Data:", dataSpinner);
        addLabeledField(panel, "Fascia:", fasciaCombo);
        addLabeledField(panel, "ID Utente:", idUtenteSpinner);
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("✓ Assegna Turno", () -> {
            try {
                java.util.Date utilDate = (java.util.Date) dataSpinner.getValue();
                LocalDate data = new java.sql.Date(utilDate.getTime()).toLocalDate();
                String fascia = (String) fasciaCombo.getSelectedItem();
                int idUtente = (int) idUtenteSpinner.getValue();
                getController().adminAssignedTurno(idUtente, data, fascia);
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel title = new JLabel("Crea e Assegna Mansione");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BACKGROUND_COLOR);

        String[] mansioniVolontario = {"Pulizia recinto", "Distribuzione cibo e acqua", "Manutenzione recinto", "Accoglienza animali"};
        String[] mansioniVeterinario = {"Somministrazione medicinali", "Visita animali", "Assistenza veterinaria", "Vaccinazioni"};

        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"volontario", "veterinario"});
        JComboBox<String> descrizioneCombo = new JComboBox<>(mansioniVolontario);

        tipoCombo.addActionListener(e -> {
            descrizioneCombo.removeAllItems();
            String[] mansioni = "volontario".equals(tipoCombo.getSelectedItem())
                ? mansioniVolontario : mansioniVeterinario;
            for (String m : mansioni) descrizioneCombo.addItem(m);
        });

        var staff = getController().getStaff();

        // Filtra per tipo iniziale (volontario)
        JComboBox<Utente> utenteCombo = new JComboBox<>();
        staff.stream()
            .filter(u -> "volontario".equalsIgnoreCase(u.ruolo))
            .forEach(utenteCombo::addItem);

        // Aggiorna il combo utenti quando cambia il tipo
        tipoCombo.addActionListener(e -> {
            String tipoSel = (String) tipoCombo.getSelectedItem();
            utenteCombo.removeAllItems();
            staff.stream()
                .filter(u -> tipoSel.equalsIgnoreCase(u.ruolo))
                .forEach(utenteCombo::addItem);
        });

        // Combo recinto
        var recinti = getController().getRecintiDisponibili();
        JComboBox<String> recintoCombo = new JComboBox<>();
        recinti.forEach(r -> recintoCombo.addItem(r.tipologia + " (ID: " + r.id + ")"));
        recintoCombo.addItem("Altro");

        JTextField recintoAltro = new JTextField(20);
        recintoAltro.setVisible(false);
        recintoAltro.setMaximumSize(new Dimension(300, 30));

        recintoCombo.addActionListener(e -> {
            boolean isAltro = "Altro".equals(recintoCombo.getSelectedItem());
            recintoAltro.setVisible(isAltro);
            form.revalidate();
            form.repaint();
        });

        addLabeledField(form, "Tipo:", tipoCombo);
        addLabeledField(form, "Mansione:", descrizioneCombo);
        addLabeledField(form, "Assegna a:", utenteCombo);
        addLabeledField(form, "Recinto:", recintoCombo);
        form.add(recintoAltro);
        form.add(Box.createVerticalStrut(20));

        form.add(createButton("Crea e Assegna", () -> {
            Utente selezionato = (Utente) utenteCombo.getSelectedItem();
            if (selezionato == null) {
                genericError("Seleziona un utente.");
                return;
            }
            String tipo = (String) tipoCombo.getSelectedItem();
            String descrizione = (String) descrizioneCombo.getSelectedItem();
            String recintoSel = "Altro".equals(recintoCombo.getSelectedItem())
                ? recintoAltro.getText().trim()
                : (String) recintoCombo.getSelectedItem();
            if (recintoSel == null || recintoSel.isEmpty()) {
                genericError("Specifica il recinto.");
                return;
            }
            getController().adminCreatedMansione(descrizione, tipo, selezionato.id, recintoSel);
        }));

        form.add(Box.createVerticalStrut(10));
        form.add(createButton("← Annulla", () -> getController().userClickedBack()));

        panel.add(form, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        showPage("nuovaMansione", outerPanel);
    }
    
    public void nuovaSpecieForm() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Crea Nuova Specie");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        JTextField nome = new JTextField(30);
        addLabeledField(panel, "Nome Specie:", nome);
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("✓ Crea Specie", () -> {
            try {
                if (nome.getText().isEmpty()) {
                    genericError("Il nome della specie è obbligatorio.");
                    return;
                }
                getController().adminCreatedSpecie(nome.getText());
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
        
        showPage("nuovaSpecie", outerPanel);
    }
    public void nuovoPersonaleForm() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);
        JPanel panel = createCenteredPanel();
        
        JLabel titleLabel = createTitleLabel("Crea Nuovo Personale");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        JTextField nome = new JTextField(30);
        JTextField cognome = new JTextField(30);
        JTextField email = new JTextField(30);
        JPasswordField password = new JPasswordField(30);
        JComboBox<String> ruoloCombo = new JComboBox<>(new String[]{"volontario", "veterinario"});
        
        addLabeledField(panel, "Nome:", nome);
        addLabeledField(panel, "Cognome:", cognome);
        addLabeledField(panel, "Email:", email);
        addLabeledField(panel, "Password:", password);
        addLabeledField(panel, "Ruolo:", ruoloCombo);
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(createButton("✓ Crea Personale", () -> {
            try {
                String ruolo = (String) ruoloCombo.getSelectedItem();
                getController().adminCreatedPersonale(
                    nome.getText(),
                    cognome.getText(),
                    email.getText(),
                    new String(password.getPassword()),
                    ruolo
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
        
        showPage("nuovoPersonale", outerPanel);
    }
    
    // ============ DETTAGLIO ANIMALE ============
    
    public void dettaglioAnimale(Animale a, Utente u) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 80, 30, 80));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = createTitleLabel("Dettaglio Animale: " + a.nome);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        // ---- INFO BOX ----
        JPanel infoBox = new JPanel(new GridLayout(0, 2, 10, 10));
        infoBox.setBackground(Color.WHITE);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        java.util.function.BiConsumer<String, String> addRow = (label, value) -> {
            JLabel l = new JLabel(label);
            l.setFont(new Font("Segoe UI", Font.BOLD, 13));
            l.setForeground(new Color(100, 120, 140));
            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            v.setForeground(TEXT_COLOR);
            infoBox.add(l);
            infoBox.add(v);
        };

        addRow.accept("Nome", a.nome);
        addRow.accept("Specie", a.nomeSpecie);
        addRow.accept("Età", a.eta + " anni");
        addRow.accept("Provenienza", a.provenienza);
        addRow.accept("Stato di salute", a.statoDiSalute);
        addRow.accept("Sesso", a.sesso.equals("M") ? "Maschio" : "Femmina");

        if (a.idRecinto.isPresent()) {
            var recinto = getController().getRecintoAnimale(a.idRecinto.get());
            String recintoInfo = recinto.isPresent()
                ? recinto.get().tipologia + " (ID: " + recinto.get().id + ")"
                : "ID " + a.idRecinto.get();
            addRow.accept("Recinto", recintoInfo);
        } else {
            addRow.accept("Recinto", "non assegnato");
        }

        addRow.accept("Data arrivo", a.dataArrivo.toString());
        addRow.accept("Descrizione", a.descrizione == null || a.descrizione.isEmpty() ? "—" : a.descrizione);

        panel.add(infoBox);
        panel.add(Box.createVerticalStrut(25));

        // ---- SEZIONE VETERINARIO ----
        if (u.isVeterinario()) {
            JPanel sectionVet = new JPanel();
            sectionVet.setLayout(new BoxLayout(sectionVet, BoxLayout.Y_AXIS));
            sectionVet.setBackground(Color.WHITE);
            sectionVet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 25, 20, 25)
            ));

            JLabel checkupLabel = createSubtitleLabel("Registra Controllo Sanitario");
            sectionVet.add(checkupLabel);
            sectionVet.add(Box.createVerticalStrut(15));

            JComboBox<String> tipo = new JComboBox<>(new String[]{
                "visita di routine", "esami delle feci", "valutazione respiratoria",
                "valutazione cardiaca", "valutazione post operatoria",
                "monitoraggio terapia", "richiamo vaccinale", "altro"
            });
            JComboBox<String> esito = new JComboBox<>(new String[]{"positivo", "negativo", "da monitorare"});

            JPanel tipoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            tipoRow.setBackground(Color.WHITE);
            JLabel tipoLabel = new JLabel("Tipologia:  ");
            tipoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            tipoRow.add(tipoLabel);
            tipoRow.add(tipo);
            sectionVet.add(tipoRow);

            JPanel esitoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            esitoRow.setBackground(Color.WHITE);
            JLabel esitoLabel = new JLabel("Esito:         ");
            esitoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            esitoRow.add(esitoLabel);
            esitoRow.add(esito);
            sectionVet.add(esitoRow);

            sectionVet.add(Box.createVerticalStrut(15));

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnRow.setBackground(Color.WHITE);
            btnRow.add(createButton("Registra controllo", () ->
                getController().userSubmittedNuovoControllo(a.id,
                    (String) tipo.getSelectedItem(),
                    (String) esito.getSelectedItem())
            ));
            btnRow.add(createButton("Storico controlli", () ->
                getController().userClickedStoricoControlli(a.id)));
            sectionVet.add(btnRow);

            panel.add(sectionVet);
            panel.add(Box.createVerticalStrut(25));
        }

        // ---- SEZIONE AGGIORNA STATO ----
        if (u.isVolontario() || u.isVeterinario()) {
            JPanel sectionStato = new JPanel();
            sectionStato.setLayout(new BoxLayout(sectionStato, BoxLayout.Y_AXIS));
            sectionStato.setBackground(Color.WHITE);
            sectionStato.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 25, 20, 25)
            ));

            sectionStato.add(createSubtitleLabel("Aggiorna Stato di Salute"));
            sectionStato.add(Box.createVerticalStrut(15));

            JPanel statoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            statoRow.setBackground(Color.WHITE);
            JLabel statoLabel = new JLabel("Nuovo Stato:  ");
            statoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            JComboBox<String> nuovoStato = new JComboBox<>(new String[]{"buono", "discreto", "critico"});
            nuovoStato.setSelectedItem(a.statoDiSalute);
            statoRow.add(statoLabel);
            statoRow.add(nuovoStato);
            sectionStato.add(statoRow);

            sectionStato.add(Box.createVerticalStrut(15));
            JPanel btnStato = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            btnStato.setBackground(Color.WHITE);
            btnStato.add(createButton("Salva stato", () ->
                getController().userSubmittedAggiornaStato(a.id, (String) nuovoStato.getSelectedItem())
            ));
            sectionStato.add(btnStato);

            panel.add(sectionStato);
            panel.add(Box.createVerticalStrut(25));
        }

        // ---- SEZIONE SPOSTA RECINTO ----
        if (u.isVolontario() || u.isVeterinario()) {
            JPanel sectionRecinto = new JPanel();
            sectionRecinto.setLayout(new BoxLayout(sectionRecinto, BoxLayout.Y_AXIS));
            sectionRecinto.setBackground(Color.WHITE);
            sectionRecinto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 25, 20, 25)
            ));

            sectionRecinto.add(createSubtitleLabel("Sposta in Recinto"));
            sectionRecinto.add(Box.createVerticalStrut(15));

            var recinti = getController().getRecintiDisponibili();
            if (recinti.isEmpty()) {
                sectionRecinto.add(createInfoLabel("Nessun recinto disponibile."));
            } else {
                JPanel recintoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
                recintoRow.setBackground(Color.WHITE);
                JLabel recintoLabel = new JLabel("Destinazione:  ");
                recintoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                JComboBox<Recinto> recintoCombo = new JComboBox<>(recinti.toArray(new Recinto[0]));
                recintoRow.add(recintoLabel);
                recintoRow.add(recintoCombo);
                sectionRecinto.add(recintoRow);
                sectionRecinto.add(Box.createVerticalStrut(15));

                JPanel btnRecinto = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                btnRecinto.setBackground(Color.WHITE);
                btnRecinto.add(createButton("Sposta", () -> {
                    Recinto selezionato = (Recinto) recintoCombo.getSelectedItem();
                    if (selezionato != null) {
                        getController().userSubmittedMovimentazione(a.id, selezionato.id);
                    }
                }));
                sectionRecinto.add(btnRecinto);
            }

            panel.add(sectionRecinto);
            panel.add(Box.createVerticalStrut(25));
        }

        panel.add(createButton("← Indietro", () -> getController().userClickedBack()));
        panel.add(Box.createVerticalGlue());

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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 25, 30, 25));

        // Titolo
        JLabel title = new JLabel("Elenco Specie");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Lista specie
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);

        for (Specie s : specie) {
            int num = conta.applyAsInt(s.id);

            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 20, 12, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            JLabel nomeLabel = new JLabel(s.nome);
            nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nomeLabel.setForeground(TEXT_COLOR);

            JLabel numLabel = new JLabel(num + " " + (num == 1 ? "animale" : "animali"));
            numLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            numLabel.setForeground(new Color(130, 140, 150));

            card.add(nomeLabel, BorderLayout.WEST);
            card.add(numLabel, BorderLayout.EAST);

            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(6));
        }

        // Bottone indietro
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(createButton("← Indietro", () -> getController().userClickedBack()));

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

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
        JTextField descr = new JTextField(20);
        JComboBox<Specie> combo = new JComboBox<>(specie.toArray(new Specie[0]));
        var recinti = getController().getRecintiDisponibili();
        JComboBox<Recinto> recintoCombo = new JComboBox<>(recinti.toArray(new Recinto[0]));

        // Combo stato di salute
        JComboBox<String> statoCombo = new JComboBox<>(new String[]{"buono", "discreto", "critico"});
        JComboBox<String> sessoCombo = new JComboBox<>(new String[]{"M", "F"});

        // Combo provenienza con campo "altro"
        String[] provenienze = {"Zoo", "Abbandono", "Altro parco naturale", "Sequestro", "Selvatico", "Privato", "Altro"};
        JComboBox<String> provenienzaCombo = new JComboBox<>(provenienze);
        JTextField provenienzaAltro = new JTextField(20);
        provenienzaAltro.setVisible(false);
        provenienzaAltro.setMaximumSize(new java.awt.Dimension(300, 30));

        provenienzaCombo.addActionListener(e -> {
            boolean isAltro = "Altro".equals(provenienzaCombo.getSelectedItem());
            provenienzaAltro.setVisible(isAltro);
            panel.revalidate();
            panel.repaint();
        });

        addLabeledField(panel, "Nome:", nome);
        addLabeledField(panel, "Età:", eta);
        addLabeledField(panel, "Provenienza:", provenienzaCombo);
        addLabeledField(panel, "Sesso:", sessoCombo);
        panel.add(provenienzaAltro);
        panel.add(Box.createVerticalStrut(5));
        addLabeledField(panel, "Stato:", statoCombo);
        addLabeledField(panel, "Descrizione:", descr);
        addLabeledField(panel, "Specie:", combo);
        addLabeledField(panel, "Recinto:", recintoCombo);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createButton("Registra", () -> {
            try {
                String provenienzaFinale = "Altro".equals(provenienzaCombo.getSelectedItem())
                    ? provenienzaAltro.getText().trim()
                    : (String) provenienzaCombo.getSelectedItem();

                if (provenienzaFinale.isEmpty()) {
                    genericError("Inserisci la provenienza.");
                    return;
                }

                Recinto recintoSelezionato = (Recinto) recintoCombo.getSelectedItem();
                if (recintoSelezionato == null) {
                    genericError("Seleziona un recinto.");
                    return;
                }
                getController().userSubmittedNuovoAnimale(
                nome.getText(),
                Integer.parseInt(eta.getText()),
                provenienzaFinale,
                (String) statoCombo.getSelectedItem(),
                descr.getText(),
                ((Specie) combo.getSelectedItem()).id,
                recintoSelezionato.id,
                (String) sessoCombo.getSelectedItem()
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel title = new JLabel("Statistiche Centro");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);

        for (Map.Entry<String, Object> e : stats.entrySet()) {
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 20, 12, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            JLabel keyLabel = new JLabel(e.getKey());
            keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            keyLabel.setForeground(TEXT_COLOR);

            JLabel valLabel = new JLabel(e.getValue().toString());
            valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            valLabel.setForeground(new Color(130, 140, 150));

            card.add(keyLabel, BorderLayout.WEST);
            card.add(valLabel, BorderLayout.EAST);

            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(6));
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(createButton("← Indietro", () -> getController().userClickedBack()));

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        showPage("statistiche", outerPanel);
    }

    public void personalePage(List<Utente> personale) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel title = new JLabel("Personale");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);

        for (Utente u : personale) {
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 20, 12, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            JLabel nomeLabel = new JLabel(u.cognome + " " + u.nome + "  —  " + u.email);
            nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nomeLabel.setForeground(TEXT_COLOR);

            JLabel ruoloLabel = new JLabel(u.ruolo);
            ruoloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ruoloLabel.setForeground(new Color(130, 140, 150));

            card.add(nomeLabel, BorderLayout.WEST);
            card.add(ruoloLabel, BorderLayout.EAST);

            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(6));
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(createButton("← Indietro", () -> getController().userClickedBack()));

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        showPage("personale", outerPanel);
    }

    public void mansioniSempliciPage(List<String> mansioni) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel title = new JLabel("Mansioni");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);

        if (mansioni.isEmpty()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 20, 12, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            JLabel vuoto = new JLabel("Nessuna mansione presente.");
            vuoto.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vuoto.setForeground(new Color(130, 140, 150));
            card.add(vuoto, BorderLayout.WEST);
            listPanel.add(card);
        } else {
            for (String m : mansioni) {
                String[] parti = m.split("  →  ");
                JPanel card = new JPanel(new BorderLayout(15, 0));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(12, 20, 12, 20)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

                JLabel mansioneLabel = new JLabel(parti[0]);
                mansioneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                mansioneLabel.setForeground(TEXT_COLOR);

                JLabel utenteLabel = new JLabel(parti.length > 1 ? parti[1] : "");
                utenteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                utenteLabel.setForeground(new Color(130, 140, 150));

                card.add(mansioneLabel, BorderLayout.WEST);
                card.add(utenteLabel, BorderLayout.EAST);

                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(6));
            }
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(createButton("← Indietro", () -> getController().userClickedBack()));

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        showPage("mansioni", outerPanel);
    }

    public void turniSempliciPage(List<String> turni) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel title = new JLabel("Turni");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);

        if (turni.isEmpty()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 20, 12, 20)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            JLabel vuoto = new JLabel("Nessun turno presente.");
            vuoto.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vuoto.setForeground(new Color(130, 140, 150));
            card.add(vuoto, BorderLayout.WEST);
            listPanel.add(card);
        } else {
            for (String t : turni) {
                String[] parti = t.split("  →  ");
                JPanel card = new JPanel(new BorderLayout(15, 0));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(12, 20, 12, 20)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

                JLabel turnoLabel = new JLabel(parti[0]);
                turnoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                turnoLabel.setForeground(TEXT_COLOR);

                JLabel utenteLabel = new JLabel(parti.length > 1 ? parti[1] : "");
                utenteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                utenteLabel.setForeground(new Color(130, 140, 150));

                card.add(turnoLabel, BorderLayout.WEST);
                card.add(utenteLabel, BorderLayout.EAST);

                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(6));
            }
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(createButton("← Indietro", () -> getController().userClickedBack()));

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        showPage("turni", outerPanel);
    }
    
}