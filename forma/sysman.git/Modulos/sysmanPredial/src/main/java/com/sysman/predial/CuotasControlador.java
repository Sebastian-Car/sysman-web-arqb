/*-
 * CuotasControlador.java
 *
 * 1.0
 * 
 * 30/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.CuotasControladorEnum;
import com.sysman.predial.enums.CuotasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase en la cual guarda informacion sobre las cuotas de pago del
 * impuesto predial a contribuyentes al dia
 *
 * @version 1.0, 30/01/2017
 * @author jguerrero
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * @author jcrodriguez -Depuracion del controlador y Refactoring
 * @version 3, 28/06/2017
 */
@ManagedBean
@ViewScoped
public class CuotasControlador extends BeanBaseContinuoAcmeImpl
{
    private double porcentaje;
    private int indice;
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el String FECHALIMITE en cual es usado
     * para obtener datos de registro.
     */

    /**
     * Variable que se encarga de mostrar el dialogo a la hora de
     * cerrar el formulario
     *
     */
    private boolean diagloVisible;
    /**
     * Variable que se encarga de mostra el texto en el dialogo
     *
     */
    private String textoDialog;

    /**
     * Lista de tipo registro que almancena temporalemte la
     * informacion de los a�os
     */
    private List<Registro> listaANO;

