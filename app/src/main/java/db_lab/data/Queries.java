package db_lab.data;

public final class Queries {

    // ------- Utente -------

    public static final String FIND_UTENTE_BY_CREDENTIALS =
        """
        SELECT * FROM Utente WHERE email = ? AND password = ?
        """;

    public static final String INSERT_UTENTE =
        """
        INSERT INTO Utente (nome, cognome, email, password, ruolo)
        VALUES (?, ?, ?, ?, ?)
        """;

    public static final String LIST_UTENTI =
        """
        SELECT * FROM Utente ORDER BY cognome, nome
        """;

    // ------- Specie -------

    public static final String LIST_SPECIE =
        """
        SELECT * FROM Specie ORDER BY nome_specie
        """;

    public static final String COUNT_ANIMALI_PER_SPECIE =
        """
        SELECT COUNT(*) AS totale FROM Animale WHERE ID_specie = ?
        """;

    public static final String INSERT_SPECIE =
        "INSERT INTO SPECIE (nome_specie) VALUES (?)";
    // ------- Recinto -------

    public static final String LIST_RECINTI =
        """
        SELECT * FROM Recinto ORDER BY ID_recinto
        """;

    public static final String INSERT_RECINTO =
        "INSERT INTO RECINTO (tipo_recinto) VALUES (?)";

    // ------- Animale -------

    public static final String LIST_ANIMALI =
        """
        SELECT A.*, S.nome_specie
        FROM Animale A
        JOIN Specie S ON A.ID_specie = S.ID_specie
        ORDER BY A.nome_animale
        """;

    public static final String FIND_ANIMALE =
        """
        SELECT A.*, S.nome_specie
        FROM Animale A
        JOIN Specie S ON A.ID_specie = S.ID_specie
        WHERE A.ID_animale = ?
        """;

    public static final String SEARCH_ANIMALI_BY_NOME =
        """
        SELECT A.*, S.nome_specie
        FROM Animale A
        JOIN Specie S ON A.ID_specie = S.ID_specie
        WHERE A.nome_animale LIKE ?
        ORDER BY A.nome_animale
        """;

    public static final String FILTER_ANIMALI_BY_STATO =
        """
        SELECT A.*, S.nome_specie
        FROM Animale A
        JOIN Specie S ON A.ID_specie = S.ID_specie
        WHERE A.stato_di_salute = ?
        ORDER BY A.nome_animale
        """;

    public static final String INSERT_ANIMALE =
        """
        INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute,
            descrizione, data_arrivo, ID_specie, ID_recinto)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    public static final String UPDATE_ANIMALE_STATO =
        """
        UPDATE Animale SET stato_di_salute = ? WHERE ID_animale = ?
        """;

    public static final String ANIMALI_DA_CONTROLLARE =
        """
        SELECT A.*, S.nome_specie, MAX(CS.data) AS ultimo_controllo
        FROM Animale A
        JOIN Specie S ON A.ID_specie = S.ID_specie
        LEFT JOIN Controllo_Sanitario CS ON A.ID_animale = CS.ID_animale
        GROUP BY A.ID_animale
        HAVING MAX(CS.data) < DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            OR MAX(CS.data) IS NULL
        ORDER BY ultimo_controllo ASC
        """;

    // ------- Controllo Sanitario -------

    public static final String INSERT_CONTROLLO =
        """
        INSERT INTO Controllo_Sanitario (data, ora, tipologia, esito, ID_animale, ID_veterinario)
        SELECT ?, ?, ?, ?, ?, ?
        WHERE (SELECT ruolo FROM Utente WHERE ID_utente = ?) = 'veterinario'
        """;

    public static final String LIST_CONTROLLI_BY_ANIMALE =
        """
        SELECT CS.*, U.nome AS nome_vet, U.cognome AS cognome_vet
        FROM Controllo_Sanitario CS
        JOIN Utente U ON CS.ID_veterinario = U.ID_utente
        WHERE CS.ID_animale = ?
        ORDER BY CS.data DESC, CS.ora DESC
        """;

    // ------- Terapia -------

    public static final String INSERT_TERAPIA =
        """
        INSERT INTO Terapia (farmaco, dosaggio, durata, data_inizio, data_fine, ID_controllo)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    public static final String LIST_TERAPIE_BY_CONTROLLO =
        """
        SELECT * FROM Terapia WHERE ID_controllo = ?
        """;

    public static final String LIST_TERAPIE_BY_ANIMALE =
        """
        SELECT T.*, CS.data AS data_controllo, CS.tipologia AS tipo_controllo
        FROM Terapia T
        JOIN Controllo_Sanitario CS ON T.ID_controllo = CS.ID_controllo
        WHERE CS.ID_animale = ?
        ORDER BY T.data_inizio DESC
        """;

    public static final String UPDATE_TERAPIA =
        """
        UPDATE Terapia 
        SET farmaco = ?, dosaggio = ?, durata = ?, data_fine = ?
        WHERE ID_terapia = ?
        """;

    // ------- Movimentazione -------

    public static final String INSERT_MOVIMENTAZIONE =
        """
        INSERT INTO Movimentazione (data_movimentazione, ID_animale, ID_recinto_destinazione)
        VALUES (?, ?, ?)
        """;

    public static final String LIST_MOVIMENTAZIONI_BY_ANIMALE =
        """
        SELECT M.*, R.tipo_recinto, R.capienza
        FROM Movimentazione M
        JOIN Recinto R ON M.ID_recinto_destinazione = R.ID_recinto
        WHERE M.ID_animale = ?
        ORDER BY M.data_movimentazione DESC
        """;

    public static final String FIND_RECINTO_BY_ID =
        """
        SELECT * FROM Recinto WHERE ID_recinto = ?
        """;

