/*-
 * FacturaconveniosControlador.java
 *
 * 1.0
 * 
 * 03/10/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.FacturaConveniosControladorEnum;
import com.sysman.serviciospublicos.enums.FacturaConveniosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Contiene la migracion de la pestana Convenios del formulario
 * "Factura" en Access. Se realiza la migracion como un formulario
 * continuo
 *
 * @version 1.0, 03/10/2016
 * @author amonroy
 * 
 * @version 2.0, 22/05/2017 Proceso de refactoring.
 * @author jrodrigueza
 */
@ManagedBean
@ViewScoped
public class FacturaConveniosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante que almacena el identificador de la compania
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio de la factura
     */
    private String ano;
    /**
     * Atributo que almacena el periodo de la factura
     */
    private String periodo;
    /**
     * Atributo que almacena el ciclo de la factura
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo de ruta de la factura
     */
    private String codigoRuta;
    /**
     * Atributo que almacena la fecha de pago de la factura
     */
    private String fechaPagoPerProceso;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado que contiene los registros pertenecientes a la tabla
     * SP_BANCOS, es llamada en el comboBox de Banco
     */
    private List<Registro> listaBancoPago;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Constructor de la clase FacturaconveniosControlador que recibe
     * los parametros de inicializacion desde el formulario "Factura".
     * 
     * Crea una nueva instancia de FacturaconveniosControlador
     */
    public FacturaConveniosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURA_CONVENIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("ciclo").toString();
                codigoRuta = parametrosEntrada.get("codigoruta").toString();
                ano = parametrosEntrada.get("ano").toString();
                periodo = parametrosEntrada.get("periodo").toString();
                fechaPagoPerProceso = parametrosEntrada
                                .get("fechaPagoPerProceso").toString();
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_HISTORIA_CONVENIOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaBancoPago();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Realiza la carga del listado de registros para el combo de
     * banco en el subformulario "Frmsaldocredito"
     */
    public void cargarListaBancoPago() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = FacturaConveniosControladorUrlEnum.URL5258
                        .getValue();
        List<Parameter> parameters;
        try {
            String url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
            parameters = requestManager.getList(url, param);
            listaBancoPago = RegistroConverter.toListRegistro(parameters);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

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
        // </CODIGO_DESARROLLADO>
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
    /**
     * Compara el anio y periodo actual con el de la factura, para
     * permitir o no la edicion del registro seleccionado
     */
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        int anoAux = Integer
                        .parseInt(registro.getLlave().get("KEY_ANO")
                                        .toString());
        int periodoAux = Integer.parseInt(
                        registro.getLlave().get("KEY_PERIODO").toString());
        if (anoAux != Integer.parseInt(ano)
            || periodoAux != Integer.parseInt(periodo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1718"));
            return false;
        }
        else if (!("").equals(SysmanFunciones.nvl(fechaPagoPerProceso, "")
                        .toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1719"));
            return false;
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(
                        FacturaConveniosControladorEnum.PARAM2.getValue());
        registro.getCampos().remove(
                        FacturaConveniosControladorEnum.PARAM0.getValue());
        registro.getCampos().remove(
                        FacturaConveniosControladorEnum.PARAM1.getValue());
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getFechaPagoPerProceso() {
        return fechaPagoPerProceso;
    }

    public void setFechaPagoPerProceso(String fechaPagoPerProceso) {
        this.fechaPagoPerProceso = fechaPagoPerProceso;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaBancoPago() {
        return listaBancoPago;
    }

    public void setListaBancoPago(List<Registro> listaBancoPago) {
        this.listaBancoPago = listaBancoPago;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // M�todo heredado
    }
}
