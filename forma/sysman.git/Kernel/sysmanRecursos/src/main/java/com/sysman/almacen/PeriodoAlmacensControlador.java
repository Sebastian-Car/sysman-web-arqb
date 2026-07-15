package com.sysman.almacen;

import com.sysman.almacen.enums.PeriodoAlmacensControladorEnum;
import com.sysman.almacen.enums.PeriodoAlmacensControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * @version 1, 27/01/2016
 *
 * @author eamaya
 * @version 2, 26/04/2017 Proceso de Refactoring, Correcciones Sonar
 * Lint,Manejo de EJBs
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 *
 */
@ManagedBean
@ViewScoped

public class PeriodoAlmacensControlador extends BeanBaseModal {

    private String compania;
    private String modulo;
    private String menuActual;
    private String tipoMov;
    private String nomMov;
    private String claseMov;
    private String tipoElementoMov;
    private String claseDocAsoc;
    private String cptoMov;
    private String claseDocAsMov;
    private String ano;
    private String mes;
    private String claseMovimiento;
    private String tipoElemento;
    private String tituloForm;
    private String tipoPersona;
    private String nroInicial;
    private String claseBodOrig;
    private String claseBodDest;
    private Date fechaCorte;
    private boolean inventarioInicial;
    private boolean manVtas;
    private boolean generaPlaca;
    private boolean pideCCto;
    private boolean obligaCampos;
    private boolean bloqueaAux;
    private boolean contratoManual;
	private List<Registro> listaAno;
    private List<Registro> listaMes;
    private RegistroDataModelImpl listaTipoMovimiento;
    private Map<String,Object> parametroswf;
    

