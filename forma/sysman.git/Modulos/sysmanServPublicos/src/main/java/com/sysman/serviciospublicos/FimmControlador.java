/*-
 * FimmControlador.java
 *
 * 1.0
 * 
 * 29/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroRemote;
import com.sysman.serviciospublicos.enums.FimmControladorEnum;
import com.sysman.serviciospublicos.enums.FimmControladorUrlEnum;
import com.sysman.util.SysmanConstantes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma fimm asociada al formulario Resumen de recaudos por concepto. Que permite generar un comprimido con dos archivos planos, uno con los encabezados y otro con los detalles de
 * los datos de los usuarios que tienen deudas de facturas de acueducto y alcantarillado, teniendo en cuenta si se desea facturar o no.
 *
 * @version 1.0, 26/12/2016
 * @author jlramirez
 * @version 2, 23/05/2017
 * @author spina - Se refactoriza para dss, depuracion sonar y ejbs
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class FimmControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo asociado al check del formulario y que es usado para determinar si se factura o no para el usuario seleccionado.
     */
    private boolean facturar;
    /**
     * Atributo asociado al check de la fecha de emisión, que es usado para determinar si es una fecha fija o no.
     */
    private boolean emisionFija;
    /**
     * Atributo asociado al check de la fecha de vencimiento, que es usado para determinar si es una fecha fija o no.
     */
    private boolean vencimientoFijo;
    /**
     * Atributo asociado al número de días que se desea facturar
     */
    private int diasFacturar;
    /**
     * Atributo asociado al número de dias que van a pasar antes del vencimiento de la factura
     */
    private int diasPrimerVenc;
    /**
     * Constante a nivel de clase que almacena el codigo del ciclo en el cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion correspondiente
     */
    private String ciclo;
    /**
     * Atributo asociado al codigo inicial del formulario, usado para la generación de los archivos planos.
     */
    private String codigoInicial;
    /**
     * Atributo encargado de almacenar el codigo final, usado para la generación de los archivos planos.
     */
    private String codigoFinal;
    /**
     * Variable encargada de almacenar temporalmente la fecha seleccionada en el campo Fecha de Emisión del formulario.
     */
    private Date fechaEmision;
    /**
     * Variable encargada de almacenar temporalmente la fecha seleccionada en el campo Fecha de Vencimiento del formulario.
     */
    private Date fechaVencimiento;
    /**
     * Nombre de la constante para el "CODIGORUTA"
     */
    private String codigoRuta;
    /**
     * Variable encargada de almacenar temporalmente el primer día de la vigencia de una factura
     */
    private int diaInicial;
    /**
     * Variable encargada de almacenar temporalmente el último día de vigencia de la factura.
     */
    private int diaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista.
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene la información de los detalles del combo ciclo.
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la información de los detalles del combo código inicial.
     */
    private RegistroDataModelImpl listaCmbCodigoInicial;
    /**
     * Lista que contiene la información de los detalles del combo código final.
     */
    private RegistroDataModelImpl listaCmbCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FimmControlador
     */

    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    EjbServiciosPublicosCuatroRemote ejbServiciosPublicosCuatro;

    public FimmControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FIMM_CONTROLADOR.getCodigo();
            codigoRuta = "CODIGORUTA";
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FimmControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
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
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbCodigoInicial();
        cargarListaCmbCodigoFinal();
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
        codigoInicial = "0";
        codigoFinal = "99999999999";
        fechaEmision = new Date();
        fechaVencimiento = new Date();
        emisionFija = true;
        vencimientoFijo = false;
        facturar = true;
        diasFacturar = 3;
        try
        {
            diaInicial = Integer.parseInt(
                            ejbSysmanUtil.consultarParametro(compania, "FIMM - DIA INICIAL",
                                            String.valueOf(SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS), new Date(), true));

            diaFinal = Integer.parseInt(
                            ejbSysmanUtil.consultarParametro(compania, "FIMM - DIA FINAL",
                                            String.valueOf(SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS), new Date(), true));

            diasPrimerVenc = Integer.parseInt(
                            ejbSysmanUtil.consultarParametro(compania, "FIMM - DIAS PRIMER VENCIMIENTO",
                                            String.valueOf(SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS), new Date(), true));

        }
        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(FimmControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FimmControladorUrlEnum.URL8947.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaCmbCodigoInicial
     */
    public void cargarListaCmbCodigoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FimmControladorUrlEnum.URL9415.getValue());

        listaCmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        codigoRuta);
    }

    /**
     * Carga la lista listaCmbCodigoFinal
     */
    public void cargarListaCmbCodigoFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(FimmControladorEnum.CODIGORUTAINI.getValue(), codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FimmControladorUrlEnum.URL10228.getValue());

        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        codigoRuta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Método que genera un comprimido con dos archivos planos, teniendo en cuenta si se quiere facturar o no.
     */
    public void generarReporte()
    {
        String cabeza = null;
        String cuerpo = null;
        ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
        String[] nombres = new String[2];

        try
        {
            if (facturar)
            {
                cabeza = cargarCabezaNormal();
                cuerpo = cargarDetallesNormal();
            }
            else
            {
                cabeza = ejbServiciosPublicosCuatro.enviarFimmSoloLecturaCabeza(compania, Integer.parseInt(ciclo), diasFacturar,
                                diaInicial,
                                diaFinal, codigoInicial, codigoInicial);

                cuerpo = ejbServiciosPublicosCuatro.enviarFimmSoloLecturaCuerpo(compania, Integer.parseInt(ciclo), codigoInicial,
                                codigoFinal);

            }
            salidas[0] = JsfUtil
                            .serializarPlano(cabeza);
            nombres[0] = "CABEZA.DAT";
            salidas[1] = JsfUtil
                            .serializarPlano(cuerpo);
            nombres[1] = "CUERPO.DAT";
            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salidas, nombres, "ARCHIVOSPLANOS");
        }
        catch (JRException | IOException
                        | DRException | NumberFormatException | SystemException | SQLException e)
        {
            Logger.getLogger(FimmControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Método que permite cargar la cadena de texto correspondiente al archivo plano cabeza generado por la funcion FC_FIMMCABEZA.
     * 
     * @return Clob que contiene la cadena de texto para el archivo cabeza.
     */
    public String cargarCabezaNormal()
    {
        String cabeza = null;
        try
        {
            cabeza = ejbServiciosPublicosCuatro.enviarFimmCabeza(compania, Integer.parseInt(ciclo), diasFacturar, diaInicial, diaFinal,
                            codigoInicial, codigoFinal, fechaEmision, SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(FimmControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return cabeza;
    }

    /**
     * Método que permite cargar la cadena de texto correspondiente al archivo plano cuerpo (detalles) generado por la funcion FC_FIMMCUERPO.
     * 
     * @return Clob que contiene la cadena de texto para el archivo cuerpo (detalles).
     */
    public String cargarDetallesNormal()
    {
        String cuerpo = null;
        try
        {
            cuerpo = ejbServiciosPublicosCuatro.enviarFimmCuerpo(compania, Integer.parseInt(ciclo), diasPrimerVenc, codigoInicial,
                            codigoFinal, fechaEmision, fechaVencimiento, emisionFija, vencimientoFijo, SessionUtil.getUser().getCodigo());
        }

        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(FimmControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return cuerpo;
    }

    /**
     * Metodo ejecutado al oprimir el boton Generar en la vista
     */
    public void oprimirComando34()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     */
    public void oprimirComando35()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     */
    public void cambiarCiclo()
    {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";
        cargarListaCmbCodigoInicial();
        cargarListaCmbCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CmbCodigoInicial
     */
    public void cambiarCmbCodigoInicial()
    {
        // <CODIGO_DESARROLLADO>
        codigoFinal = "";
        cargarListaCmbCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCmbCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoRuta).toString();
        cargarListaCmbCodigoFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCmbCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRuta).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Método que retorna la variable booleana facturar
     * 
     * @return Variable de facturar
     */
    public boolean isFacturar()
    {
        return facturar;
    }

    /**
     * Método que asigna la variable booleana facturar.
     * 
     * @param facturar
     * Variable a asignar en facturar
     */
    public void setFacturar(boolean facturar)
    {
        this.facturar = facturar;
    }

    /**
     * Método que retorna la variable booleana emisionFija
     * 
     * @return Variable de emisionFija
     */
    public boolean isEmisionFija()
    {
        return emisionFija;
    }

    /**
     * Método que asigna la variable booleana emisionFija.
     * 
     * @param emisionFija
     * Variable a asignar en emisionFija
     */
    public void setEmisionFija(boolean emisionFija)
    {
        this.emisionFija = emisionFija;
    }

    /**
     * Método que retorna la variable booleana vencimientoFijo
     * 
     * @return Variable de vencimientoFijo
     */
    public boolean isVencimientoFijo()
    {
        return vencimientoFijo;
    }

    /**
     * Método que asigna la variable booleana vencimientoFijo.
     * 
     * @param vencimientoFijo
     * Variable a asignar en vencimientoFijo
     */
    public void setVencimientoFijo(boolean vencimientoFijo)
    {
        this.vencimientoFijo = vencimientoFijo;
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Asigna la variable diasFacturar
     * 
     * @param diasFacturar
     * Variable a asignar en diasFacturar
     */
    public void setDiasFacturar(int diasFacturar)
    {
        this.diasFacturar = diasFacturar;
    }

    /**
     * Retorna la variable diasFacturar
     * 
     * @return diasFacturar
     */
    public int getDiasFacturar()
    {
        return diasFacturar;
    }

    /**
     * Asigna la variable diasPrimerVenc
     * 
     * @param diasPrimerVenc
     * Variable a asignar en diasPrimerVenc
     */
    public void setDiasPrimerVenc(int diasPrimerVenc)
    {
        this.diasPrimerVenc = diasPrimerVenc;
    }

    /**
     * Retorna la variable diasPrimerVenc
     * 
     * @return diasPrimerVenc
     */
    public int getDiasPrimerVenc()
    {
        return diasPrimerVenc;
    }

    /**
     * Retorna la variable fechaEmision
     * 
     * @return fechaEmision
     */
    public Date getFechaEmision()
    {
        return fechaEmision;
    }

    /**
     * Asigna la variable fechaEmision
     * 
     * @param fechaEmision
     * Variable a asignar en fechaEmision
     */
    public void setFechaEmision(Date fechaEmision)
    {
        this.fechaEmision = fechaEmision;
    }

    /**
     * Retorna la variable fechaVencimiento
     * 
     * @return fechaVencimiento
     */
    public Date getFechaVencimiento()
    {
        return fechaVencimiento;
    }

    /**
     * Asigna la variable fechaVencimiento
     * 
     * @param fechaVencimiento
     * Variable a asignar en fechaVencimiento
     */
    public void setFechaVencimiento(Date fechaVencimiento)
    {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Retorna la variable diaInicial
     * 
     * @return diaInicial
     */
    public int getDiaInicial()
    {
        return diaInicial;
    }

    /**
     * Asigna la variable diaInicial
     * 
     * @param diaInicial
     */
    public void setDiaInicial(int diaInicial)
    {
        this.diaInicial = diaInicial;
    }

    /**
     * Retorna la variable diaFinal
     * 
     * @return diaFinal
     */
    public int getDiaFinal()
    {
        return diaFinal;
    }

    /**
     * Asigna la variable diaFinal
     * 
     * @param diaFinal
     */
    public void setDiaFinal(int diaFinal)
    {
        this.diaFinal = diaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista.
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCmbCodigoInicial
     * 
     * @return listaCmbCodigoInicial
     */
    public RegistroDataModelImpl getListaCmbCodigoInicial()
    {
        return listaCmbCodigoInicial;
    }

    /**
     * Asigna la lista listaCmbCodigoInicial
     * 
     * @param listaCmbCodigoInicial
     * Variable a asignar en listaCmbCodigoInicial
     */
    public void setListaCmbCodigoInicial(
        RegistroDataModelImpl listaCmbCodigoInicial)
    {
        this.listaCmbCodigoInicial = listaCmbCodigoInicial;
    }

    /**
     * Retorna la lista listaCmbCodigoFinal
     * 
     * @return listaCmbCodigoFinal
     */
    public RegistroDataModelImpl getListaCmbCodigoFinal()
    {
        return listaCmbCodigoFinal;
    }

    /**
     * Asigna la lista listaCmbCodigoFinal
     * 
     * @param listaCmbCodigoFinal
     * Variable a asignar en listaCmbCodigoFinal
     */
    public void setListaCmbCodigoFinal(RegistroDataModelImpl listaCmbCodigoFinal)
    {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
