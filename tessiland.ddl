-- =====================================================
-- SCHEMA DATABASE: Centro Recupero Animali
-- =====================================================

-- Crea il database se non esiste
CREATE DATABASE IF NOT EXISTS animal_reserve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE animal_reserve;

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
    ruolo VARCHAR(20) NOT NULL CHECK(ruolo IN('visitatore','volontario', 'veterinario','admin'))
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

-- =====================================================
-- INSERISCI DATI DI TEST
-- =====================================================

-- Inserisci Utenti
INSERT INTO Utente (nome, cognome, email, password, ruolo) 
VALUES ('Mario', 'Rossi', 'veterinario@zoo.it', 'pass', 'veterinario');

INSERT INTO Utente (nome, cognome, email, password, ruolo) 
VALUES ('Laura', 'Bianchi', 'volontario@zoo.it', 'pass', 'volontario');

INSERT INTO Utente (nome, cognome, email, password, ruolo) 
VALUES ('Giulia', 'Verdi', 'visitatore@zoo.it', 'pass', 'visitatore');

INSERT INTO Utente (nome, cognome, email, password, ruolo) 
VALUES ('Admin', 'Sistema', 'admin@zoo.it', 'pass', 'admin');

-- Inserisci Specie
INSERT INTO Specie (nome_specie) VALUES ('Leone');
INSERT INTO Specie (nome_specie) VALUES ('Elefante');
INSERT INTO Specie (nome_specie) VALUES ('Pinguino');
INSERT INTO Specie (nome_specie) VALUES ('Orso');
INSERT INTO Specie (nome_specie) VALUES ('Tigre');

-- Inserisci Recinti
INSERT INTO Recinto (tipologia) VALUES ('Savana');
INSERT INTO Recinto (tipologia) VALUES ('Foresta');
INSERT INTO Recinto (tipologia) VALUES ('Acquatico');
INSERT INTO Recinto (tipologia) VALUES ('Montagna');

-- Inserisci Animali
INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute, descrizione, data_arrivo, ID_specie, ID_recinto) 
VALUES ('Simba', 5, 'Kenya', 'buono', 'Leone africano maschio', '2021-03-10', 1, 1);

INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute, descrizione, data_arrivo, ID_specie, ID_recinto) 
VALUES ('Dumbo', 12, 'India', 'discreto', 'Elefante asiatico femmina', '2019-07-22', 2, 2);

INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute, descrizione, data_arrivo, ID_specie, ID_recinto) 
VALUES ('Pingo', 3, 'Antartide', 'critico', 'Pinguino imperatore giovane', '2023-01-05', 3, 3);

INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute, descrizione, data_arrivo, ID_specie, ID_recinto) 
VALUES ('Bruno', 8, 'Russia', 'buono', 'Orso bruno maschio', '2020-05-15', 4, 4);

INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute, descrizione, data_arrivo, ID_specie, ID_recinto) 
VALUES ('Raja', 6, 'India', 'discreto', 'Tigre del Bengala maschio', '2022-09-20', 5, 1);

-- Inserisci Turni
INSERT INTO Turno (data, fascia_oraria) VALUES (CURDATE(), 'mattina');
INSERT INTO Turno (data, fascia_oraria) VALUES (CURDATE(), 'pomeriggio');
INSERT INTO Turno (data, fascia_oraria) VALUES (DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'mattina');
INSERT INTO Turno (data, fascia_oraria) VALUES (DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'pomeriggio');
INSERT INTO Turno (data, fascia_oraria) VALUES (DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'mattina');

-- Inserisci Mansioni
INSERT INTO Mansione (descrizione) VALUES ('pulizia recinto');
INSERT INTO Mansione (descrizione) VALUES ('distribuzione cibo e acqua');
INSERT INTO Mansione (descrizione) VALUES ('somministrazione medicinali');
INSERT INTO Mansione (descrizione) VALUES ('manutenzione recinto');
INSERT INTO Mansione (descrizione) VALUES ('altro');

-- Assegna Turni ai Volontari (SVOLGIMENTO)
INSERT INTO Svolgimento (ID_utente, data, fascia_oraria) 
VALUES (1, CURDATE(), 'mattina');

INSERT INTO Svolgimento (ID_utente, data, fascia_oraria) 
VALUES (2, CURDATE(), 'pomeriggio');