    public static final String COUNT_ANIMALI_IN_RECINTO =
        """
        SELECT COUNT(*) AS totale FROM Animale WHERE ID_recinto = ?
        """;

    public static final String RECINTI_DISPONIBILI =
        """
        SELECT R.*, 
            (SELECT COUNT(*) FROM Animale WHERE ID_recinto = R.ID_recinto) AS occupazione
        FROM Recinto R
        WHERE (SELECT COUNT(*) FROM Animale WHERE ID_recinto = R.ID_recinto) < R.capienza
        ORDER BY R.tipo_recinto, R.ID_recinto
        """;

    // ------- Trasporto Esterno -------

    public static final String INSERT_TRASPORTO =
        """
        INSERT INTO Trasporto_Esterno 
        (data_trasporto, destinazione, motivazione, ID_animale, ID_volontario)
        VALUES (?, ?, ?, ?, ?)
        """;

    public static final String LIST_TRASPORTI_BY_ANIMALE =
        """
        SELECT TE.*, U.nome AS nome_volontario, U.cognome AS cognome_volontario
        FROM Trasporto_Esterno TE
        JOIN Utente U ON TE.ID_volontario = U.ID_utente
        WHERE TE.ID_animale = ?
        ORDER BY TE.data_trasporto DESC
        """;

    public static final String LIST_ALL_TRASPORTI =
        """
        SELECT TE.*, A.nome_animale, U.nome AS nome_volontario, U.cognome AS cognome_volontario
        FROM Trasporto_Esterno TE
        JOIN Animale A ON TE.ID_animale = A.ID_animale
        JOIN Utente U ON TE.ID_volontario = U.ID_utente
        ORDER BY TE.data_trasporto DESC
        """;

    // ------- Statistiche / Dashboard -------

    public static final String CONTA_ANIMALI_TOTALI =
        """
        SELECT COUNT(*) AS totale FROM Animale
        """;

    public static final String CONTA_ANIMALI_PER_STATO =
        """
        SELECT stato_di_salute, COUNT(*) AS totale 
        FROM Animale 
        GROUP BY stato_di_salute
        """;

    public static final String CONTA_CONTROLLI_ULTIMI_30_GIORNI =
        """
        SELECT COUNT(*) AS totale 
        FROM Controllo_Sanitario 
        WHERE data >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
        """;

    public static final String ANIMALI_PIU_RECENTI =
        """
        SELECT A.*, S.nome_specie
        FROM Animale A
        JOIN Specie S ON A.ID_specie = S.ID_specie
        ORDER BY A.data_arrivo DESC
        LIMIT 5
        """;

    public static final String SPECIE_PIU_NUMEROSE =
        """
        SELECT S.nome_specie, COUNT(A.ID_animale) AS totale
        FROM Specie S
        LEFT JOIN Animale A ON S.ID_specie = A.ID_specie
        GROUP BY S.ID_specie, S.nome_specie
        ORDER BY totale DESC
        LIMIT 5
        """;

    public static final String RECINTI_OCCUPAZIONE =
        """
        SELECT R.ID_recinto, R.tipo_recinto, R.capienza,
            COUNT(A.ID_animale) AS occupati
        FROM Recinto R
        LEFT JOIN Animale A ON R.ID_recinto = A.ID_recinto
        GROUP BY R.ID_recinto, R.tipo_recinto, R.capienza
        ORDER BY R.ID_recinto
        """;

    // ------- Dettaglio Controllo -------

    public static final String FIND_CONTROLLO =
        """
        SELECT CS.*, A.nome_animale, U.nome AS nome_vet, U.cognome AS cognome_vet
        FROM Controllo_Sanitario CS
        JOIN Animale A ON CS.ID_animale = A.ID_animale
        JOIN Utente U ON CS.ID_veterinario = U.ID_utente
        WHERE CS.ID_controllo = ?
        """;

    // ------- Turno -------
    public static final String INSERT_TURNO =
    "INSERT INTO TURNO (data, fascia_oraria) VALUES (?, ?)";

    public static final String ASSIGN_TURNO =
        """
        INSERT INTO Svolgimento (ID_utente, data, fascia_oraria)
        SELECT ?, ?, ?
        WHERE (SELECT ruolo FROM Utente WHERE ID_utente = ?) IN ('volontario', 'veterinario')
        """;

    public static final String LIST_TURNI =
        "SELECT data, fascia_oraria FROM TURNO ORDER BY data, fascia_oraria";
    
    public static final String LIST_TURNI_BY_UTENTE = 
    "SELECT t.data, t.fascia_oraria FROM turno t " +
    "JOIN svolgimento s ON t.data = s.data AND t.fascia_oraria = s.fascia_oraria " +
    "WHERE s.id_utente = ?";
    
    // ------- Mansioni -------
    public static final String INSERT_MANSIONE =
    "INSERT INTO MANSIONE (descrizione, tipo_mansione) VALUES (?, ?)";

    public static final String INSERT_AFFIDATO =
        """
        INSERT INTO Affidato (ID_utente, ID_mansione)
        SELECT ?, ?
        WHERE (SELECT ruolo FROM Utente WHERE ID_utente = ?) = 'volontario'
        """;

    public static final String LIST_MANSIONI =
        "SELECT ID_mansione, descrizione FROM MANSIONE ORDER BY ID_mansione";

    public static final String LIST_MANSIONI_BY_UTENTE = 
        "SELECT m.ID_mansione, m.descrizione FROM mansione m " +
        "JOIN assegnazione_mansione am ON m.ID_mansione = am.ID_mansione " +
        "WHERE am.ID_utente = ?";
}