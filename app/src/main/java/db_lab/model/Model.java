package db_lab.model;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface Model {

    // ------- Utente -------

    Optional<Utente> login(String email, String password);

    Optional<Utente> registra(String nome, String cognome, String email, String password);

    // ------- Specie -------

    List<Specie> specie();

    int contaAnimaliPerSpecie(int idSpecie);

    // ------- Animale -------

    List<Animale> animali();

    List<Animale> loadAnimali();

    boolean loadedAnimali();

    List<Animale> searchByNome(String nome);

    List<Animale> filterByStato(String stato);

    Optional<Animale> findAnimale(int id);

    int insertAnimale(String nome, int eta, String provenienza, String statoDiSalute,
                      String descrizione, LocalDate dataArrivo, int idSpecie,
                      Optional<Integer> idRecinto);

    void updateStatoAnimale(int id, String nuovoStato);

    List<Animale> animaliDaControllare();

    // ------- Controllo Sanitario -------

    int insertControllo(LocalDate data, LocalTime ora, String tipologia,
                        String esito, int idAnimale, int idVeterinario);

    List<ControlloSanitario> storicoControlli(int idAnimale);

    // ------- Terapia -------

    int insertTerapia(String farmaco, String dosaggio, String durata,
                      LocalDate dataInizio, LocalDate dataFine, int idControllo);

    // ------- Factory -------

    static Model fromConnection(Connection connection) {
        return new DBModel(connection);
    }

    static Model mock() {
        return new MockedModel();
    }
}
