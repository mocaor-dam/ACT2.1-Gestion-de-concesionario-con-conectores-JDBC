package service;

import exceptions.AppException;
import model.Coche;
import model.Propietario;
import utils.ConexionDB;
import utils.PropertiesReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionBBDD {

    public static void inicializarTablas() throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        int motor = ConexionDB.getOpcionActual();

        String pkAuto = "";
        if (motor == 1) {
            pkAuto = "INT AUTO_INCREMENT PRIMARY KEY";
        } else if (motor == 2) {
            pkAuto = "INTEGER PRIMARY KEY AUTOINCREMENT";
        }

        String tablaPropietarios = "CREATE TABLE IF NOT EXISTS propietarios (" +
                "id_propietario " + pkAuto + "," +
                "dni VARCHAR(10) NOT NULL UNIQUE,\n" +
                "nombre VARCHAR(100) NOT NULL,\n" +
                "apellidos VARCHAR(150) NOT NULL,\n" +
                "telefono VARCHAR(15)\n" +
                ");";
        String tablaCoches = "CREATE TABLE IF NOT EXISTS coches (\n" +
                "matricula VARCHAR(10) PRIMARY KEY,\n" +
                "marca VARCHAR(50) NOT NULL,\n" +
                "modelo VARCHAR(50) NOT NULL,\n" +
                "extras VARCHAR(255),\n" +
                "precio DECIMAL(10,2) NOT NULL,\n" +
                "id_propietario INT,\n" +
                "FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)\n" +
                ");";

        String tablaTraspasos = "CREATE TABLE IF NOT EXISTS traspasos (\n" +
                "id_traspaso " + pkAuto + "," +
                "matricula_coche VARCHAR(10) NOT NULL,\n" +
                "id_vendedor INT,\n" +
                "id_comprador INT NOT NULL,\n" +
                "monto_economico DECIMAL(10,2) NOT NULL,\n" +
                "FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),\n" +
                "FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario),\n" +
                "FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)\n" +
                ");";

        String cochesPorMarca = "CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50)) " +
                "BEGIN " +
                "SELECT * FROM coches WHERE marca = p_marca;" +
                "END";

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(tablaPropietarios);
            st.executeUpdate(tablaCoches);
            st.executeUpdate(tablaTraspasos);

            if (motor == 1) {
                st.execute("DROP PROCEDURE IF EXISTS sp_coches_por_marca");
                st.execute(cochesPorMarca);
            }
        }
    }


    public static void registrarPropietario(Propietario p) throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();


        String insertarPropiertario = "INSERT INTO propietarios(dni, nombre, apellidos, telefono) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertarPropiertario)) {
            //Sustituimos los ? por el valor
            ps.setString(1, p.getDni());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellidos());
            ps.setString(4, p.getTelefono());

            //Actualizamos la tabla
            ps.executeUpdate();
        }
    }

    public static void insertarCoche(Coche c) throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        String insertarCoche = "INSERT INTO coches(matricula, marca, modelo, extras, precio, id_propietario) VALUES (?, ?, ?, ?, ?, NULL)";

        try (PreparedStatement ps = conn.prepareStatement(insertarCoche)) {

            ps.setString(1, c.getMatricula());
            ps.setString(2, c.getMarca());
            ps.setString(3, c.getModelo());
            ps.setString(4, c.getExtras());
            ps.setDouble(5, c.getPrecio());


            ps.executeUpdate();
        }
    }

    public static List<Coche> listarCochesConcesionario() throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        String consultaListaCoches = "SELECT * FROM coches WHERE id_propietario IS NULL";

        List<Coche> coches = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(consultaListaCoches);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Coche c = new Coche(
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("extras"),
                        rs.getDouble("precio"),
                        null
                );
                coches.add(c);
            }

        }
        return coches;
    }

    public static List<String> listarCochesConPropietario() throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        String consultaListaCochesConPropietarios = "SELECT c.*, p.dni FROM coches c INNER JOIN propietarios p ON c.id_propietario = p.id_propietario";

        List<String> listaADevolver = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(consultaListaCochesConPropietarios);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String resultado = "Matricula: " + rs.getString("matricula") +
                        "| Marca: " + rs.getString("marca") +
                        "| Modelo: " + rs.getString("modelo") +
                        "| Precio: " + rs.getDouble("precio") +
                        "| DNI Propietario: " + rs.getString("dni");
                listaADevolver.add(resultado);
            }
        }
        return listaADevolver;
    }

    public static boolean modificarCoche(Coche c) throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        String updateCoche = "UPDATE coches SET marca = ?, modelo = ?, extras = ?, precio = ? WHERE matricula = ?";

        try (PreparedStatement ps = conn.prepareStatement(updateCoche)) {
            ps.setString(1, c.getMarca());
            ps.setString(2, c.getModelo());
            ps.setString(3, c.getExtras());
            ps.setDouble(4, c.getPrecio());
            ps.setString(5, c.getMatricula());

            return ps.executeUpdate() > 0;
        }
    }

    public static boolean borrarCoche(String matricula) throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        String deleteCoche = "DELETE FROM coches WHERE matricula  = ?";

        try (PreparedStatement ps = conn.prepareStatement(deleteCoche)) {
            ps.setString(1, matricula);

            return ps.executeUpdate() > 0;
        }
    }

    public static void importarCsv() throws AppException, SQLException {
        String rutaCsv = PropertiesReader.getInstance().get("ruta.csv");
        Connection conn = ConexionDB.getConnection();

        //Desactivo el autocommit, que viene por defecto en true
        conn.setAutoCommit(false);

        String contenidoCSV = "INSERT INTO coches (matricula, marca, modelo, extras, precio, id_propietario) VALUES (?, ?, ?, ?, ?, NULL)";

        try (BufferedReader br = Files.newBufferedReader(Path.of(rutaCsv), StandardCharsets.UTF_8);
             PreparedStatement ps = conn.prepareStatement(contenidoCSV)) {

            String cabecera = br.readLine();

            String linea;


            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(";");

                ps.setString(1, datos[0].trim());
                ps.setString(2, datos[1].trim());
                ps.setString(3, datos[2].trim());
                ps.setString(4, datos[3].trim());
                ps.setDouble(5, Double.parseDouble(datos[4].trim()));

                ps.executeUpdate();

            }
            conn.commit();


        } catch (IOException | SQLException | NumberFormatException e) {
            conn.rollback();
            throw new AppException("Error al importar el CSV " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }

    }

    public static void realizarTraspaso(String dniComprador, String matricula, double montoEconomico) throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        String buscarPropietarioPorDNI = "SELECT id_propietario FROM propietarios WHERE dni = ?";
        String buscarCocheYSuPropietarioPorMatricula = "SELECT id_propietario FROM coches WHERE matricula = ?";
        String insertarTraspaso = "INSERT INTO traspasos (matricula_coche, id_vendedor, id_comprador, monto_economico) VALUES (?, ?, ?, ?)";
        String actualizarCoche = "UPDATE coches SET id_propietario = ? WHERE matricula = ?";

        int idComprador;
        Integer idVendedor;

        //Buscamos al comprador
        try (PreparedStatement ps = conn.prepareStatement(buscarPropietarioPorDNI)) {
            ps.setString(1, dniComprador);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new AppException("No existe ningun propietario con ese DNI");
                }
                idComprador = rs.getInt("id_propietario");
            }
        }

        //Buscamos el coche y el vendedor
        try (PreparedStatement ps = conn.prepareStatement(buscarCocheYSuPropietarioPorMatricula)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new AppException("No existe un coche con esa matricula");
                }
                idVendedor = (Integer) rs.getObject("id_propietario");

                if (idVendedor != null && idVendedor == idComprador) {
                    throw new AppException("El comprador ya es propietario de este coche");
                }
            }
        }

        //Ponemos el autocommit a false
        conn.setAutoCommit(false);
        //Creamos dos preparedStatement uno encargado de insertar y otro encargado de actualizar el propetario de un coche
        try (PreparedStatement psInsert = conn.prepareStatement(insertarTraspaso);
             PreparedStatement psUpdate = conn.prepareStatement(actualizarCoche)) {

            psInsert.setString(1, matricula);
            if (idVendedor == null) {
                psInsert.setNull(2, Types.INTEGER);
            } else {
                psInsert.setInt(2, idVendedor);
            }
            psInsert.setInt(3, idComprador);
            psInsert.setDouble(4, montoEconomico);
            psInsert.executeUpdate();

            psUpdate.setInt(1, idComprador);
            psUpdate.setString(2, matricula);
            psUpdate.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new AppException("Error en el traslado " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }

    }

    public static List<Coche> ejecutarProcedimientoMarca(String marca) throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();

        List<Coche> coches = new ArrayList<>();

        if (ConexionDB.getOpcionActual() != 1) {
            throw new AppException("Los procedimientos almacenados solo estan disponibles en MySql");
        }

        try (CallableStatement cs = conn.prepareCall("{call sp_coches_por_marca(?)}")) {
            cs.setString(1, marca);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Coche coche = new Coche(
                            rs.getString("matricula"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getString("extras"),
                            rs.getDouble("precio"),
                            (Integer) rs.getObject("id_propietario")
                    );
                    coches.add(coche);
                }
            }

        }
        return coches;
    }

    public static void generarInforme() throws AppException, SQLException {
        Connection conn = ConexionDB.getConnection();
        String rutaInforme = PropertiesReader.getInstance().get("ruta.informe");

        String numCoches = "SELECT COUNT(*) FROM coches";
        String cochesPorMarca = "SELECT marca, COUNT(*) AS cantidad FROM coches GROUP BY marca";
        String extraMasRepetido = "SELECT extras FROM coches WHERE extras IS NOT NULL AND extras != ''";


        int total = 0;


        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(rutaInforme), StandardCharsets.UTF_8)) {
            try (PreparedStatement ps = conn.prepareStatement(numCoches)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new AppException("No hay coches");
                    }
                    total = rs.getInt(1);
                    bw.write("===Numero Total de Coches===");
                    bw.newLine();
                    bw.write("Total de coches: " + total);
                    bw.newLine();
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(cochesPorMarca)) {
                bw.write("===Coches Agrupados Por Marca===");
                try (ResultSet rs = ps.executeQuery()) {
                    bw.newLine();
                    while (rs.next()) {
                        bw.write(rs.getString("marca") + ": " + rs.getInt("cantidad") + " coche(s)");
                        bw.newLine();
                    }
                }
            }

            Map<String, Integer> conteoExtras = new HashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(extraMasRepetido)){
                bw.write("===Extra mas repetido===");
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        String extrasFila = rs.getString("extras");
                        for (String extra : extrasFila.split("\\|")){
                            String limpio = extra.trim().toLowerCase();
                            conteoExtras.put(limpio, conteoExtras.getOrDefault(limpio, 0) + 1);
                        }
                    }
                }
            }

            String topExtra = "Ninguno";
            int maxVeces = 0;
            for (Map.Entry<String, Integer> entry : conteoExtras.entrySet()){
                if (entry.getValue() > maxVeces){
                    maxVeces = entry.getValue();
                    topExtra = entry.getKey();
                }
            }
            bw.newLine();
            bw.write(topExtra + " (" + maxVeces + " veces)");
            bw.newLine();
        } catch (IOException e) {
            throw new AppException("No se pudo generar el informe " + e.getMessage());
        }

    }


}
