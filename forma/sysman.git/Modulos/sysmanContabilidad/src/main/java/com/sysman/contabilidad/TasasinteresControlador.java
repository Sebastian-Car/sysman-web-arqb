package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.TasasinteresesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ybecerra
 * @version 1, 16/03/2016
 * 
 * @version 2, 12/04/2017
 * @author jreina se realizaron los cambios de refactoring en el
 * reasignar origen.
 * 
 * @author asana
 * @version 3, 12/06/2017 Se implementa enum en formulario y elimina
 * conectorpool de metodo inicializar() dado que no se utilizaba.
 * 
 */
@ManagedBean
@ViewScoped
public class TasasinteresControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
	private String anoOrigen;
	private String anoDestino;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	private List<Registro> listaanoOrigen;
	private List<Registro> listaanoDestino;
//</DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las cuentas contables
     */
    private RegistroDataModelImpl listaCuentaContable;
    /**
     * Lista que carga las cuentas contables en la grilla
     */
    private RegistroDataModelImpl listaCuentaContableE;

    private String anioGrilla;

    private boolean visibleDeterioro;
    
    private boolean visibleTasaTES;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * variable EJB
     */
    @EJB
    EjbContabilidadTresRemote ejbContabilidadTres;
    /**
     * Creates a new instance of TasasinteresControlador
     */
    public TasasinteresControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.TASASINTERES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Calendar calendario = new GregorianCalendar();
            anoOrigen = String.valueOf(calendario.get(Calendar.YEAR));
            anoDestino = String.valueOf(calendario.get(Calendar.YEAR) + 1); 
        }
        catch (Exception ex) {
            Logger.getLogger(TasasinteresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TASAS_INTERES;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        // <CARGAR_LISTA>
        cargarListaAnio();
        cargarListaanoOrigen();
        cargarListaanoDestino();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaContable();
        // </CARGAR_LISTA_COMBO_GRANDE>

        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TasasinteresesControladorUrlEnum.URL4001
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }
    

	
	public void cargarListaanoOrigen(){
			 Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        
			try {
				listaanoOrigen = RegistroConverter.toListRegistro(
				        requestManager.getList(UrlServiceUtil.getInstance()
				                .getUrlServiceByUrlByEnumID(
				                                TasasinteresesControladorUrlEnum.URL4001
				                                                .getValue())
				                .getUrl(), param));
			} catch (SystemException e) {
	            JsfUtil.agregarMensajeError(e.getMessage());
				e.printStackTrace();
			}
		}
		    /**
		     * 
		     * Carga la lista listaanoDestino
		     *
		     */
		public void cargarListaanoDestino(){
			Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        
			try {
				listaanoDestino = RegistroConverter.toListRegistro(
				        requestManager.getList(UrlServiceUtil.getInstance()
				                .getUrlServiceByUrlByEnumID(
				                                TasasinteresesControladorUrlEnum.URL4001
				                                                .getValue())
				                .getUrl(), param));
			} catch (SystemException e) {
	            JsfUtil.agregarMensajeError(e.getMessage());
				e.printStackTrace();
			}
		}

	/**
	 * 
	 * Carga la lista listaCuentaContable
	 *
	 */
    public void cargarListaCuentaContable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TasasinteresesControladorUrlEnum.URL16104
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        param.put(GeneralParameterEnum.CLASE.getName(), "C");

        listaCuentaContable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaContable
     *
     */
    public void cargarListaCuentaContableE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TasasinteresesControladorUrlEnum.URL16104
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioGrilla);

        param.put(GeneralParameterEnum.CLASE.getName(), "C");

        listaCuentaContableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    // </METODOS_CARGAR_LISTA>

    @Override
    public void abrirFormulario() {

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            visibleDeterioro = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "DETERIORO CARTERA POR CUENTA",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"));
            if(visibleDeterioro)
            {
            	visibleTasaTES = false;
            }
            else
            {
            	visibleTasaTES = true;
            }
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // METODO NO IMPLEMENTADO
    }
    
    
	public void oprimirPasarTasas() {
		try {

			int Cantidad = ejbContabilidadTres.pasarTasasInteres(Integer.parseInt(anoOrigen),
					Integer.parseInt(anoDestino), SessionUtil.getUser().getCodigo());
			if (Cantidad > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));
			} else {
				JsfUtil.agregarMensajeInformativo("Ya existe información para el año seleccionado");
			}
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(MayorizarplancuentasControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
    

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (((Date) registro.getCampos().get("FECHA_FINAL")).before(
                        (Date) registro.getCampos().get("FECHA_INICIAL"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB38"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>

        cargarListaCuentaContable();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Tasa
     * 
     */
    public void cambiarTasa() {
        // <CODIGO_DESARROLLADO>
        String aux = SysmanFunciones.nvlStr(
                        registro.getCampos().get("TASA").toString(), "");
        if (Double.parseDouble(aux) > 100) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3118"));
            registro.getCampos().put("TASA", "");
        }

        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Tasa TES
     * 
     */
    public void cambiarTasaTES() {
        // <CODIGO_DESARROLLADO>
        String aux = SysmanFunciones.nvlStr(
                        registro.getCampos().get("TASA_TES").toString(), "");
        if (Double.parseDouble(aux) > 100) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3118"));
            registro.getCampos().put("TASA_TES", "");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Anio en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarAnioC(int rowNum) {

        anioGrilla = listaInicial.getDatasource().get(rowNum %
            10).getCampos().get(GeneralParameterEnum.ANO.getName()).toString();

        cargarListaCuentaContableE();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaContable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaContable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_CONTABLE",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaContable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaContableE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

	public String getAnoOrigen() {
		return anoOrigen;
	}

	/**
	 * Asigna la variable anoOrigen
	 * 
	 * @param anoOrigen Variable a asignar en anoOrigen
	 */
	public void setAnoOrigen(String anoOrigen) {
		this.anoOrigen = anoOrigen;
	}

	/**
	 * Retorna la variable anoDestino
	 * 
	 * @return anoDestino
	 */
	public String getAnoDestino() {
		return anoDestino;
	}

	/**
	 * Asigna la variable anoDestino
	 * 
	 * @param anoDestino Variable a asignar en anoDestino
	 */
	public void setAnoDestino(String anoDestino) {
		this.anoDestino = anoDestino;
	}

	/**
	 * Retorna la lista listaanoOrigen
	 * 
	 * @return listaanoOrigen
	 */
	public List<Registro> getListaanoOrigen() {
		return listaanoOrigen;
	}

	/**
	 * Asigna la lista listaanoOrigen
	 * 
	 * @param listaanoOrigen Variable a asignar en listaanoOrigen
	 */
	public void setListaanoOrigen(List<Registro> listaanoOrigen) {
		this.listaanoOrigen = listaanoOrigen;
	}

	/**
	 * Retorna la lista listaanoDestino
	 * 
	 * @return listaanoDestino
	 */
	public List<Registro> getListaanoDestino() {
		return listaanoDestino;
	}

	/**
	 * Asigna la lista listaanoDestino
	 * 
	 * @param listaanoDestino Variable a asignar en listaanoDestino
	 */
	public void setListaanoDestino(List<Registro> listaanoDestino) {
		this.listaanoDestino = listaanoDestino;
	}


    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaContable
     * 
     * @return listaCuentaContable
     */
    public RegistroDataModelImpl getListaCuentaContable() {
        return listaCuentaContable;
    }

    /**
     * Asigna la lista listaCuentaContable
     * 
     * @param listaCuentaContable
     * Variable a asignar en listaCuentaContable
     */
    public void setListaCuentaContable(
        RegistroDataModelImpl listaCuentaContable) {
        this.listaCuentaContable = listaCuentaContable;
    }

    /**
     * Retorna la lista listaCuentaContable
     * 
     * @return listaCuentaContable
     */
    public RegistroDataModelImpl getListaCuentaContableE() {
        return listaCuentaContableE;
    }

    /**
     * Asigna la lista listaCuentaContable
     * 
     * @param listaCuentaContable
     * Variable a asignar en listaCuentaContable
     */
    public void setListaCuentaContableE(
        RegistroDataModelImpl listaCuentaContableE) {
        this.listaCuentaContableE = listaCuentaContableE;
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
        this.auxiliar = auxiliar;
    }

    public boolean isVisibleDeterioro() {
        return visibleDeterioro;
    }

    public void setVisibleDeterioro(boolean visibleDeterioro) {
        this.visibleDeterioro = visibleDeterioro;
    }

	/**
	 * @return the visibleTasaTES
	 */
	public boolean isVisibleTasaTES() {
		return visibleTasaTES;
	}

	/**
	 * @param visibleTasaTES the visibleTasaTES to set
	 */
	public void setVisibleTasaTES(boolean visibleTasaTES) {
		this.visibleTasaTES = visibleTasaTES;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>

}
