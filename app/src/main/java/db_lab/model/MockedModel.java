package db_lab.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implementazione in-memory usata per testare l'applicazione senza database.
// Comoda per verificare l'aspetto visivo e il flusso senza configurare MySQL.
//
public final class MockedModel implements Model {

    private final List<Utente> utenti;
    private final List<Specie> specie;
    private final List<Animale> animali;
    private final List<ControlloSanitario> controlli;
    private final List<Terapia> terapie;
    private int nextId = 100;

    public MockedModel() {
        this.utenti = new ArrayList<>();
        utenti.add(new Utente(1, "Mario", "Rossi", "veterinario@zoo.it", "pass", "veterinario"));
        utenti.add(new Utente(2, "Laura", "Bianchi", "volontario@zoo.it", "pass", "volontario"));
        utenti.add(new Utente(3, "Giulia", "Verdi", "visitatore@zoo.it", "pass", "visitatore"));

        this.specie = new ArrayList<>();
        specie.add(new Specie(1, "Leone"));
        specie.add(new Specie(2, "Elefante"));
        specie.add(new Specie(3, "Pinguino"));

        this.animali = new ArrayList<>();
        animali.add(new Animale(1, "Simba", 5, "Kenya", "buono", "Leone africano maschio",
            LocalDate.of(2021, 3, 10), 1, "Leone", Optional.of(1)));
        animali.add(new Animale(2, "Dumbo", 12, "India", "discreto", "Elefante asiatico femmina",
            LocalDate.of(2019, 7, 22), 2, "Elefante", Optional.of(2)));
        animali.add(new Animale(3, "Pingo", 3, "Antartide", "critico", "Pinguino imperatore giovane",
            LocalDate.of(2023, 1, 5), 3, "Pinguino", Optional.empty()));

        this.controlli = new ArrayList<>();
        this.terapie = new ArrayList<>();
    }

    @Override
    public Optional<Utente> login(String email, String password) {
        return utenti.stream()
            .filter(u -> u.email.equals(email) && u.password.equals(password))
            .findFirst();
    }

    @Override
    public Optional<Utente> registra(String nome, String cognome, String email, String password) {
        var nuovo = new Utente(++nextId, nome, cognome, email, password, "visitatore");
        utenti.add(nuovo);
        return Optional.of(nuovo);
    }

    @Override
    public List<Specie> specie() {
        return List.copyOf(specie);
    }

    @Override
    public int contaAnimaliPerSpecie(int idSpecie) {
        return (int) animali.stream().filter(a -> a.idSpecie == idSpecie).count();
    }

    @Override
    public List<Animale> animali() {
        return List.copyOf(animali);
    }

    @Override
    public List<Animale> loadAnimali() {
        return List.copyOf(animali);
    }

    @Override
    public boolean loadedAnimali() {
        return true;
    }

    @Override
    public List<Animale> searchByNome(String nome) {
        return animali.stream()
            .filter(a -> a.nome.toLowerCase().contains(nome.toLowerCase()))
            .toList();
    }

    @Override
    public List<Animale> filterByStato(String stato) {
        return animali.stream().filter(a -> a.statoDiSalute.equalsIgnoreCase(stato)).toList();
    }

    @Override
    public Optional<Animale> findAnimale(int id) {
        return animali.stream().filter(a -> a.id == id).findFirst();
    }

    @Override
    public int insertAnimale(String nome, int eta, String provenienza, String statoDiSalute,
                             String descrizione, LocalDate dataArrivo, int idSpecie,
                             Optional<Integer> idRecinto) {
        int id = ++nextId;
        String nomeSpecie = specie.stream()
            .filter(s -> s.id == idSpecie).findFirst()
            .map(s -> s.nome).orElse("?");
        animali.add(new Animale(id, nome, eta, provenienza, statoDiSalute,
            descrizione, dataArrivo, idSpecie, nomeSpecie, idRecinto));
        return id;
    }

    @Override
    public void updateStatoAnimale(int id, String nuovoStato) {
        animali.stream().filter(a -> a.id == id).findFirst().ifPresent(old -> {
            animali.remove(old);
            animali.add(new Animale(old.id, old.nome, old.eta, old.provenienza,
                nuovoStato, old.descrizione, old.dataArrivo,
                old.idSpecie, old.nomeSpecie, old.idRecinto));
        });
    }

    @Override
    public List<Animale> animaliDaControllare() {
        return animali.stream()
            .filter(a -> a.statoDiSalute.equalsIgnoreCase("critico"))
            .toList();
    }

    @Override
    public int insertControllo(LocalDate data, LocalTime ora, String tipologia,
                               String esito, int idAnimale, int idVeterinario) {
        int id = ++nextId;
        controlli.add(new ControlloSanitario(id, data, ora, tipologia, esito, idAnimale, idVeterinario));
        return id;
    }

    @Override
    public List<ControlloSanitario> storicoControlli(int idAnimale) {
        return controlli.stream().filter(c -> c.idAnimale == idAnimale).toList();
    }

    @Override
    public int insertTerapia(String farmaco, String dosaggio, String durata,
                             LocalDate dataInizio, LocalDate dataFine, int idControllo) {
        int id = ++nextId;
        terapie.add(new Terapia(id, farmaco, dosaggio, durata, dataInizio, dataFine, idControllo));
        return id;
    }
}
