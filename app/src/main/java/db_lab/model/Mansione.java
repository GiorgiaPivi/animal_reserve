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
    public final String tipoMansione;  // NUOVO CAMPO

    public Mansione(int id, String descrizione) {
        this(id, descrizione, "volontario");  // Default a volontario per compatibilità
    }

    public Mansione(int id, String descrizione, String tipoMansione) {
        this.id = id;
        this.descrizione = descrizione == null ? "" : descrizione;
        this.tipoMansione = tipoMansione == null ? "volontario" : tipoMansione;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Mansione m) {
            return m.id == this.id && m.descrizione.equals(this.descrizione) && 
                   m.tipoMansione.equals(this.tipoMansione);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.descrizione, this.tipoMansione);
    }

    @Override
    public String toString() {
        return Printer.stringify("Mansione", List.of(
            Printer.field("id", this.id),
            Printer.field("descrizione", this.descrizione),
            Printer.field("tipo", this.tipoMansione)
        ));
    }

    // ---------------- DAO ----------------

    public static final class DAO {

        /** Inserisce una nuova mansione */
       public static void insert(Connection connection, String descrizione, String tipoMansione) {
            try (var stmt = connection.prepareStatement(Queries.INSERT_MANSIONE)) {
                stmt.setString(1, descrizione);
                stmt.setString(2, tipoMansione);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static void affida(Connection connection, int idUtente, int idMansione) {
            // Verifica che la mansione sia di tipo 'volontario'
            try (var checkStmt = DAOUtils.prepare(connection, 
                    "SELECT tipo_mansione FROM Mansione WHERE ID_mansione = ?", idMansione);
                var rs = checkStmt.executeQuery()) {
                
                if (!rs.next()) {
                    throw new DAOException("Mansione non trovata.");
                }
                
                String tipoMansione = rs.getString("tipo_mansione");
                if (!"volontario".equals(tipoMansione)) {
                    throw new DAOException("Questa mansione è riservata ai veterinari.");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
            
            // Assegna la mansione al volontario
            try (var stmt = connection.prepareStatement(Queries.INSERT_AFFIDATO)) {
                stmt.setInt(1, idUtente);
                stmt.setInt(2, idMansione);
                stmt.setInt(3, idUtente);
                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new DAOException("Operazione non consentita: l'utente non è un volontario.");
                }
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
                        rs.getString("descrizione"),
                        rs.getString("tipo_mansione")  // LEGGI IL TIPO
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
                        rs.getString("descrizione"),
                        rs.getString("tipo_mansione")  // LEGGI IL TIPO
                    ));
                }
                return mansioni;

            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}