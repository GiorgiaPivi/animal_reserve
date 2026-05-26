package db_lab.model;

import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.Printer;
import db_lab.data.Queries;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Utente {

    public final int id;
    public final String nome;
    public final String cognome;
    public final String email;
    public final String password;
    public final String ruolo; // visitatore, volontario, veterinario

    public Utente(int id, String nome, String cognome, String email, String password, String ruolo) {
        this.id = id;
        this.nome = nome == null ? "" : nome;
        this.cognome = cognome == null ? "" : cognome;
        this.email = email == null ? "" : email;
        this.password = password == null ? "" : password;
        this.ruolo = ruolo == null ? "" : ruolo;
    }

    public boolean isVeterinario() {
        return "veterinario".equalsIgnoreCase(this.ruolo);
    }

    public boolean isVolontario() {
        return "volontario".equalsIgnoreCase(this.ruolo);
    }

    public boolean isVisitatore() {
        return "visitatore".equalsIgnoreCase(this.ruolo);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof Utente u) {
            return u.id == this.id
                && u.email.equals(this.email)
                && u.ruolo.equals(this.ruolo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.email);
    }

    @Override
    public String toString() {
        return Printer.stringify("Utente", List.of(
            Printer.field("id", this.id),
            Printer.field("nome", this.nome + " " + this.cognome),
            Printer.field("email", this.email),
            Printer.field("ruolo", this.ruolo)
        ));
    }

    public static final class DAO {

        // OP02 - Login: cerca utente per email e password
        public static Optional<Utente> login(Connection connection, String email, String password) {
            try (var statement = DAOUtils.prepare(connection, Queries.FIND_UTENTE_BY_CREDENTIALS,
                    email, password);
                 var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Utente(
                        rs.getInt("ID_utente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("ruolo")
                    ));
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // OP01 - Registrazione nuovo visitatore
        public static Optional<Utente> registra(Connection connection,
                String nome, String cognome, String email, String password) {
            try (var statement = connection.prepareStatement(
                    Queries.INSERT_UTENTE, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, nome);
                statement.setString(2, cognome);
                statement.setString(3, email);
                statement.setString(4, password);
                statement.setString(5, "visitatore");
                statement.executeUpdate();
                try (var keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        return Optional.of(new Utente(id, nome, cognome, email, password, "visitatore"));
                    }
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

        // Lista tutti gli utenti
        public static List<Utente> list(Connection connection) {
            try (var statement = DAOUtils.prepare(connection, Queries.LIST_UTENTI);
                 var rs = statement.executeQuery()) {
                var utenti = new ArrayList<Utente>();
                while (rs.next()) {
                    utenti.add(new Utente(
                        rs.getInt("ID_utente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("ruolo")
                    ));
                }
                return utenti;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }

    public boolean isAdmin() {
        return !ruolo.equalsIgnoreCase("visitatore");
    }

}
