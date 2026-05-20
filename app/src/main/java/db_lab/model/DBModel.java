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
}
