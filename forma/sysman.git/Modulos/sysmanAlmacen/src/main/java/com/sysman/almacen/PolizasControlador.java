package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.PolizasControladorEnum;
import com.sysman.almacen.enums.PolizasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 24/11/2015
 * @version 2, 04/05/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class PolizasControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String etqPol;
    private final String etqPolDos;
    private Registro registroSub;
    private RegistroDataModelImpl listaAseguradora;
    private RegistroDataModelImpl listaPlacaSeleccion;
    private RegistroDataModelImpl listaElementosSeleccion;
    private RegistroDataModelImpl listaGrupos;
    private RegistroDataModelImpl listaSubdpolizasserie;
    private String cbPlaca;
    private String cbElemento;
    private String cbGrupo;
    private Double cbValorPlaca;
    private Double valorLiberado;
    private boolean bloqueaVigente;
    private String cpVigenciaActual;
    private String visiblePlacaS;
    private String visibleElementoS;
    private String visibleGrupoS;
    private String nombreAseguradora;
    private boolean bloqueaCKGrupo;
    private boolean bloqueaCKElemento;
    private boolean bloqueaCKSerie;
    private String cpRiesgosSeleccion;
    private String visibleRiesgosS;
    private boolean visibleSubPolizas;
    private boolean manejaControlValor;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private static final String VISIBLEBLOCK = "block";
    private static final String VISIBLENONE = "none";

    public PolizasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        etqPol = "TB_TB3130";
        etqPolDos = "TB_TB3131";
        try {
            numFormulario = GeneralCodigoFormaEnum.POLIZAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.POLIZAS_ACTIVOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaSubdpolizasserie() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_POLIZAS_ACTIVOS
                                                        .getGridKey());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PolizasControladorEnum.ASEGURADORA.getValue(),
                        registro.getCampos()
                                        .get(PolizasControladorEnum.ASEGURADORA
                                                        .getValue()));
        param.put(PolizasControladorEnum.NUMERO_POLIZA.getValue(),
                        registro.getCampos()
                                        .get(PolizasControladorEnum.NUMERO_POLIZA
                                                        .getValue()));

        try {
            listaSubdpolizasserie = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "D_POLIZAS_ACTIVOS"));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaaseguradora() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PolizasControladorUrlEnum.URL7194
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaAseguradora = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NITASEGURADORA");

    }

    public void cargarListaPlacaSeleccion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PolizasControladorUrlEnum.URL7627
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), cbElemento);
        listaPlacaSeleccion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    public void cargarListaElementosSeleccion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PolizasControladorUrlEnum.URL8175
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PolizasControladorEnum.GRUPO.getValue(),
                        cbGrupo == null ? "" : cbGrupo + "%");
        listaElementosSeleccion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ELEMENTO.getName());
    }

    public void cargarListaGrupos() {
        String digitosAgrupacion = "6";
        try {
            digitosAgrupacion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            modulo,
                                            new Date(), true), "6");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PolizasControladorUrlEnum.URL9262
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PolizasControladorEnum.DIGITOS.getValue(), digitosAgrupacion);
        listaGrupos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "GRUPO");
    }

    public void seleccionarFilaPlacaSeleccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cbPlaca = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.SERIE.getName()),
                        "")
                        .toString();
        cbValorPlaca = Double.parseDouble(SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.VALOR.getName()),0).toString());
    }

    public void seleccionarFilaElementosSeleccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cbElemento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()),
                                        "")
                        .toString();
        cargarListaPlacaSeleccion();
        cbPlaca = null;
    }

    public void seleccionarFilaGrupos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cbGrupo = SysmanFunciones.nvl(registroAux.getCampos().get("GRUPO"), "")
                        .toString();
        cargarListaElementosSeleccion();
        cbElemento = null;
        cbPlaca = null;
    }

    public void cambiargrupo() {
        registro.getCampos().put(PolizasControladorEnum.SEL_ELEMENTO.getValue(),
                        false);
        registro.getCampos().put(PolizasControladorEnum.SEL_SERIE.getValue(),
                        false);
        if (Boolean.valueOf(registro.getCampos()
                        .get(PolizasControladorEnum.SEL_GRUPO.getValue())
                        .toString())) {
            visibleGrupoS = VISIBLEBLOCK;
            visibleElementoS = VISIBLENONE;
            visiblePlacaS = VISIBLENONE;
            cargarListaGrupos();
            cbElemento = null;
            cbPlaca = null;
        }
        else {
            visibleGrupoS = VISIBLENONE;
            visibleElementoS = VISIBLENONE;
            visiblePlacaS = VISIBLENONE;
            cbGrupo = null;
            cbElemento = null;
            cbPlaca = null;
        }
    }

    public void cambiarElemento() {
        registro.getCampos().put(PolizasControladorEnum.SEL_GRUPO.getValue(),
                        false);
        registro.getCampos().put(PolizasControladorEnum.SEL_SERIE.getValue(),
                        false);
        if (Boolean.valueOf(
                        registro.getCampos()
                                        .get(PolizasControladorEnum.SEL_ELEMENTO
                                                        .getValue())
                                        .toString())) {
            visibleGrupoS = VISIBLEBLOCK;
            visibleElementoS = VISIBLEBLOCK;
            visiblePlacaS = VISIBLENONE;
            cargarListaGrupos();
            cargarListaElementosSeleccion();
            cbPlaca = null;
        }
        else {
            visibleGrupoS = VISIBLENONE;
            visibleElementoS = VISIBLENONE;
            visiblePlacaS = VISIBLENONE;
            cbGrupo = null;
            cbElemento = null;
            cbPlaca = null;
        }
    }

    public void cambiarSerie() {
        registro.getCampos().put(PolizasControladorEnum.SEL_ELEMENTO.getValue(),
                        false);
        registro.getCampos().put(PolizasControladorEnum.SEL_GRUPO.getValue(),
                        false);
        if (Boolean.valueOf(registro.getCampos()
                        .get(PolizasControladorEnum.SEL_SERIE.getValue())
                        .toString())) {
            visibleGrupoS = VISIBLEBLOCK;
            visibleElementoS = VISIBLEBLOCK;
            visiblePlacaS = VISIBLEBLOCK;
            cargarListaGrupos();
            cargarListaElementosSeleccion();
            cargarListaPlacaSeleccion();
        }
        else {
            visibleGrupoS = VISIBLENONE;
            visibleElementoS = VISIBLENONE;
            visiblePlacaS = VISIBLENONE;
            cbGrupo = null;
            cbElemento = null;
            cbPlaca = null;
        }
    }
    
    public void cambiarvalor() {
    	generarTotalesSub();
    }

    public void seleccionarFilaAseguradora(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(PolizasControladorEnum.ASEGURADORA.getValue(),
                        registroAux.getCampos().get("NITASEGURADORA"));
        registro.getCampos().put(
                        PolizasControladorEnum.NOMBREASEGURADORA.getValue(),
                        SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get("NOMBRE"), "")
                                        .toString());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(GeneralParameterEnum.SUCURSAL
                                                                        .getName()),
                                                        "")
                                        .toString());
    }

    public void agregarRegistroSubSubdpolizasserie() {
        try {
        	
        	UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_POLIZAS_ACTIVOS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubdpolizasserie();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubdpolizasserie(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_POLIZAS_ACTIVOS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaSubdpolizasserie();
        }
    }

    public void eliminarRegSubSubdpolizasserie(Registro reg) {
        try {
            if (Boolean.parseBoolean(registro.getCampos()
                            .get(PolizasControladorEnum.ACTUALIZADA.getValue())
                            .toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3149"));
                return;
            }
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_POLIZAS_ACTIVOS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubdpolizasserie();
            generarTotalesSub();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionSubdpolizasserie() {
        cargarListaSubdpolizasserie();
    }

    public String generarFiltro(String campo, String valor) {
        String salida;
        salida = "false".equals(campo) ? "null" : valor;
        return salida;
    }

    public void oprimirGuardar() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        if (!validaciones()) {
            return;
        }
        String numPoliza = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(PolizasControladorEnum.NUMERO_POLIZA
                                                        .getValue()),
                                        "")
                        .toString();
        String grupo = registro.getCampos()
                        .get(PolizasControladorEnum.SEL_GRUPO.getValue())
                        .toString();
        String elemento = registro.getCampos()
                        .get(PolizasControladorEnum.SEL_ELEMENTO.getValue())
                        .toString();
        String placa = registro.getCampos()
                        .get(PolizasControladorEnum.SEL_SERIE.getValue())
                        .toString();
        Date fechaInicial = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAI.getValue());
        Date fechaFinal = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAF.getValue());
        String strPlaca = generarFiltro(placa, cbPlaca);
        if ("true".equalsIgnoreCase(placa)) {
            grupo = "true";
            elemento = "true";
        }
        String strElemento = generarFiltro(elemento, cbElemento);
        if ("true".equalsIgnoreCase(elemento)) {
            grupo = "true";
        }
        String strGrupo = generarFiltro(grupo, cbGrupo);
        int rta = 0;
        try {
            rta = Integer.parseInt(ejbAlmacenCero
                            .guardarPoliza(compania, SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get(PolizasControladorEnum.ASEGURADORA
                                                                            .getValue()),
                                                            "")
                                            .toString(), numPoliza,
                                            SysmanFunciones
                                                            .nvl(registro.getCampos()
                                                                            .get(GeneralParameterEnum.SUCURSAL
                                                                                            .getName()),
                                                                            "")
                                                            .toString(),
                                            fechaInicial,
                                            fechaFinal,
                                            strGrupo, strElemento, strPlaca,
                                            cpRiesgosSeleccion,
                                            SessionUtil.getUser().getCodigo())
                            .toString());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaSubdpolizasserie();
        if (rta == -1) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1989"));
            cargarRegistro();
        }
        else {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1990"));
        }
        //generarTotalesSub();
    }
    
    private void generarTotalesSub()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PolizasControladorEnum.ASEGURADORA.getValue(),
                    registro.getCampos()
                                    .get(PolizasControladorEnum.ASEGURADORA
                                                    .getValue()));
		    param.put(PolizasControladorEnum.NUMERO_POLIZA.getValue(),
		                    registro.getCampos()
		                                    .get(PolizasControladorEnum.NUMERO_POLIZA
		                                                    .getValue()));
            Registro regTotales = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                            		PolizasControladorUrlEnum.URL168001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));  
            if(regTotales != null)
            {
	            Double totalAmparos = Double.parseDouble(SysmanFunciones
	                    .nvl(regTotales.getCampos().get("TOTAL_D_POLIZAS"), "0").toString());
	            
	            Double totalPoliza =  Double.parseDouble(SysmanFunciones
	                    .nvl(registro.getCampos().get("VALOR"), "").toString());
	            
	            valorLiberado = (totalPoliza - totalAmparos);  
            }
            else
            {
            	valorLiberado = (double) 0;  
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }


    private boolean validaciones() {    	
    	if (!"false".equals(
                        registro.getCampos()
                                        .get(PolizasControladorEnum.ACTUALIZADA
                                                        .getValue())
                                        .toString())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1987"));
            return false;
        }
        else if (!validaFormularioPolizas()) {
            return false;
        }
        else if (SysmanFunciones.validarVariableVacio(cpRiesgosSeleccion)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1988"));
            return false;
        }  
        return true;
    }

    public void oprimirActualizarPoliza() {
        // <CODIGO_DESARROLLADO>
        if (validarActualizacionPoliza()) {
            agregarRegistroNuevo(false);
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirRevertirActualizacion() {
        // <CODIGO_DESARROLLADO>
    	/*if(validarRevertirPoliza())
    	{
    		agregarRegistroNuevo(true);
    	};*/
    	validarRevertirPoliza();
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Adjuntos
     * en la vista
     *
     *
     */
    public void oprimirAdjuntos() {
    	//<CODIGO_DESARROLLADO>
    	if (css != null) {
    		agregarRegistroNuevo(false);
    		String[] campos = { "aseguradora", "sucursal", "numeroPoliza", "poliza" };
    		String[] valores = { SysmanFunciones
    				.toString(registro.getCampos()
    						.get(PolizasControladorEnum.ASEGURADORA
    								.getValue())), SysmanFunciones
    				.toString(registro.getCampos()
    						.get(GeneralParameterEnum.SUCURSAL
    								.getName())), SysmanFunciones
    				.toString(registro.getCampos()
    						.get(PolizasControladorEnum.NUMERO_POLIZA
    								.getValue())), "true" };
    		SessionUtil.cargarModalDatosFlash(
    				String.valueOf(GeneralCodigoFormaEnum.DIGITALIZACION_CONTRATOS_CONTROLADOR
    						.getCodigo()),
    				modulo, campos,
    				valores);
    	}else {
    		JsfUtil.agregarMensajeError(idioma.getString("TB_TB4486"));
    	}
    	//</CODIGO_DESARROLLADO>
    }

    public void abrirRegistroActual(Registro reg, boolean nuevo) {
        if (nuevo) {
            bloqueaCKGrupo = false;
            bloqueaCKElemento = false;
            bloqueaCKSerie = false;
            visibleSubPolizas = false;
            visibleGrupoS = VISIBLENONE;
            visibleElementoS = VISIBLENONE;
            visiblePlacaS = VISIBLENONE;
            visibleRiesgosS = VISIBLENONE;
            bloqueaVigente = false;
        }
        else {

            if (Boolean.valueOf(reg.getCampos()
                            .get(PolizasControladorEnum.SEL_GRUPO.getValue())
                            .toString())) {
                bloqueaCKElemento = true;
                bloqueaCKSerie = true;
                visibleSubPolizas = true;
                visibleGrupoS = VISIBLEBLOCK;
                visibleElementoS = VISIBLENONE;
                visiblePlacaS = VISIBLENONE;
                visibleRiesgosS = VISIBLEBLOCK;
            }
            else if (Boolean.valueOf(
                            reg.getCampos().get(
                                            PolizasControladorEnum.SEL_ELEMENTO
                                                            .getValue())
                                            .toString())) {
                bloqueaCKGrupo = true;
                bloqueaCKSerie = true;
                visibleSubPolizas = true;
                visibleGrupoS = VISIBLEBLOCK;
                visibleElementoS = VISIBLEBLOCK;
                visiblePlacaS = VISIBLENONE;
                visibleRiesgosS = VISIBLEBLOCK;
            }
            else if (Boolean.valueOf(
                            reg.getCampos().get(PolizasControladorEnum.SEL_SERIE
                                            .getValue()).toString())) {
                bloqueaCKElemento = true;
                bloqueaCKGrupo = true;
                visibleSubPolizas = true;
                visibleGrupoS = VISIBLEBLOCK;
                visibleElementoS = VISIBLEBLOCK;
                visiblePlacaS = VISIBLEBLOCK;
                visibleRiesgosS = VISIBLEBLOCK;
            }
            else {
                bloqueaCKGrupo = false;
                bloqueaCKElemento = false;
                bloqueaCKSerie = false;
                visibleSubPolizas = false;
                visibleGrupoS = VISIBLENONE;
                visibleElementoS = VISIBLENONE;
                visiblePlacaS = VISIBLENONE;
                visibleRiesgosS = VISIBLENONE;
            }
        }
        if (!nuevo) {
            Date sysdate = new Date();
            if (((Date) reg.getCampos()
                            .get(PolizasControladorEnum.FECHAF.getValue()))
                                            .after(sysdate)
                && ((Date) reg.getCampos()
                                .get(PolizasControladorEnum.FECHAI.getValue()))
                                                .before(sysdate)) {
                reg.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                                true);
                cpVigenciaActual = idioma.getString(etqPol);
            }
            else {
                reg.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                                false);
                cpVigenciaActual = idioma.getString(etqPolDos);
            }
            estadoPoliza();
            bloquearPoliza(Boolean.valueOf(
                            reg.getCampos().get(
                                            PolizasControladorEnum.ACTUALIZADA
                                                            .getValue())
                                            .toString()));
        }     
    }

    public void bloquearPoliza(boolean vigenteOactualizada) {
        if (vigenteOactualizada) {
            bloqueaVigente = true;
            bloqueaCKGrupo = true;
            bloqueaCKElemento = true;
            bloqueaCKSerie = true;
        }
        else {
            bloqueaVigente = false;
            bloqueaCKGrupo = false;
            bloqueaCKElemento = false;
            bloqueaCKSerie = false;
        }
    }

    /**
     * �ste m�todo revisa si la p�liza est� vigente o no y seg�n eso,
     * bloquea o desbloquea los objetos.
     *
     */
    public void estadoPoliza() {
        if (Boolean.valueOf(registro.getCampos()
                        .get(PolizasControladorEnum.VIGENTE.getValue())
                        .toString())) {
            cpVigenciaActual = idioma.getString(etqPol);
            bloqueaVigente = true;
        }
        else {
            cpVigenciaActual = idioma.getString(etqPolDos);
            bloqueaVigente = false;
        }
    }

    public boolean validaFormularioPolizas() {
        String aseguradora = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(PolizasControladorEnum.ASEGURADORA
                                                        .getValue()),
                                        "")
                        .toString();
        String numPoliza = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(PolizasControladorEnum.NUMERO_POLIZA
                                                        .getValue()),
                                        "")
                        .toString();
        String valor = SysmanFunciones
                        .nvl(registro.getCampos().get("VALOR"), "").toString();
        String descripcion = SysmanFunciones
                        .nvl(registro.getCampos().get("DESCRIPCION"), "")
                        .toString();
        Date fechaAdq = (Date) registro.getCampos().get("FECHAADQUISICION");
        Date fechaInicial = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAI.getValue());
        Date fechaFinal = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAF.getValue());
        boolean rta = true;
        if (SysmanFunciones.validarVariableVacio(aseguradora)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1979"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(numPoliza)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1980"));
            rta = false;
        }
        if (fechaAdq == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1981"));
            rta = false;
        }
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1982"));
            rta = false;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1983"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1984"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(descripcion)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1985"));
            rta = false;
        }
        return rta;
    }

    public boolean validarActualizacionPoliza() {
        if (!validaFormularioPolizas()) {
            return false;
        }
        Date fechaInicial = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAI.getValue());
        Date fechaFinal = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAF.getValue());
        Date sysdate = new Date();
        if (fechaInicial.before(sysdate) && fechaFinal.after(sysdate)) {
            registro.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                            true);
            cpVigenciaActual = idioma.getString(etqPol);
            bloqueaVigente = true;
            bloqueaCKGrupo = true;
            bloqueaCKElemento = true;
            bloqueaCKSerie = true;
            registro.getCampos().put(
                            PolizasControladorEnum.ACTUALIZADA.getValue(),
                            true);
        }
        else {
            registro.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                            false);
            cpVigenciaActual = idioma.getString(etqPolDos);
            bloqueaVigente = true;
            bloqueaCKGrupo = true;
            bloqueaCKElemento = true;
            bloqueaCKSerie = true;
            registro.getCampos().put(
                            PolizasControladorEnum.ACTUALIZADA.getValue(),
                            true);
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1986"));
        return true;
    }
    
    public boolean validarRevertirPoliza() {
        if (!validaFormularioPolizas()) {
            return false;
        }
        Date fechaInicial = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAI.getValue());
        Date fechaFinal = (Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAF.getValue());
        Date sysdate = new Date();
        if (fechaInicial.before(sysdate) && fechaFinal.after(sysdate)) {
            registro.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                            true);
            cpVigenciaActual = idioma.getString(etqPol);
            bloqueaVigente = false;
            bloqueaCKGrupo = false;
            bloqueaCKElemento = false;
            bloqueaCKSerie = false;
            registro.getCampos().put(
                            PolizasControladorEnum.ACTUALIZADA.getValue(),
                            false);
        }
        else {
            registro.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                            false);
            cpVigenciaActual = idioma.getString(etqPolDos);
            bloqueaVigente = false;
            bloqueaCKGrupo = false;
            bloqueaCKElemento = false;
            bloqueaCKSerie = false;
            registro.getCampos().put(
                            PolizasControladorEnum.ACTUALIZADA.getValue(),
                            false);
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1986"));
        return true;
    }

    @Override
    public void abrirFormulario() {
    	try {
			manejaControlValor = "SI".equals(SysmanFunciones
						.nvl(ejbSysmanUtil.consultarParametro(compania, "CONTROLA POLIZAS DE DEVOLUTIVOS POR VALOR",
								SessionUtil.getModulo(), new Date(), true), "NO"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
        generarTotalesSub();
        if (css != null) {
            abrirRegistroActual(registro, false);
        }
        else {
            listaSubdpolizasserie = null;
            abrirRegistroActual(registro, true);
        }
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        if (((Date) registro.getCampos()
                        .get(PolizasControladorEnum.FECHAI.getValue()))
                                        .after((Date) registro.getCampos()
                                                        .get(PolizasControladorEnum.FECHAF
                                                                        .getValue()))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB528"));
            return false;
        }
        long consecutivo = 0;
        try {
            consecutivo = Long.parseLong(ejbAlmacenCero
                            .generarConsecutivoPolizas("POLIZAS_ACTIVOS",
                                            " COMPANIA = ''" + compania
                                                + "''",
                                            GeneralParameterEnum.CONSECUTIVO
                                                            .getName(),
                                            "1"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        return true;
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubdpolizasserie = null;
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubdpolizasserie();
    }

    @Override
    public void iniciarListas() {
        cargarListaaseguradora();
        cargarListaGrupos();
        cargarListaElementosSeleccion();
        cargarListaPlacaSeleccion();
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(PolizasControladorEnum.VIGENTE.getValue(),
                        false);
        registro.getCampos().remove(
                        PolizasControladorEnum.NOMBREASEGURADORA
                                        .getValue());
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("NUMERO_POLIZA");
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos().remove("ASEGURADORA");
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if (Boolean.parseBoolean(registro.getCampos()
                        .get(PolizasControladorEnum.ACTUALIZADA.getValue())
                        .toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3149"));
            return false;
        }
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public RegistroDataModelImpl getListaAseguradora() {
        return listaAseguradora;
    }

    public void setListaAseguradora(RegistroDataModelImpl listaAseguradora) {
        this.listaAseguradora = listaAseguradora;
    }

    public RegistroDataModelImpl getListaPlacaSeleccion() {
        return listaPlacaSeleccion;
    }

    public void setListaPlacaSeleccion(
        RegistroDataModelImpl listaPlacaSeleccion) {
        this.listaPlacaSeleccion = listaPlacaSeleccion;
    }

    public RegistroDataModelImpl getListaElementosSeleccion() {
        return listaElementosSeleccion;
    }

    public void setListaElementosSeleccion(
        RegistroDataModelImpl listaElementosSeleccion) {
        this.listaElementosSeleccion = listaElementosSeleccion;
    }

    public RegistroDataModelImpl getListaGrupos() {
        return listaGrupos;
    }

    public void setListaGrupos(RegistroDataModelImpl listaGrupos) {
        this.listaGrupos = listaGrupos;
    }

    public RegistroDataModelImpl getListaSubdpolizasserie() {
        return listaSubdpolizasserie;
    }

    public void setListaSubdpolizasserie(
        RegistroDataModelImpl listaSubdpolizasserie) {
        this.listaSubdpolizasserie = listaSubdpolizasserie;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getCbPlaca() {
        return cbPlaca;
    }

    public void setCbPlaca(String cbPlaca) {
        this.cbPlaca = cbPlaca;
    }

    public String getCbElemento() {
        return cbElemento;
    }

    public void setCbElemento(String cbElemento) {
        this.cbElemento = cbElemento;
    }

    public String getCbGrupo() {
        return cbGrupo;
    }

    public void setCbGrupo(String cbGrupo) {
        this.cbGrupo = cbGrupo;
    }

    public boolean isBloqueaVigente() {
        return bloqueaVigente;
    }

    public void setBloqueaVigente(boolean bloqueaVigente) {
        this.bloqueaVigente = bloqueaVigente;
    }

    public String getCpVigenciaActual() {
        return cpVigenciaActual;
    }

    public void setCpVigenciaActual(String cpVigenciaActual) {
        this.cpVigenciaActual = cpVigenciaActual;
    }

    public String getVisiblePlacaS() {
        return visiblePlacaS;
    }

    public void setVisiblePlacaS(String visiblePlacaS) {
        this.visiblePlacaS = visiblePlacaS;
    }

    public String getVisibleElementoS() {
        return visibleElementoS;
    }

    public void setVisibleElementoS(String visibleElementoS) {
        this.visibleElementoS = visibleElementoS;
    }

    public String getVisibleGrupoS() {
        return visibleGrupoS;
    }

    public void setVisibleGrupoS(String visibleGrupoS) {
        this.visibleGrupoS = visibleGrupoS;
    }

    public String getNombreAseguradora() {
        return nombreAseguradora;
    }

    public void setNombreAseguradora(String nombreAseguradora) {
        this.nombreAseguradora = nombreAseguradora;
    }

    public boolean isBloqueaCKGrupo() {
        return bloqueaCKGrupo;
    }

    public void setBloqueaCKGrupo(boolean bloqueaCKGrupo) {
        this.bloqueaCKGrupo = bloqueaCKGrupo;
    }

    public boolean isBloqueaCKElemento() {
        return bloqueaCKElemento;
    }

    public void setBloqueaCKElemento(boolean bloqueaCKElemento) {
        this.bloqueaCKElemento = bloqueaCKElemento;
    }

    public boolean isBloqueaCKSerie() {
        return bloqueaCKSerie;
    }

    public void setBloqueaCKSerie(boolean bloqueaCKSerie) {
        this.bloqueaCKSerie = bloqueaCKSerie;
    }

    public String getCpRiesgosSeleccion() {
        return cpRiesgosSeleccion;
    }

    public void setCpRiesgosSeleccion(String cpRiesgosSeleccion) {
        this.cpRiesgosSeleccion = cpRiesgosSeleccion;
    }

    public String getVisibleRiesgosS() {
        return visibleRiesgosS;
    }

    public void setVisibleRiesgosS(String visibleRiesgosS) {
        this.visibleRiesgosS = visibleRiesgosS;
    }

    public boolean getVisibleSubPolizas() {
        return visibleSubPolizas;
    }

    public void setVisibleSubPolizas(boolean visibleSubPolizas) {
        this.visibleSubPolizas = visibleSubPolizas;
    }

	public Double getCbValorPlaca() {
		return cbValorPlaca;
	}

	public void setCbValorPlaca(Double cbValorPlaca) {
		this.cbValorPlaca = cbValorPlaca;
	}

	public Double getValorLiberado() {
		return valorLiberado;
	}

	public void setValorLiberado(Double valorLiberado) {
		this.valorLiberado = valorLiberado;
	}

	public boolean isManejaControlValor() {
		return manejaControlValor;
	}

	public void setManejaControlValor(boolean manejaControlValor) {
		this.manejaControlValor = manejaControlValor;
	}
}
