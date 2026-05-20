package db_lab.model;

/**
 * Classe che rappresenta un Recinto
 */
public class Recinto {
    
    private int idRecinto;
    private String tipologia;
    
    // Costruttore vuoto
    public Recinto() {}
    
    // Costruttore completo
    public Recinto(int idRecinto, String tipologia) {
        this.idRecinto = idRecinto;
        this.tipologia = tipologia;
    }
    
    // Getters e Setters
    public int getIdRecinto() {
        return idRecinto;
    }
    
    public void setIdRecinto(int idRecinto) {
        this.idRecinto = idRecinto;
    }
    
    public String getTipologia() {
        return tipologia;
    }
    
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }
    
    @Override
    public String toString() {
        return String.format("Recinto[ID=%d, Tipologia=%s]", idRecinto, tipologia);
    }
}
