/*-
 * MedidorGeoreferencialControlador.java
 *
 * 1.0
 * 
 * 25/10/2016
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.MedidorGeoreferencialControladorEnum;
import com.sysman.serviciospublicos.enums.MedidorGeoreferencialControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase permite generar infomes tanto en formato pdf como en
 * formato excel teniendo encuenta el ciclo el suscriptor y el vertice
 *
 * @version 1.0, 25/10/2016
 * @author jguerrero
 * 
 * @author ybecerra
 * @version 2, 09/06/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class MedidorGeoreferencialControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String codigoRutaCons;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del combo ciclo Inicial
     */
    private String cicloInicial;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del combo ciclo Inicial
     */
    private String cicloFinal;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del combo VerticeInicial
     */
    private String verticeInicial;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del combo codigoRutaInicial
     */
    private String codigoRutaInicial;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del combo codigoRutaFinal
     */
    private String codigoRutaFinal;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del combo VerticeFinal
     */
    private String verticeFinal;
    /**
     * Atributo usado para cargar los combos del ciclo en la vista.
     */

    private boolean cargaCiclos;
    /**
     * Atributo usado para cargar los combos del codigoRutaen la
     * vista.
     */
    private boolean cargaCodigoRutas;
    /**
     * Atributo usado para cargar los combos del vertic en la vista.
     */
    private boolean cargaVerticeInicial;

    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del check todos ademas oculta todos los combos del formulario
     */

    private boolean checkTodos;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del check vertice y muestra los combos relacionados a dicho
     * vertice
     */

    private boolean checkVertice;
    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del check codigoRuta y muestra los combos relacionados a dicho
     * codigoRuta o suscriptor en la vista
     */

    private boolean checkCodigoRuta;

    /**
     * Variable que permite almancenar temporalmente lo seleccionaldo
     * del check ciclo muestra los combos relacionados a dicho ciclo
     */

    private boolean checkCiclo;
    /**
     * Atributo que permite la descarga de archivos
     *
     */

    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena temporalmente el titulo del reporte
     */

    private String titulo;
    /**
     * Atributo que permite que el dialogo se muestre o no
     */

    private boolean dialogVisible;
    /**
     * Atributo que que permite identificar el tipo de descarga, es
     * decir si se desea descargar los dos archivos o solo uno en el
     * comprimido
     * 
     */

    private boolean tipoDescarga;
    /**
     * Atributo que identificar el tipo de boton oprimido, es decir
     * pdf o excel para la generacaion de los informes
     */

    private boolean tipoBoton;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que almacena el resultado de la consulta ciclo y los
     * muestra en el respectivo combo
     * 
     */
    private List<Registro> listaCicloIni;
    /**
     * Lista que almacena el resultado de la consulta ciclo y los
     * muestra en el respectivo combo
     * 
     */
    private List<Registro> listaCicloFin;
    /**
     * Lista que almacena el resultado de la consulta vertice y los
     * muestra en el respectivo combo
     * 
     */
    private List<Registro> listaVerticeIni;
    /**
     * Lista que almacena el resultado de la consulta vertice y los
     * muestra en el respectivo combo
     * 
     */
    private List<Registro> listaVerticeFin;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena el resultado de la consulta codigoRuta y los
     * muestra en el respectivo combo
     * 
     */
    private RegistroDataModelImpl listaCodRutaIni;
    /**
     * Lista que almacena el resultado de la consulta codigoRuta y los
     * muestra en el respectivo combo
     * 
     */
    private RegistroDataModelImpl listaCodRutaFin;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de MedidorGeoreferencialControlador
     */
    public MedidorGeoreferencialControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoDescarga = false;
        codigoRutaCons = GeneralParameterEnum.CODIGORUTA.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.MEDIDOR_GEOREFERENCIAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            checkTodos = true;
            cargaCiclos = false;
            cargaCodigoRutas = false;
            cargaVerticeInicial = false;

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCicloIni();
        cargarListaVerticeIni();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodRutaIni();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCICLOIni
     *
     * Metodo se que encarga de hacer la peticion a la base de datos y
     * traer los datos correspondientes al ciclo
     */
    public void cargarListaCicloIni() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCicloIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidorGeoreferencialControladorUrlEnum.URL8568
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCICLOFin
     *
     * Metodo se que encarga de hacer la peticion a la base de datos y
     * traer los datos correspondientes al ciclo
     */
    public void cargarListaCicloFin() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MedidorGeoreferencialControladorEnum.PARAM0.getValue(),
                        String.valueOf(cicloInicial));

        try {
            listaCicloFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidorGeoreferencialControladorUrlEnum.URL9253
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaVERTICEIni
     *
     * Metodo se que encarga de hacer la peticion a la base de datos y
     * traer los datos correspondientes al vertice en la tabla
     * sp_medidor
     */
    public void cargarListaVerticeIni() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVerticeIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidorGeoreferencialControladorUrlEnum.URL10053
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaVERTICEFin
     *
     * Metodo se que encarga de hacer la peticion a la base de datos y
     * traer los datos correspondientes al codigoruta y usuario en la
     * tabla sp_usuario
     */
    public void cargarListaVerticeFin() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MedidorGeoreferencialControladorEnum.PARAM1.getValue(),
                        String.valueOf(verticeInicial));

        try {
            listaVerticeFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidorGeoreferencialControladorUrlEnum.URL10814
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCODRUTAIni
     *
     * Metodo se que encarga de hacer la peticion a la base de datos y
     * traer los datos correspondientes al vertice en la tabla
     * sp_medidor
     */
    public void cargarListaCodRutaIni() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if (checkCiclo) {
            param.put(MedidorGeoreferencialControladorEnum.PARAM0.getValue(),
                            cicloInicial);
            param.put(MedidorGeoreferencialControladorEnum.PARAM2.getValue(),
                            cicloFinal);
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MedidorGeoreferencialControladorUrlEnum.URL11679
                                                            .getValue());

            listaCodRutaIni = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoRutaCons);

        }
        else {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MedidorGeoreferencialControladorUrlEnum.URL444
                                                            .getValue());

            listaCodRutaIni = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoRutaCons);
        }
    }

    /**
     * 
     * Carga la lista listaCODRUTAFin
     *
     * Metodo se que encarga de hacer la peticion a la base de datos y
     * traer los datos correspondientes al codigoruta y usuario en la
     * tabla sp_usuario
     */
    public void cargarListaCodRutaFin() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MedidorGeoreferencialControladorEnum.PARAM3.getValue(),
                        codigoRutaInicial);

        if (checkCiclo) {
            param.put(MedidorGeoreferencialControladorEnum.PARAM0.getValue(),
                            cicloInicial);
            param.put(MedidorGeoreferencialControladorEnum.PARAM2.getValue(),
                            cicloFinal);
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MedidorGeoreferencialControladorUrlEnum.URL12634
                                                            .getValue());
            listaCodRutaFin = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoRutaCons);
        }
        else {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MedidorGeoreferencialControladorUrlEnum.URL490
                                                            .getValue());
            listaCodRutaFin = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoRutaCons);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     *
     * Metodo que se ejectua al oprimir el boton de pdf en la vista,
     * ademas ejecuta el metodo revisarMedRepetidos que recibe como
     * parametro un formato
     *
     */
    public void oprimirPantalla() {
        // <CODIGO_DESARROLLADO>
        tipoBoton = true;
        archivoDescarga = null;

        if (validarCasillas()) {
            return;
        }
        revisarMedRepetidos(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo que se ejectua al oprimir el boton de pdf en la vista,
     * ademas ejecuta el metodo revisarMedRepetidos que recibe como
     * parametro un formato *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        tipoBoton = false;
        if (validarCasillas()) {
            return;
        }
        revisarMedRepetidos(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el check todos, ademas oculta todos
     * combos del formulario
     * 
     *
     * 
     */
    public void cambiarTodos() {
        // <CODIGO_DESARROLLADO>
        if (checkTodos) {
            cargaCiclos = cargaCodigoRutas = cargaVerticeInicial = checkCiclo = checkCodigoRuta = checkVertice = false;
            codigoRutaInicial = codigoRutaFinal = cicloInicial = cicloFinal = verticeInicial = verticeFinal = null;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CICLO, ademas muestra
     * los combos de ciclos respectivamente relacionados y cambia el
     * estado al check todos.
     * 
     * 
     * 
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        if (checkCiclo) {
            checkTodos = false;
            cargaCiclos = true;

        }
        else {
            cargaCiclos = false;
            cicloInicial = null;
            cicloFinal = null;
        }

        cargarListaCodRutaIni();
        cargarListaCodRutaFin();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * 
     * Metodo ejecutado al cambiar el control Vertice, ademas muestra
     * los combos de Vertice respectivamente relacionados y cambia el
     * estado al check todos.
     * 
     */
    public void cambiarVertice() {
        // <CODIGO_DESARROLLADO>
        if (checkVertice) {
            checkTodos = false;
            cargaVerticeInicial = true;

        }
        else {
            cargaVerticeInicial = false;
            verticeInicial = null;
            verticeFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * 
     * Metodo ejecutado al cambiar el control Vertice, ademas muestra
     * los combos de Vertice respectivamente relacionados y cambia el
     * estado al check todos.
     * 
     */
    public void cambiarCodRuta() {
        // <CODIGO_DESARROLLADO>
        if (checkCodigoRuta) {
            checkTodos = false;
            cargaCodigoRutas = true;

        }
        else {
            cargaCodigoRutas = false;
            codigoRutaInicial = null;
            codigoRutaFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCicloIni() {
        // <CODIGO_DESARROLLADO>
        cicloFinal = null;
        cargarListaCicloFin();
        cargarListaCodRutaIni();
        cargarListaCodRutaFin();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCicloFin() {
        codigoRutaInicial = null;
        codigoRutaFinal = null;
        cargarListaCodRutaIni();

    }

    /**
     * Metodo ejecutado al cambiar el control VERTICEIni
     * 
     * 
     * 
     */
    public void cambiarVerticeIni() {
        // <CODIGO_DESARROLLADO>
        verticeFinal = null;
        cargarListaVerticeFin();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control confirmacion
     * 
     * 
     */
    public void cambiarconfirmacion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_ADICIONALES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * confirmacion en la vista. cabia el tipo de descarga a true para
     * que genere los dos archivos en el comprimido. ademas tiene una
     * validacion para que identifique en que tipo de formato genere
     * el informe
     *
     *
     */
    public void aceptarconfirmacion() {
        // <CODIGO_DESARROLLADO>
        tipoDescarga = true;
        archivoDescarga = null;

        if (tipoBoton) {

            generarComprimidoInforme(ReportesBean.FORMATOS.PDF);
        }
        else {
            generarComprimidoInforme(ReportesBean.FORMATOS.EXCEL);
        }

        dialogVisible = false;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo en el
     * cual pone la variable tipo descarga en false que a su vez
     * permite que en el comprimido solo sea generado la hoja de
     * datos.
     *
     *
     */
    public void cancelarconfirmacion() {
        // <CODIGO_DESARROLLADO>

        tipoDescarga = false;
        generarComprimidoInforme(ReportesBean.FORMATOS.PDF);
        dialogVisible = false;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * @param formato
     */
    public void genInforme(ReportesBean.FORMATOS formato) {
        String reporte = "001172rptMedidoresGeore";

        try {

            String[] parametroCiclo = { "AND SP_MEDIDOR.CICLO BETWEEN ",
                                        cicloInicial, " AND ", cicloFinal, ""

            };
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("ciclo", checkCiclo
                ? SysmanFunciones.concatenar(parametroCiclo) : "");
            String[] parametroCodigoRuta = { "AND SP_MEDIDOR.CODIGORUTA BETWEEN '",
                                             codigoRutaInicial, "'  AND '",
                                             codigoRutaFinal, "'"

            };
            reemplazar.put("codigoRuta",
                            checkCodigoRuta
                                ? SysmanFunciones
                                                .concatenar(parametroCodigoRuta)
                                : "");

            String[] parametroVertice = { "AND SP_MEDIDOR.GEO_VERTICE BETWEEN '",
                                          verticeInicial, "' AND  '",
                                          verticeFinal, "'"

            };
            reemplazar.put("vertice", checkVertice
                ? SysmanFunciones.concatenar(parametroVertice)
                : "");
            generarTitutloReporte();

            parametros.put("PR_TITULO", titulo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte),
                            "<br>", ex.getMessage()));
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * @param formato
     */
    public void revisarMedRepetidos(ReportesBean.FORMATOS formato) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        List<Registro> registros;
        try {
            registros = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidorGeoreferencialControladorUrlEnum.URL809
                                                                            .getValue())
                                            .getUrl(), param));

            if (!registros.isEmpty()) {
                dialogVisible = true;

            }
            else {

                genInforme(formato);

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * @param formato
     */
    public void generarComprimidoInforme(ReportesBean.FORMATOS formato) {
        String informe = "001172rptMedidoresGeore";
        enviarParametrosyReemplazar(informe, ConectorPool.ESQUEMA_SYSMAN,
                        formato);
    }

    /**
     * @param informe
     * @param nombreConexion
     * @param formato
     */
    private void enviarParametrosyReemplazar(String informe,
        String nombreConexion,
        FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            String[] parametroCiclo = { "AND SP_MEDIDOR.CICLO BETWEEN ",
                                        cicloInicial, " AND ", cicloFinal, ""

            };
            reemplazar.put("ciclo", checkCiclo
                ? SysmanFunciones.concatenar(parametroCiclo) : "");
            String[] parametroCodigoRuta = { "AND SP_MEDIDOR.CODIGORUTA BETWEEN '",
                                             codigoRutaInicial, "' AND '",
                                             codigoRutaFinal, "'"

            };
            reemplazar.put("codigoRuta",
                            checkCodigoRuta
                                ? SysmanFunciones
                                                .concatenar(parametroCodigoRuta)
                                : "");
            String[] parametroVertice = { "AND SP_MEDIDOR.GEO_VERTICE BETWEEN '",
                                          verticeInicial, "' AND '",
                                          verticeFinal, "'"

            };
            reemplazar.put("vertice", checkVertice
                ? SysmanFunciones.concatenar(parametroVertice)
                : "");
            generarTitutloReporte();

            parametros.put("PR_TITULO", titulo);

            Reporteador.resuelveConsulta(informe,
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar, parametros);

            ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
            String consulta = Reporteador.resuelveConsulta(
                            "800062MedidoresPorEdades",
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar);

            String[] nombresArchivos = new String[2];

            if (tipoDescarga) {
                salidas[0] = JsfUtil.serializarReporte(informe, parametros,
                                nombreConexion, formato);
                salidas[1] = JsfUtil.serializarHojaDatos(consulta,
                                nombreConexion, FORMATOS.EXCEL);

                nombresArchivos[1] = "Repetidos.xlsx";
                if (formato == ReportesBean.FORMATOS.PDF) {
                    nombresArchivos[0] = "001172rptMedidoresGeore.pdf";

                }
                else {
                    nombresArchivos[0] = "001172rptMedidoresGeore.xlsx";

                }
            }
            else {

                salidas[1] = JsfUtil.serializarHojaDatos(consulta,
                                nombreConexion, FORMATOS.EXCEL);
                nombresArchivos[1] = "Repetidos.xlsx";
            }

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salidas, nombresArchivos);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     */
    private void generarTitutloReporte() {
        titulo = "";
        if (checkTodos) {
            titulo = idioma.getString("TB_TB3232");
        }
        else {
            if (checkCiclo) {
                String[] ciclo = { idioma.getString("TB_TB3233"), " ",
                                   cicloInicial,
                                   " y ",
                                   cicloFinal };
                titulo = SysmanFunciones.concatenar(ciclo);
            }
            if (checkCodigoRuta) {
                String[] suscriptor = { titulo, " ",
                                        idioma.getString("TB_TB3234"), " ",
                                        codigoRutaInicial, " y ",
                                        codigoRutaFinal };
                titulo = SysmanFunciones.concatenar(suscriptor);
            }
            if (checkVertice) {
                String[] vertice = { titulo, " ", idioma.getString("TB_TB3235"),
                                     " ",
                                     verticeInicial, " y ", verticeFinal };
                titulo = SysmanFunciones.concatenar(vertice);
            }
        }

    }

    private boolean validarCasillas() {
        if (!checkTodos && !checkCiclo && !checkCodigoRuta && !checkVertice) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3207"));
            return true;
        }
        return false;
    }

    // </METODOS_ADICIONALES>

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCODRUTAIni
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodRutaIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoRutaInicial = registroAux.getCampos().get(codigoRutaCons) == null
            ? "" : registroAux.getCampos().get(codigoRutaCons).toString();
        codigoRutaFinal = null;

        cargarListaCodRutaFin();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCODRUTAFin
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodRutaFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRutaFinal = registroAux.getCampos().get(codigoRutaCons) == null
            ? "" : registroAux.getCampos().get(codigoRutaCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cicloInicial
     * 
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     * 
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     * 
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     * 
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
    }

    public String getVerticeFinal() {
        return verticeFinal;
    }

    public void setVerticeFinal(String verticeFinal) {
        this.verticeFinal = verticeFinal;
    }

    /**
     * Retorna la variable codigoRutaInicial
     * 
     * @return codigoRutaInicial
     */
    public String getCodigoRutaInicial() {
        return codigoRutaInicial;
    }

    /**
     * Asigna la variable codigoRutaInicial
     * 
     * @param codigoRutaInicial
     * Variable a asignar en codigoRutaInicial
     */
    public void setCodigoRutaInicial(String codigoRutaInicial) {
        this.codigoRutaInicial = codigoRutaInicial;
    }

    /**
     * Retorna la variable codigoRutaFinal
     * 
     * @return codigoRutaFinal
     */
    public String getCodigoRutaFinal() {
        return codigoRutaFinal;
    }

    /**
     * Asigna la variable codigoRutaFinal
     * 
     * @param codigoRutaFinal
     * Variable a asignar en codigoRutaFinal
     */
    public void setCodigoRutaFinal(String codigoRutaFinal) {
        this.codigoRutaFinal = codigoRutaFinal;
    }

    /**
     * Retorna la variable verticeInicial
     * 
     * @return verticeInicial
     */
    public String getVerticeInicial() {
        return verticeInicial;
    }

    /**
     * Asigna la variable verticeInicial
     * 
     * @param verticeInicial
     * Variable a asignar en verticeInicial
     */
    public void setVerticeInicial(String verticeInicial) {
        this.verticeInicial = verticeInicial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable cargaCiclos
     * 
     * @return cargaCiclos
     */
    public boolean isCargaCiclos() {
        return cargaCiclos;
    }

    /**
     * Asigna la variable cargaCiclos
     * 
     * @param cargaCiclos
     * Variable a asignar en cargaCiclos
     */
    public void setCargaCiclos(boolean cargaCiclos) {
        this.cargaCiclos = cargaCiclos;
    }

    /**
     * Retorna la variable cargaCodigoRutas
     * 
     * @return cargaCodigoRutas
     */
    public boolean isCargaCodigoRutas() {
        return cargaCodigoRutas;
    }

    /**
     * Asigna la variable cargaCodigoRutas
     * 
     * @param cargaCodigoRutas
     * Variable a asignar en cargaCodigoRutas
     */
    public void setCargaCodigoRutas(boolean cargaCodigoRutas) {
        this.cargaCodigoRutas = cargaCodigoRutas;
    }

    /**
     * Retorna la variable cargaVerticeInicial
     * 
     * @return cargaVerticeInicial
     */
    public boolean isCargaVerticeInicial() {
        return cargaVerticeInicial;
    }

    /**
     * Asigna la variable cargaVerticeInicial
     * 
     * @param cargaVerticeInicial
     * Variable a asignar en cargaVerticeInicial
     */
    public void setCargaVerticeInicial(boolean cargaVerticeInicial) {
        this.cargaVerticeInicial = cargaVerticeInicial;
    }

    /**
     * Retorna la variable checkTodos
     * 
     * @return checkTodos
     */
    public boolean isCheckTodos() {
        return checkTodos;
    }

    /**
     * Asigna la variable checkTodos
     * 
     * @param checkTodos
     * Variable a asignar en checkTodos
     */
    public void setCheckTodos(boolean checkTodos) {
        this.checkTodos = checkTodos;
    }

    /**
     * Retorna la variable checkVertice
     * 
     * @return checkVertice
     */
    public boolean isCheckVertice() {
        return checkVertice;
    }

    /**
     * Asigna la variable checkVertice
     * 
     * @param checkVertice
     * Variable a asignar en checkVertice
     */
    public void setCheckVertice(boolean checkVertice) {
        this.checkVertice = checkVertice;
    }

    /**
     * Retorna la variable checkCodigoRuta
     * 
     * @return checkCodigoRuta
     */
    public boolean isCheckCodigoRuta() {
        return checkCodigoRuta;
    }

    /**
     * Asigna la variable checkCodigoRuta
     * 
     * @param checkCodigoRuta
     * Variable a asignar en checkCodigoRuta
     */
    public void setCheckCodigoRuta(boolean checkCodigoRuta) {
        this.checkCodigoRuta = checkCodigoRuta;
    }

    /**
     * Retorna la variable checkCiclo
     * 
     * @return checkCiclo
     */
    public boolean isCheckCiclo() {
        return checkCiclo;
    }

    /**
     * Asigna la variable checkCiclo
     * 
     * @param checkCiclo
     * Variable a asignar en checkCiclo
     */
    public void setCheckCiclo(boolean checkCiclo) {
        this.checkCiclo = checkCiclo;
    }

    /**
     * Retorna la variable dialogVisible
     * 
     * @return dialogVisible
     */
    public boolean isDialogVisible() {
        return dialogVisible;
    }

    /**
     * Asigna la variable dialogVisible
     * 
     * @param dialogVisible
     * Variable a asignar en dialogVisible
     */
    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCicloIni
     * 
     * @return listaCicloIni
     */
    public List<Registro> getListaCicloIni() {
        return listaCicloIni;
    }

    /**
     * Asigna la lista listaCicloIni
     * 
     * @param listaCicloIni
     * Variable a asignar en listaCicloIni
     */
    public void setListaCicloIni(List<Registro> listaCicloIni) {
        this.listaCicloIni = listaCicloIni;
    }

    /**
     * Retorna la lista listaCicloFin
     * 
     * @return listaCicloFin
     */
    public List<Registro> getListaCicloFin() {
        return listaCicloFin;
    }

    /**
     * Asigna la lista listaCicloFin
     * 
     * @param listaCicloFin
     * Variable a asignar en listaCicloFin
     */
    public void setListaCicloFin(List<Registro> listaCicloFin) {
        this.listaCicloFin = listaCicloFin;
    }

    /**
     * Retorna la lista listaVerticeIni
     * 
     * @return listaVerticeIni
     */
    public List<Registro> getListaVerticeIni() {
        return listaVerticeIni;
    }

    /**
     * Asigna la lista listaVerticeIni
     * 
     * @param listaVerticeIni
     * Variable a asignar en listaVerticeIni
     */
    public void setListaVerticeIni(List<Registro> listaVerticeIni) {
        this.listaVerticeIni = listaVerticeIni;
    }

    /**
     * Retorna la lista listaVerticeFin
     * 
     * @return listaVerticeFin
     */
    public List<Registro> getListaVerticeFin() {
        return listaVerticeFin;
    }

    /**
     * Asigna la lista listaVerticeFin
     * 
     * @param listaVerticeFin
     * Variable a asignar en listaVerticeFin
     */
    public void setListaVerticeFin(List<Registro> listaVerticeFin) {
        this.listaVerticeFin = listaVerticeFin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodRutaIni
     * 
     * @return listaCodRutaIni
     */
    public RegistroDataModelImpl getListaCodRutaIni() {
        return listaCodRutaIni;
    }

    /**
     * Asigna la lista listaCodRutaIni
     * 
     * @param listaCodRutaIni
     * Variable a asignar en listaCodRutaIni
     */
    public void setListaCodRutaIni(RegistroDataModelImpl listaCodRutaIni) {
        this.listaCodRutaIni = listaCodRutaIni;
    }

    /**
     * Retorna la lista listaCodRutaFin
     * 
     * @return listaCodRutaFin
     */
    public RegistroDataModelImpl getListaCodRutaFin() {
        return listaCodRutaFin;
    }

    /**
     * Asigna la lista listaCodRutaFin
     * 
     * @param listaCodRutaFin
     * Variable a asignar en listaCodRutaFin
     */
    public void setListaCodRutaFin(RegistroDataModelImpl listaCodRutaFin) {
        this.listaCodRutaFin = listaCodRutaFin;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
