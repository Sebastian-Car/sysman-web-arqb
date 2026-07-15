package com.sysman.general;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.Sentencia;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author apineda
 * @version 1, 14/06/2016
 */
@ManagedBean
@ViewScoped
public class FrmadminactualizaControlador extends BeanBaseModal {
    // <DECLARAR_ATRIBUTOS>
    private final String extensionSysman;
    private final String extensionSysmanDs;
    private final String nombreArchivoHistorial;
    private final String cError;
    private final String cEtapa;
    private final String cPiePaginaUno;
    private final String cPiePaginaDos;

    private String etapa;
    private StringBuilder elementos = new StringBuilder();
    private String archivos;
    private String ubicacion;
    private String ubicacionSeguimiento;
    private String sentenciaActual;
    private String cadenaError;
    private String nombreLog;
    private int revisarLog;
    private int archivosAbiertos;
    private int totalAfectados;
    private Statement stmt;
    private Statement stmtDS;
    private ConectorPool con;
    private ConectorPool conDS;

    private Calendar fechaActual;
    private File folder;
    private String anio;
    private String mes;
    private String dia;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmadminactualizaControlador
     */
    public FrmadminactualizaControlador() {
        super();
        etapa = "";
        extensionSysman = ".sysman";
        extensionSysmanDs = ".sysmanDs";
        nombreArchivoHistorial = "historial.txt";
        cError = "Error-->";
        cEtapa = "\r\n\r\nEtapa";
        cPiePaginaUno = ": ***********";
        cPiePaginaDos = "***********\r\n\r\n";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMADMINACTUALIZA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            verUltimaActualizacion();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirBtnEjecutar() {
        // <CODIGO_DESARROLLADO>

        int recorreArchivos = 0;
        int totalArchivosEnCarpeta = 0;
        String tipo = "";
        StringBuilder archivosNoLeidos = new StringBuilder();
        String mensaje = "";
        elementos.append("");
        archivos = "";
        revisarLog = 0;
        archivosAbiertos = 0;
        sentenciaActual = "";
        totalAfectados = 0;
        cadenaError = "";
        fechaActual = Calendar.getInstance();
        anio = String.valueOf(fechaActual.get(Calendar.YEAR));
        mes = String.valueOf(fechaActual.get(Calendar.MONTH) + 1);
        dia = String.valueOf(fechaActual.get(Calendar.DAY_OF_MONTH));

        try {
            etapa = "1.Conectando con la base de datos";
            con = new ConectorPool();
            con.conectar(ConectorPool.ESQUEMA_SYSMANK);
            stmt = con.getConection().createStatement();

            conDS = new ConectorPool();
            conDS.conectar(ConectorPool.ESQUEMA_SYSMAN);
            stmtDS = conDS.getConection().createStatement();

            String rutaBD = consultarRuta();
            if (SysmanFunciones.validarVariableVacio(rutaBD)) {
                return;
            }
            etapa = "4.Verificando ruta y lista de archivos";
            File[] listaDeArchivos = folder.listFiles();

            for (recorreArchivos = 0; recorreArchivos < listaDeArchivos.length; recorreArchivos++) {
                if (listaDeArchivos[recorreArchivos].isFile()) {
                    archivos = listaDeArchivos[recorreArchivos].getName();

                    totalArchivosEnCarpeta = totalArchivosEnCarpeta + 1;
                    // Merge registros SYSMANIRIS
                    if (archivos.endsWith(extensionSysman)) {
                        etapa = "5.Ejecutando .sysman";
                        tipo = extensionSysman;
                        extraerSentencias(tipo);
                    }
                    // Archivos para creacion o reemplazo de vistas
                    // SYSMANDS.
                    else if (archivos.endsWith(extensionSysmanDs)) {
                        etapa = "6.Ejecutando .sysmanDs";
                        tipo = extensionSysmanDs;
                        extraerSentencias(tipo);
                    }
                    // Archivos que contienen Script para la creacion
                    // de tablas, restricciones y campos
                    else if (archivos.endsWith(".sql")) {
                        etapa = "7.Ejecutando .sql";
                        extraerSentenciasArchivo();
                    }
                    // Archivos que contienen Script encriptados para
                    // la creacion o reemplazo de paquetes
                    else if (archivos.endsWith(".plb")) {
                        etapa = "8.Ejecutando .plb";
                        extraerPaquete();
                    }
                    else {
                        archivosNoLeidos.append(
                                        archivosNoLeidos + archivos + " ");
                    }
                }
            } // Finaliza for listaDeArchivos

            if ((recorreArchivos == 0) || (archivosAbiertos == 0)) {
                elementos.append(
                                "No existen archivos de actualizacion en la ruta especificada. "
                                    + ubicacion);
                JsfUtil.agregarMensajeError(
                                "No existen archivos de actualizacion en la ruta especificada "
                                    + ubicacion);
                return;
            }

            etapa = "9.Verificacion elementos compilados";
            compilar();

            etapa = "10.Armando mensaje de respuesta";

            if ((totalArchivosEnCarpeta - archivosAbiertos) != 0) {
                mensaje = "Advertencia-->No se ejecutaron los siguientes archivos debido a que la extension no es aceptada: "
                    + archivosNoLeidos;
                sentenciaActual = "";
                elementos.append(mensaje);
                crearLog();
            }
            if (revisarLog != 0) {
                mensaje = mensaje + "Total errores: " + revisarLog
                    + "\nPor favor revise el log de errores "
                    + ubicacionSeguimiento
                    + nombreLog + "\n"
                    + cadenaError;
            }
            else {
                mensaje = mensaje + "Proceso finalizado exitosamente!";
            }
            etapa = "11.Guardando historial";
            guardarHistorial();

            elementos.append("Se ejecutaron " + archivosAbiertos + " de "
                + totalArchivosEnCarpeta + " archivos... \n"
                + "Total de elementos afectados: " + totalAfectados + "\n"
                + mensaje);

        } // Cierra try
        catch (Exception e) {
            elementos.append(cError + e.getMessage() + cEtapa
                + etapa
                + cPiePaginaUno + archivos + cPiePaginaDos);
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            crearLog();
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.getConection().close();
                }
                if (stmtDS != null) {
                    stmtDS.close();
                }
                if (conDS != null) {
                    conDS.getConection().close();
                }
            }
            catch (SQLException e) {
                elementos.append(cError + e.getMessage() + cEtapa
                    + etapa + cPiePaginaUno + archivos
                    + cPiePaginaDos);
                Logger.getLogger(FrmadminactualizaControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public String consultarRuta() {
        File folderSeguimiento;
        Statement stmtRuta = null;
        ConectorPool conRuta = null;
        ResultSet rs = null;
        String ruta = "";
        try {
            conRuta = new ConectorPool();
            conRuta.conectar(ConectorPool.ESQUEMA_SYSMAN);
            stmtRuta = conRuta.getConection().createStatement();

            rs = stmtRuta.executeQuery(
                            "SELECT RUTA_ARCHIVOS FROM APLICACIONES WHERE APLICACION ="
                                + SessionUtil.getModulo());

            if (rs.next()) {
                ruta = rs.getString("RUTA_ARCHIVOS");
            }
            if (SysmanFunciones.validarVariableVacio(ruta)) {
                elementos.append(
                                "No se encuentra configurada la ruta para los archivos de la aplicacion "
                                    + SessionUtil.getModulo()
                                    + ", por favor registre la ruta en el formulario de Aplicaciones y asegurese de que los archivos de actualizacion se encuentren en la carpeta.");
                JsfUtil.agregarMensajeError(elementos.toString());
            }
            else {
                ubicacion = ruta + "actualizaciones" + File.separator;

                etapa = "3.Verificando ubicacion y obteniendo archivos";
                folder = new File(ubicacion);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                // Ubicacion en donde se almacena log e historial de
                // actualizaciones
                ubicacionSeguimiento = ubicacion + "seguimiento"
                    + File.separator;
                folderSeguimiento = new File(ubicacionSeguimiento);
                if (!folderSeguimiento.exists()) {
                    folderSeguimiento.mkdirs();
                }
            }
        }
        catch (SQLException | NamingException e) {
            elementos.append(cError + e.getMessage() + cEtapa
                + etapa
                + cPiePaginaUno + archivos + cPiePaginaDos);
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (stmtRuta != null) {
                    stmtRuta.close();
                }
                if (conRuta != null) {
                    conRuta.getConection().close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                elementos.append(cError + e.getMessage() + cEtapa
                    + etapa + cPiePaginaUno + archivos
                    + cPiePaginaDos);
                Logger.getLogger(FrmadminactualizaControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return ruta;
    }

    public void crearLog() {
        FileWriter logArchivo = null;
        revisarLog = revisarLog + 1;
        String novedad;
        int nCaracteres;
        try {
            nombreLog = "log" + anio + "m" + mes + "d" + dia + ".txt";
            if (!new File(ubicacionSeguimiento + nombreLog).exists()) {
                logArchivo = new FileWriter(
                                new File(ubicacionSeguimiento + nombreLog),
                                false);
                logArchivo.close();
            }
            logArchivo = new FileWriter(
                            new File(ubicacionSeguimiento + nombreLog), true);

            // Empieza a escribir en el archivo
            logArchivo.write((dia + "/" + mes + "/" + anio
                + ";" + fechaActual.get(Calendar.HOUR_OF_DAY)
                + ":" + fechaActual.get(Calendar.MINUTE)
                + ":" + fechaActual.get(Calendar.SECOND)) + ";"
                + elementos + "..." + sentenciaActual
                + "\r\n");

            nCaracteres = sentenciaActual.length();
            if (nCaracteres > 46) {
                novedad = elementos + "..." + sentenciaActual.substring(0, 46)
                    + "\n";
            }
            else {
                novedad = elementos + "..." + sentenciaActual + "\n";
            }
            cadenaError = cadenaError + novedad;

        }
        catch (IOException e) {
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (logArchivo != null) {
                    logArchivo.close();
                }
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public void guardarHistorial() {
        FileWriter historialArchivo = null;
        String resumen;

        try {
            // Pregunta el archivo existe, caso contrario crea uno con
            // el nombre historial.txt
            if (!new File(ubicacionSeguimiento + nombreArchivoHistorial)
                            .exists()) {
                historialArchivo = new FileWriter(new File(
                                ubicacionSeguimiento + nombreArchivoHistorial),
                                false);

                historialArchivo.close();
            }
            historialArchivo = new FileWriter(
                            new File(ubicacionSeguimiento
                                + nombreArchivoHistorial),
                            true);

            if (revisarLog != 0) {
                resumen = "Proceso efectuado con errores; Archivo registrado: "
                    + nombreLog;
            }
            else {
                resumen = "Proceso finalizado correctamente";
            }
            // Empieza a escribir en el archivo
            historialArchivo.write(
                            ("Fecha y hora: " + dia + "/" + mes + "/" + anio
                                + ";" + fechaActual
                                                .get(Calendar.HOUR_OF_DAY)
                                + ":" + fechaActual
                                                .get(Calendar.MINUTE)
                                + ":"
                                +
                                fechaActual.get(Calendar.SECOND))
                                + "; Usuario que ejecuto ultima actualizacion: "
                                + SessionUtil.getUser().getCodigo() + ";"
                                + " Total Errores: " + revisarLog
                                + "; Resultado: " + resumen
                                + "\r\n");
            historialArchivo.close(); // Se cierra el archivo
        }
        catch (IOException e) {
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (historialArchivo != null) {
                    historialArchivo.close();
                }
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public void verUltimaActualizacion() {
        String ultimaActualizacion = "";
        String rutaBD = "";
        String file = nombreArchivoHistorial;
        LineNumberReader ln = null;
        FileReader archivo = null;
        try {
            etapa = "2.Consultando ruta";
            rutaBD = consultarRuta();

            if ((rutaBD == null) || rutaBD.isEmpty()) {
                return;
            }
            file = ubicacionSeguimiento + file;
            if (new File(file)
                            .exists()) {
                int numLinea = 0;// Numero de linea leido
                archivo = new FileReader(file);

                ln = new LineNumberReader(archivo);
                while (true) {
                    String linea = ln.readLine();
                    if (numLinea == ln.getLineNumber()) {
                        break;
                    }
                    ultimaActualizacion = linea;
                    numLinea = ln.getLineNumber();// Almacena el
                                                  // numero de linea
                }
            }

            elementos.append("Datos de la última actualización: \n"
                + ultimaActualizacion + "\n"
                + "\nAún no se ha ejecutado una nueva actualización, recuerde que este proceso puede tardar unos minutos... Oprima el botón EJECUTAR ACTUALIZACIÓN");

        }
        catch (Exception e) {
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            if (archivo != null) {
                try {
                    archivo.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
        }

    }

    public void compilar() {
        sentenciaActual = "";
        ResultSet rs = null;
        try {
            String compilarTodo = "begin FOR cur IN (SELECT OBJECT_NAME, OBJECT_TYPE FROM USER_OBJECTS WHERE OBJECT_TYPE IN ('PACKAGE','PACKAGE BODY','VIEW')) LOOP "
                +
                "BEGIN " +
                "if cur.OBJECT_TYPE = 'PACKAGE BODY' then " +
                "EXECUTE IMMEDIATE 'alter package ' || cur.OBJECT_NAME || ' compile body'; "
                +
                "else " +
                "EXECUTE IMMEDIATE 'alter ' || cur.OBJECT_TYPE || ' ' || cur.OBJECT_NAME || ' compile'; "
                +
                "end if; " +
                "EXCEPTION " +
                "WHEN OTHERS THEN NULL; " +
                "END; " +
                "end loop; end;";

            stmtDS.execute(compilarTodo);

            String compilar = "SELECT OBJECT_TYPE, OBJECT_NAME FROM USER_OBJECTS WHERE STATUS = 'INVALID' AND OBJECT_TYPE IN ('VIEW','PACKAGE','PACKAGE BODY')";
            rs = stmtDS.executeQuery(compilar);
            while (rs.next()) {
                elementos.append("Error--> No se pudo compilar el objeto "
                    + rs.getString("OBJECT_NAME") + " de tipo "
                    + rs.getString("OBJECT_TYPE"));
                crearLog();
            }

        }
        catch (SQLException e) {
            elementos.append(cError + e.getMessage() + cEtapa
                + etapa
                + cPiePaginaUno + archivos + cPiePaginaDos);
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            crearLog();
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void extraerSentencias(String tipo) {
        int respuesta = 0;
        ObjectInputStream file = null;

        try (FileInputStream inputFile = new FileInputStream(
                        ubicacion + archivos)) {
            ArrayList<Sentencia> listaSentencias;
            // Stream para leer archivo
            file = new ObjectInputStream(inputFile);

            archivosAbiertos = archivosAbiertos + 1;

            // Se lee el objeto de archivo y este debe convertirse al
            // tipo de clase que corresponde
            listaSentencias = (ArrayList<Sentencia>) file.readObject();
            file.close();

            for (int i = 0; i < listaSentencias.size(); i++) {

                sentenciaActual = listaSentencias.get(i).getSentencia();
                // Merge registros SYSMANIRIS
                if (tipo.equals(extensionSysman)) {
                    respuesta = stmt.executeUpdate(sentenciaActual);
                    totalAfectados = totalAfectados + respuesta;
                }
                // Vistas SYSMANDS
                if (tipo.equals(extensionSysmanDs)) {
                    stmtDS.executeUpdate(sentenciaActual);
                    totalAfectados = totalAfectados + 1;
                }

            }

        }
        catch (IOException | ClassNotFoundException | SQLException e) {
            elementos.append(cError + e.getMessage() + cEtapa
                + etapa
                + cPiePaginaUno + archivos + cPiePaginaDos);
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            crearLog();
        }
        finally {
            try {
                if (file != null) {
                    file.close();
                }
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void extraerSentenciasArchivo() {
        String sqllinea;
        StringBuilder sqlbuffer = new StringBuilder();
        ArrayList<String> sqlLista = new ArrayList<>();

        try (BufferedReader leer = new BufferedReader(new InputStreamReader(
                        new FileInputStream(ubicacion + archivos), "UTF8"));) {

            archivosAbiertos = archivosAbiertos + 1;
            while ((sqllinea = leer.readLine()) != null) {
                // Ignora comentarios que comienzan con --
                int comentario = sqllinea.indexOf("--");
                if (comentario != -1) {
                    if (sqllinea.startsWith("--")) {
                        sqllinea = "";
                    }
                    else {
                        sqllinea = sqllinea.substring(0, comentario - 1);
                    }
                }

                sqllinea = sqllinea.replace("/", ";");

                // El + " " es necesario, porque de lo contrario el
                // contenido antes y despues de un salto de linea se
                // concatenan
                if (sqllinea != null) {
                    sqlbuffer.append(sqllinea + " ");
                }
            }
            leer.close();

            // Se pone para el ";" que separar las sentencias
            String[] separador = sqlbuffer.toString().split(";");

            // Se verifica que posiciones estan vacias
            for (int i = 0; i < separador.length; i++) {
                if (!"".equals(separador[i].trim())
                    && !"\t".equals(separador[i].trim())) {
                    sqlLista.add(separador[i]);
                }
            }

            for (String query : sqlLista) {

                sentenciaActual = query;
                stmtDS.execute(query);
                totalAfectados = totalAfectados + 1;

            }
        }
        catch (IOException | SQLException e) {
            elementos.append(cError + e.getMessage() + cEtapa
                + etapa
                + cPiePaginaUno + archivos + cPiePaginaDos);
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            crearLog();
        }
    }

    public void extraerPaquete() {
        String sqllinea;
        StringBuilder sqlbuffer = new StringBuilder();

        try (BufferedReader leer = new BufferedReader(new InputStreamReader(
                        new FileInputStream(ubicacion + archivos), "UTF8"))) {

            archivosAbiertos = archivosAbiertos + 1;
            while ((sqllinea = leer.readLine()) != null) {
                sqlbuffer.append(sqllinea + "\n");
            }

            sentenciaActual = sqlbuffer.toString();
            stmtDS.execute(sentenciaActual);
            totalAfectados = totalAfectados + 1;

        }
        catch (SQLException | IOException e) {
            elementos.append(cError + e.getMessage() + cEtapa
                + etapa + cPiePaginaUno + archivos
                + cPiePaginaDos);
            Logger.getLogger(FrmadminactualizaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            crearLog();

        }

    }

    public StringBuilder getElementos() {
        return elementos;
    }

    public void setElementos(StringBuilder elementos) {
        this.elementos = elementos;
    }

}
