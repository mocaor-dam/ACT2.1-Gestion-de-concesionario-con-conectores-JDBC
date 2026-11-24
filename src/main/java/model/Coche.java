package model;

public class Coche {
    private String matricula;
    private String marca;
    private String modelo;
    private String extras;
    private double precio;
    //Uso Integer en lugar de int ya que la base de datos no admite que un int primitivo sea null
    private Integer idPropietario;

    //Constructor para altas nuevas antes de que la BBDD genere el ID autoincremental
    public Coche(String matricula, String marca, String modelo, String extras, double precio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
    }

    //Constructor completo para reconstruir objetos leidos desde un ResultSet
    public Coche(String matricula, String marca, String modelo, String extras, double precio, Integer idPropiertario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropiertario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Integer getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(Integer idPropietario) {
        this.idPropietario = idPropietario;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras='" + extras + '\'' +
                ", precio=" + precio +
                ", idPropiertario=" + idPropietario +
                '}';
    }
}
