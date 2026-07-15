/*-
 * FrCrearNovedadesPensioandosControlador.java
 *
 * 1.0
 * 
 * 01/12/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
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
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.*;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.enums.FrCrearNovedadesPensionadosUrlEnum;
import com.sysman.nomina.enums.volantesDePagoControladorEnum;
import com.sysman.nomina.enums.volantesDePagoControladorUrlEnum;
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
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/12/2022
 * @author carenas
 */
@ManagedBean
@ViewScoped
public class  FrCrearNovedadesPensioandosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private String idDeEmpleado;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private Date fechaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private Date fechaFinal;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private String nombreCompleto;

private String numeroDocumento;


private RegistroDataModelImpl listaidDeEmpleado;

private String tipoNovedad;

@EJB
private EjbNominaSeisRemote ejbNominaSeis;


    /**
     * Crea una nueva instancia de FrCrearNovedadesPensioandosControlador
     */
    public FrCrearNovedadesPensioandosControlador() {
    		super();
    		fechaInicial = new Date();
    		fechaFinal = new Date();
            compania = SessionUtil.getCompania();
        try {
        	numFormulario=GeneralCodigoFormaEnum.FR_CREAR_NOVEDADES_PENSIONADOS_CONTROLADOR
					.getCodigo();
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
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		 cargarListaidDeEmpleado();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
    }
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
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaidDeEmpleado
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaidDeEmpleado(){
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		FrCrearNovedadesPensionadosUrlEnum.URL001
                                                    .getValue());
    listaidDeEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true, volantesDePagoControladorEnum.ID_DE_EMPLEADO
                                    .getValue());
}

public void oprimircrearNovedad() {


    try {
        String datos = null;
        switch (tipoNovedad) {
		case "1": 
			tipoNovedad = "N01";
			break;
		case "2": 
			tipoNovedad = "N02";
			break;
		case "3": 
			tipoNovedad = "N03";
			break;
		case "4": 
			tipoNovedad = "N04";
			break;
		case "5": 
			tipoNovedad = "N05";
			break;
		case "6": 
			tipoNovedad = "N06";
			break;
		case "7": 
			tipoNovedad = "N07";
			break;
		case "8": 
			tipoNovedad = "N08";
			break;
		case "9": 
			tipoNovedad = "N09";
			break;

		default:
			break;
		}
    
		datos = ejbNominaSeis.crearNovedadesPensionado(  
				 compania,
				 fechaInicial,
				 fechaFinal,
		         numeroDocumento,
		         idDeEmpleado,
		         tipoNovedad);
		if(datos.equals("UPDATE")) {
        JsfUtil.agregarMensajeInformativo("REGISTRO MODIFICADO CON EXITO");
		}else {
			JsfUtil.agregarMensajeInformativo("REGISTRO CREADO CON EXITO");
		}
	} catch (SystemException e) {
		e.printStackTrace();
	}
    } 
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidDeEmpleado
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaidDeEmpleado(SelectEvent event) {
    Registro registroAux = (Registro) event.getObject();
    idDeEmpleado = SysmanFunciones
                    .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                    .toString();
    numeroDocumento = SysmanFunciones
            .nvl(registroAux.getCampos().get("NUMERO_DCTO"), "")
            .toString();
    nombreCompleto = SysmanFunciones
                    .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                    .toString();

}



    /**
	 * @return the idDeEmpleado
	 */
	public String getIdDeEmpleado() {
		return idDeEmpleado;
	}
	/**
	 * @param idDeEmpleado the idDeEmpleado to set
	 */
	public void setIdDeEmpleado(String idDeEmpleado) {
		this.idDeEmpleado = idDeEmpleado;
	}
	
	
	/**
	 * @return the numeroDocumento
	 */
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	/**
	 * @param numeroDocumento the numeroDocumento to set
	 */
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	
    /**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
     * Retorna la variable nombreCompleto
     * 
     * @return  nombreCompleto
     */
public String getNombreCompleto() {
        return nombreCompleto;
    }
    /**
     * Asigna la variable  nombreCompleto
     * 
     * @param  nombreCompleto
     * Variable a asignar en  nombreCompleto
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaidDeEmpleado
     * 
     * @return listaidDeEmpleado
     */
	/**
	 * @return the listaidDeEmpleado
	 */
	public RegistroDataModelImpl getListaidDeEmpleado() {
		return listaidDeEmpleado;
	}
	/**
	 * @param listaidDeEmpleado the listaidDeEmpleado to set
	 */
	public void setListaidDeEmpleado(RegistroDataModelImpl listaidDeEmpleado) {
		this.listaidDeEmpleado = listaidDeEmpleado;
	}
	/**
	 * @return the tipoNovedad
	 */
	public String getTipoNovedad() {
		return tipoNovedad;
	}
	/**
	 * @param tipoNovedad the tipoNovedad to set
	 */
	public void setTipoNovedad(String tipoNovedad) {
		this.tipoNovedad = tipoNovedad;
	}
	
	
	

//</SET_GET_LISTAS_COMBO_GRANDE>
}
