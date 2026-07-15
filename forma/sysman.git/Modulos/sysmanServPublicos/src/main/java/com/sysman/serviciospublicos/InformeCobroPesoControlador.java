/*-
 * InformeCobroPesoControlador.java
 *
 * 1.0
 * 
 * 26/10/2016
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.InformeCobroPesoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario Resolucion 351 en
 * Access "informe_res351", el cual es llamado desde
 * Facturacion\Informes\Generales\Informe Resoluci�n 351
 *
 * @version 1.0, 26/10/2016
 * @author amonroy
 * @version 2.0, 05/06/2017
 * @author jcrodriguez=> Depuracion del controlador,Refactoring y creacion de Dss
 */
@ManagedBean
@ViewScoped

public class InformeCobroPesoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo de servicios
     * publicos
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar si se ha seleccionado la
     * opcion de Pequeno Productor
     */
    private boolean pequenoProd;
    /**
     * Atributo que permite identificar si se ha seleccionado la
     * opcion de Gran Productor
     */
    private boolean granProd;
    /**
     * Atributo que almacena el ciclo seleccionado en el formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor de peso aseo definido en el
     * formulario
     */
    private String pesoAseo;

    /**
     * Atributo que define si se bloquea o no el campo de Peso Aseo
     */
    private boolean bloqueado;
    /**
     * Atributo que almacenael valor del par�metro
     * "PESO ASEO EN TONELADAS"
     */
    private boolean pesoToneladas;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Listado de registros para el comboBox de ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * EJB
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    /**
     * Crea una nueva instancia de InformeCobroPesoControlador
     */
    public InformeCobroPesoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_COBRO_PESO_CONTROLADOR.getCodigo();
            validarPermisos();  
            pesoAseo = "1";      
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }
    private String getParametro(String nombre,boolean indMayus){
        try {
            return ejbSysmanUtilRemote.consultarParametro(compania,
                            nombre,
                            SessionUtil.getModulo(),
                            new Date(),
                            indMayus);
        }
        catch (SystemException e) {       
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
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
        cargarListaCiclo();      
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean padre
        pesoToneladas = ("SI").equalsIgnoreCase(SysmanFunciones.nvl(
                        getParametro("PESO ASEO EN TONELADAS",true),
                        "NO").toString()); 
    }
    /**
     * Realiza la carga del listado de registros para el comboBox que
     * permite seleccionar el Ciclo
     */
    public void cargarListaCiclo() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformeCobroPesoControladorUrlEnum.URL5557.getValue()).getUrl(), param));
        }
        catch (SystemException e) {      
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /*
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato PDF
     */
    public void oprimirBtnPdf() {  
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    /*
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato EXCEL
     */
    public void oprimirBtnExcel() {  
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
    }


    /*
     * Se actualiza el valor del campo Peso Aseo y se verifica que
     * solo un chek est� seleccionado, se ejecuta al seleccionar el
     * check de Pequeno Productor
     */
    public void cambiarPequenoProd() {
        if (pequenoProd) {
            granProd = false;
            pesoAseo = "0";
            bloqueado = true;
        }
        else {
            bloqueado = false;
        }

    }

    /*
     * Se actualiza el valor del campo Peso Aseo y se verifica que
     * solo un chek est� seleccionado, se ejecuta al seleccionar el
     * check de Gran Productor
     */
    public void cambiarGranProd() {
        if (granProd) {
            pequenoProd = false;
            pesoAseo = "0";
            bloqueado = true;
        }
        else {
            bloqueado = false;
        }

    }

    /**
     * Define el tipo de productor de acuerdo a los checks
     * selecionados en el formulario, dependiendo el tipo de productor
     * cambia el campo TIPOPRODUCTOR en la consulta del reporte
     * 
     * @return Tipo de productor
     */
    public int definirTipoProductor() {
        int productor;
        if (pequenoProd) {
            productor = 1;
        }
        else if (granProd) {
            productor = 2;
        }
        else {
            productor = 3;
        }

        return productor;
    }

    /**
     * Define parte de la condicion para la consulta del reporte que
     * se genera
     * 
     * @return condicion para el campo PESOASEO de acuerdo a las
     * opciones seleccionadas en el reporte
     */
    public String definirCondicionPesoAseo() {
        int productor = definirTipoProductor();
        String condicionPesoAseo;
        if (productor == 1) {
            condicionPesoAseo = pesoToneladas
                            ? "USUARIO.PESOASEO <= 0.25"
                                : "(USUARIO.PESOASEO * 0.25) <= 0.25";
        }
        else if (productor == 2) {
            condicionPesoAseo = pesoToneladas
                            ? "USUARIO.PESOASEO > 0.25"
                                : "(USUARIO.PESOASEO * 0.25) > 0.25";
        }
        else {
            condicionPesoAseo = "USUARIO.PESOASEO >= " + pesoAseo;
        }
        return condicionPesoAseo;
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y env�a los
     * par�metros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        String reporte="001175Resolucion351";

        try {
            // Definicion de los valores que se enviaran por parametro
            int productor = definirTipoProductor();
            String condicionPesoAseo = definirCondicionPesoAseo();

            String condicionCiclo = ("T").equalsIgnoreCase(ciclo)
                            ? "USUARIO.CICLO"
                                : ciclo;
            String parToneladas = pesoToneladas
                            ? "SI"
                                : "NO";
            String parCiclo = ("T").equalsIgnoreCase(ciclo)
                            ? "Todos"
                                : ciclo;


            // HashMap reemplazar es para que reemplace en la
            // consulta almacenada en la tabla CONSULTAS
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("parToneladas", parToneladas);
            reemplazar.put("productor", productor);
            reemplazar.put("condicionPesoAseo", condicionPesoAseo);
            reemplazar.put("condicionCiclo", condicionCiclo);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", parCiclo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(InformeCobroPesoControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException ex) {
            Logger.getLogger(InformeCobroPesoControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {     
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }       

        JsfUtil.agregarMensajeError(idioma.getString("TB_TB1750"));

    }
    /**
     * Retorna la variable pequenoProd
     * 
     * @return pequenoProd
     */
    public boolean getPequenoProd() {
        return pequenoProd;
    }

    /**
     * Retorna la variable granProd
     * 
     * @return granProd
     */
    public boolean getGranProd() {
        return granProd;
    }

    /**
     * Asigna la variable pequenoProd
     * 
     * @param pequenoProd
     * Variable a asignar en pequenoProd
     */
    public void setPequenoProd(boolean pequenoProd) {
        this.pequenoProd = pequenoProd;
    }

    /**
     * Asigna la variable granProd
     * 
     * @param granProd
     * Variable a asignar en granProd
     */
    public void setGranProd(boolean granProd) {
        this.granProd = granProd;
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable pesoAseo
     * 
     * @return pesoAseo
     */
    public String getPesoAseo() {
        return pesoAseo;
    }

    /**
     * Asigna la variable pesoAseo
     * 
     * @param pesoAseo
     * Variable a asignar en pesoAseo
     */
    public void setPesoAseo(String pesoAseo) {
        this.pesoAseo = pesoAseo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable bloqueado
     * 
     * @return
     */
    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * Asigna la variable bloqueado
     * 
     * @param bloqueado
     * Variable a asignar en bloqueado
     */
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    /**
     * Retorna la variable pesoToneladas
     * 
     * @return pesoToneladas
     */
    public boolean isPesoToneladas() {
        return pesoToneladas;
    }

    /**
     * Asigna la variable pesoToneladas
     * 
     * @param pesoToneladas
     */
    public void setPesoToneladas(boolean pesoToneladas) {
        this.pesoToneladas = pesoToneladas;
    }
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
}
