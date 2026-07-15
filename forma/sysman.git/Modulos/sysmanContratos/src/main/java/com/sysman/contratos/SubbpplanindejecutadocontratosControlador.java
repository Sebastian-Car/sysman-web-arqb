package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.enums.SubbpplanindejecutadocontratosControladorEnum;
import com.sysman.contratos.enums.SubbpplanindejecutadocontratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 23/11/2015
 * @modified jguerrero
 * @version 2. 11/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class SubbpplanindejecutadocontratosControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String cAuxiliar;
    private final String cCodigo;

    private final String cIdPlanN;
    private final String cIdPlanP;
    private final String cNombre;
    private final String cNombreProyecto;
    private final String cVigenciaMetaN;

    private String claseOrden;
    private String numeroOrden;
    private String tipot;
    private String claset;
    private String fechan;
    private String pejecucion;
    private String novedad;
    private RegistroDataModelImpl listaIdPlanP;
    private RegistroDataModelImpl listaIdPlanPE;
    private RegistroDataModelImpl listaComponente;
    private RegistroDataModelImpl listaComponenteE;
    private RegistroDataModel listaIdPlanPNombre;
    private RegistroDataModel listaIdPlanPNombreE;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaProyectoE;
    private String auxiliar;
    private String nombreProyecto;
    private Object vigencia;
    private Object vigenciaPlan;
    private String codigoProyecto;
    private Map<String, Object> parametrosEntrada;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private Map<String, Object> parametroswf;
	private String modulo;

    /**
     * Creates a new instance of
     * SubbpplanindejecutadocontratosControlador
     */
    public SubbpplanindejecutadocontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cAuxiliar = GeneralParameterEnum.AUXILIAR.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();

        cIdPlanN = SubbpplanindejecutadocontratosControladorEnum.PARAM18
                        .getValue();
        cIdPlanP = SubbpplanindejecutadocontratosControladorEnum.PARAM17
                        .getValue();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cNombreProyecto = SubbpplanindejecutadocontratosControladorEnum.PARAM16
                        .getValue();
        cVigenciaMetaN = SubbpplanindejecutadocontratosControladorEnum.PARAM15
                        .getValue();
        parametrosEntrada = SessionUtil.getFlash();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
    		if(parametroswf != null) {
    			SessionUtil.setSessionVar("modulo", "9");
    		}
    		modulo = SessionUtil.getModulo();
            numFormulario = GeneralCodigoFormaEnum.SUBBPPLANINDEJECUTADOCONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SubbpplanindejecutadocontratosControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarFlash();

        enumBase = GenericUrlEnum.BPPLANINDEJECUTADO_CONTRATO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaIdPlanP();
        cargarListaIdPlanPE();
        cargarListaCOMPONENTE();
        cargarListaCOMPONENTEE();
        cargarListaPROYECTO();
        cargarListaPROYECTOE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);
        parametrosListado.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getNovedad() {
        return novedad;
    }

    public void setNovedad(String novedad) {
        this.novedad = novedad;
    }

    public RegistroDataModelImpl getListaIdPlanP() {
        return listaIdPlanP;
    }

    public void setListaIdPlanP(RegistroDataModelImpl listaIdPlanP) {
        this.listaIdPlanP = listaIdPlanP;
    }

    public RegistroDataModelImpl getListaIdPlanPE() {
        return listaIdPlanPE;
    }

    public void setListaIdPlanPE(RegistroDataModelImpl listaIdPlanPE) {
        this.listaIdPlanPE = listaIdPlanPE;
    }

    public RegistroDataModelImpl getListaComponente() {
        return listaComponente;
    }

    public void setListaComponente(RegistroDataModelImpl listaComponente) {
        this.listaComponente = listaComponente;
    }

    public RegistroDataModelImpl getListaComponenteE() {
        return listaComponenteE;
    }

    public void setListaComponenteE(RegistroDataModelImpl listaComponenteE) {
        this.listaComponenteE = listaComponenteE;
    }

    public RegistroDataModel getListaIdPlanPNombre() {
        return listaIdPlanPNombre;
    }

    public void setListaIdPlanPNombre(RegistroDataModel listaIdPlanPNombre) {
        this.listaIdPlanPNombre = listaIdPlanPNombre;
    }

    public RegistroDataModel getListaIdPlanPNombreE() {
        return listaIdPlanPNombreE;
    }

    public void setListaIdPlanPNombreE(RegistroDataModel listaIdPlanPNombreE) {
        this.listaIdPlanPNombreE = listaIdPlanPNombreE;
    }

    public RegistroDataModelImpl getListaProyecto() {
        return listaProyecto;
    }

    public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    public RegistroDataModelImpl getListaProyectoE() {
        return listaProyectoE;
    }

    public void setListaProyectoE(RegistroDataModelImpl listaProyectoE) {
        this.listaProyectoE = listaProyectoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void cargarListaIdPlanP() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpplanindejecutadocontratosControladorUrlEnum.URL8521
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaIdPlanP = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubbpplanindejecutadocontratosControladorEnum.PARAM17
                                        .getValue());

    }

    public void cargarListaIdPlanPE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpplanindejecutadocontratosControladorUrlEnum.URL8521
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaIdPlanPE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubbpplanindejecutadocontratosControladorEnum.PARAM17
                                        .getValue());
    }

    public void cargarListaCOMPONENTE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpplanindejecutadocontratosControladorUrlEnum.URL14724
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(cVigenciaMetaN));
        param.put(SubbpplanindejecutadocontratosControladorEnum.PARAM8
                        .getValue(),
                        registro.getCampos().get(GeneralParameterEnum.AUXILIAR
                                        .getName()));

        listaComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaCOMPONENTEE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpplanindejecutadocontratosControladorUrlEnum.URL14724
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(SubbpplanindejecutadocontratosControladorEnum.PARAM8
                        .getValue(), codigoProyecto);

        listaComponenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaPROYECTO() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpplanindejecutadocontratosControladorUrlEnum.URL25398
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaPROYECTOE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpplanindejecutadocontratosControladorUrlEnum.URL25398
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaProyectoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cambiarIdPlanP() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPROYECTO() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cAuxiliar,
                        retornoString(registroAux, cCodigo));
        registro.getCampos().put(cNombreProyecto,
                        retornoString(registroAux, cNombre));
    }

    public void seleccionarFilaProyectoE(SelectEvent event) {
        // Primer metodo grilla
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornoString(registroAux, cCodigo);

        nombreProyecto = retornoString(registroAux, cNombre);
    }

    public void cambiarProyectoC(int rowNum) {
        //
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreProyecto, nombreProyecto);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaIdPlanP(SelectEvent event) {
        // Se ejecuta al cambiar combo al ingresar un nuevo registro
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cIdPlanN,
                        retornoString(registroAux, cIdPlanP));
        registro.getCampos().put(cVigenciaMetaN,
                        registroAux.getCampos()
                                        .get(SubbpplanindejecutadocontratosControladorEnum.PARAM14
                                                        .getValue()));
        registro.getCampos()
                        .put(SubbpplanindejecutadocontratosControladorEnum.PARAM9
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SubbpplanindejecutadocontratosControladorEnum.PARAM13
                                                                        .getValue()));

        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(cAuxiliar));

        cargarListaCOMPONENTE();

    }

    public void seleccionarFilaIdPlanPE(SelectEvent event) {
        // Primer metodo que se ejecuta al cambiar el combo en la
        // grilla
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornoString(registroAux, cIdPlanP);

        vigencia = retornoString(registroAux,
                        SubbpplanindejecutadocontratosControladorEnum.PARAM14
                                        .getValue());

        vigenciaPlan = retornoString(registroAux,
                        SubbpplanindejecutadocontratosControladorEnum.PARAM13
                                        .getValue());

        codigoProyecto = retornoString(registroAux, cAuxiliar);

        cargarListaCOMPONENTEE();

    }

    public void cambiarIdPlanPC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cVigenciaMetaN, vigencia);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SubbpplanindejecutadocontratosControladorEnum.PARAM9
                                        .getValue(), vigenciaPlan);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaComponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(SubbpplanindejecutadocontratosControladorEnum.PARAM12
                                        .getValue(),
                                        registroAux.getCampos().get(cCodigo));
    }

    public void seleccionarFilaComponenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornoString(registroAux, cCodigo);

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        try {

            String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "'' AND CLASEORDEN_N = ''", claseOrden,
                            "'' AND ORDENDECOMPRA_N =", numeroOrden,
                            " AND NOVEDAD_N= ", novedad, " AND PROYECTO = ''",
                            retornoString(registro, cAuxiliar), "'' ");
            Long consecutivo = ejbSysmanUtil
                            .generarConsecutivoConValorInicial(
                                            GenericUrlEnum.BPPLANINDEJECUTADO_CONTRATO
                                                            .getTable(),
                                            criterio,
                                            SubbpplanindejecutadocontratosControladorEnum.PARAM7
                                                            .getValue(),
                                            SubbpplanindejecutadocontratosControladorEnum.PARAM2
                                                            .getValue());

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
            registro.getCampos().remove(cNombre);
            registro.getCampos().remove(cNombreProyecto);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM23
                                            .getValue(), claseOrden);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM11
                                            .getValue(), novedad);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM22
                                            .getValue(), numeroOrden);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM10
                                            .getValue(), claset);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM21
                                            .getValue(), tipot);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM6
                                            .getValue(),
                                            SysmanFunciones.convertirAFecha(
                                                            fechan));
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM20
                                            .getValue(), pejecucion);
            registro.getCampos()
                            .put(SubbpplanindejecutadocontratosControladorEnum.PARAM5
                                            .getValue(),
                                            registro.getCampos()
                                                            .get(cAuxiliar));
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        registro.getCampos().remove(cNombreProyecto);

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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public Object getVigencia() {
        return vigencia;
    }

    public void setVigencia(Object vigencia) {
        this.vigencia = vigencia;
    }

    public String getTipot() {
        return tipot;
    }

    public void setTipot(String tipot) {
        this.tipot = tipot;
    }

    public String getClaset() {
        return claset;
    }

    public void setClaset(String claset) {
        this.claset = claset;
    }

    public String getFechan() {
        return fechan;
    }

    public void setFechan(String fechan) {
        this.fechan = fechan;
    }

    public String getPejecucion() {
        return pejecucion;
    }

    public void setPejecucion(String pejecucion) {
        this.pejecucion = pejecucion;
    }

    public Object getVigenciaPlan() {
        return vigenciaPlan;
    }

    public void setVigenciaPlan(Object vigenciaPlan) {
        this.vigenciaPlan = vigenciaPlan;
    }

    @Override
    public void removerCombos() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SUBNOVEDADCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    private void cargarFlash() {

        if (parametrosEntrada != null) {
            claseOrden = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM1
                                            .getValue());
            numeroOrden = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM4
                                            .getValue());
            novedad = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM3
                                            .getValue());
            tipot = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM24
                                            .getValue());
            claset = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM25
                                            .getValue());
            fechan = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM26
                                            .getValue());
            pejecucion = (String) parametrosEntrada
                            .get(SubbpplanindejecutadocontratosControladorEnum.PARAM27
                                            .getValue());

        }
    }

    private String retornoString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
