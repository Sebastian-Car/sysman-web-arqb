/*-
 * TareasayudaControlador.java
 *
 * 1.0
 * 
 * 04/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import com.sysman.ayudas.ejb.EjbAyudalineaRemote;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TareasAyudaControladorUrlEnum;
import com.sysman.general.enums.TareasayudaControladorEnum;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 04/06/2023
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  TareasayudaControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    //<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private int proceso;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String nombreProceso;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String aplicacion;
    private String salida;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos
     * cargarExcel y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoCargarExcel;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listalistaprocesos;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listalistaprocesosE;
    /**
     * variable EJB
     */
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;
  
    
    @EJB
    EjbAyudalineaRemote ejbAyudalinea;
    
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    /**
     * Variable que almacena la informacion del excel
     */
    private String cadena;
    private long contador;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TareasayudaControlador
     */
    public TareasayudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        contArchivoCargarExcel = new ContenedorArchivo();
        try {
            numFormulario=GeneralCodigoFormaEnum.FRM_AYUDA_TAREAS
                            .getCodigo();
            validarPermisos();
            
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
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
    public void inicializar(){
        
            enumBase = GenericUrlEnum.AYUDA_TAREAS;
            buscarLlave();
            reasignarOrigen();              
            registro= new Registro();
            cargarListalistaprocesos(); 
            cargarListalistaprocesosE();
            abrirFormulario();
        
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
        
        parametrosListado.put(
                        TareasayudaControladorEnum.PARAM0.getValue(),
                        String.valueOf(proceso));
        
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        TareasAyudaControladorUrlEnum.URL1911
                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        TareasAyudaControladorUrlEnum.URL1909
                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TareasAyudaControladorUrlEnum.URL1912
                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TareasAyudaControladorUrlEnum.URL1910
                                        .getValue());
        
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listalistaprocesos
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListalistaprocesos(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TareasAyudaControladorUrlEnum.URL1908
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        listalistaprocesos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.IDPROCESO.getName());
    }
    /**
     * 
     * Carga la lista listalistaprocesos
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void  cargarListalistaprocesosE(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TareasAyudaControladorUrlEnum.URL1908
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        listalistaprocesosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.IDPROCESO.getName());
    }
    
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cargar
     * en la vista
     * @param event
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirCargar() {
        Workbook workbook = null;
        cadena = "";
        try (FileInputStream file = new FileInputStream(contArchivoCargarExcel.getArchivo());) {

                if (validarArchivo()) {
                    
                        String rutaArchivo = contArchivoCargarExcel.getArchivo().getPath();

                        String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
                                        rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

                        if ("xls".equals(extension)) {
                                workbook = new HSSFWorkbook(file);
                        } else {
                                workbook = new XSSFWorkbook(file);
                        }
                        Sheet sheet = workbook.getSheet("Tareas");
                        long i = 1;
                        contador = 0;
                        for (Row row : sheet) {
                                contador++;
                        }
                        salida = null;
                        for (Row row : sheet) {

                                if (!validarCelda(row.getCell(0))) {
                                        break;
                                }
                                
                                capturaDatosExcel(row);
                                if ((i >= contador && !"".equals(cadena))) {
                                       cargarDatos();
                                }

                                i = i + 1;
                        }
                        ArchivosBean.generarPlano("ErroresActualizaTareas.txt", salida);
                        JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
                                                

                }
        } catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
    /**
     * Verifica el valor de la celda y retorna false si esta vacia.
     * 
     * @param celda Objeto de tipo <code>Cell</code>
     * @return false si la celda esta vacia.
     */
    private boolean validarCelda(Cell celda) {
            if (celda == null) {
                    return false;
            }

            return !celda.getStringCellValue().isEmpty();
    }

    private void capturaDatosExcel(Row row) {

            if (row.getRowNum() > 0) {

                    for (int i = 0; i < 11; i++) {
                            String val = "";

                            val = row.getCell(i) + "";
                                                        
                            cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
                    }
                    cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

                    cadena = cadena + SysmanConstantes.SEPARADOR_REG;
            }

    }

    private void cargarDatos() {
            try {
                    String parametro = (SysmanFunciones.esBdSqlServer()) ? cadena.replace("TO_CLOB(", "").replace(")", "")
                                    : cadena;
                  
                    salida = ejbAyudalinea.cargarTareas(proceso, parametro, SessionUtil.getUser().getCodigo());

            } catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
            }

    }

    /**
     * Valida que se suba un archivo
     * 
     * @return
     */
    public boolean validarArchivo() {

            if (contArchivoCargarExcel.getArchivo() == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
                    return false;
            } else {
                    return true;
            }
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Crear
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     * @throws IOException 
     *
     */
    public void oprimirCrear() throws IOException {
        
        archivoDescarga=null;
        HSSFWorkbook workbook = new HSSFWorkbook();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

                HSSFSheet excelSheet = null;
                /* Propiedades letra encabezado */
                Font font = workbook.createFont();
                font.setFontName("Calibri");
                font.setBold(true);

                // TamaÃ±o de letra
                font.setFontHeightInPoints((short) 8);
                
             // lista de la grilla de las tareas
                List<Registro> listaTareas;
                Map<String, Object> param = new HashMap<>();
                param.put("ID_PROCESO", proceso);
                
                listaTareas = RegistroConverter.toListRegistro(
                                        requestManager.getList(UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        TareasAyudaControladorUrlEnum.URL1913
                                                                                        .getValue())
                                                        .getUrl(), param));
                
                addValidationToSheet(workbook, excelSheet, listaTareas, 'A', 1, 10000, "Tareas");
                
                workbook.write(out);

                archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
                                "Plantilla Actualizar Tareas.xls");

        } catch (IOException | JRException | SystemException e) {
                e.printStackTrace();
        } finally {
                workbook.close();
        }
        
        
    }
    
    /**
     * 
     * @param workbook
     * @param targetSheet
     * @param options
     * @param column
     * @param fromRow
     * @param endRow
     * @param name
     */
    public static void addValidationToSheet(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
                    int fromRow, int endRow, String name) {
            String hiddenSheetName = name;
            Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
            int rowIndex = 0;
            Row row = optionsSheet.createRow(0);
            Cell cell = row.createCell(0);

            row = optionsSheet.createRow(0);

            cell = row.createCell(0);
            cell.setCellValue("ID PROCESO");

            cell = row.createCell(1);
            cell.setCellValue("ID TAREA");

            cell = row.createCell(2);
            cell.setCellValue("TIPO");

            cell = row.createCell(3);
            cell.setCellValue("ORDEN");

            cell = row.createCell(4);
            cell.setCellValue("DESCRIPCION");

            cell = row.createCell(5);
            cell.setCellValue("FORMULARIO");
            
            cell = row.createCell(6);
            cell.setCellValue("CONTROL");
            
            cell = row.createCell(7);
            cell.setCellValue("URL VIDEO");
            
            cell = row.createCell(8);
            cell.setCellValue("RUTA");
            
            cell = row.createCell(9);
            cell.setCellValue("TENERCUENTA");
            
            rowIndex = 1;
            for (Registro option : options) {
                    row = optionsSheet.createRow(rowIndex++);

                    cell = row.createCell(0);
                    cell.setCellValue(option.getCampos().get("ID_PROCESO").toString());
                    cell = row.createCell(1);
                    cell.setCellValue(option.getCampos().get("ID_TAREA").toString());
                    cell = row.createCell(2);
                    cell.setCellValue(option.getCampos().get("TIPO").toString());
                    cell = row.createCell(3);
                    cell.setCellValue(option.getCampos().get("ORDEN").toString());
                    cell = row.createCell(4);
                    cell.setCellValue(option.getCampos().get("DESCRIPCION").toString());
                    cell = row.createCell(5);
                    if (option.getCampos().get("FORMULARIO") == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(option.getCampos().get("FORMULARIO").toString());
                    }
                    cell = row.createCell(6);
                    if (option.getCampos().get("CONTROL") == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(option.getCampos().get("CONTROL").toString());
                    }
                    cell = row.createCell(7);
                    if (option.getCampos().get("URL_VIDEO") == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(option.getCampos().get("URL_VIDEO").toString());
                    }
                    cell = row.createCell(8);
                    if (option.getCampos().get("RUTA") == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(option.getCampos().get("RUTA").toString());
                    }
                    cell = row.createCell(9);
                    if (option.getCampos().get("TENERCUENTA") == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(option.getCampos().get("TENERCUENTA").toString());
                    }
                    
            }
            
    }

    /**
     * No se puede empezar con un nÃƒÂºmero
     *
     * @param name
     * @return
     */
    static String formatNameName(String name) {
            name = name.replace(" ", "").replace("-", "_").replace(":", ".");
            if (Character.isDigit(name.charAt(0))) {
                    name = "_" + name;
            }

            return name;
    }

    
    
    
    
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listalistaprocesos
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistaprocesos(SelectEvent event) {
        
        Registro registroAux = (Registro) event.getObject();
        proceso = Integer.parseInt(registroAux.getCampos().get(GeneralParameterEnum.IDPROCESO.getName()).toString());
        nombreProceso = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        aplicacion = registroAux.getCampos().get(GeneralParameterEnum.APLICACION.getName()).toString();
        registro.getCampos().put("ID_PROCESO", registroAux.getCampos().get(GeneralParameterEnum.IDPROCESO.getName()));
        reasignarOrigen();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listalistaprocesos
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistaprocesosE(SelectEvent event) {
        
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("ID_PROCESO").toString();
        proceso = Integer.parseInt(registroAux.getCampos().get(GeneralParameterEnum.IDPROCESO.getName()).toString());
        nombreProceso = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        aplicacion = registroAux.getCampos().get(GeneralParameterEnum.APLICACION.getName()).toString();
        registro.getCampos().put("ID_PROCESO", registroAux.getCampos().get(GeneralParameterEnum.IDPROCESO.getName()));
        reasignarOrigen();
        
    }
    
    //</METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
         * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
         * TODO DOCUMENTACION ADICIONAL
         */
        @Override
        public void cancelarEdicion(RowEditEvent event) {
            getListaInicial().load();
        }
        /**
         * Metodo ejecutado antes de realizar la insercion del registro
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override
        public boolean insertarAntes(){
            //<CODIGO_DESARROLLADO>
            
            registro.getCampos().put("ID_PROCESO", proceso);
            
            try
            {
            
            String[] parametros = {""};
            registro.getCampos().put("ID_TAREA", ejbSysmanUtil
                            .generarConsecutivoConValorInicial("AYUDA_TAREAS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            "ID_TAREA", "1"));
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            //</CODIGO_DESARROLLADO>
            return true;
        }
        /**
         * Metodo ejecutado despues de realizar la insercion del registro
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override
        public boolean insertarDespues(){
            //<CODIGO_DESARROLLADO>
            //</CODIGO_DESARROLLADO>
            return true;
        }
        /**
         * Metodo ejecutado antes de realizar la insercion y actualizacion
         * del registro
         * 
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override
        public boolean actualizarAntes(){
            //<CODIGO_DESARROLLADO>
            //</CODIGO_DESARROLLADO>
         return true;
    }
    /**
             * Metodo ejecutado despues de realizar la insercion y actualizacion
             * del registro
             * 
             * TODO DOCUMENTACION ADICIONAL
             * 
             * @return TODO VARIABLE
             */
            @Override   
            public boolean actualizarDespues(){
                //<CODIGO_DESARROLLADO>
                //</CODIGO_DESARROLLADO>
                return true;
            }
    /**
                 * Metodo ejecutado antes de realizar la eliminacion del
                 * registro
                 * 
                 * TODO DOCUMENTACION ADICIONAL
                 * 
                 * @return TODO VARIABLE
                 */
                @Override    
                public boolean eliminarAntes(){
                    //<CODIGO_DESARROLLADO>
                    //</CODIGO_DESARROLLADO>
                  return true;
                }
    /**
                     * Metodo ejecutado despues de realizar la eliminacion del
                     * registro
                     * 
                     * TODO DOCUMENTACION ADICIONAL
                     * 
                     * @return TODO VARIABLE
                     */
                    @Override   
                    public boolean eliminarDespues(){
                        //<CODIGO_DESARROLLADO>
                        //</CODIGO_DESARROLLADO>
                        return true;
                    }
                    /**
                     * Este metodo se ejecuta antes enviar la accion de actualizacion,
                     * en el se pueden remover valores auxiliares que no se desee o se
                     * deban enviar en el registro
                     */
                    @Override
                    public void removerCombos() {
                        
                        registro.getCampos().remove("TIPOD");
                                                
                    }
                    /**
                     * Este metodo es ejecutado despues de finalizar la insercion y
                     * edicion del registro se usa cuando se desean agregar valores
                     * al registro despues de dichas acciones
                     */
                    @Override
                    public void asignarValoresRegistro()
                    {
                        // TODO Auto-generated method stub
                    }
                    //<SET_GET_ATRIBUTOS>
                    /**
                     * Retorna la variable proceso
                     * 
                     * @return  proceso
                     */
                    public int getProceso() {
                        return proceso;
                    }
                    /**
                     * Asigna la variable  proceso
                     * 
                     * @param  proceso
                     * Variable a asignar en  proceso
                     */
                    public void setProceso(int proceso) {
                        this.proceso = proceso;
                    }
                    /**
                     * Retorna la variable nombreProceso
                     * 
                     * @return  nombreProceso
                     */
                    public String getNombreProceso() {
                        return nombreProceso;
                    }
                    /**
                     * Asigna la variable  nombreProceso
                     * 
                     * @param  nombreProceso
                     * Variable a asignar en  nombreProceso
                     */
                    public void setNombreProceso(String nombreProceso) {
                        this.nombreProceso = nombreProceso;
                    }
                    /**
                     * Retorna la variable aplicacion
                     * 
                     * @return  aplicacion
                     */
                    public String getAplicacion() {
                        return aplicacion;
                    }
                    /**
                     * Asigna la variable  aplicacion
                     * 
                     * @param  aplicacion
                     * Variable a asignar en  aplicacion
                     */
                    public void setAplicacion(String aplicacion) {
                        this.aplicacion = aplicacion;
                    }
                    /**
                     * Atributo usado para descargar contenidos de archivos desde la
                     * vista
                     */
                public StreamedContent getArchivoDescarga() {
                        return archivoDescarga;
                    }
                    /**
                     * Retorna el objeto contArchivoCargarExcel
                     * 
                     * @return contArchivoCargarExcel
                     */
                public ContenedorArchivo getContArchivoCargarExcel() {
                        return contArchivoCargarExcel;
                    }
                    /**
                     * Asigna el objeto contArchivoCargarExcel
                     * 
                     * @param contArchivoCargarExcel
                     * Variable a asignar en contArchivoCargarExcel
                     */
                    public void setContArchivoCargarExcel(ContenedorArchivo contArchivoCargarExcel) {
                        this.contArchivoCargarExcel = contArchivoCargarExcel;
                    }
                    
                    //</SET_GET_ATRIBUTOS>
                    //<SET_GET_PARAMETROS>
                    //</SET_GET_PARAMETROS>
                    //<SET_GET_LISTAS>
                    //</SET_GET_LISTAS>
                    //<SET_GET_LISTAS_COMBO_GRANDE>     
                    /**
                     * Retorna la lista listalistaprocesos
                     * 
                     * @return listalistaprocesos
                     */
                    public RegistroDataModelImpl getListalistaprocesos() {
                        return listalistaprocesos;
                    }
                    /**
                     * Asigna la lista listalistaprocesos
                     * 
                     * @param listalistaprocesos
                     * Variable a asignar en  listalistaprocesos
                     */
                    public void setListalistaprocesos(RegistroDataModelImpl listalistaprocesos) {
                        this.listalistaprocesos = listalistaprocesos;
                    }
                    /**
                     * Retorna la lista listalistaprocesos
                     * 
                     * @return listalistaprocesos
                     */
                    public RegistroDataModelImpl getListalistaprocesosE() {
                        return listalistaprocesosE;
                    }
                    /**
                     * Asigna la lista listalistaprocesos
                     * 
                     * @param listalistaprocesos
                     * Variable a asignar en  listalistaprocesos
                     */
                    public void setListalistaprocesosE(RegistroDataModelImpl listalistaprocesosE) {
                        this.listalistaprocesosE = listalistaprocesosE;
                    }
                    /**
                     * Retorna la variable auxiliar
                     * 
                     * @return auxiliar
                     */
                    public String getAuxiliar() {
                        return auxiliar;
                    }
                    /**
                     * Asigna la variable auxiliar
                     * 
                     * @param auxiliar
                     * Variable a asignar en auxiliar
                     */
                    public void setAuxiliar(String auxiliar) {
                        this.auxiliar= auxiliar;
                    }
                    //</SET_GET_LISTAS_COMBO_GRANDE>
                }
