/*-
 * ImportarConfiguracionTxtControlador.java
 *
 * 1.0
 * 
 * 02/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;

/**
 * Esta clase es el controlador para el formularioque permite importar
 * la configuraon base en Access "Importartxt", el cual es llamado
 * desde Entes de Control\Schip - CGR\Configuraci�n /
 * Informes\Configuraci�n Archivos Base
 *
 * 
 * @version 1, 08/03/2017 11:52:36
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ImportarConfiguracionTxtControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio de trabajo seleccionado en el
     * formulario
     */
    private String anioTrabajo;
    /**
     * Atributo que almacena el archivo a importar seleccionado en el
     * formulario, que en realidad es el nombre de la tabla en la que
     * se realizara la carga de datos
     */
    private String archivo;
    /**
     * Atributo que almacena la fila en la que se encuentra el
     * encabezado en el archivo a importar
     */
    private String fila;
    /**
     * Atributo que almacena el codigo Schip asociado a la compania de
     * ingreso
     */
    private String codEntidad;
    /**
     * Atributo que almacena la ruta del archivo que se desea importar
     */
    private String ruta;
    /**
     * Atributo que almacena el tipo de entidad de la compania de
     * ingreso
     */
    private String tipoEntidad;
    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    /**
     * Atributo usado como bandera parala visualizacion del dialogo
     * "confirmarEliminar(DG179)"
     */
    private boolean confirmarEliminarVisible;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de anoTrabajo
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Listado de registros para el comboBox de anoTrabajo
     */
    private List<Registro> listaCodigoDetalle;
    /**
     * Atributo que permite la lectura de los datos contenidos en el
     * archivo que se carga
     */
    private String extension;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ImportarConfiguracionTxtControlador
     */
    public ImportarConfiguracionTxtControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1331;
            validarPermisos();
            // <INI_ADICIONAL>
            anioTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
            fila = "1";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnoTrabajo();
        cargarListaCodigoDetalle();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        obtenerCodigoSchip();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        listaAnoTrabajo = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT " +
                            "     ANO.NUMERO " +
                            " FROM " +
                            "     ANO " +
                            " WHERE " +
                            "     ANO.COMPANIA = '" + compania + "' " +
                            " ORDER BY " +
                            "     ANO.NUMERO DESC");
    }

    /**
     * 
     * Carga la lista listaCodigoDetalle
     * 
     * Define la consulta definida como orgigen de datos para la lista
     * listaCodigoDetalle
     */
    public void cargarListaCodigoDetalle() {
        listaCodigoDetalle = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT   ROWNUM NUMERO" +
                            "         ,DECODE(ROWNUM" +
                            "         ,1,'CODENTIDADES_SCHIP'" +
                            "         ,2,'DEPENDENCIA_SCHIP'" +
                            "         ,3,'DESTINACION_SCHIP'" +
                            "         ,4,'ENTIDADES_SCHIP'" +
                            "         ,5,'FINALIDAD_GASTO_SCHIP'" +
                            "         ,6,'OEI_GASTOS_SCHIP'" +
                            "         ,7,'OEI_INGRESOS_SCHIP'" +
                            "         ,8,'PLAN_SCHIP'" +
                            "         ,9,'PLAN_SIRECI_SCHIP'" +
                            "         ,10,'RECURSO_SCHIP'" +
                            "         ,11,'VIGENCIA_GASTO_SCHIP'" +
                            "         ,12,'VIGENCIATESORERIA_SCHIP'" +
                            "         ,13,'DESTINACION_REGALIAS_SCHIP'" +
                            "         ,14,'OEI_REGALIAS_SCHIP'" +
                            "         ,15,'RECURSO_REGALIAS_SCHIP') NOMBRE" +
                            " FROM DUAL             " +
                            " CONNECT BY ROWNUM < 16");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     * 
     * Valida que el archivo que contiene la informacion haya sido
     * cargado previamente y realiza el llamado al metodo importar
     * datos que realiza el cargue de la informacion
     *
     */

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        if (extension == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
            return;
        }
        try {
            int numeroHoja = obtenerNumeroHoja(workbook);

            if (numeroHoja == -1) {
                return;
            }
            importarDatos(workbook, numeroHoja);
            workbook.close();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
            archivo = null;
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Excel
     *
     * Permite definir la extension del archivo que se ha seleccionado
     * desde el formulario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoExcel(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        workbook = null;
        try {
            InputStream is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }

            String rutaArchivo = event.getFile().getFileName();
            extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length())
                            .substring(1, rutaArchivo.substring(
                                            rutaArchivo.indexOf('.'),
                                            rutaArchivo.length()).length());
            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if (workbook == null) {
                if ("xls".equals(extension)) {
                    workbook = new HSSFWorkbook(is);
                }
                else {
                    workbook = new XSSFWorkbook(is);
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Identifica el numero de hoja que se desea importar dentro del
     * archivo que se ha cargado
     * 
     * @param workbook
     * contenido del archivo excel que ha sido cargado por el usuario
     * @return numero de hoja correspondiente a la hoja a importar
     * dentro del archivo
     */
    private int obtenerNumeroHoja(Workbook workbook) {
        int hoja = -1;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (archivo.toUpperCase()
                            .equalsIgnoreCase(workbook.getSheetName(i))) {
                hoja = i;
            }
        }
        if (hoja == -1) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2904"));
        }
        return hoja;
    }

    /**
     * Realiza la validacion de vacio en el valor de la celda y su
     * valor en tipo booleano
     * 
     * @param valorCelda
     * contenido de la celda a analizar
     * @return si la celda posee el valor "False"
     */
    private boolean validarMovimiento(String valorCelda) {
        return !SysmanFunciones.validarVariableVacio(valorCelda)
            && "FALSE".equalsIgnoreCase(valorCelda);
    }

    /**
     * Valida si una celda especifica dentro de una hoja de datos
     * viene nula o esta en blanco
     * 
     * @param sheet
     * Hoja de datos que se va a analizar
     * @param rowNum
     * Numero de fila que se evaluara
     * @param column
     * Numero de la columna dentro de la fila que se evaluara
     * @return Verdadero si la celda posee valor
     */
    private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
        boolean respuesta = false;
        Row rAux = sheet.getRow(rowNum);
        if (rAux != null) {
            Cell cellAux = rAux.getCell(column, Row.RETURN_BLANK_AS_NULL);
            if (cellAux != null) {
                cellAux.setCellType(Cell.CELL_TYPE_STRING);
                if (!SysmanFunciones.validarVariableVacio(
                                cellAux.getStringCellValue())
                    || "PLAN_SCHIP".equalsIgnoreCase(archivo)) {
                    respuesta = true;

                }
            }

        }
        return respuesta;
    }

    /**
     * Permite validar si un campo que viene vac�o cuando se est�
     * importando "PLAN_SCHIP" no sea la primera columna
     * 
     * @param campos
     * estructura que almacena los campo que se van a insertar
     * @param column
     * numero de columna dentro de la fila que se est� leyebdo
     * @return Verdadero si la estructura que almacena los campos no
     * est� vac�a
     */
    private boolean validarCampos(Map<String, Object> campos, int column) {
        return !campos.isEmpty() || column >= 0;
    }

    /**
     * Recorre la hoja en la que se encuentran los datos que se van a
     * importar y realiza el proceso de insercion de los mismos
     * 
     * @param workbook
     * Contenido del archivo seleccionado
     * @param numeroHoja
     * Numero de hoja con la informacion a importar
     */
    private void importarDatos(Workbook workbook, int numeroHoja) {
        Sheet sheet = workbook.getSheetAt(numeroHoja);
        // Almacena los campo y valores que se enviaran en la
        // insercion
        HashMap<String, Object> campos = new HashMap<>();
        // Almacena el valor de los encabezados, que para este
        // caso es el nombre de los campos de las tablas en las
        // que se realizara la insercion
        HashMap<String, Object> nombreCampos = new HashMap<>();
        int colMovimiento = -1;
        for (int i = 0; i < Math
                        .max(sheet.getRow(Integer.parseInt(fila) - 1)
                                        .getLastCellNum(), 0); i++) {
            Row r = sheet.getRow(Integer.parseInt(fila) - 1);
            nombreCampos.put(String.valueOf(i),
                            r.getCell(i, Row.RETURN_BLANK_AS_NULL)
                                            .getStringCellValue()
                                            .toUpperCase());
            if ("MOVIMIENTO".equalsIgnoreCase(
                            r.getCell(i, Row.RETURN_BLANK_AS_NULL)
                                            .getStringCellValue())) {
                colMovimiento = i;
            }

        }
        // Filas
        for (int rowNum = Integer.parseInt(fila); rowNum <= sheet
                        .getLastRowNum(); rowNum++) {
            // Columnas
            for (int column = 0; column < Math
                            .max(sheet.getRow(Integer.parseInt(fila))
                                            .getLastCellNum(),
                                            0); column++) {

                // Valida que la fila y la celda especifica que se
                // va a leer no sean nulas
                if (validarFilaCeldaVacia(sheet, rowNum, column)) {

                    Row r = sheet.getRow(rowNum);
                    Cell cell = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    // Almacena temporalmente el valor de la celda
                    String valor = "";

                    // Valida si el valor de la columna es un booleano
                    // y realiza la conversion numerica para hacer la
                    // insercion de forma adecuada
                    if (colMovimiento == column) {
                        valor = validarMovimiento(cell.getStringCellValue())
                            ? "0"
                            : "-1";
                    }
                    else if (validarCampos(campos, column)) {
                        valor = !SysmanFunciones.validarVariableVacio(
                                        cell.getStringCellValue())
                                            ? cell.getStringCellValue()
                                            : null;
                    }

                    campos.put(nombreCampos.get(String.valueOf(column))
                                    .toString(), valor);
                }
                else {
                    return;
                }
            }
            campos.put("DATE_CREATED", new Date());
            campos.put("CREATED_BY", SessionUtil.getUser().getCodigo());
            insertarDatos(archivo, campos);
            campos = new HashMap<>();
        }
    }

    /**
     * Realiza el llamado al metodo de Insertar ubicado en la clase
     * acciones, para realizar el registro de la informacion que se
     * envia por parametro
     * 
     * @param archivo
     * nombre de la tabla en la que se realizara la insercion
     * @param campos
     * con sus repectivos valores a insertar
     */
    private void insertarDatos(String archivo, HashMap<String, Object> campos) {
        try {
            Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, archivo, campos);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Obtiene el numero que identifica el valor seleccionado en el
     * combo "CodigoDetalle"
     * 
     * @return identificaador del texto que almacena el atributo
     * "archivo" dentro de la lista "listaCodigoDetalle"
     */
    private String obtenerIdArchivo() {
        return service.buscarEnLista(archivo, "NOMBRE", "NUMERO",
                        listaCodigoDetalle);
    }

    /**
     * Permite definir la ruta del archivo plano a cargar de acuerdo a
     * la opcion seleccionada en el combo CodigoDetalle
     */
    public void cambiarCodigoDetalle() {
        evaluarTablaVacia();

        // Esta funcionalidad no se esta usando debido a que se cambia
        // el formato del archivo en que se carga la informacion, en
        // este switch se
        // visualizaba la ruta en la que deberian estar los archivos
        // planos que contenian la informacion a cargar
        String idArchvo = obtenerIdArchivo();
        switch (idArchvo) {
        case "1":
            ruta = idioma.getString("TB_TB2880");
            break;
        case "2":
            ruta = idioma.getString("TB_TB2881");
            break;
        case "3":
            ruta = idioma.getString("TB_TB2882");
            break;
        case "4":
            ruta = idioma.getString("TB_TB2883");
            break;
        case "5":
            ruta = idioma.getString("TB_TB2884");
            break;
        case "6":
            ruta = idioma.getString("TB_TB2879");
            break;
        default:
            cambiarCodigoDetalleAux();
            break;
        }
    }

    /**
     * Metodo complementario al anterior, el cual permite definir la
     * ruta del archivo plano a cargar de acuerdo a la opcion
     * seleccionada en el combo CodigoDetalle
     */
    private void cambiarCodigoDetalleAux() {
        String idArchvo = obtenerIdArchivo();
        switch (idArchvo) {
        case "7":
            ruta = idioma.getString("TB_TB2885");
            break;
        case "8":
            ruta = idioma.getString("TB_TB2886");
            break;
        case "10":
            ruta = idioma.getString("TB_TB2889");
            break;
        case "11":
            ruta = idioma.getString("TB_TB2890");
            break;
        case "12":
            ruta = idioma.getString("TB_TB2888");
            break;
        case "13":
            ruta = idioma.getString("TB_TB2887");
            break;
        default:
            ruta = idioma.getString("TB_TB2891");
            break;
        }
    }

    /**
     * Evalua si la tabla seleccionada posee valores
     */
    private void evaluarTablaVacia() {
        String sql = "SELECT DISTINCT 'X' DATOS FROM " + archivo.toUpperCase();
        Registro rs = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);
        if (!SysmanFunciones.validarCampoVacio(rs.getCampos(), "DATOS")) {
            confirmarEliminarVisible = true;
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * confirmarEliminar en la vista
     *
     * Limpia los valores que se encuentran en la tabla que se va a
     * trabajar
     *
     */
    public void aceptarconfirmarEliminar() {
        try {
            Acciones.eliminarRegistro(ConectorPool.ESQUEMA_SYSMAN,
                            archivo.toUpperCase(), "1 = 1");
        }
        catch (SQLException | NamingException e) {
            logger.error(e.getMessage(), e);
            String msj = idioma.getString("TB_TB3022");
            msj = msj.replace("s$archivo$s", archivo);
            JsfUtil.agregarMensajeError(msj);
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * confirmarEliminar en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void cancelarconfirmarEliminar() {
        // <CODIGO_DESARROLLADO>
        archivo = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Permite obtener el codigo Schip y el tipo de compania
     * configurados para la compania de ingreso
     */
    private void obtenerCodigoSchip() {
        String sql = "SELECT CODIGO, "
            + "     CODIGOSCHIP, " +
            "       TIPOENTIDAD " +
            "  FROM COMPANIA " +
            " WHERE CODIGO   = '" + compania + "'";
        Registro rs = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);
        if (rs != null) {
            if (!SysmanFunciones.validarCampoVacio(rs.getCampos(),
                            "CODIGOSCHIP")) {
                codEntidad = rs.getCampos().get("CODIGOSCHIP").toString();
                tipoEntidad = rs.getCampos().get("TIPOENTIDAD").toString();
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2878"));
            }
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2877"));
        }
    }

    /**
     * Metodo que permite la visualizacion de mensajes informativos al
     * cargar el formulario
     */
    public void mensajesInicioModal() {
        obtenerCodigoSchip();
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anioTrabajo
     * 
     * @return anioTrabajo
     */
    public String getAnioTrabajo() {
        return anioTrabajo;
    }

    /**
     * Asigna la variable anioTrabajo
     * 
     * @param anioTrabajo
     * Variable a asignar en anioTrabajo
     */
    public void setAnioTrabajo(String anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
    }

    /**
     * Retorna la variable archivo
     * 
     * @return archivo
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * Asigna la variable archivo
     * 
     * @param archivo
     * Variable a asignar en archivo
     */
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    /**
     * Retorna la variable fila
     * 
     * @return fila
     */
    public String getFila() {
        return fila;
    }

    /**
     * Asigna la variable fila
     * 
     * @param fila
     * Variable a asignar en fila
     */
    public void setFila(String fila) {
        this.fila = fila;
    }

    /**
     * Retorna la variable codEntidad
     * 
     * @return codEntidad
     */
    public String getCodEntidad() {
        return codEntidad;
    }

    /**
     * Asigna la variable codEntidad
     * 
     * @param codEntidad
     * Variable a asignar en codEntidad
     */
    public void setCodEntidad(String codEntidad) {
        this.codEntidad = codEntidad;
    }

    /**
     * Retorna la variable ruta
     * 
     * @return ruta
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * Asigna la variable ruta
     * 
     * @param ruta
     * Variable a asignar en ruta
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Retorna la variable tipoEntidad
     * 
     * @return tipoEntidad
     */
    public String getTipoEntidad() {
        return tipoEntidad;
    }

    /**
     * Asigna la variable tipoEntidad
     * 
     * @param tipoEntidad
     * Variable a asignar en tipoEntidad
     */
    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    /**
     * Retorna la variable confirmarEliminarVisible
     * 
     * @return confirmarEliminarVisible
     */
    public boolean isConfirmarEliminarVisible() {
        return confirmarEliminarVisible;
    }

    /**
     * Asigna la variable confirmarEliminarVisible
     * 
     * @param confirmarEliminarVisible
     */
    public void setConfirmarEliminarVisible(boolean confirmarEliminarVisible) {
        this.confirmarEliminarVisible = confirmarEliminarVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoTrabajo
     * 
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     * 
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaCodigoDetalle
     * 
     * @return listaCodigoDetalle
     */
    public List<Registro> getListaCodigoDetalle() {
        return listaCodigoDetalle;
    }

    /**
     * Asigna la lista listaCodigoDetalle
     * 
     * @param listaCodigoDetalle
     * Variable a asignar en listaCodigoDetalle
     */
    public void setListaCodigoDetalle(List<Registro> listaCodigoDetalle) {
        this.listaCodigoDetalle = listaCodigoDetalle;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
