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

public final class Movimentazione {
    public final int id;
    public final LocalDate dataMovimentazione;
    public final int idAnimale;
    public final int idRecintoDestinazione;
    public final String tipoRecinto;
    public final int capienza;

    public Movimentazione(
        int id,
        LocalDate dataMovimentazione,
        int idAnimale,
        int idRecintoDestinazione,
        String tipoRecinto,
        int capienza
    ) {
        this.id = id;
        this.dataMovimentazione = dataMovimentazione;
        this.idAnimale = idAnimale;
        this.idRecintoDestinazione = idRecintoDestinazione;
        this.tipoRecinto = tipoRecinto;
        this.capienza = capienza;
    }

    public static Movimentazione fromResultSet(ResultSet rs) throws SQLException {
        return new Movimentazione(
            rs.getInt("ID_movimentazione"),
            rs.getDate("data_movimentazione").toLocalDate(),
            rs.getInt("ID_animale"),
            rs.getInt("ID_recinto_destinazione"),
            rs.getString("tipo_recinto"),
            rs.getInt("capienza")
        );
    }

    @Override
    public String toString() {
        return "Movimentazione[" +
            "id=" + id +
            ", data=" + dataMovimentazione +
            ", recintoDestinazione=" + idRecintoDestinazione +
            ", tipo=" + tipoRecinto +
            ']';
    }

    public static final class DAO {

        public static int insert(Connection conn, LocalDate data, int idAnimale, int idRecintoDestinazione) {
            try (var stmt = conn.prepareStatement(Queries.INSERT_MOVIMENTAZIONE, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, Date.valueOf(data));
                stmt.setInt(2, idAnimale);
                stmt.setInt(3, idRecintoDestinazione);
                
                stmt.executeUpdate();
                var rs = stmt.getGeneratedKeys();
                
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Inserimento movimentazione fallito");
            } catch (SQLException e) {
                throw new RuntimeException("Errore inserimento movimentazione", e);
            }
        }

        public static List<Movimentazione> listByAnimale(Connection conn, int idAnimale) {
            try (var stmt = conn.prepareStatement(Queries.LIST_MOVIMENTAZIONI_BY_ANIMALE)) {
                stmt.setInt(1, idAnimale);
                var rs = stmt.executeQuery();
                
                var list = new ArrayList<Movimentazione>();
                while (rs.next()) {
                    list.add(fromResultSet(rs));
                }
                return list;
            } catch (SQLException e) {
                throw new RuntimeException("Errore caricamento movimentazioni", e);
            }
        }
    }
}