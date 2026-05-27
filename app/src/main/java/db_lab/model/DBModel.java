package db_lab.model;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Implementazione reale del Model: usa i DAO per caricare i dati dal database.
// Mantiene una cache della lista animali caricata più di recente.
//
public final class DBModel implements Model {

    private final Connection connection;
    private Optional<List<Animale>> animali;

    public DBModel(Connection connection) {
        Objects.requireNonNull(connection, "DBModel creato con connessione null");
        this.connection = connection;
        this.animali = Optional.empty();
    }

    // ------- Utente -------

    @Override
    public Optional<Utente> login(String email, String password) {
        return Utente.DAO.login(connection, email, password);
    }

    @Override
    public Optional<Utente> registra(String nome, String cognome, String email, String password) {
        return Utente.DAO.registra(connection, nome, cognome, email, password);
    }

    // ------- Specie -------

    @Override
    public List<Specie> specie() {
        return Specie.DAO.list(connection);
    }

    @Override
    public int contaAnimaliPerSpecie(int idSpecie) {
        return Specie.DAO.contaAnimali(connection, idSpecie);
    }

    // ------- Animale -------

    @Override
    public List<Animale> animali() {
        return this.animali.orElse(List.of());
    }

    @Override
    public List<Animale> loadAnimali() {
        var list = Animale.DAO.list(connection);
        this.animali = Optional.of(list);
        return list;
    }

    @Override
    public boolean loadedAnimali() {
        return this.animali.isPresent();
    }

    @Override
    public List<Animale> searchByNome(String nome) {
        return Animale.DAO.searchByNome(connection, nome);
    }

    @Override
    public List<Animale> filterByStato(String stato) {
        return Animale.DAO.filterByStato(connection, stato);
    }

    @Override
    public Optional<Animale> findAnimale(int id) {
        return Animale.DAO.find(connection, id);
    }

    @Override
    public int insertAnimale(String nome, int eta, String provenienza, String statoDiSalute,
                             String descrizione, LocalDate dataArrivo, int idSpecie,
                             Optional<Integer> idRecinto) {
        return Animale.DAO.insert(connection, nome, eta, provenienza, statoDiSalute,
            descrizione, dataArrivo, idSpecie, idRecinto);
    }

    @Override
    public void updateStatoAnimale(int id, String nuovoStato) {
        Animale.DAO.updateStato(connection, id, nuovoStato);
    }

    @Override
    public List<Animale> animaliDaControllare() {
        return Animale.DAO.daControllare(connection);
    }

    // ------- Controllo Sanitario -------

    @Override
    public int insertControllo(LocalDate data, LocalTime ora, String tipologia,
                               String esito, int idAnimale, int idVeterinario) {
        return ControlloSanitario.DAO.insert(connection, data, ora, tipologia,
            esito, idAnimale, idVeterinario);
    }

    @Override
    public List<ControlloSanitario> storicoControlli(int idAnimale) {
        return ControlloSanitario.DAO.storicoByAnimale(connection, idAnimale);
    }

    // ------- Terapia -------

    @Override
    public int insertTerapia(String farmaco, String dosaggio, String durata,
                             LocalDate dataInizio, LocalDate dataFine, int idControllo) {
        return Terapia.DAO.insert(connection, farmaco, dosaggio, durata,
            dataInizio, dataFine, idControllo);
    }

    @Override
    public List<Terapia> terapieByAnimale(int idAnimale) {
        return Terapia.DAO.listByAnimale(connection, idAnimale);
    }

    @Override
    public List<Terapia> terapieByControllo(int idControllo) {
        return Terapia.DAO.listByControllo(connection, idControllo);
    }

    // ------- Recinto -------

    @Override
    public List<Recinto> recinti() {
        return Recinto.DAO.list(connection);
    }

    @Override
    public List<Recinto> recintiDisponibili() {
        return Recinto.DAO.disponibili(connection);
    }

    @Override
    public Optional<Recinto> findRecinto(int idRecinto) {
        return Recinto.DAO.find(connection, idRecinto);
    }

    @Override
    public int contaAnimaliInRecinto(int idRecinto) {
        return Recinto.DAO.contaAnimali(connection, idRecinto);
    }

    // ------- Movimentazione -------

    @Override
    public int insertMovimentazione(LocalDate dataMovimentazione, int idAnimale, int idRecintoDestinazione) {
        return Movimentazione.DAO.insert(connection, dataMovimentazione, idAnimale, idRecintoDestinazione);
    }

    @Override
    public List<Movimentazione> movimentazioniByAnimale(int idAnimale) {
        return Movimentazione.DAO.listByAnimale(connection, idAnimale);
    }

    // ------- Trasporto Esterno -------

    @Override
    public int insertTrasporto(LocalDate dataTrasporto, String destinazione, String motivazione,
                                int idAnimale, int idVolontario) {
        return TrasportoEsterno.DAO.insert(connection, dataTrasporto, destinazione, motivazione,
            idAnimale, idVolontario);
    }

    @Override
    public List<TrasportoEsterno> trasportiByAnimale(int idAnimale) {
        return TrasportoEsterno.DAO.listByAnimale(connection, idAnimale);
    }

    @Override
    public List<TrasportoEsterno> allTrasporti() {
        return TrasportoEsterno.DAO.listAll(connection);
    }

    // ------- Statistiche -------

    @Override
    public int contaAnimaliTotali() {
        return Animale.DAO.contaTotali(connection);
    }

    @Override
    public java.util.Map<String, Integer> contaAnimaliPerStato() {
        return Animale.DAO.contaPerStato(connection);
    }

    @Override
    public int contaControlliUltimi30Giorni() {
        return ControlloSanitario.DAO.contaUltimi30Giorni(connection);
    }

    @Override
    public java.util.Map<String, Object> statisticheGenerali() {
        var stats = new java.util.HashMap<String, Object>();

        stats.put("Totale animali", this.contaAnimaliTotali());
        stats.put("Animali per stato", this.contaAnimaliPerStato());
        stats.put("Controlli ultimi 30 giorni", this.contaControlliUltimi30Giorni());

        return stats;
    }

    @Override
    public java.util.Map<String, Object> statisticheSanitarie() {
        var stats = new java.util.HashMap<String, Object>();

        stats.put("Controlli ultimi 30 giorni", this.contaControlliUltimi30Giorni());
        stats.put("Animali per stato di salute", this.contaAnimaliPerStato());

        return stats;
    }
    @Override
    public void insertSpecie(String nome) {
        Specie.DAO.insert(connection, nome);
    }

    @Override
    public void insertRecinto(String tipologia) {
        Recinto.DAO.insert(connection, tipologia);
    }

    @Override
    public void insertTurno(LocalDate data, String fascia) {
        Turno.DAO.insert(connection, data, fascia);
    }

    @Override
    public void assegnaTurno(int idUtente, LocalDate data, String fascia) {
        Turno.DAO.assegna(connection, idUtente, data, fascia);
    }

    @Override
    public void insertMansione(String descrizione) {
        Mansione.DAO.insert(connection, descrizione);
    }

    @Override
    public List<Turno> turni() {
        return Turno.DAO.list(connection);
    }

    @Override
    public List<Mansione> mansioni() {
        return Mansione.DAO.list(connection);
    }

    @Override
    public List<Turno> turniByUtente(int idUtente) {
        return Turno.DAO.listByUtente(connection,idUtente);
    }

    @Override
    public List<Mansione> mansioniByUtente(int idUtente) {
        return Mansione.DAO.listByUtente(connection,idUtente);
    }
}