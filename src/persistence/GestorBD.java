package persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import main.Actividad;
import main.Usuario;
import main.Usuario.Sexo;



public class GestorBD {

    private static final String PROPERTIES_FILE = "config/parametros.properties";
    private static final String CONNECTION_STRING = "jdbc:sqlite:resources/db/database.db";
    private static final String LOG_FOLDER = "log";
    private final String CSV_USUARIOS = "resources/data/usuarios.csv";
    private final String CSV_SESIONES = "resources/data/Sesiones.csv";
    private final String CSV_ACTIVIDADES = "resources/data/actividades.csv";

    private Properties properties;
    private String driverName;
    private String databaseFile;

    private static final Logger logger = Logger.getLogger(GestorBD.class.getName());


	public GestorBD() {
		try (FileInputStream fis = new FileInputStream("config/logger.properties")) {
			LogManager.getLogManager().readConfiguration(fis);

			properties = new Properties();
			properties.load(new FileReader(PROPERTIES_FILE));

			driverName = properties.getProperty("driver");
			databaseFile = properties.getProperty("file");

			File dir = new File(LOG_FOLDER);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			dir = new File(databaseFile.substring(0, databaseFile.lastIndexOf("/")));
			if (!dir.exists()) {
				dir.mkdirs();
			}

			Class.forName(driverName);
		} catch (Exception ex) {
			logger.warning(String.format("Error al cargar el driver de la base de datos: %s", ex.getMessage()));
		}
	}

	public void initilizeFromCSV() {
		if (properties.get("loadCSV").equals("true")) {
			this.borrarDatos();
			
			List<Usuario> usuarios = this.loadCSVUsuarios();

		    insertarUsuarios(usuarios.toArray(new Usuario[usuarios.size()]));
			
			List<Actividad> actividades = this.loadCSVActividades();
			for (Actividad a:actividades) {
				this.insertarActividades(a.getNombre(),a.getDuracion(),a.getIntensidad(),a.getCalorias(),a.getDescripcion());	
			}
			
			List<Actividad> sesiones = this.LoadCSVSesiones();
			for (Actividad s:sesiones) {
				this.asignarUsuariosAleatorios(s, usuarios);
			}
			
			this.insertarSesiones(sesiones.toArray(new Actividad[sesiones.size()]));
		}
	}
	
	public void asignarUsuariosAleatorios(Actividad a, List<Usuario> usuarios) {
		for (int l = 0; l < (2 + (new Random()).nextInt(6)); l++) {
		    if (!usuarios.isEmpty()) {
		        Usuario usuarioSeleccionado = usuarios.get((new Random()).nextInt(usuarios.size()));

		        if (!a.getListaUsuarios().contains(usuarioSeleccionado)) {
		            a.addUsuario(usuarioSeleccionado);
		        } else {
		            System.out.println("El usuario ya está en la actividad. No se agregará nuevamente.");
		            l = l-1;
		        }
		    } else {
		        System.err.println("La lista de usuarios está vacía. No se pueden agregar usuarios a la actividad.");
		    }
		}
	}
	
