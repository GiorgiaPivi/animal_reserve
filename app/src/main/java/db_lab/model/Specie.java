package db_lab.model;

/**
 * Classe che rappresenta una Specie animale
 */
public class Specie {
    
    private int idSpecie;
    private String nomeSpecie;
    
    // Costruttore vuoto
    public Specie() {}
    
    // Costruttore completo
    public Specie(int idSpecie, String nomeSpecie) {
        this.idSpecie = idSpecie;
        this.nomeSpecie = nomeSpecie;
    }
    
    // Getters e Setters
    public int getIdSpecie() {
        return idSpecie;
    }
    
    public void setIdSpecie(int idSpecie) {
        this.idSpecie = idSpecie;
    }
    
    public String getNomeSpecie() {
        return nomeSpecie;
    }
    
    public void setNomeSpecie(String nomeSpecie) {
        this.nomeSpecie = nomeSpecie;
    }
    
    @Override
    public String toString() {
        return String.format("Specie[ID=%d, Nome=%s]", idSpecie, nomeSpecie);
    }
}