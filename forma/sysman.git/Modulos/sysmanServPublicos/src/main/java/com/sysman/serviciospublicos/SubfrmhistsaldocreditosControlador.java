/*-
 * SubfrmhistsaldocreditosControlador.java
 *
 * 1.0
 *
 * 20 de sept. de 2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyacï¿½.
 * All rights reserved.
 */

package com.sysman.serviciospublicos;

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
import com.sysman.services.FormContinuoService;
import com.sysman.serviciospublicos.enums.SubfrmhistsaldocreditosControladorEnum;
import com.sysman.serviciospublicos.enums.SubfrmhistsaldocreditosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Contiene la migracion del formulario "SaldoCredito" en el modulo de
 * Servicios Publicos en Access.
 *
 * @version 1.0, 19 de sept. de 2016
 * @author amonroy
 * 
 * @version 2.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Refactoring.<br>
 * Se reemplazó el llamado a la forma por el llamado al código de la
 * misma.
 *
 */
@ManagedBean
@ViewScoped
public class SubfrmhistsaldocreditosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Atributo que almacena la compania
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ciclo de la factura
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo de ruta de la factura
     */
    private String codigoruta;
    /**
     * Atributo que almacena el nombre completo del usuario a quien
     * pertenece esa factura
     */
    private String nombre;
    /**
     * Atributo que almacena el anio de la factura
     */
    private String ano;
    /**
     * Atributo que almacena el periodo de la factura
     */
    private String periodo;
    /**
     * Atributo que almacena el valor de la nota credito de la factura
     */
    private String notacredito;
    /**
     * Atributo que almacena el valor del campo bancoperproceso de la
     * factura
     */
    private String bancoperproceso;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Atributo que permite almacenar valores temporales
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia del formulario , recibe los parametros
     * necesarios para la carga de datos en el formulario.
     */
    public SubfrmhistsaldocreditosControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            // 1106
            numFormulario = GeneralCodigoFormaEnum.SUBFRMHISTSALDOCREDITOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("ciclo").toString();
                codigoruta = parametrosEntrada.get("codigoruta").toString();
                ano = parametrosEntrada.get("ano").toString();
                periodo = parametrosEntrada.get("periodo").toString();
                notacredito = parametrosEntrada.get("notacredito").toString();
                bancoperproceso = parametrosEntrada.get("bancoperproceso")
                                .toString();
            }

        }
        catch (Exception ex) {
            Logger.getLogger(SubfrmhistsaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.SP_TBLHIST_SALDO_CREDITO.getTable();

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

        Registro rs = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(SubfrmhistsaldocreditosControladorEnum.CODIGO.getValue(),
                        codigoruta);

        try {
            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubfrmhistsaldocreditosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null) {
            nombre = SysmanFunciones.nvl(rs.getCampos().get("NOMBRE"), "")
                            .toString();
        }
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubfrmhistsaldocreditosControladorUrlEnum.URL9446
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoruta);
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    // </METODOS_COMBOS_GRANDES>
    /**
     * Se envia la referencia desde el formulario para poder cargar el
     * formulario "facturasaldocredito" al elegir la opcion de cerrar
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("ciclo", ciclo);
        param.put("codigoruta", codigoruta);
        param.put("ano", ano);
        param.put("periodo", periodo);
        param.put("notacredito", notacredito);
        param.put("bancoperproceso", bancoperproceso);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FACTURASALDOCREDITOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoruta() {
        return codigoruta;
    }

    public void setCodigoruta(String codigoruta) {
        this.codigoruta = codigoruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

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

    public String getNotacredito() {
        return notacredito;
    }

    public void setNotacredito(String notacredito) {
        this.notacredito = notacredito;
    }

    public String getBancoperproceso() {
        return bancoperproceso;
    }

    public void setBancoperproceso(String bancoperproceso) {
        this.bancoperproceso = bancoperproceso;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
