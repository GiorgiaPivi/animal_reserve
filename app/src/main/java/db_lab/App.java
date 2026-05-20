package db_lab;

import db_lab.data.DAOUtils;
import db_lab.model.Model;

public final class App {

    public static void main(String[] args) throws Exception {
        // Per testare l'applicazione senza database, usa il model mockato:
        //
        // var model = Model.mock();
        // var view = new View(() -> {});
        //
        var connection = DAOUtils.localMySQLConnection("zoo_db", "root", "");
        var model = Model.fromConnection(connection);
        var view = new View(() -> {
            // Chiudiamo la connessione quando l'utente chiude la finestra.
            try {
                connection.close();
            } catch (Exception ignored) {}
        });
        var controller = new Controller(model, view);
        view.setController(controller);
        controller.userRequestedInitialPage();
    }
}
