-- =====================================================
-- SCHEMA DATABASE: Centro Recupero Animali
-- =====================================================

-- Crea il database se non esiste
CREATE DATABASE IF NOT EXISTS zoo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE zoo_db;

-- Elimina tabelle esistenti (in ordine per rispettare i vincoli)
DROP TABLE IF EXISTS Previsione;
DROP TABLE IF EXISTS Assegnato;
DROP TABLE IF EXISTS Affidato;
DROP TABLE IF EXISTS Svolgimento;
DROP TABLE IF EXISTS Trasporto_Esterno;
DROP TABLE IF EXISTS Movimentazione_Animale;
DROP TABLE IF EXISTS Terapia;
DROP TABLE IF EXISTS Controllo_Sanitario;
DROP TABLE IF EXISTS Turno;
DROP TABLE IF EXISTS Mansione;
DROP TABLE IF EXISTS Animale;
DROP TABLE IF EXISTS Specie;
DROP TABLE IF EXISTS Recinto;
DROP TABLE IF EXISTS Utente;

-- =====================================================
-- TABELLA: UTENTE
-- =====================================================
CREATE TABLE Utente (
    ID_utente INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ruolo VARCHAR(20) NOT NULL CHECK(ruolo IN('visitatore','volontario','veterinario'))
);

-- =====================================================
-- TABELLA: SPECIE
-- =====================================================
CREATE TABLE Specie (
    ID_specie INT PRIMARY KEY AUTO_INCREMENT,
    nome_specie VARCHAR(50) NOT NULL UNIQUE
);

-- =====================================================
-- TABELLA: RECINTO
-- =====================================================
CREATE TABLE Recinto (
    ID_recinto INT PRIMARY KEY AUTO_INCREMENT,
    tipologia VARCHAR(50) NOT NULL
);

-- =====================================================
-- TABELLA: ANIMALE
-- =====================================================
CREATE TABLE Animale (
    ID_animale INT PRIMARY KEY AUTO_INCREMENT,
    nome_animale VARCHAR(50) NOT NULL,
    eta INT NOT NULL CHECK(eta >= 0),
    provenienza VARCHAR(100) NOT NULL,
    stato_di_salute VARCHAR(50) NOT NULL CHECK(stato_di_salute IN('buono','discreto','critico')),
    descrizione TEXT,
    data_arrivo DATE NOT NULL,
    ID_specie INT NOT NULL,
    ID_recinto INT,
    FOREIGN KEY (ID_specie) REFERENCES Specie(ID_specie) ON DELETE RESTRICT,
    FOREIGN KEY (ID_recinto) REFERENCES Recinto(ID_recinto) ON DELETE SET NULL
);

-- =====================================================
-- TABELLA: TURNO
-- =====================================================
CREATE TABLE Turno (
    data DATE NOT NULL,
    fascia_oraria VARCHAR(20) NOT NULL CHECK(fascia_oraria IN('mattina','pomeriggio')),
    PRIMARY KEY (data, fascia_oraria)
);

-- =====================================================
-- TABELLA: MANSIONE
-- =====================================================
CREATE TABLE Mansione (
    ID_mansione INT PRIMARY KEY AUTO_INCREMENT,
    descrizione VARCHAR(50) NOT NULL CHECK(descrizione IN(
        'pulizia recinto',
        'manutenzione recinto',
        'distribuzione cibo e acqua',
        'somministrazione medicinali',
        'altro'
    ))
);

-- =====================================================
-- TABELLA: CONTROLLO_SANITARIO
-- =====================================================
CREATE TABLE Controllo_Sanitario (
    ID_controllo INT PRIMARY KEY AUTO_INCREMENT,
    data DATE NOT NULL,
    ora TIME NOT NULL,
    tipologia VARCHAR(50) NOT NULL CHECK(tipologia IN(
        'visita di routine',
        'esami delle feci',
        'valutazione respiratoria',
        'valutazione cardiaca',
        'valutazione post operatoria',
        'monitoraggio terapia',
        'richiamo vaccinale',
        'altro'
    )),
    esito VARCHAR(20) NOT NULL CHECK(esito IN('positivo','negativo','da monitorare')),
    ID_animale INT NOT NULL,
    ID_veterinario INT NOT NULL,
    FOREIGN KEY (ID_animale) REFERENCES Animale(ID_animale) ON DELETE CASCADE,
    FOREIGN KEY (ID_veterinario) REFERENCES Utente(ID_utente) ON DELETE RESTRICT
);

-- =====================================================
-- TABELLA: TERAPIA
-- =====================================================
CREATE TABLE Terapia (
    ID_terapia INT PRIMARY KEY AUTO_INCREMENT,
    farmaco VARCHAR(100) NOT NULL,
    dosaggio VARCHAR(50) NOT NULL,
    durata VARCHAR(50) NOT NULL,
    data_inizio DATE NOT NULL,
    data_fine DATE NOT NULL,
    ID_controllo INT NOT NULL,
    FOREIGN KEY (ID_controllo) REFERENCES Controllo_Sanitario(ID_controllo) ON DELETE CASCADE
);

