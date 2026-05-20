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

    // ------- Recinto -------

    public static final String LIST_RECINTI =
        """
        SELECT * FROM Recinto ORDER BY ID_recinto
        """;

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
        VALUES (?, ?, ?, ?, ?, ?)
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
}
