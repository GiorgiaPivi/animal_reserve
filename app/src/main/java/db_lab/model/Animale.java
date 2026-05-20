package db_lab.model;

import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.Printer;
import db_lab.data.Queries;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Animale {

    public final int id;
    public final String nome;
    public final int eta;
    public final String provenienza;
    public final String statoDiSalute;
    public final String descrizione;
    public final LocalDate dataArrivo;
    public final int idSpecie;
    public final String nomeSpecie;
    public final Optional<Integer> idRecinto;

    public Animale(int id, String nome, int eta, String provenienza,
                   String statoDiSalute, String descrizione, LocalDate dataArrivo,
                   int idSpecie, String nomeSpecie, Optional<Integer> idRecinto) {
        this.id = id;
        this.nome = nome == null ? "" : nome;
        this.eta = eta;
        this.provenienza = provenienza == null ? "" : provenienza;
        this.statoDiSalute = statoDiSalute == null ? "" : statoDiSalute;
        this.descrizione = descrizione == null ? "" : descrizione;
        this.dataArrivo = dataArrivo;
        this.idSpecie = idSpecie;
        this.nomeSpecie = nomeSpecie == null ? "" : nomeSpecie;
        this.idRecinto = idRecinto == null ? Optional.empty() : idRecinto;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Animale a) {
            return a.id == this.id && a.nome.equals(this.nome);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.nome);
    }

    @Override
    public String toString() {
        return Printer.stringify("Animale", List.of(
            Printer.field("id", this.id),
            Printer.field("nome", this.nome),
            Printer.field("eta", this.eta),
            Printer.field("stato", this.statoDiSalute),
            Printer.field("specie", this.nomeSpecie),
            Printer.field("arrivo", this.dataArrivo)
        ));
    }

    public static final class DAO {

        // OP03 - Lista tutti gli animali
        public static List<Animale> list(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_ANIMALI);
                 var rs = statement.executeQuery()) {
                var animali = new ArrayList<Animale>();
                while (rs.next()) {
                    animali.add(fromResultSet(rs));
                }
                return animali;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP04 - Cerca animali per nome (like)
        public static List<Animale> searchByNome(Connection connection, String nome) {
            try (var statement = DAOUtils.prepare(connection, Queries.SEARCH_ANIMALI_BY_NOME,
                    "%" + nome + "%");
                 var rs = statement.executeQuery()) {
                var animali = new ArrayList<Animale>();
                while (rs.next()) {
                    animali.add(fromResultSet(rs));
                }
                return animali;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP05 - Filtra per stato di salute
        public static List<Animale> filterByStato(Connection connection, String stato) {
            try (var statement = DAOUtils.prepare(connection, Queries.FILTER_ANIMALI_BY_STATO, stato);
                 var rs = statement.executeQuery()) {
                var animali = new ArrayList<Animale>();
                while (rs.next()) {
                    animali.add(fromResultSet(rs));
                }
                return animali;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // Trova animale per ID
        public static Optional<Animale> find(Connection connection, int id) {
            try (var statement = DAOUtils.prepare(connection, Queries.FIND_ANIMALE, id);
                 var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(fromResultSet(rs));
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP06 - Inserisce un nuovo animale, restituisce l'ID generato
        public static int insert(Connection connection, String nome, int eta, String provenienza,
                String statoDiSalute, String descrizione, LocalDate dataArrivo,
                int idSpecie, Optional<Integer> idRecinto) {
            try (var statement = connection.prepareStatement(
                    Queries.INSERT_ANIMALE, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, nome);
                statement.setInt(2, eta);
                statement.setString(3, provenienza);
                statement.setString(4, statoDiSalute);
                statement.setString(5, descrizione);
                statement.setDate(6, Date.valueOf(dataArrivo));
                statement.setInt(7, idSpecie);
                if (idRecinto.isPresent()) {
                    statement.setInt(8, idRecinto.get());
                } else {
                    statement.setNull(8, Types.INTEGER);
                }
                statement.executeUpdate();
                try (var keys = statement.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
                throw new DAOException("Inserimento animale non ha prodotto un ID");
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP07 - Aggiorna lo stato di salute
        public static void updateStato(Connection connection, int id, String nuovoStato) {
            try (var statement = DAOUtils.prepare(connection, Queries.UPDATE_ANIMALE_STATO,
                    nuovoStato, id)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP08 - Animali che necessitano controllo (> 30 giorni o mai controllati)
        public static List<Animale> daControllare(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.ANIMALI_DA_CONTROLLARE);
                 var rs = statement.executeQuery()) {
                var animali = new ArrayList<Animale>();
                while (rs.next()) {
                    animali.add(fromResultSet(rs));
                }
                return animali;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        private static Animale fromResultSet(java.sql.ResultSet rs) throws SQLException {
            int idRecinto = rs.getInt("ID_recinto");
            Optional<Integer> recinto = rs.wasNull() ? Optional.empty() : Optional.of(idRecinto);
            return new Animale(
                rs.getInt("ID_animale"),
                rs.getString("nome_animale"),
                rs.getInt("eta"),
                rs.getString("provenienza"),
                rs.getString("stato_di_salute"),
                rs.getString("descrizione"),
                rs.getDate("data_arrivo").toLocalDate(),
                rs.getInt("ID_specie"),
                rs.getString("nome_specie"),
                recinto
            );
        }
    }
}