-- =====================================================
-- TABELLA: MOVIMENTAZIONE_ANIMALE
-- =====================================================
CREATE TABLE Movimentazione_Animale (
    ID_movimentazione INT PRIMARY KEY AUTO_INCREMENT,
    data_spostamento DATE NOT NULL,
    motivazione VARCHAR(255) NOT NULL,
    ID_animale INT NOT NULL,
    ID_recinto_destinazione INT NOT NULL,
    ID_recinto_provenienza INT,
    FOREIGN KEY (ID_animale) REFERENCES Animale(ID_animale) ON DELETE CASCADE,
    FOREIGN KEY (ID_recinto_destinazione) REFERENCES Recinto(ID_recinto) ON DELETE RESTRICT,
    FOREIGN KEY (ID_recinto_provenienza) REFERENCES Recinto(ID_recinto) ON DELETE SET NULL
);

-- =====================================================
-- TABELLA: TRASPORTO_ESTERNO
-- =====================================================
CREATE TABLE Trasporto_Esterno (
    ID_trasporto INT PRIMARY KEY AUTO_INCREMENT,
    destinazione VARCHAR(150) NOT NULL,
    data DATE NOT NULL,
    motivazione VARCHAR(255) NOT NULL,
    mezzo_di_trasporto VARCHAR(50) NOT NULL CHECK(mezzo_di_trasporto IN(
        'ambulanza veterinaria',
        'furgone',
        'auto privata',
        'altro'
    )),
    ID_animale INT NOT NULL,
    FOREIGN KEY (ID_animale) REFERENCES Animale(ID_animale) ON DELETE CASCADE
);

-- =====================================================
-- TABELLA: SVOLGIMENTO (Relazione Utente-Turno)
-- =====================================================
CREATE TABLE Svolgimento (
    ID_utente INT NOT NULL,
    data DATE NOT NULL,
    fascia_oraria VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_utente, data, fascia_oraria),
    FOREIGN KEY (ID_utente) REFERENCES Utente(ID_utente) ON DELETE CASCADE,
    FOREIGN KEY (data, fascia_oraria) REFERENCES Turno(data, fascia_oraria) ON DELETE CASCADE
);

-- =====================================================
-- TABELLA: AFFIDATO (Relazione Utente-Mansione)
-- =====================================================
CREATE TABLE Affidato (
    ID_utente INT NOT NULL,
    ID_mansione INT NOT NULL,
    PRIMARY KEY (ID_utente, ID_mansione),
    FOREIGN KEY (ID_utente) REFERENCES Utente(ID_utente) ON DELETE CASCADE,
    FOREIGN KEY (ID_mansione) REFERENCES Mansione(ID_mansione) ON DELETE CASCADE
);

-- =====================================================
-- TABELLA: ASSEGNATO (Relazione Mansione-Recinto)
-- =====================================================
CREATE TABLE Assegnato (
    ID_mansione INT NOT NULL,
    ID_recinto INT NOT NULL,
    PRIMARY KEY (ID_mansione, ID_recinto),
    FOREIGN KEY (ID_mansione) REFERENCES Mansione(ID_mansione) ON DELETE CASCADE,
    FOREIGN KEY (ID_recinto) REFERENCES Recinto(ID_recinto) ON DELETE CASCADE
);

-- =====================================================
-- TABELLA: PREVISIONE (Relazione Trasporto-Mansione)
-- =====================================================
CREATE TABLE Previsione (
    ID_trasporto INT NOT NULL,
    ID_mansione INT NOT NULL,
    PRIMARY KEY (ID_trasporto, ID_mansione),
    FOREIGN KEY (ID_trasporto) REFERENCES Trasporto_Esterno(ID_trasporto) ON DELETE CASCADE,
    FOREIGN KEY (ID_mansione) REFERENCES Mansione(ID_mansione) ON DELETE CASCADE
);

-- =====================================================
-- INDICI PER OTTIMIZZAZIONE
-- =====================================================

-- Indici per ricerche frequenti
CREATE INDEX idx_animale_specie ON Animale(ID_specie);
CREATE INDEX idx_animale_recinto ON Animale(ID_recinto);
CREATE INDEX idx_animale_stato ON Animale(stato_di_salute);
CREATE INDEX idx_controllo_data ON Controllo_Sanitario(data);
CREATE INDEX idx_controllo_animale ON Controllo_Sanitario(ID_animale);
CREATE INDEX idx_controllo_veterinario ON Controllo_Sanitario(ID_veterinario);
CREATE INDEX idx_terapia_date ON Terapia(data_inizio, data_fine);
CREATE INDEX idx_turno_data ON Turno(data);
CREATE INDEX idx_utente_email ON Utente(email);
CREATE INDEX idx_utente_ruolo ON Utente(ruolo);

-- =====================================================
-- FINE SCHEMA
-- =====================================================