    @EJB

    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of PeriodoAlmacensControlador
     */
    @SuppressWarnings("unchecked")
	public PeriodoAlmacensControlador() {
        super();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "10");
			}
            numFormulario = GeneralCodigoFormaEnum.PERIODO_ALMACENS_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            if(parametroswf != null) {
            	menuActual = SysmanFunciones.nvl(parametroswf.get("menu"),"").toString();
        	} else {
        		menuActual = SessionUtil.getMenuActual();
        	}
            switch (menuActual) {
            case "10022701":
                claseMovimiento = "E";
                tipoElemento = "C";
                break;
            case "10022702":
                claseMovimiento = "E";
                tipoElemento = "D,E";
                break;
            case "10022801":
                claseMovimiento = "S";
                tipoElemento = "C";
                break;
            case "10022802":
                claseMovimiento = "S";
                tipoElemento = "E,D";
                break;
            case "100208":
                claseMovimiento = null;
                tipoElemento = null;
                break;
            default:
                break;
            }
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoAlmacensControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        cargarListaTipoMovimiento();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMes();
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoAlmacensControladorUrlEnum.URL4578
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoAlmacensControladorUrlEnum.URL5118
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipoMovimiento() {
        String auxiliar;

        if (tipoElemento == null) {
            auxiliar = "-1";
        }
        else {
            auxiliar = "0";
        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PeriodoAlmacensControladorUrlEnum.URL6969
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(PeriodoAlmacensControladorEnum.PARAM0.getValue(),
                        claseMovimiento);

        param.put(PeriodoAlmacensControladorEnum.PARAM1.getValue(),
                        tipoElemento);

        param.put(GeneralParameterEnum.AUXILIAR.getName(),
                        auxiliar);
        
        param.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());
        param.put(GeneralParameterEnum.MODULO.getName(), SessionUtil.getModulo());
        
        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        String parGeneraCpteCtble;
        try {
            parGeneraCpteCtble = ejbParametro.consultarParametro(
                            compania,
                            "GENERA COMPROBANTE CONTABLE DESDE ALMACEN", modulo,
                            new Date(), false);

            if ("SI".equals(parGeneraCpteCtble)) {

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);

                Registro aux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PeriodoAlmacensControladorUrlEnum.URL5952
                                                                                .getValue())
                                                .getUrl(), param));

                if ("0".equals(aux.getCampos().get("CUENTA").toString())) {

                    HashMap<String, Object> parSet = new HashMap<>();

                    parSet.put(
                                    GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    parSet.put(GeneralParameterEnum.CODIGO.getName(),
                                    tipoMov);

                    parSet.put(GeneralParameterEnum.NOMBRE.getName(),
                                    nomMov);

                    parSet.put("PIDE_RETENCION", "-1");

                    parSet.put("CLASE_CONTABLE", "P");

                    parSet.put("FORMATO", "INT");

                    parSet.put("OBLIGA_AFECT_PPTAL", "0");

                    parSet.put(GeneralParameterEnum.CREATED_BY
                                    .getName(),
                                    SessionUtil.getUser()
                                                    .getCodigo());

                    parSet.put(
                                    GeneralParameterEnum.DATE_CREATED.getName(),
                                    new Date());

                    Parameter parametro = new Parameter();
                    parametro.setFields(parSet);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    GenericUrlEnum.TIPO_COMPROBANTE
                                                                    .getCreateKey());
                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(),
                                    parametro);

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2006"));

                }
            }

            asignarFechaCorte();

            Map<String, Object> parametro = new TreeMap<>();

            parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametro.put(PeriodoAlmacensControladorEnum.PARAM3.getValue(),
                            claseMov);
            parametro.put(PeriodoAlmacensControladorEnum.PARAM4.getValue(),
                            cptoMov);
            parametro.put(PeriodoAlmacensControladorEnum.PARAM5.getValue(),
                            claseBodOrig);
            parametro.put(PeriodoAlmacensControladorEnum.PARAM6.getValue(),
                            claseBodDest);

            Registro auxValidacion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoAlmacensControladorUrlEnum.URL8999
                                                                            .getValue())
                                            .getUrl(), parametro));

            boolean obligaOrigen = (boolean) auxValidacion.getCampos()
                            .get("OBLIGA_ORIGEN");
            boolean obligaDestino = (boolean) auxValidacion.getCampos()
                            .get("OBLIGA_DESTINO");
            boolean obligaTercero = (boolean) auxValidacion.getCampos()
                            .get("OBLIGA_TERCERO");
            boolean obligaProveedor = (boolean) auxValidacion.getCampos()
                            .get("OBLIGA_PROVEEDOR");
            boolean igualarOrigDest = (boolean) auxValidacion.getCampos()
                            .get("IGUALA_ORIGENDESTINO");

            Map<String, Object> parametros = new HashMap<>();
            // parametros.put("estadoAlmacen", estadoAlmacen);
            parametros.put("anoMov", ano);
            parametros.put("mesMov", mes);
            parametros.put("tipoMov", tipoMov);
            parametros.put("fechaCorte", fechaCorte);
            parametros.put("claseDocAsoc", claseDocAsoc);
            parametros.put("inventarioInicial", inventarioInicial);
            parametros.put("claseMov", claseMov);
            parametros.put("tipoElementoMov", tipoElementoMov);
            parametros.put("tituloForm", tituloForm);
            parametros.put("cptoMov", cptoMov);
            parametros.put("manVtas", manVtas);
            parametros.put("claseDocAsMov", claseDocAsMov);
            parametros.put("generaPlaca", generaPlaca);
            parametros.put("tipoPersona", tipoPersona);
            parametros.put("pideCCto", pideCCto);
            parametros.put("nroInicial", nroInicial);
            parametros.put("claseBodOrig", claseBodOrig);
            parametros.put("claseBodDest", claseBodDest);
            parametros.put("obligaOrigen", obligaOrigen);
            parametros.put("obligaDestino", obligaDestino);
            parametros.put("obligaTercero", obligaTercero);
            parametros.put("obligaProveedor", obligaProveedor);
            parametros.put("igualarOrigDest", igualarOrigDest);
            parametros.put("obligaCampos", obligaCampos);
            parametros.put("bloqueaAux", bloqueaAux);
            parametros.put("contratoManual", contratoManual);


            if(parametroswf != null) {
            	String[] campos = {
            			"anoMov", "mesMov", "tipoMov", "fechaCorte", "claseDocAsoc",
            			"inventarioInicial", "claseMov", "tipoElementoMov", "tituloForm",
            			"cptoMov", "manVtas", "claseDocAsMov", "generaPlaca", "tipoPersona",
            			"pideCCto", "nroInicial", "claseBodOrig", "claseBodDest", "obligaOrigen",
            			"obligaDestino", "obligaTercero", "obligaProveedor", "igualarOrigDest",
            			"obligaCampos", "bloqueaAux","contratoManual"
            	};

            	Object[] valores = {
            			ano, mes, tipoMov, fechaCorte, claseDocAsoc,
            			inventarioInicial, claseMov, tipoElementoMov, tituloForm,
            			cptoMov, manVtas, claseDocAsMov, generaPlaca, tipoPersona,
            			pideCCto, nroInicial, claseBodOrig, claseBodDest, obligaOrigen,
            			obligaDestino, obligaTercero, obligaProveedor, igualarOrigDest,
            			obligaCampos, bloqueaAux,contratoManual
            	};
            	SessionUtil.redireccionarFormularioModalFormulario(modulo, "469", campos, valores, true);
            }else {            

            	Direccionador direccionador = new Direccionador();
            	direccionador.setNumForm("469");
            	direccionador.setParametros(parametros);
            	RequestContext.getCurrentInstance().closeDialog(direccionador);

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private void asignarFechaCorte() {
        // Esta l�nea se agrego el 7 de febrero del 2000 - eviar
        // que modifiquen los inventarios iniciales

        try {
            if ("M".equals(claseDocAsoc)) {
                if (inventarioInicial) {
                    String parFechaCorte;

                    parFechaCorte = ejbParametro.consultarParametro(compania,
                                    "FECHA DE CORTE PARA INICIO DEL ALMACEN",
                                    modulo, new Date(), false);

                    fechaCorte = SysmanFunciones.convertirAFecha(parFechaCorte,
                                    "DD/MM/YYYY");

                    if (("E".equals(claseMov))
                        && ("C".equals(tipoElementoMov))) {
                        fechaCorte = SysmanFunciones
                                        .sumarRestarDiasFecha(fechaCorte, -1);
                    }
                }

            }
            else {
                fechaCorte = new Date();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        tipoMov = null;
        cargarListaMes();

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoMov = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        nomMov = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBREMOV"), "")
                        .toString();
        claseDocAsoc = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CLASEDOCASOCIADO"),
                                        "")
                        .toString();
        inventarioInicial = (boolean) registroAux.getCampos()
                        .get("INVENTARIOINICIAL");
        claseMov = SysmanFunciones.nvl(registroAux.getCampos().get("CLASE"), "")
                        .toString();
        tipoElementoMov = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPOELEMENTO"), "")
                        .toString();
        tituloForm = tipoMov + " - " + nomMov;
        cptoMov = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CONCEPTO"), "")
                        .toString();
        manVtas = (boolean) registroAux.getCampos().get("MANEJA_VENTAS");
        claseDocAsMov = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CLASEDOCASOCIADO"), "").toString();

        generaPlaca = (boolean) registroAux.getCampos().get("GENERAPLACA");
        tipoPersona = (String) registroAux.getCampos().get("TIPOPESONA");
        pideCCto = (boolean) registroAux.getCampos().get("PIDECENTROCOSTO");

        obligaCampos = (boolean) registroAux.getCampos().get("OBLIGA_CAMPOS");
        
        bloqueaAux = (boolean) registroAux.getCampos().get("CAMBIA_AUX");

        nroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMEROINICIAL"), "")
                        .toString();

        claseBodOrig = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CLASE_BODEGA_ORIGEN"), "").toString();

        claseBodDest = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CLASE_BODEGA_DESTINO"), "").toString();
        
        contratoManual = (boolean) registroAux.getCampos().get("CONTRATO_MANUAL");

    }

    public String getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(String tipoMov) {
        this.tipoMov = tipoMov;
    }

    public String getNomMov() {
        return nomMov;
    }

    public void setNomMov(String nomMov) {
        this.nomMov = nomMov;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public RegistroDataModelImpl getListaTipoMovimiento() {
        return listaTipoMovimiento;
    }

    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento) {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }
    
    public boolean isContratoManual() {
		return contratoManual;
	}

	public void setContratoManual(boolean contratoManual) {
		this.contratoManual = contratoManual;
	}

}
