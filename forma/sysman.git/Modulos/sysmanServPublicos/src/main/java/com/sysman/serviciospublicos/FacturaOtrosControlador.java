/*-
 * FacturaOtrosControlador.java
 *
 * 1.0
 * 
 * 22/09/2016
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModel;
import com.sysman.serviciospublicos.enums.FacturaOtrosControladorEnum;
import com.sysman.serviciospublicos.enums.FacturaOtrosControladorUrlEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Contiene la migracion de la pestana Otros del formulario "Factura"
 * en Access. Se compone de dos subformularios definidos en Access
 * como MedidorUsuario y Usuario_HistoricosSub.
 *
 * @version 1.0, 22/09/2016
 * @author amonroy
 * 
 * @version 2, 22/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 */

@ManagedBean
@ViewScoped
public class FacturaOtrosControlador extends BeanBaseDatosAcmeImpl {
    // <DECLARAR_CONSTANTES>
    /**
     * Constante que almacena el valor del campo compania
     */
    private final String compania;

    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */

    private final String strCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CICLO en el formulario, almacena el texto
     * CICLO el cual es un campo del registro
     */

    private final String strCiclo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGORUTA en el formulario, almacena el texto
     * CODIGORUTA el cual es un campo del registro
     */

    private final String strCodigoRuta;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo COMPANIA en el formulario, almacena el texto
     * COMPANIA el cual es un campo del registro
     */

    private final String strCompania;

    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la tabla SP_MEDIDOR
     */
    private final String tablaMedidor;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la tabla SP_MEDIDOR
     */
    private final String tablaUsuHist;
    // </DECLARAR_CONSTANTES>
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ciclo de la factura
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo de ruta de la factura
     */
    private String codigoRuta;
    /**
     * Atributo que almacena el ano que se trae por parametro desde el
     * formulario Factura.
     */
    private String ano;
    /**
     * Atributo que almacena el periodo que se trae por parametro
     * desde el formulario Factura.
     */
    private String periodo;
    /**
     * Atributo que almacena el valor del campo lectura que se trae
     * por parametro desde el formulario Factura.
     */
    private String lectura;
    /**
     * Atributo que permite definir si es posible la eliminacion en el
     * subformulario MedidorUsuario
     */
    private boolean permiteEliminar;
    /**
     * Atributo que permite almacenar valores temporales
     */
    private String auxiliar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de Marcas de Medidores para el subformulario
     * "SubformularioMedidorUsuario"
     */
    private List<Registro> listaMarca;
    /**
     * Listado de bancos para registrar el banco donde se realiza el
     * pago1 en el subformulario "SubformularioUsuarioHistoricos"
     */
    private List<Registro> listaBancoPago1;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado para el comboBox de codigo en el subformulario
     * "SubformularioMedidorUsuario"
     */
    private RegistroDataModel listaCodigo;
    /**
     * Listado para el comboBox de codigo en el subformulario
     * "SubformularioMedidorUsuario" cuando se edita un registro
     */
    private RegistroDataModel listaCodigoE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Listado de registros que se visualizan en el subformulario
     * "SubformularioMedidorUsuario"
     */
    private List<Registro> listaSubformulariomedidorusuario;
    /**
     * Listado de registros que se visualizan en el subformulario
     * "SubformularioUsuarioHistoricos"
     */
    private List<Registro> listaSubformulariousuariohistoricos;
    /**
     * Referencia para el registro seleccionado en el subformulario
     * "SubformularioMedidorUsuario"
     */
    private Registro registroSubSubformularioMedidorUsuario;
    /**
     * Referencia para el registro seleccionado en el subformulario
     * "SubformularioUsuarioHistoricos"
     */
    private Registro registroSubSubformularioUsuarioHistoricos;

