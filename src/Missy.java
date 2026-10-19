import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JFrame;

import Components.Stages.Main;
import Components.Tasks.Database;
import Components.UIComponents.Panel;

import java.util.Scanner;

public class Missy {

    // private static final String URL = "jdbc:mysql://localhost:3306/missytodo";
    // private static final String USER = "root";
    // private static final String PASSWORD = "sm8zerxZ!%Du5i,";

    public static void main(String [] args) throws SQLException{

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Panel panel = new Panel();
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.requestFocusInWindow();  


        Main main = new Main();
        Database db = new Database();
        Scanner in = new Scanner(System.in);

        db.table_init();

        while(true){

        System.out.print("Enter task name: ");
        String task = in.nextLine();

        System.out.print("Enter task deadline (YYYY-MM-DD): ");
        LocalDate deadline = LocalDate.parse(in.nextLine());

        System.out.print("Enter task priority (LOW, MEDIUM, HIGH): ");
        String priority = in.nextLine();

        System.out.print("Enter task description: ");
        String desc = in.nextLine();

        main.add_query(task, deadline, priority, desc);
        main.display_query();

    }
}
}
