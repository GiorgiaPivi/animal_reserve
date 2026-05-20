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

public final class Recinto {

    public final int id;
    public final String tipologia;

    public Recinto(int id, String tipologia) {
        this.id = id;
        this.tipologia = tipologia == null ? "" : tipologia;
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
            Printer.field("tipologia", this.tipologia)
        ));
    }

    public static final class DAO {

        public static List<Recinto> list(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_RECINTI);
                 var rs = statement.executeQuery()) {
                var recinti = new ArrayList<Recinto>();
                while (rs.next()) {
                    recinti.add(new Recinto(rs.getInt("ID_recinto"), rs.getString("tipologia")));
                }
                return recinti;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}
