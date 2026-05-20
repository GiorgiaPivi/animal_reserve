package db_lab.controller;

import db_lab.data.*;
import db_lab.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale con menu interattivo per il sistema
 */

public class Main {
    
    private static Scanner scanner = new Scanner(System.in);
    private static UtenteDAO utenteDAO = new UtenteDAO();
    private static AnimaleDAO animaleDAO = new AnimaleDAO();
    private static ControlloSanitarioDAO controlloDAO = new ControlloSanitarioDAO();
    private static TerapiaDAO terapiaDAO = new TerapiaDAO();
    private static SpecieDAO specieDAO = new SpecieDAO();
    private static RecintoDAO recintoDAO = new RecintoDAO();
    
    private static Utente utenteCorrente = null;
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  SISTEMA GESTIONE CENTRO RECUPERO ANIMALI   ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");
        
        // Test connessione
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Impossibile connettersi al database!");
            System.err.println("Verifica che MySQL sia avviato e le credenziali siano corrette.");
            return;
        }
        
        boolean running = true;
        
        while (running) {
            if (utenteCorrente == null) {
                menuLogin();
            } else {
                menuPrincipale();
            }
        }
        
        DatabaseConnection.closeConnection();
        scanner.close();
    }
    
    // ========== MENU LOGIN ==========
    
    private static void menuLogin() {
        System.out.println("\n╔═══ MENU ACCESSO ═══╗");
        System.out.println("║ 1. Login           ║");
        System.out.println("║ 2. Registrazione   ║");
        System.out.println("║ 0. Esci            ║");
        System.out.println("╚════════════════════╝");
        System.out.print("Scelta: ");
        
        int scelta = leggiIntero();
        
        switch (scelta) {
            case 1: effettuaLogin(); break;
            case 2: registraVisitatore(); break;
            case 0: 
                System.out.println("\n👋 Arrivederci!");
                System.exit(0);
                break;
            default: System.out.println("❌ Scelta non valida!");
        }
    }
    
    private static void effettuaLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        utenteCorrente = utenteDAO.login(email, password);
        
        if (utenteCorrente != null) {
            System.out.println("\n✅ Benvenuto/a " + utenteCorrente.getNome() + " " + 
                             utenteCorrente.getCognome() + "!");
            System.out.println("Ruolo: " + utenteCorrente.getRuolo());
        }
    }
    
    private static void registraVisitatore() {
        System.out.println("\n--- REGISTRAZIONE NUOVO VISITATORE ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(nome);
        nuovoUtente.setCognome(cognome);
        nuovoUtente.setEmail(email);
        nuovoUtente.setPassword(password);
        nuovoUtente.setRuolo("visitatore");
        
        if (utenteDAO.registraUtente(nuovoUtente)) {
            System.out.println("\n✅ Registrazione completata! Ora puoi effettuare il login.");
        }
    }
    
    // ========== MENU PRINCIPALE ==========
    
    private static void menuPrincipale() {
        System.out.println("\n╔═══ MENU PRINCIPALE ═══╗");
        System.out.println("║ Utente: " + utenteCorrente.getNome() + " " + utenteCorrente.getCognome());
        System.out.println("║ Ruolo: " + utenteCorrente.getRuolo());
        System.out.println("╠═══════════════════════╣");
        System.out.println("║ 1. Visualizza animali ║");
        System.out.println("║ 2. Cerca animale      ║");
        System.out.println("║ 3. Filtra per stato   ║");
        System.out.println("║ 4. Visualizza specie  ║");
        
        if (utenteCorrente.isVeterinario()) {
            System.out.println("║                       ║");
            System.out.println("║ --- VETERINARIO ---  ║");
            System.out.println("║ 5. Nuovo controllo    ║");
            System.out.println("║ 6. Storico controlli  ║");
            System.out.println("║ 7. Prescrivere terapia║");
            System.out.println("║ 8. Animali da control.║");
        }
        
        if (utenteCorrente.isVolontario() || utenteCorrente.isVeterinario()) {
            System.out.println("║                       ║");
            System.out.println("║ --- STAFF ---         ║");
            System.out.println("║ 9. Registra animale   ║");
            System.out.println("║ 10. Aggiorna animale  ║");
        }
        
        System.out.println("║                       ║");
        System.out.println("║ 0. Logout             ║");
        System.out.println("╚═══════════════════════╝");
        System.out.print("Scelta: ");
        
        int scelta = leggiIntero();
        
        switch (scelta) {
            case 1: visualizzaTuttiAnimali(); break;
            case 2: cercaAnimale(); break;
            case 3: filtraPerStato(); break;
            case 4: visualizzaSpecie(); break;
            case 5: if (utenteCorrente.isVeterinario()) nuovoControllo(); break;
            case 6: if (utenteCorrente.isVeterinario()) storicoControlli(); break;
            case 7: if (utenteCorrente.isVeterinario()) prescriviTerapia(); break;
            case 8: if (utenteCorrente.isVeterinario()) animaliDaControllare(); break;
            case 9: if (utenteCorrente.isVolontario() || utenteCorrente.isVeterinario()) registraAnimale(); break;
            case 10: if (utenteCorrente.isVolontario() || utenteCorrente.isVeterinario()) aggiornaAnimale(); break;
            case 0:
                utenteCorrente = null;
                System.out.println("👋 Logout effettuato.");
                break;
            default: System.out.println("❌ Scelta non valida!");
        }
    }
    
    // ========== OPERAZIONI ANIMALI ==========
    
    private static void visualizzaTuttiAnimali() {
        System.out.println("\n═══ LISTA COMPLETA ANIMALI ═══");
        List<Animale> animali = animaleDAO.getAllAnimali();
        
        if (animali.isEmpty()) {
            System.out.println("Nessun animale presente.");
        } else {
            for (Animale a : animali) {
                stampaAnimale(a);
            }
        }
    }
    
    private static void cercaAnimale() {
        System.out.print("\nInserisci nome (o parte): ");
        String nome = scanner.nextLine();
        
        List<Animale> animali = animaleDAO.searchByNome(nome);
        
        if (animali.isEmpty()) {
            System.out.println("❌ Nessun animale trovato.");
        } else {
            System.out.println("\n✅ Animali trovati:");
            for (Animale a : animali) {
                stampaAnimale(a);
            }
        }
    }
    
    private static void filtraPerStato() {
        System.out.println("\nSeleziona stato:");
        System.out.println("1. Buono");
        System.out.println("2. Discreto");
        System.out.println("3. Critico");
        System.out.print("Scelta: ");
        
        int scelta = leggiIntero();
        String stato = "";
        
        switch (scelta) {
            case 1: stato = "buono"; break;
            case 2: stato = "discreto"; break;
            case 3: stato = "critico"; break;
            default:
                System.out.println("❌ Scelta non valida!");
                return;
        }
        
        List<Animale> animali = animaleDAO.getByStatoSalute(stato);
        
        if (animali.isEmpty()) {
            System.out.println("Nessun animale con stato " + stato + ".");
        } else {
            System.out.println("\n═══ Animali con stato '" + stato + "' ═══");
            for (Animale a : animali) {
                stampaAnimale(a);
            }
        }
    }
    
    private static void visualizzaSpecie() {
        System.out.println("\n═══ ELENCO SPECIE ═══");
        List<Specie> specie = specieDAO.getAllSpecie();
        
        for (Specie s : specie) {
            int count = specieDAO.contaAnimaliPerSpecie(s.getIdSpecie());
            System.out.println("• " + s.getNomeSpecie() + " (" + count + " animali)");
        }
    }
    
    private static void registraAnimale() {
        System.out.println("\n--- REGISTRAZIONE NUOVO ANIMALE ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Età: ");
        int eta = leggiIntero();
        scanner.nextLine(); // Consuma newline
        System.out.print("Provenienza: ");
        String provenienza = scanner.nextLine();
        System.out.print("Stato (buono/discreto/critico): ");
        String stato = scanner.nextLine();
        System.out.print("Descrizione: ");
        String descrizione = scanner.nextLine();
        
        // Mostra specie disponibili
        List<Specie> specie = specieDAO.getAllSpecie();
        System.out.println("\nSpecie disponibili:");
        for (int i = 0; i < specie.size(); i++) {
            System.out.println((i+1) + ". " + specie.get(i).getNomeSpecie());
        }
        System.out.print("Seleziona specie: ");
        int sceltaSpecie = leggiIntero() - 1;
        
        if (sceltaSpecie < 0 || sceltaSpecie >= specie.size()) {
            System.out.println("❌ Specie non valida!");
            return;
        }
        
        Animale animale = new Animale();
        animale.setNomeAnimale(nome);
        animale.setEta(eta);
        animale.setProvenienza(provenienza);
        animale.setStatoDiSalute(stato);
        animale.setDescrizione(descrizione);
        animale.setDataArrivo(LocalDate.now());
        animale.setIdSpecie(specie.get(sceltaSpecie).getIdSpecie());
        animale.setIdRecinto(null); // Verrà assegnato successivamente
        
        if (animaleDAO.insertAnimale(animale)) {
            System.out.println("\n✅ Animale registrato con successo! ID: " + animale.getIdAnimale());
        }
    }
    
    private static void aggiornaAnimale() {
        System.out.print("\nID animale da aggiornare: ");
        int id = leggiIntero();
        scanner.nextLine();
        
        Animale animale = animaleDAO.getById(id);
        if (animale == null) {
            System.out.println("❌ Animale non trovato!");
            return;
        }
        
        stampaAnimale(animale);
        
        System.out.print("\nNuovo stato (buono/discreto/critico) [Enter per mantenere]: ");
        String nuovoStato = scanner.nextLine();
        if (!nuovoStato.isEmpty()) {
            animale.setStatoDiSalute(nuovoStato);
        }
        
        if (animaleDAO.updateAnimale(animale)) {
            System.out.println("✅ Animale aggiornato!");
        }
    }
    
    // ========== OPERAZIONI VETERINARIO ==========
    
    private static void nuovoControllo() {
        System.out.print("\nID animale: ");
        int idAnimale = leggiIntero();
        scanner.nextLine();
        
        System.out.println("Tipologia controllo:");
        System.out.println("1. Visita di routine");
        System.out.println("2. Esami delle feci");
        System.out.println("3. Valutazione respiratoria");
        System.out.println("4. Valutazione cardiaca");
        System.out.print("Scelta: ");
        int sceltaTipo = leggiIntero();
        scanner.nextLine();
        
        String[] tipologie = {"visita di routine", "esami delle feci", 
                             "valutazione respiratoria", "valutazione cardiaca"};
        String tipologia = tipologie[sceltaTipo - 1];
        
        System.out.print("Esito (positivo/negativo/da monitorare): ");
        String esito = scanner.nextLine();
        
        ControlloSanitario controllo = new ControlloSanitario();
        controllo.setData(LocalDate.now());
        controllo.setOra(LocalTime.now());
        controllo.setTipologia(tipologia);
        controllo.setEsito(esito);
        controllo.setIdAnimale(idAnimale);
        controllo.setIdVeterinario(utenteCorrente.getIdUtente());
        
        if (controlloDAO.insertControllo(controllo)) {
            System.out.println("✅ Controllo registrato! ID: " + controllo.getIdControllo());
        }
    }
    
    private static void storicoControlli() {
        System.out.print("\nID animale: ");
        int idAnimale = leggiIntero();
        
        List<ControlloSanitario> controlli = controlloDAO.getStoricoByAnimale(idAnimale);
        
        if (controlli.isEmpty()) {
            System.out.println("Nessun controllo presente.");
        } else {
            System.out.println("\n═══ STORICO CONTROLLI ═══");
            for (ControlloSanitario c : controlli) {
                System.out.println("\n• Data: " + c.getData() + " " + c.getOra());
                System.out.println("  Tipologia: " + c.getTipologia());
                System.out.println("  Esito: " + c.getEsito());
            }
        }
    }
    
    private static void prescriviTerapia() {
        System.out.print("\nID controllo sanitario: ");
        int idControllo = leggiIntero();
        scanner.nextLine();
        
        System.out.print("Farmaco: ");
        String farmaco = scanner.nextLine();
        System.out.print("Dosaggio: ");
        String dosaggio = scanner.nextLine();
        System.out.print("Durata in giorni: ");
        int giorni = leggiIntero();
        
        Terapia terapia = new Terapia();
        terapia.setFarmaco(farmaco);
        terapia.setDosaggio(dosaggio);
        terapia.setDurata(giorni + " giorni");
        terapia.setDataInizio(LocalDate.now());
        terapia.setDataFine(LocalDate.now().plusDays(giorni));
        terapia.setIdControllo(idControllo);
        
        if (terapiaDAO.insertTerapia(terapia)) {
            System.out.println("✅ Terapia prescritta!");
        }
    }
    
    private static void animaliDaControllare() {
        System.out.println("\n═══ ANIMALI CHE NECESSITANO CONTROLLO ═══");
        List<Animale> animali = animaleDAO.getAnimaliDaControllare();
        
        if (animali.isEmpty()) {
            System.out.println("✅ Tutti gli animali sono aggiornati!");
        } else {
            for (Animale a : animali) {
                stampaAnimale(a);
            }
        }
    }
    
    // ========== UTILITY ==========
    
    private static void stampaAnimale(Animale a) {
        System.out.println("\n┌────────────────────────────");
        System.out.println("│ ID: " + a.getIdAnimale());
        System.out.println("│ Nome: " + a.getNomeAnimale());
        System.out.println("│ Età: " + a.getEta() + " anni");
        System.out.println("│ Stato: " + a.getStatoDiSalute());
        System.out.println("│ Provenienza: " + a.getProvenienza());
        System.out.println("│ Arrivo: " + a.getDataArrivo());
        System.out.println("│ Descrizione: " + a.getDescrizione());
        System.out.println("└────────────────────────────");
    }
    
    private static int leggiIntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
