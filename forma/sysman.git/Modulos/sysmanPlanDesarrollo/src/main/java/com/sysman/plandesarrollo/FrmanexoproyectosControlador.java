/*-
 * FrmanexoproyectosControlador.java
 *
 * 1.0
 * 
 * 30/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.DigitalizacionContratosControlador;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.FrmanexoproyectosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 30/09/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmanexoproyectosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Variable que almacena el modulo
     */
    private final String modulo;

    /**
     * Usuario que ingresa a la aplicaci�n
     */
    private final String usuario;

    private String rutafinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private String tipo;

    private String idPlanIndicativo;

    private String vigenciaInicial;

    private String numero;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esAdministrador;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esJefeUnidad;

    /**
     * Atributo que almacena el valor de la vigencia seleccionada en
     * la forma
     */
    private String vigencia;

    /**
     * Atributo que almacena la vigencia GetVIGENCIA_GUB()
     * 
     */
    private String vigenciaGubernamental;

    /**
     * Atributo que almacena los digitos de GetACCION()
     */
    private int digitosAccion;

    /**
     * Atributo que almacena el id del plan del formulario anterior
     */

    private String idPlan;

    /**
     * Atributo que almacena el total del valor estimado
     */
    private String valorTotalEstimado;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String dependencia;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private int digitosMetaProducto;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esUsuarioConsulta;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String vigenciaFinal;
    /**
     * Atributo que almacena el nombre del id seleccionado en el arbol
     */
    private String nombrePlan;

    private String predecesor;

    private String carpeta;

    /**
     * Atributo que almacena la descripcion del plan seleccionado
     */
    private String nomPlanAdquisiciones;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmanexoproyectosControlador
     */
    public FrmanexoproyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        carpeta = "documentos";

        try {
            // 2109
            numFormulario = GeneralCodigoFormaEnum.FRM_ANEXO_PROYECTOS_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                esAdministrador = (boolean) parametrosEntrada
                                .get("administrador");
                esJefeUnidad = (boolean) parametrosEntrada
                                .get("jefeUnidad");
                dependencia = SysmanFunciones.nvl(
                                parametrosEntrada.get("dependencia"),
                                "").toString();
                predecesor = SysmanFunciones.nvl(
                                parametrosEntrada.get("predecesor"),
                                "").toString();
                digitosMetaProducto = (int) SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get("digitosMetaProducto"), 0);
                esUsuarioConsulta = (boolean) parametrosEntrada
                                .get("esUsuarioConsulta");

                vigenciaFinal = SysmanFunciones.nvl(parametrosEntrada
                                .get("vigenciaFinal"), "").toString();

                tipo = parametrosEntrada.get("tipo").toString();
                numero = parametrosEntrada.get("numero").toString();
                vigenciaGubernamental = parametrosEntrada
                                .get("vigenciaGubernamental").toString();
                idPlan = parametrosEntrada.get("idPlan").toString();
                digitosAccion = Integer
                                .parseInt(parametrosEntrada.get("digitosAccion")
                                                .toString());
                nombrePlan = parametrosEntrada.get("nombrePlan").toString();

            }
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
    public void inicializar() {
        enumBase = GenericUrlEnum.PA_ANEXOS_BASE;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

        try {
            rutafinal = ejbSysmanUtil.consultarParametro(
                            compania, "RUTA ANEXOS INDICADORES META",
                            SessionUtil.getModulo(),
                            new Date(),
                            false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(),
                        tipo);
        /*Ticket 7700529  
         *gfigueredo
         *11/11/2021
         *Se quita filtro por numero, debido a que es necesario que muestre
         *todos los anexos por el plan, compania , tipo y codigo.
         *Verificar proceso antes de habilitar.
         */   
        //parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
        //                numero);
        parametrosListado.put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Guardar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirGuardar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        String ruta;
        // String archivo = archivoCargaAdjuntar.getFileName();

        if (SysmanFunciones.validarVariableVacio("")) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3979"));
            return;
        }
        try {
            ruta = ejbSysmanUtil.consultarParametro(compania,
                            "RUTA SERVIDOR DE ANEXOS",
                            Integer.toString(
                                            SysmanConstantes.CODIGO_APLICACION_GENERAL),
                            new Date(),
                            false);

            /*
             * ruta = ruta +
             * ejbHojasDeVidaCero.crearRutaAnexos(compania,
             * Integer.parseInt(modulo), numeroDocumento,
             * reg.getCampos().get(GeneralParameterEnum.CODIGO
             * .getName()).toString());
             */

            // ruta = verificarRuta(ruta);

            // JsfUtil.upload(archivoCargaAdjuntar.getInputstream(),
            // ruta);

            acturalizarRutaAdjunto(ruta, reg);
            reasignarOrigen();

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4083"));

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private void acturalizarRutaAdjunto(String ruta, Registro reg) {

        Map<String, Object> param = new TreeMap<>();

        try {
            param.put("RUTA", ruta);
            param.put("KEY_COMPANIA", compania);
            param.put("KEY_MODULO", modulo);
            param.put("KEY_NIVEL_AGRUPAMIENTO",
                            reg.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()).toString());

            param.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            UrlBean urlBean = UrlServiceUtil.getUrlBeanById(
                            FrmanexoproyectosControladorUrlEnum.URL0001
                                            .getValue());

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            parameter);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Eliminar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirEliminar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        try {
            param.put("DEBE_ELIMINAR", -1);
            param.put("KEY_COMPANIA", compania);
            param.put("KEY_TIPO",
                            reg.getCampos().get(GeneralParameterEnum.TIPO
                                            .getName()).toString());
            param.put("KEY_NUMERO",
                            reg.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()).toString());
            param.put("KEY_ID_PLAN",
                            reg.getCampos().get(GeneralParameterEnum.ID_PLAN
                                            .getName()).toString());
            param.put("KEY_CODIGO",
                            reg.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()).toString());

            param.put("KEY_VIGENCIA_INICIAL",
                            reg.getCampos().get(
                                            GeneralParameterEnum.VIGENCIA_INICIAL
                                                            .getName())
                                            .toString());

            param.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            UrlBean urlBean = UrlServiceUtil.getUrlBeanById(
                            FrmanexoproyectosControladorUrlEnum.URL0001
                                            .getValue());

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            parameter);

            reasignarOrigen();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Descargar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirDescargar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        String ruta = SysmanFunciones.nvl(reg.getCampos()
                        .get("RUTA_FIN"), "")
                        .toString();

        if (SysmanFunciones.validarVariableVacio(ruta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4332"));
            return;
        }

        File adjunto = new File(ruta);

        try (InputStream inputStream = new FileInputStream(adjunto)) {
            byte[] vec = new byte[(int) adjunto.length()];

            inputStream.read(vec, 0, vec.length);

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), adjunto.getName());
        }
        catch (IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Adjuntar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoAdjuntar(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        try {
            // <CODIGO_DESARROLLADO>
            String nombreArch = event.getFile().getFileName();
            nombreArch = nombreArch.contains(File.separator)
                ? nombreArch.substring(
                                nombreArch.lastIndexOf(File.separator) + 1,
                                nombreArch.length())
                : nombreArch;

            String valor = tipo.concat(idPlan);

            String ruta = generarRuta(rutafinal, valor, carpeta);
            if (ruta != null) {

                registro.getCampos().put("RUTA_FIN",
                                JsfUtil.upload(event.getFile().getInputstream(),
                                                nombreArch,
                                                ruta)
                                                .toString());
                agregarRegistro();
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3042")
                                .replace("#PARAMETER#", rutafinal));
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (IOException ex) {
            Logger.getLogger(DigitalizacionContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        // </CODIGO_DESARROLLADO>
    }
    public void cargarArchivolector(FileUploadEvent event){
   	 //<CODIGO_DESARROLLADO>
   	try {
           // <CODIGO_DESARROLLADO>
           String nombreArch = event.getFile().getFileName();
           nombreArch = nombreArch.contains(File.separator)
               ? nombreArch.substring(
                               nombreArch.lastIndexOf(File.separator) + 1,
                               nombreArch.length())
               : nombreArch;

           String valor = tipo.concat(idPlan);

           String ruta = generarRuta(rutafinal, valor, carpeta);
           if (ruta != null) {

               registro.getCampos().put("RUTA_FIN",
                               JsfUtil.upload(event.getFile().getInputstream(),
                                               nombreArch,
                                               ruta)
                                               .toString());
               agregarRegistro();
           }
           else {
               JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3042")
                               .replace("#PARAMETER#", rutafinal));
           }
           // </CODIGO_DESARROLLADO>
       }
       catch (IOException ex) {
           Logger.getLogger(DigitalizacionContratosControlador.class.getName())
                           .log(Level.SEVERE, null, ex);
       }
   	 //</CODIGO_DESARROLLADO>
   	}
    private void agregarRegistro() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("COMPANIA = ''").append(compania)
                            .append("'' AND TIPO = ''").append(tipo)
                            .append("'' AND NUMERO = ''").append(numero)
                            .append("'' AND ID_PLAN = ''").append(idPlan)
                            .append("'' AND VIGENCIA_INICIAL = ''")
                            .append(vigenciaGubernamental).append("''");
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "PA_ANEXOS_BASE", builder.toString(), "CODIGO",
                            "1");
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);

            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            numero);

            registro.getCampos().put(GeneralParameterEnum.ID_PLAN.getName(),
                            idPlan);

            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            consecutivo);

            registro.getCampos().put(
                            GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                            vigenciaGubernamental);

            registro.getCampos().put("ES_NUEVO", -1);

            registro.getCampos().put("DEBE_ELIMINAR", 0);

            registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registro.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PA_ANEXOS_BASE
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registro.getCampos());

            reasignarOrigen();
        }
        catch (SystemException ex) {
            Logger.getLogger(DigitalizacionContratosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registro = new Registro(new HashMap<String, Object>());
        }

    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public String generarRuta(String parametro, String documento, String ruta) {
        String strRuta;
        String carpeta = ruta;
        String placa = documento.replace(" ", "");
        if (parametro != null) {
            strRuta = parametro + carpeta + File.separator + placa
                + File.separator;
            File folder = new File(strRuta);
            folder.mkdirs();
            return strRuta;
        }
        else {
            return null;
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2109-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 67, Me.Name parRutaArchivos =
         * par("RUTA ANEXOS INDICADORES META", Getcompany()) If
         * IsNull(parRutaArchivos) Then MsgBox
         * "El parámetro RUTA ANEXOS INDICADORES META no se ha configurado, por favor verificar"
         * , vbCritical, "Sysman Software" End If If
         * esUsuarioConsulta() Then Me.AllowAdditions = False
         * Me.AllowDeletions = False Me.AllowEdits = False End If End
         * Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2109-ANTES_ACTUALIZAR Private Sub
         * Form_BeforeUpdate(Cancel As Integer) If Me.NewRecord Then
         * If Me.Ruta = "" Then Cancel = True Exit Sub End If
         * Me!Codigo = genConsecutivo2 End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
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
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2109-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As
         * Integer) ' If
         * MsgBox("Este proceso eliminará el archivo almacenado como anexo. ¿Esta seguro de eliminar el archivo?"
         * , vbYesNo, "Sysman Software") = vbYes Then ' FileDelete
         * Me.Ruta ' Me!txtDebeEliminar = -1 ' Cancel = True '
         * Forms!FRM_ANEXO_INDICADORMETA.Requery ' End If End Sub
         */
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
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
    	Map<String, Object> parametros = new HashMap<>();
        parametros.put("vigencia", vigenciaGubernamental);
        parametros.put("tipo", tipo);
        parametros.put("administrador", esAdministrador);
        parametros.put("jefeUnidad", esJefeUnidad);
        parametros.put((GeneralParameterEnum.DEPENDENCIA.getName().toLowerCase()), dependencia);

        parametros.put("numero", numero);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
    	Map<String, Object> parametros = new HashMap<>();
        parametros.put("vigencia", vigenciaGubernamental);
        parametros.put("tipo", tipo);
        parametros.put("administrador", esAdministrador);
        parametros.put("jefeUnidad", esJefeUnidad);
        parametros.put((GeneralParameterEnum.DEPENDENCIA.getName().toLowerCase()), dependencia);

        parametros.put("numero", numero);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // TODO Auto-generated method stub
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo
     * the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the idPlanIndicativo
     */
    public String getIdPlanIndicativo() {
        return idPlanIndicativo;
    }

    /**
     * @param idPlanIndicativo
     * the idPlanIndicativo to set
     */
    public void setIdPlanIndicativo(String idPlanIndicativo) {
        this.idPlanIndicativo = idPlanIndicativo;
    }

    /**
     * @return the vigenciaInicial
     */
    public String getVigenciaInicial() {
        return vigenciaInicial;
    }

    /**
     * @param vigenciaInicial
     * the vigenciaInicial to set
     */
    public void setVigenciaInicial(String vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero
     * the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the esAdministrador
     */
    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    /**
     * @param esAdministrador
     * the esAdministrador to set
     */
    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    /**
     * @return the esJefeUnidad
     */
    public boolean isEsJefeUnidad() {
        return esJefeUnidad;
    }

    /**
     * @param esJefeUnidad
     * the esJefeUnidad to set
     */
    public void setEsJefeUnidad(boolean esJefeUnidad) {
        this.esJefeUnidad = esJefeUnidad;
    }

    /**
     * @return the vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * @param vigencia
     * the vigencia to set
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * @return the vigenciaGubernamental
     */
    public String getVigenciaGubernamental() {
        return vigenciaGubernamental;
    }

    /**
     * @param vigenciaGubernamental
     * the vigenciaGubernamental to set
     */
    public void setVigenciaGubernamental(String vigenciaGubernamental) {
        this.vigenciaGubernamental = vigenciaGubernamental;
    }

    /**
     * @return the digitosAccion
     */
    public int getDigitosAccion() {
        return digitosAccion;
    }

    /**
     * @param digitosAccion
     * the digitosAccion to set
     */
    public void setDigitosAccion(int digitosAccion) {
        this.digitosAccion = digitosAccion;
    }

    /**
     * @return the idPlan
     */
    public String getIdPlan() {
        return idPlan;
    }

    /**
     * @param idPlan
     * the idPlan to set
     */
    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    /**
     * @return the valorTotalEstimado
     */
    public String getValorTotalEstimado() {
        return valorTotalEstimado;
    }

    /**
     * @param valorTotalEstimado
     * the valorTotalEstimado to set
     */
    public void setValorTotalEstimado(String valorTotalEstimado) {
        this.valorTotalEstimado = valorTotalEstimado;
    }

    /**
     * @return the dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * @param dependencia
     * the dependencia to set
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * @return the digitosMetaProducto
     */
    public int getDigitosMetaProducto() {
        return digitosMetaProducto;
    }

    /**
     * @param digitosMetaProducto
     * the digitosMetaProducto to set
     */
    public void setDigitosMetaProducto(int digitosMetaProducto) {
        this.digitosMetaProducto = digitosMetaProducto;
    }

    /**
     * @return the esUsuarioConsulta
     */
    public boolean isEsUsuarioConsulta() {
        return esUsuarioConsulta;
    }

    /**
     * @param esUsuarioConsulta
     * the esUsuarioConsulta to set
     */
    public void setEsUsuarioConsulta(boolean esUsuarioConsulta) {
        this.esUsuarioConsulta = esUsuarioConsulta;
    }

    /**
     * @return the vigenciaFinal
     */
    public String getVigenciaFinal() {
        return vigenciaFinal;
    }

    /**
     * @param vigenciaFinal
     * the vigenciaFinal to set
     */
    public void setVigenciaFinal(String vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    /**
     * @return the nombrePlan
     */
    public String getNombrePlan() {
        return nombrePlan;
    }

    /**
     * @param nombrePlan
     * the nombrePlan to set
     */
    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    /**
     * @return the predecesor
     */
    public String getPredecesor() {
        return predecesor;
    }

    /**
     * @param predecesor
     * the predecesor to set
     */
    public void setPredecesor(String predecesor) {
        this.predecesor = predecesor;
    }

    /**
     * @return the nomPlanAdquisiciones
     */
    public String getNomPlanAdquisiciones() {
        return nomPlanAdquisiciones;
    }

    /**
     * @param nomPlanAdquisiciones
     * the nomPlanAdquisiciones to set
     */
    public void setNomPlanAdquisiciones(String nomPlanAdquisiciones) {
        this.nomPlanAdquisiciones = nomPlanAdquisiciones;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
