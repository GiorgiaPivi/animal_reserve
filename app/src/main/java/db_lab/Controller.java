package db_lab;

import db_lab.data.DAOException;
import db_lab.model.Animale;
import db_lab.model.ControlloSanitario;
import db_lab.model.Model;
import db_lab.model.Utente;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Il Controller descrive tutte le possibili interazioni dell'utente con l'applicazione.
// Ogni metodo pubblico segue il pattern: soggetto + azione + oggetto.
// Leggendo i metodi si capisce immediatamente tutto quello che può succedere nell'app.
//
//    ┌────── aggiorna ──────┐
//    │                      │
// ┌──▼─┐                 ┌──┴───────┐ aggiorna ┌───────┐
// │view│                 │controller├──────────►model  │
// └──┬─┘                 └──▲───────┘          └───────┘
//    │      notifica         │
//    └──── azioni utente ────┘
//
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
        try {
            var utente = this.model.login(email, password);
            if (utente.isPresent()) {
                this.utenteCorrente = utente;
                this.loadAnimaliPage();
            } else {
                this.view.loginFailed("Credenziali non valide.");
            }
        } catch (DAOException e) {
            this.view.loginFailed("Errore di connessione al database.");
        }
    }

    public void userSubmittedRegistrazione(String nome, String cognome, String email, String password) {
        try {
            var utente = this.model.registra(nome, cognome, email, password);
            if (utente.isPresent()) {
                this.view.registrazioneOk();
            } else {
                this.view.registrazioneFailed("Registrazione fallita.");
            }
        } catch (DAOException e) {
            this.view.registrazioneFailed("Email già registrata o errore database.");
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
        try {
            var animali = this.model.searchByNome(nome);
            this.view.animaliPage(animali, this.utenteCorrente.orElseThrow());
        } catch (DAOException e) {
            this.view.genericError("Errore nella ricerca.");
        }
    }

    public void userSelectedFiltroStato(String stato) {
        try {
            var animali = this.model.filterByStato(stato);
            this.view.animaliPage(animali, this.utenteCorrente.orElseThrow());
        } catch (DAOException e) {
            this.view.genericError("Errore nel filtro.");
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
        var specie = this.model.specie();
        this.view.nuovoAnimaleForm(specie);
    }

    public void userSubmittedNuovoAnimale(String nome, int eta, String provenienza,
            String statoDiSalute, String descrizione, int idSpecie) {
        try {
            int id = this.model.insertAnimale(nome, eta, provenienza, statoDiSalute,
                descrizione, LocalDate.now(), idSpecie, Optional.empty());
            this.view.animaleRegistrato(id);
            this.loadAnimaliPage();
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione animale.");
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
        try {
            int id = this.model.insertControllo(LocalDate.now(), LocalTime.now(),
                tipologia, esito, idAnimale, this.utenteCorrente.orElseThrow().id);
            this.view.controlloRegistrato(id);
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione controllo.");
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
        try {
            LocalDate inizio = LocalDate.now();
            LocalDate fine = inizio.plusDays(giorni);
            int id = this.model.insertTerapia(farmaco, dosaggio, giorni + " giorni",
                inizio, fine, idControllo);
            this.view.terapiaRegistrata(id);
        } catch (DAOException e) {
            this.view.genericError("Errore nella registrazione terapia.");
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
        if (this.model.loadedAnimali()) {
            this.view.animaliPage(this.model.animali(), this.utenteCorrente.orElseThrow());
        } else {
            this.loadAnimaliPage();
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
}
