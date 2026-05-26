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

public final class Specie {

    public final int id;
    public final String nome;

    public Specie(int id, String nome) {
        this.id = id;
        this.nome = nome == null ? "" : nome;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Specie s) {
            return s.id == this.id && s.nome.equals(this.nome);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.nome);
    }

    @Override
    public String toString() {
        return Printer.stringify("Specie", List.of(
            Printer.field("id", this.id),
            Printer.field("nome", this.nome)
        ));
    }

    public static final class DAO {

        public static List<Specie> list(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_SPECIE);
                 var rs = statement.executeQuery()) {
                var specie = new ArrayList<Specie>();
                while (rs.next()) {
                    specie.add(new Specie(rs.getInt("ID_specie"), rs.getString("nome_specie")));
                }
                return specie;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static int contaAnimali(Connection connection, int idSpecie) {
            try (var statement = DAOUtils.prepare(connection, Queries.COUNT_ANIMALI_PER_SPECIE, idSpecie);
                 var rs = statement.executeQuery()) {
                if (rs.next()) return rs.getInt("totale");
                return 0;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        public static void insert(Connection connection, String nome) {
            try (var statement = connection.prepareStatement(Queries.INSERT_SPECIE)) {
                statement.setString(1, nome);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }
}
