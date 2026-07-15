/*-
 * FrmelegiblesubControlador.java
 *
 * 1.0
 *
 * 30/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmelegiblesubControladorEnum;
import com.sysman.hojasdevida.enums.FrmelegiblesubControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Permite consultar los elegibles
 *
 * @version 1.0, 30/01/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class FrmelegiblesubControlador extends BeanBaseContinuoAcmeImpl {
    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * codigo de la convocatoria recibido del formulario principal
     */
    private String convocatoria;

    private String cNumeroCon = FrmelegiblesubControladorEnum.NO_CONVOCATORIA
                    .getValue();

    private String cSucursal = GeneralParameterEnum.SUCURSAL.getName();

    private String cNumeroDcto;

    private String modulo;

    private int indice;
    /**
     * Esta variable almacena el nombre del cargo
     */
    private String nombreCargo;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    /**
     * Mensaje enviado correctamente
     */
    private String mensajeCorrectoAprobados;
    private String mensajeCorrectoRechazadas;

    /**
     * Mensaje NO enviado
     */
    private String mensajeIncorrecto;
    /**
     * Variable para almacenar los correos destino
     */
    private String correosDestinoAprobados;
    private String correosDestinoRechazados;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmelegiblesubControlador
     */
    public FrmelegiblesubControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        mensajeCorrectoAprobados = "Alerta de email enviada correctamente a las personas APROBADAS.";
        mensajeCorrectoRechazadas = "Alerta de email enviada correctamente a las personas RECHAZADAS.";
        mensajeIncorrecto = "No se pudo enviar la alerta de email.";
        correosDestinoAprobados = "";
        correosDestinoRechazados = "";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMELEGIBLESUB_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            convocatoria = SysmanFunciones
                            .nvl(parametrosEntrada.get("convocatoria"), "")
                            .toString();

            nombreCargo = SysmanFunciones
                            .nvl(parametrosEntrada.get("cargo"), "")
                            .toString();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        tabla = "NAT_ELEGIBLES";
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

        abrirFormulario();

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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        Date fecha = null;
        Map<String, Object> parametrosFecha = new TreeMap<>();
        parametrosFecha.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosFecha.put("NRO_CONVOCATORIA", convocatoria);

        Registro rsFechaEnvio;
        try {
            rsFechaEnvio = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID("953008")
                                            .getUrl(), parametrosFecha));

            if (rsFechaEnvio != null) {
                fecha = (Date) rsFechaEnvio.getCampos().get("FECHA_ENVIO_CORREO");
            }
            else {
                fecha = null;
            }
            registro.getCampos().put("FECHA_ENVIO_CORREO", fecha);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public String getConvocatoria() {
        return convocatoria;
    }

    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(FrmelegiblesubControladorUrlEnum.URL4133
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getUrlBeanById(
                        FrmelegiblesubControladorUrlEnum.URL4132.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(FrmelegiblesubControladorEnum.NO_CONVOCATORIA
                        .getValue(), convocatoria);
        // <CODIGO_DESARROLLADO>

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Anexos
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirAnexos(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();

        param.put("nroConvocatoria",
                        reg.getCampos().get("NRO_CONVOCATORIA").toString());

        String numeroDcto = reg.getCampos().get("NUMERO_DCTO").toString();

        param.put("numeroDocumento", numeroDcto);

        param.put("sucursal", reg.getCampos().get("SUCURSAL").toString());

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_DETALLE_DOCUMENTO_lISTELEGIBLE_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnEnviar en la vista
     *
     */
    public void oprimirbtnEnviar() {
        try {
            /**
             * Solo para correos que aprobaron
             */
            correosDestinoAprobados = obtenerCorreosDestino("-1") == null ? "" : obtenerCorreosDestino("-1");

            Map<String, Object> remplazosDescripcion = new TreeMap<>();
            remplazosDescripcion.put("cargo", nombreCargo);
            remplazosDescripcion.put("nroConvocatoria", convocatoria);

            if (!correosDestinoAprobados.isEmpty()) {

                Map<String, Object> paramEnvioAprobados = new TreeMap<>();
                paramEnvioAprobados.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                paramEnvioAprobados.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                "18");

                Registro rsEmailAprobados = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                "1663003")
                                                .getUrl(),
                                                paramEnvioAprobados));

                if (rsEmailAprobados != null) {

                    String descripcionFinal = remplazarVariable(
                                    rsEmailAprobados.getCampos().get(
                                                    GeneralParameterEnum.DESCRIPCION
                                                                    .getName())
                                                    .toString(),
                                    remplazosDescripcion);

                    EmailPojo emailAprobados = new EmailPojo();
                    emailAprobados.setFrom(rsEmailAprobados.getCampos().get("ORIGEN")
                                    .toString());
                    emailAprobados.setTo(correosDestinoAprobados);
                    emailAprobados.setSubject(rsEmailAprobados.getCampos().get("ASUNTO").toString());
                    emailAprobados.setBody(descripcionFinal);

                    ApiRestClient client = new ApiRestClient();
                    client.postClient(emailAprobados);

                    registro.getCampos().put("FECHA_ENVIO_CORREO", new Date());

                    ejbHojasDeVidaCero.actualizarEnvioCorreos(compania, convocatoria, 6);
                    JsfUtil.agregarMensajeInformativo(mensajeCorrectoAprobados);

                }
                else {
                    JsfUtil.agregarMensajeAlerta(
                                    "No existen personas APROBADAS por lo que no se realizó envio de aprobación.");
                }

            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                "No existen personas APROBADAS por lo que no se realizó envio de aprobación.");
            }

            /**
             * solo envía al correo a las personas que tengan indicador Acepto en "NO"
             */
            correosDestinoRechazados = obtenerCorreosDestino("0") == null ? "" : obtenerCorreosDestino("0");

            if (!correosDestinoRechazados.isEmpty()) {

                Map<String, Object> paramEnvioRechazados = new TreeMap<>();
                paramEnvioRechazados.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                paramEnvioRechazados.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                "17");

                Registro rsEmailRechazados = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                "1663003")
                                                .getUrl(),
                                                paramEnvioRechazados));

                if (rsEmailRechazados != null) {

                    String descripcionFinal = remplazarVariable(
                                    rsEmailRechazados.getCampos().get(
                                                    GeneralParameterEnum.DESCRIPCION
                                                                    .getName())
                                                    .toString(),
                                    remplazosDescripcion);

                    EmailPojo emailRechazados = new EmailPojo();
                    emailRechazados.setFrom(rsEmailRechazados.getCampos().get("ORIGEN")
                                    .toString());
                    emailRechazados.setTo(correosDestinoRechazados);
                    emailRechazados.setSubject(rsEmailRechazados.getCampos().get("ASUNTO").toString());
                    emailRechazados.setBody(descripcionFinal);

                    ApiRestClient client = new ApiRestClient();
                    client.postClient(emailRechazados);

                    registro.getCampos().put("FECHA_ENVIO_CORREO", new Date());

                    ejbHojasDeVidaCero.actualizarEnvioCorreos(compania, convocatoria, 6);

                    JsfUtil.agregarMensajeInformativo(mensajeCorrectoRechazadas);
                }
                else {
                    JsfUtil.agregarMensajeAlerta(
                                    "No existen personas RECHAZADAS por lo que no se realizó envio de rechazo.");
                }

            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                "No existen personas RECHAZADAS por lo que no se realizó envio de rechazo.");
            }

        }
        catch (SystemException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo obtiene los correos de las personas que fueron aprobadas y rechazados
     */
    public String obtenerCorreosDestino(String aprobados) {
        String salida = "";

        Map<String, Object> parametrosDestinos = new TreeMap<>();
        parametrosDestinos.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosDestinos.put("NRO_CONVOCATORIA",
                        convocatoria);
        parametrosDestinos.put("ACEPTO",
                        aprobados);

        try {
            Registro rsListadoCorreos = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID("953007")
                                            .getUrl(), parametrosDestinos));

            if (rsListadoCorreos != null) {
                salida = SysmanFunciones.toString(
                                rsListadoCorreos.getCampos().get("CORREOS_DESTINOS"));
            }
            else {
                salida = "";
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return salida;

    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public String remplazarVariable(String descripcion,
                    Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(
                            SysmanFunciones.concatenar("s$", entry.getKey(),
                                            "$s"),
                            SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    /**
     * Recibe la variable indice
     * 
     * @return indice
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
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
