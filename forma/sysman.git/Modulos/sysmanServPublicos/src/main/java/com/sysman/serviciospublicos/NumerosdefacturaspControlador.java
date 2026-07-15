package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.NumerosdefacturaspControladorEnum;
import com.sysman.serviciospublicos.enums.NumerosdefacturaspControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 07/09/2016
 * 
 * @author eamaya
 * @version 2.0, 09/06/2017 Proceso de Refacotring y Correcciones
 * SonarLint
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * <code>ConectorPool.ESQUEMA_SYSMAN</code>.
 * 
 */
@ManagedBean
@ViewScoped
public class NumerosdefacturaspControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String consecutivo;
    private final String secuencia;
    // <DECLARAR_ATRIBUTOS>
    private boolean bloqueaConsecutivo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of NumerosdefacturaspControlador
     */
    public NumerosdefacturaspControlador() {
        super();
        compania = SessionUtil.getCompania();
        consecutivo = "CONSECUTIVO";
        secuencia = "SECUENCIA";
        try {
            // 1080
            numFormulario = GeneralCodigoFormaEnum.NUMEROSDEFACTURASP_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(NumerosdefacturaspControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.SP_NUMEROSDEFACTURA;

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put("COMPANIA", compania);
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
        bloqueaConsecutivo = true;
        // <CODIGO_DESARROLLADO>
        /*
         * FR1080-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 74, Me.Name Me.AllowDeletions = False
         * DoCmd.Maximize End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (Double.doubleToRawLongBits(SysmanFunciones
                        .nvlDbl(registro.getCampos().get(secuencia), 0)) == 0) {
            try {

                HashMap<String, Object> param = new HashMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                Registro rs;

                rs = RegistroConverter
                                .toRegistro(requestManager
                                                .get(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                NumerosdefacturaspControladorUrlEnum.URL4375
                                                                                                .getValue())
                                                                .getUrl(),
                                                                param));

                if (rs == null) {
                    registro.getCampos().put(secuencia, 1);
                }
                else {
                    registro.getCampos().put(secuencia,
                                    SysmanFunciones.nvlDbl(
                                                    rs.getCampos().get(
                                                                    "ULTIMONUMERO"),
                                                    0)
                                        + 1);
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
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
        /*
         * FR1080-ANTES_ACTUALIZAR
         */
        if (!"0".equals(SysmanFunciones.nvlStr(
                        registro.getCampos().get(consecutivo).toString(),
                        "0"))) {

            try {

                HashMap<String, Object> param = new HashMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(NumerosdefacturaspControladorEnum.PARAM0.getValue(),
                                registro.getCampos().get("SECUENCIA"));

                param.put(NumerosdefacturaspControladorEnum.PARAM1.getValue(),
                                SysmanFunciones.strZero(
                                                SysmanFunciones.nvlStr(
                                                                registro.getCampos()
                                                                                .get(consecutivo)
                                                                                .toString(),
                                                                "0"),
                                                10));

                Registro rs;

                rs = RegistroConverter
                                .toRegistro(requestManager
                                                .get(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                NumerosdefacturaspControladorUrlEnum.URL4478
                                                                                                .getValue())
                                                                .getUrl(),
                                                                param));

                // 'Valida que el nuevo consecutivo no sea menor que
                // uno
                // anterior
                if (rs == null) {
                    registro.getCampos().put("CONSECUTIVOREAL", SysmanFunciones
                                    .strZero(SysmanFunciones.nvlStr(
                                                    registro.getCampos()
                                                                    .get(consecutivo)
                                                                    .toString(),
                                                    "0"), 10));
                }
                else {
                    if (Long.parseLong(rs.getCampos().get("ULTIMOCON") == null
                        ? "0"
                        : rs.getCampos().get("ULTIMOCON").toString()) < Long
                                        .parseLong(registro.getCampos()
                                                        .get(consecutivo)
                                                        .toString())) {
                        registro.getCampos().put("CONSECUTIVOREAL",
                                        SysmanFunciones
                                                        .strZero(SysmanFunciones
                                                                        .nvlStr(
                                                                                        registro.getCampos()
                                                                                                        .get(consecutivo)
                                                                                                        .toString(),
                                                                                        "0"),
                                                                        10));
                    }
                    else {
                        JsfUtil.agregarMensajeAlerta(
                                        idioma.getString("TB_TB1539"));
                        return false;
                    }
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1540"));
            return false;
        }
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("FECHA", new Date());
        registro.getCampos().put("HORA", new Date());
        registro.getCampos().put("USUARIO", SessionUtil.getUser().getCodigo());
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
        // M�todo no implementado
    }

    @Override
    public void asignarValoresRegistro() {
        // M�todo no implementado
    }
    // <SET_GET_ATRIBUTOS>

    public boolean isBloqueaConsecutivo() {
        return bloqueaConsecutivo;
    }

    public void setBloqueaConsecutivo(boolean bloqueaConsecutivo) {
        this.bloqueaConsecutivo = bloqueaConsecutivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
