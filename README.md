# ANIMAL RESERVE – Sistema di Gestione per una Riserva Naturale

Animal Reserve è un'applicazione sviluppata in Java e MySQL per la gestione delle attività di una riserva naturale. Il sistema consente di amministrare animali, specie, recinti, personale, controlli sanitari, terapie, movimentazioni e trasporti esterni attraverso un'interfaccia grafica desktop.

---

## Funzionalità Principali

- Gestione degli animali ospitati nella riserva
- Gestione delle specie animali
- Gestione dei recinti e delle aree di permanenza
- Gestione del personale e delle mansioni assegnate
- Registrazione dei turni di lavoro
- Monitoraggio dei controlli sanitari
- Gestione delle terapie veterinarie
- Registrazione delle movimentazioni degli animali
- Gestione dei trasporti esterni
- Operazioni di inserimento, modifica, eliminazione e consultazione dei dati
- Persistenza dei dati tramite database MySQL

---

## Tecnologie Utilizzate

| Componente              | Tecnologia      |
|-------------------------|-----------------|
| Linguaggio              | Java 17         |
| Database                | MySQL           |
| Accesso ai dati         | JDBC            |
| Build Tool              | Gradle 8.7      |
| Testing                 | JUnit 4.13.2    |

---

## Architettura del Progetto

L'applicazione segue il pattern architetturale **MVC (Model-View-Controller)**, separando l'interfaccia utente dalla logica applicativa e dalla gestione dei dati.

```
animal_reserve/
├── App.java
├── Controller.java
├── View.java
│
├── data/
│   ├── DAOException.java
│   ├── DAOUtils.java
│   ├── Printer.java
│   └── Queries.java
│
└── model/
    ├── Model.java
    ├── DBModel.java
    ├── MockedModel.java
    ├── Animale.java
    ├── Specie.java
    ├── Recinto.java
    ├── Utente.java
    ├── Mansione.java
    ├── Turno.java
    ├── Terapia.java
    ├── ControlloSanitario.java
    ├── Movimentazione.java
    └── TrasportoEsterno.java
```

---

## Descrizione dei Componenti

### App.java
Punto di ingresso dell'applicazione. Inizializza la connessione al database e avvia i componenti principali del sistema.

### Controller.java
Gestisce gli eventi generati dall'interfaccia grafica e coordina la comunicazione tra vista e modello.

### View.java
Implementa l'interfaccia grafica dell'applicazione e consente all'utente di interagire con il sistema.

### Package `data`
Contiene le classi dedicate all'accesso ai dati:

- **DAOUtils.java** – gestione delle connessioni JDBC.
- **Queries.java** – raccolta centralizzata delle query SQL.
- **DAOException.java** – eccezioni personalizzate per il layer di accesso ai dati.
- **Printer.java** – utility per la visualizzazione delle informazioni.

### Package `model`
Contiene la logica applicativa e le entità del dominio:

- **Model.java** – interfaccia del modello.
- **DBModel.java** – implementazione del modello basata sul database MySQL.
- **MockedModel.java** – implementazione alternativa utilizzata per test e simulazioni.
- **Animale.java** – rappresentazione degli animali della riserva.
- **Specie.java** – gestione delle specie animali.
- **Recinto.java** – gestione dei recinti.
- **Utente.java** – rappresentazione degli utenti del sistema.
- **Mansione.java** – gestione delle mansioni del personale.
- **Turno.java** – gestione dei turni di lavoro.
- **Terapia.java** – gestione delle terapie veterinarie.
- **ControlloSanitario.java** – registrazione dei controlli sanitari.
- **Movimentazione.java** – gestione degli spostamenti degli animali.
- **TrasportoEsterno.java** – gestione dei trasporti verso strutture esterne.

---

## Installazione e Configurazione

### Prerequisiti

- Java 17 o superiore
- MySQL
- Gradle 8.7

### Clonare il Repository

```bash
git clone https://github.com/GiorgiaPivi/animal_reserve.git
```

### Configurare il Database

1. Aprire MySQL Workbench.
2. Creare un database denominato:

```sql
CREATE DATABASE animal_reserve;
```

3. Importare il file SQL fornito nel progetto per creare lo schema e caricare i dati iniziali.

### Configurare la Connessione

Nel file `App.java` verificare le credenziali di accesso al database:

```java
connection = DAOUtils.localMySQLConnection(
    "animal_reserve",
    "root",
    "<your-password>"
);
```

### Compilazione ed Esecuzione

```bash
./gradlew run
```

---

## Credenziali di Accesso

| Ruolo       | Email                  | Password |
|-------------|------------------------|----------|
| Visitatore  | visitatore@zoo.it      | pass     |
| Volontario  | volontario@zoo.it      | pass     |
| Veterinario | veterinario@zoo.it     | pass     |
| Admin       | admin@zoo.it           | pass     |

---

## Modello dei Dati

Il sistema gestisce le seguenti entità principali:

- Animale
- Specie
- Recinto
- Utente
- Mansione
- Turno
- Terapia
- Controllo Sanitario
- Movimentazione
- Trasporto Esterno

Le informazioni vengono memorizzate e recuperate tramite query SQL eseguite su database MySQL.

---

## Autrici

**Giorgia Pivi, Giulia Abbondanza** – Progetto universitario realizzato per il corso di Basi di Dati.