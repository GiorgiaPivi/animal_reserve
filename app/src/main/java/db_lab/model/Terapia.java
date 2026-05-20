package db_lab.model;

import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.Printer;
import db_lab.data.Queries;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Terapia {

    public final int id;
    public final String farmaco;
    public final String dosaggio;
    public final String durata;
    public final LocalDate dataInizio;
    public final LocalDate dataFine;
    public final int idControllo;

    public Terapia(int id, String farmaco, String dosaggio, String durata,
                   LocalDate dataInizio, LocalDate dataFine, int idControllo) {
        this.id = id;
        this.farmaco = farmaco == null ? "" : farmaco;
        this.dosaggio = dosaggio == null ? "" : dosaggio;
        this.durata = durata == null ? "" : durata;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.idControllo = idControllo;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Terapia t) {
            return t.id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return Printer.stringify("Terapia", List.of(
            Printer.field("id", this.id),
            Printer.field("farmaco", this.farmaco),
            Printer.field("dal", this.dataInizio),
            Printer.field("al", this.dataFine)
        ));
    }

    public static final class DAO {

        // OP11 - Prescrive una nuova terapia, restituisce l'ID generato
        public static int insert(Connection connection, String farmaco, String dosaggio,
                String durata, LocalDate dataInizio, LocalDate dataFine, int idControllo) {
            try (var statement = connection.prepareStatement(
                    Queries.INSERT_TERAPIA, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, farmaco);
                statement.setString(2, dosaggio);
                statement.setString(3, durata);
                statement.setDate(4, Date.valueOf(dataInizio));
                statement.setDate(5, Date.valueOf(dataFine));
                statement.setInt(6, idControllo);
                statement.executeUpdate();
                try (var keys = statement.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
                throw new DAOException("Inserimento terapia non ha prodotto un ID");
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static List<Terapia> byControllo(Connection connection, int idControllo) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_TERAPIE_BY_CONTROLLO, idControllo);
                 var rs = statement.executeQuery()) {
                var terapie = new ArrayList<Terapia>();
                while (rs.next()) {
                    terapie.add(new Terapia(
                        rs.getInt("ID_terapia"),
                        rs.getString("farmaco"),
                        rs.getString("dosaggio"),
                        rs.getString("durata"),
                        rs.getDate("data_inizio").toLocalDate(),
                        rs.getDate("data_fine").toLocalDate(),
                        rs.getInt("ID_controllo")
                    ));
                }
                return terapie;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}
