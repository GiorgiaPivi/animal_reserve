package db_lab.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe che rappresenta un Controllo Sanitario
 */
public class ControlloSanitario {
    
    private int idControllo;
    private LocalDate data;
    private LocalTime ora;
    private String tipologia;
    private String esito;
    private int idAnimale;
    private int idVeterinario;
    
    // Costruttore vuoto
    public ControlloSanitario() {}
    
    // Costruttore completo
    public ControlloSanitario(int idControllo, LocalDate data, LocalTime ora, 
                             String tipologia, String esito, int idAnimale, int idVeterinario) {
        this.idControllo = idControllo;
        this.data = data;
        this.ora = ora;
        this.tipologia = tipologia;
        this.esito = esito;
        this.idAnimale = idAnimale;
        this.idVeterinario = idVeterinario;
    }
    
    // Getters e Setters
    public int getIdControllo() {
        return idControllo;
    }
    
    public void setIdControllo(int idControllo) {
        this.idControllo = idControllo;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public LocalTime getOra() {
        return ora;
    }
    
    public void setOra(LocalTime ora) {
        this.ora = ora;
    }
    
    public String getTipologia() {
        return tipologia;
    }
    
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }
    
    public String getEsito() {
        return esito;
    }
    
    public void setEsito(String esito) {
        this.esito = esito;
    }
    
    public int getIdAnimale() {
        return idAnimale;
    }
    
    public void setIdAnimale(int idAnimale) {
        this.idAnimale = idAnimale;
    }
    
    public int getIdVeterinario() {
        return idVeterinario;
    }
    
    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }
    
    @Override
    public String toString() {
        return String.format("ControlloSanitario[ID=%d, Data=%s, Tipologia=%s, Esito=%s]",
                idControllo, data, tipologia, esito);
    }
}