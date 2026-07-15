package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.RevisarcuentasporpagarsControladorEnum;
import com.sysman.contabilidad.enums.RevisarcuentasporpagarsControladorUrlEnum;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dsuesca
 * @version 1, 16/05/2016 12:37:53 -- Modificado por dsuesca
 * 
 * @version 2, 10/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.
 * 
 * @author asana
 * @version 3, 12/06/2017, Se implementa enum en formulario
 * 
 */
@ManagedBean
@ViewScoped
public class RevisarcuentasporpagarsControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena {@code ABONADO }
     */
    private final String cAbonado;

    // <DECLARAR_ATRIBUTOS>
    private String anio;
    private String mes;
    /**
     * Atributo que almacena el valor de la fecha anterior dependiendo
     * el mes y ano seleccionado
     */
    private Date fechaPagoAux;
    /**
     * Atributo que valida la visibilidad de los campos de Pago del
     * formulario
     */
    private boolean mostrarPago;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSiete;

    /**
     * Creates a new instance of RevisarcuentasporpagarsControlador
     */
    public RevisarcuentasporpagarsControlador() {
        super();

        compania = SessionUtil.getCompania();

        cAbonado = "ABONADO";

        try {
            numFormulario = GeneralCodigoFormaEnum.REVISARCUENTASPORPAGARS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RevisarcuentasporpagarsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = GenericUrlEnum.COMPROBANTE_CNT.getTable();
        buscarLlave();
        abrirFormulario();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisarcuentasporpagarsControladorUrlEnum.URL5729
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisarcuentasporpagarsControladorUrlEnum.URL5728
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisarcuentasporpagarsControladorUrlEnum.URL5727
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        RevisarcuentasporpagarsControladorEnum.ANIO.getValue(),
                        anio);
        parametrosListado.put(GeneralParameterEnum.MES.getName(), mes);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAnio() {
        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisarcuentasporpagarsControladorUrlEnum.URL5730
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Seleccionar en la vista
     *
     */
    public void oprimirSeleccionar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbContabilidadSiete.actualizarFechaPago(compania,
                            Integer.valueOf(anio), Integer.valueOf(mes),
                            fechaPagoAux, SessionUtil.getUser().getCodigo(),
                            true);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Borrar en la vista
     *
     */
    public void oprimirBorrar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbContabilidadSiete.actualizarFechaPago(compania,
                            Integer.valueOf(anio), Integer.valueOf(mes),
                            fechaPagoAux, SessionUtil.getUser().getCodigo(),
                            false);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarVariableVacio(anio)
            && !SysmanFunciones.validarVariableVacio(mes)) {
            armarFecha(Integer.valueOf(anio), Integer.valueOf(mes));
        }
        reasignarOrigen();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarVariableVacio(anio)
            && !SysmanFunciones.validarVariableVacio(mes)) {
            armarFecha(Integer.valueOf(anio), Integer.valueOf(mes));
        }
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAbonadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cAbonado) != null) {
            if (!"0".equals(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos().get(cAbonado))) {
                BigDecimal vlrAgirar = new BigDecimal(
                                SysmanFunciones.nvl(listaInicial
                                                .getDatasource()
                                                .get(rowNum % 10)
                                                .getCampos().get("VLRAGIRAR"),
                                                "0.0").toString());

                BigDecimal debitosAfectados = new BigDecimal(SysmanFunciones
                                .nvl(listaInicial.getDatasource()
                                                .get(rowNum % 10)
                                                .getCampos()
                                                .get("DEBITOSAFECTADOS"), "0.0")
                                .toString());
                BigDecimal creditosAfectados = new BigDecimal(SysmanFunciones
                                .nvl(listaInicial.getDatasource()
                                                .get(rowNum % 10)
                                                .getCampos()
                                                .get("CREDITOSAFECTADOS"),
                                                "0.0")
                                .toString());
                BigDecimal abonado = new BigDecimal(SysmanFunciones
                                .nvl(listaInicial.getDatasource()
                                                .get(rowNum % 10)
                                                .getCampos().get(cAbonado),
                                                "0.0")
                                .toString());

                BigDecimal auxSaldo = vlrAgirar.subtract(debitosAfectados)
                                    .subtract(creditosAfectados);

                if (abonado.compareTo(auxSaldo) > 0) {
                    abonado = auxSaldo;
                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put(cAbonado, abonado);
                }

                if (abonado.compareTo(BigDecimal.ZERO) < 0) {
                    abonado = BigDecimal.ZERO;
                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put(cAbonado, abonado);
                }

                BigDecimal saldoT = auxSaldo.compareTo(BigDecimal.ZERO) > 0
                    ? auxSaldo.subtract(abonado)
                    : BigDecimal.ZERO;

                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put("SALDOT", saldoT);
                listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                                "USUARIO_ABONO",
                                SessionUtil.getUser().getCodigo());
                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put("FECHA_ABONO", new Date());

            }
        }
        else {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cAbonado, "0.0");
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    /**
     * Metodo que arma la fecha con el ultimo dia del mes anterior
     * anterior seleccionado
     * 
     * @param ano
     * @param mes
     */
    public void armarFecha(int ano, int mes) {
        String fechaPago = "";
        if (mes == 1) {
            fechaPago = SysmanFunciones.concatenar("00/", "00", "/",
                            String.valueOf(ano));
        }
        else {

            String mesAux = String.valueOf(mes - 1);
            fechaPago = SysmanFunciones.concatenar("00/", mesAux, "/",
                            String.valueOf(ano));
        }
        try {
            fechaPagoAux = SysmanFunciones.ultimoDiaDate(
                            SysmanFunciones.sumarRestarMesesFecha(
                                            SysmanFunciones.convertirAFecha(
                                                            fechaPago),
                                            1));
        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            String visiblePago = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "REVISAR CUENTAS POR PAGAR POR FECHA PAGO",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO");

            mostrarPago = "SI".equals(visiblePago);

            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = "1";
            armarFecha(Integer.valueOf(anio), Integer.valueOf(mes));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

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
        registro.getCampos().remove("SALDOT");
        registro.getCampos().remove("NOMBRETIPO");
        registro.getCampos().remove("CLASE_CONTABLE");
        registro.getCampos().remove("NOMBRETERCERO");
        registro.getCampos().remove("TIPOYNOMBRE");
        registro.getCampos().remove("EXPR1");

        if ((boolean) registro.getCampos().get("IND_PAGADO")) {
            registro.getCampos().put("FECHAPAGADOGN", fechaPagoAux);
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("TIPO");
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove("CREDITOSAFECTADOS");
        registro.getCampos().remove("FECHA");
        registro.getCampos().remove("VLRAGIRAR");
        registro.getCampos().remove("DEBITOSAFECTADOS");
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable fechaPagoAux
     * 
     * @return fechaPagoAux
     */
    public Date getFechaPagoAux() {
        return fechaPagoAux;
    }

    /**
     * Asigna la variable fechaPagoAux
     * 
     * @param fechaPagoAux
     * Variable a asignar en fechaPagoAux
     */
    public void setFechaPagoAux(Date fechaPagoAux) {
        this.fechaPagoAux = fechaPagoAux;
    }

    /**
     * Retorna la variable mostrarPago
     * 
     * @return mostrarPago
     */
    public boolean isMostrarPago() {
        return mostrarPago;
    }

    /**
     * Asigna la variable mostrarPago
     * 
     * @param mostrarPago
     * Variable a asignar en mostrarPago
     */
    public void setMostrarPago(boolean mostrarPago) {
        this.mostrarPago = mostrarPago;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
