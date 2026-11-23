package utils;

import exceptions.AppException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static Connection conexion = null;
    private static int opcionActual = -1;

    // Privado para evitar instanciación externa (Patrón Singleton)
    private ConexionDB() {}

    /**
     * Devuelve una conexión activa a la base de datos.
     */
    public static Connection getConnection() throws SQLException, AppException {
        PropertiesReader p = PropertiesReader.getInstance();
        if (opcionActual == -1){
            throw new SQLException("Primero debes conectar a una base de datos en la opcion1");
        }

        // Si la conexión no existe o está cerrada, creamos una nueva
        if (conexion == null || conexion.isClosed()) {
            if (opcionActual == 1){
                conexion = DriverManager.getConnection(p.get("mysql.url"), p.get("mysql.user"), p.get("mysql.password"));
            } else if (opcionActual == 2){
                conexion = DriverManager.getConnection(p.get("sqlite.url"));
                try (Statement statement = conexion.createStatement()){
                    statement.execute("PRAGMA foreign_keys = ON");
                }
            }
        }
        return conexion;
    }

    /**
     *
     * @param opcion 1(MySql), 2(SQLite)
     * @throws SQLException
     */
    public static void configurarConexion(int opcion) throws SQLException, AppException {
        if (conexion != null && !conexion.isClosed()){
            conexion.close();
        }
        opcionActual = opcion;
        getConnection();
    }

    public static int getOpcionActual() {
        return opcionActual;
    }

    public static void cerrarConexion() throws SQLException {
        conexion.close();
    }
}