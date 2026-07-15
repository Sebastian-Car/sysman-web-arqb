package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.ConciliacionBancariaControladorEnum;
import com.sysman.contabilidad.enums.ConciliacionBancariaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite realizar la impresion de la conciliacion,
 * antes de verificar la fecha limite en la cual esta conciliando y
 * registrar el valor del saldo final que reporta el extracto; el cual
 * debe coincidir con el del sistema; de lo contrario esto indica que
 * la conciliacion esta mal.
 *
 * @author jrodrigueza
 * @version 1, 16/03/2016
 * 
 * @version 2, 07/04/2017
 * @author jreina se realizaron los cambios de refactoring en el
 * reasignar Origen.
 */
@ManagedBean
@ViewScoped
public class ConciliacionBancariaControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String consNombreCuenta;
    private final String consCodCuenta;
    private final String consNombre;
    private final String consCodigo;
    private String modulo;
    private int ano;
    private int mes;
    /**
     * Indica si se debe renderizar o no, el bot�n Vig.Ant
     */
    private boolean cargaVigAnt;
    /**
     * Indica si se debe renderizar o no, el bot�n Act. Partidas o
     * el bot�n Traer Partidas
     */
    private boolean cargaBtnPartidas;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of ConciliacionBancariaControlador
     */
    public ConciliacionBancariaControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNombreCuenta = "nombreCuenta";
        consCodCuenta = "codCuenta";
        consNombre = "NOMBRE";
        consCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CONCILIACION_BANCARIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            modulo = SessionUtil.getModulo();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                ano = (int) parametros.get("ano");
                mes = (int) parametros.get("mes");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConciliacionBancariaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = ConciliacionBancariaControladorEnum.TABLA.getValue();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ConciliacionBancariaControladorUrlEnum.URL2929
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);

    }

    private Map<String, Object> getParametrosContinuo(Registro reg) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);
        parametros.put("mes", mes);
        parametros.put(consCodCuenta, reg.getCampos().get(consCodigo));
        parametros.put(consNombreCuenta, reg.getCampos().get(consNombre));
        //
        // parametros.put("tercero", reg.getCampos().get("TERCERO"));
        // parametros.put("sucursal",
        // reg.getCampos().get("SUCURSAL"));
        // parametros.put("centroCosto",
        // reg.getCampos().get("CENTRO_COSTO"));
        // parametros.put("auxiliar",
        // reg.getCampos().get("AUXILIAR"));
        // parametros.put("fuenteRecurso",
        // reg.getCampos().get("FUENTE_RECURSO"));
        // parametros.put("referencia",
        // reg.getCampos().get("REFERENCIA"));
        return parametros;
    }

    public void oprimirActConciliacion(Registro reg, int indice) {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUB_CONCILIACION_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(getParametrosContinuo(reg));
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirVigAnt(Registro reg, int indice) {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUB_CONCILIACION_CONTROLADOR
                                        .getCodigo()));
        Map<String, Object> parametros = getParametrosContinuo(reg);
        parametros.put(consCodCuenta, reg.getCampos().get("COD_EQUIV"));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirImprimir(Registro reg, int indice) {
        String[] campos = { "ano", "mes", consCodCuenta, consNombreCuenta,
                            "codEquivalente" };
        Object[] valores = { String.valueOf(ano),
                             String.valueOf(mes), SysmanFunciones
                                             .nvl(reg.getCampos().get(
                                                             consCodigo), " ")
                                             .toString(),
                             SysmanFunciones.nvl(
                                             reg.getCampos().get(consNombre),
                                             " ")
                                             .toString(),
                             SysmanFunciones.nvl(
                                             reg.getCampos().get("COD_EQUIV"),
                                             " ").toString()
        };

        // String[] campos = { "ano", "mes", consCodCuenta,
        // consNombreCuenta,
        // "codEquivalente", "tercero", "sucursal",
        // "centroCosto", "auxiliar", "fuenteRecurso",
        // "referencia" };
        // Object[] valores = { String.valueOf(ano),
        // String.valueOf(mes), SysmanFunciones
        // .nvl(reg.getCampos().get(
        // consCodigo), " ")
        // .toString(),
        // SysmanFunciones.nvl(
        // reg.getCampos().get(consNombre),
        // " ")
        // .toString(),
        // SysmanFunciones.nvl(
        // reg.getCampos().get("COD_EQUIV"),
        // " ").toString(),
        // reg.getCampos().get("TERCERO"),
        // reg.getCampos().get("SUCURSAL"),
        // reg.getCampos().get("CENTRO_COSTO"),
        // reg.getCampos().get("AUXILIAR"),
        // reg.getCampos().get("FUENTE_RECURSO"),
        // reg.getCampos().get("REFERENCIA") };
        // impconciliacion.sysman
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.IMP_CONCILIACION_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
    }

    public void oprimirActPartidas(Registro reg, int indice) {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.PARTIDAS_CONCILIATORIAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(getParametrosContinuo(reg));
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirTraerPartidas(Registro reg, int indice) {
        String[] campos = { "ano", "mes", consCodCuenta, consNombreCuenta };
        String[] valores = { String.valueOf(ano),
                             String.valueOf(mes), SysmanFunciones
                                             .nvl(reg.getCampos().get(
                                                             consCodigo), " ")
                                             .toString(),
                             SysmanFunciones.nvl(
                                             reg.getCampos().get(consNombre),
                                             " ").toString() };
        // traerpartidasconciliatorias.sysman"
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.TRAER_PARTIDAS_CONCILIATORIAS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
    }

    @Override
    public void abrirFormulario() {
        try {
            String par = ejbSysmanUtilRemote.consultarParametro(compania,
                            "CONCILIACION CON CODIGO EQUIVALENTE VIGENCIA ANTERIOR",
                            modulo, new Date(), true);
            cargaVigAnt = "SI".equals(par);

            par = ejbSysmanUtilRemote.consultarParametro(compania,
                            "MANEJA INGRESO DE PARTIDAS CONCILIATORIAS", modulo,
                            new Date(), true);
            cargaBtnPartidas = "SI".equals(par);
        } catch (SystemException ex) {
            Logger.getLogger(ConciliacionBancariaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
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

    /*
     * Getters and Setters
     */
    public int getAnoConciliacion() {
        return ano;
    }

    public void setAnoConciliacion(int anoConciliacion) {
        this.ano = anoConciliacion;
    }

    public int getMesConciliacion() {
        return mes;
    }

    public void setMesConciliacion(int mesConciliacion) {
        this.mes = mesConciliacion;
    }

    public boolean isCargaVigAnt() {
        return cargaVigAnt;
    }

    public void setCargaVigAnt(boolean cargaVigAnt) {
        this.cargaVigAnt = cargaVigAnt;
    }

    public boolean isCargaBtnPartidas() {
        return cargaBtnPartidas;
    }

    public void setCargaBtnPartidas(boolean cargaBtnPartidas) {
        this.cargaBtnPartidas = cargaBtnPartidas;
    }

}
