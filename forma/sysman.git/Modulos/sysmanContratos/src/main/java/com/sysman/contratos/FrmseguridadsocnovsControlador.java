/*-
 * FrmseguridadsocnovsControlador.java
 *
 * 1.0
 * 
 * 30/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 30/09/2019
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class FrmseguridadsocnovsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String tipoContrato;
    private String numero;
    private String claseNov;
    private String txtNovedad;
    private String claseT;
    private String anio;
    private Map<String, Object> llaveRID;
    protected Map<String, Object> rid;
    private String valorAPagar;
    private String claseOrden;
    private String novedad;

    private double sena;
    private double pension;
    private double arl;
    private double cajaCompen;
    private double icbf;
    private double salud;
    private double total;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmseguridadsocnovsControlador
     */
    public FrmseguridadsocnovsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FMR_SEGURIDAD_SOC_NOVS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                llaveRID = (Map<String, Object>) parametrosEntrada.get("ridR");
                tipoContrato = (String) parametrosEntrada.get("tipoContrato");
                numero = (String) parametrosEntrada.get("NUMERO".toLowerCase());
                novedad = (String) parametrosEntrada.get("novedad");
                claseNov = (String) parametrosEntrada.get("claseNov");
                claseOrden = (String) parametrosEntrada.get("claseOrden");
                anio = (String) parametrosEntrada.get("anio");

                valorAPagar = (String) parametrosEntrada.get("valorAPagar");

            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SEGURIDADSOCIALNOV;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);
        parametrosListado.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control txtPlanilla
     * 
     * 
     */
    public void cambiartxtPlanilla() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtPeriodo
     * 
     * 
     */
    public void cambiartxtPeriodo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtIBC
     * 
     * 
     */
    public void cambiartxtIBC() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtSalud
     * 
     * 
     */
    public void cambiartxtSalud() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtPension
     * 
     * 
     */
    public void cambiartxtPension() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtARL
     * 
     * 
     */
    public void cambiartxtARL() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtCajaComp
     * 
     * 
     */
    public void cambiartxtCajaComp() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtSENA
     * 
     * 
     */
    public void cambiartxtSENA() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtICBF
     * 
     * 
     */
    public void cambiartxtICBF() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtVlrTotal
     * 
     * 
     */
    public void cambiartxtVlrTotal() {
        // <CODIGO_DESARROLLADO>
        cambiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtPlanilla en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtPlanillaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtPeriodo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtPeriodoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtIBC en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtIBCC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtSalud en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtSaludC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtPension en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtPensionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtARL en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtARLC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtCajaComp en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtCajaCompC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtSENA en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtSENAC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtICBF en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtICBFC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtVlrTotal en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartxtVlrTotalC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        cambiarCamposC(rowNum);
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void cambiarCampos() {


        sena = Double.parseDouble(SysmanFunciones
                        .nvl((Object) registro.getCampos().get("SENA"), "0").toString());
        pension = Double.parseDouble(SysmanFunciones
                        .nvl((Object) registro.getCampos().get("PENSION"), "0")
                        .toString());
        arl = Double.parseDouble(SysmanFunciones
                        .nvl((Object) registro.getCampos().get("ARL"), "0").toString());
        cajaCompen = Double.parseDouble(SysmanFunciones
                        .nvl((Object) registro.getCampos().get("CAJACOMPEN"), "0")
                        .toString());
        icbf = Double.parseDouble(SysmanFunciones
                        .nvl((Object) registro.getCampos().get("ICBF"), "0").toString());
        salud = Double.parseDouble(SysmanFunciones
                        .nvl((Object) registro.getCampos().get("SALUD"), "0")
                        .toString());

        total = sena + pension + arl + cajaCompen + icbf + salud;

        registro.getCampos().put("TOTALAPORTES", total);
    }

    public String cambiarCamposC(int rowNum) {
    	
    	if(listaInicial.getDatasource().get(rowNum % 10)
                .getCampos().get("SENA").equals("")){
    		 listaInicial.getDatasource().get(rowNum % 10)
             .getCampos().put("SENA",0);
    	}
    	if(listaInicial.getDatasource().get(rowNum % 10)
                .getCampos().get("CAJACOMPEN").equals("")){
    		 listaInicial.getDatasource().get(rowNum % 10)
             .getCampos().put("CAJACOMPEN",0);
    	}
    	if(listaInicial.getDatasource().get(rowNum % 10)
                .getCampos().get("ICBF").equals("")){
    		 listaInicial.getDatasource().get(rowNum % 10)
             .getCampos().put("ICBF",0);
    	}


        sena = Double.parseDouble(SysmanFunciones.nvl(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("SENA"),
                        "0").toString());

        pension = Double.parseDouble(SysmanFunciones.nvl((Object)
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("PENSION"),
                        "0").toString());

        arl = Double.parseDouble(SysmanFunciones.nvl(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("ARL"),
                        "0").toString());

        cajaCompen = Double.parseDouble(SysmanFunciones.nvl(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("CAJACOMPEN"),
                        "0").toString());

        icbf = Double.parseDouble(SysmanFunciones.nvl(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("ICBF"),
                        "0").toString());

        salud = Double.parseDouble(SysmanFunciones.nvl(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("SALUD"),
                        "0").toString());

        total = sena + pension + arl + cajaCompen + icbf + salud;

        return listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TOTALAPORTES", total).toString();

    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2111-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * 'TAR: 1000093049; CBENITEZ; 18/07/2019 formularioAbrir 9,
         * Me.Name '> End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        registro.getCampos().put("ORDENDECOMPRA", numero);
        registro.getCampos().put("NOVEDAD", novedad);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
    }

    public void cerrarFormulario() {

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        rid = (Map<String, Object>) parametrosEntrada.get("rid");
        llaveRID = (Map<String, Object>) parametrosEntrada.get("ridR");
        tipoContrato = (String) parametrosEntrada.get("tipoContrato");
        numero = (String) parametrosEntrada.get("NUMERO".toLowerCase());
        novedad = (String) parametrosEntrada.get("novedad");
        claseNov = (String) parametrosEntrada.get("claseNov");
        claseOrden = (String) parametrosEntrada.get("claseOrden");
        anio = (String) parametrosEntrada.get("anio");

        valorAPagar = (String) parametrosEntrada.get("valorAPagar");

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametrosEntrada);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.SUBNOVEDADCONTRATOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador,
                        SessionUtil.getModulo());
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        rid = (Map<String, Object>) parametrosEntrada.get("rid");
        llaveRID = (Map<String, Object>) parametrosEntrada.get("ridR");
        tipoContrato = (String) parametrosEntrada.get("tipoContrato");
        numero = (String) parametrosEntrada.get("NUMERO".toLowerCase());
        novedad = (String) parametrosEntrada.get("novedad");
        claseNov = (String) parametrosEntrada.get("claseNov");
        claseOrden = (String) parametrosEntrada.get("claseOrden");
        anio = (String) parametrosEntrada.get("anio");

        valorAPagar = (String) parametrosEntrada.get("valorAPagar");

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametrosEntrada);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.SUBNOVEDADCONTRATOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // TODO Auto-generated method stub
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getClaseNov() {
        return claseNov;
    }

    public void setClaseNov(String claseNov) {
        this.claseNov = claseNov;
    }

    public String getTxtNovedad() {
        return txtNovedad;
    }

    public void setTxtNovedad(String txtNovedad) {
        this.txtNovedad = txtNovedad;
    }

    public String getClaseT() {
        return claseT;
    }

    public void setClaseT(String claseT) {
        this.claseT = claseT;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public Map<String, Object> getLlaveRID() {
        return llaveRID;
    }

    public void setLlaveRID(Map<String, Object> llaveRID) {
        this.llaveRID = llaveRID;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getValorAPagar() {
        return valorAPagar;
    }

    public void setValorAPagar(String valorAPagar) {
        this.valorAPagar = valorAPagar;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNovedad() {
        return novedad;
    }

    public void setNovedad(String novedad) {
        this.novedad = novedad;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
