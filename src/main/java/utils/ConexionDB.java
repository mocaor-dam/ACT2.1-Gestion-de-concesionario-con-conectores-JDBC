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
     *
     * @return Devuelve una conexión activa a la base de datos.
     * @throws SQLException
     * @throws AppException
     */
    public static Connection getConnection() throws SQLException, AppException {
        PropertiesReader p = PropertiesReader.getInstance();
        //Comprobamos que la opcion no sea -1, ya que yo he asociado a -1 como que no se ha seleccionado ninguna opcion
        if (opcionActual == -1){
            throw new SQLException("Primero debes conectar a una base de datos en la opcion 1");
        }

        // Si la conexión no existe o está cerrada, creamos una nueva
        if (conexion == null || conexion.isClosed()) {
            if (opcionActual == 1){
                conexion = DriverManager.getConnection(p.get("mysql.url"), p.get("mysql.user"), p.get("mysql.password"));
            } else if (opcionActual == 2){
                conexion = DriverManager.getConnection(p.get("sqlite.url"));
                try (Statement statement = conexion.createStatement()){
                    //Activamos las foreign keys en MySQLite ya que por defecto vienen desactivadas
                    statement.execute("PRAGMA foreign_keys = ON");
                }
            }
        }
        return conexion;
    }

    /**
     * Metodo encargado de la configuracion de la conexion
     * @param opcion 1(MySql), 2(SQLite)
     * @throws SQLException
     */
    public static void configurarConexion(int opcion) throws SQLException, AppException {
        //Comprobamos que la conexion no sea nulo y que no este cerrada para cerrarla
        if (conexion != null && !conexion.isClosed()){
            conexion.close();
        }
        //Y una vez cerrada ponemos como opcion actual la opcion pasada por parametro
        opcionActual = opcion;
        //Llamamos al metodo para obtener la conexion
        getConnection();
    }

    public static int getOpcionActual() {
        return opcionActual;
    }

    /**
     * Metodo encargado de cerrar la conexion
     * @throws SQLException
     */
    public static void cerrarConexion() throws SQLException {
        conexion.close();
    }
}