INSERT INTO Svolgimento (ID_utente, data, fascia_oraria) 
VALUES (1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'mattina');

INSERT INTO Svolgimento (ID_utente, data, fascia_oraria) 
VALUES (2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'pomeriggio');

-- Assegna Mansioni ai Volontari (AFFIDATO)
INSERT INTO Affidato (ID_utente, ID_mansione) VALUES (1, 1);
INSERT INTO Affidato (ID_utente, ID_mansione) VALUES (1, 2);
INSERT INTO Affidato (ID_utente, ID_mansione) VALUES (2, 2);
INSERT INTO Affidato (ID_utente, ID_mansione) VALUES (2, 3);
INSERT INTO Affidato (ID_utente, ID_mansione) VALUES (4, 1);

-- Assegna Mansioni ai Recinti (ASSEGNATO)
INSERT INTO Assegnato (ID_mansione, ID_recinto) VALUES (1, 1);
INSERT INTO Assegnato (ID_mansione, ID_recinto) VALUES (1, 2);
INSERT INTO Assegnato (ID_mansione, ID_recinto) VALUES (2, 1);
INSERT INTO Assegnato (ID_mansione, ID_recinto) VALUES (2, 2);
INSERT INTO Assegnato (ID_mansione, ID_recinto) VALUES (2, 3);
INSERT INTO Assegnato (ID_mansione, ID_recinto) VALUES (3, 3);

-- Inserisci Controlli Sanitari
INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario) 
VALUES (CURDATE(), CURTIME(), 'visita di routine', 'positivo', 1, 1);

INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario) 
VALUES (DATE_SUB(CURDATE(), INTERVAL 2 DAY), '10:30:00', 'esami delle feci', 'negativo', 2, 1);

INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario) 
VALUES (DATE_SUB(CURDATE(), INTERVAL 1 DAY), '14:00:00', 'valutazione respiratoria', 'da monitorare', 3, 1);

INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario) 
VALUES (DATE_SUB(CURDATE(), INTERVAL 5 DAY), '09:15:00', 'visita di routine', 'positivo', 4, 1);

INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario) 
VALUES (DATE_SUB(CURDATE(), INTERVAL 3 DAY), '11:45:00', 'monitoraggio terapia', 'positivo', 5, 1);

-- Inserisci Terapie
INSERT INTO Terapia (farmaco, dosaggio, durata, data_inizio, data_fine, ID_controllo) 
VALUES ('Antibiotico A', '500mg', '7 giorni', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1);

INSERT INTO Terapia (farmaco, dosaggio, durata, data_inizio, data_fine, ID_controllo) 
VALUES ('Vitamine', '100ml', '14 giorni', DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 2);

INSERT INTO Terapia (farmaco, dosaggio, durata, data_inizio, data_fine, ID_controllo) 
VALUES ('Antinfiammatorio', '250mg', '5 giorni', DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 3);

-- Inserisci Movimentazioni
INSERT INTO Movimentazione_Animale (data_spostamento, motivazione, ID_animale, ID_recinto_destinazione, ID_recinto_provenienza) 
VALUES (DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Trasferimento per ampliamento recinto', 1, 2, 1);

INSERT INTO Movimentazione_Animale (data_spostamento, motivazione, ID_animale, ID_recinto_destinazione, ID_recinto_provenienza) 
VALUES (DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Migliore spazio per recupero', 3, 4, 3);

-- Inserisci Trasporti Esterni
INSERT INTO Trasporto_Esterno (destinazione, data, motivazione, mezzo_di_trasporto, ID_animale) 
VALUES ('Clinica Veterinaria Centro', DATE_SUB(CURDATE(), INTERVAL 8 DAY), 'Controllo approfondito', 'ambulanza veterinaria', 2);

INSERT INTO Trasporto_Esterno (destinazione, data, motivazione, mezzo_di_trasporto, ID_animale) 
VALUES ('Ospedale Animali Specializzato', DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'Intervento chirurgico', 'ambulanza veterinaria', 4);

-- Inserisci Previsioni (Trasporto-Mansione)
INSERT INTO Previsione (ID_trasporto, ID_mansione) VALUES (1, 3);
INSERT INTO Previsione (ID_trasporto, ID_mansione) VALUES (1, 2);
INSERT INTO Previsione (ID_trasporto, ID_mansione) VALUES (2, 3);

-- =====================================================
-- FINE DATI
-- =====================================================