    /**
     * Crea una nueva instancia de CuotasControlador
     */
    public CuotasControlador()
    {
        super();

        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CUOTAS_CONTROLADOR.getCodigo();
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
        enumBase = GenericUrlEnum.IP_CUOTAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaANO();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaANO
     *
     * Metodo encargado de hacer el llamdo a la base de datos y
     * almacenar la respuesa en la lista ano
     */
    public void cargarListaANO()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaANO = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(CuotasControladorUrlEnum.URL4896.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control ANO
     * 
     * 
     */
    public void cambiarANO()
    {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.ANO.getName()))
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

            Registro reg = null;
            try
            {
                reg = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(CuotasControladorUrlEnum.URL9722.getValue()).getUrl(),
                                param));
                registro.getCampos().put(GeneralParameterEnum.CUOTA.getName(),
                                Integer.parseInt(reg.getCampos().get(GeneralParameterEnum.CUOTA.getName())
                                                .toString())
                                    + 1);
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes()
    {
        if (!validarFechas())
        {
            return false;
        }
        if (fechaLimite())
        {
            try
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3269").replace("s$fecha$s",
                                SysmanFunciones.convertirAFechaCadena(
                                                (Date) registro.getCampos().get(CuotasControladorEnum.FECHALIMITE.getValue()))));
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            return false;
        }
        if (validarPorcentajeCero())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3268"));
            return false;
        }
        porcentaje = validarPorcentajeAct()
            + validarPorcentaje(registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString());
        if (porcentaje > 100)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3267")
                            .replace("s$" + GeneralParameterEnum.ANO.getName().toLowerCase() + "$s",
                                            registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString())
                            .replace("s$porcentaje$s", String.valueOf(porcentaje - 100)));
            return false;
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_MODIFIED.getName());

        return true;
    }

    private double validarPorcentaje(String ano)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        Registro reg = null;
        double porcentajes = 0;
        try
        {
            reg = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(CuotasControladorUrlEnum.URL4826.getValue()).getUrl(),
                            param));
            porcentajes = SysmanFunciones.validarCampoVacio(reg.getCampos(), GeneralParameterEnum.PORCENTAJE.getName()) ? 0
                : Double.parseDouble(reg.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()).toString());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return porcentajes;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarPorcentajeCero()
    {
        return Double.doubleToRawLongBits(
                        SysmanFunciones.nvlDbl(registro.getCampos().get(CuotasControladorEnum.PORCENTAJE.getValue()), 0)) == 0;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas())
        {
            return false;
        }
        if (validarPorcentajeCero())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3268"));
            return false;
        }
        if (porcentaje > 100)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3267")
                            .replace("s$" + GeneralParameterEnum.ANO.getName().toLowerCase() + "$s",
                                            registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString())
                            .replace("s$porcentaje$s", String.valueOf(porcentaje - 100)));
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private double validarPorcentajeAct()
    {
        return Double.parseDouble(registro.getCampos().get(CuotasControladorEnum.PORCENTAJE.getValue()).toString());

    }

    private boolean fechaLimite()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.FECHA.getName(), registro.getCampos().get(CuotasControladorEnum.FECHALIMITE.getValue()));
        Registro reg = null;
        try
        {
            reg = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(CuotasControladorUrlEnum.URL4827.getValue()).getUrl(),
                            param));
            if (reg != null)
            {
                return true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues()
    {
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
    public boolean eliminarAntes()
    {
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
    public boolean eliminarDespues()
    {
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
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
        registro.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

    }

    public void ejecutarmostrarMensaje()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario y abre el modal
     * que verifica el porcentaje de los a�os
     * 
     */
    public void cerrarFormulario()
    {

        diagloVisible = true;

    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        diagloVisible = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto evaluarCierre en
     * la vista
     *
     *
     */
    public void ejecutarevaluarCierre()
    {
        // <CODIGO_DESARROLLADO>
        StringBuilder anos = new StringBuilder();
        int contador = 0;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        List<Registro> reg = null;
        try
        {
            reg = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(CuotasControladorUrlEnum.URL4824.getValue()).getUrl(),
                            param));

            for (Registro registro : reg)
            {
                Registro regAux = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(CuotasControladorUrlEnum.URL4825.getValue()).getUrl(),
                                param));

                if (!SysmanFunciones.validarCampoVacio(regAux.getCampos(), CuotasControladorEnum.CANT.getValue())
                    && !"100".equals(regAux.getCampos().get(CuotasControladorEnum.CANT.getValue()).toString()))
                {
                    diagloVisible = true;
                    anos.append(" ").append(
                                    registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString())
                                    .append(" ");
                    contador++;

                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (contador > 1)
        {
            textoDialog = idioma.getString("TB_TB2812").replace("s$ano$s",
                            anos.toString());
        }
        if (contador == 1)
        {
            textoDialog = idioma.getString("TB_TB2800").replace("s$ano$s",
                            anos.toString());
        }
        if (contador == 0)
        {
            JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dgMensaje en la vista y que cierra el modal.
     * 
     *
     */
    public void aceptardgMensaje()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado de la clase Beanbase
    }

    /**
     * metodo que se utiliza para validar las fechas
     * 
     * @return
     */
    private boolean validarFechas()
    {
        boolean respuesta = true;
        Date fecha = (Date) registro.getCampos().get(CuotasControladorEnum.FECHALIMITE.getValue());
        int ano = SysmanFunciones.ano(fecha);
        if (ano != Integer
                        .parseInt(registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2813"));
            registro.getCampos().put(CuotasControladorEnum.FECHALIMITE.getValue(), null);
            respuesta = false;

        }

        return respuesta;

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        porcentaje = 0.0;
        indice = listaInicial.getRowIndex();
        porcentaje = (validarPorcentaje(registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString())
            - SysmanFunciones.nvlDbl(registro.getCampos().get(CuotasControladorEnum.PORCENTAJE.getValue()), 0)) + porcentaje;

    }

    /**
     * Metodo ejecutado al cambiar el control PORCENTAJE en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPORCENTAJEC(int rowNum)
    {

        porcentaje = porcentaje + SysmanFunciones
                        .nvlDbl(listaInicial.getDatasource().get(rowNum).getCampos().get(CuotasControladorEnum.PORCENTAJE.getValue()), 0);

    }

    /**
     * metodos get y set
     */
    /**
     * Retorna la lista listaANO
     * 
     * @return listaANO
     */
    public List<Registro> getListaANO()
    {
        return listaANO;
    }

    /**
     * Asigna la lista listaANO
     * 
     * @param listaANO
     * Variable a asignar en listaANO
     */
    public void setListaANO(List<Registro> listaANO)
    {
        this.listaANO = listaANO;
    }

    public boolean isDiagloVisible()
    {
        return diagloVisible;
    }

    public void setDiagloVisible(boolean diagloVisible)
    {
        this.diagloVisible = diagloVisible;
    }

    public String getTextoDialog()
    {
        return textoDialog;
    }

    public void setTextoDialog(String textoDialog)
    {
        this.textoDialog = textoDialog;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public double getPorcentaje()
    {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje)
    {
        this.porcentaje = porcentaje;
    }

}
