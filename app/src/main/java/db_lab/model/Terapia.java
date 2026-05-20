package db_lab.model;

import java.time.LocalDate;

/**
 * Classe che rappresenta una Terapia prescritta
 */
public class Terapia {
    
    private int idTerapia;
    private String farmaco;
    private String dosaggio;
    private String durata;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private int idControllo;
    
    // Costruttore vuoto
    public Terapia() {}
    
    // Costruttore completo
    public Terapia(int idTerapia, String farmaco, String dosaggio, String durata,
                   LocalDate dataInizio, LocalDate dataFine, int idControllo) {
        this.idTerapia = idTerapia;
        this.farmaco = farmaco;
        this.dosaggio = dosaggio;
        this.durata = durata;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.idControllo = idControllo;
    }
    
    // Getters e Setters
    public int getIdTerapia() {
        return idTerapia;
    }
    
    public void setIdTerapia(int idTerapia) {
        this.idTerapia = idTerapia;
    }
    
    public String getFarmaco() {
        return farmaco;
    }
    
    public void setFarmaco(String farmaco) {
        this.farmaco = farmaco;
    }
    
    public String getDosaggio() {
        return dosaggio;
    }
    
    public void setDosaggio(String dosaggio) {
        this.dosaggio = dosaggio;
    }
    
    public String getDurata() {
        return durata;
    }
    
    public void setDurata(String durata) {
        this.durata = durata;
    }
    
    public LocalDate getDataInizio() {
        return dataInizio;
    }
    
    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }
    
    public LocalDate getDataFine() {
        return dataFine;
    }
    
    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }
    
    public int getIdControllo() {
        return idControllo;
    }
    
    public void setIdControllo(int idControllo) {
        this.idControllo = idControllo;
    }
    
    @Override
    public String toString() {
        return String.format("Terapia[ID=%d, Farmaco=%s, Dal %s al %s]",
                idTerapia, farmaco, dataInizio, dataFine);
    }
}