package db_lab.model;

import java.time.LocalDate;

/**
 * Classe che rappresenta un Turno di lavoro
 */
public class Turno {
    
    private LocalDate data;
    private String fasciaOraria; // mattina, pomeriggio
    
    // Costruttore vuoto
    public Turno() {}
    
    // Costruttore completo
    public Turno(LocalDate data, String fasciaOraria) {
        this.data = data;
        this.fasciaOraria = fasciaOraria;
    }
    
    // Getters e Setters
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public String getFasciaOraria() {
        return fasciaOraria;
    }
    
    public void setFasciaOraria(String fasciaOraria) {
        this.fasciaOraria = fasciaOraria;
    }
    
    @Override
    public String toString() {
        return String.format("Turno[Data=%s, Fascia=%s]", data, fasciaOraria);
    }
}