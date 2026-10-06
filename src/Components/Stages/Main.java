/**
 * THE MAIN WINDOW WHERE THINGS HAPPEN MAINLY FRFRFR
 */
package Components.Stages;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import Components.Tasks.Database;

public class Main {

    Scanner in = new Scanner(System.in);
    Database db = new Database();
    int id = 0;

    //TESTING FOR DATABASE CONNECTION
    public void add_query(String title, LocalDate deadline, String priority) throws SQLException {
        id = db.counts();
        db.add(title, deadline, priority);
    }

    public void delete_query(int id) throws SQLException {
        db.delete(id);
    }

    public void display_query() throws SQLException {
        db.printTable("Test " + id);
    }
}
