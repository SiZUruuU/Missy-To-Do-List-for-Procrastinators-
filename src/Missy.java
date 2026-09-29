import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;

import Components.UIComponents.Panel;

public class Missy {

    private static final String URL = "jdbc:mysql://localhost:3306/missytodo";
    private static final String USER = "root";
    private static final String PASSWORD = "sm8zerxZ!%Du5i,";
    public static void main(String [] args){

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Panel panel = new Panel();
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected: " + !conn.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
