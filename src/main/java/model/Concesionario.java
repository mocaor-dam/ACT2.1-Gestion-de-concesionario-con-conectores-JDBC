package model;

import java.util.ArrayList;
import java.util.List;

public class Concesionario {
    private List<Coche> coches;

    public Concesionario() {
        this.coches = new ArrayList<>();
    }

    public List<Coche> getCoches() {
        return coches;
    }

    public void setCoches(List<Coche> coches) {
        this.coches = coches;
    }

    @Override
    public String toString() {
        return "Concesionario{" +
                "coches=" + coches +
                '}';
    }

    public boolean existeMatricula(String matricula){
        for (Coche c : this.coches){
            if (c.getMatricula() != null && c.getMatricula().equalsIgnoreCase(matricula)){
                return true;
            }
        }
        return false;
    }
}
