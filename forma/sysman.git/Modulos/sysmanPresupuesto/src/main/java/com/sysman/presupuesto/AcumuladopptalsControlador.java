package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.AcumuladopptalsControladorEnum;
import com.sysman.presupuesto.enums.AcumuladopptalsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 21/06/2016
 * @modified jguerrero
 * @version 2. 17/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 2.1, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario
 * 
 */
@ManagedBean
@ViewScoped

public class AcumuladopptalsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> ridPlanPptal;
    private String anio;
    private String codigo;
    private String nombre;
    private String totalDipon;
    private String totalReg;
    private String totalRegObl;
    private String totalPAC;
    private String totalGiros;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of AcumuladopptalsControlador
     */
    @SuppressWarnings("unchecked")
    public AcumuladopptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACUMULADOPPTALS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridPlanPptal = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                anio = SysmanFunciones.nvl(parametrosEntrada.get("anio"), "")
                                .toString();
                codigo = SysmanFunciones
                                .nvl(parametrosEntrada.get("codigo"), "")
                                .toString();
                nombre = SysmanFunciones
                                .nvl(parametrosEntrada.get("nombre"), "")
                                .toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AcumuladopptalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = AcumuladopptalsControladorEnum.PARAM2.getValue();

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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumuladopptalsControladorUrlEnum.URL9445
                                                        .getValue());

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
        titulo = idioma.getString("TB_TB238") + " " + nombre.toUpperCase();
        actualizarTotales();
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
        // Mtodo heredado
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "anio" };
        Object[] valores = { ridPlanPptal, anio };
        SessionUtil.redireccionar("/planpresupuestalpto.sysman", campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    // <SET_GET_ATRIBUTOS>

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTotalDipon() {
        return totalDipon;
    }

    public void setTotalDipon(String totalDipon) {
        this.totalDipon = totalDipon;
    }

    public String getTotalReg() {
        return totalReg;
    }

    public void setTotalReg(String totalReg) {
        this.totalReg = totalReg;
    }

    public String getTotalRegObl() {
        return totalRegObl;
    }

    public void setTotalRegObl(String totalRegObl) {
        this.totalRegObl = totalRegObl;
    }

    public String getTotalPAC() {
        return totalPAC;
    }

    public void setTotalPAC(String totalPAC) {
        this.totalPAC = totalPAC;
    }

    public String getTotalGiros() {
        return totalGiros;
    }

    public void setTotalGiros(String totalGiros) {
        this.totalGiros = totalGiros;
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
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void actualizarTotales() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladopptalsControladorUrlEnum.URL9297
                                                                            .getValue())
                                            .getUrl(), param));

            totalDipon = SysmanFunciones
                            .nvl(regAux.getCampos().get("DISPONIBILIDAD"), "")
                            .toString();
            totalReg = SysmanFunciones
                            .nvl(regAux.getCampos().get("REGISTRO"), "")
                            .toString();
            totalRegObl = SysmanFunciones
                            .nvl(regAux.getCampos().get("REGISTROOBLIGACION"),
                                            "")
                            .toString();
            totalPAC = SysmanFunciones
                            .nvl(regAux.getCampos().get("PACPAGOS"), "")
                            .toString();
            totalGiros = SysmanFunciones
                            .nvl(regAux.getCampos().get("GIROSACUMULADOS"), "")
                            .toString();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
}
