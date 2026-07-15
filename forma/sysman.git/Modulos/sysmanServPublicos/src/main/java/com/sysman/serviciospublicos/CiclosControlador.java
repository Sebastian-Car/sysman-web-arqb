package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.serviciospublicos.enums.CiclosControladorEnum;
import com.sysman.serviciospublicos.enums.CiclosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author jguerrero
 * @version 1, 01/08/2016
 * @modifier amonroy
 * @version 2, 17/05/2017 Proceso de Refactoring e implementaci�n de
 * EJBs para consulta de parametros
 */
@ManagedBean
@ViewScoped
public class CiclosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String strPeriodo;

    // <DECLARAR_ATRIBUTOS>
    private boolean anioBloqueado;
    private boolean fechaPreparacionBloqueada;
    private boolean prefacturadoVisible;
    private boolean aplicaDescuentoVisible;
    private boolean lbEstadistica;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * los parametros
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listatxtAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public CiclosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strPeriodo = GeneralParameterEnum.PERIODO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.CICLOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CiclosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListatxtAno();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_CICLO;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatxtAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listatxtAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CiclosControladorUrlEnum.URL3582
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiartxtPeriodo() {
        // <CODIGO_DESARROLLADO>

        if ((Integer.parseInt(
                        registro.getCampos().get(strPeriodo).toString()) < 1)
            || (Integer.parseInt(registro.getCampos().get(strPeriodo)
                            .toString()) > 12)) {
            registro.getCampos().put(strPeriodo, null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1135"));

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Valida que las fechas registradas en los campos de fecha limite
     * posean una diferencia maxima de un anio al anio del ciclo
     */
    private void validarFechas() {

        int anio = Integer.parseInt(registro.getCampos().get("ANO").toString());

        if (registro.getCampos().get("FECHAPAGO1") != null
            && Math.abs(anio
                - SysmanFunciones.ano((Date) registro.getCampos()
                                .get("FECHAPAGO1"))) >= 2) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3187"));
        }
        else if (registro.getCampos().get("FECHAPAGO2") != null
            && Math.abs(anio
                - SysmanFunciones.ano(
                                (Date) registro.getCampos()
                                                .get("FECHAPAGO2"))) >= 2) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3188"));
        }
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        if (permisoAccion("MODIFICADORES FECHA DE PREPARACION")) {
            fechaPreparacionBloqueada = false;
        }
        else {
            fechaPreparacionBloqueada = true;
        }

        if ("SI".equals(getParametro(idioma.getString("TB_TB3161"),
                        "NO"))) {
            prefacturadoVisible = true;
        }
        else {
            prefacturadoVisible = false;

        }

        if ("SI".equals(getParametro(
                        "CALCULAR DESCUENTO CON PORCENTAJE POR CONCEPTO",
                        "NO"))) {

            aplicaDescuentoVisible = true;
        }
        else {
            aplicaDescuentoVisible = false;
        }

        if ("SI".equals(getParametro("INTERFAZ MENSUALIZADA DE FACTURACION",
                        "NO"))) {
            lbEstadistica = true;
        }
        else {
            lbEstadistica = false;
        }
    }

    private boolean permisoAccion(String accion) {
        String temp = getParametro(accion, "");
        if (temp.isEmpty()) {
            return false;
        }
        else {
            temp = temp.replace(" ", "");
            boolean retorno;
            if (temp.contains(SessionUtil.getUser().getCodigo())) {
                retorno = true;
            }
            else {
                retorno = false;
            }
            return retorno;
        }

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css != null) {
            anioBloqueado = true;
        }
        else {
            anioBloqueado = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(CiclosControladorEnum.ANOINICIAL.getValue(),
                        SysmanFunciones.getParteFecha(new Date(),
                                        Calendar.YEAR));
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
        validarFechas();
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
    // <SET_GET_ATRIBUTOS>

    public boolean isAnioBloqueado() {
        return anioBloqueado;
    }

    public void setAnioBloqueado(boolean anioBloqueado) {
        this.anioBloqueado = anioBloqueado;
    }

    public boolean isFechaPreparacionBloqueada() {
        return fechaPreparacionBloqueada;
    }

    public void setFechaPreparacionBloqueada(
        boolean fechaPreparacionBloqueada) {
        this.fechaPreparacionBloqueada = fechaPreparacionBloqueada;
    }

    public boolean isPrefacturadoVisible() {
        return prefacturadoVisible;
    }

    public void setPrefacturadoVisible(boolean prefacturadoVisible) {
        this.prefacturadoVisible = prefacturadoVisible;
    }

    public boolean isAplicaDescuentoVisible() {
        return aplicaDescuentoVisible;
    }

    public void setAplicaDescuentoVisible(boolean aplicaDescuentoVisible) {
        this.aplicaDescuentoVisible = aplicaDescuentoVisible;
    }

    public boolean isLbEstadistica() {
        return lbEstadistica;
    }

    public void setLbEstadistica(boolean lbEstadistica) {
        this.lbEstadistica = lbEstadistica;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListatxtAno() {
        return listatxtAno;
    }

    public void setListatxtAno(List<Registro> listatxtAno) {
        this.listatxtAno = listatxtAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
