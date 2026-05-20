package db_lab.data;

import db_lab.model.Animale;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione degli Animali nel database
 * Implementa le operazioni: OP03, OP04, OP05, OP06, OP07, OP08
 */
public class AnimaleDAO {
    
    /**
     * OP03 - Recupera tutti gli animali
     */
    public List<Animale> getAllAnimali() {
        List<Animale> animali = new ArrayList<>();
        String sql = "SELECT A.*, S.nome_specie " +
                     "FROM Animale A " +
                     "JOIN Specie S ON A.ID_specie = S.ID_specie " +
                     "ORDER BY A.nome_animale";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                animali.add(extractAnimaleFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero animali: " + e.getMessage());
        }
        
        return animali;
    }
    
    /**
     * OP04 - Cerca animali per nome
     */
    public List<Animale> searchByNome(String nome) {
        List<Animale> animali = new ArrayList<>();
        String sql = "SELECT * FROM Animale WHERE nome_animale LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nome + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    animali.add(extractAnimaleFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore ricerca animali: " + e.getMessage());
        }
        
        return animali;
    }
    
    /**
     * OP05 - Filtra animali per specie
     */
    public List<Animale> getBySpecie(int idSpecie) {
        List<Animale> animali = new ArrayList<>();
        String sql = "SELECT * FROM Animale WHERE ID_specie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idSpecie);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    animali.add(extractAnimaleFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore filtro specie: " + e.getMessage());
        }
        
        return animali;
    }
    
    /**
     * OP05 - Filtra animali per stato di salute
     */
    public List<Animale> getByStatoSalute(String stato) {
        List<Animale> animali = new ArrayList<>();
        String sql = "SELECT * FROM Animale WHERE stato_di_salute = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, stato);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    animali.add(extractAnimaleFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore filtro stato: " + e.getMessage());
        }
        
        return animali;
    }
    
    /**
     * OP06 - Registra nuovo animale
     */
    public boolean insertAnimale(Animale animale) {
        String sql = "INSERT INTO Animale (nome_animale, eta, provenienza, stato_di_salute, " +
                     "descrizione, data_arrivo, ID_specie, ID_recinto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, animale.getNomeAnimale());
            pstmt.setInt(2, animale.getEta());
            pstmt.setString(3, animale.getProvenienza());
            pstmt.setString(4, animale.getStatoDiSalute());
            pstmt.setString(5, animale.getDescrizione());
            pstmt.setDate(6, Date.valueOf(animale.getDataArrivo()));
            pstmt.setInt(7, animale.getIdSpecie());
            
            if (animale.getIdRecinto() != null) {
                pstmt.setInt(8, animale.getIdRecinto());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Recupera l'ID generato
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        animale.setIdAnimale(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Errore inserimento animale: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * OP07 - Aggiorna informazioni animale
     */
    public boolean updateAnimale(Animale animale) {
        String sql = "UPDATE Animale SET nome_animale=?, eta=?, provenienza=?, " +
                     "stato_di_salute=?, descrizione=?, ID_recinto=? WHERE ID_animale=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, animale.getNomeAnimale());
            pstmt.setInt(2, animale.getEta());
            pstmt.setString(3, animale.getProvenienza());
            pstmt.setString(4, animale.getStatoDiSalute());
            pstmt.setString(5, animale.getDescrizione());
            
            if (animale.getIdRecinto() != null) {
                pstmt.setInt(6, animale.getIdRecinto());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            pstmt.setInt(7, animale.getIdAnimale());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento animale: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * OP08 - Animali che necessitano controllo sanitario
     */
    public List<Animale> getAnimaliDaControllare() {
        List<Animale> animali = new ArrayList<>();
        String sql = "SELECT A.*, MAX(CS.data) as ultimo_controllo " +
                     "FROM Animale A " +
                     "LEFT JOIN Controllo_Sanitario CS ON A.ID_animale = CS.ID_animale " +
                     "GROUP BY A.ID_animale " +
                     "HAVING MAX(CS.data) < DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                     "   OR MAX(CS.data) IS NULL " +
                     "ORDER BY ultimo_controllo ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                animali.add(extractAnimaleFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Errore ricerca animali da controllare: " + e.getMessage());
        }
        
        return animali;
    }
    
    /**
     * Recupera un animale per ID
     */
    public Animale getById(int id) {
        String sql = "SELECT * FROM Animale WHERE ID_animale = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAnimaleFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore recupero animale: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Helper per estrarre Animale da ResultSet
     */
    private Animale extractAnimaleFromResultSet(ResultSet rs) throws SQLException {
        Animale animale = new Animale();
        animale.setIdAnimale(rs.getInt("ID_animale"));
        animale.setNomeAnimale(rs.getString("nome_animale"));
        animale.setEta(rs.getInt("eta"));
        animale.setProvenienza(rs.getString("provenienza"));
        animale.setStatoDiSalute(rs.getString("stato_di_salute"));
        animale.setDescrizione(rs.getString("descrizione"));
        animale.setDataArrivo(rs.getDate("data_arrivo").toLocalDate());
        animale.setIdSpecie(rs.getInt("ID_specie"));
        
        int idRecinto = rs.getInt("ID_recinto");
        if (!rs.wasNull()) {
            animale.setIdRecinto(idRecinto);
        }
        
        return animale;
    }
}
