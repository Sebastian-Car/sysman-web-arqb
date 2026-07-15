package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.enums.ComprobantecntretencionsControladorEnum;
import com.sysman.contabilidad.enums.ComprobantecntretencionsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 04/03/2016
 * 
 * @author jrodrigueza
 * @version 2, 18/04/2017 Proceso de refactoring y ajustes segun
 * recomendaciones de SonarLint.
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 */

@ManagedBean
@ViewScoped
public class ComprobantecntretencionsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del mudulo
     * actual, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private RegistroDataModelImpl listaCodigoRetencion;
    private RegistroDataModelImpl listaCodigoRetencionE;
    private RegistroDataModelImpl listaActEcca;
    private RegistroDataModelImpl listaActEccaE;
    private String auxiliar;
    private List<Registro> listaTipo;
    private List<Registro> listaTipoRetencion;
    /**
     * Ano que se recibe por parametro desde el formulario
     * COMPROBANTE_CNT.
     */
    private int ano;
    private boolean terceroautoretenedor;
    /**
     * Tipo que se recibe por parametro desde el formulario
     * COMPROBANTE_CNT.
     */
    private String tipo;
    /**
     * Nombre que recibe por parametro desde el formulario
     * COMPROBANTE_CNT.
     */
    private String nombreComprobante;
    /**
     * Valor base con iva que recibe por parametro desde el formulario
     * COMPROBANTE_CNT.
     */
    private double vlrBaseIva;
    /**
     * Valor base que recibe por parametro desde el formulario
     * COMPROBANTE_CNT.
     */
    private double vlrBase;

    private String numeroComprobante;
    private Map<String, Object> rid;
    private int indice;
    private String opcionMenu;
    private String mes;
    /**
     * Copia del valor de la base gravable. Se captura en el evento
     * activarEdicion.
     */
    private Object copiaValorBase;
    /**
     * Copia del valor de la retencion. Se captura en el evento
     * activarEdicion.
     */
    private Object copiaValor;

    /**
     * Campo TIPORETENCION.
     */
    private static final String CAMPO_TIPORETENCION = "TIPORETENCION";
    /**
     * Campo VALORBASE.
     */
    private static final String CAMPO_VALORBASE = "VALORBASE";
    /**
     * Campo CODIGORETENCION.
     */
    private static final String CAMPO_CODIGORETENCION = "CODIGORETENCION";
    /**
     * Campo ACTIVIDAD_ECONOMICA.
     */
    private static final String CAMPO_ACTIVIDAD_ECONOMICA = "ACTIVIDAD_ECONOMICA";

    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Indica si permite modificar el valor de la base gravable, segun
     * el tipo y codigo de retencion.
     */
    private boolean permiteModificarBaseGravable;
    
    private boolean manejaCausacion=false;
	private String tercero;
	private String sucursal;
	private Boolean manejaCodigoEcca = false;

    /**
     * Creates a new instance of ComprobantecntretencionsControlador
     */
    public ComprobantecntretencionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTECNTRETENCIONS_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                rid = (Map<String, Object>) parametros.get("rid");
                ano = Integer.parseInt(parametros.get("ano").toString());
                mes = extraerString(parametros.get("mes"));
                tipo = extraerString(parametros.get("tipoComp"));
                numeroComprobante = extraerString(parametros.get("numeroComp"));
                nombreComprobante = extraerString(parametros.get("nombreComprobante"));
                String vlrBaseIvaE = parametros.get("vlrBaseIva").toString();
                vlrBaseIva = Double.parseDouble(vlrBaseIvaE);
                String vlrBaseE = parametros.get("vlrBase").toString();
                vlrBase = Double.parseDouble(vlrBaseE);
                opcionMenu = extraerString(parametros.get("opcionMenu"));
                terceroautoretenedor =Boolean.valueOf(extraerString(parametros.get("terceroautoRetenedor")));
                tercero = extraerString(parametros.get("tercero"));
                sucursal = extraerString(parametros.get("sucursal"));
            }
            validarPermisos();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.COMPROBANTE_CNTRETENCION;
        buscarLlave();
        nombreComprobante = nombreComprobante + " No. " + numeroComprobante;
        reasignarOrigen();
        registro = new Registro();
        cargarListaTipo();
        cargarListaTipoRetencion();
        cargarListaCodigoRetencion();
        cargarListaActEcca();
        cargarListaActEccaE();
    	try {
			manejaCausacion = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CAUSACION AUTOMATICA", SessionUtil.getModulo(), new Date(), true),
					"NO").toString().equals("SI")?true:false;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
    	
        abrirFormulario();
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        Map<String, Object> registroIni = registro.getCampos();
        copiaValorBase = registroIni.get(CAMPO_VALORBASE);
        copiaValor = registroIni.get("VALOR");
        String tipoRet = SysmanFunciones.toString(registroIni.get("TIPORETENCION"));
        cargarListaCodigoRetencionE();
        manejaCodigoEcca(tipoRet);
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put("TIPO", tipo);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                        numeroComprobante);
    }

    public void cargarListaTipo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantecntretencionsControladorUrlEnum.URL6758
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoRetencion() {
        try {
        	if (terceroautoretenedor) {
        		listaTipoRetencion = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        ComprobantecntretencionsControladorUrlEnum.URL7073
                                                                        .getValue())
                                        .getUrl(), null));
        	}else {
        		listaTipoRetencion = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        ComprobantecntretencionsControladorUrlEnum.URL7072
                                                                        .getValue())
                                        .getUrl(), null));
        		
        	}
        	
        	Boolean RetencionXRegimen = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CONFIGURACION DE RETENCIONES POR REGIMEN", SessionUtil.getModulo(), new Date(), true),
					"NO").toString().equals("SI")?true:false;
        	if(RetencionXRegimen) {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.NIT.getName(), tercero);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
        		
        		listaTipoRetencion = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        ComprobantecntretencionsControladorUrlEnum.URL8014
                                                                        .getValue())
                                        .getUrl(), param));
        	}
        	
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoRetencion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobantecntretencionsControladorUrlEnum.URL7597
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ComprobantecntretencionsControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(ComprobantecntretencionsControladorEnum.PARAM1.getValue(),
                        ano);
        param.put(ComprobantecntretencionsControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(CAMPO_TIPORETENCION));

        listaCodigoRetencion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoRetencionE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobantecntretencionsControladorUrlEnum.URL7597
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ComprobantecntretencionsControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(ComprobantecntretencionsControladorEnum.PARAM1.getValue(),
                        ano);
        param.put(ComprobantecntretencionsControladorEnum.PARAM2.getValue(),
                        listaInicial.getDatasource().get(indice % 10)
                                        .getCampos().get(CAMPO_TIPORETENCION));

        listaCodigoRetencionE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }
    
    /**
     * 
     * Carga la lista listaActEcca
     *
     */
    public void cargarListaActEcca(){

    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					ComprobantecntretencionsControladorUrlEnum.URL1953
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),
    			compania);
    	param.put(GeneralParameterEnum.TERCERO.getName(),
    			tercero);
    	param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

    	listaActEcca = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
    			"COD_CIIU");
    }
    /**
     * 
     * Carga la lista listaActEcca
     *
     */
    public void  cargarListaActEccaE(){
    	listaActEccaE = listaActEcca;
    }

    public void cambiarTipoRetencion() {
        // <CODIGO_DESARROLLADO>
        String tipoRet = SysmanFunciones
                        .nvl(registro.getCampos().get(CAMPO_TIPORETENCION), "")
                        .toString();
        if ("".equals(tipoRet)) {
            registro.getCampos().put(CAMPO_VALORBASE, null);
            registro.getCampos().put(CAMPO_CODIGORETENCION, null);
            registro.getCampos().put(GeneralParameterEnum.VALOR.getName(),
                            null);
            return;
        }
        asignarValorBase(tipoRet);
        registro.getCampos().put(CAMPO_CODIGORETENCION, null);
        registro.getCampos().put(GeneralParameterEnum.VALOR.getName(), 0);
        cargarListaCodigoRetencion();
        manejaCodigoEcca(tipoRet);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValor() {
        // <CODIGO_DESARROLLADO>
        Object tipoRetencion = registro.getCampos().get(CAMPO_TIPORETENCION);
        Object codigoRetencion = registro.getCampos()
                        .get(CAMPO_CODIGORETENCION);
        if (!permiteCambiarValor(tipoRetencion, codigoRetencion)) {
            registro.getCampos().put(GeneralParameterEnum.VALOR.getName(), "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBase.
     */
    public void cambiarValorBase() {
        // <CODIGO_DESARROLLADO>
        Object tipoRetencion = registro.getCampos().get(CAMPO_TIPORETENCION);
        Object codigoRetencion = registro.getCampos()
                        .get(CAMPO_CODIGORETENCION);
        Object valorBase = registro.getCampos().get(CAMPO_VALORBASE);
        if (baseMenorAlLimite(tipoRetencion,
                        codigoRetencion,
                        valorBase)) {
            return;
        }
        if (!permiteCambiarValorBase(tipoRetencion, codigoRetencion)) {
            asignarValorBase(tipoRetencion);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Segun el tipo de retencion y el parametro <i>MONTO SOMETIDO
     * RETENCION IVA = VALOR IVA</i> asigna el valor base.
     * 
     * @param tipoRetencion
     * Tipo de Retencion.
     */
    private void asignarValorBase(Object tipoRetencion) {
        double nuevoValorBase;
        String parametroMontoIgualIVA = getParametro(
                        "MONTO SOMETIDO RETENCION IVA = VALOR IVA", "NO");
        if ("IVA".equals(tipoRetencion)
            && "SI".equals(parametroMontoIgualIVA)) {
            nuevoValorBase = vlrBaseIva;
        }
        else {
            nuevoValorBase = vlrBase;
        }
        registro.getCampos().put(CAMPO_VALORBASE, nuevoValorBase);
    }

    public void cambiarTipoRetencionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        String tipoRet = SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(CAMPO_TIPORETENCION),
                                        "")
                        .toString();
        if ("".equals(tipoRet)) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_VALORBASE, null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_CODIGORETENCION, null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.VALOR.getName(), null);
            return;
        }
        asignarValorBase(tipoRet);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(CAMPO_CODIGORETENCION, null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.VALOR.getName(),
                        0);
        cargarListaCodigoRetencionE();
        manejaCodigoEcca(tipoRet);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoRetencionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Valor en la fila
     * seleccionada dentro de la grilla.
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Object tipoRetencion = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(CAMPO_TIPORETENCION);
        Object codigoRetencion = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(CAMPO_CODIGORETENCION);
        if (!permiteCambiarValor(tipoRetencion, codigoRetencion)) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.VALOR.getName(),
                                            copiaValor);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBase en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorBaseC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Object tipoRetencion = listaInicial.getDatasource().get(indice % 10)
                        .getCampos().get(CAMPO_TIPORETENCION);
        Object codigoRetencion = listaInicial.getDatasource().get(indice % 10)
                        .getCampos().get(CAMPO_CODIGORETENCION);
        Object valorBase = listaInicial.getDatasource().get(indice % 10)
                        .getCampos().get(CAMPO_VALORBASE);
        if (baseMenorAlLimite(tipoRetencion, codigoRetencion,
                        valorBase)) {
            return;
        }
        if (!permiteCambiarValorBase(tipoRetencion, codigoRetencion)) {
            listaInicial.getDatasource().get(indice % 10).getCampos()
                            .put(CAMPO_VALORBASE, copiaValorBase);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoRetencion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_CODIGORETENCION,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

    }

    public void seleccionarFilaCodigoRetencionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActEcca
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActEcca(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("ACTIVIDAD_ECONOMICA", registroAux.getCampos().get("COD_CIIU"));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActEcca
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActEccaE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("COD_CIIU"));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);
        parametros.put("ano", String.valueOf(ano));
        parametros.put("mes", mes);
        parametros.put("tipoMov", tipo);
        parametros.put("opcionMenu", opcionMenu);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }
    
    public void manejaCodigoEcca(String tipo) {
    	
    	try {
    	Map<String, Object> param = new HashMap<>();
    	param.put(GeneralParameterEnum.CODIGO.getName(), tipo);

        Registro rs = RegistroConverter
			                .toRegistro(requestManager.get(
			                                UrlServiceUtil.getInstance()
			                                                .getUrlServiceByUrlByEnumID(
			                                                		ComprobantecntretencionsControladorUrlEnum.URL8012.getValue())
			                                                .getUrl(),
			                                                param));


        if (rs != null)
        {

            manejaCodigoEcca = Boolean.valueOf(SysmanFunciones.toString(rs.getCampos().get("MAN_ACT_ECCA")));
        }
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
    }

    private boolean campoObligatorioEcca() {
    	Map<String, Object> campos = registro.getCampos();
    	if(manejaCodigoEcca) {
    		if (SysmanFunciones.validarCampoVacio(campos, CAMPO_ACTIVIDAD_ECONOMICA)) {
    			return true;
    		}
    	}
    	return false;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRERETENCIONES");
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", ano);
        registro.getCampos().put("TIPO", tipo);
        registro.getCampos().put("NUMERO", numeroComprobante);
        if (existeRetencion()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB5"));
            return false;
        }
        if (campoObligatorioEcca()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4463"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean existeRetencion() {
        boolean existe = false;
        try {
            String urlEnumId = ComprobantecntretencionsControladorUrlEnum.URL24799
                            .getValue();
            String url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
            Map<String, Object> params = new TreeMap<>();
            params.put(ComprobantecntretencionsControladorEnum.PARAM0
                            .getValue(), compania);
            params.put(ComprobantecntretencionsControladorEnum.PARAM1
                            .getValue(), ano);
            params.put(ComprobantecntretencionsControladorEnum.PARAM2
                            .getValue(), tipo);
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            numeroComprobante);
            params.put(CAMPO_TIPORETENCION,
                            registro.getCampos().get(CAMPO_TIPORETENCION));
            params.put(CAMPO_CODIGORETENCION,
                            registro.getCampos().get(CAMPO_CODIGORETENCION));
            Parameter parameter = requestManager.get(url, params);
            Registro reg = RegistroConverter.toRegistro(parameter);
            existe = reg != null;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return existe;
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
        if (faltanCamposObligatorios()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB7"));
            return false;
        }
        if (campoObligatorioEcca()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4463"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
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
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("TIPO");
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.PORCIVA.getName());
        registro.getCampos().remove("NOMBRERETENCIONES");
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Validacion de campos obligatorios segun el tipo.
     *
     * @return Verdadero si hay camos nulos o vacios.
     */
    private boolean faltanCamposObligatorios() {
        Map<String, Object> campos = registro.getCampos();
        if (SysmanFunciones.validarCampoVacio(campos, CAMPO_TIPORETENCION)
            || SysmanFunciones.validarCampoVacio(campos, CAMPO_CODIGORETENCION)
            || SysmanFunciones.validarCampoVacio(campos,
                            GeneralParameterEnum.VALOR.getName())) {
            return true;
        }
        if (SysmanFunciones.validarCampoVacio(campos, CAMPO_VALORBASE)) {
            return true;
        }
        return false;
    }

    /**
     * Valida si la base gravable de la retencion no supera el limite
     * inferior configurado para esta, para lo cual no se permite
     * ingresar valor de esta.
     * 
     * @param tipoRetencion
     * Tipo de Retencion.
     * @param codigoRetencion
     * Codigo de la retencion.
     * @param valorBase
     * Valor base gravable.
     * @return Verdadero si el valor base es menor al limite inferior
     * de retencion.
     */
    private boolean baseMenorAlLimite(Object tipoRetencion,
        Object codigoRetencion, Object valorBase) {
        boolean esMayorQueLimite = esMayorQuelimiteInferiorRete(tipoRetencion,
                        codigoRetencion,
                        valorBase);
        if (!esMayorQueLimite) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1782"));
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Verifica que el valor base sea mayor al limite inferior de la
     * retencion.
     * 
     * @param tipoRetencion
     * Tipo de retencion.
     * @param codigoRetencion
     * Codigo de la retencion.
     * @param valorBase
     * Valor base.
     * @return Verdadero si el valor base es mayoir al limite inferior
     * de retencion.
     */
    public boolean esMayorQuelimiteInferiorRete(Object tipoRetencion,
        Object codigoRetencion,
        Object valorBase) {
        boolean esMayorQueLimite = false;
        BigDecimal valor = new BigDecimal((String) valorBase);
        try {
            esMayorQueLimite = ejbContabilidadCinco.LimiteInferiorRetencion(
                            compania, tipoRetencion.toString(), ano,
                            codigoRetencion.toString(), valor);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return esMayorQueLimite;
    }

    /**
     * Consulta el indicador de permite modificar valor.
     * 
     * @param tipoRetencion
     * Tipo de retencion.
     * @param codigoRetencion
     * Codigo de retencion.
     * @return Verdadero si se puede modificar el valor.
     */
    private boolean permiteCambiarValor(Object tipoRetencion,
        Object codigoRetencion) {
        boolean permiteModificar = false;
        if (tipoRetencion == null || codigoRetencion == null) {
            return permiteModificar;
        }
        try {
            permiteModificar = ejbContabilidadCinco.permiteCambiarValor(
                            compania, tipoRetencion.toString(), ano,
                            codigoRetencion.toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (!permiteModificar) {
            String notificacion = idioma.getString("TB_TB10");
            notificacion = notificacion.replace("s$tipoRetencion$s",
                            tipoRetencion.toString());
            notificacion = notificacion.replace("s$codigoRetencion$s",
                            codigoRetencion.toString());
            JsfUtil.agregarMensajeErrorDialogo(notificacion);
        }
        return permiteModificar;
    }

    /**
     * Consulta el indicador de permite modificar valor base.
     * 
     * @param tipoRetencion
     * Tipo de retencion.
     * @param codigoRetencion
     * Codigo de retencion.
     * @return Verdadero si se puede modificar el valor base.
     */
    private boolean permiteCambiarValorBase(Object tipoRetencion,
        Object codigoRetencion) {
        permiteModificarBaseGravable = false;
        if (tipoRetencion == null || codigoRetencion == null) {
            return permiteModificarBaseGravable;
        }
        try {
            permiteModificarBaseGravable = ejbContabilidadCinco
                            .permiteCambiarValorBase(
                                            compania, tipoRetencion.toString(),
                                            ano,
                                            codigoRetencion.toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (!permiteModificarBaseGravable) {
            String mensaje = idioma.getString("TB_TB1783");
            mensaje = mensaje.replace("s$tipoRetencion$s",
                            tipoRetencion.toString());
            mensaje = mensaje.replace("s$codigoRetencion$s",
                            codigoRetencion.toString());
            JsfUtil.agregarMensajeErrorDialogo(mensaje);
        }
        return permiteModificarBaseGravable;
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreComprobante() {
        return nombreComprobante;
    }

    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    public List<Registro> getListaTipoRetencion() {
        return listaTipoRetencion;
    }

    public void setListaTipoRetencion(List<Registro> listaTipoRetencion) {
        this.listaTipoRetencion = listaTipoRetencion;
    }

    public RegistroDataModelImpl getListaCodigoRetencion() {
        return listaCodigoRetencion;
    }

    public void setListaCodigoRetencion(
        RegistroDataModelImpl listaCodigoRetencion) {
        this.listaCodigoRetencion = listaCodigoRetencion;
    }

    public RegistroDataModelImpl getListaCodigoRetencionE() {
        return listaCodigoRetencionE;
    }

    public void setListaCodigoRetencionE(
        RegistroDataModelImpl listaCodigoRetencionE) {
        this.listaCodigoRetencionE = listaCodigoRetencionE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

	public boolean isManejaCausacion() {
		return manejaCausacion;
	}

	public void setManejaCausacion(boolean manejaCausacion) {
		this.manejaCausacion = manejaCausacion;
	}

	/**
	 * @return the listaActEcca
	 */
	public RegistroDataModelImpl getListaActEcca() {
		return listaActEcca;
	}

	/**
	 * @param listaActEcca the listaActEcca to set
	 */
	public void setListaActEcca(RegistroDataModelImpl listaActEcca) {
		this.listaActEcca = listaActEcca;
	}

	/**
	 * @return the listaActEccaE
	 */
	public RegistroDataModelImpl getListaActEccaE() {
		return listaActEccaE;
	}

	/**
	 * @param listaActEccaE the listaActEccaE to set
	 */
	public void setListaActEccaE(RegistroDataModelImpl listaActEccaE) {
		this.listaActEccaE = listaActEccaE;
	}   

}
