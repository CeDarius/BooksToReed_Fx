package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnJDBC {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/booktoread", "root", "password");
            System.out.println(("Connected"));
        } catch (SQLException e){
            System.out.println("Not Connected!");
            e.printStackTrace();
        }
    }
}
