package main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;

import persistence.GestorBD;

public class Actividad {

	public enum Tipo {
		ANDAR, CORE, CORE_AVANZADO, EQUILIBRIO, GIMNASIA, HIIT, YOGA
		} 

	
	private String nombre;
	private int capacidad;
	private LocalDateTime fecha;
	private int ocupacion;
	private ImageIcon logo;
	protected ArrayList<Usuario> listaUsuarios = new ArrayList<>();
	private int calorias;
	private String intensidad;
	private String descripcion;
	private int duracion;
	private int idSesion;
	private Tipo tipo;
	
	/* 
	 * Este es el constructor que debe de usar la base de datos para crear las actividades
	 * La base de datos coje los parametros desde las siguientes tablas: 
	 *          Actividad: 
	 *          	- nombre	
	 *          	- duracion
	 *          	- Intensidad
	 *              - Calorias
	 *              - Descripcion
	 *          
	 * 			Sesion:
	 *              - capacidad
	 *              - Fecha
	 *              - ID sesion
	 *              
	 *          Participa:
	 *              - lista Usuarios (Crea la lista leyendo el DNI de la tabla y
	 *              				  añadiendo el usuario de la lista global de ventana principal
	 *              				  -la cual se deberia de cargar antes para poder leerla- que coincida con el DNI)
	 */
	public Actividad(String nombre, int duracion, String intensidad, int calorias, String descripcion, int capacidad, String fecha, int idSesion, ArrayList<Usuario> listaApuntados) {
		this.nombre = nombre;
		this.duracion = duracion;
		this.capacidad = capacidad;
		this.intensidad = intensidad;
		this.calorias = calorias;
		this.descripcion = descripcion;
		if (fecha != null) {
			DateTimeFormatter formateador = new DateTimeFormatterBuilder().parseCaseInsensitive().append(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")).toFormatter();
			LocalDateTime fechaThis = LocalDateTime.parse(fecha, formateador);
			this.fecha = fechaThis;
		}
		
		this.idSesion = idSesion;
		this.listaUsuarios = new ArrayList<>();
		
		
		// El resto de atributos se calculan a continuacion:
		this.logo = new ImageIcon("Images/"+nombre+".png");
		
	
		switch (nombre) {
		case "Andar":
			this.tipo = Tipo.ANDAR;
			break;
			
		case "Core":
			this.tipo = Tipo.CORE;
			break;
			
		case "Core Avanzado":
			this.tipo = Tipo.CORE_AVANZADO;
			break;
			
		case "Equilibrio":
			this.tipo = Tipo.EQUILIBRIO;
			break;
			
		case "Gimnasia":
			this.tipo = Tipo.GIMNASIA;
			break;
			
		case "HIIT":
			this.tipo = Tipo.HIIT;
			break;
		
		case "Yoga":
			this.tipo = Tipo.YOGA;
			break;

		default:
			this.tipo = null;
			break;
		}
		
	}
	
	
	
	public Actividad(String nombre, int capacidad, LocalDateTime fecha, int ocupacion,
			 String descripcion, int duracion, Tipo tipo) {
		super();
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.fecha = fecha;
		this.ocupacion = ocupacion;
		this.descripcion = descripcion;
		this.duracion = duracion;
		
		if (nombre.contains("Core")|| nombre.contains("HIIT")){
			this.intensidad = "Alta";
		} else {
			this.intensidad = "Normal";
		}
		
		
		if (nombre.contains("Yoga") || nombre.contains("Andar") || nombre.contains("Gimnasia")) {
			if (intensidad.contains("Alta")) {
				this.calorias = 350;
			} else {
				this.calorias = 200;
			}
		} else {
			if (intensidad.contains("Alta")) {
				this.calorias = 650;
			} else {
				this.calorias = 450;
			}
		}
		
		switch (nombre) {
		case "Andar":
			this.descripcion = "En esta clase andaremos en las cintas con un monitor";
			break;
			
		case "Core":
		case "Core Avanzado":
			this.descripcion = "En esta clase haremos ejercicios para fortalecer los abdominales";
			break;
			
		case "Equilibrio":
			this.descripcion = "En esta clase entrenaremos el equilibrio y la flexibilidad";
			break;
			
		case "Gimnasia":
			this.descripcion = "En esta clase practicaremos varios ejercicios gimnasticos";
			break;
			
		case "HIIT":
			this.descripcion = "En esta clase haremos ejercicios en intervalos de alta intensidad";
			break;
		
		case "Yoga":
			this.descripcion = "En esta clase haremos ejercicios de yoga y meditacion";
			break;
		default:
			this.descripcion = "No hay descripcion para esta actividad";
			break;
		}
	
		switch (nombre) {
		case "Andar":
			this.tipo = Tipo.ANDAR;
			break;
			
		case "Core":
			this.tipo = Tipo.CORE;
			break;
			
		case "Core Avanzado":
			this.tipo = Tipo.CORE_AVANZADO;
			break;
			
		case "Equilibrio":
			this.tipo = Tipo.EQUILIBRIO;
			break;
			
		case "Gimnasia":
			this.tipo = Tipo.GIMNASIA;
			break;
			
		case "HIIT":
			this.tipo = Tipo.HIIT;
			break;
		
		case "Yoga":
			this.tipo = Tipo.YOGA;
			break;

		default:
			this.tipo = null;
			break;
		}
	}
	
	public Actividad(String nombre, LocalDateTime fecha) {
		super();
		this.nombre = nombre;
		this.capacidad = 20 + (new Random()).nextInt(61);
		this.fecha = fecha;
		this.ocupacion = 0;
		this.logo = new ImageIcon("Images/"+nombre+".png");
		this.listaUsuarios = new ArrayList<>();
		this.duracion = 20 + (new Random()).nextInt(61);
		
		if (nombre.contains("Core")|| nombre.contains("HIIT")){
			this.intensidad = "Alta";
		} else {
			this.intensidad = "Normal";
		}
		
	
		
	
	
		
		if (nombre.contains("Yoga") || nombre.contains("Andar") || nombre.contains("Gimnasia")) {
			if (intensidad.contains("Alta")) {
				this.calorias = 350;
			} else {
				this.calorias = 200;
			}
		} else {
			if (intensidad.contains("Alta")) {
				this.calorias = 650;
			} else {
				this.calorias = 450;
			}
		}
		
		switch (nombre) {
		case "Andar":
			this.descripcion = "En esta clase andaremos en las cintas con un monitor";
			break;
			
		case "Core":
		case "Core Avanzado":
			this.descripcion = "En esta clase haremos ejercicios para fortalecer los abdominales";
			break;
			
		case "Equilibrio":
			this.descripcion = "En esta clase entrenaremos el equilibrio y la flexibilidad";
			break;
			
		case "Gimnasia":
			this.descripcion = "En esta clase practicaremos varios ejercicios gimnasticos";
			break;
			
		case "HIIT":
			this.descripcion = "En esta clase haremos ejercicios en intervalos de alta intensidad";
			break;
		
		case "Yoga":
			this.descripcion = "En esta clase haremos ejercicios de yoga y meditacion";
			break;
		default:
			this.descripcion = "No hay descripcion para esta actividad";
			break;
		}
	
		switch (nombre) {
		case "Andar":
			this.tipo = Tipo.ANDAR;
			break;
			
		case "Core":
			this.tipo = Tipo.CORE;
			break;
			
		case "Core Avanzado":
			this.tipo = Tipo.CORE_AVANZADO;
			break;
			
		case "Equilibrio":
			this.tipo = Tipo.EQUILIBRIO;
			break;
			
		case "Gimnasia":
			this.tipo = Tipo.GIMNASIA;
			break;
			
		case "HIIT":
			this.tipo = Tipo.HIIT;
			break;
		
		case "Yoga":
			this.tipo = Tipo.YOGA;
			break;

		default:
			this.tipo = null;
			break;
		}
	}

	

	public int getDuracion() {
		return duracion;
	}
	
	public int getIdSesion() {
		return idSesion;
	}


	public ArrayList<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}


