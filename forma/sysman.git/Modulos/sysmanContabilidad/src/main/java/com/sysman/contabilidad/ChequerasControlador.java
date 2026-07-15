package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.enums.ChequerasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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
 * @author otorres
 * @version 1, 07/03/2016
 * 
 * @author jlramirez
 * @version 2, 07/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017, Manejo de EJBs
 */
@ManagedBean
@ViewScoped
public class ChequerasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private String ano;
    private String cuenta;
    private Map<String, Object> rid;
    @EJB
    private EjbContabilidadCeroRemote contabilidadCero;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    @EJB
    private EjbContabilidadSeisRemote contabilidadSeis;

    /**
     * Creates a new instance of ChequerasControlador
     */
    public ChequerasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CHEQUERAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ChequerasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CHEQUERA;
        buscarLlave();
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            ano = (String) parametrosEntrada
                            .get(GeneralParameterEnum.ANO.getName());
            cuenta = (String) parametrosEntrada
                            .get(GeneralParameterEnum.CUENTA.getName());
            rid = (Map<String, Object>) parametrosEntrada.get("RID");
        }
        SessionUtil.cleanFlash();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
    }

    public void oprimirComando25() {
        try {
            // <CODIGO_DESARROLLADO>
            String respuesta = contabilidadCero.corregirChequera(compania,
                            Integer.parseInt(ano), cuenta);

            if ("OK".equals(respuesta)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB869"));
                return;
            }
            else {
                JsfUtil.agregarMensajeInformativo(respuesta);
                return;
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(ChequerasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        reasignarOrigen();
    }

    public void oprimiranularcheque() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { GeneralParameterEnum.CUENTA.getName() };
        String[] valores = { cuenta };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.CHEQUES_ANULADOS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        try {

            contabilidadSeis.validarChequera(compania,
                            Integer.valueOf(ano), cuenta,
                            Integer.valueOf(registro.getCampos()
                                            .get("NUMINICIAL").toString()),
                            Integer.valueOf(registro.getCampos().get("NUMFINAL")
                                            .toString()));

            // <CODIGO_DESARROLLADO>
            long consecutivo = sysmanUtil.generarConsecutivoConValorInicial(
                            tabla,
                            " COMPANIA = ''" + compania + "'' AND ANO = " + ano
                                + " AND CUENTA = ''" + cuenta + "'' ",
                            "NUMCHEQUERA", "1");
            registro.getCampos().put("NUMCHEQUERA", consecutivo);
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(ChequerasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            return false;
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
        try {
            String disponible;
            String numFinal = SysmanFunciones
                            .nvl(registro.getCampos().get("NUMFINAL"), " ")
                            .toString();
            String numActual = SysmanFunciones
                            .nvl(registro.getCampos().get("NUMACTUAL"), " ")
                            .toString();
            String numInicial = SysmanFunciones
                            .nvl(registro.getCampos().get("NUMINICIAL"), " ")
                            .toString();
            if ((Integer.parseInt(numFinal) >= Integer.parseInt(numInicial))
                && (Integer.parseInt(numActual) <= Integer.parseInt(numFinal))
                && (Integer.parseInt(numActual) >= Integer
                                .parseInt(numInicial))) {
                if (numActual != null) {
                    disponible = String.valueOf((Integer.parseInt(numFinal)
                        - Integer.parseInt(numActual)) + 1);
                }
                else {
                    disponible = String.valueOf((Integer.parseInt(numFinal)
                        - Integer.parseInt(numInicial)) + 1);
                }
                registro.getCampos().put(
                                GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                ano);
                registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                                cuenta);
                registro.getCampos().put("DISPONIBLES", disponible);
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB870"));
                return false;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

            Registro validacion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ChequerasControladorUrlEnum.URL254
                                                                            .getValue())
                                            .getUrl(), param));

            if (validacion != null
                && !(boolean) registro.getCampos().get("ANULADA")) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3173"));
                return false;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(ChequerasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("anio", ano);
        parametros.put("rid", rid);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PLAN_CONTABLE_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

}
