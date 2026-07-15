/*-
 * FrmEgretrimestreControlador.java
 *
 * 1.0
 * 
 * 06/11/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmEgretrimestreControladorUrlEnum;
import com.sysman.presupuesto.enums.LisanualpacpagosControladorEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 06/11/2020
 * @author mavargas
 * 
 * @version 2.0,09/03/2021
 * @author avega-Se refactoriza y conexiones de igual forma se documenta y ajusta las alertas de sonar.
 * 
 */
@ManagedBean
@ViewScoped
public class FrmEgretrimestreControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;

    private int trimestre;

    private String cuentaInicial;

    private String cuentaFinal;

    private int anio;

    private int mesInicial;

    private int mesFinal;

    private int mesIntermedio;

    private String nombreCuentaInicial;

    private String nombreCuentaFinal;

    private String nom;
    private final String cod;

    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * variable que almacena la lista ano
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * variable que almmacena la lisya cuenta Inicial
     */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmEgretrimestreControlador
     */
    public FrmEgretrimestreControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        anio = SysmanFunciones.ano(new Date());
        cod = GeneralParameterEnum.CODIGO.getName();
        nom = GeneralParameterEnum.NOMBRE.getName();
        try
        {
            // 2207
            numFormulario = GeneralCodigoFormaEnum.FRM_EGRETRIMESTRE_CONTROLADOR.getCodigo();
            validarPermisos();

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como son
     * tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
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
        /*
         * FR2207-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEgretrimestreControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEgretrimestreControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEgretrimestreControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LisanualpacpagosControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton pdf en la vista
     *
     *
     */
    public void oprimirpdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        if (trimestre == 1)
        {
            mesInicial = 1;
            mesFinal = 3;
        }
        else if (trimestre == 2)
        {
            mesInicial = 4;
            mesFinal = 6;
        }
        else if (trimestre == 3)
        {
            mesInicial = 7;
            mesFinal = 9;
        }
        else if (trimestre == 4)
        {
            mesInicial = 10;
            mesFinal = 12;
        }

        mesIntermedio = mesInicial + 1;

        try
        {
            String reporte = "002142INFEGRETRIMESTREP";
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("trimestre", trimestre);
            reemplazar.put("anio", anio);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("mesIntermedio", mesIntermedio);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CUENTAINICIAL", (cuentaInicial));
            parametros.put("PR_CUENTAFINAL", (cuentaFinal));
            parametros.put("PR_FORMS_FRMINGRETRIMESTRE_ANO", (anio));
            parametros.put("PR_FORMS_FRMINGRETRIMESTRE_TRIMESTRE", (trimestre));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_MESINICIAL", (mesInicial));
            parametros.put("PR_MESFINAL", (mesFinal));
            parametros.put("PR_MESINTERMEDIO", (mesIntermedio));
            parametros.put("PR_DEPARTAMENTOCOMPANIA", SessionUtil.getCompaniaIngreso().getDepartamento());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                            + ex.getMessage());
            Logger.getLogger(LisanualpacpagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void cambiarAno()
    {
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        nombreCuentaInicial = registroAux.getCampos().get(nom).toString();
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreCuentaFinal = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
        nombreCuentaFinal = registroAux.getCampos().get(nom).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public int getTrimestre()
    {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(int trimestre)
    {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public int getMesInicial()
    {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(int mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public int getMesFinal()
    {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(int mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable nombreCuentaInicial
     * 
     * @return nombreCuentaInicial
     */
    public String getNombreCuentaInicial()
    {
        return nombreCuentaInicial;
    }

    /**
     * Asigna la variable nombreCuentaInicial
     * 
     * @param nombreCuentaInicial
     * Variable a asignar en nombreCuentaInicial
     */
    public void setNombreCuentaInicial(String nombreCuentaInicial)
    {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    /**
     * Retorna la variable nombreCuentaFinal
     * 
     * @return nombreCuentaFinal
     */
    public String getNombreCuentaFinal()
    {
        return nombreCuentaFinal;
    }

    /**
     * Asigna la variable nombreCuentaFinal
     * 
     * @param nombreCuentaFinal
     * Variable a asignar en nombreCuentaFinal
     */
    public void setNombreCuentaFinal(String nombreCuentaFinal)
    {
        this.nombreCuentaFinal = nombreCuentaFinal;
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
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    public int getMesIntermedio()
    {
        return mesIntermedio;
    }

    public void setMesIntermedio(int mesIntermedio)
    {
        this.mesIntermedio = mesIntermedio;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getCod()
    {
        return cod;
    }
}
