package db_lab;

import db_lab.data.DAOException;
import db_lab.model.Animale;
import db_lab.model.Model;
import db_lab.model.Utente;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public final class Controller {

    private final Model model;
    private final View view;
    private Optional<Utente> utenteCorrente;

    public Controller(Model model, View view) {
        Objects.requireNonNull(model, "Controller creato con model null");
        Objects.requireNonNull(view, "Controller creato con view null");
        this.model = model;
        this.view = view;
        this.utenteCorrente = Optional.empty();
    }

    // ------- Avvio -------

    public void userRequestedInitialPage() {
        this.view.loginPage();
    }

    // ------- Login / Registrazione -------

    public void userSubmittedLogin(String email, String password) {
        // Validazione input
        if (email == null || email.trim().isEmpty()) {
            this.view.loginFailed("Email obbligatoria.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            this.view.loginFailed("Password obbligatoria.");
            return;
        }
        
        try {
            var utente = this.model.login(email, password);
            if (utente.isPresent()) {
                this.utenteCorrente = utente;
                this.loadAnimaliPage();
            } else {
                this.view.loginFailed("Credenziali non valide.");
            }
        } catch (DAOException e) {
            this.view.loginFailed("Errore di connessione al database: " + e.getMessage());
        }
    }

    public void userSubmittedRegistrazione(String nome, String cognome, String email, String password) {
        // Validazione input
        if (nome == null || nome.trim().isEmpty()) {
            this.view.registrazioneFailed("Il nome è obbligatorio.");
            return;
        }
        if (cognome == null || cognome.trim().isEmpty()) {
            this.view.registrazioneFailed("Il cognome è obbligatorio.");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            this.view.registrazioneFailed("L'email è obbligatoria.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            this.view.registrazioneFailed("Formato email non valido.");
            return;
        }
        if (password == null || password.length() < 4) {
            this.view.registrazioneFailed("La password deve essere di almeno 4 caratteri.");
            return;
        }
        
        try {
            var utente = this.model.registra(nome, cognome, email, password);
            if (utente.isPresent()) {
                this.view.registrazioneOk();
            } else {
                this.view.registrazioneFailed("Registrazione fallita.");
            }
        } catch (DAOException e) {
            this.view.registrazioneFailed("Email già registrata o errore database: " + e.getMessage());
        }
    }
    public boolean isAdmin() {
        return this.utenteCorrente.isPresent() && 
            "admin".equalsIgnoreCase(this.utenteCorrente.get().ruolo);
    }

    public void userClickedAdminPanel() {
        try {
            this.view.adminPanelPage();
        } catch (Exception e) {
            this.view.genericError("Errore caricamento admin panel.");
        }
    }
    public void userClickedLogout() {
        this.utenteCorrente = Optional.empty();
        this.view.loginPage();
    }

    // ------- Lista Animali (pagina principale dopo login) -------

    public void userClickedReloadAnimali() {
        this.loadAnimaliPage();
    }

    public void userSubmittedSearch(String nome) {
        if (this.utenteCorrente.isEmpty()) {
            this.view.genericError("Errore: utente non autenticato.");
            return;
        }
        if (nome == null || nome.trim().isEmpty()) {
            this.view.genericError("Inserisci un nome per la ricerca.");
            return;
        }
        
        try {
            var animali = this.model.searchByNome(nome);
            this.view.animaliPage(animali, this.utenteCorrente.get());
        } catch (DAOException e) {
            this.view.genericError("Errore nella ricerca: " + e.getMessage());
        }
    }

    public void userSelectedFiltroStato(String stato) {
        if (this.utenteCorrente.isEmpty()) {
            this.view.genericError("Errore: utente non autenticato.");
            return;
        }
        if (stato == null || stato.trim().isEmpty()) {
            // Se stato vuoto, carica tutti gli animali
            this.loadAnimaliPage();
            return;
        }
        
        try {
            var animali = this.model.filterByStato(stato);
            this.view.animaliPage(animali, this.utenteCorrente.get());
        } catch (DAOException e) {
            this.view.genericError("Errore nel filtro: " + e.getMessage());
        }
    }

    public void userClickedAnimale(Animale animale) {
        try {
            var dettaglio = this.model.findAnimale(animale.id);
            if (dettaglio.isPresent()) {
                this.view.dettaglioAnimale(dettaglio.get(), this.utenteCorrente.orElseThrow());
            } else {
                this.view.genericError("Animale non trovato.");
            }
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento animale.");
        }
    }

    // ------- Specie -------

    public void userClickedSpecie() {
        try {
            var specie = this.model.specie();
            this.view.speciePage(specie, id -> this.model.contaAnimaliPerSpecie(id));
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento specie.");
        }
    }

    // ------- Operazioni Staff (volontario/veterinario) -------

    public void userRequestedNuovoAnimale() {
        try {
            var specie = this.model.specie();
            this.view.nuovoAnimaleForm(specie);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento delle specie.");
        }
    }

    public void userSubmittedNuovoAnimale(String nome, int eta, String provenienza,
            String statoDiSalute, String descrizione, int idSpecie) {
        // Validazione input
        if (nome == null || nome.trim().isEmpty()) {
            this.view.genericError("Il nome dell'animale è obbligatorio.");
            return;
        }
        if (eta < 0) {
            this.view.genericError("L'età non può essere negativa.");
            return;
        }
        if (statoDiSalute == null || statoDiSalute.trim().isEmpty()) {
            this.view.genericError("Lo stato di salute è obbligatorio.");
            return;
        }
        
        try {
            int id = this.model.insertAnimale(nome, eta, provenienza, statoDiSalute,
                descrizione, LocalDate.now(), idSpecie, Optional.empty());
            this.view.animaleRegistrato(id);
            this.loadAnimaliPage();
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione animale: " + e.getMessage());
        }
    }

    public void userSubmittedAggiornaStato(int idAnimale, String nuovoStato) {
        try {
            this.model.updateStatoAnimale(idAnimale, nuovoStato);
            this.view.statoAggiornato();
            this.loadAnimaliPage();
        } catch (DAOException e) {
            this.view.genericError("Errore nell'aggiornamento stato.");
        }
    }

    // ------- Operazioni Veterinario -------

    public void userSubmittedNuovoControllo(int idAnimale, String tipologia,
            String esito) {
        // Validazione input
        if (tipologia == null || tipologia.trim().isEmpty()) {
            this.view.genericError("La tipologia del controllo è obbligatoria.");
            return;
        }
        if (esito == null || esito.trim().isEmpty()) {
            this.view.genericError("L'esito del controllo è obbligatorio.");
            return;
        }
        if (this.utenteCorrente.isEmpty()) {
            this.view.genericError("Errore: utente non autenticato.");
            return;
        }
        
        try {
            int id = this.model.insertControllo(LocalDate.now(), LocalTime.now(),
                tipologia, esito, idAnimale, this.utenteCorrente.get().id);
            this.view.controlloRegistrato(id);
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione controllo: " + e.getMessage());
        }
    }

    public void userClickedStoricoControlli(int idAnimale) {
        try {
            var controlli = this.model.storicoControlli(idAnimale);
            this.view.storicoControlliPage(controlli);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento storico.");
        }
    }

    public void userSubmittedTerapia(String farmaco, String dosaggio, int giorni, int idControllo) {
        // Validazione input
        if (farmaco == null || farmaco.trim().isEmpty()) {
            this.view.genericError("Il nome del farmaco è obbligatorio.");
            return;
        }
        if (dosaggio == null || dosaggio.trim().isEmpty()) {
            this.view.genericError("Il dosaggio è obbligatorio.");
            return;
        }
        if (giorni <= 0) {
            this.view.genericError("La durata deve essere positiva.");
            return;
        }
        
        try {
            LocalDate inizio = LocalDate.now();
            LocalDate fine = inizio.plusDays(giorni);
            int id = this.model.insertTerapia(farmaco, dosaggio, giorni + " giorni",
                inizio, fine, idControllo);
            this.view.terapiaRegistrata(id);
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione terapia: " + e.getMessage());
        }
    }

    public void userClickedAnimaliDaControllare() {
        try {
            var animali = this.model.animaliDaControllare();
            this.view.animaliPage(animali, this.utenteCorrente.orElseThrow());
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento animali da controllare.");
        }
    }

    // ------- Back -------

    public void userClickedBack() {
        try {
            if (this.utenteCorrente.isEmpty()) {
                this.view.loginPage();
                return;
            }
            
            if (this.model.loadedAnimali()) {
                this.view.animaliPage(this.model.animali(), this.utenteCorrente.get());
            } else {
                this.loadAnimaliPage();
            }
        } catch (DAOException e) {
            this.view.genericError("Errore nel ritorno alla pagina precedente.");
        }
    }

    // ------- Private helpers -------

    private void loadAnimaliPage() {
        try {
            this.view.loadingAnimali();
            var animali = this.model.loadAnimali();
            this.view.animaliPage(animali, this.utenteCorrente.orElseThrow());
        } catch (DAOException e) {
            e.printStackTrace();
            this.view.genericError("Errore nel caricamento animali.");
        }
    }

    public void userClickedStatisticheGenerali() {
        try {
            var stats = this.model.statisticheGenerali();
            this.view.showStatistiche(stats);
        } catch (DAOException e) {
            this.view.genericError("Errore statistiche.");
        }
    }

    public void userClickedStatisticheSanitarie() {
        try {
            var stats = this.model.statisticheSanitarie();
            this.view.showStatistiche(stats);
        } catch (DAOException e) {
            this.view.genericError("Errore statistiche sanitarie.");
        }
    }

    public void adminCreatedSpecie(String nome) {
        try {
            this.model.insertSpecie(nome);
            this.view.genericMessage("Specie aggiunta.");
        } catch (DAOException e) {
            this.view.genericError("Errore inserimento specie.");
        }
    }

    public void adminCreatedRecinto(String tipologia) {
        try {
            this.model.insertRecinto(tipologia);
            this.view.genericMessage("Recinto creato.");
        } catch (DAOException e) {
            this.view.genericError("Errore creazione recinto.");
        }
    }

    public void adminCreatedTurno(LocalDate data, String fascia) {
        try {
            this.model.insertTurno(data, fascia);
            this.view.genericMessage("Turno creato.");
        } catch (DAOException e) {
            this.view.genericError("Errore creazione turno.");
        }
    }

    public void adminAssignedTurno(int idUtente,
                               LocalDate data,
                               String fascia) {
        try {
            this.model.assegnaTurno(idUtente, data, fascia);
            this.view.genericMessage("Turno assegnato.");
        } catch (DAOException e) {
            this.view.genericError("Errore assegnazione turno.");
        }
    }

    public void adminCreatedMansione(String descrizione) {
        try {
            this.model.insertMansione(descrizione);
            this.view.genericMessage("Mansione creata.");
        } catch (DAOException e) {
            this.view.genericError("Errore creazione mansione.");
        }
    }

    // ------- Recinti -------

    public void userClickedRecinti() {
        try {
            var recinti = this.model.recinti();
            this.view.recintPage(recinti);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento recinti.");
        }
    }

    public void userClickedRecintiDisponibili() {
        try {
            var recinti = this.model.recintiDisponibili();
            this.view.recintPage(recinti);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento recinti disponibili.");
        }
    }

    public void userClickedDettaglioRecinto(int idRecinto) {
        try {
            var recinto = this.model.findRecinto(idRecinto);
            if (recinto.isPresent()) {
                int numAnimali = this.model.contaAnimaliInRecinto(idRecinto);
                this.view.dettaglioRecinto(recinto.get(), numAnimali);
            } else {
                this.view.genericError("Recinto non trovato.");
            }
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento dettaglio recinto.");
        }
    }

    // ------- Movimentazioni -------

    public void userSubmittedMovimentazione(int idAnimale, int idRecintoDestinazione) {
        // Validazione
        if (idAnimale <= 0 || idRecintoDestinazione <= 0) {
            this.view.genericError("Dati movimentazione non validi.");
            return;
        }

        try {
            // Verifica che il recinto esista
            var recinto = this.model.findRecinto(idRecintoDestinazione);
            if (recinto.isEmpty()) {
                this.view.genericError("Recinto di destinazione non trovato.");
                return;
            }

            // Verifica capienza
            int occupazione = this.model.contaAnimaliInRecinto(idRecintoDestinazione);
            if (occupazione >= recinto.get().capienza) {
                this.view.genericError("Recinto pieno: non è possibile spostare l'animale.");
                return;
            }

            int id = this.model.insertMovimentazione(LocalDate.now(), idAnimale, idRecintoDestinazione);
            this.view.genericMessage("Movimentazione registrata con ID: " + id);
            this.loadAnimaliPage();
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione movimentazione: " + e.getMessage());
        }
    }

    public void userClickedStoricoMovimentazioni(int idAnimale) {
        try {
            var movimentazioni = this.model.movimentazioniByAnimale(idAnimale);
            this.view.movimentazioniPage(movimentazioni);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento storico movimentazioni.");
        }
    }

    // ------- Trasporti Esterni -------

    public void userSubmittedTrasporto(String destinazione, String motivazione, int idAnimale) {
        // Validazione
        if (destinazione == null || destinazione.trim().isEmpty()) {
            this.view.genericError("La destinazione è obbligatoria.");
            return;
        }
        if (motivazione == null || motivazione.trim().isEmpty()) {
            this.view.genericError("La motivazione è obbligatoria.");
            return;
        }
        if (this.utenteCorrente.isEmpty()) {
            this.view.genericError("Errore: utente non autenticato.");
            return;
        }

        try {
            int id = this.model.insertTrasporto(LocalDate.now(), destinazione, motivazione,
                idAnimale, this.utenteCorrente.get().id);
            this.view.genericMessage("Trasporto registrato con ID: " + id);
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione trasporto: " + e.getMessage());
        }
    }

    public void userClickedStoricoTrasporti(int idAnimale) {
        try {
            var trasporti = this.model.trasportiByAnimale(idAnimale);
            this.view.trasportiPage(trasporti);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento storico trasporti.");
        }
    }

    public void userClickedTuttiTrasporti() {
        try {
            var trasporti = this.model.allTrasporti();
            this.view.trasportiPage(trasporti);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento trasporti.");
        }
    }

    // ------- Terapie -------

    public void userClickedTerapieAnimale(int idAnimale) {
        try {
            var terapie = this.model.terapieByAnimale(idAnimale);
            this.view.terapiePage(terapie);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento terapie.");
        }
    }

    public void userClickedTerapieControllo(int idControllo) {
        try {
            var terapie = this.model.terapieByControllo(idControllo);
            this.view.terapiePage(terapie);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento terapie del controllo.");
        }
    }

    // ------- Validazione permessi utente -------

    public boolean isVeterinario() {
        return this.utenteCorrente.isPresent() && 
               "veterinario".equalsIgnoreCase(this.utenteCorrente.get().ruolo);
    }

    public boolean isVolontario() {
        return this.utenteCorrente.isPresent() && 
               "volontario".equalsIgnoreCase(this.utenteCorrente.get().ruolo);
    }

    public boolean isVisitatore() {
        return this.utenteCorrente.isPresent() && 
               "visitatore".equalsIgnoreCase(this.utenteCorrente.get().ruolo);
    }

    public boolean isStaff() {
        return isVeterinario() || isVolontario();
    }

    public Optional<Utente> getUtenteCorrente() {
        return this.utenteCorrente;
    }

    public void userClickedTurni() {
        try {
            var turni = this.model.turniByUtente(this.utenteCorrente.get().id);
            this.view.turniPage(turni);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento turni.");
        }
    }

    public void userClickedMansioni() {
        try {
            var mansioni = this.model.mansioniByUtente(this.utenteCorrente.get().id);
            this.view.mansioniPage(mansioni);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento mansioni.");
        }
    }

    public void adminClickedAllTurni() {
        try {
            var turni = this.model.turni();
            this.view.turniPage(turni);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento turni.");
        }
    }

    public void adminClickedAllMansioni() {
        try {
            var mansioni = this.model.mansioni();
            this.view.mansioniPage(mansioni);
        } catch (DAOException e) {
            this.view.genericError("Errore nel caricamento mansioni.");
        }
    }
    public void userRequestedNuovoTurno() {
        this.view.nuovoTurnoForm();
    }

    public void userRequestedNuovaMansione() {
        this.view.nuovaMansioneForm();
    }
}