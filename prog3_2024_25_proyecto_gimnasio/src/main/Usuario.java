 package main;

public class Usuario {
	
	public enum Sexo{
		HOMBRE, MUJER
	}
	
	private String nombre;
	private String apellido;
	private String dni;
	private int telefono;
	private int edad;
	private Sexo sexo;
	private String contraseña;
	
	public Usuario(String nombre, String apellido, String dni, int telefono, int edad, Sexo sexo, String contraseña) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.telefono = telefono;
		this.edad = edad;
		this.sexo = sexo;
		this.contraseña = contraseña;
	}
	
	public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public int getTelefono() {
        return telefono;
    }

    public int getEdad() {
        return edad;
    }

    public Sexo getSexo() {
        return sexo;
    }
    
    public String getContraseña() {
    	return contraseña;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    @Override
    public String toString() {
        return "Usuario [nombre=" + nombre + ", apellido=" + apellido + ", dni=" + dni + ", telefono=" + telefono
                + ", edad=" + edad + ", sexo=" + sexo + ", contraseña="+contraseña+"]";
    }
}
