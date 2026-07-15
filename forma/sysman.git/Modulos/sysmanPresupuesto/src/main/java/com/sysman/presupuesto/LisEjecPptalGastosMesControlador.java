/*-
 * LisEjecPptalGastosMesControlador.java
 *
 * 1.0
 *
 * 05/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisEjecPptalGastosMesControladorEnum;
import com.sysman.presupuesto.enums.LisEjecPptalGastosMesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar un informe de la ejecucion presupuestal de gasto mensual.
 *
 * @version 1.0, 05/12/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class LisEjecPptalGastosMesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * almacena el tipo de cuenta seleccionada para generar el informe
     */
    private String tipoCuenta;
    /**
     * almacena el codigo de cuenta inicial seleccionada para generar el informe
     */
    
    private boolean excelPlano;
    private String cuentaInicial;
    /**
     * almacena el codigo de cuenta final seleccionada para generar el informe
     */
    private String cuentaFinal;
    /**
     * almacena el mes seleccionado para generar el informe
     */
    private String mes;
    /**
     * almacena el ano seleccionado para generar el informe
     */
    private String anio;
    /**
     * almacena el nivel seleccionado para generar el informe
     */
    private String nivel;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de LisEjecPptalGastosMesControlador
     */
    public LisEjecPptalGastosMesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_EJECPPTAL_GASTOSMES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = String.valueOf(SysmanFunciones.mes(new Date()));
            cuentaInicial = "0";
            cuentaFinal = "9999999999999999";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        nivel = "6";
        tipoCuenta = "1";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisEjecPptalGastosMesControladorUrlEnum.URL11959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisEjecPptalGastosMesControladorUrlEnum.URL12000
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LisEjecPptalGastosMesControladorEnum.PARAM0.getValue(), cuentaInicial);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    /**
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisEjecPptalGastosMesControladorUrlEnum.URL12003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton excel en la vista
     *
     *
     */
    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato) {
    	try {
    		archivoDescarga = null;
    		String reporte;
    		HashMap<String, Object> reemplazar = new HashMap<>();
    		reemplazar.put("compania", compania);
    		reemplazar.put("anio", anio);
    		reemplazar.put("mes", mes);
    		reemplazar.put("cuentaInicial", cuentaInicial);
    		reemplazar.put("cuentaFinal", cuentaFinal);
    		reemplazar.put("nivel", nivel);
    		reemplazar.put("tipoCuenta", tipoCuenta);

    		Map<String, Object> parametros = new HashMap<>();
    		parametros.put("PR_MES", ejbSysmanUtil
    				.mostrarNombreDeMes(Integer.parseInt(mes)));
    		parametros.put("PR_ANO", anio);

    		if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
    				"FORMATO CALIDAD", SessionUtil.getModulo(),
    				new Date(), true))) {
    			reporte = "001533LisEjecPptalGastosMesCOS";
    		}
    		else {
    			reporte = "001532LisEjecPptalGastosMes";
    		}

    		if(excelPlano) 
    		{
    			try {
    				reporte = "800635LisEjecPptalGastoMes";


    				String datosExcel = Reporteador.resuelveConsulta(reporte, 
    						Integer.parseInt(SessionUtil.getModulo()),
    						reemplazar);


    	            // Exportar los datos al archivo temporal y configurar para descarga inicial
    	            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, 
    	                    ConectorPool.ESQUEMA_SYSMAN, 
    	                    FORMATOS.EXCEL, reporte);

    	            // Leer el contenido del archivo descargable
    	            InputStream initialInputStream = archivoDescarga.getStream();
    	            Workbook workbook = new XSSFWorkbook (initialInputStream);
    	            Sheet sheet = workbook.getSheetAt(0);

//    	             Modificar el archivo en memoria
    	            modificarExcel(sheet);

    	            // Escribir el archivo modificado a un ByteArrayOutputStream
    	            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
                    workbook.write(fileOut);

                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    new ByteArrayInputStream(fileOut.toByteArray()),
                                    reporte + ".xlsx");
                    fileOut.close();
                    
    			} catch (SQLException | DRException e) {
    				// TODO Auto-generated catch block
    				((Throwable) e).printStackTrace();
    			}

    		}else {
    			Reporteador.resuelveConsulta("001532LisEjecPptalGastosMes",
    					Integer.parseInt(SessionUtil.getModulo()),
    					reemplazar, parametros);

    			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
    					ConectorPool.ESQUEMA_SYSMAN, formato);
    		}


    	}
    	catch (JRException | IOException | SysmanException
    			| SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }
    

    
    private void modificarExcel(Sheet sheet) {
        // Desplazar todas las filas una posición hacia abajo
        sheet.shiftRows(0, sheet.getLastRowNum(), 1);

        // Crear una nueva fila en la parte superior
        Row newRow = sheet.createRow(0);
        Row secondRow = sheet.getRow(1); // Obtener la segunda fila

        // Crear una celda para 'MODIFICACIONES PRESUPUESTALES DEL MES' y combinar las celdas D, E, F y G
        Cell combinedCell = newRow.createCell(3);
        combinedCell.setCellValue("MODIFICACIONES PRESUPUESTALES DEL MES");
        CellStyle combinedStyle = secondRow.getCell(3).getCellStyle();
        combinedCell.setCellStyle(combinedStyle);

        // Combinar las celdas D, E, F y G
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 6));

        // Combinar celdas A1 con A2 hasta Q1 con Q2, excluyendo D, E, F y G (ya combinadas)
        for (int col = 0; col <= 16; col++) {
            if (col == 3 || col == 4 || col == 5 || col == 6) {
                continue; // Saltar columnas D, E, F y G (ya combinadas)
            }

            // Copiar el contenido y el estilo de la segunda fila a la primera fila
            Cell newCell = newRow.createCell(col);
            Cell secondRowCell = secondRow.getCell(col);
            if (secondRowCell != null) {
                // Copiar el contenido
                newCell.setCellValue(secondRowCell.getStringCellValue());
                // Copiar el estilo
                newCell.setCellStyle(secondRowCell.getCellStyle());
            }

            // Combinar las celdas
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col));
        }
        
        // Congelar las dos primeras filas para que queden fijas como encabezado
        sheet.createFreezePane(0, 2);
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        cuentaInicial = cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoCuenta
     *
     * @return tipoCuenta
     */
    public String getTipoCuenta() {
        return tipoCuenta;
    }

    
    
    
    public boolean isExcelPlano() {
		return excelPlano;
	}

	public void setExcelPlano(boolean excelPlano) {
		this.excelPlano = excelPlano;
	}

	/**
     * Asigna la variable tipoCuenta
     *
     * @param tipoCuenta
     * Variable a asignar en tipoCuenta
     */
    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    /**
     * Retorna la variable cuentaInicial
     *
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     *
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     *
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     *
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio() {
        return anio;
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
     * Retorna la variable nivel
     *
     * @return nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Asigna la variable nivel
     *
     * @param nivel
     * Variable a asignar en nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
