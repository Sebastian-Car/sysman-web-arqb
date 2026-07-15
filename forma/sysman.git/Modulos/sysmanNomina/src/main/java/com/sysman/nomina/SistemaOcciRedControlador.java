/*-
 * SistemaOcciRedControlador.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.nomina.enums.SistemaOcciRedControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite gestionar el sistema occired.
 *
 * @version 1.0, 11/04/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class SistemaOcciRedControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean version;
    private String anio;
    private String mes;
    private String periodo;
    private String proceso;
    private String banco;
    private Date fechaReporte;
    private String consecutivo;
    private String nombreBanco;
    private String comprobante;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos Plantilla y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoPlantilla;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    private List<Registro> listaProceso;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBanco;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbNominaOchoRemote ejbNominaOcho;
    
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB    
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de SistemaOcciRedControlador
     */
    public SistemaOcciRedControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SISTEMA_OCCIRED_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            contArchivoPlantilla = new ContenedorArchivo();
            proceso = SessionUtil.getSessionVar("procesoNomina").toString();
            anio = SessionUtil.getSessionVar("anioNomina").toString();
            mes = SessionUtil.getSessionVar("mesNomina").toString();
            periodo = SessionUtil.getSessionVar("periodoNomina").toString();

            comprobante = "1";
            fechaReporte = new Date();
            consecutivo = "000000";
            // <INI_ADICIONAL>
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
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaProceso();
        cargarListaBanco();
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
        /*
         * FR1756-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore Me.NombreArchivo =
         * "C:\SYSMAN\SISTEMAOCCIRED.xls" Me.NombreLibro = "DATOS"
         * Me.Consecutivo = "000000" 'Me.Ano1 & Me.Mes1 & Me.Periodo1
         * Me.Consecutivo.Requery If parametro(31) = "800.090.735-1"
         * Then 'CONTRALORIA DEL VALLE Me.NombreArchivo.visible =
         * False Me.NombreLibro.visible = False
         * Me.EnviarExcelEMB.visible = False Me.TXT1.visible = False
         * Me.TXT2.visible = False Me.NombreArchivo.visible = False
         * Me.NombreLibro.visible = False End If End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAno1
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SistemaOcciRedControladorUrlEnum.URL0002
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMes1
     *
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMes = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SistemaOcciRedControladorUrlEnum.URL0003
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo1
     *
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaPeriodo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SistemaOcciRedControladorUrlEnum.URL0004
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaProceso
     *
     */
    public void cargarListaProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SistemaOcciRedControladorUrlEnum.URL0001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SistemaOcciRedControladorUrlEnum.URL0005
                                                        .getValue());
        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "BANCO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     *
     */
    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>

        try {
            archivoDescarga = null;
            String datos = null;
            String modulo = SessionUtil.getModulo();
            int informe = 0;

            String plantillaColumnasAdicionales = null;
            try
            {
	            plantillaColumnasAdicionales = ejbSysmanUtil.consultarParametro(compania,
	                    "PLANTILLA OCCIRED CON COLUMNAS ADICIONALES",
	                    modulo, new Date(), true);
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }                        
            
            if ("NO".equals(SysmanFunciones.nvlStr(plantillaColumnasAdicionales, "NO"))) {
            	informe = 3;
            }            
            
            datos = ejbNominaOcho.cargarExcelOccred(compania,
                            Integer.parseInt(proceso),
                            Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            SysmanFunciones.nvlStr(banco, " "), consecutivo,
                            fechaReporte,
                            version, informe, comprobante);
            generarInforme(datos);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    
    
    public void generarInformeEMB(String datos) {
        FileInputStream file = null;
        Workbook workbook = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (validarArchivo()) {
                String rutaArchivo = contArchivoPlantilla.getArchivo()
                                .getPath();
                file = new FileInputStream(new File(rutaArchivo));
                String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());
                
                if (workbook == null)
                {
                    if (".xls".equals(extension))
                    {
                        workbook = new HSSFWorkbook(file);
                    }
                    else
                    {
                        workbook = new XSSFWorkbook(file);
                    }
                }
                
                Sheet sheet = workbook.getSheet("EMBARGOS");

                String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
                String[] colum;
                String[] titulo = registro[0].split(SysmanConstantes.SEPARADOR_COL);
                
                //sheet.shiftRows(4, lastRow, rowNumber, true, true);
                

                double valorTotal = 0;
                int totregistros = 0;

                if (registro.length == 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2514"));
                    workbook.close();
                    return;
                }
                Row fila;
                Cell celda;

                CellStyle style1 = workbook.createCellStyle();
                style1.setBorderBottom((short) 1);
                style1.setBorderLeft((short) 1);
                style1.setBorderTop((short) 1);
                style1.setBorderRight((short) 1);
                
        		CellStyle stylenumber = workbook.createCellStyle();
        		DataFormat format = workbook.createDataFormat();
        		stylenumber.setBorderBottom((short) 1);
        		stylenumber.setBorderLeft((short) 1);
        		stylenumber.setBorderTop((short) 1);
        		stylenumber.setBorderRight((short) 1);
        		stylenumber.setDataFormat(format.getFormat("###,###,##0.00"));
        		stylenumber.setLocked(false);
     

                // anexar al encabezado de la plantilla el mes y a�o 
                fila = sheet.getRow(0);
                celda = fila.getCell(0);
                celda.setCellValue(celda.getStringCellValue()+titulo[2]+' '+titulo[1]);
                //agregar el numero de filas respecto al numero de datos 
                sheet.shiftRows(4, registro.length, (registro.length-1), true, true);
                
                for (int i = 0; i < registro.length; i++) {
                    fila = sheet.createRow(i + 4);
                    colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
                    //contador incremental nro de registros  
                    celda = fila.createCell(0);
                    celda.setCellValue(i+1);
                    celda.setCellStyle(style1);  
               
                    for (int j = 1; j < colum.length; j++) {
                        
                        if((j+3) <  colum.length ) {
                            //validacion para tomar los valores numericos y totalizar 
                            if((j+3) == 6) {
                            	celda = fila.createCell(j);
                                celda.setCellValue(Double.parseDouble(colum[j+3]));
                         
                            	celda.setCellStyle(stylenumber);
                            	 valorTotal = valorTotal
                                         + Double.parseDouble(colum[j+3]);  
                            }  else {
                            	celda = fila.createCell(j);
                                celda.setCellValue(colum[j+3]);
                            	celda.setCellStyle(style1);
                            }
                           
                              
                        }  
                                                                 
                    }
                    totregistros = totregistros + 1;
                }
                
                
                //agregar el valor total 
                fila = sheet.getRow(totregistros + 4);
                celda = fila.createCell(3);
                celda.setCellValue(valorTotal);
                celda.setCellStyle(stylenumber);
                
                

                workbook.write(out);
                out.close();

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(new ByteArrayInputStream(
                                                out.toByteArray()),
                                                "SISTEMAOCCIREDV25"
                                                    + (version ? "V25" : "")
                                                    + ".xls");
                workbook.close();
            }
        }
        catch (IOException | NumberFormatException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }                
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    

    public void generarInforme(String datos) {
        FileInputStream file = null;
        Workbook workbook = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (validarArchivo()) {
                String rutaArchivo = contArchivoPlantilla.getArchivo()
                                .getPath();
                file = new FileInputStream(new File(rutaArchivo));
                String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());
                
               String cuentaPrincipal9 = ejbSysmanUtil.consultarParametro(compania,
	                    "CUENTA PRINCIPAL OCCIRED 9 DIGITOS",
	                    "6", new Date(), true); //JM 18/102024 7800559 
                
                
                if (workbook == null)
                {
                    if (".xls".equals(extension))
                    {
                        workbook = new HSSFWorkbook(file);
                    }
                    else
                    {
                        workbook = new XSSFWorkbook(file);
                    }
                } 
               
                Sheet sheet = workbook.getSheet("Pagos");

                String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
                String[] colum;

                double valorTotal = 0;
                int totregistros = 0;

                if (registro.length == 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2514"));
                    workbook.close();
                    return;
                }
                Row fila;
                Cell celda;

                CellStyle style1 = workbook.createCellStyle();
                style1.setBorderBottom((short) 1);
                style1.setBorderLeft((short) 1);
                style1.setBorderTop((short) 1);
                style1.setBorderRight((short) 1);

                fila = sheet.getRow(12);
                celda = fila.getCell(1);
                celda.setCellValue(SysmanFunciones.ano(fechaReporte));
                celda = fila.getCell(2);
                celda.setCellValue(SysmanFunciones.mes(fechaReporte));
                celda = fila.getCell(3);
                celda.setCellValue(SysmanFunciones.dia(fechaReporte));
                int columnaValorTotal;
                if(cuentaPrincipal9.length() == 9) { //JM_INI 18/102024 7800559 
                	
                	String cuenta = registro[0].toString();
                	cuenta = cuenta.substring(0, 3)+"-"+cuenta.substring(3, 8)+"-"+cuenta.substring(8, 9);
                	String cuentaprincipal = cuentaPrincipal9;
                	cuentaprincipal = cuentaprincipal.substring(0, 3)+"-"+cuentaprincipal.substring(3, 8)+"-"+cuentaprincipal.substring(8, 9);
                	fila = sheet.getRow(8);
                    celda = fila.getCell(1);
                    celda.setCellValue(cuentaprincipal);
                	
	                for (int i = 1; i < registro.length; i++) {
	                    fila = sheet.createRow(i + 20);
	                    colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
	                    columnaValorTotal = colum.length == 16 ? 11 : 8;
	                    for (int j = 0; j < colum.length; j++) {
	                    		
		                        celda = fila.createCell(j);
		                        if(j == 0) {
		                        	celda.setCellValue(cuenta);
		                        }else {
		                        	celda.setCellValue(colum[j]);
		                        }
		                        celda.setCellStyle(style1);                         
		                        if (j == columnaValorTotal) {
		                            valorTotal = valorTotal
		                                + Double.parseDouble(colum[j]);                            
		              
	                    	} 
	                    }
	                    totregistros = totregistros + 1;
	                }
	              //JM_FIN 18/102024 7800559 
                }else { 
                	 for (int i = 0; i < registro.length; i++) {
 	                    fila = sheet.createRow(i + 20);
 	                    colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
 	                    columnaValorTotal = colum.length == 16 ? 11 : 8;
 	                    for (int j = 0; j < colum.length; j++) {
 	                        celda = fila.createCell(j);
 	                        celda.setCellValue(colum[j]);
 	                        celda.setCellStyle(style1);                         
 	                        if (j == columnaValorTotal) {
 	                            valorTotal = valorTotal
 	                                + Double.parseDouble(colum[j]);                            
 	                        }                        
 	                    }
 	                    totregistros = totregistros + 1;
 	                }
                	
                }
                
                fila = sheet.getRow(14);
                celda = fila.getCell(4);
                celda.setCellValue(totregistros);                

                fila = sheet.getRow(15);
                celda = fila.getCell(4);
                celda.setCellValue(valorTotal);

                workbook.write(out);
                out.close();

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(new ByteArrayInputStream(
                                                out.toByteArray()),
                                                "SISTEMAOCCIREDV25"
                                                    + (version ? "V25" : "")
                                                    + ".xls");
                workbook.close();
            }
        }
        catch (IOException | NumberFormatException | JRException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }                
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public boolean validarArchivo() {

        String archivo = String.valueOf(contArchivoPlantilla.getArchivo());
        if (contArchivoPlantilla.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
            return false;
        }
        else {
            String extension = archivo
                            .substring(archivo.indexOf('.'), archivo.length())
                            .toLowerCase();
            if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
                return true;
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
                return false;
            }
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcelEMB en la vista
     *
     */
    public void oprimirEnviarExcelEMB() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String datos = ejbNominaOcho.cargarExcelOccred(compania,
                            Integer.parseInt(proceso),
                            Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            SysmanFunciones.nvlStr(banco, " "), consecutivo,
                            fechaReporte,
                            version, 1, comprobante);
            generarInformeEMB(datos);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PLANOOCCIRED en la vista
     *
     */
    public void oprimirPLANOOCCIRED() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String datos = ejbNominaOcho.cargarExcelOccred(compania,
                            Integer.parseInt(proceso),
                            Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            SysmanFunciones.nvlStr(banco, " "), consecutivo,
                            fechaReporte,
                            version, 2, comprobante);
            ByteArrayInputStream streamTexto;

            streamTexto = JsfUtil.serializarPlano(datos);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "SISTEMAOCCIRED.txt");
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarMes() {
        periodo = null;
        cargarListaPeriodo();
    }

    public void cambiarAno() {
        mes = null;
        periodo = null;
        cargarListaMes();
        cargarListaPeriodo();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ")
                        .toString();
        nombreBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBREBANCO"), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable banco
     * 
     * @return banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Asigna la variable banco
     * 
     * @param banco
     * Variable a asignar en banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna la variable fechaReporte
     * 
     * @return fechaReporte
     */
    public Date getFechaReporte() {
        return fechaReporte;
    }

    /**
     * Asigna la variable fechaReporte
     * 
     * @param fechaReporte
     * Variable a asignar en fechaReporte
     */
    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    /**
     * Retorna la variable consecutivo
     * 
     * @return consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Asigna la variable consecutivo
     * 
     * @param consecutivo
     * Variable a asignar en consecutivo
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Retorna la variable nombreBanco
     * 
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     * 
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    /**
     * Retorna la variable comprobante
     * 
     * @return comprobante
     */
    public String getComprobante() {
        return comprobante;
    }

    /**
     * Asigna la variable comprobante
     * 
     * @param comprobante
     * Variable a asignar en comprobante
     */
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno1
     * 
     * @return listaAno1
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno1
     * 
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaMes1
     * 
     * @return listaMes1
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes1
     * 
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo1
     * 
     * @return listaPeriodo1
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo1
     * 
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBanco
     * 
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    /**
     * Asigna la lista listaBanco
     * 
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    /**
     * Retorna el objeto contArchivoPlantilla
     * 
     * @return contArchivoPlantilla
     */
    public ContenedorArchivo getContArchivoPlantilla() {
        return contArchivoPlantilla;
    }

    /**
     * Asigna el objeto contArchivoPlantilla
     * 
     * @param contArchivoPlantilla
     * Variable a asignar en contArchivoPlantilla
     */
    public void setContArchivoPlantilla(
        ContenedorArchivo contArchivoPlantilla) {
        this.contArchivoPlantilla = contArchivoPlantilla;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
