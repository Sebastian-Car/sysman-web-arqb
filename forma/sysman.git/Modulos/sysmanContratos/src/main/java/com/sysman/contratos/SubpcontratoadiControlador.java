package com.sysman.contratos;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.ejb.EjbContratosDosRemote;
import com.sysman.contratos.enums.SubpcontratoadiControladorEnum;
import com.sysman.contratos.enums.SubpcontratoadiControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 19/02/2016
 * 
 * @version 2, 01/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 * 
 * @author ybecerra
 * @version 3, 23/10/2017, se quitaron parametros de entrada, debido a
 * que eran valores de parametros, que se deben validar directamente
 * en este controlador
 */

@ManagedBean
@ViewScoped
public class SubpcontratoadiControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingreso en la aplicacion
     */
    private String modulo;
    private final String cOrdenSuministro;
    private final String cPorciva;
    private final String cValorTotal;
    private final String cValorUnitario;
    private final String cValorUnitariODI;
    private final String cVlrTotal;
    private final String cAnoPpto;
    private final String cCantidad;
    private final String cClaseOrden;
    private final String cCodigo;
    private final String cCuenta;
    private final String cItemOrigen;
    private final String cNumeroPpto;
    private final String cOrdenDeCompra;
    private final String consDependencia;

    private RegistroDataModelImpl listaCB1024;
    private List<Registro> listaCB1593;
    private RegistroDataModelImpl listaELEMENTO;
    private RegistroDataModelImpl listaCB1595;
    private RegistroDataModelImpl listaCB1594;
    private String nombreElemento;

    private String claseOrden;
    private String anio;
    private String vigencia;
    private String nombreContrato;
    private String numeroOrden;
    private String numeroAfectado;
    private String tipoAfectado;
    private HashMap<String, Object> rid2;

    private boolean bloqueadoElemento;
    private String maneja;
    private String dependencia;
    private String vTxtDigitosRedondeoValorIVA;
    private boolean subPContratoPermiteAgregar;
    private boolean subPContratoPermiteEditar;
    private boolean subPContratoPermiteEliminar;

    private Double cantidadAnterior;
    private Double valorUnitarioAnt;
    private Double valorTotalAnterior;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContratosDosRemote ejbContratosDos;
	private Map<String, Object> parametroswf;

    public SubpcontratoadiControlador() {
        super();
        compania = SessionUtil.getCompania();
        cOrdenSuministro = "ORDENDESUMINISTRO";
        cPorciva = "PORCIVA";
        cValorTotal = "VALORTOTAL";
        cValorUnitario = "VALORUNITARIO";
        cValorUnitariODI = "VALORUNITARIODI";
        cVlrTotal = "VLRTOTAL";
        cAnoPpto = "ANOPPTO";
        cCantidad = "CANTIDAD";
        cClaseOrden = "CLASEORDEN";
        cCodigo = "CODIGO";
        cCuenta = "CUENTA";
        cItemOrigen = "ITEMORIGEN";
        cNumeroPpto = "NUMEROPPTO";
        cOrdenDeCompra = "ORDENDECOMPRA";
        consDependencia = "DEPENDENCIA";

        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
        	numFormulario = GeneralCodigoFormaEnum.SUBPCONTRATOADI_CONTROLADOR
        			.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid2 = (HashMap<String, Object>) parametrosEntrada.get("rid");
                claseOrden = (String) parametrosEntrada.get("tipoContrato");
                numeroOrden = (String) parametrosEntrada.get("numeroOrden");
                anio = (String) parametrosEntrada.get("anio");
                vigencia = (String) parametrosEntrada.get("vigencia");
                nombreContrato = (String) parametrosEntrada
                                .get("nombreContrato");
                numeroAfectado = (String) parametrosEntrada
                                .get("numeroAfectado");
                tipoAfectado = (String) parametrosEntrada.get("tipoAfectado");
                dependencia = (String) parametrosEntrada.get("dependencia");
                maneja = (String) parametrosEntrada.get("maneja");
            }
            registro = new Registro(new HashMap<String, Object>());
            tabla = SubpcontratoadiControladorEnum.TABLA.getValue();
        }
        catch (SysmanException | NamingException ex) {
            Logger.getLogger(SubpcontratoadiControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaCB1023();
        cargarListaCB1595();
        cargarListaCB1594();
        cargarListaCB1024();
    }

    @Override
    public void iniciarListasSub() {
        bloqueadoElemento = true;
    }

    @Override
    public void iniciarListasSubNulo() {
        // METODO_NO_IMPLEMENTADO
    }

    @PostConstruct
    public void inicializar() {
        tabla = SubpcontratoadiControladorEnum.TABLA.getValue();
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubpcontratoadiControladorUrlEnum.URL15084.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL91505
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL54786
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL98566
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL72648
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(SubpcontratoadiControladorEnum.PARAM12.getValue(),
                        claseOrden);
        parametrosListado.put(SubpcontratoadiControladorEnum.PARAM13.getValue(),
                        numeroOrden);
    }

    public void cambiarCB1593() {
        if (registro.getCampos().get(cNumeroPpto) != null) {
            registro.getCampos().put(cAnoPpto, service.buscarEnLista(
                            registro.getCampos().get(cNumeroPpto).toString(),
                            cNumeroPpto, cAnoPpto, listaCB1593));
        }
    }

    public void cargarListaCB1024() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL10595
                                                        .getValue());
        listaCB1024 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCB1593() {
        // NUMEROPPTO
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(SubpcontratoadiControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get("TIPOPPTO"));
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            param.put(GeneralParameterEnum.NUMERO.getName(), numeroOrden);
            param.put(SubpcontratoadiControladorEnum.PARAM1.getValue(),
                            tipoAfectado);
            param.put(SubpcontratoadiControladorEnum.PARAM2.getValue(),
                            numeroAfectado);

            listaCB1593 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratoadiControladorUrlEnum.URL10542
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCB1023() {
        // ELEMENTO
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL11804
                                                        .getValue());
        if ("1".equals(maneja)) { // maneja 1
            param.put(SubpcontratoadiControladorEnum.PARAM3.getValue(), "S");
        }
        else if ("2".equals(maneja)) { // maneja 2
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            SubpcontratoadiControladorUrlEnum.URL11810
                                            .getValue());
            param.put(SubpcontratoadiControladorEnum.PARAM3.getValue(), "S");
        }
        else {
            param.put(SubpcontratoadiControladorEnum.PARAM3.getValue(), " ");
        }

        listaELEMENTO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }

    public void cargarListaCB1595() {
        // TIPOPPTO
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL12742
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCB1595 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCB1594() {
        // RUBRO
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL13391
                                                        .getValue());
        listaCB1594 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCuenta);
    }

    public void seleccionarFilaELEMENTO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ELEMENTO",
                        registroAux.getCampos().get("CODIGOELEMENTO"));
        nombreElemento = registroAux.getCampos().get("NOMBRELARGO").toString();
    }

    public void seleccionarFilaCB1595(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOPPTO",
                        registroAux.getCampos().get(cCodigo));
        cargarListaCB1593();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCB1024
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCB1024(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consDependencia,
                        registroAux.getCampos().get(cCodigo));
    }

    public void seleccionarFilaCB1594(SelectEvent event) {
        // RUBRO
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBROPPTO",
                        registroAux.getCampos().get(cCuenta));
        if (!"".equals(registroAux.getCampos().get(cCuenta))) {
            registro.getCampos().put("CONSECUTIVOPPTO", registroAux.getCampos()
                            .get("CONSECUTIVO").toString());
            registro.getCampos().put("VALORPPTO",
                            Double.parseDouble(registroAux.getCampos()
                                            .get("VALORPPTON").toString()));
            if (registro.getCampos().get(cNumeroPpto) != null
                && !"".equals(registro.getCampos().get(cNumeroPpto))) {
                registro.getCampos().put(cAnoPpto,
                                service.buscarEnLista(registro.getCampos()
                                                .get(cNumeroPpto).toString(),
                                                cNumeroPpto,
                                                cAnoPpto, listaCB1593));
            }
        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        registro.getCampos().get("ELEMENTO"));
        if (css != null) {
            bloqueadoElemento = true;
            try {
                nombreElemento = listaELEMENTO.getRegistroUnico(param)
                                .getCampos().get("NOMBRELARGO").toString();
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            cargarListaCB1593();
        }
        else {
            bloqueadoElemento = false;
            nombreElemento = null;
            registro.getCampos().put(cPorciva, 0);
            registro.getCampos().put("PORCDESC", 0);
        }
        valorTotalAnterior = registro.getCampos().get(cVlrTotal) == null ? 0.0
            : Double.parseDouble(
                            registro.getCampos().get(cVlrTotal).toString());
        cantidadAnterior = registro.getCampos().get(cCantidad) == null ? 0.0
            : Double.parseDouble(
                            registro.getCampos().get(cCantidad).toString());
        valorUnitarioAnt = registro.getCampos().get(cValorUnitario) == null
            ? 0.0
            : Double.parseDouble(registro.getCampos().get(cValorUnitario)
                            .toString());

        registro.getCampos().put(cOrdenDeCompra, numeroOrden);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        try {
            registro.getCampos().put("COMPANIA", compania);
            registro.getCampos().put(cClaseOrden, claseOrden);

            StringBuilder condicion = new StringBuilder();
            StringBuilder condicion2 = new StringBuilder();
            condicion.append("COMPANIA = ''").append(compania)
                            .append("'' AND CLASEORDEN = ''").append(claseOrden)
                            .append("'' AND CANTIDAD > 0 AND ORDENDECOMPRA = ")
                            .append(numeroOrden);
            
            condicion2.append("COMPANIA = ''").append(compania)
            .append("'' AND CLASEORDEN = ''").append(tipoAfectado)
            .append("'' AND CANTIDAD > 0 AND ORDENDECOMPRA = ")
            .append(numeroAfectado);

            Long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "D_ORDENDECOMPRA", condicion.toString(), cCodigo);
            
            Long consecutivo2 = ejbSysmanUtil.generarSiguienteConsecutivo(
                    "D_ORDENDECOMPRA", condicion2.toString(), cCodigo);
            
            if(consecutivo2 > consecutivo) {
            	consecutivo = consecutivo2;
            }
            
            registro.getCampos().put(cCodigo, consecutivo);
       
        }catch (SystemException ex) {
            Logger.getLogger(SubpcontratoadiControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
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
        try {
            BigDecimal valorTotal = ejbContratosDos.actualizarPSubcontrato(
                            compania,
                            Long.parseLong("".equals(registro.getCampos()
                                            .get(cOrdenSuministro).toString())
                                                ? "0"
                                                : registro.getCampos()
                                                                .get(cOrdenSuministro)
                                                                .toString()),
                            Long.parseLong(SysmanFunciones
                                            .nvl(registro.getCampos().get(
                                                            cItemOrigen), "-1")
                                            .toString()),
                            cantidadAnterior.longValue(),
                            registro.getCampos().get(cCantidad) == null ? 0
                                : Long.parseLong(registro.getCampos()
                                                .get(cCantidad).toString()),
                            Long.parseLong(registro.getCampos()
                                            .get(cOrdenDeCompra).toString()),
                            Long.parseLong(registro.getCampos().get(cCodigo)
                                            .toString()),
                            tipoAfectado,
                            Long.parseLong(numeroAfectado),
                            registro.getCampos().get(cClaseOrden).toString(),
                            claseOrden,
                            Long.parseLong(numeroOrden),
                            SessionUtil.getUser().getCodigo());

            SessionUtil.setSessionVar(cValorTotal, valorTotal);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            registro.getLlave()
                                            .get(SubpcontratoadiControladorEnum.PARAM9
                                                            .getValue()));
            param.put(SubpcontratoadiControladorEnum.PARAM4.getValue(),
                            registro.getLlave()
                                            .get(SubpcontratoadiControladorEnum.PARAM10
                                                            .getValue()));
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getLlave()
                                            .get(SubpcontratoadiControladorEnum.PARAM11
                                                            .getValue()));

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratoadiControladorUrlEnum.URL25327
                                                                            .getValue())
                                            .getUrl(), param));

            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoAfectado);
            param2.put(SubpcontratoadiControladorEnum.PARAM4.getValue(),
                            numeroAfectado);
            param2.put(GeneralParameterEnum.CODIGO.getName(),
                            rs.getCampos().get(cCodigo));
            param2.put(SubpcontratoadiControladorEnum.PARAM5.getValue(),
                            rs.getCampos().get(cOrdenDeCompra));
            param2.put(SubpcontratoadiControladorEnum.PARAM6.getValue(),
                            rs.getCampos().get(cClaseOrden));
            param2.put(SubpcontratoadiControladorEnum.PARAM7.getValue(),
                            rs.getCampos().get(cCodigo));

            Registro rs1 = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratoadiControladorUrlEnum.URL29171
                                                                            .getValue())
                                            .getUrl(), param2));

            if ("0".equals(rs1.getCampos().get("REGISTROS"))) {

                if (!"0".equals(rs.getCampos().get(cCantidad).toString())) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2123")
                                    .replace("$#codigo$#",
                                                    (String) rs.getCampos()
                                                                    .get(cCodigo))
                                    .replace("$#cantidad$#",
                                                    (String) rs.getCampos()
                                                                    .get(cCantidad)));
                    return false;
                }
            }
            else {
            	
            	//SE ELIMINA DEL CONTRATO
                UrlBean urlDelete2 = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratoadiControladorUrlEnum.URL11342
                                                        .getValue());
                
                HashMap<String, Object> param3 = new HashMap<>();
                
                param3.put(SubpcontratoadiControladorEnum.PARAM8
                                .getValue(), compania);
                param3.put(SubpcontratoadiControladorEnum.PARAM9
                                .getValue(), tipoAfectado); 
                param3.put(SubpcontratoadiControladorEnum.PARAM10
                                .getValue(), numeroAfectado);   
                param3.put(SubpcontratoadiControladorEnum.PARAM11
                                .getValue(),
                                rs.getCampos().get(cCodigo).toString());
                param3.put(SubpcontratoadiControladorEnum.PARAM6
                        .getValue(),registro.getLlave()
                        .get(SubpcontratoadiControladorEnum.PARAM9
                                .getValue()));
                
                requestManager.delete(urlDelete2.getUrl(), param3);
               
                //SE ELIMINA DE LA ADICION 
                HashMap<String, Object> llaveDorden = new HashMap<>();
                 
                llaveDorden.put(SubpcontratoadiControladorEnum.PARAM8
                                .getValue(), compania);
                llaveDorden.put(SubpcontratoadiControladorEnum.PARAM9
                                .getValue(), registro.getLlave()
                                .get(SubpcontratoadiControladorEnum.PARAM9
                                        .getValue())); // MOD JM 21/10/2024 tipoAfectado
                llaveDorden.put(SubpcontratoadiControladorEnum.PARAM10
                                .getValue(), registro.getLlave()
                                .get(SubpcontratoadiControladorEnum.PARAM10
                                        .getValue()));  // MOD JM 21/10/2024 numeroAfectado
                llaveDorden.put(SubpcontratoadiControladorEnum.PARAM11
                                .getValue(),
                                rs.getCampos().get(cCodigo).toString());

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubpcontratoadiControladorUrlEnum.URL39635
                                                                .getValue());
            
                requestManager.delete(urlDelete.getUrl(), llaveDorden);
                
            }
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String getNombreElemento() {
        return nombreElemento;
    }

    public void setNombreElemento(String nombreElemento) {
        this.nombreElemento = nombreElemento;
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

    public void cambiarCP8244() {
        calcularTotalADI();

    }

    public void cambiarCP16044() {
        calcularTotalADI();

    }

    public void cambiarCP8243() {
        // MODIFICAR VLR UNITARIO
        calcularTotalADI();

    }

    public void ejecutarantesCambioValorUni() {
        // comando remoto
        valorUnitarioAnt = Double.parseDouble(
                        nvl(registro.getCampos().get(cValorUnitario), "0.0")
                                        .toString());
    }

    public void cambiarCP8236() {
        // MODIFICAR CANTIDAD

        calcularTotalADI();
        int cantidad = (registro.getCampos().get(cCantidad) == null
            || "".equals(registro.getCampos().get(cCantidad)))
                ? 0
                : Integer.parseInt(registro.getCampos().get(cCantidad)
                                .toString());
        registro.getCampos().put("SALDOCANT", cantidad);
        modificarcantidadContratoOri(Double.parseDouble(numeroAfectado),
                        tipoAfectado,
                        Double.parseDouble(
                                        nvl(registro.getCampos().get(cCodigo),
                                                        "0.0").toString()),
                        "CANTIDADMODIFICADO",
                        Double.parseDouble(nvl(
                                        registro.getCampos().get(cCantidad),
                                        "0.0").toString()),
                        cantidadAnterior);

        valorUnitarioAnt = registro.getCampos().get(cValorUnitario) == null
            ? 0.0
            : Double.parseDouble(registro.getCampos().get(cValorUnitario)
                            .toString());

    }

    public void ejecutarantesCambioCantidad() {
        // comandoREMOTO
        cantidadAnterior = Double.parseDouble(
                        nvl(registro.getCampos().get(cCantidad), "0.0")
                                        .toString());
    }

    public void calcularTotalADI() {
        try {
            int cantidad = (registro.getCampos().get(cCantidad) == null
                || "".equals(registro.getCampos().get(cCantidad)))
                    ? 0
                    : Integer.parseInt(registro.getCampos().get(cCantidad)
                                    .toString());
            Double dblIva;
            Double dblTotal = Double.parseDouble(
                            nvl(registro.getCampos().get(cValorUnitario), "0.0")
                                            .toString())
                * Double.parseDouble(
                                nvl(registro.getCampos().get(cCantidad), "0.0")
                                                .toString());
            Double dblDescuento = SysmanFunciones.redondear(
                            dblTotal
                                * Double.parseDouble(nvl(registro.getCampos()
                                                .get("PORCDESC"), "0.0")
                                                                .toString())
                                / 100,
                            2);

            String redondearIva = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "REDONDEAR VALOR IVA EN O.C.",
                                            modulo, new Date(),
                                            true), "NO");
            int digitosRedondeoIva = Integer.parseInt(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "DIGITOS REDONDEO VALOR IVA EN O.C.",
                                            modulo, new Date(), true), "0"));
            if ("NO".equals(redondearIva)) {

                dblIva = SysmanFunciones.redondear(
                                (dblTotal - dblDescuento) * (1
                                    + Double.parseDouble(nvl(
                                                    registro.getCampos()
                                                                    .get(cPorciva),
                                                    "0.0").toString())
                                        / 100),
                                2)
                    - dblTotal + dblDescuento;
            }
            else {
                dblIva = SysmanFunciones.redondear(
                                Double.parseDouble(nvl(
                                                registro.getCampos()
                                                                .get(cValorUnitario),
                                                "0.0").toString())
                                    * Double.parseDouble(nvl(
                                                    registro.getCampos()
                                                                    .get(cPorciva),
                                                    "0.0").toString())
                                    / 100,
                                digitosRedondeoIva)
                    * Double.parseDouble(registro.getCampos().get(cCantidad)
                                    .toString());
            }
            dblTotal = dblTotal - dblDescuento + dblIva;
            if (cantidad > 0) {

                String redonValorUnitarioIVA = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "REDONDEAR UNITARIO CON IVA EN O.C.",
                                                modulo, new Date(), true),
                                                "NO");

                if ("SI".equals(redonValorUnitarioIVA)) {

                    int digRedondeoValorUnitario = Integer.parseInt(
                                    SysmanFunciones.nvlStr(ejbSysmanUtil
                                                    .consultarParametro(
                                                                    compania,
                                                                    "DIGITOS REDONDEO UNITARIO CON IVA O.C.",
                                                                    modulo,
                                                                    new Date(),
                                                                    true),
                                                    "0"));
                    registro.getCampos().put(cValorUnitariODI,
                                    SysmanFunciones.redondear(dblTotal
                                        / cantidad, digRedondeoValorUnitario));
                }
                else {
                    registro.getCampos().put(cValorUnitariODI, SysmanFunciones
                                    .redondear(dblTotal / cantidad, 2));
                }
                String redondeoTotal = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "REDONDEAR VALOR TOTAL EN O.C.",
                                                modulo, new Date(),
                                                true),
                                "NO");
                if ("SI".equals(redondeoTotal)) {
                    int digitosRedonTotal = Integer.parseInt(SysmanFunciones
                                    .nvlStr(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "DIGITOS REDONDEO VALOR TOTAL O.C.",
                                                    modulo,
                                                    new Date(), true), "0"));
                    valorTotalAnterior = registro.getCampos()
                                    .get(cVlrTotal) == null ? 0.0
                                        : Double.parseDouble(nvl(
                                                        registro.getCampos()
                                                                        .get(cVlrTotal),
                                                        "0.0").toString());
                    registro.getCampos()
                                    .put(cVlrTotal,
                                                    SysmanFunciones.redondear(
                                                                    Double
                                                                                    .parseDouble(
                                                                                                    nvl(registro.getCampos()
                                                                                                                    .get(cValorUnitariODI),
                                                                                                                    "0.0").toString())
                                                                        * cantidad,

                                                                    digitosRedonTotal));
                }
                else {
                    valorTotalAnterior = registro.getCampos()
                                    .get(cVlrTotal) == null ? 0.0
                                        : Double.parseDouble(nvl(
                                                        registro.getCampos()
                                                                        .get(cVlrTotal),
                                                        "0.0").toString());
                    registro.getCampos()
                                    .put(cVlrTotal,
                                                    SysmanFunciones.redondear(
                                                                    Double
                                                                                    .parseDouble(
                                                                                                    nvl(registro.getCampos()
                                                                                                                    .get(cValorUnitariODI),
                                                                                                                    "0.0").toString())
                                                                        * cantidad,
                                                                    2));
                }

            }
            else {
                registro.getCampos().put(cValorUnitariODI, 0);
                valorTotalAnterior = registro.getCampos().get(cVlrTotal) == null
                    ? 0.0
                    : Double.parseDouble(
                                    nvl(registro.getCampos().get(cVlrTotal),
                                                    "0.0")
                                                                    .toString());
                registro.getCampos().put(cVlrTotal, 0);
            }
            registro.getCampos().put("VLRDESCUENTO", dblDescuento);
            registro.getCampos().put("VLRIVA", dblIva);
            Double valorTotal = registro.getCampos().get(cValorTotal) == null
                ? 0.0
                : Double.parseDouble(
                                nvl(registro.getCampos().get(cValorTotal),
                                                "0.0")
                                                                .toString());
            modificarcantidadContratoOri(Double.parseDouble(numeroAfectado),
                            tipoAfectado,
                            Double.parseDouble(
                                            nvl(registro.getCampos()
                                                            .get(cCodigo),
                                                            "0.0").toString()),
                            "VALORTOTALMODIFICADO", valorTotal,
                            valorTotalAnterior);
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void modificarcantidadContratoOri(Double numeroContratoafectado,
        String tipoContratoafectado, Double itemAfectado, String campoAafectar,
        Double valorAafectar, Double valorAnterior) {
        try {
            ejbContratosDos.modificarCantidadContrato(compania,
                            tipoContratoafectado,
                            numeroContratoafectado.longValue(),
                            itemAfectado.longValue(), campoAafectar,
                            String.valueOf(valorAnterior),
                            String.valueOf(valorAafectar),
                            SessionUtil.getUser().getCodigo());
        }
        catch (SystemException ex) {
            Logger.getLogger(SubpcontratoadiControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void ejecutarrcCerrar() {
        SessionUtil.cleanFlash();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tipoContrato", claseOrden);
        parametros.put("tipoAfectado", tipoAfectado);
        parametros.put("anio", anio);
        parametros.put("vigencia", vigencia);
        parametros.put("nombreContrato", nombreContrato);
        parametros.put("rid", rid2);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.ADICIONESPCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    public String getNumeroAfectado() {
        return numeroAfectado;
    }

    public void setNumeroAfectado(String numeroAfectado) {
        this.numeroAfectado = numeroAfectado;
    }

    public String getTipoAfectado() {
        return tipoAfectado;
    }

    public void setTipoAfectado(String tipoAfectado) {
        this.tipoAfectado = tipoAfectado;
    }

    public boolean isBloqueadoElemento() {
        return bloqueadoElemento;
    }

    public void setBloqueadoElemento(boolean bloqueadoElemento) {
        this.bloqueadoElemento = bloqueadoElemento;
    }

    public String getManeja() {
        return maneja;
    }

    public void setManeja(String maneja) {
        this.maneja = maneja;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getvTxtDigitosRedondeoValorIVA() {
        return vTxtDigitosRedondeoValorIVA;
    }

    public void setvTxtDigitosRedondeoValorIVA(
        String vTxtDigitosRedondeoValorIVA) {
        this.vTxtDigitosRedondeoValorIVA = vTxtDigitosRedondeoValorIVA;
    }

    public Double getCantidadAnterior() {
        return cantidadAnterior;
    }

    public void setCantidadAnterior(Double cantidadAnterior) {
        this.cantidadAnterior = cantidadAnterior;
    }

    public Double getValorUnitarioAnt() {
        return valorUnitarioAnt;
    }

    public void setValorUnitarioAnt(Double valorUnitarioAnt) {
        this.valorUnitarioAnt = valorUnitarioAnt;
    }

    public Double getValorTotalAnterior() {
        return valorTotalAnterior;
    }

    public void setValorTotalAnterior(Double valorTotalAnterior) {
        this.valorTotalAnterior = valorTotalAnterior;
    }

    public boolean isSubPContratoPermiteAgregar() {
        return subPContratoPermiteAgregar;
    }

    public void setSubPContratoPermiteAgregar(
        boolean subPContratoPermiteAgregar) {
        this.subPContratoPermiteAgregar = subPContratoPermiteAgregar;
    }

    public boolean isSubPContratoPermiteEditar() {
        return subPContratoPermiteEditar;
    }

    public void setSubPContratoPermiteEditar(
        boolean subPContratoPermiteEditar) {
        this.subPContratoPermiteEditar = subPContratoPermiteEditar;
    }

    public boolean isSubPContratoPermiteEliminar() {
        return subPContratoPermiteEliminar;
    }

    public void setSubPContratoPermiteEliminar(
        boolean subPContratoPermiteEliminar) {
        this.subPContratoPermiteEliminar = subPContratoPermiteEliminar;
    }

    public RegistroDataModelImpl getListaCB1024() {
        return listaCB1024;
    }

    public void setListaCB1024(RegistroDataModelImpl listaCB1024) {
        this.listaCB1024 = listaCB1024;
    }

    public List<Registro> getListaCB1593() {
        return listaCB1593;
    }

    public void setListaCB1593(List<Registro> listaCB1593) {
        this.listaCB1593 = listaCB1593;
    }

    public RegistroDataModelImpl getListaELEMENTO() {
        return listaELEMENTO;
    }

    public void setListaELEMENTO(RegistroDataModelImpl listaCB1023) {
        this.listaELEMENTO = listaCB1023;
    }

    public RegistroDataModelImpl getListaCB1595() {
        return listaCB1595;
    }

    public void setListaCB1595(RegistroDataModelImpl listaCB1595) {
        this.listaCB1595 = listaCB1595;
    }

    public RegistroDataModelImpl getListaCB1594() {
        return listaCB1594;
    }

    public void setListaCB1594(RegistroDataModelImpl listaCB1594) {
        this.listaCB1594 = listaCB1594;
    }

}