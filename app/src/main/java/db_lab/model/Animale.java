package db_lab.model;

import java.time.LocalDate;

/**
 * Classe che rappresenta un Animale nel sistema
 */
public class Animale {
    
    private int idAnimale;
    private String nomeAnimale;
    private int eta;
    private String provenienza;
    private String statoDiSalute;
    private String descrizione;
    private LocalDate dataArrivo;
    private int idSpecie;
    private Integer idRecinto; // Nullable
    
    // Costruttore vuoto
    public Animale() {}
    
    // Costruttore completo
    public Animale(int idAnimale, String nomeAnimale, int eta, String provenienza, 
                   String statoDiSalute, String descrizione, LocalDate dataArrivo, 
                   int idSpecie, Integer idRecinto) {
        this.idAnimale = idAnimale;
        this.nomeAnimale = nomeAnimale;
        this.eta = eta;
        this.provenienza = provenienza;
        this.statoDiSalute = statoDiSalute;
        this.descrizione = descrizione;
        this.dataArrivo = dataArrivo;
        this.idSpecie = idSpecie;
        this.idRecinto = idRecinto;
    }
    
    // Getters e Setters
    public int getIdAnimale() {
        return idAnimale;
    }
    
    public void setIdAnimale(int idAnimale) {
        this.idAnimale = idAnimale;
    }
    
    public String getNomeAnimale() {
        return nomeAnimale;
    }
    
    public void setNomeAnimale(String nomeAnimale) {
        this.nomeAnimale = nomeAnimale;
    }
    
    public int getEta() {
        return eta;
    }
    
    public void setEta(int eta) {
        this.eta = eta;
    }
    
    public String getProvenienza() {
        return provenienza;
    }
    
    public void setProvenienza(String provenienza) {
        this.provenienza = provenienza;
    }
    
    public String getStatoDiSalute() {
        return statoDiSalute;
    }
    
    public void setStatoDiSalute(String statoDiSalute) {
        this.statoDiSalute = statoDiSalute;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    public LocalDate getDataArrivo() {
        return dataArrivo;
    }
    
    public void setDataArrivo(LocalDate dataArrivo) {
        this.dataArrivo = dataArrivo;
    }
    
    public int getIdSpecie() {
        return idSpecie;
    }
    
    public void setIdSpecie(int idSpecie) {
        this.idSpecie = idSpecie;
    }
    
    public Integer getIdRecinto() {
        return idRecinto;
    }
    
    public void setIdRecinto(Integer idRecinto) {
        this.idRecinto = idRecinto;
    }
    
    @Override
    public String toString() {
        return String.format("Animale[ID=%d, Nome=%s, Età=%d, Stato=%s, Arrivo=%s]",
                idAnimale, nomeAnimale, eta, statoDiSalute, dataArrivo);
    }
}
