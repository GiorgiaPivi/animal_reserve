package db_lab.model;

/**
 * Classe che rappresenta un Utente del sistema
 */
public class Utente {
    
    private int idUtente;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo; // visitatore, volontario, veterinario
    
    // Costruttore vuoto
    public Utente() {}
    
    // Costruttore completo
    public Utente(int idUtente, String nome, String cognome, String email, 
                  String password, String ruolo) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }
    
    // Getters e Setters
    public int getIdUtente() {
        return idUtente;
    }
    
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCognome() {
        return cognome;
    }
    
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRuolo() {
        return ruolo;
    }
    
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
    
    // Metodi helper
    public boolean isVeterinario() {
        return "veterinario".equalsIgnoreCase(ruolo);
    }
    
    public boolean isVolontario() {
        return "volontario".equalsIgnoreCase(ruolo);
    }
    
    public boolean isVisitatore() {
        return "visitatore".equalsIgnoreCase(ruolo);
    }
    
    @Override
    public String toString() {
        return String.format("Utente[ID=%d, Nome=%s %s, Email=%s, Ruolo=%s]",
                idUtente, nome, cognome, email, ruolo);
    }
}
