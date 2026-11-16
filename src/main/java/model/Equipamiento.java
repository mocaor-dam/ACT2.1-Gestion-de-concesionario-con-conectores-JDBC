package model;

public class Equipamiento {
    private String nombre;

    public Equipamiento() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Equipamiento{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
