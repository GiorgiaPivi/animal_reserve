package db_lab.model;

import db_lab.data.Queries;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class TrasportoEsterno {
    public final int id;
    public final LocalDate dataTrasporto;
    public final String destinazione;
    public final String motivazione;
    public final int idAnimale;
    public final int idVolontario;
    public final String nomeVolontario;
    public final String cognomeVolontario;
    public final String nomeAnimale;

    public TrasportoEsterno(
        int id,
        LocalDate dataTrasporto,
        String destinazione,
        String motivazione,
        int idAnimale,
        int idVolontario,
        String nomeVolontario,
        String cognomeVolontario,
        String nomeAnimale
    ) {
        this.id = id;
        this.dataTrasporto = dataTrasporto;
        this.destinazione = destinazione;
        this.motivazione = motivazione;
        this.idAnimale = idAnimale;
        this.idVolontario = idVolontario;
        this.nomeVolontario = nomeVolontario;
        this.cognomeVolontario = cognomeVolontario;
        this.nomeAnimale = nomeAnimale;
    }

    public static TrasportoEsterno fromResultSet(ResultSet rs) throws SQLException {
        return new TrasportoEsterno(
            rs.getInt("ID_trasporto"),
            rs.getDate("data_trasporto").toLocalDate(),
            rs.getString("destinazione"),
            rs.getString("motivazione"),
            rs.getInt("ID_animale"),
            rs.getInt("ID_volontario"),
            rs.getString("nome_volontario"),
            rs.getString("cognome_volontario"),
            rs.getString("nome_animale")
        );
    }

    public static TrasportoEsterno fromResultSetSimple(ResultSet rs) throws SQLException {
        return new TrasportoEsterno(
            rs.getInt("ID_trasporto"),
            rs.getDate("data_trasporto").toLocalDate(),
            rs.getString("destinazione"),
            rs.getString("motivazione"),
            rs.getInt("ID_animale"),
            rs.getInt("ID_volontario"),
            rs.getString("nome_volontario"),
            rs.getString("cognome_volontario"),
            null
        );
    }

    @Override
    public String toString() {
        return "TrasportoEsterno[" +
            "id=" + id +
            ", data=" + dataTrasporto +
            ", destinazione=" + destinazione +
            ", motivazione=" + motivazione +
            ", volontario=" + nomeVolontario + " " + cognomeVolontario +
            ']';
    }

    public static final class DAO {

        public static int insert(Connection conn, LocalDate data, String destinazione, 
                                 String motivazione, int idAnimale, int idVolontario) {
            try (var stmt = conn.prepareStatement(Queries.INSERT_TRASPORTO, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, Date.valueOf(data));
                stmt.setString(2, destinazione);
                stmt.setString(3, motivazione);
                stmt.setInt(4, idAnimale);
                stmt.setInt(5, idVolontario);
                
                stmt.executeUpdate();
                var rs = stmt.getGeneratedKeys();
                
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Inserimento trasporto fallito");
            } catch (SQLException e) {
                throw new RuntimeException("Errore inserimento trasporto", e);
            }
        }

        public static List<TrasportoEsterno> listByAnimale(Connection conn, int idAnimale) {
            try (var stmt = conn.prepareStatement(Queries.LIST_TRASPORTI_BY_ANIMALE)) {
                stmt.setInt(1, idAnimale);
                var rs = stmt.executeQuery();
                
                var list = new ArrayList<TrasportoEsterno>();
                while (rs.next()) {
                    list.add(fromResultSetSimple(rs));
                }
                return list;
            } catch (SQLException e) {
                throw new RuntimeException("Errore caricamento trasporti", e);
            }
        }

        public static List<TrasportoEsterno> listAll(Connection conn) {
            try (var stmt = conn.prepareStatement(Queries.LIST_ALL_TRASPORTI)) {
                var rs = stmt.executeQuery();
                
                var list = new ArrayList<TrasportoEsterno>();
                while (rs.next()) {
                    list.add(fromResultSet(rs));
                }
                return list;
            } catch (SQLException e) {
                throw new RuntimeException("Errore caricamento tutti i trasporti", e);
            }
        }
    }
}