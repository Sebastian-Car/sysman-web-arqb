/*-
 * AuxiliarfacturacionconceptosControlador.java
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.AuxiliarfacturacionconceptosControladorEnum;
import com.sysman.serviciospublicos.enums.AuxiliarfacturacionconceptosControladorUrlEnum;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para el formulario Auxiliar de cartera por concepto,
 * es llamado desde Facturacion\Informes\Facturaci�n y
 * cartera\Auxiliar de cartera por concepto, genera un reporte con el
 * listado de los auxiliares de facturacion por concepto
 *
 * @version 1.0, 10/11/2016
 * @author ybecerra
 * @version 2.0, 16/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuaracion del controlador *Refactoring y creacion de
 * dss
 */
@ManagedBean
@ViewScoped
public class AuxiliarfacturacionconceptosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel clase que almacena el codigo del modulo por
     * la cual se ingresa a la aplicacion
     */
    private final String modulo;
    /**
     * Atributo que almacena el codigo del ciclo seleccionado en el
     * combo ciclo del formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor digitado en el campo Concepto
     * Inicial del formulario
     */
    private String conceptoInicial;
    /**
     * Atributo que almacena el valor digitado en el campo Concepto
     * final del formulario
     */
    private String conceptoFinal;
    /**
     * Atributo que almacena el valor digitado en el campo Atrasos
     * Mayores a: del formulario
     */
    private String atrasos;
    /**
     * Atributo que almacena el valor digitado en el campo Total
     * facturado del formulario
     */
    private String valores;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Listado de registros para el combo de ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Ejb
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * AuxiliarfacturacionconceptosControlador
     */
    public AuxiliarfacturacionconceptosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.AUXILIARFACTURACIONCONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        conceptoInicial = "0";
        conceptoFinal = SysmanConstantes.CONS_SUCURSAL;
        atrasos = "0";
        valores = "0";
        ciclo = "T";
    }

    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliarfacturacionconceptosControladorUrlEnum.URL5280
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
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
    }

    /**
     * Obtiene el valor del parametro ingresado por parametro
     * 
     * @param nombre
     * Paramtero que se desea consultar en la Base de Datos
     * @param isMayMin
     * Indicador para obtener el resultado en mayuscula o exactamente
     * como se encuentra almacenado
     * @return valor del parametro a consultar
     */
    private String getParametro(String nombre, boolean isMayMin)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                            new Date(), isMayMin);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * Metodo que se ejecuta al darle clic al boton presentar o excel
     * del formulario
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String parReporte = "";
        Registro rs;
        try
        {
            String param = getParametro(
                            AuxiliarfacturacionconceptosControladorEnum.FORMATO_CALIDAD
                                            .getValue(),
                            true);
            if (param == null)
            {
                JsfUtil.agregarMensajeError(idioma
                                .getString(AuxiliarfacturacionconceptosControladorEnum.TB_TB1800
                                                .getValue()));
                return;
            }
            else if (AuxiliarfacturacionconceptosControladorEnum.SI.getValue()
                            .equals(param))
            {
                parReporte = AuxiliarfacturacionconceptosControladorEnum.FORMULARIO001237
                                .getValue();
            }
            else
            {
                parReporte = AuxiliarfacturacionconceptosControladorEnum.FORMULARIO001226
                                .getValue();
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            if ("T".equals(ciclo))
            {
                parametros.put(AuxiliarfacturacionconceptosControladorEnum.PR_CICLO
                                .getValue(), "Ciclo: Todos");

                reemplazar.put(AuxiliarfacturacionconceptosControladorEnum.CONDICIONCICLO
                                .getValue(), " ");
                parametros.put(AuxiliarfacturacionconceptosControladorEnum.PR_PERIODOS
                                .getValue(), idioma
                                                .getString(AuxiliarfacturacionconceptosControladorEnum.TB_TB3158
                                                                .getValue()));

            }
            else
            {
                parametros.put(AuxiliarfacturacionconceptosControladorEnum.PR_CICLO
                                .getValue(), "Ciclo: " + ciclo);
                reemplazar.put(AuxiliarfacturacionconceptosControladorEnum.CONDICIONCICLO
                                .getValue(),
                                " AND SP_FACTURADO.CICLO = '" + ciclo + "'");

                HashMap<String, Object> params = new HashMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                AuxiliarfacturacionconceptosControladorUrlEnum.URL5224
                                                                                                .getValue())
                                                                .getUrl(),
                                                params));
                if (rs != null)
                {
                    parametros.put(AuxiliarfacturacionconceptosControladorEnum.PR_PERIODOS
                                    .getValue(), SysmanFunciones.concatenar(idioma.getString("TG_ANIO"), ": ", rs.getCampos().get(
                                                    GeneralParameterEnum.ANO
                                                                    .getName())
                                                    .toString(), ". Periodo: ", rs.getCampos().get(
                                                                    GeneralParameterEnum.PERIODO
                                                                                    .getName())
                                                                    .toString()));
                }
            }

            reemplazar.put(AuxiliarfacturacionconceptosControladorEnum.ATRASO
                            .getValue().toLowerCase(), atrasos);
            reemplazar.put(AuxiliarfacturacionconceptosControladorEnum.VALORES
                            .getValue().toLowerCase(), valores);
            reemplazar.put(AuxiliarfacturacionconceptosControladorEnum.CONCEPTOINICIAL
                            .getValue(), conceptoInicial);
            reemplazar.put(AuxiliarfacturacionconceptosControladorEnum.CONCEPTOFINAL
                            .getValue(), conceptoFinal);

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
     * Retorna la variable conceptoInicial
     *
     * @return conceptoInicial
     */
    public String getConceptoInicial()
    {
        return conceptoInicial;
    }

    /**
     * Asigna la variable conceptoInicial
     *
     * @param conceptoInicial
     * Variable a asignar en conceptoInicial
     */
    public void setConceptoInicial(String conceptoInicial)
    {
        this.conceptoInicial = conceptoInicial;
    }

    /**
     * Retorna la variable conceptoFinal
     *
     * @return conceptoFinal
     */
    public String getConceptoFinal()
    {
        return conceptoFinal;
    }

    /**
     * Asigna la variable conceptoFinal
     *
     * @param conceptoFinal
     * Variable a asignar en conceptoFinal
     */
    public void setConceptoFinal(String conceptoFinal)
    {
        this.conceptoFinal = conceptoFinal;
    }

    /**
     * Retorna la variable atrasos
     *
     * @return atrasos
     */
    public String getAtrasos()
    {
        return atrasos;
    }

    /**
     * Asigna la variable atrasos
     *
     * @param atrasos
     * Variable a asignar en atrasos
     */
    public void setAtrasos(String atrasos)
    {
        this.atrasos = atrasos;
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
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
}
