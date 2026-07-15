/*-
 * MantdepuraciondecuponesControlador.java
 *
 * 1.0
 *
 * 13/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.MantdepuraciondecuponesControladorEnum;
import com.sysman.predial.enums.MantdepuraciondecuponesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;

/**
 * Formulario desde el cual se realiza la depuracion de los cupones de pago de acuerdo a la fecha, banco y paquete a los que corresponden los cupones a actualizar. Se accede desde la ruta Panel
 * Principal\Impuesto Predial\Mantenimiento\Depuracion de cupones de pago.
 *
 * @version 1.0, 13/02/2017
 * @author lcortes
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3, 07/07/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class MantdepuraciondecuponesControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que identifica el nombre del campo PAG_BAN
     */
    private final String campoPagBan;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar la fecha inicial seleccionada en el formulario.
     */
    private String fechaInicial;
    /**
     * Atributo que permite identificar el banco inicial seleccionado en el formulario.
     */
    private String bancoInicial;
    /**
     * Atributo que permite identificar el paquete inicial seleccionado en el formulario.
     */
    private String paqueteInicial;
    /**
     * Atributo que permite identificar la fecha final seleccionada en el formulario.
     */
    private String fechaFinal;
    /**
     * Atributo que permite identificar el banco final seleccionado en el formulario.
     */
    private String bancoFinal;
    /**
     * Atributo que permite identificar el paquete final seleccionado en el formulario.
     */
    private String paqueteFinal;
    /**
     * Atributo que permite identificar el nombre correspondiente al banco inicial seleccionado en el formulario.
     */
    private String nombreBanIni;
    /**
     * Atributo que permite identificar el nombre correspondiente al banco final seleccionado en el formulario.
     */
    private String nombreBanFin;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de las fechas de registro de los pagos registrados.
     */
    private List<Registro> listaFechaInicial;
    /**
     * Lista de los codigos de paquete que se encuentran relacionados con las fechas y bancos seleccionados.
     */
    private List<Registro> listaPaqueteInicial;
    /**
     * Lista de las fechas de registro de los pagos registrados.
     */
    private List<Registro> listaFechaFinal;
    /**
     * Lista de los codigos de paquete que se encuentran relacionados con las fechas y bancos seleccionados.
     */
    private List<Registro> listaPaqueteFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de los bancos en los cuales se registraron pagos en las fechas seleccionadas.
     */
    private RegistroDataModelImpl listaBancoInicial;
    /**
     * Lista de los bancos en los cuales se registraron pagos en las fechas seleccionadas.
     */
    private RegistroDataModelImpl listaBancoFinal;
    
    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatroRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de MantdepuraciondecuponesControlador
     */
    public MantdepuraciondecuponesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        campoPagBan = "PAG_BAN";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.MANTDEPURACIONDECUPONES_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaFechaInicial();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaFechaInicial
     *
     */
    public void cargarListaFechaInicial()
    {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaFechaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MantdepuraciondecuponesControladorUrlEnum.URL6262
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPaqueteInicial
     *
     */
    public void cargarListaPaqueteInicial()
    {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.FECHAINICIAL.getName(),fechaInicial);
            param.put(GeneralParameterEnum.FECHAFINAL.getName(),fechaFinal);
            param.put(MantdepuraciondecuponesControladorEnum.PARAM0.getValue(),bancoInicial);
            param.put(MantdepuraciondecuponesControladorEnum.PARAM1.getValue(),bancoFinal);
            
            listaPaqueteInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MantdepuraciondecuponesControladorUrlEnum.URL7087
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaFechaFinal
     *
     */
    public void cargarListaFechaFinal()
    {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.FECHAINICIAL.getName(),fechaInicial);
            
            listaFechaFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MantdepuraciondecuponesControladorUrlEnum.URL7848
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPaqueteFinal
     *
     */
    public void cargarListaPaqueteFinal()
    {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.FECHAINICIAL.getName(),fechaInicial);
            param.put(GeneralParameterEnum.FECHAFINAL.getName(),fechaFinal);
            param.put(MantdepuraciondecuponesControladorEnum.PARAM0.getValue(),bancoInicial);
            param.put(MantdepuraciondecuponesControladorEnum.PARAM1.getValue(),bancoFinal);
            param.put(MantdepuraciondecuponesControladorEnum.PARAM2.getValue(),paqueteInicial);
            
            listaPaqueteFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MantdepuraciondecuponesControladorUrlEnum.URL8749
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaBancoInicial
     *
     */
    public void cargarListaBancoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(MantdepuraciondecuponesControladorUrlEnum.URL9618.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(),fechaFinal);

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, campoPagBan);
    }

    /**
     *
     * Carga la lista listaBancoFinal
     *
     */
    public void cargarListaBancoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(MantdepuraciondecuponesControladorUrlEnum.URL10969.getValue());       
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(MantdepuraciondecuponesControladorEnum.PARAM0.getValue(),bancoInicial);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(),fechaFinal);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, campoPagBan);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista. Permite ejecutar la funcion FC_EJECUTARDEPURACIONCUPONES, en la cual se realiza la depuracion de los cupones.
     *
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        if (!validarCampos() || !validarFechas())
        {
            return;
        }
        try
        {
            String rta = ejbPredialCuatroRemote.arreglarCupones(compania,
                            fechaInicial, fechaFinal, bancoInicial, bancoFinal,
                            paqueteInicial, paqueteFinal, nombreBanIni,
                            SessionUtil.getUser().getCodigo());
                            
            if (("NO").equals(rta))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2819"));
            }
            else if (("OK").equals(rta))
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(MantdepuraciondecuponesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite validar si alguno de los campos del formulario no ha sido ingresado.
     *
     * @return true si los campos han sido ingresados completamente.
     */
    private boolean validarCampos()
    {
        if (SysmanFunciones.validarVariableVacio(fechaInicial)
            || SysmanFunciones.validarVariableVacio(fechaFinal)
            || SysmanFunciones.validarVariableVacio(bancoInicial)
            || SysmanFunciones.validarVariableVacio(bancoFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2818"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(paqueteInicial)
            || SysmanFunciones.validarVariableVacio(paqueteFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2818"));
            return false;
        }
        return true;
    }

    private boolean validarFechas()
    {
        try
        {
            Date fechaIni = SysmanFunciones.convertirAFecha(fechaInicial);
            Date fechaFin = SysmanFunciones.convertirAFecha(fechaFinal);
            if (fechaIni.after(fechaFin))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
                fechaFinal = null;
                return false;
            }
        }
        catch (ParseException e)
        {
            Logger.getLogger(MantdepuraciondecuponesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     *
     *
     */
    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        fechaFinal = null;
        bancoInicial = null;
        nombreBanIni = null;
        bancoFinal = null;
        nombreBanFin = null;
        paqueteInicial = null;
        paqueteFinal = null;
        listaBancoInicial = null;
        listaBancoFinal = null;
        listaPaqueteInicial = null;
        listaPaqueteFinal = null;
        cargarListaFechaFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     *
     *
     */
    public void cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
        bancoInicial = null;
        nombreBanIni = null;
        bancoFinal = null;
        nombreBanFin = null;
        paqueteInicial = null;
        paqueteFinal = null;
        listaBancoFinal = null;
        listaPaqueteInicial = null;
        listaPaqueteFinal = null;
        cargarListaBancoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PaqueteInicial
     *
     *
     */
    public void cambiarPaqueteInicial()
    {
        // <CODIGO_DESARROLLADO>
        paqueteFinal = null;
        cargarListaPaqueteFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaBancoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos().get(campoPagBan).toString();
        nombreBanIni = registroAux.getCampos().get("NOMBREBANCO").toString();
        nombreBanFin = null;
        paqueteInicial = null;
        paqueteFinal = null;
        listaPaqueteInicial = null;
        listaPaqueteFinal = null;
        cargarListaBancoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaBancoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos().get(campoPagBan).toString();
        nombreBanFin = registroAux.getCampos().get("NOMBREBANCO").toString();
        paqueteInicial = null;
        paqueteFinal = null;
        listaPaqueteFinal = null;
        cargarListaPaqueteInicial();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public String getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(String fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable bancoInicial
     *
     * @return bancoInicial
     */
    public String getBancoInicial()
    {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     *
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial)
    {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable paqueteInicial
     *
     * @return paqueteInicial
     */
    public String getPaqueteInicial()
    {
        return paqueteInicial;
    }

    /**
     * Asigna la variable paqueteInicial
     *
     * @param paqueteInicial
     * Variable a asignar en paqueteInicial
     */
    public void setPaqueteInicial(String paqueteInicial)
    {
        this.paqueteInicial = paqueteInicial;
    }

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public String getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(String fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable bancoFinal
     *
     * @return bancoFinal
     */
    public String getBancoFinal()
    {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     *
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal)
    {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Retorna la variable paqueteFinal
     *
     * @return paqueteFinal
     */
    public String getPaqueteFinal()
    {
        return paqueteFinal;
    }

    /**
     * Asigna la variable paqueteFinal
     *
     * @param paqueteFinal
     * Variable a asignar en paqueteFinal
     */
    public void setPaqueteFinal(String paqueteFinal)
    {
        this.paqueteFinal = paqueteFinal;
    }

    /**
     * Retorna la variable nombreBanIni
     *
     * @return nombreBanIni
     */
    public String getNombreBanIni()
    {
        return nombreBanIni;
    }

    /**
     * Asigna la variable nombreBanIni
     *
     * @param nombreBanIni
     * Variable a asignar en nombreBanIni
     */
    public void setNombreBanIni(String nombreBanIni)
    {
        this.nombreBanIni = nombreBanIni;
    }

    /**
     * Retorna la variable nombreBanFin
     *
     * @return nombreBanFin
     */
    public String getNombreBanFin()
    {
        return nombreBanFin;
    }

    /**
     * Asigna la variable nombreBanFin
     *
     * @param nombreBanFin
     * Variable a asignar en nombreBanFin
     */
    public void setNombreBanFin(String nombreBanFin)
    {
        this.nombreBanFin = nombreBanFin;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaFechaInicial
     *
     * @return listaFechaInicial
     */
    public List<Registro> getListaFechaInicial()
    {
        return listaFechaInicial;
    }

    /**
     * Asigna la lista listaFechaInicial
     *
     * @param listaFechaInicial
     * Variable a asignar en listaFechaInicial
     */
    public void setListaFechaInicial(List<Registro> listaFechaInicial)
    {
        this.listaFechaInicial = listaFechaInicial;
    }

    /**
     * Retorna la lista listaPaqueteInicial
     *
     * @return listaPaqueteInicial
     */
    public List<Registro> getListaPaqueteInicial()
    {
        return listaPaqueteInicial;
    }

    /**
     * Asigna la lista listaPaqueteInicial
     *
     * @param listaPaqueteInicial
     * Variable a asignar en listaPaqueteInicial
     */
    public void setListaPaqueteInicial(List<Registro> listaPaqueteInicial)
    {
        this.listaPaqueteInicial = listaPaqueteInicial;
    }

    /**
     * Retorna la lista listaFechaFinal
     *
     * @return listaFechaFinal
     */
    public List<Registro> getListaFechaFinal()
    {
        return listaFechaFinal;
    }

    /**
     * Asigna la lista listaFechaFinal
     *
     * @param listaFechaFinal
     * Variable a asignar en listaFechaFinal
     */
    public void setListaFechaFinal(List<Registro> listaFechaFinal)
    {
        this.listaFechaFinal = listaFechaFinal;
    }

    /**
     * Retorna la lista listaPaqueteFinal
     *
     * @return listaPaqueteFinal
     */
    public List<Registro> getListaPaqueteFinal()
    {
        return listaPaqueteFinal;
    }

    /**
     * Asigna la lista listaPaqueteFinal
     *
     * @param listaPaqueteFinal
     * Variable a asignar en listaPaqueteFinal
     */
    public void setListaPaqueteFinal(List<Registro> listaPaqueteFinal)
    {
        this.listaPaqueteFinal = listaPaqueteFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
   
    public RegistroDataModelImpl getListaBancoInicial() {
        return listaBancoInicial;
    }

    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial) {
        this.listaBancoInicial = listaBancoInicial;
    }

    public RegistroDataModelImpl getListaBancoFinal() {
        return listaBancoFinal;
    }

    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal) {
        this.listaBancoFinal = listaBancoFinal;
    }

    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
