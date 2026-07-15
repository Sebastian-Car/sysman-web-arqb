/*-
 * FrmdiscofavidiControlador.java
 *
 * 1.0
 * 
 * 31/08/2020
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
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.nomina.ejb.EjbNominaCincoRemote;
import com.sysman.nomina.enums.ResumenPorCentroCostoControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 31/08/2020
 * @author lmosquera
 */
@ManagedBean
@ViewScoped
public class  FrmdiscofavidiControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaAno;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaMes;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaPeriodo;
	private String modulo;
	private String proceso;
	private String mes;
	private String periodo;
	private String ano;
	private String nombrePeriodoNomina;
	private String opcion;
	private StreamedContent archivoDescarga;
	
	@EJB
	private EjbNominaCincoRemote ejbNominaCincoRemote; 
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmdiscofavidiControlador
     */
    public FrmdiscofavidiControlador() {
    	super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		//anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		ano = "2019";
		mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		nombrePeriodoNomina = SysmanFunciones.nvl(SessionUtil.getSessionVar("nombrePeriodoNomina"), "").toString();
		opcion =  "0";
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
		 cargarListaAno1();
 cargarListaMes1();
 cargarListaPeriodo1();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
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
            /*
FR2182-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
     formularioAbrir 1
End Sub
*/
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno1
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaAno1(){
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	try {
		listaAno = RegistroConverter
				.toListRegistro(requestManager.getList(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ResumenPorCentroCostoControladorUrlEnum.URL4751.getValue())
								.getUrl(),
						param));
	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
    /**
     * 
     * Carga la lista listaMes1
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaMes1(){
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.ANO.getName(), ano);
	try {
		listaMes = RegistroConverter
				.toListRegistro(requestManager.getList(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ResumenPorCentroCostoControladorUrlEnum.URL4752.getValue())
								.getUrl(),
						param));
	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
    /**
     * 
     * Carga la lista listaPeriodo1
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaPeriodo1(){
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.ANO.getName(), ano);
	param.put(GeneralParameterEnum.MES.getName(), mes);
	try {
		listaPeriodo = RegistroConverter
				.toListRegistro(requestManager.getList(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ResumenPorCentroCostoControladorUrlEnum.URL4753.getValue())
								.getUrl(),
						param));
	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarDisco
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirGenerarDisco() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarPlano();
}
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprime
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */

public void generarPlano() {
	try {		
		String datos = ejbNominaCincoRemote.generarDiscoFoncepFavidi(compania, Integer.parseInt(proceso), SessionUtil.getUser().getCodigo(), 
				Integer.parseInt(periodo), opcion, Integer.parseInt(mes), Integer.parseInt(ano));

		ByteArrayInputStream streamTexto;

		streamTexto = JsfUtil.serializarPlano(datos);
		archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "PlanoFoncepFavidi.txt");
	} catch (NumberFormatException | SystemException | JRException | IOException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}

public void oprimirImprime() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando67
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirComando67() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
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
     * Variable a asignar en  listaAno1
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
     * Variable a asignar en  listaMes1
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
     * Variable a asignar en  listaPeriodo1
     */
public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public String getAno() {
		return ano;
	}
	public void setAno(String ano) {
		this.ano = ano;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getOpcion() {
		return opcion;
	}
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
	
	

}