    // </DECLARAR_ADICIONALES>
    /**
     * Constructor de la clase FacturaOtrosControlador que recibe los
     * parametros de inicializacion desde el formulario
     * "Facturaintegrado".
     * 
     * Inicializacion de los registros de los subformularios.
     */
    public FacturaOtrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        strCodigo = "CODIGO";
        strCiclo = "CICLO";
        strCodigoRuta = "CODIGORUTA";
        strCompania = "COMPANIA";
        tablaMedidor = FacturaOtrosControladorEnum.TABLAMEDIDOR.getValue();
        tablaUsuHist = FacturaOtrosControladorEnum.TABLAUSUHIST.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURA_OTROS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("ciclo").toString();
                codigoRuta = parametrosEntrada.get("codigoRuta").toString();
                ano = parametrosEntrada.get("ano").toString();
                periodo = (String) parametrosEntrada.get("periodo");
                lectura = parametrosEntrada.get("lectura").toString();

            }

            registroSubSubformularioMedidorUsuario = new Registro(
                            new HashMap<String, Object>());
            registroSubSubformularioUsuarioHistoricos = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {   
        cargarListaCodigo();
        cargarListaCodigoE();  
        cargarListaMarca();
        cargarListaBancoPago1();  
    }

    @Override
    public void iniciarListasSub() {  
        cargarListaSubformulariomedidorusuario();
        cargarListaSubformulariousuariohistoricos();
    }

    @Override
    public void iniciarListasSubNulo() {      
        listaSubformulariomedidorusuario = null;
        listaSubformulariousuariohistoricos = null;
    }

    @PostConstruct
    public void inicializar() {
        tabla = "SP_USUARIO";
        buscarLlave();
        iniciarListasSub();
        cargarListaCodigo();
        cargarListaCodigoE();
        cargarListaMarca();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        // No es un formulario de datos pero es necesario para la
        // carga del formulario
    }


    /**
     * Realiza la carga del listado de registros para el Subformulario
     * "SubformularioMedidorUsuario"
     */
    public void cargarListaSubformulariomedidorusuario() {
    
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),codigoRuta);

            try {
                listaSubformulariomedidorusuario = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FacturaOtrosControladorUrlEnum.URL9712
                                                                                .getValue())
                                                .getUrl(), param),
                                CacheUtil.getLlaveServicio(urlConexionCache,
                                                tablaMedidor));
            }
            catch (SystemException e) {      
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            catch (SysmanException e) {        
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

    }

    /**
     * Realiza la carga del listado de registros para el Subformulario
     * "SubformularioUsuarioHistoricos"
     */
    public void cargarListaSubformulariousuariohistoricos() {

            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),codigoRuta);
            
            try {
                listaSubformulariousuariohistoricos = RegistroConverter
                                .toListRegistro(requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FacturaOtrosControladorUrlEnum.URL12345
                                                                                                .getValue())
                                                                .getUrl(),
                                                param),
                                                CacheUtil.getLlaveServicio(urlConexionCache,
                                                                tablaUsuHist));
            }
            catch (SystemException e) {             
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            catch (SysmanException e) {     
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
      
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * Realiza la carga del listado de registros para el combo de
     * Marca del Medidor en el subformulario
     * "SubformularioMedidorUsuario"
     */
    public void cargarListaMarca() {
        //METODO NO IMPLEMENTADO
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * Banco en el subformulario "SubformularioUsuarioHistoricos"
     */
    public void cargarListaBancoPago1() {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * Codigo, lista los medidores disponiobles para ser asignados en
     * el subformulario "SubformularioMedidorUsuario"
     */
    public void cargarListaCodigo() {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * Codigo, lista los medidores disponiobles para ser asignados en
     * el subformulario "SubformularioMedidorUsuario" cuando se
     * realiza la edicion de un registro
     */
    public void cargarListaCodigoE() {
      //METODO NO IMPLEMENTADO
    }
    /**
     * Actualizacion del campo Codigo al seleccionar en el comboBox
     * Codigo del subformulario "SubformularioMedidorUsuario"
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox Codigo
     */
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubformularioMedidorUsuario.getCampos().put(strCodigo,
                        registroAux.getCampos().get(strCodigo));
    }

    /**
     * Actualizacion del campo Codigo al seleccionar en el comboBox
     * Codigo cuando se est� editando un registro
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox Codigo
     */
    public void seleccionarFilaCodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(strCodigo).toString();
    }

    /**
     * Agrega un registro al subformulario
     * "Subformulariomedidorusuario"
     */
    public void agregarRegistroSubSubformulariomedidorusuario() {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Acciones realizadas al editar un registro del subformulario
     * "Subformulariomedidorusuario"
     * 
     * Comprueba las fechas ingresadas con las fechas que vienen por
     * par�metro del formulario factura de acuerdo al resultado envia
     * un mensaje, de lo contrario calcula y asigna el valor del
     * consecutivo al registro que se va a crear
     * 
     * Realiza la verificaci�n de las lecturas del medidor
     * 
     * @param event
     * Evento generado al editar un registro del subformulario
     */
    public void editarRegSubSubformulariomedidorusuario(RowEditEvent event) {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Realiza la eliminacion de un registro del subformulario
     * "Subformulariomedidorusuario"
     * 
     * @param reg
     * El registro que va a ser eliminado
     */
    public void eliminarRegSubSubformulariomedidorusuario(Registro reg) {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Acciones a realizar cuando se cancela la insercion de un nuevo
     * registro en el subformulario "Subformulariomedidorusuario"
     */
    public void onCancelSubformulariomedidorusuario() {
        cargarListaSubformulariomedidorusuario();
        cargarListaSubformulariousuariohistoricos();
    }

    /**
     * Este metodo no se esta usando debido a que el subformulario
     * "Subformulariousuariohistoricos" es unicamente de consulta
     */
    public void agregarRegistroSubSubformulariousuariohistoricos() {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Este metodo no se esta usando debido a que el subformulario
     * "Subformulariousuariohistoricos" es unicamente de consulta
     * 
     * @param event
     */
    public void editarRegSubSubformulariousuariohistoricos(RowEditEvent event) {
      //METODO NO IMPLEMENTADO
    }

    /**
     * Este metodo no se esta usando debido a que el subformulario
     * "Subformulariousuariohistoricos" es unicamente de consulta
     * 
     * @param reg
     */
    public void eliminarRegSubSubformulariousuariohistoricos(Registro reg) {
      //METODO NO IMPLEMENTADO
    }

    public void onCancelSubformulariousuariohistoricos() {
        cargarListaSubformulariousuariohistoricos();
    }
    /**
     * Se envia la referencia desde el formulario para poder cargar el
     * formulario "facturaintegrado" al elegir la opcion de cerrar
     */
    public void ejecutarrcCerrar() {
        HashMap<String, Object> ridR = new HashMap<>();
        ridR.put(strCompania, compania);
        ridR.put(strCiclo, ciclo);
        ridR.put(strCodigoRuta, codigoRuta);

        String[] campos = { "rid" };
        Object[] valores = { ridR };

        SessionUtil.redireccionar("/facturaintegrado.sysman", campos, valores);
    }

    
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(strCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getLectura() {
        return lectura;
    }

    public void setLectura(String lectura) {
        this.lectura = lectura;
    }

    public boolean isPermiteEliminar() {
        return permiteEliminar;
    }

    public void setPermiteEliminar(boolean permiteEliminar) {
        this.permiteEliminar = permiteEliminar;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    public List<Registro> getListaMarca() {
        return listaMarca;
    }

    public void setListaMarca(List<Registro> listaMarca) {
        this.listaMarca = listaMarca;
    }

    public List<Registro> getListaBancoPago1() {
        return listaBancoPago1;
    }

    public void setListaBancoPago1(List<Registro> listaBancoPago1) {
        this.listaBancoPago1 = listaBancoPago1;
    }
    public RegistroDataModel getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModel listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModel getListaCodigoE() {
        return listaCodigoE;
    }

    public void setListaCodigoE(RegistroDataModel listaCodigoE) {
        this.listaCodigoE = listaCodigoE;
    }
    public List<Registro> getListaSubformulariomedidorusuario() {
        return listaSubformulariomedidorusuario;
    }

    public void setListaSubformulariomedidorusuario(
        List<Registro> listaSubformulariomedidorusuario) {
        this.listaSubformulariomedidorusuario = listaSubformulariomedidorusuario;
    }

    public List<Registro> getListaSubformulariousuariohistoricos() {
        return listaSubformulariousuariohistoricos;
    }

    public void setListaSubformulariousuariohistoricos(
        List<Registro> listaSubformulariousuariohistoricos) {
        this.listaSubformulariousuariohistoricos = listaSubformulariousuariohistoricos;
    }
    public Registro getRegistroSubSubformularioMedidorUsuario() {
        return registroSubSubformularioMedidorUsuario;
    }

    public void setRegistroSubSubformularioMedidorUsuario(
        Registro registroSubSubformularioMedidorUsuario) {
        this.registroSubSubformularioMedidorUsuario = registroSubSubformularioMedidorUsuario;
    }

    public Registro getRegistroSubSubformularioUsuarioHistoricos() {
        return registroSubSubformularioUsuarioHistoricos;
    }

    public void setRegistroSubSubformularioUsuarioHistoricos(
        Registro registroSubSubformularioUsuarioHistoricos) {
        this.registroSubSubformularioUsuarioHistoricos = registroSubSubformularioUsuarioHistoricos;
    }

}
