import exceptions.AppException;
import exceptions.MiEntradaSalidaException;
import model.Coche;
import model.Propietario;
import service.GestionBBDD;
import utils.ConexionDB;
import utils.MiEntradaSalida;

import java.sql.SQLException;
import java.util.List;

public class Principal {
    public static void main(String[] args) {

        boolean activo = true;

        while (activo) {
            try {
                interfaz();
                int opcion = MiEntradaSalida.leerEnteroRango("Introduce la opcion: ", 1, 13);

                switch (opcion) {
                    case 1:
                        System.out.println("1. MySql");
                        System.out.println("2. MySQLite");
                        int motor = MiEntradaSalida.leerEnteroRango("Elige que quieres usar(1/2): ", 1, 2);
                        ConexionDB.configurarConexion(motor);
                        System.out.println("Conexion establecida correctamente");
                        break;

                    case 2:
                        GestionBBDD.inicializarTablas();
                        System.out.println("Tablas (y procedimiento si es MySql) inicializadas correctamente");
                        break;
                    case 3:

                        String dni = MiEntradaSalida.leerLinea("Introduce el dni del propietario: ");
                        String nombre = MiEntradaSalida.leerLinea("Introduce el nombre del propietario: ");
                        String apellidos = MiEntradaSalida.leerLinea("Introduce el apellido del propietario: ");
                        String telefono = MiEntradaSalida.leerLinea("Introduce el telefono: ");

                        Propietario propietario = new Propietario(dni, nombre, apellidos, telefono);
                        GestionBBDD.registrarPropietario(propietario);
                        System.out.println("Propietario registrado correctamente.");
                        break;


                    case 4:

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

                        GestionBBDD.importarCsv();
                        System.out.println("Importacion del CSV realizada correctamente");
                        break;


                    case 6:

                        List<Coche> coches = GestionBBDD.listarCochesConcesionario();
                        if (coches.isEmpty()) {
                            System.out.println("No hay coches en el concesionario");
                        } else {
                            coches.forEach(System.out::println);
                        }
                        break;


                    case 7:

                        List<String> cochesConPropietarios = GestionBBDD.listarCochesConPropietario();
                        if (cochesConPropietarios.isEmpty()) {
                            System.out.println("No hay coches con propietarios");
                        } else {
                            cochesConPropietarios.forEach(System.out::println);
                        }
                        break;


                    case 8:

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

                        String matABorrar = MiEntradaSalida.leerLinea("Introduce la matricula del coche a borrar: ");
                        if (GestionBBDD.borrarCoche(matABorrar)){
                            System.out.println("Coche borrado correctamente.");
                        } else {
                            System.out.println("No se encontro ningun coche con esa matricula");
                        }
                        break;

                    case 10:
                        String dniComprador = MiEntradaSalida.leerLinea("Introduce el dni del comprador: ");
                        String matTraspaso = MiEntradaSalida.leerLinea("Introduce la matricula del coche a traspasar: ");
                        double monto = MiEntradaSalida.leerDouble("Introduce el monto economico de la operacion: ");

                        GestionBBDD.realizarTraspaso(dniComprador, matTraspaso, monto);
                        System.out.println("Traspaso realizado y registrado correctamente.");
                        break;

                    case 11:

                        String marcaBuscar = MiEntradaSalida.leerLinea("Introduce la marca a consultar: ");
                        List<Coche> cochesMarca = GestionBBDD.ejecutarProcedimientoMarca(marcaBuscar);
                        if (cochesMarca.isEmpty()){
                            System.out.println("No se encontraron coches con esa marca.");
                        } else {
                            cochesMarca.forEach(System.out::println);
                        }
                        break;


                    case 12:
                        GestionBBDD.generarInforme();
                        System.out.println("Informe resumen generado correctamente.");
                        break;

                    case 13:
                        ConexionDB.cerrarConexion();
                        activo = false;
                }
            } catch (AppException | SQLException | MiEntradaSalidaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


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
