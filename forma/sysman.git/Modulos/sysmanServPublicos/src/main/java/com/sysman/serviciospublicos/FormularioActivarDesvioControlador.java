/*-
 * FormularioActivarDesvioControlador.java
 *
 * 1.0
 *
 * 29/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.enums.FormularioActivarDesvioControladorEnum;
import com.sysman.serviciospublicos.enums.FormularioActivarDesvioControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Esta clase es el controlador de la forma formularioactivardesvio,
 * llamada desde Panel Principal/Facturacion de Servicios
 * Publicos/Novedades/Correccion de
 * Critica/SubFormularioCorreccionCriticaLista/Check Activar
 * Desviacion
 *
 * @version 1.0, 29/09/2016
 * @author dmaldonado
 * 
 * @author eamaya
 * @version 2, 23/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class FormularioActivarDesvioControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     *
     */
    private String lecturaInicialReal;
    private Date vencimiento;
    private Date fechaAforo;
    private Double promedio;
    private String ciclo;
    private String codigoRuta;
    private String ano;
    private String periodo;
    private String lectura;
    private String lecturaAforo;
    private Date fechaLecAforo;
    private boolean activaDesvio;
    private String consumoPromedio;
    private String formularioPrevio;

    @EJB
    private EjbSysmanUtilRemote ejbConsecutivo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of FormularioActivarDesvioControlador
     */
    public FormularioActivarDesvioControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FORMULARIO_ACTIVAR_DESVIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            cargarFlash();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void cargarFlash() {
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            formularioPrevio = (String) parametrosEntrada
                            .get("formularioPrevio");
            ciclo = (String) parametrosEntrada.get("ciclo");
            codigoRuta = (String) parametrosEntrada.get("codigoRuta");
            ano = (String) parametrosEntrada.get("ano");
            periodo = (String) parametrosEntrada.get("periodo");
            lectura = (String) parametrosEntrada.get("lectura");
            lecturaAforo = (String) parametrosEntrada.get("lecturaAforo");
            fechaLecAforo = (Date) parametrosEntrada.get("fechaLecAforo");
            consumoPromedio = (String) parametrosEntrada.get("consumoPromedio");
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1115-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore
         * Forms!FRM_CORRECCIONCRITICA!FRM_CORRECCIONCRITICA_LIS.Form!
         * ActivaDesvio = False End Sub
         */
        activaDesvio = false;
        lecturaInicialReal = lecturaAforo;
        vencimiento = SysmanFunciones.sumarRestarMesesFecha(new Date(), 4);
        fechaAforo = fechaLecAforo;
        promedio = Double.valueOf(consumoPromedio);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {

            Long consecutivo;

            consecutivo = ejbConsecutivo.generarSiguienteConsecutivo(
                            "SP_DESVIACIONES", "COMPANIA=" + compania,
                            "CONSECUTIVO");

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);

            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);

            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoRuta);

            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            param.put(GeneralParameterEnum.PERIODO.getName(),
                            periodo);

            param.put(FormularioActivarDesvioControladorEnum.PARAM0.getValue(),
                            vencimiento);

            param.put(GeneralParameterEnum.ESTADO.getName(),
                            "A");

            param.put(FormularioActivarDesvioControladorEnum.PARAM1.getValue(),
                            SysmanFunciones.nvlStr(lecturaInicialReal,
                                            "0"));

            param.put(FormularioActivarDesvioControladorEnum.PARAM2.getValue(),
                            fechaAforo);

            param.put(FormularioActivarDesvioControladorEnum.PARAM3.getValue(),
                            SysmanFunciones.nvlDbl(promedio, 0));

            param.put(FormularioActivarDesvioControladorEnum.PARAM4.getValue(),
                            SysmanFunciones.nvlStr(lectura, "0"));

            param.put(FormularioActivarDesvioControladorEnum.PARAM5.getValue(),
                            new Date());

            param.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            param.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FormularioActivarDesvioControladorUrlEnum.URL5304
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            param);

            if ("FormularioCorreccionCritica".equals(formularioPrevio)) {
                activaDesvio = true;
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("activaDesvio", activaDesvio);
            SessionUtil.setFlash(parametros);
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getLecturaInicialReal() {
        return lecturaInicialReal;
    }

    public void setLecturaInicialReal(String lecturaInicialReal) {
        this.lecturaInicialReal = lecturaInicialReal;
    }

    public Date getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(Date vencimiento) {
        this.vencimiento = vencimiento;
    }

    public Date getFechaAforo() {
        return fechaAforo;
    }

    public void setFechaAforo(Date fechaAforo) {
        this.fechaAforo = fechaAforo;
    }

    public Double getPromedio() {
        return promedio;
    }

    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
