package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.PacsaldopptalsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
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
 * @author NGOMEZ
 * @version 1, 20/06/2016
 * 
 * @version 2, 19/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class PacsaldopptalsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario esta interactuando.
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String valorTotalApro;
    private String valorTotalMod;
    private String valorTotalPac;
    private String titulo;
    private String rezago;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    private String naturaleza;
    /**
     * Estructura que almacena los parametros enviados por Flash
     */
    private Map<String, Object> parametrosEntrada;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbGeneralesRemote ejbGenerales;

    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of PacsaldopptalsControlador
     */
    public PacsaldopptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PACSALDOPPTALS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anio = (String) parametrosEntrada.get("anio");
                codigo = (String) parametrosEntrada.get("codigo");
                nombre = (String) parametrosEntrada.get("nombre");
                naturaleza = (String) parametrosEntrada.get("naturaleza");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PacsaldopptalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = GenericUrlEnum.SALDO_PLAN_PPTAL.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PacsaldopptalsControladorUrlEnum.URL0001
                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = idioma.getString("TB_TB433") + " " + nombre.toUpperCase();
        actualizarTotales();
        calcularRezago();
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
        // NO EST� IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // NO EST� IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>

    public String getValorTotalApro() {
        return valorTotalApro;
    }

    public void setValorTotalApro(String valorTotalApro) {
        this.valorTotalApro = valorTotalApro;
    }

    public String getValorTotalMod() {
        return valorTotalMod;
    }

    public void setValorTotalMod(String valorTotalMod) {
        this.valorTotalMod = valorTotalMod;
    }

    public String getValorTotalPac() {
        return valorTotalPac;
    }

    public void setValorTotalPac(String valorTotalPac) {
        this.valorTotalPac = valorTotalPac;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getRezago() {
        return rezago;
    }

    public void setRezago(String rezago) {
        this.rezago = rezago;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void actualizarTotales() {
        Registro regAux = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);

        try {
            regAux = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PacsaldopptalsControladorUrlEnum.URL9096
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (regAux != null) {
            valorTotalApro = regAux.getCampos().get("PAC_APROPIADO").toString();
            valorTotalPac = regAux.getCampos().get("PAC_PROGRAMADO").toString();
            valorTotalMod = regAux.getCampos().get("MODIFICACIONESPAC")
                            .toString();
        }
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void calcularRezago() {
        try {
            BigDecimal aux;

            if (("NO").equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "DISTRIBUIR PAC TENIENDO EN CUENTA MODIFICACIONES",
                                            modulo, new Date(), true), "NO"))) {
                aux = ejbGenerales.consultarEjecucionPresupuestal(compania,
                                "SALDOAPROPIACION", Integer.parseInt(anio),
                                codigo, 12);
            }
            else {
                aux = ejbGenerales.consultarEjecucionPresupuestal(compania,
                                "APROPIADO", Integer.parseInt(anio),
                                codigo, 12);

            }
            rezago = String.valueOf(
                            (Double.parseDouble(aux.toString())
                                - Double.parseDouble(valorTotalApro.replace(",",
                                                "")))
                                + Double.parseDouble(valorTotalMod.replace(",",
                                                "")));
        }
        catch (SystemException e) {
            Logger.getLogger(PacsaldopptalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
}
