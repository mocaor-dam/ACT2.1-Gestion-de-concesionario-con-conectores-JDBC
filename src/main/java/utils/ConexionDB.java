package utils;

import exceptions.AppException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {



    public static Connection conectarDB(int opcion) throws AppException {
        String url_sql = PropertiesReader.getInstance().get("mysql.url");
        String user_sql = PropertiesReader.getInstance().get("mysql.user");
        String password_sql = PropertiesReader.getInstance().get("mysql.password");

        String url_sqlLite = PropertiesReader.getInstance().get("sqlite.url");



        if (opcion == 1){
           try (connection = DriverManager.getConnection(url_sql, user_sql, password_sql)){

           } catch (SQLException e) {
               System.out.println("Error al conectar con la base de datos(SQL)");
           }
        } else if (opcion == 2) {
            try (Connection connection = DriverManager.getConnection(url_sqlLite)){

            } catch (SQLException e) {
                System.out.println("Error al conectar con la base de datos(MySqlLite)");
            }
        }

        return connection;
    }
}
