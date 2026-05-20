package db_lab.model;

/**
 * Classe che rappresenta una Mansione
 */
public class Mansione {
    
    private int idMansione;
    private String descrizione;
    
    // Costruttore vuoto
    public Mansione() {}
    
    // Costruttore completo
    public Mansione(int idMansione, String descrizione) {
        this.idMansione = idMansione;
        this.descrizione = descrizione;
    }
    
    // Getters e Setters
    public int getIdMansione() {
        return idMansione;
    }
    
    public void setIdMansione(int idMansione) {
        this.idMansione = idMansione;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    @Override
    public String toString() {
        return String.format("Mansione[ID=%d, Descrizione=%s]", idMansione, descrizione);
    }
}
