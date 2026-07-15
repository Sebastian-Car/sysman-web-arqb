/*-
 * InformePagosDoblesControlador.java
 *
 * 1.0
 * 
 * 02/11/2016
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.InformePagosDoblesControladorEnum;
import com.sysman.serviciospublicos.enums.InformePagosDoblesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario Infome de Pagos Dobles.
 *
 * @version 1.0, 02/11/2016
 * @author Pablo Andr�s Espitia Cuca
 * @version 2.0, 05/06/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejbs
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class InformePagosDoblesControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Constante a nivel de clase que almacena el valor de una palabra replicada.
     */
    private final String codigo;

    /**
     * Atributo que gestiona el valor de la casilla separar por banco.
     */
    private boolean separarBanco;

    /**
     * Atributo que gestiona el valor de la casilla separar por usuario.
     */
    private boolean separarUsuario;

    /**
     * Atributo que gestiona el valor ingresado en el combo ciclo.
     */
    private String ciclo;

    /**
     * Atributo que gestiona el valor ingresado en el combo banco inicial.
     */
    private String bancoInicial;

    /**
     * Atributo que gestiona el valor ingresado en el combo banco final.
     */
    private String bancoFinal;

    /**
     * Atributo que controla el valor ingresado en el campo de fecha inicial del formulario.
     */
    private Date fechaInicial;

    /**
     * Atributo que controla el valor ingresado en el campo de fecha final del formulario.
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /** Lista que contiene los detalles del combo ciclo. */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que contiene los detalles del combo banco inicial. */
    private RegistroDataModelImpl listaBancoInicial;

    /** Lista que contiene los detalles del combo banco final. */
    private RegistroDataModelImpl listaBancoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformePagosDoblesControlador
     */
    public InformePagosDoblesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        codigo = "CODIGO";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORME_PAGOS_DOBLES_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            ciclo = "T";
            fechaInicial = fechaFinal = new Date();
            separarUsuario = true;

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
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBancoInicial();
        cargarListaBancoFinal();
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
    /** Carga la lista del combo ciclo. */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaCiclo = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformePagosDoblesControladorUrlEnum.URL4922.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista del combo banco inicial. */
    public void cargarListaBancoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformePagosDoblesControladorUrlEnum.URL4948.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /** Carga la lista del combo banco final. */
    public void cargarListaBancoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformePagosDoblesControladorUrlEnum.URL4949.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformePagosDoblesControladorEnum.BANCOINICIAL.getValue(), bancoInicial);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf en la vista. Gestiona los eventos del mismo.
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista. Gestiona los eventos del mismo.
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato)
    {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            /*
             * Determina si deben ordenar los recaudos por fecha y hora.
             */
            boolean recaudo = "SI".equals(
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtilRemote.consultarParametro(compania,
                                                            "INFORMES DE RECAUDO ORDENADOS POR FECHA Y HORA", SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO"));

            String reporte = seleccionarReporte(recaudo);

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("fechaInicial", formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", formatearFecha(fechaFinal));
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_FECHAINICIAL",
                            convertirFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL", convertirFechaCadena(fechaFinal));
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        
        
    }

    /**
     * Asigna el formato TO_DATE(DD/MM/YYYY) a la fecha ingresada por parametro.
     * 
     * @param fechaDate
     * Variable a la cual se le hace el casting.
     * @return La fecha formateada.
     */
    public String formatearFecha(Date fechaDate)
    {
        return SysmanFunciones.formatearFecha(fechaDate);
    }

    /**
     * Parsea la fecha que ingresa por parametro de tipo Date a tipo String.
     * 
     * @param fechaDate
     * Variable de tipo Date a la cual se le va a hacer el casting.
     * @return La fecha cadena en formato DD/MM/YYYY.
     */
    public String convertirFechaCadena(Date fechaDate)
    {
        String fechaStr = " ";
        try
        {
            fechaStr = SysmanFunciones.convertirAFechaCadena(fechaDate);
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return fechaStr;
    }

    /**
     * Determina cual consulta y reporte se deben generar.
     * 
     * @param key
     * Parametro que establece si el reporte debe ordenar por fecha y hora el listado de recaudos.
     * @return El reporte y consulta a generar.
     */
    public String seleccionarReporte(boolean key)
    {
        String reporte;

        if (separarUsuario)
        {
            reporte = key ? "001204RptPagosDoblesUsuarioFECHA"
                : "001211RptPagosDoblesUsuario";
        }
        else
        {
            reporte = separarBanco ? "001212RptPagosDoblesBancos"
                : "001214RptPagosDobles";
        }

        return reporte;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar la casilla de verificacion Separar por usuario.
     */
    public void cambiarSepararUsuario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista del combo Banco inicial.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaBancoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos().get(codigo).toString();
        bancoFinal = null;
        cargarListaBancoFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista del combo Banco final.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaBancoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos().get(codigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable separarBanco
     * 
     * @return separarBanco
     */
    public boolean getSepararBanco()
    {
        return separarBanco;
    }

    /**
     * Asigna la variable separarBanco
     * 
     * @param separarBanco
     * Variable a asignar en separarBanco
     */
    public void setSepararBanco(boolean separarBanco)
    {
        this.separarBanco = separarBanco;
    }

    public boolean isSepararUsuario()
    {
        return separarUsuario;
    }

    public void setSepararUsuario(boolean separarUsuario)
    {
        this.separarUsuario = separarUsuario;
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
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
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
     * Retorna la lista listaBancoInicial
     * 
     * @return listaBancoInicial
     */
    public RegistroDataModelImpl getListaBancoInicial()
    {
        return listaBancoInicial;
    }

    /**
     * Asigna la lista listaBancoInicial
     * 
     * @param listaBancoInicial
     * Variable a asignar en listaBancoInicial
     */
    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial)
    {
        this.listaBancoInicial = listaBancoInicial;
    }

    /**
     * Retorna la lista listaBancoFinal
     * 
     * @return listaBancoFinal
     */
    public RegistroDataModelImpl getListaBancoFinal()
    {
        return listaBancoFinal;
    }

    /**
     * Asigna la lista listaBancoFinal
     * 
     * @param listaBancoFinal
     * Variable a asignar en listaBancoFinal
     */
    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal)
    {
        this.listaBancoFinal = listaBancoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
