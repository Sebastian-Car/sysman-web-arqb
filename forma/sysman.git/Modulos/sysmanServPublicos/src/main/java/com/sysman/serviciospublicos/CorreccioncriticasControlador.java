/*-
 * CorreccioncriticasControlador.java
 *
 * 1.0
 * 
 * 01/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.CorreccioncriticasControladorEnum;
import com.sysman.serviciospublicos.enums.CorreccioncriticasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 01/08/2017
 * @author jcrodriguez=>Migracion,refactoring,depuracion y creacion de
 * dss
 */
@ManagedBean
@ViewScoped

public class CorreccioncriticasControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private RegistroDataModelImpl listaAforador;
    private List<Registro> listaCiclo;
    private RegistroDataModelImpl listaSeguimientosubfacturacritica;
    /**
     * variable parametro que almacena el ciclo seleccionado
     */
    private String ciclo;
    /**
     * variable estado true=>vuelve visible el subformulario
     * false=>oculta el subformulario
     */
    private boolean visibleSub;
    /**
     * variable que almacena el modulo actual del formulario de datos
     */
    private String modulo;
    /**
     * variable que almacena la lectura actual, se le asigna un valor
     * cuando la accion es modificar
     */
    private String antLecturaAforo;
    /**
     * variable que almacena la lectura anterior, se le asigna un
     * valor cuando la accion es modificar
     */
    private String antLecturaAnterior;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    /**
     * Crea una nueva instancia de CorreccioncriticasControlador
     */
    public CorreccioncriticasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        Map<String, Object> parametros = SessionUtil.getFlash();
        try
        {
            ciclo = validarCadena(parametros, GeneralParameterEnum.CICLO.getName().toLowerCase());
            rid = (Map<String, Object>) parametros.get("rid");
            numFormulario = GeneralCodigoFormaEnum.CORRECCIONCRITICAS_CONTROLADOR.getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que retorna vacio cuando no tiene ningun valor el nombre
     * o llave del Map
     * 
     * @param parametros=>Map
     * el cual contiene todos las llaves con su respectivos valores
     * @param nombre=>nobre
     * o llave
     * @return
     */
    private String validarCadena(Map<String, Object> parametros, String nombre)
    {
        return SysmanFunciones.validarCampoVacio(parametros, nombre) ? "" : parametros.get(nombre).toString();
    }

    /**
     * metodo que consulta un parametro de la base de datos
     * 
     * @param nombre=>nombre
     * del parametro
     * @param indMayus=>mayuscula
     * o minuscula
     * @return
     */
    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtilRemote.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        cargarListaAforador();
        cargarListaCiclo();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        cargarListaSeguimientosubfacturacritica();
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        listaSeguimientosubfacturacritica = null;
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
        enumBase = GenericUrlEnum.SP_USUARIO_LECTURAS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     */

    /**
     * 
     * Carga la lista listaSeguimientosubfacturacritica metodo en cual
     * Carga los registro para el subformulario
     */
    public void cargarListaSeguimientosubfacturacritica()
    {

        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(CorreccioncriticasControladorUrlEnum.URL8929.getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGORUTA.getName()));

            listaSeguimientosubfacturacritica = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            CorreccioncriticasControladorEnum.SP_ORDENTRABAJO
                                                            .getValue()));
        }

        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaAforador
     */
    public void cargarListaAforador()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CorreccioncriticasControladorUrlEnum.URL8959.getValue());

        listaAforador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaAforador(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AFORADOR.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(CorreccioncriticasControladorEnum.NOMBREAFORADOR.getValue(),
                        registroAux.getCampos().get(CorreccioncriticasControladorEnum.NOMBREAFORADOR.getValue()));
    }

    /**
     * 
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaCiclo = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(CorreccioncriticasControladorUrlEnum.URL8925.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando61 en la vista
     *
     */
    public void oprimirComando61()
    {
        String[] campos = { "rid", GeneralParameterEnum.CICLO.getName().toLowerCase(), GeneralParameterEnum.ANO.getName().toLowerCase(),
                            GeneralParameterEnum.CODIGORUTA.getName().toLowerCase(), GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                            GeneralParameterEnum.CLASE.getName().toLowerCase(),
                            CorreccioncriticasControladorEnum.LECTURA.getValue().toLowerCase(),
                            CorreccioncriticasControladorEnum.ACCION.getValue().toLowerCase() };
        Object[] valores = { css, ciclo, registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
                             registro.getCampos().get(GeneralParameterEnum.CODIGORUTA.getName()).toString(),
                             registro.getCampos().get(GeneralParameterEnum.PERIODO.getName()).toString(),
                             registro.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString(),
                             registro.getCampos().get(CorreccioncriticasControladorEnum.LECTURAAFORO.getValue()),
                             accion };
        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(GeneralCodigoFormaEnum.USUARIOPROBLEMA_CORRECCIONCS_CONTROLADOR.getCodigo()),
                        campos,
                        valores, true);
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Seguimientosubfacturacritica
     */
    public void agregarRegistroSubSeguimientosubfacturacritica()
    {
        // Heredado del bean base
    }

    /**
     * Metodo de edicion del formulario Seguimientosubfacturacritica
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSeguimientosubfacturacritica(RowEditEvent event)
    {
        // Heredado del bean base
    }

    /**
     * Metodo de eliminacion del formulario
     * Seguimientosubfacturacritica
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSeguimientosubfacturacritica(Registro reg)
    {
        // Heredado del bean base
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Seguimientosubfacturacritica
     */
    public void cancelarEdicionSeguimientosubfacturacritica()
    {
        cargarListaSeguimientosubfacturacritica();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        String parametro = SysmanFunciones.nvlStr(getParametro("VER SEGUIMIENTO PQR EN CRITICA", true), "NO");
        if ("SI".equals(parametro))
        {
            visibleSub = true;
        }
        else
        {
            visibleSub = false;
        }

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (ACCION_MODIFICAR.equals(accion))
        {
            antLecturaAforo = validarCadena(registro.getCampos(), CorreccioncriticasControladorEnum.LECTURAAFORO.getValue());
            antLecturaAnterior = validarCadena(registro.getCampos(), CorreccioncriticasControladorEnum.LECTURA.getValue());
        }
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return boolean
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return boolean VARIABLE
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return boolean
     */
    @Override
    public boolean actualizarAntes()
    {
        if (lecturaAforo())
        {
            return false;
        }
        registro.getCampos().remove(CorreccioncriticasControladorEnum.NOMBREAFORADOR.getValue());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CLASE.getName());

        return true;
    }

    /**
     * metodo que retorna un boleano si las condiciones del cuerpo del
     * metodo no se cumplen mostrando un mensaje de error, el metodo
     * se llama al orpimir el boton de guardar (actualizar) el
     * registro seleccionado de la grilla del formualrio de datos las
     * validaciones corresponden a la lectura actual del aforador
     * 
     * @return
     */
    private boolean lecturaAforo()
    {
        double lecturaAforo = SysmanFunciones
                        .nvlDbl(validarCadena(registro.getCampos(), CorreccioncriticasControladorEnum.LECTURAAFORO.getValue()), 0.0);
        double lecturaAnterior = SysmanFunciones
                        .nvlDbl(validarCadena(registro.getCampos(), CorreccioncriticasControladorEnum.LECTURA.getValue()), 0.0);

        if (Double.doubleToRawLongBits(lecturaAforo) == 0)
        {
            JsfUtil.agregarMensajeError(idioma.getString(CorreccioncriticasControladorEnum.TB_TB3364.getValue()));
            return true;
        }
        if (Math.round(Math.log(lecturaAforo) / (Math.log(10) + 0.5)) > 200)
        {
            JsfUtil.agregarMensajeError(idioma.getString(CorreccioncriticasControladorEnum.TB_TB3365.getValue()));
            registro.getCampos().put(CorreccioncriticasControladorEnum.LECTURAAFORO.getValue(), antLecturaAforo);
            return true;
        }
        else if ("01".equals(registro.getCampos().get(CorreccioncriticasControladorEnum.USO.getValue()).toString())
            && lecturaAforo - lecturaAnterior > 200)
        {
            JsfUtil.agregarMensajeError(idioma.getString(CorreccioncriticasControladorEnum.TB_TB3366.getValue()));
            return true;
        }
        if (validarLectura(lecturaAforo, lecturaAnterior))
        {
            return true;
        }

        return false;

    }

    private boolean validarLectura(double lecturaAforo, double lecturaAnterior)
    {
        if ((!"01".equals(registro.getCampos().get(CorreccioncriticasControladorEnum.USO.getValue()).toString())
            && lecturaAforo - lecturaAnterior > 500)
            ||
            (lecturaAforo < lecturaAnterior || lecturaAforo - lecturaAnterior > 300))
        {
            JsfUtil.agregarMensajeError(idioma.getString(CorreccioncriticasControladorEnum.TB_TB3366.getValue()));
            return true;
        }
        return false;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return boolean
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
     * @return boolean
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
     * @return boolean
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Retorna la lista listaAforador
     * 
     * @return listaAforador
     */
    public RegistroDataModelImpl getListaAforador()
    {
        return listaAforador;
    }

    /**
     * Asigna la lista listaAforador
     * 
     * @param listaAforador
     * Variable a asignar en listaAforador
     */
    public void setListaAforador(RegistroDataModelImpl listaAforador)
    {
        this.listaAforador = listaAforador;
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
     * Retorna la lista listaSeguimientosubfacturacritica
     * 
     * @return listaSeguimientosubfacturacritica
     */
    public RegistroDataModelImpl getListaSeguimientosubfacturacritica()
    {
        return listaSeguimientosubfacturacritica;
    }

    /**
     * Asigna la lista listaSeguimientosubfacturacritica
     * 
     * @param listaSeguimientosubfacturacritica
     * Variable a asignar en listaSeguimientosubfacturacritica
     */
    public void setListaSeguimientosubfacturacritica(RegistroDataModelImpl listaSeguimientosubfacturacritica)
    {
        this.listaSeguimientosubfacturacritica = listaSeguimientosubfacturacritica;
    }

    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub()
    {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public boolean isVisibleSub()
    {
        return visibleSub;
    }

    public void setVisibleSub(boolean visibleSub)
    {
        this.visibleSub = visibleSub;
    }

    public String getAntLecturaAforo()
    {
        return antLecturaAforo;
    }

    public void setAntLecturaAforo(String antLecturaAforo)
    {
        this.antLecturaAforo = antLecturaAforo;
    }

    public String getAntLecturaAnterior()
    {
        return antLecturaAnterior;
    }

    public void setAntLecturaAnterior(String antLecturaAnterior)
    {
        this.antLecturaAnterior = antLecturaAnterior;
    }

}
