/*-
 * Frmproyeccionesdenomina.java
 *
 * 1.0
 * 
 * 19/05/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.DevengosDescuentosControladorEnum;
import com.sysman.nomina.enums.PeriodoTrabajoControladorUrlEnum;
import com.sysman.nomina.enums.PersonalsControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/05/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  Frmproyeccionesdenomina extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    private final String modulo;
    private String proceso;
    private String anio;
    private String nombreUbicacion;
    private String CodigoUbicacion;
    private Double procentaje;
    private Double procentajedos;
    private Double procentajetres;
    private RegistroDataModelImpl listaCodigoEstablecimiento;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
    private StreamedContent archivoDescarga;
//<DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaAnobase;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de Frmproyeccionesdenomina
     */
    public Frmproyeccionesdenomina() {
  super();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
        try {
        	numFormulario=2277;
            validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
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
//<CARGAR_LISTA>
		abrirFormulario();
		cargarListaCodigoEstablecimiento();
		cargarListaAnobase();
		


    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
  @Override
	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
	  	proceso = "1";
	  	anio = Integer.toString(SysmanFunciones.ano(new Date()));
	  	procentaje = procentajedos = procentajetres = 0.00;
        
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnobase
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaAnobase(){

    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

    try {
    	listaAnobase = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        PeriodoTrabajoControladorUrlEnum.URL4735
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }
}

public void cargarListaCodigoEstablecimiento(){

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL35438
                                                        .getValue());

        listaCodigoEstablecimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
}
//</METODOS_CARGAR_LISTA>
public void seleccionarFilaCodigoEstablecimiento(SelectEvent event)
{
	Registro registroAux1 = (Registro) event.getObject();
	CodigoUbicacion = registroAux1.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()).toString();
	nombreUbicacion = registroAux1.getCampos()
			.get(GeneralParameterEnum.NOMBRE.getName()).toString();
}
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Actualizar
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirActualizar() {
         //<CODIGO_DESARROLLADO>
	setArchivoDescarga(null);

	generarInforme(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }

public void generarInforme(ReportesBean.FORMATOS formato) {
	archivoDescarga = null;
	try {
		Map<String, Object> parametros = new HashMap<>();
		
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("compania", compania);
		reemplazar.put("ubicacion", CodigoUbicacion);
		reemplazar.put("porcentaje", procentaje);
		reemplazar.put("procentajedos", procentajedos);
		reemplazar.put("procentajetres",procentajetres);
		reemplazar.put("ano", anio);
		

			String reporte = "002277ProyeccionesDeNominaRN_IN_PR";
			
			Reporteador.resuelveConsulta(reporte,Integer.parseInt(modulo), reemplazar,
					parametros);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		

	}

	catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeInformativo(
				idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
						+ e.getMessage());
	}
}
    /**
     * Retorna la lista listaAnobase
     * 
     * @return listaAnobase
     */
public List<Registro> getListaAnobase() {
        return listaAnobase;
    }
    /**
     * Asigna la lista listaAnobase
     * 
     * @param listaAnobase
     * Variable a asignar en  listaAnobase
     */
public void setListaAnobase(List<Registro> listaAnobase) {
        this.listaAnobase = listaAnobase;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public Double getProcentaje() {
		return procentaje;
	}
	public void setProcentaje(Double procentaje) {
		this.procentaje = procentaje;
	}
	public Double getProcentajedos() {
		return procentajedos;
	}
	public void setProcentajedos(Double procentajedos) {
		this.procentajedos = procentajedos;
	}
	public Double getProcentajetres() {
		return procentajetres;
	}
	public void setProcentajetres(Double procentajetres) {
		this.procentajetres = procentajetres;
	}
	public RegistroDataModelImpl getListaCodigoEstablecimiento() {
		return listaCodigoEstablecimiento;
	}
	public void setListaCodigoEstablecimiento(RegistroDataModelImpl listaCodigoEstablecimiento) {
		this.listaCodigoEstablecimiento = listaCodigoEstablecimiento;
	}
	public String getNombreUbicacion() {
		return nombreUbicacion;
	}
	public void setNombreUbicacion(String nombreUbicacion) {
		this.nombreUbicacion = nombreUbicacion;
	}
	public String getCodigoUbicacion() {
		return CodigoUbicacion;
	}
	public void setCodigoUbicacion(String codigoUbicacion) {
		this.CodigoUbicacion = codigoUbicacion;
	}

}
