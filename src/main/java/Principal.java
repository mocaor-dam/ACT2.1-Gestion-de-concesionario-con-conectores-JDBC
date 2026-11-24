import exceptions.AppException;
import exceptions.MiEntradaSalidaException;
import model.Coche;
import model.Propietario;
import service.GestionBBDD;
import utils.ConexionDB;
import utils.MiEntradaSalida;

import java.sql.SQLException;
import java.util.List;

/**
 * Clase principal que actúa como capa de presentación (interfaz por consola).
 * Delega toda la lógica de acceso a datos y transacciones en la clase de servicio GestionBBDD.
 */
public class Principal {
    public static void main(String[] args) {

        boolean activo = true;

        // Bucle principal de la aplicación: se mantiene en ejecución hasta que el usuario elija salir (opción 13)
        while (activo) {
            try {
                interfaz();
                int opcion = MiEntradaSalida.leerEnteroRango("Introduce la opcion: ", 1, 13);

                switch (opcion) {
                    case 1:
                        // Selección dinámica del motor de base de datos (patrón Singleton en ConexionDB)
                        System.out.println("1. MySql");
                        System.out.println("2. MySQLite");
                        int motor = MiEntradaSalida.leerEnteroRango("Elige que quieres usar(1/2): ", 1, 2);
                        ConexionDB.configurarConexion(motor);
                        System.out.println("Conexion establecida correctamente");
                        break;

                    case 2:
                        // Creación de tablas adaptada al dialecto SQL activo (y procedimiento almacenado si es MySQL)
                        GestionBBDD.inicializarTablas();
                        System.out.println("Tablas (y procedimiento si es MySql) inicializadas correctamente");
                        break;
                    case 3:
                        // Alta de propietario: usamos el constructor sin ID porque lo genera la BBDD automáticamente
                        String dni = MiEntradaSalida.leerLinea("Introduce el dni del propietario: ");
                        String nombre = MiEntradaSalida.leerLinea("Introduce el nombre del propietario: ");
                        String apellidos = MiEntradaSalida.leerLinea("Introduce el apellido del propietario: ");
                        String telefono = MiEntradaSalida.leerLinea("Introduce el telefono: ");

                        Propietario propietario = new Propietario(dni, nombre, apellidos, telefono);
                        GestionBBDD.registrarPropietario(propietario);
                        System.out.println("Propietario registrado correctamente.");
                        break;


                    case 4:
                        // Alta de coche nuevo en el inventario del concesionario (id_propietario a null)
                        String matricula = MiEntradaSalida.leerLinea("Introduce la matricula: ");
                        String marca = MiEntradaSalida.leerLinea("Introduce la marca: ");
                        String modelo = MiEntradaSalida.leerLinea("Introduce el modelo: ");
                        String extras = MiEntradaSalida.leerLinea("Introduce los extras separados por |: ");
                        double precio = MiEntradaSalida.leerDouble("Introduce el precio: ");

                        Coche coche = new Coche(matricula, marca, modelo, extras, precio, null);
                        GestionBBDD.insertarCoche(coche);
                        System.out.println("Coche agregado al concesionario correctamente.");
                        break;

                    case 5:
                        // Carga transaccional desde el fichero CSV configurado en config.properties
                        GestionBBDD.importarCsv();
                        System.out.println("Importacion del CSV realizada correctamente");
                        break;


                    case 6:
                        // Consulta de vehículos en stock del concesionario (id_propietario IS NULL)
                        List<Coche> coches = GestionBBDD.listarCochesConcesionario();
                        if (coches.isEmpty()) {
                            System.out.println("No hay coches en el concesionario");
                        } else {
                            coches.forEach(System.out::println);
                        }
                        break;


                    case 7:
                        // Consulta con INNER JOIN para mostrar los vehículos vendidos junto al DNI de su propietario
                        List<String> cochesConPropietarios = GestionBBDD.listarCochesConPropietario();
                        if (cochesConPropietarios.isEmpty()) {
                            System.out.println("No hay coches con propietarios");
                        } else {
                            cochesConPropietarios.forEach(System.out::println);
                        }
                        break;


                    case 8:
                        // Actualización de los datos de un vehículo existente buscando por su clave primaria (matrícula)
                        String matriculaABuscar = MiEntradaSalida.leerLinea("Introduce la matricula del coche a modificar: ");
                        String nuevaMarca = MiEntradaSalida.leerLinea("Introduce la nueva marca: ");
                        String nuevoModelo = MiEntradaSalida.leerLinea("Introduce el nuevo modelo: ");
                        String nuevosExtras = MiEntradaSalida.leerLinea("Introduce los nuevos extras(separados por |): ");
                        double nuevoPrecio = MiEntradaSalida.leerDouble("Introduce el nuevo precio: ");

                        Coche cocheModificado = new Coche(matriculaABuscar, nuevaMarca, nuevoModelo, nuevosExtras, nuevoPrecio);

                        if (GestionBBDD.modificarCoche(cocheModificado)){
                            System.out.println("Coche modificado correctamente.");
                        } else {
                            System.out.println("No se encontro ningun coche con esa matricula");
                        }
                        break;


                    case 9:
                        // Eliminación de un vehículo por matrícula
                        String matABorrar = MiEntradaSalida.leerLinea("Introduce la matricula del coche a borrar: ");
                        if (GestionBBDD.borrarCoche(matABorrar)){
                            System.out.println("Coche borrado correctamente.");
                        } else {
                            System.out.println("No se encontro ningun coche con esa matricula");
                        }
                        break;

                    case 10:
                        // Creacion de un Traspaso
                        String dniComprador = MiEntradaSalida.leerLinea("Introduce el dni del comprador: ");
                        String matTraspaso = MiEntradaSalida.leerLinea("Introduce la matricula del coche a traspasar: ");
                        double monto = MiEntradaSalida.leerDouble("Introduce el monto economico de la operacion: ");

                        GestionBBDD.realizarTraspaso(dniComprador, matTraspaso, monto);
                        System.out.println("Traspaso realizado y registrado correctamente.");
                        break;

                    case 11:
                        // Invocación del procedimiento almacenado sp_coches_por_marca mediante CallableStatement (solo MySQL)
                        String marcaBuscar = MiEntradaSalida.leerLinea("Introduce la marca a consultar: ");
                        List<Coche> cochesMarca = GestionBBDD.ejecutarProcedimientoMarca(marcaBuscar);
                        if (cochesMarca.isEmpty()){
                            System.out.println("No se encontraron coches con esa marca.");
                        } else {
                            cochesMarca.forEach(System.out::println);
                        }
                        break;


                    case 12:
                        //Generacion de un informe resumen
                        GestionBBDD.generarInforme();
                        System.out.println("Informe resumen generado correctamente.");
                        break;

                    case 13:
                        ConexionDB.cerrarConexion();
                        activo = false;
                        break;
                }
            } catch (AppException | SQLException | MiEntradaSalidaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    /**
     * Imprime por pantalla el menú principal
     */
    public static void interfaz() {
        System.out.println("\n===MENU BBDD===");
        System.out.println("1. Conectar BBDD");
        System.out.println("2. Inicializar/Crear Tablas");
        System.out.println("3. Registrar Propietario");
        System.out.println("4. Insertar Coche");
        System.out.println("5. Importar Coches(CSV)");
        System.out.println("6. Listar Coches(Concesionario)");
        System.out.println("7. Listar Coches(Propietarios)");
        System.out.println("8. Modificar Coche");
        System.out.println("9. Borrar Coche");
        System.out.println("10. Realizar traspaso");
        System.out.println("11. Ejecutar Procedimiento almacenado");
        System.out.println("12. Generar informe Resumen");
        System.out.println("13. Salir");

    }
}
