package db_lab;

import db_lab.data.DAOUtils;
import db_lab.model.Model;

public final class App {

    public static void main(String[] args) throws Exception {

        // var connection = DAOUtils.localMySQLConnection("animal_reserve", "root", "");
        // var model = Model.fromConnection(connection);

        var model = Model.mock(); 

        View view = new View();
        view.loginPage();

        var controller = new Controller(model, view);
        view.setController(controller);

        controller.userRequestedInitialPage();
    }
}