	public void crearBBDD() {
		if (properties.get("createBBDD").equals("true")) {
			String sql1 = """
                CREATE TABLE IF NOT EXISTS usuario (
					nombre_usuario TEXT NOT NULL,
					apellido_usuario TEXT NOT NULL, 
					dni_usuario TEXT NOT NULL,
					telefono_usuario TEXT NOT NULL,
					edad_usuario INTEGER NOT NULL,
					sexo_usuario TEXT NOT NULL,
					contrasena_usuario TEXT NOT NULL,
					PRIMARY KEY (dni_usuario),
					CHECK (edad_usuario > 0)
					);
            """;
	
			String sql2 = """
                CREATE TABLE IF NOT EXISTS actividad (
					nombre_actividad TEXT NOT NULL,
					duracion_actividad INTEGER NOT NULL,
					intensidad_actividad TEXT NOT NULL,
					calorias_actividad INTEGER NOT NULL,
					descripcion TEXT NOT NULL,
					PRIMARY KEY (nombre_actividad),
					CHECK (duracion_actividad > 0)
					);
            """;
	
			String sql3 = """
               CREATE TABLE IF NOT EXISTS sesion (
					capacidad_sesion INTEGER NOT NULL CHECK (capacidad_sesion > 0),
					fecha_sesion TEXT NOT NULL,
					id_sesion INTEGER PRIMARY KEY AUTOINCREMENT,
					nombre_actividad TEXT NOT NULL,
					FOREIGN KEY (nombre_actividad) REFERENCES actividad (nombre_actividad) ON DELETE CASCADE
					);
            """;
			
			String sql4 = """
                CREATE TABLE IF NOT EXISTS participa (
					dni_usuario TEXT NOT NULL,
					id_sesion INTEGER NOT NULL,
					PRIMARY KEY (dni_usuario, id_sesion),
					FOREIGN KEY (dni_usuario) REFERENCES usuario (dni_usuario) ON DELETE CASCADE,
					FOREIGN KEY (id_sesion) REFERENCES sesion (id_sesion) ON DELETE CASCADE
					);
            """;

			try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
			     PreparedStatement pStmt1 = con.prepareStatement(sql1);
				 PreparedStatement pStmt2 = con.prepareStatement(sql2);
				 PreparedStatement pStmt3 = con.prepareStatement(sql3);
				 PreparedStatement pStmt4 = con.prepareStatement(sql4)) {
				
				//Se ejecutan las sentencias de creación de las tablas
		        if (!pStmt1.execute() && !pStmt2.execute() && !pStmt3.execute() && !pStmt4.execute()) {
		        	logger.info("Se han creado las tablas");
		        }
			} catch (Exception ex) {
				logger.warning(String.format("Error al crear las tablas: %s", ex.getMessage()));
			}
		}
	}
	
	public void borrarBBDD() {
		if (properties.get("deleteBBDD").equals("true")) {	
			String sql1 = "DROP TABLE IF EXISTS usuario;";
			String sql2 = "DROP TABLE IF EXISTS actividad";
			String sql3 = "DROP TABLE IF EXISTS sesion;";
			String sql4 = "DROP TABLE IF EXISTS participa;";
			
			try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
			     PreparedStatement pStmt1 = con.prepareStatement(sql1);
				 PreparedStatement pStmt2 = con.prepareStatement(sql2);
				 PreparedStatement pStmt3 = con.prepareStatement(sql3);
				 PreparedStatement pStmt4 = con.prepareStatement(sql4)) {
				
		        if (!pStmt1.execute() && !pStmt2.execute() && !pStmt3.execute() && !pStmt4.execute()) {
		        	logger.info("Se han borrado las tablas");
		        }
			} catch (Exception ex) {
				logger.warning(String.format("Error al borrar las tablas: %s", ex.getMessage()));
			}
			
			try {
				Files.delete(Paths.get(databaseFile));
				logger.info("Se ha borrado el fichero de la BBDD");
			} catch (Exception ex) {
				logger.warning(String.format("Error al borrar el fichero de la BBDD: %s", ex.getMessage()));
			}
		}
	}
	
	public void borrarDatos() {
		if (properties.get("cleanBBDD").equals("true")) {	
			String sql1 = "DELETE FROM usuario;";
			String sql2 = "DELETE FROM actividad;";
			String sql3 = "DELETE FROM sesion;";
			String sql4 = "DELETE FROM participa;";
			
			try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
			     PreparedStatement pStmt1 = con.prepareStatement(sql1);
				 PreparedStatement pStmt2 = con.prepareStatement(sql2);
				 PreparedStatement pStmt3 = con.prepareStatement(sql3);
				 PreparedStatement pStmt4 = con.prepareStatement(sql4)) {
				
		        if (!pStmt1.execute() && !pStmt2.execute() && !pStmt3.execute() && !pStmt4.execute()) {
		        	logger.info("Se han borrado los datos");
		        }
			} catch (Exception ex) {
				logger.warning(String.format("Error al borrar los datos: %s", ex.getMessage()));
			}
		}
	}
	
	
	/** 
	 * 
	 * 
	 * 
	 * GESTION DE LA TABLA USUARIOS:
	 * 
	 * 
	 * 
	 * **/
	
	
    public static void insertarUsuarios(Usuario... usuarios) {
        String sql = "INSERT INTO usuario (nombre_usuario, apellido_usuario, dni_usuario, telefono_usuario, edad_usuario, sexo_usuario, contrasena_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            for (Usuario u : usuarios) {
                pstmt.setString(1, u.getNombre());
                pstmt.setString(2, u.getApellido());
                pstmt.setString(3, u.getDni());
                pstmt.setInt(4, u.getTelefono());
                pstmt.setInt(5, u.getEdad());
                pstmt.setString(6, u.getSexo().toString());
                pstmt.setString(7, u.getContraseña());
                pstmt.executeUpdate();
            }
            System.out.println("Usuarios insertados correctamente.");
        } catch (SQLException ex) {
            System.err.format("Error al insertar usuarios: %s", ex.getMessage());
        }
    }

    public static boolean eliminarUsuario(String dni) {
        String sql = "DELETE FROM usuario WHERE dni_usuario = ?";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dni);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public List<Usuario> loadCSVUsuarios(){
    	List<Usuario> usuarios = new ArrayList<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(CSV_USUARIOS))) {
			String linea = null;
			Usuario u = null;
			in.readLine();		
			
			while ((linea = in.readLine()) != null) {
				String[] campos = linea.split(";");
				try {
				    u = new Usuario(
				        campos[0], campos[1], campos[2],
				        Integer.parseInt(campos[3]),
				        Integer.parseInt(campos[4]),
				        Sexo.valueOf(campos[5].toUpperCase()),
				        campos[6]
				    );
				} catch (IllegalArgumentException | NullPointerException e) {
				    logger.warning(String.format("Dato inválido en línea: %s. Error: %s", linea, e.getMessage()));
				    continue; 
				}
				
				if (u != null) {
					usuarios.add(u);
				}
			}			
			
		} catch (Exception ex) {
			logger.warning(String.format("Error leyendo usuarios del CSV: %s", ex.getMessage()));
		}
		
		return usuarios;
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("nombre_usuario"),
                        rs.getString("apellido_usuario"),
                        rs.getString("dni_usuario"),
                        rs.getInt("telefono_usuario"),
                        rs.getInt("edad_usuario"),
                        Sexo.valueOf(rs.getString("sexo_usuario")),
                        rs.getString("contrasena_usuario")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public List<Usuario> obtenerUsuarioPorActividad(Actividad a){
    	List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM participa p left join usuario u on p.dni_usuario = u.dni_usuario where p.id_sesion = ? ";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = con.prepareStatement(sql)){
        	 pstmt.setInt(1,a.getIdSesion());
             ResultSet rs = pstmt.executeQuery();
             while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("nombre_usuario"),
                        rs.getString("apellido_usuario"),
                        rs.getString("dni_usuario"),
                        rs.getInt("telefono_usuario"),
                        rs.getInt("edad_usuario"),
                        Sexo.valueOf(rs.getString("sexo_usuario")),
                        rs.getString("contrasena_usuario")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    

    private Connection connection;

    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    
    /** 
	 * 
	 * 
	 * 
	 * GESTION DE LA TABLA ACTIVIDADES:
	 * 
	 * 
	 * 
	 * **/
    
    
    
    public void insertarActividades(String nombre, int duracion, String intensidad, int calorias, String descripcion) {
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING)) {
            String sql = """
                INSERT INTO actividad (nombre_actividad, duracion_actividad, intensidad_actividad, calorias_actividad, descripcion) 
                VALUES (?, ?, ?, ?, ?)
            """;

            PreparedStatement pstmt = con.prepareStatement(sql);
            
            System.out.println("- Insertando actividades...");

           
            pstmt.setString(1, nombre);
            pstmt.setInt(2, duracion);
            pstmt.setString(3, intensidad);
            pstmt.setInt(4, calorias);
            pstmt.setString(5, descripcion);

            if (1 == pstmt.executeUpdate()) {
                System.out.format("\n - Actividad insertada: %s", nombre);
            } else {
                System.out.format("\n - No se ha insertado la actividad: %s", nombre);
            }
            
            pstmt.close();
        } catch (Exception ex) {
            System.err.format("\n* Error al insertar actividades: %s", ex.getCause());
            ex.printStackTrace();
        }
    }

    public List<Actividad> loadCSVActividades(){
    	List<Actividad> actividades = new ArrayList<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(CSV_ACTIVIDADES))) {
			String linea = null;
			Actividad a = null;
			in.readLine();		
			while ((linea = in.readLine()) != null) {
				System.out.println(linea);
				String[] campos = linea.split(";");
				a = new Actividad(campos[0],(int) Integer.valueOf(campos[1]),campos[2],(int) Integer.valueOf(campos[3]),campos[4],0,null,0,null);
				
				if (a!= null) {
					actividades.add(a);
				}
			}			
			
		} catch (Exception ex) {
			logger.warning(ex.getMessage());
		}
		return actividades;
    }
    
    /** 
   	 * 
   	 * 
   	 * 
   	 * GESTION DE LA TABLA SESIONES:
   	 * 
   	 * 
   	 * 
   	 * **/
    

    public void insertarSesiones(Actividad... actividades) {
        String sql = "INSERT INTO sesion (capacidad_sesion, fecha_sesion, nombre_actividad) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            for (Actividad a : actividades) {
                pstmt.setInt(1, a.getCapacidad());
                pstmt.setString(2, a.getFecha().toString());
                pstmt.setString(3, a.getNombre());
                if (pstmt.executeUpdate() != 1) {					
					System.out.println(String.format("No se ha insertado la Sesion: %s", a.getNombre()));
				} else {
					a = this.getIdPorSesion(a);					
					
					for (Usuario u : a.getListaUsuarios()) {
						GestorBD.insertarParticipacion(u.getDni(),a.getIdSesion());
					}
					
					logger.info(String.format("Se ha insertado la Sesion: %s", a.getNombre()));
            }
            }
            System.out.println("Usuarios insertados correctamente.");
        } catch (SQLException ex) {
            System.err.format("Error al insertar usuarios: %s", ex.getMessage());
        }
    }

    public List<Actividad> obtenerTodosLasSesiones() {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT * FROM sesion s LEFT JOIN actividad a on s.nombre_actividad = a.nombre_actividad";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Actividad actividad = new Actividad(
                		rs.getString("nombre_actividad"),
                		rs.getInt("duracion_actividad"),
                		rs.getString("intensidad_actividad"),
                		rs.getInt("calorias_actividad"),
                		rs.getString("descripcion"),
                        rs.getInt("capacidad_sesion"),
                        rs.getString("fecha_sesion"),
                        rs.getInt("id_sesion"),
                        new ArrayList<Usuario>()
                );
                actividades.add(actividad);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener sesiones: " + e.getMessage());
        }
        return actividades;
    }
 
    public List<Actividad> LoadCSVSesiones(){
    	List<Actividad> sesiones = new ArrayList<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(CSV_SESIONES))) {
			String linea = null;
			Actividad s = null;
			in.readLine();		
			
			while ((linea = in.readLine()) != null) {
				String[] campos = linea.split(";");
				s = new Actividad(campos[0],0,null,0,null,Integer.parseInt(campos[2]),campos[1],0,null);
				
				if (s!= null) {
					sesiones.add(s);
				}
			}			
			
		} catch (Exception ex) {
			logger.warning(String.format("Error leyendo sesiones del CSV: %s", ex.getMessage()));
		}
		
		return sesiones;
    }
    
    public Actividad getIdPorSesion(Actividad a) {
		String sql = "SELECT * FROM sesion WHERE nombre_actividad = ? and fecha_sesion = ? LIMIT 1";
		
		try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
		     PreparedStatement pStmt = con.prepareStatement(sql)) {			
			
			pStmt.setString(1, a.getNombre());
			pStmt.setString(2, a.getFecha().toString());
			
			ResultSet rs = pStmt.executeQuery();			

			if (rs.next()) {
				a.setIdSesion(rs.getInt("id_sesion"));
			}
			
			rs.close();
			
			logger.info(String.format("Se ha recuperado la sesion %s", a.getNombre()));			
		} catch (Exception ex) {
			logger.warning(String.format("Error recuperar la sesion con nombre %s: %s", a.getNombre(), ex.getMessage()));						
		}		
		
		return a;
	}
    
    
    /** 
	 * 
	 * 
	 * 
	 * GESTION DE LA TABLA PARTICIPACIONES:
	 * 
	 * 
	 * 
	 * **/
    
    
    
    public static void insertarParticipacion(String dniUsuario, int idActividad) {
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING)) {
            String sql = """
                INSERT INTO participa (dni_usuario, id_sesion) 
                VALUES (?, ?)
            """;

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, dniUsuario);
            pstmt.setInt(2, idActividad);

            if (1 == pstmt.executeUpdate()) {
                System.out.format("\n - Usuario %s inscrito a la actividad %d", dniUsuario, idActividad);
            } else {
                System.out.println("\n - No se pudo inscribir al usuario.");
            }

            pstmt.close();
        } catch (Exception ex) {
            System.err.format("\n* Error al insertar participación: %s", ex.getMessage());
            ex.printStackTrace();
        }
    }
   
    
    public static void eliminarParticipacion(String dniUsuario, int idActividad) {
        String sql = "DELETE FROM participa WHERE dni_usuario = ? AND id_sesion = ?";
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, dniUsuario);
            pstmt.setInt(2, idActividad);            
            
            if (1 == pstmt.executeUpdate()) {
                System.out.format("\n - Usuario %s eliminado de la actividad %d", dniUsuario, idActividad);
            } else {
                System.out.println("\n - No se pudo eliminar al usuario.");
            }
            
            pstmt.close();
        } catch (SQLException e) {
            System.err.format("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
   
        }
    }
    
    public static Boolean obtenerParticipacionExiste(String dniUsuario, int idSesion) {
        Boolean existe = false;
        String sql = """
        	SELECT count(*) FROM participa
        	WHERE id_sesion = ? AND dni_usuario = ?;
        """;
        
        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, idSesion);
            pstmt.setString(2, dniUsuario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.getInt(1) == 1) {
            	existe = true;
                System.out.println("Existe una participación con ese ID.");          
            } else {
                System.out.println("No se encontró ninguna participación con ese ID.");
            }
        } catch (Exception ex) {
            System.err.format("\n* Error al obtener participación por ID: %s", ex.getMessage());
            ex.printStackTrace();
        }

        return existe;
    }

    
    public List<String> obtenerTodasLasParticipaciones() {
        List<String> participaciones = new ArrayList<>();
        String sql = """
            SELECT * FROM participa
        """;

        try (Connection con = DriverManager.getConnection(CONNECTION_STRING);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String participacion = String.format(
                    "%s, %s",
                    rs.getString("dni_usuario"),
                    rs.getString("id_sesion")
                );
                participaciones.add(participacion);
            }
        } catch (Exception ex) {
            System.err.format("\n* Error al obtener participaciones: %s", ex.getMessage());
            ex.printStackTrace();
        }

        return participaciones;
    }
    
}

