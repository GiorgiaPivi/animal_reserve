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
import java.util.Optional;

public final class Recinto {

    public final int id;
    public final String tipologia;
    public final int capienza;
    public final int occupazione;

    public Recinto(int id, String tipologia, int capienza, int occupazione) {
        this.id = id;
        this.tipologia = tipologia == null ? "" : tipologia;
        this.capienza = capienza;
        this.occupazione = occupazione;
    }

    public Recinto(int id, String tipologia) {
        this(id, tipologia, 0, 0);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Recinto r) {
            return r.id == this.id && r.tipologia.equals(this.tipologia);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.tipologia);
    }

    @Override
    public String toString() {
        return Printer.stringify("Recinto", List.of(
            Printer.field("id", this.id),
            Printer.field("tipologia", this.tipologia),
            Printer.field("capienza", this.capienza),
            Printer.field("occupazione", this.occupazione)
        ));
    }

    public static final class DAO {

        public static List<Recinto> list(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_RECINTI);
                 var rs = statement.executeQuery()) {
                var recinti = new ArrayList<Recinto>();
                while (rs.next()) {
                    recinti.add(new Recinto(
                        rs.getInt("ID_recinto"), 
                        rs.getString("tipo_recinto"),
                        rs.getInt("capienza"),
                        0
                    ));
                }
                return recinti;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static List<Recinto> disponibili(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.RECINTI_DISPONIBILI);
                 var rs = statement.executeQuery()) {
                var recinti = new ArrayList<Recinto>();
                while (rs.next()) {
                    recinti.add(new Recinto(
                        rs.getInt("ID_recinto"),
                        rs.getString("tipo_recinto"),
                        rs.getInt("capienza"),
                        rs.getInt("occupazione")
                    ));
                }
                return recinti;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static Optional<Recinto> find(Connection connection, int idRecinto) {
            try (var stmt = DAOUtils.prepare(connection, Queries.FIND_RECINTO_BY_ID)) {
                stmt.setInt(1, idRecinto);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(new Recinto(
                        rs.getInt("ID_recinto"),
                        rs.getString("tipo_recinto"),
                        rs.getInt("capienza"),
                        0
                    ));
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static int contaAnimali(Connection connection, int idRecinto) {
            try (var stmt = DAOUtils.prepare(connection, Queries.COUNT_ANIMALI_IN_RECINTO)) {
                stmt.setInt(1, idRecinto);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("totale");
                }
                return 0;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static void insert(Connection connection, String tipologia) {
            try (var statement = connection.prepareStatement(Queries.INSERT_RECINTO)) {
                statement.setString(1, tipologia);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

    }
}