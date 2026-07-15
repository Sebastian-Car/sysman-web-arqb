package com.sysman.serviciospublicos;

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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoRemote;
import com.sysman.serviciospublicos.enums.BorrarpagosControladorEnum;
import com.sysman.serviciospublicos.enums.BorrarpagosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 08/09/2016 11:30:24 -- Modificado por jrodriguezr
 * 
 * @author eamaya
 * @version 2, 16/05/2017 Proceso de Refactoring, Correcciones
 * SonarLint y Manejo de EJBs
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class BorrarpagosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String texto;
    private final String banco;
    private final String fecha;
    private final String numPaquete;
    private final String tablaPago;
    private final String valor;
    // <DECLARAR_ATRIBUTOS>
    private String nombreBanco;
    private String totalCupones;
    private String totalValorReg;
    private int indiceSubpago;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaSubpago;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    @EJB
    private EjbServiciosPublicosUnoRemote ejbServPublicos;

    // </DECLARAR_ADICIONALES>
    public BorrarpagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        texto = "$ #,##0.00";
        banco = "BANCO";
        fecha = "FECHA";
        numPaquete = "NUMEROPAQUETE";
        tablaPago = "SP_PAGO";
        valor = "VALOR";
        try {
            numFormulario = GeneralCodigoFormaEnum.BORRARPAGOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(BorrarpagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubpago();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubpago = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_RECAUDOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        try {
            String permiteVerOtrosUsuarios = ejbParametro.consultarParametro(
                            compania,
                            "PERMITE VER RECAUDOS DE OTROS USUARIOS",
                            SessionUtil.getModulo(), new Date(), false);

            String permiteVerOtrasFechas = ejbParametro.consultarParametro(
                            compania,
                            "PERMITE VER RECAUDOS DE OTRAS FECHAS",
                            SessionUtil.getModulo(), new Date(), false);

            String condicionUsuarios = (permiteVerOtrosUsuarios != null)
                && "NO".equals(permiteVerOtrosUsuarios)
                    ? SessionUtil.getUser().getCodigo()
                    : null;

            String condicionFechas = (permiteVerOtrasFechas != null)
                && "NO".equals(permiteVerOtrasFechas)
                    ? SysmanFunciones.convertirAFechaCadena(new Date())
                    : null;

            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosListado.put(BorrarpagosControladorEnum.PARAM0.getValue(),
                            condicionUsuarios);

            parametrosListado.put(BorrarpagosControladorEnum.PARAM1.getValue(),
                            condicionFechas);

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            BorrarpagosControladorUrlEnum.URL6969
                                                            .getValue());

            urlLectura = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            BorrarpagosControladorUrlEnum.URL1313
                                                            .getValue());

        }
        catch (SystemException | ParseException e) {
            Logger.getLogger(BorrarpagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaSubpago() {
        try {

            Date fechaIngreso = (Date) registro.getCampos().get(fecha);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            BorrarpagosControladorUrlEnum.URL9147
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(BorrarpagosControladorEnum.PARAM2.getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaIngreso));

            param.put(BorrarpagosControladorEnum.PARAM3.getValue(),
                            registro.getCampos().get(banco));

            param.put(BorrarpagosControladorEnum.PARAM4.getValue(),
                            registro.getCampos().get(numPaquete));

            listaSubpago = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            tablaPago));
        }
        catch (ParseException | SysmanException e) {
            Logger.getLogger(BorrarpagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubpago() {
        // METODO_NO_IMPLEMENTADO
    }

    public void editarRegSubSubpago(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    public void eliminarRegSubSubpago(Registro reg) {
        if (!registro.getCampos().get("USUARIO")
                        .equals(SessionUtil.getUser().getCodigo())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3159"));
            return;
        }
        try {

            Date fechaBorrar = (Date) reg.getCampos().get(fecha);
            BigDecimal valorPago = new BigDecimal(
                            reg.getCampos().get("VALORPAGO").toString());

            JsfUtil.agregarMensajeInformativo(ejbServPublicos.borrarPagos(
                            compania, fechaBorrar,
                            reg.getCampos().get(banco).toString(),
                            reg.getCampos().get(numPaquete).toString(),
                            Integer.parseInt(reg.getCampos().get("CICLO")
                                            .toString()),
                            reg.getCampos().get("CODIGORUTA").toString(),
                            reg.getCampos().get("OPERACION").toString(),
                            Integer.parseInt(reg.getCampos().get("CONSECUTIVO")
                                            .toString()),
                            SessionUtil.getGrupo(SessionUtil.getModulo())
                                            .getCodigo(),
                            valorPago,
                            SessionUtil.getUser().getCodigo()));
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(BorrarpagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaSubpago();
            cargarRegistro();
        }
    }

    public void cancelarEdicionSubpago() {
        cargarListaSubpago();
        cargarRegistro();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        try {
            precargarRegistro();

            Date fechaIngreso = (Date) registro.getCampos().get(fecha);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(BorrarpagosControladorEnum.PARAM2.getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaIngreso));

            param.put(BorrarpagosControladorEnum.PARAM3.getValue(),
                            registro.getCampos().get(banco));

            param.put(BorrarpagosControladorEnum.PARAM4.getValue(),
                            registro.getCampos().get(numPaquete));

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BorrarpagosControladorUrlEnum.URL12035
                                                                            .getValue())
                                            .getUrl(), param));

            totalValorReg = new java.text.DecimalFormat(texto)
                            .format(Double.parseDouble(reg.getCampos()
                                            .get(valor)
                                            .toString()));

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BorrarpagosControladorUrlEnum.URL24498
                                                                            .getValue())
                                            .getUrl(), param));

            totalCupones = regAux.getCampos()
                            .get(valor).toString();

        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicionSubpago(Registro reg) {
        indiceSubpago = reg.getIndice();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        cargarRegistro();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getTotalCupones() {
        return totalCupones;
    }

    public void setTotalCupones(String totalCupones) {
        this.totalCupones = totalCupones;
    }

    public String getTotalValorReg() {
        return totalValorReg;
    }

    public void setTotalValorReg(String totalValorReg) {
        this.totalValorReg = totalValorReg;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public int getIndiceSubpago() {
        return indiceSubpago;
    }

    public void setIndiceSubpago(int indiceSubpago) {
        this.indiceSubpago = indiceSubpago;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public RegistroDataModelImpl getListaSubpago() {
        return listaSubpago;
    }

    public void setListaSubpago(RegistroDataModelImpl listaSubpago) {
        this.listaSubpago = listaSubpago;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
