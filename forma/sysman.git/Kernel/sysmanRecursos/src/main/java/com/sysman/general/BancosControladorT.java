package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.BancosControladorTUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author apineda
 * @version 1, 06/04/2016
 * @modified spina 03/04/2017 - se refactoriza para dss
 * 
 * @version 2.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class BancosControladorT extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private RegistroDataModelImpl listaCodigoSia;
    private RegistroDataModelImpl listaCodigoSiaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    
    private String dgAno;
    
    private List<Registro> listaAnio;
    

    /**
     * Creates a new instance of BancosControladorT
     */
    public BancosControladorT() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 613
            numFormulario = GeneralCodigoFormaEnum.BANCOS_CONTROLADOR_T
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BancosControladorT.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BANCO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCodigoSia();
        cargarListaCodigoSiaE();
        cargarListaAnio();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }
    
    public void cargarListaAnio(){
    	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         try {
        	 listaAnio = RegistroConverter
                     .toListRegistro(
                                     requestManager
                                                     .getList(
                                                                     UrlServiceUtil.getInstance()
                                                                                     .getUrlServiceByUrlByEnumID(
                                                                                    		 BancosControladorTUrlEnum.URL4001
                                                                                                                     .getValue())
                                                                                     .getUrl(),
                                                                     param));
         }
         catch (SystemException e) {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoSia
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCodigoSia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("1805001");

        listaCodigoSia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoSia
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCodigoSiaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("1805001");

        listaCodigoSiaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoSia
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoSia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_SIA",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoSia
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoSiaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
    }
    // </METODOS_COMBOS_GRANDES>

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }
    
    public void aceptarAnio() {
    	 Map<String, Object> parametros = new HashMap<>();

         parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         parametros.put(GeneralParameterEnum.ANO.getName(), dgAno);
         Parameter parameter = new Parameter();
         parameter.setFields(parametros);

         UrlBean urlUpdate = UrlServiceUtil.getInstance()
                         .getUrlServiceByUrlByEnumID(
                        		 BancosControladorTUrlEnum.URL16229
                                                         .getValue());
         try
         {
             requestManager.update(urlUpdate.getUrl(),
                             urlUpdate.getMetodo(),
                             parameter);
             JsfUtil.agregarMensajeInformativo(
                     idioma.getString("MSM_PROCESO_EJECUTADO"));
         }
         catch (SystemException e)
         {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }
   }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
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
        // Metodo heredado
    }
    
    public void oprimirActSaldos() {
    	
   }
    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoSia
     * 
     * @return listaCodigoSia
     */
    public RegistroDataModelImpl getListaCodigoSia() {
        return listaCodigoSia;
    }

    /**
     * Asigna la lista listaCodigoSia
     * 
     * @param listaCodigoSia
     * Variable a asignar en listaCodigoSia
     */
    public void setListaCodigoSia(RegistroDataModelImpl listaCodigoSia) {
        this.listaCodigoSia = listaCodigoSia;
    }

    /**
     * Retorna la lista listaCodigoSia
     * 
     * @return listaCodigoSia
     */
    public RegistroDataModelImpl getListaCodigoSiaE() {
        return listaCodigoSiaE;
    }

    /**
     * Asigna la lista listaCodigoSia
     * 
     * @param listaCodigoSia
     * Variable a asignar en listaCodigoSia
     */
    public void setListaCodigoSiaE(RegistroDataModelImpl listaCodigoSiaE) {
        this.listaCodigoSiaE = listaCodigoSiaE;
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

	/**
	 * @return the dgAno
	 */
	public String getDgAno() {
		return dgAno;
	}

	/**
	 * @param dgAno the dgAno to set
	 */
	public void setDgAno(String dgAno) {
		this.dgAno = dgAno;
	}

	/**
	 * @return the listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * @param listaAnio the listaAnio to set
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
