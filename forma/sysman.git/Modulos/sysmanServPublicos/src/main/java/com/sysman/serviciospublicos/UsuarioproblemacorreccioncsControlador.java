/*-
 * UsuarioproblemacorreccioncsControlador.java
 *
 * 1.0
 * 
 * 02/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.UsuarioproblemacorreccioncsControladorEnum;
import com.sysman.serviciospublicos.enums.UsuarioproblemacorreccioncsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 02/08/2017
 * @author jcrodriguez migracion, refactoring, creacion de dss y
 * depuracion del controlador de acuerdo al estandar
 * 
 */
@ManagedBean
@ViewScoped

public class UsuarioproblemacorreccioncsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private List<Registro> listaCuadrocombinado11;

    private RegistroDataModelImpl listaProblema;

    private RegistroDataModelImpl listaProblemaE;

    /**
     * variable parametro que almacena el ciclo del registro
     * seleccionado del formulario de datos
     */
    private String ciclo;
    /**
     * variable parametro que almacena el periodo del registro
     * seleccionado del formulario de datos
     */
    private String periodo;
    /**
     * variable parametro que almacena ano del registro seleccionado
     * del formulario de datos
     */
    private String ano;
    /**
     * variable parametro que almacena el codigo ruta del registro
     * seleccionado del formulario de datos
     */
    private String codigoRuta;
    /**
     * variable parametro que almacena la clase del registro
     * seleccionado del formulario de datos
     */
    private String clase;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private Map<String, Object> rid;
    /**
     * variable que almacena el codigo del problema cuando selecciona
     * un registro del combo grande de la tabla
     */
    private String auxiliar;
    /**
     * variable que almacena el nombre del problema cuando selecciona
     * un registro del combo grande la tabla
     */
    private String problemaE;
    /**
     * variable que almacena la solucion del problema cuando
     * selecciona un registro del combo grande la tabla
     */
    private String solucionE;
    /**
     * variable que almacena el nombre seleccionado
     */
    private String nombreE;
    /**
     * variable que almacena el identificador del problema actual,se
     * utiliza en el actualizar antes
     */
    private String idProblemaActual;
    /**
     * variable que contiene la lectura actual del aforador del
     * registro seleccionado del formualrio de datos
     */
    private String lectura;
    private int indice;
    /**
     * variable que recibe la accion del formulario de datos
     * (ver,editar,eliminar)
     */
    private String accionEncabezado;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * UsuarioproblemacorreccioncsControlador
     */
    public UsuarioproblemacorreccioncsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        try
        {
            ciclo = validarCadena(parametros, GeneralParameterEnum.CICLO.getName().toLowerCase());
            ano = validarCadena(parametros, GeneralParameterEnum.ANO.getName().toLowerCase());
            codigoRuta = validarCadena(parametros, GeneralParameterEnum.CODIGORUTA.getName().toLowerCase());
            periodo = validarCadena(parametros, GeneralParameterEnum.PERIODO.getName().toLowerCase());
            clase = validarCadena(parametros, GeneralParameterEnum.CLASE.getName().toLowerCase());
            lectura = validarCadena(parametros, UsuarioproblemacorreccioncsControladorEnum.LECTURA.getValue().toLowerCase());
            accionEncabezado = validarCadena(parametros, UsuarioproblemacorreccioncsControladorEnum.ACCION.getValue().toLowerCase());
            rid = (Map<String, Object>) parametros.get("rid");

            numFormulario = GeneralCodigoFormaEnum.USUARIOPROBLEMA_CORRECCIONCS_CONTROLADOR.getCodigo();
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
        tabla = "SP_USUARIO_PROBLEMA";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        cargarListaProblema();
        cargarListaProblemaE();
        abrirFormulario();
    }

    private String validarCadena(Map<String, Object> campos, String nombre)
    {
        return SysmanFunciones.validarCampoVacio(campos, nombre) ? "" : campos.get(nombre).toString();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuarioproblemacorreccioncsControladorUrlEnum.URL7322
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuarioproblemacorreccioncsControladorUrlEnum.URL7328
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuarioproblemacorreccioncsControladorUrlEnum.URL7326
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuarioproblemacorreccioncsControladorUrlEnum.URL7324
                                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaProblema
     *
     */
    public void cargarListaProblema()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(UsuarioproblemacorreccioncsControladorUrlEnum.URL7321.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(UsuarioproblemacorreccioncsControladorEnum.CLASEPROBLEMA.getValue(),
                        clase);
        listaProblema = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaProblema
     */
    public void cargarListaProblemaE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(UsuarioproblemacorreccioncsControladorUrlEnum.URL7321.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(UsuarioproblemacorreccioncsControladorEnum.CLASEPROBLEMA.getValue(),
                        clase);
        listaProblemaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo ejecutado al cambiar el control Problema en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProblemaC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum).getCampos().put(UsuarioproblemacorreccioncsControladorEnum.PROBLEMA.getValue(), auxiliar);
        listaInicial.getDatasource().get(rowNum).getCampos().put(GeneralParameterEnum.NOMBRE.getName(), nombreE);
        listaInicial.getDatasource().get(rowNum).getCampos().put(UsuarioproblemacorreccioncsControladorEnum.SOLUCION.getValue(), solucionE);

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProblema
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProblema(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(UsuarioproblemacorreccioncsControladorEnum.PROBLEMA.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(UsuarioproblemacorreccioncsControladorEnum.SOLUCION.getValue(),
                        registroAux.getCampos().get(UsuarioproblemacorreccioncsControladorEnum.SOLUCION.getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProblema
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProblemaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
        nombreE = validarCadena(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
        solucionE = validarCadena(registroAux.getCampos(), UsuarioproblemacorreccioncsControladorEnum.SOLUCION.getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuadrocombinado12
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuadrocombinado12(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(UsuarioproblemacorreccioncsControladorEnum.PROBLEMA.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuadrocombinado12
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuadrocombinado12E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_COMBOS_GRANDES>
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
        if (ACCION_VER.equals(accionEncabezado))
        {
            permisos[0] = false;
            permisos[1] = false;
            permisos[2] = false;
        }
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
     * @return boolean
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(UsuarioproblemacorreccioncsControladorEnum.SOLUCION.getValue());
        registro.getCampos().remove(UsuarioproblemacorreccioncsControladorEnum.PROBLEMA_ANT.getValue());
        registro.getCampos().put(UsuarioproblemacorreccioncsControladorEnum.LECTURA.getValue(), lectura);
        addRegistro();
        return true;
    }

    private void addRegistro()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), periodo);
        registro.getCampos().put(GeneralParameterEnum.CLASE.getName(), clase);
        registro.getCampos().put(GeneralParameterEnum.CICLO.getName(), ciclo);
        registro.getCampos().put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return boolean
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(UsuarioproblemacorreccioncsControladorEnum.SOLUCION.getValue());
        addRegistro();

        return true;
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {

        registro.getCampos().remove(UsuarioproblemacorreccioncsControladorEnum.LECTURA.getValue());
        registro.getLlave().remove("KEY_PERIODO");
        registro.getLlave().remove("KEY_CODIGORUTA");
        registro.getLlave().remove("KEY_COMPANIA");
        registro.getLlave().remove("KEY_CICLO");
        registro.getLlave().remove("KEY_CLASE");
        registro.getLlave().remove("KEY_PROBLEMA");
        registro.getLlave().remove("KEY_ANO");
        registro.getCampos().put(UsuarioproblemacorreccioncsControladorEnum.PROBLEMA_ANT.getValue(),
                        idProblemaActual);

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        redicreccionar();
    }

    private void redicreccionar()
    {
        String[] campos = { "rid", GeneralParameterEnum.CICLO.getName().toLowerCase() };
        Object[] valores = { rid, ciclo };
        SessionUtil.redireccionar("/correccioncritica.sysman", campos,
                        valores);
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        idProblemaActual = validarCadena(registro.getCampos(), UsuarioproblemacorreccioncsControladorEnum.PROBLEMA.getValue());
    }

    /**
     * Retorna la lista listaCuadrocombinado11
     * 
     * @return listaCuadrocombinado11
     */
    public List<Registro> getListaCuadrocombinado11()
    {
        return listaCuadrocombinado11;
    }

    /**
     * Asigna la lista listaCuadrocombinado11
     * 
     * @param listaCuadrocombinado11
     * Variable a asignar en listaCuadrocombinado11
     */
    public void setListaCuadrocombinado11(List<Registro> listaCuadrocombinado11)
    {
        this.listaCuadrocombinado11 = listaCuadrocombinado11;
    }

    /**
     * Retorna la lista listaProblema
     * 
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaProblema()
    {
        return listaProblema;
    }

    /**
     * Asigna la lista listaProblema
     * 
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaProblema(RegistroDataModelImpl listaProblema)
    {
        this.listaProblema = listaProblema;
    }

    /**
     * Retorna la lista listaProblema
     * 
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaProblemaE()
    {
        return listaProblemaE;
    }

    /**
     * Asigna la lista listaProblema
     * 
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaProblemaE(RegistroDataModelImpl listaProblemaE)
    {
        this.listaProblemaE = listaProblemaE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getProblemaE()
    {
        return problemaE;
    }

    public void setProblemaE(String problemaE)
    {
        this.problemaE = problemaE;
    }

    public String getSolucionE()
    {
        return solucionE;
    }

    public void setSolucionE(String solucionE)
    {
        this.solucionE = solucionE;
    }

    public String getNombreE()
    {
        return nombreE;
    }

    public void setNombreE(String nombreE)
    {
        this.nombreE = nombreE;
    }

    public String getIdProblemaActual()
    {
        return idProblemaActual;
    }

    public void setIdProblemaActual(String idProblemaActual)
    {
        this.idProblemaActual = idProblemaActual;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public String getLectura()
    {
        return lectura;
    }

    public void setLectura(String lectura)
    {
        this.lectura = lectura;
    }

    public String getAccionEncabezado()
    {
        return accionEncabezado;
    }

    public void setAccionEncabezado(String accionEncabezado)
    {
        this.accionEncabezado = accionEncabezado;
    }

}
