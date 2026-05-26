package db_lab.model;

import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.Printer;
import db_lab.data.Queries;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Turno {

    public final LocalDate data;
    public final String fascia;

    public Turno(LocalDate data, String fascia) {
        this.data = data;
        this.fascia = fascia == null ? "" : fascia;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Turno t) {
            return t.data.equals(this.data) && t.fascia.equals(this.fascia);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data, this.fascia);
    }

    @Override
    public String toString() {
        return Printer.stringify("Turno", List.of(
            Printer.field("data", this.data),
            Printer.field("fascia", this.fascia)
        ));
    }

    // ---------------- DAO ----------------

    public static final class DAO {

        /** Inserisce un nuovo turno (data + fascia) */
        public static void insert(Connection connection, LocalDate data, String fascia) {
            try (var stmt = connection.prepareStatement(Queries.INSERT_TURNO)) {
                stmt.setDate(1, Date.valueOf(data));
                stmt.setString(2, fascia);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        /** Assegna un turno a un utente (relazione SVOLGIMENTO) */
        public static void assegna(Connection connection, int idUtente, LocalDate data, String fascia) {
            try (var stmt = connection.prepareStatement(Queries.ASSIGN_TURNO)) {
                stmt.setInt(1, idUtente);
                stmt.setDate(2, Date.valueOf(data));
                stmt.setString(3, fascia);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        /** Lista tutti i turni presenti nel sistema */
        public static List<Turno> list(Connection connection) {
            try (var stmt = DAOUtils.prepare(connection, Queries.LIST_TURNI);
                 var rs = stmt.executeQuery()) {

                var turni = new ArrayList<Turno>();
                while (rs.next()) {
                    turni.add(new Turno(
                        rs.getDate("data").toLocalDate(),
                        rs.getString("fascia_oraria")
                    ));
                }
                return turni;

            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}
