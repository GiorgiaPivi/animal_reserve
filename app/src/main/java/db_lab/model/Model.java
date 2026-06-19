package db_lab.model;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface Model {

    //Utente

    Optional<Utente> login(String email, String password);

    Optional<Utente> registra(String nome, String cognome, String email, String password);

    Optional<Utente> registraConRuolo(String nome, String cognome, String email, String password, String ruolo);

    List<Utente> listPersonale();

    List<Utente> listStaff();

    //Specie

    List<Specie> specie();

    int contaAnimaliPerSpecie(int idSpecie);

    //Animale

    List<Animale> animali();

    List<Animale> loadAnimali();

    boolean loadedAnimali();

    List<Animale> searchByNome(String nome);

    List<Animale> filterByStato(String stato);

    Optional<Animale> findAnimale(int id);

    int insertAnimale(String nome, int eta, String provenienza, String statoDiSalute,
                      String descrizione, LocalDate dataArrivo, int idSpecie,
                      Optional<Integer> idRecinto, String sesso);

    void updateStatoAnimale(int id, String nuovoStato);

    List<Animale> animaliDaControllare();

    //Controllo Sanitario

    int insertControllo(LocalDate data, LocalTime ora, String tipologia,
                        String esito, int idAnimale, int idVeterinario);

    List<ControlloSanitario> storicoControlli(int idAnimale);

    //Terapia

    int insertTerapia(String farmaco, String dosaggio, String durata,
                      LocalDate dataInizio, LocalDate dataFine, int idControllo);

    List<Terapia> terapieByAnimale(int idAnimale);

    List<Terapia> terapieByControllo(int idControllo);

    //Recinto

    List<Recinto> recinti();

    List<Recinto> recintiDisponibili();

    Optional<Recinto> findRecinto(int idRecinto);

    int contaAnimaliInRecinto(int idRecinto);

    void updateRecintoAnimale(int idAnimale, int idRecinto);

    //Movimentazione

    int insertMovimentazione(LocalDate dataMovimentazione, int idAnimale, int idRecintoDestinazione);

    List<Movimentazione> movimentazioniByAnimale(int idAnimale);

    //Trasporto Esterno

    int insertTrasporto(LocalDate dataTrasporto, String destinazione, String motivazione,
                        int idAnimale, int idVolontario);

    List<TrasportoEsterno> trasportiByAnimale(int idAnimale);

    List<TrasportoEsterno> allTrasporti();

    //Statistiche

    int contaAnimaliTotali();

    java.util.Map<String, Integer> contaAnimaliPerStato();

    int contaControlliUltimi30Giorni();

    java.util.Map<String, Object> statisticheGenerali();

    java.util.Map<String, Object> statisticheSanitarie();

    //Operazioni Admin

    void insertSpecie(String nome);

    void insertRecinto(String tipologia);

    void insertTurno(LocalDate data, String fascia);

    void assegnaTurno(int idUtente, LocalDate data, String fascia);

    void insertMansione(String descrizione, String tipoMansione);

    static Model fromConnection(Connection connection) {
        return new DBModel(connection);
    }

    static Model mock() {
        return new MockedModel();
    }

    //Turni 
    List<Turno> turni();

    List<Turno> turniByUtente(int idUtente);

    List<String> turniConAssegnati();


    //Mansioni
    List<Mansione> mansioni();

    List<Mansione> mansioniByUtente(int idUtente);

    List<String> mansioniConAssegnati();

    void affidaMansione(int idUtente, int idMansione, String recinto);

}