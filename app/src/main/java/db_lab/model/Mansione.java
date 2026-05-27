package db_lab.model;

import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.Printer;
import db_lab.data.Queries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Mansione {

    public final int id;
    public final String descrizione;

    public Mansione(int id, String descrizione) {
        this.id = id;
        this.descrizione = descrizione == null ? "" : descrizione;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Mansione m) {
            return m.id == this.id && m.descrizione.equals(this.descrizione);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.descrizione);
    }

    @Override
    public String toString() {
        return Printer.stringify("Mansione", List.of(
            Printer.field("id", this.id),
            Printer.field("descrizione", this.descrizione)
        ));
    }

    // ---------------- DAO ----------------

    public static final class DAO {

        /** Inserisce una nuova mansione */
        public static void insert(Connection connection, String descrizione) {
            try (var stmt = connection.prepareStatement(Queries.INSERT_MANSIONE)) {
                stmt.setString(1, descrizione);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        /** Lista tutte le mansioni presenti nel sistema */
        public static List<Mansione> list(Connection connection) {
            try (var stmt = DAOUtils.prepare(connection, Queries.LIST_MANSIONI);
                 var rs = stmt.executeQuery()) {

                var mansioni = new ArrayList<Mansione>();
                while (rs.next()) {
                    mansioni.add(new Mansione(
                        rs.getInt("ID_mansione"),
                        rs.getString("descrizione")
                    ));
                }
                return mansioni;

            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
        public static List<Mansione> listByUtente(Connection connection, int idUtente) {
            try (var stmt = DAOUtils.prepare(connection, Queries.LIST_MANSIONI_BY_UTENTE, idUtente);
                var rs = stmt.executeQuery()) {

                var mansioni = new ArrayList<Mansione>();
                while (rs.next()) {
                    mansioni.add(new Mansione(
                        rs.getInt("ID_mansione"),
                        rs.getString("descrizione")
                    ));
                }
                return mansioni;

            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}

