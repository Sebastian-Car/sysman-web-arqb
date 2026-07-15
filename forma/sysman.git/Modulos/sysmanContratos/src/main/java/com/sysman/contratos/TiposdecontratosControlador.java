package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.enums.TiposdecontratosControladorUrlEnum;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 07/10/2015
 * 
 * @author eamaya
 * @version 2.0, 15/08/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class TiposdecontratosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "CODIGO"
     */
    private final String cCodigo;
    /**
     * Constante definida para almacenar la cadena "NUMERACIONUNICA"
     */
    private final String cNumeracionUnica;

    /**
     * Constante definida para almacenar la cadena "true"
     */
    private RegistroDataModelImpl listatipoFut;
    private RegistroDataModelImpl listatipoSia;
    private RegistroDataModelImpl listatipoSigec;
    private List<Registro> listaTexto73;
    private List<Registro> listaTipocontratos;
    private List<Registro> listatipoContratocgr;
    private RegistroDataModelImpl listaCodContTolima;
    private boolean numeroInicial;
    private int marca;
    private boolean codContTolimaVisible;
    private boolean verTipoContratoCasanare;
    private boolean manejaNominaDeContratistas;
    private boolean manejaPagoDeEstampillas;
    private boolean visibleObliga;
    private boolean consaldo;

   

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public TiposdecontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = "CODIGO";
        cNumeracionUnica = "NUMERACIONUNICA";
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPOSDECONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            numeroInicial = true;
            marca = 0;
        }
        catch (Exception ex) {
            Logger.getLogger(TiposdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOORDENDECOMPRA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL001
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        TiposdecontratosControladorUrlEnum.URL002.getValue());

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        TiposdecontratosControladorUrlEnum.URL003.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL004
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL005
                                                        .getValue());
    }

    @Override
    public void iniciarListas() {
        cargarListaCodContTolima();
        cargarListaTexto73();
        cargarListaTipocontratos();
        cargarListatipoContratocgr();
        cargarListatipoFut();
        cargarListatipoSia();
        cargarListatipoSigec();
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilatipoFut(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOFUT",
                        registroAux.getCampos().get(cCodigo));
    }

    public void seleccionarFilatipoSia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOSIA",
                        registroAux.getCampos().get(cCodigo));
    }

	public void seleccionarFilatipoSigec(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPO_SIGEC", registroAux.getCampos().get("CODIGO"));
	}

    public void cargarListaTexto73() {

        try {
            listaTexto73 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiposdecontratosControladorUrlEnum.URL6502
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListatipoFut() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL6878
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListatipoSia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL7445
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoSia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTipocontratos() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaTipocontratos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiposdecontratosControladorUrlEnum.URL7542
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListatipoContratocgr() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listatipoContratocgr = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiposdecontratosControladorUrlEnum.URL7854
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodContTolima() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL8723
                                                        .getValue());
        listaCodContTolima = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, cCodigo);
    }

    public void seleccionarFilaCodContTolima(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODCONTTOLIMA",
                        registroAux.getCampos().get(cCodigo));
    }

    public void cargarListatipoSigec() {
    	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(),
                         compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposdecontratosControladorUrlEnum.URL1928
                                                        .getValue());
        
        listatipoSigec = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }    
    
    @Override
    public void abrirFormulario() {
        codContTolimaVisible = true;

        verTipoContratoCasanare = valorParametro(
                        "VER TIPO CONTRATO CASANARE");

        manejaNominaDeContratistas = valorParametro(
                        "MANEJA NOMINA DE CONTRATISTAS");

        manejaPagoDeEstampillas = valorParametro(
                        "MANEJA PAGO DE ESTAMPILLAS");

        visibleObliga = valorParametro(
                "NUEVOS CAMPOS OBLIGATORIOS EN CONTROL DE CONTRATOS");
    }

    /**
     * Funcion que retorna verdadero o falso, dependiendo del nombre
     * del parametro recibido
     *
     * @param nombreParametro
     * @return respuesta
     */
    private boolean valorParametro(String nombreParametro) {
        boolean respuesta = true;
        try {
            respuesta = "SI".equalsIgnoreCase(ejbSysmanUtil.consultarParametro(
                            compania, nombreParametro, modulo, new Date(),
                            false));

        }
        catch (NullPointerException | SystemException ex) {
            respuesta = false;
            Logger.getLogger(TiposdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        return respuesta;
    }

    /**
     * Metodo ejecutado al cambiar el control NumeracionUnica
     * 
     * 
     */
    public void cambiarNumeracionUnica() {
        // <CODIGO_DESARROLLADO>
        numeroInicial = (boolean) registro.getCampos().get(cNumeracionUnica);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
        if (css != null) {
            boolean numeracionUnica = (boolean) registro.getCampos()
                            .get(cNumeracionUnica);
            numeroInicial = numeracionUnica;
        }
        else {
            registro.getCampos().put(cNumeracionUnica, false);
            registro.getCampos().put("GENERACONTRATO", false);
            numeroInicial = false;
        }
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        actualizarAntes();
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {

        if ("m".equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        //registro.getCampos()
       // .remove("CON_SALDO");
        
               return true;
        
        
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public boolean isNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(boolean numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public int getMarca() {
        return marca;
    }

    public void setMarca(int marca) {
        this.marca = marca;
    }

    public boolean isCodContTolimaVisible() {
        return codContTolimaVisible;
    }

    public void setCodContTolimaVisible(boolean codContTolimaVisible) {
        this.codContTolimaVisible = codContTolimaVisible;
    }

    public boolean isVerTipoContratoCasanare() {
        return verTipoContratoCasanare;
    }

    public void setVerTipoContratoCasanare(boolean verTipoContratoCasanare) {
        this.verTipoContratoCasanare = verTipoContratoCasanare;
    }

    public boolean isManejaNominaDeContratistas() {
        return manejaNominaDeContratistas;
    }

    public void setManejaNominaDeContratistas(
        boolean manejaNominaDeContratistas) {
        this.manejaNominaDeContratistas = manejaNominaDeContratistas;
    }

    public boolean isManejaPagoDeEstampillas() {
        return manejaPagoDeEstampillas;
    }

    public void setManejaPagoDeEstampillas(boolean manejaPagoDeEstampillas) {
        this.manejaPagoDeEstampillas = manejaPagoDeEstampillas;
    }

    public void oprimirDocumentos() {
        agregarRegistroNuevo(false);

        Map<String, Object> parametros = new TreeMap<>();
        parametros.put("tipoContrato",
                        registro.getCampos().get(cCodigo));
        parametros.put("nombreContrato",
                        registro.getCampos().get("NOMBRE"));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CLASIFICADOCUMENTOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);

    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTexto73() {
        return listaTexto73;
    }

    public void setListaTexto73(List<Registro> listaTexto73) {
        this.listaTexto73 = listaTexto73;
    }

    public List<Registro> getListaTipocontratos() {
        return listaTipocontratos;
    }

    public void setListaTipocontratos(List<Registro> listaTipocontratos) {
        this.listaTipocontratos = listaTipocontratos;
    }

    /**
     * Retorna la lista listatipoContratocgr
     *
     * @return listatipoContratocgr
     */
    public List<Registro> getListatipoContratocgr() {
        return listatipoContratocgr;
    }

    /**
     * Asigna la lista listatipoContratocgr
     *
     * @param listatipoContratocgr
     * Variable a asignar en listatipoContratocgr
     */
    public void setListatipoContratocgr(List<Registro> listatipoContratocgr) {
        this.listatipoContratocgr = listatipoContratocgr;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listatipoSia
     *
     * @return listatipoSia
     */
    public RegistroDataModelImpl getListatipoSia() {
        return listatipoSia;
    }

    /**
     * Asigna la lista listatipoSia
     *
     * @param listatipoSia
     * Variable a asignar en listatipoSia
     */
    public void setListatipoSia(RegistroDataModelImpl listatipoSia) {
        this.listatipoSia = listatipoSia;
    }

    /**
     * Retorna la lista listatipoFut
     *
     * @return listatipoFut
     */
    public RegistroDataModelImpl getListatipoFut() {
        return listatipoFut;
    }

    /**
     * Asigna la lista listatipoFut
     *
     * @param listatipoFut
     * Variable a asignar en listatipoFut
     */
    public void setListatipoFut(RegistroDataModelImpl listatipoFut) {
        this.listatipoFut = listatipoFut;
    }

    public RegistroDataModelImpl getListaCodContTolima() {
        return listaCodContTolima;
    }

    public void setListaCodContTolima(
        RegistroDataModelImpl listaCodContTolima) {
        this.listaCodContTolima = listaCodContTolima;
    }

	/**
	 * @return the visibleObliga
	 */
	public boolean isVisibleObliga() {
		return visibleObliga;
	}

	/**
	 * @param visibleObliga the visibleObliga to set
	 */
	public void setVisibleObliga(boolean visibleObliga) {
		this.visibleObliga = visibleObliga;
	}
	
	public RegistroDataModelImpl getListatipoSigec() {
		return listatipoSigec;
	}

	public void setListatipoSigec(RegistroDataModelImpl listatipoSigec) {
		this.listatipoSigec = listatipoSigec;
	}
	
	public boolean isConsaldo() {
			return consaldo;
		}

	public void setConsaldo(boolean consaldo) {
			this.consaldo = consaldo;
		}
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

}
