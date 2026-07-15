/*-
 * FrmLisdocAlmacenControlador.java
 *
 * 1.0
 * 
 * 04/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.FrmLisdocAlmacenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
import net.sf.dynamicreports.report.exception.DRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 04/07/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmLisdocAlmacenControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Almacena el tipo inicial
     */
    private String tipoInicial;
    /**
     * Almacena el tipo final
     */
    private String tipoFinal;
    /**
     * Almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * Almacena la fecha final
     */
    private Date fechaFinal;

    /**
     * Variable que almacena el nombre del tipo de comprobante inicial
     */
    private String nombreTipoIni;

    /**
     * Variable que almacena el nombre del tipo de comprobante final
     */
    private String nombreTipoFin;
  

    private boolean ckEspecial;
    private boolean ckExcelplano;
    private String reporte;
    // private String subReporte;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaTipoInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaTipoFinal;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmLisdocAlmacenControlador
     */
    public FrmLisdocAlmacenControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_LIS_DOC_ALMACEN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>

        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoInicial();
        cargarListaTipoFinal();
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

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoInicial
     *
     */
    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmLisdocAlmacenControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTipoFinal
     *
     */
    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmLisdocAlmacenControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
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

        archivoDescarga = null;
    
        generarInforme(FORMATOS.PDF);

    }

    public void oprimirExcel() {

        archivoDescarga = null;
        
        if ( isCkExcelplano() ) {
        	if (isCkEspecial()) {
                reporte = "800580CDocAlmacenEspPlano";
            }
            else {
                reporte = "800579CDocAlmacenPlano";
            }
        	
        	generarInformexcel(FORMATOS.EXCEL);
        }
        else {
        	generarInforme(FORMATOS.EXCEL);
        }
    }

    public void consultarReporte() {

        if (isCkEspecial()) {
            reporte = "001831CDocAlmacenEspecial";
            // subReporte = "001832CDocAlmacenEspecialTotal";
        }
        else {
            reporte = "001830CDocAlmacen";
        }

    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            consultarReporte();
            String consulta = reporte;
            
            if (isCkEspecial()) {
            	
				try {
					consulta = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					        "FORMATO ESPECIAL DIARIO A CONTABILIDAD ALMACEN", SessionUtil.getModulo(), new Date(), false),
					        "001831CDocAlmacenEspecial");
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
				}
            }
           

            HashMap<String, Object> reemplazarCons = new HashMap<>();
            reemplazarCons.put("compania", compania);
            reemplazarCons.put("tipoInicial", tipoInicial);
            reemplazarCons.put("tipoFinal", tipoFinal);

            reemplazarCons.put("fechaInicial",
                            SysmanFunciones.formatearFechaCadena(fechaInicial,
                                            "DD/MM/YYYY"));
            reemplazarCons.put("fechaFinal",
                            SysmanFunciones.formatearFechaCadena(fechaFinal,
                                            "DD/MM/YYYY"));

            reemplazarCons.put("consulta1831",
                            Reporteador.resuelveConsulta(
                                            consulta,
                                            Integer.parseInt(SessionUtil
                                                            .getModulo()),
                                            reemplazarCons));

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FORMS_LISDOCALMACEN_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));

            parametros.put("PR_FORMS_LISDOCALMACEN_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            
        	Reporteador.resuelveConsulta(consulta, Integer.parseInt(modulo),
                    reemplazarCons, parametros);

        	archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                    ConectorPool.ESQUEMA_SYSMAN, formato);
            
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    private void generarInformexcel(FORMATOS formato) {
		String sql = "";
		try {
	        HashMap<String, Object> reemplazarCons = new HashMap<>();
	        reemplazarCons.put("compania", compania);
	        reemplazarCons.put("tipoInicial", tipoInicial);
	        reemplazarCons.put("tipoFinal", tipoFinal);
	
	        reemplazarCons.put("fechaInicial",
	                        SysmanFunciones.formatearFechaCadena(fechaInicial,
	                                        "DD/MM/YYYY"));
	        reemplazarCons.put("fechaFinal",
	                        SysmanFunciones.formatearFechaCadena(fechaFinal,
	                                        "DD/MM/YYYY"));
	
	        sql = Reporteador.resuelveConsulta(reporte,
	                Integer.parseInt(SessionUtil.getModulo()),
	                reemplazarCons);
	
	        archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
	                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
	                reporte);
	
		} catch (JRException | IOException | SysmanException | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoInicial
     *
     *
     */
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        nombreTipoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoFinal
     *
     * 
     */
    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        nombreTipoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoInicial
     * 
     * @return tipoInicial
     */
    public String getTipoInicial() {
        return tipoInicial;
    }

    /**
     * Asigna la variable tipoInicial
     * 
     * @param tipoInicial
     * Variable a asignar en tipoInicial
     */
    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    /**
     * Retorna la variable tipoFinal
     * 
     * @return tipoFinal
     */
    public String getTipoFinal() {
        return tipoFinal;
    }

    /**
     * Asigna la variable tipoFinal
     * 
     * @param tipoFinal
     * Variable a asignar en tipoFinal
     */
    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoInicial
     * 
     * @return listaTipoInicial
     */
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    /**
     * Asigna la lista listaTipoInicial
     * 
     * @param listaTipoInicial
     * Variable a asignar en listaTipoInicial
     */
    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    /**
     * Retorna la lista listaTipoFinal
     * 
     * @return listaTipoFinal
     */
    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    /**
     * Asigna la lista listaTipoFinal
     * 
     * @param listaTipoFinal
     * Variable a asignar en listaTipoFinal
     */
    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    /**
     * @return the nombreTipoIni
     */
    public String getNombreTipoIni() {
        return nombreTipoIni;
    }

    /**
     * @param nombreTipoIni
     * the nombreTipoIni to set
     */
    public void setNombreTipoIni(String nombreTipoIni) {
        this.nombreTipoIni = nombreTipoIni;
    }

    /**
     * @return the nombreTipoFin
     */
    public String getNombreTipoFin() {
        return nombreTipoFin;
    }

    /**
     * @param nombreTipoFin
     * the nombreTipoFin to set
     */
    public void setNombreTipoFin(String nombreTipoFin) {
        this.nombreTipoFin = nombreTipoFin;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the ckEspecial
     */
    public boolean isCkEspecial() {
        return ckEspecial;
    }

    /**
     * @param ckEspecial
     * the ckEspecial to set
     */
    public void setCkEspecial(boolean ckEspecial) {
        this.ckEspecial = ckEspecial;
    }
    
    /**
     * @return the ckExcelplano
     */
    public boolean isCkExcelplano() {
        return ckExcelplano;
    }

    /**
     * @param ckEspecial
     * the ckEspecial to set
     */
    public void setCkExcelplano(boolean ckExcelplano) {
        this.ckExcelplano = ckExcelplano;
    }
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
