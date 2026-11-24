package model;

public class Traspaso {
    private int idTraspaso;
    private String matriculaCoche;
    //Uso Integer en lugar de int ya que la base de datos no admite que un int primitivo sea null
    private Integer idVendedor;
    private int idComprador;
    private double montoEconomico;

    //Constructor para altas nuevas antes de que la BBDD genere el ID autoincremental
    public Traspaso(String matriculaCoche, Integer idVendedor, int idComprador, double montoEconomico) {
        this.matriculaCoche = matriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }

    //Constructor completo para reconstruir objetos leidos desde un ResultSet
    public Traspaso(int idTraspaso, String matriculaCoche, Integer idVendedor, int idComprador, double montoEconomico) {
        this.idTraspaso = idTraspaso;
        this.matriculaCoche = matriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }


    public int getIdTraspaso() {
        return idTraspaso;
    }

    public void setIdTraspaso(int idTraspaso) {
        this.idTraspaso = idTraspaso;
    }

    public String getMatriculaCoche() {
        return matriculaCoche;
    }

    public void setMatriculaCoche(String matriculaCoche) {
        this.matriculaCoche = matriculaCoche;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public double getMontoEconomico() {
        return montoEconomico;
    }

    public void setMontoEconomico(double montoEconomico) {
        this.montoEconomico = montoEconomico;
    }

    @Override
    public String toString() {
        return "Traspaso{" +
                "idTraspaso=" + idTraspaso +
                ", matriculaCoche='" + matriculaCoche + '\'' +
                ", idVendedor=" + idVendedor +
                ", idComprador=" + idComprador +
                ", montoEconomico=" + montoEconomico +
                '}';
    }
}