	public void setListaUsuarios(ArrayList<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
		ocupacion = listaUsuarios.size();
		
	}

	public void setIdSesion(int id) {
		this.idSesion = id;
	}

	public String getNombre() {
		return nombre;
	}


	public int getCapacidad() {
		return capacidad;
	}


	public LocalDateTime getFecha() {
		return fecha;
	}


	public int getOcupacion() {
		return ocupacion;
	}


	public ImageIcon getLogo() {
		return logo;
	}


	public int getCalorias() {
		return calorias;
	}


	public String getIntensidad() {
		return intensidad;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void addUsuario(Usuario usuario) {
		this.listaUsuarios.add(usuario);
		GestorBD.insertarParticipacion(usuario.getDni(), this.idSesion);
	}
	
	public void removeUsuario(Usuario usuario) {
		this.listaUsuarios.remove(usuario);
		GestorBD.eliminarParticipacion(usuario.getDni(), this.getIdSesion());
	}
	
	public void actualizarOcupacion(){
		this.ocupacion = this.listaUsuarios.size();
	}


	public Tipo getTipo() {
		return tipo;
	}

	@Override
	public String toString() {
		return "Actividad [nombre=" + nombre + ", id ="+idSesion+", capacidad=" + capacidad + ", fecha=" + fecha + ", ocupacion="
				+ ocupacion + ", logo=" + logo + ", listaUsuarios=" + listaUsuarios + ", calorias=" + calorias
				+ ", intensidad=" + intensidad + ", descripcion=" + descripcion + ", duracion=" + duracion + ", tipo="
				+ tipo + "]";
	}

	
}
