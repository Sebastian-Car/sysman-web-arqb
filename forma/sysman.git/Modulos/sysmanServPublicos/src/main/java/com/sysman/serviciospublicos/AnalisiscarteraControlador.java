/*-
 * AnalisiscarteraControlador.java
 *
 * 1.0
 * 
 * 10/11/2016
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.AnalisiscarteraControladorEnum;
import com.sysman.serviciospublicos.enums.AnalisiscarteraControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Controlador del formulario AnalisiscarteraControlador
 *
 * @version 1.0, 10/11/2016
 * @author cperez
 * @version 2.0, 15/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuracion del controlador sonar
 *             *Creacion de dss y refactoring
 *             
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class AnalisiscarteraControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el estado los Option
     */
    private String condicion;
    /**
     * Obtiene el estado del check de los Opci�n para saber si viene activo o no
     */
    private Boolean incluirSuspendidosRetiros;
    /**
     * Obtiene el ciclo Inicial para mandarlo a la consulta
     */
    private String cicloInicial;
    /**
     * Obtiene el ciclo Final para mandarlo a la consulta
     */
    private String cicloFinal;
    /**
     * Obtiene el el numero del perido de atraso Inicial para mandarlo a la consulta
     */
    private String periodoAtrasoInicial;
    /**
     * Obtiene el el valor superior digitado por el usuario para mandarlo a la consulta
     */
    private String valores;
    /**
     * Obtiene el el numero del perido de atraso Final para mandarlo a la consulta
     */
    private String periodoAtrasoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Necesario para obtener mandar la lista del ciclo Inicial
     */
    private List<Registro> listaCiclo;

    /**
     * Necesario para obtener mandar la lista del ciclo Final
     */
    private RegistroDataModelImpl listaCicloFinal;
    /**
     * Ejb 
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de AnalisiscarteraControlador
     */
    public AnalisiscarteraControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ANALISISCARTERA_CONTROLADOR.getCodigo(); 
            validarPermisos();

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        cargarListaCiclo();
        cargarListaCicloFinal();   
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        periodoAtrasoInicial = "0";
        periodoAtrasoFinal = SysmanConstantes.CONS_SUCURSAL;
        condicion = "1";
        valores = "0.00"; 
    }

    /**
     * 
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo()
    {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AnalisiscarteraControladorUrlEnum.URL5850.getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCicloFinal
     */
    public void cargarListaCicloFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AnalisiscarteraControladorUrlEnum.URL6503.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(AnalisiscarteraControladorEnum.CODIGO_INICIAL.getValue(),cicloInicial);

        listaCicloFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * Para saber si el par�metro �FORMATO CALIDAD�es igual a si o a no
     */
    public String valorParametro()
    {
        archivoDescarga = null;
        try {
            return ejbSysmanUtil.consultarParametro(compania
                            ,AnalisiscarteraControladorEnum.FORMATO_CALIDAD.getValue() 
                            , SessionUtil.getModulo()
                            , new Date()
                            , true);
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     * 
     * @return
     *
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long cicloIn = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIn > cicloFin)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(AnalisiscarteraControladorEnum.TB_TB1801.getValue()));
        }else{         
            if (AnalisiscarteraControladorEnum.SI.getValue().equals(valorParametro()))
            {
                genInforme(FORMATOS.PDF, AnalisiscarteraControladorEnum.INFORME1243.getValue());
            }
            else if (AnalisiscarteraControladorEnum.NO.getValue().equals(valorParametro()))
            {
                genInforme(FORMATOS.PDF, AnalisiscarteraControladorEnum.INFORME1241.getValue());
            }
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {      
        archivoDescarga = null;
        long cicloIn = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIn > cicloFin)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(AnalisiscarteraControladorEnum.TB_TB1801.getValue()));
        }else{         
            if (AnalisiscarteraControladorEnum.SI.getValue().equals(valorParametro()))
            {
                genInforme(FORMATOS.EXCEL, AnalisiscarteraControladorEnum.INFORME1243.getValue());
            }
            else if (AnalisiscarteraControladorEnum.NO.getValue().equals(valorParametro()))
            {
                genInforme(FORMATOS.EXCEL, AnalisiscarteraControladorEnum.INFORME1241.getValue());
            }
        }    
    }
    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte)
    {
        
            archivoDescarga = null;
            String incluirSuspendidosRetirados;
            String condicionEnvio= null;
            
            if ("1".equals(condicion))
            {
                condicionEnvio = "   AND (SP_USUARIO.PERIODOSATRASO BETWEEN " + " " + periodoAtrasoInicial + " " + " AND " + " "
                                + periodoAtrasoFinal +
                                "   AND SP_USUARIO.TOTFACTURAPERACTUAL BETWEEN " + " " + valores + " " + " AND 999999999)";
            }
            else if ("2".equals(condicion))
            {

                condicionEnvio = "   AND (SP_USUARIO.PERIODOSATRASO BETWEEN " + " " + periodoAtrasoInicial + " AND " + periodoAtrasoFinal +
                                "   OR SP_USUARIO.TOTFACTURAPERACTUAL BETWEEN " + " " + valores + " " + "AND 999999999)";
            }
            if (!incluirSuspendidosRetiros)
            {
                incluirSuspendidosRetirados = "AND SP_USUARIO.ESTADO NOT IN('S','R')";
            }
            else
            {
                incluirSuspendidosRetirados = "";
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);
            reemplazar.put(AnalisiscarteraControladorEnum.CICLOINICIAL.getValue(), cicloInicial);
            reemplazar.put(AnalisiscarteraControladorEnum.CICLOFINAL.getValue(), cicloFinal);
            reemplazar.put(AnalisiscarteraControladorEnum.CONDICION.getValue().toLowerCase(), condicionEnvio);
            reemplazar.put("incluirSuspendidosRetirados", incluirSuspendidosRetirados);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(AnalisiscarteraControladorEnum.PR_FORMS_ANALISISCARTERA_CICLO.getValue(), cicloInicial);
            parametros.put(AnalisiscarteraControladorEnum.PR_FORMS_ANALISISCARTERA_CMBCICLOF.getValue(), cicloFinal);
            parametros.put(AnalisiscarteraControladorEnum.PR_COMPANIA.getValue(), SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta(AnalisiscarteraControladorEnum.INFORME1241.getValue(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            try {
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
       
        

    }
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     */
    public void cambiarCiclo()
    {

        periodoAtrasoFinal = "";
        periodoAtrasoFinal = periodoAtrasoInicial;      
        cargarListaCicloFinal();
        buscarCicloFinal();


    }
    /**
     * buscar un registro en la lista ciclo final
     */
    private void buscarCicloFinal(){
        if(!SysmanFunciones.validarVariableVacio(cicloInicial)){
            for (int i = 0; i <listaCicloFinal.getDatasource().size(); i++) {
                if(SysmanFunciones.nvl(listaCicloFinal.getDatasource().get(i).getCampos().get(GeneralParameterEnum.NUMERO.getName())
                                ,"").toString().equals(cicloInicial)){
                    cicloFinal=listaCicloFinal.getDatasource().get(i).getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
                    break;
                }
            }
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCicloFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCicloFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cicloFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()),"").toString();
    }

    /**
     * Retorna la variable condicion
     * 
     * @return condicion
     */
    public String getCondicion()
    {
        return condicion;
    }

    /**
     * Asigna la variable condicion
     * 
     * @param condicion
     * Variable a asignar en condicion
     */
    public void setCondicion(String condicion)
    {
        this.condicion = condicion;
    }

    /**
     * Retorna la variable incluirSuspendidosRetiros
     * 
     * @return incluirSuspendidosRetiros
     */
    public Boolean getIncluirSuspendidosRetiros()
    {
        return incluirSuspendidosRetiros;
    }

    /**
     * Asigna la variable incluirSuspendidosRetiros
     * 
     * @param incluirSuspendidosRetiros
     * Variable a asignar en incluirSuspendidosRetiros
     */
    public void setIncluirSuspendidosRetiros(Boolean incluirSuspendidosRetiros)
    {
        this.incluirSuspendidosRetiros = incluirSuspendidosRetiros;
    }

    /**
     * Retorna la variable cicloInicial
     * 
     * @return cicloInicial
     */
    public String getCicloInicial()
    {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     * 
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial)
    {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     * 
     * @return cicloFinal
     */
    public String getCicloFinal()
    {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     * 
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal)
    {
        this.cicloFinal = cicloFinal;
    }

    /**
     * Retorna la variable periodoAtrasoInicial
     * 
     * @return periodoAtrasoInicial
     */
    public String getPeriodoAtrasoInicial()
    {
        return periodoAtrasoInicial;
    }

    /**
     * Asigna la variable periodoAtrasoInicial
     * 
     * @param periodoAtrasoInicial
     * Variable a asignar en periodoAtrasoInicial
     */
    public void setPeriodoAtrasoInicial(String periodoAtrasoInicial)
    {
        this.periodoAtrasoInicial = periodoAtrasoInicial;
    }

    /**
     * Retorna la variable valores
     * 
     * @return valores
     */
    public String getValores()
    {
        return valores;
    }

    /**
     * Asigna la variable valores
     * 
     * @param valores
     * Variable a asignar en valores
     */
    public void setValores(String valores)
    {
        this.valores = valores;
    }

    /**
     * Retorna la variable periodoAtrasoFinal
     * 
     * @return periodoAtrasoFinal
     */
    public String getPeriodoAtrasoFinal()
    {
        return periodoAtrasoFinal;
    }

    /**
     * Asigna la variable periodoAtrasoFinal
     * 
     * @param periodoAtrasoFinal
     * Variable a asignar en periodoAtrasoFinal
     */
    public void setPeriodoAtrasoFinal(String periodoAtrasoFinal)
    {
        this.periodoAtrasoFinal = periodoAtrasoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

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

    /**
     * Retorna la lista listaCicloFinal
     * 
     * @return listaCicloFinal
     */
    public RegistroDataModelImpl getListaCicloFinal()
    {
        return listaCicloFinal;
    }

    /**
     * Asigna la lista listaCicloFinal
     * 
     * @param listaCicloFinal
     * Variable a asignar en listaCicloFinal
     */
    public void setListaCicloFinal(RegistroDataModelImpl listaCicloFinal)
    {
        this.listaCicloFinal = listaCicloFinal;
    }
}
