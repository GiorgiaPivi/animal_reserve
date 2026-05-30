package db_lab.model;

import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.Printer;
import db_lab.data.Queries;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ControlloSanitario {

    public final int id;
    public final LocalDate data;
    public final LocalTime ora;
    public final String tipologia;
    public final String esito;
    public final int idAnimale;
    public final int idVeterinario;

    public ControlloSanitario(int id, LocalDate data, LocalTime ora,
                              String tipologia, String esito,
                              int idAnimale, int idVeterinario) {
        this.id = id;
        this.data = data;
        this.ora = ora;
        this.tipologia = tipologia == null ? "" : tipologia;
        this.esito = esito == null ? "" : esito;
        this.idAnimale = idAnimale;
        this.idVeterinario = idVeterinario;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof ControlloSanitario c) {
            return c.id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return Printer.stringify("ControlloSanitario", List.of(
            Printer.field("id", this.id),
            Printer.field("data", this.data),
            Printer.field("tipologia", this.tipologia),
            Printer.field("esito", this.esito)
        ));
    }

    public static final class DAO {

        // OP09 - Inserisce un nuovo controllo, restituisce l'ID generato.
        // La query verifica che ID_veterinario abbia ruolo = 'veterinario' prima di inserire.
        public static int insert(Connection connection, LocalDate data, LocalTime ora,
                String tipologia, String esito, int idAnimale, int idVeterinario) {
            try (var statement = connection.prepareStatement(
                    Queries.INSERT_CONTROLLO, Statement.RETURN_GENERATED_KEYS)) {
                statement.setDate(1, Date.valueOf(data));
                statement.setTime(2, Time.valueOf(ora));
                statement.setString(3, tipologia);
                statement.setString(4, esito);
                statement.setInt(5, idAnimale);
                statement.setInt(6, idVeterinario);
                statement.setInt(7, idVeterinario); // usato nel WHERE per il controllo ruolo
                int rows = statement.executeUpdate();
                if (rows == 0) {
                    throw new DAOException("Operazione non consentita: l'utente non è un veterinario.");
                }
                try (var keys = statement.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
                throw new DAOException("Inserimento controllo non ha prodotto un ID");
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP10 - Storico controlli di un animale
        public static List<ControlloSanitario> storicoByAnimale(Connection connection, int idAnimale) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_CONTROLLI_BY_ANIMALE, idAnimale);
                 var rs = statement.executeQuery()) {
                var controlli = new ArrayList<ControlloSanitario>();
                while (rs.next()) {
                    controlli.add(new ControlloSanitario(
                        rs.getInt("ID_controllo"),
                        rs.getDate("data").toLocalDate(),
                        rs.getTime("ora").toLocalTime(),
                        rs.getString("tipologia"),
                        rs.getString("esito"),
                        rs.getInt("ID_animale"),
                        rs.getInt("ID_veterinario")
                    ));
                }
                return controlli;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // Statistiche - conta controlli ultimi 30 giorni
        public static int contaUltimi30Giorni(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.CONTA_CONTROLLI_ULTIMI_30_GIORNI);
                 var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("totale");
                }
                return 0;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}