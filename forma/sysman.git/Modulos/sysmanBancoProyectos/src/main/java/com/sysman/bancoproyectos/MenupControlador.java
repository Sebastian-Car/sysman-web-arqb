package com.sysman.bancoproyectos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 05/01/2016
 */
@ManagedBean
@ViewScoped
public class MenupControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String user;
    private final String cVigencia;
    private final String cGenConsecutivo;
    private final String cCodigo;
    private final String cDescripcion;
    private final String cIdPro;
    private final String cAcme;
    private boolean codProyVisible;
    private List<Registro> listaNomProyecto;
    private List<Registro> listacmbAccion;
    private String objProyecto;
    private String nProyecto;
    private String idProyecto;
    private String accionActual;
    private String codProy;
    private String plano;
    private boolean conexionVisible;
    private boolean manejaPlanDeAccion;
    private int enc;
    private StreamedContent archivoDescarga;
    private String tituloEspecial;

    /**
     * Creates a new instance of MenupControlador
     */
    public MenupControlador() {
        super();
        numFormulario = 442;
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        user = SessionUtil.getUser().getCodigo();
        cVigencia = "VIGENCIA";
        cGenConsecutivo = "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO";
        cCodigo = "CODIGO";
        cDescripcion = "DESCRIPCION";
        cIdPro = "IDPRO";
        cAcme = "PCK_DATOS.FC_ACME";
        accionActual = "";
        
        tituloEspecial = idioma.getString("TG_SYSMAN_SOFTWARE_OPCIONES_ESPECIALES");
        tituloEspecial = tituloEspecial.replace("s$empresaparam$s", JsfUtil.obtenerParametroMarcaBlanca("TITULOESPECIAL"));
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(MenupControlador.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        conexionVisible = false;
        ConectorPool a = new ConectorPool();
        try {
            manejaPlanDeAccion = "SI".equals(SysmanFunciones.nvlStr(
                            Acciones.getParametro(ConectorPool.ESQUEMA_SYSMAN,
                                            compania, "MANEJA PLAN DE ACCION",
                                            SessionUtil.getModulo(), "SYSDATE"),
                            "")) ? true : false;

            a.conectar(ConectorPool.ESQUEMA_MGA);
            a.getConection().close();
        }
        catch (SQLException | NamingException ex) {
            conexionVisible = true;
            Logger.getLogger(MenupControlador.class.getName()).log(Level.SEVERE,
                            null, ex);
            return;
        }

        cargarListaNomProyecto();
        cargarListacmbAccion();
        abrirFormulario();
    }

    public String getCodProy() {
        return codProy;
    }

    public void setCodProy(String codProy) {
        this.codProy = codProy;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getAccionActual() {
        return accionActual;
    }

    public void setAccionActual(String accionActual) {
        this.accionActual = accionActual;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isConexionVisible() {
        return conexionVisible;
    }

    public void setConexionVisible(boolean conexionVisible) {
        this.conexionVisible = conexionVisible;
    }

    public boolean isManejaPlanDeAccion() {
        return manejaPlanDeAccion;
    }

    public void setManejaPlanDeAccion(boolean manejaPlanDeAccion) {
        this.manejaPlanDeAccion = manejaPlanDeAccion;
    }

    public void cargarListaNomProyecto() {
        listaNomProyecto = service.getListado(ConectorPool.ESQUEMA_MGA,
                        "SELECT DISTINCT"
                            + " 	GEN_PROYECTOS.ID,"
                            + " 	GEN_PROYECTOS.NOMBRE,"
                            + " 	IDE_F05.OBJETIVOGENERAL"
                            + " FROM GEN_PROYECTOS"
                            + " 	INNER JOIN IDE_F05 ON GEN_PROYECTOS.ID = IDE_F05.IDPROYECTO");
    }

    public void cargarListacmbAccion() {
        listacmbAccion = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT BP_PLAN_INDICATIVO.COMPANIA,BP_PLAN_INDICATIVO.ID,BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,"
                            +
                            "   BP_PLAN_INDICATIVO.DESCRIPCION" +
                            " FROM BP_PLAN_INDICATIVO" +
                            " INNER JOIN BP_NIVEL_PLAN_IND" +
                            " ON BP_PLAN_INDICATIVO.COMPANIA          = BP_NIVEL_PLAN_IND.COMPANIA"
                            +
                            " AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_NIVEL_PLAN_IND.VIGENCIA"
                            +
                            " WHERE BP_PLAN_INDICATIVO.COMPANIA       = '"
                            + compania + "'" +
                            " AND LENGTH(BP_PLAN_INDICATIVO.ID)       = BP_NIVEL_PLAN_IND.DIGITOS"
                            +
                            " AND NVL(BP_PLAN_INDICATIVO.CODIGOBPIM,'N') ='N' "
                            +
                            " AND BP_NIVEL_PLAN_IND.ACCION NOT IN(0)");
    }

    public void oprimirBuscar() {
        // <CODIGO_DESARROLLADO>
        if (manejaPlanDeAccion && "".equals(accionActual)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2369"));
        }
        else {
            copiarProyectoMGA();
            archivoDescarga = null;
            if (enc > 0) {
                ByteArrayInputStream aux;
                try {
                    aux = JsfUtil.serializarPlano(plano);
                    archivoDescarga = JsfUtil.getArchivoDescarga(aux,
                                    "inconsistencias__mga");
                }
                catch (JRException | IOException ex) {
                    Logger.getLogger(MenupControlador.class.getName())
                                    .log(Level.SEVERE, null, ex);
                }

            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirBDExterna(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarRegistrarProy() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaNomProyecto() {
        return listaNomProyecto;
    }

    public void setListaNomProyecto(List<Registro> listaNomProyecto) {
        this.listaNomProyecto = listaNomProyecto;
    }

    public boolean isCodProyVisible() {
        return codProyVisible;
    }

    public void setCodProyVisible(boolean codProyVisible) {
        this.codProyVisible = codProyVisible;
    }

    public String getObjProyecto() {
        return objProyecto;
    }

    public void setObjProyecto(String objProyecto) {
        this.objProyecto = objProyecto;
    }

    public String getnProyecto() {
        return nProyecto;
    }

    public void setnProyecto(String nProyecto) {
        this.nProyecto = nProyecto;
    }

    public String getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(String idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }

    public int getEnc() {
        return enc;
    }

    public void setEnc(int enc) {
        this.enc = enc;
    }

    public List<Registro> getListacmbAccion() {
        return listacmbAccion;
    }

    public void setListacmbAccion(List<Registro> listacmbAccion) {
        this.listacmbAccion = listacmbAccion;
    }

    @Override
    public void abrirFormulario() {
        // Acciones adicionales al abrir el formulario
    }

    /**
     * M�todo definido para realizar inserciones en las tablas
     * "PROYECTOS" y "BP_PLAN_INDICATIVO"
     * 
     * @param parametros
     * parametros para realizar la insercion en la tabla "PROYECTOS"
     * @param codigoBpim
     * valor del codigobpim
     */
    private void copiarProyectoTry1(String parametrosProyectos,
        String codigoBpim) {
        try {
            Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            cAcme, parametrosProyectos, Types.VARCHAR);

            // (vmolano - 13/06/2016): si maneja plan de acci�n,
            // entonces debe relacionar el proyecto con la acci�n
            // correspondiente mediante el CODIGOBPIM
            if (manejaPlanDeAccion) {
                String parametrosPlan = "'BP_PLAN_INDICATIVO','M','CODIGOBPIM=''"
                    + codigoBpim + "''', NULL, '" + accionActual + "' ";
                Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                cAcme, parametrosPlan,
                                Types.VARCHAR);
                cargarListacmbAccion();
            }

        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            plano = plano
                + "Los datos generales del proyecto no ha sido registrados, por favor verificar los datos."
                + "\r\n";
            enc = enc + 1;
            Logger.getLogger(MenupControlador.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
        }
    }

    /**
     * M�todo definido para realizar inserciones en la tabla
     * "BP_PROYFUENTESFINANCIACION"
     * 
     * @param bld
     * StringBuilder que est� armando el archivo plano
     * @param parametros
     * define los par�metros que se enviaran a la funci�n ACME
     */
    private void copiarProyectoTry2(StringBuilder bld, String parametros) {
        try {
            Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            cAcme, parametros,
                            Types.VARCHAR);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            bld.append(
                            plano
                                + "Error al insertar en BP_PROYFUENTESFINANCIACION."
                                + "\r\n");
            enc = enc + 1;
            Logger.getLogger(MenupControlador.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
        }
    }

    /**
     * M�todo definido para realizar inserciones en la tabla
     * "BP_PROYFUENTESFINANCIACION"
     * 
     * @param bld
     * StringBuilder que est� armando el archivo plano
     * @param parametros
     * define los par�metros que se enviaran a la funci�n ACME
     */
    private void copiarProyectoTry3(StringBuilder bld, String parametros) {
        try {
            Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            cAcme, parametros,
                            Types.VARCHAR);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            bld.append(
                            plano
                                + "Error al insertar en BP_PROYFUENTESFINANCIACION."
                                + "\r\n");
            enc = enc + 1;
            Logger.getLogger(MenupControlador.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
        }
    }

    /**
     * M�todo definido para realizar inserciones en la tabla
     * "COMPONENTES"
     * 
     * @param bld
     * StringBuilder que est� armando el archivo plano
     * @param parametros
     * define los par�metros que se enviaran a la funci�n ACME
     */
    private void copiarProyectoTry4(StringBuilder bld, String parametros) {
        try {
            Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            cAcme, parametros,
                            Types.VARCHAR);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            bld.append(plano
                + "Los datos de los componentes del proyecto no ha sido registrados correctamente, por favor verificar los datos."
                + "\r\n");
            enc = enc + 1;
            Logger.getLogger(MenupControlador.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
        }
    }

    /**
     * M�todo definido para realizar inserciones en la tabla
     * "COMPONENTES_ACTIVIDADES"
     * 
     * @param bld
     * StringBuilder que est� armando el archivo plano
     * @param parametros
     * define los par�metros que se enviaran a la funci�n ACME
     */
    private void copiarProyectoTry5(StringBuilder bld, String parametros) {
        try {
            Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            cAcme,
                            parametros, Types.VARCHAR);
        }
        catch (IllegalAccessException
                        | InstantiationException
                        | ClassNotFoundException
                        | SQLException
                        | NamingException ex) {
            bld.append(plano
                + "Los datos de las actividades del proyecto no ha sido registradas correctamente, por favor verificar los datos."
                + "\r\n");
            enc = enc + 1;
            Logger.getLogger(MenupControlador.class
                            .getName())
                            .log(Level.SEVERE,
                                            null, ex);
        }
    }

    /**
     * Evalua si los campos ingresados por parametro vienen vacios
     * 
     * @param codigo
     * valor de c�digo que se va a evaluar
     * @param codigoBpim
     * valor de c�digo BPIM que se va a evaluar
     * @return verdadero o falso si los campos vienen vacios
     */
    private boolean evaluarCondicion(String codigo, String codigoBpim) {
        return SysmanFunciones.validarVariableVacio(codigo)
            || SysmanFunciones.validarVariableVacio(codigoBpim);
    }

    /**
     * Evalua si los listados de registros que ingrean por parametro
     * vienen vac�os
     * 
     * @param auxB
     * listado de registros auxB
     * @param auxC
     * listado de registros auxC
     * @return verdadero o falso si las listas vienen vacias
     */
    private boolean validarVacios(List<Registro> auxB, List<Registro> auxC) {
        return auxB.isEmpty() || auxC.isEmpty();
    }

    public void copiarProyectoMGA() {
        StringBuilder bld = new StringBuilder();
        try {

            plano = "---------------------------- POR FAVOR REVISAR LOS SIGUIENTES DATOS ------------------------"
                + "\r\n";
            nProyecto = service.buscarEnLista(idProyecto, "ID", "NOMBRE",
                            listaNomProyecto);
            objProyecto = service.buscarEnLista(idProyecto, "ID",
                            "OBJETIVOGENERAL", listaNomProyecto);
            int anio = SysmanFunciones.getParteFecha(
                            Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN),
                            Calendar.YEAR);
            String fecha = SysmanFunciones
                            .formatearFecha(Acciones
                                            .getSysDate(ConectorPool.ESQUEMA_SYSMAN))
                            .replace("'", "''");
            enc = 0;

            String parametros;
            String campos;
            String valores;
            String strConsecutivoInicial;
            String strCodEntidad;
            String valorProyecto;
            String vigenciaInicial;
            String vigenciaFinal;
            String codigoBpim;
            String codigo;
            String codigoComp;
            String codigoFinan;
            String actividad;
            String entidad;
            String tipop;
            String fuentefin;
            double valorunicomp;
            List<Registro> aux;
            List<Registro> auxB;
            List<Registro> auxC;
            String localizacion;
            String aux2;

            parametros = "'" + compania
                + "','CONSECUTIVO INICIAL DE PROYECTOS'," + modulo + ",SYSDATE";
            strConsecutivoInicial = (String) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SYSMAN_UTL.FC_PAR", parametros,
                            Types.VARCHAR);
            if (strConsecutivoInicial == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2371"));
                return;
            }

            parametros = "'" + compania + "','CODIGO ENTIDAD DNP'," + modulo
                + ",SYSDATE";
            strCodEntidad = (String) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SYSMAN_UTL.FC_PAR", parametros,
                            Types.VARCHAR);

            if (strCodEntidad == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2372"));
                return;
            }

            // Se obtiene codigo del proyecto y el CodigoBPIM
            if (!codProyVisible) {
                parametros = "'PROYECTOS','COMPANIA=''" + compania
                    + "'' AND SUBSTR(CODIGO,1,4)=''" + anio + "''','CODIGO','"
                    + strConsecutivoInicial + "'";
                codigo = (String) Acciones.ejecutarFuncion(
                                ConectorPool.ESQUEMA_SYSMAN,
                                cGenConsecutivo, parametros,
                                Types.VARCHAR);
            }
            else {
                aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                                " SELECT \n"
                                    + "  COUNT(*) CUENTA\n"
                                    + "FROM PROYECTOS\n"
                                    + "WHERE PROYECTOS.CODIGO='" + codProy
                                    + "'");
                aux2 = ((BigDecimal) aux.get(0).getCampos().get("CUENTA"))
                                .toString();

                if (!"0".equals(aux2)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2373"));
                    return;
                }
                else if (codProy.length() == 8) {
                    codigo = codProy;
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2374"));
                    return;
                }
            }

            codigoBpim = anio + strCodEntidad
                + codigo.substring(codigo.length() - 4, codigo.length());

            if (evaluarCondicion(codigo, codigoBpim)) {
                plano = plano + "Revisar el c�digo del proyecto." + "\r\n";
                enc = 1;
                return;
            }

            // Se obtiene el Valor Total del del Proyecto, Vigencia
            // inicial y Vigencia Final C
            aux = service.getListado(ConectorPool.ESQUEMA_MGA, "SELECT  \n"
                + "	PRO_F02_FINANCIACION.IDF02,\n"
                + "	MIN(PRO_F02_FINANCIACIONDETALLE.VIGENCIA) MINDEVIGENCIA,\n"
                + "	MAX(PRO_F02_FINANCIACIONDETALLE.VIGENCIA) MAXDEVIGENCIA,\n"
                + "	SUM(PRO_F02_FINANCIACIONDETALLE.VALOR) SUMADEVALOR\n"
                + "FROM PRO_F02_FINANCIACION \n"
                + "	INNER JOIN PRO_F02_FINANCIACIONDETALLE \n"
                + "	ON PRO_F02_FINANCIACION.ID = PRO_F02_FINANCIACIONDETALLE.IDF02FINANCIACION \n"
                + "WHERE PRO_F02_FINANCIACION.IDF02 = 3\n"
                + "GROUP BY PRO_F02_FINANCIACION.IDF02");

            if (aux.isEmpty()) {
                plano = plano
                    + "Revisar los siguientes datos del proyecto: valor, vigencia inicial y vigencia final."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                valorProyecto = aux.get(0).getCampos().get("SUMADEVALOR")
                                .toString();
                vigenciaInicial = aux.get(0).getCampos().get("MINDEVIGENCIA")
                                .toString();
                vigenciaFinal = aux.get(0).getCampos().get("MAXDEVIGENCIA")
                                .toString();
            }

            // Selecci�n de entidad proponente (Se cambio de tabla
            // ENTIDADES A BP_ENTIDADES)
            aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, "SELECT  \n"
                + "  BP_ENTIDADES.CODIGO,\n"
                + "  BP_ENTIDADES.NOMBRE\n"
                + "FROM BP_ENTIDADES\n"
                + "WHERE BP_ENTIDADES.NOMBRE LIKE '%MUNICIPIO%'");

            if (aux.isEmpty()) {
                plano = plano + "Revisar los datos de la entidad proponente."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                entidad = aux.get(0).getCampos().get(cCodigo).toString();
            }

            // Selecci�n de tipo de proyecto
            aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, "SELECT    \n"
                + "  MIN(TIPOSPROYECTO.CODIGO) CODIGO\n"
                + "FROM TIPOSPROYECTO");

            if (aux.isEmpty()) {
                plano = plano + "Revisar los datos del tipo de proyecto."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                tipop = (String) aux.get(0).getCampos().get(cCodigo);
            }

            // Seleccionar Fuente Financiaci�n
            aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, "SELECT    \n"
                + "  FUENTESFINANCIACION.CODIGOFUENTE,\n"
                + "  FUENTESFINANCIACION.NOMBRE\n"
                + "FROM FUENTESFINANCIACION\n"
                + "WHERE FUENTESFINANCIACION.NOMBRE LIKE '%VARIOS%' \n"
                + "OR FUENTESFINANCIACION.NOMBRE LIKE '%MUNICIPIO%'");

            if (aux.isEmpty()) {
                plano = plano
                    + "Revisar los datos de la(s) fuente(s) de financiaci�n."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                fuentefin = aux.get(0).getCampos().get("CODIGOFUENTE")
                                .toString();
            }

            campos = "COMPANIA, CODIGO, TIPOPROYECTO , CODIGOBPIM, NOMBREPROYECTO, ENTIDADPROPONENTE, VIGENCIAINICIO,VIGENCIAFIN,\n"
                + "UNIDAD, ESTADOACTUAL, PORCEJECUCION, INTERVENTOR, VALORSOLICITADO, VALORDEPARTAMENTO, VALORFONDONAL, VALORCOMUNIDAD, VALOROTROS,\n"
                + "ANOSVIDA, PLAZO, PERIOCIDAD, VALOREJECUTADO, VALORPROGRAMADO, FECHAREGISTRO, ESTADONACION, ESTADODPTO, ESTADOOTRAENTIDAD, CONPROGRAMACION,\n"
                + "ENVIADONACION, ENVIADODPTO, ENVIADOOTRAENTIDAD, TIPOREGISTRO, MODIFIED_BY, DATE_MODIFIED, CREATED_BY, DATE_CREATED,\n"
                + "FECHA_ACTUALIZACION, FINANCIADO_REGALIAS, PROCESOFINANCIERO, VALORTOTAL, IREGISTRADO,OBJETO";
            valores = "''" + compania + "'' , ''" + codigo + "'' , ''" + tipop
                + "'' ,''" + codigoBpim + "'', ''" + nProyecto + "''  ,''"
                + entidad
                + "'',  " + vigenciaInicial + "," + vigenciaFinal
                + ",''No.'',''S'',0,''00000000000'',0,0,0,0,0,"
                + "0,0,''01'',0,0," + fecha + ",''S'',''S'',''S'',0,"
                + "0,0,0,''REG'',''" + user + "''," + fecha + ",''" + user
                + "''," + fecha + ","
                + fecha + ",0,0," + valorProyecto + ",0,''" + objProyecto
                + "''";
            parametros = "'PROYECTOS','I','" + campos + "'" + ",'" + valores
                + "'";

            copiarProyectoTry1(parametros, codigoBpim);

            // Se obtiene la informaci�n de la financiaci�n del
            // proyecto
            aux = service.getListado(ConectorPool.ESQUEMA_MGA, "SELECT   \n"
                + "	PRO_F02_FINANCIACIONDETALLE.VIGENCIA VIGENCIA,\n"
                + "	PRO_F02_FINANCIACIONDETALLE.VALOR VALOR\n"
                + "FROM (PRO_F02 INNER JOIN PRO_F02_FINANCIACION \n"
                + "		ON PRO_F02.ID = PRO_F02_FINANCIACION.IDF02)\n"
                + "	INNER JOIN PRO_F02_FINANCIACIONDETALLE \n"
                + "		ON PRO_F02_FINANCIACION.ID = PRO_F02_FINANCIACIONDETALLE.IDF02FINANCIACION \n"
                + "WHERE PRO_F02.IDPROYECTO=" + idProyecto);

            if (aux.isEmpty()) {
                plano = plano
                    + "Error al obtener la informaci�n de la financiaci�n del proyecto."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                for (Registro aux1 : aux) {
                    parametros = "'BP_PROYFUENTESFINANCIACION','COMPANIA=''"
                        + compania + "'' AND PROYECTO=''" + codigo
                        + "'' AND CODIGOFUENTE=''" + fuentefin
                        + "''','CONSECUTIVO',1";
                    codigoFinan = (String) Acciones.ejecutarFuncion(
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    cGenConsecutivo,
                                    parametros, Types.VARCHAR);
                    campos = "COMPANIA, PROYECTO,CODIGOFUENTE,VALOR,CONSECUTIVO,FECHACREADO,VIGENCIAFUENTE,MODIFIED_BY, DATE_MODIFIED, CREATED_BY, DATE_CREATED";
                    valores = "''" + compania + "''  ,''" + codigo + "'',  ''"
                        + fuentefin + "'',"
                        + aux1.getCampos().get("VALOR").toString()
                        + "," + codigoFinan + "," + fecha + ","
                        + aux1.getCampos().get(cVigencia).toString() + ",''"
                        + user
                        + "''," + fecha + ",''" + user + "''," + fecha;
                    parametros = "'BP_PROYFUENTESFINANCIACION','I','" + campos
                        + "'" + ",'" + valores + "'";

                    copiarProyectoTry3(bld, parametros);
                }
            }

            // Se obtiene la localizaci�n del proyecto
            aux = service.getListado(ConectorPool.ESQUEMA_MGA,
                            "SELECT TOP 1\n" +
                                "    PRE_F04_LOCALIZACION.IDMUNICIPIO MUNICIPIO,\n"
                                +
                                "    PRE_F04_LOCALIZACION.LOCALIZACION,\n" +
                                "    LIS_DETALLE.DESCRIPCION\n" +
                                "FROM PRE_F04_LOCALIZACION \n" +
                                "    INNER JOIN LIS_DETALLE\n" +
                                "        ON PRE_F04_LOCALIZACION.IDMUNICIPIO=LIS_DETALLE.ID\n"
                                +
                                "    INNER JOIN PRE_F04\n" +
                                "        ON PRE_F04_LOCALIZACION.IDF04=PRE_F04.ID\n"
                                +
                                "    INNER JOIN IDE_F01\n" +
                                "        ON PRE_F04.IDF01=IDE_F01.ID\n" +
                                "WHERE IDE_F01.IDPROYECTO=" + idProyecto);

            if (aux.isEmpty()) {
                plano = plano + "Error al obtener localizaci�n del proyecto."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                for (Registro aux1 : aux) {
                    localizacion = aux1.getCampos().get(cDescripcion)
                                    .toString();
                    aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                                    "SELECT \n"
                                        + "  PAIS,\n"
                                        + "  DEPARTAMENTO,\n"
                                        + "  CODIGO,\n"
                                        + "  NOMBRE\n"
                                        + "FROM CIUDAD\n"
                                        + "WHERE PCK_SYSMAN_UTL.FC_QUITAR_TILDES(UPPER(NOMBRE))=PCK_SYSMAN_UTL.FC_QUITAR_TILDES(UPPER('"
                                        + localizacion + "'))");

                    if (aux.isEmpty()) {
                        bld.append(plano
                            + "No se encuentraron datos de la localizaci�n del proyecto."
                            + "\r\n");
                        enc = 1;
                    }
                    else {
                        campos = "COMPANIA,CODIGOPROYECTO,PAIS,DEPARTAMENTO,CIUDAD,BARRIO,CODIGO,UBICACION,MODIFIED_BY, DATE_MODIFIED, CREATED_BY, DATE_CREATED";
                        valores = "''" + compania + "''  ,  ''" + codigo
                            + "''  ,  ''"
                            + aux.get(0).getCampos().get("PAIS").toString()
                            + "''   ,''"
                            + aux.get(0).getCampos().get("DEPARTAMENTO")
                                            .toString()
                            + "'',   ''"
                            + aux.get(0).getCampos().get(cCodigo).toString()
                            + "'',''038''," + "''001''," + "''"
                            + SysmanFunciones.nvlStr(
                                            (String) aux1.getCampos()
                                                            .get("LOCALIZACION"),
                                            "")
                            + "''"
                            + ",''" + user + "''," + fecha + ",''" + user
                            + "''," + fecha;

                        parametros = "'PROYECTOLOCALIZACION','I','" + campos
                            + "'" + ",'" + valores + "'";

                        copiarProyectoTry2(bld, parametros);

                    }
                }

            }

            // Insertar componentes y actividades del proyecto
            aux = service.getListado(ConectorPool.ESQUEMA_MGA,
                            "SELECT DISTINCT\n"
                                + "	PRE_F07_PRODUCTOS.DESCRIPCION,\n"
                                + "	PRE_F07_PRODUCTOS.ID IDPRO,\n"
                                + "	PRE_F07_INSUMOS.VIGENCIA \n"
                                + "FROM PRE_F07_PRODUCTOS INNER JOIN PRE_F07_OBJETIVOS \n"
                                + "		ON PRE_F07_PRODUCTOS.IDOBJETIVO = PRE_F07_OBJETIVOS.ID\n"
                                + "	INNER JOIN PRE_F07 \n"
                                + "		ON PRE_F07_OBJETIVOS.IDF07 = PRE_F07.ID\n"
                                + "	INNER JOIN PRE_F01 \n"
                                + "		ON PRE_F07.IDF01 = PRE_F01.ID\n"
                                + "	INNER JOIN PRE_F07_ACTIVIDADES \n"
                                + "		ON PRE_F07_PRODUCTOS.ID = PRE_F07_ACTIVIDADES.IDPRODUCTO \n"
                                + "	INNER JOIN PRE_F07_INSUMOS  \n"
                                + "		ON PRE_F07_ACTIVIDADES.ID = PRE_F07_INSUMOS.IDACTIVIDAD \n"
                                + "WHERE PRE_F01.IDPROYECTO=" + idProyecto);

            if (aux.isEmpty()) {
                plano = plano
                    + "Error al obtener la informaci�n los componentes de la BD MGA."
                    + "\r\n";
                enc = 1;
                return;
            }
            else {
                for (Registro aux1 : aux) {

                    auxB = service.getListado(ConectorPool.ESQUEMA_MGA,
                                    "SELECT COUNT(PRE_F07_ACTIVIDADES.ID) CANTIDAD\n"
                                        + "FROM PRE_F07_PRODUCTOS \n"
                                        + "	INNER JOIN PRE_F07_ACTIVIDADES \n"
                                        + "	ON PRE_F07_PRODUCTOS.ID = PRE_F07_ACTIVIDADES.IDPRODUCTO\n"
                                        + "WHERE PRE_F07_PRODUCTOS.ID='"
                                        + aux1.getCampos().get(cIdPro)
                                                        .toString()
                                        + "'");

                    auxC = service.getListado(ConectorPool.ESQUEMA_MGA,
                                    "SELECT \n"
                                        + "	SUM(PRE_F07_INSUMOS.VALOR) TOTAL\n"
                                        + "FROM PRE_F07_PRODUCTOS INNER JOIN PRE_F07_ACTIVIDADES\n"
                                        + "		ON PRE_F07_PRODUCTOS.ID = PRE_F07_ACTIVIDADES.IDPRODUCTO\n"
                                        + "	INNER JOIN PRE_F07_INSUMOS \n"
                                        + "		ON PRE_F07_ACTIVIDADES.ID = PRE_F07_INSUMOS.IDACTIVIDAD\n"
                                        + "WHERE PRE_F07_PRODUCTOS.ID="
                                        + aux1.getCampos().get(cIdPro)
                                                        .toString()
                                        + " \n"
                                        + "	AND PRE_F07_INSUMOS.VIGENCIA =  '"
                                        + aux1.getCampos().get(cVigencia)
                                                        .toString()
                                        + "'");

                    if (validarVacios(auxB, auxC)) {
                        bld.append(plano
                            + "Error al obtener valor del componente dependiendo la vigencia."
                            + "\r\n");
                        enc = 1;
                        return;
                    }
                    else {
                        valorunicomp = SysmanFunciones.redondear(Double
                                        .parseDouble(auxC.get(0).getCampos()
                                                        .get("TOTAL")
                                                        .toString())
                            / Double.parseDouble(auxB.get(0).getCampos()
                                            .get("CANTIDAD").toString()),
                                        0);
                    }

                    parametros = "'COMPONENTES','COMPANIA=''" + compania
                        + "'' AND CODIGOPROYECTO=''" + codigo
                        + "''','CODIGO',1";
                    codigoComp = (String) Acciones.ejecutarFuncion(
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    cGenConsecutivo,
                                    parametros, Types.VARCHAR);
                    campos = "COMPANIA,CODIGOPROYECTO,CODIGO,TIPOCOMPONENTE,NOMBRECOMPONENTE, UNIDAD,CANTIDAD,VALORUNITARIO,VALORTOTAL,CREATED_BY,DATE_CREATED,MODIFIED_BY,DATE_MODIFIED,VIGENCIA,OBJETO";
                    valores = "''" + compania + "''   ,''" + codigo + "'',   ''"
                        + codigoComp + "'',''000060'',''"
                        + aux1.getCampos().get(cDescripcion).toString()
                        + "'',''UND'',''"
                        + auxB.get(0).getCampos().get("CANTIDAD").toString()
                        + "''   ,  ''" + valorunicomp + "''  ,   ''"
                        + auxC.get(0).getCampos().get("TOTAL").toString()
                        + "'' ,  ''" + user
                        + "''," + fecha + ",''" + user + "''," + fecha + ","
                        + aux1.getCampos().get(cVigencia).toString()
                        + ",''" + aux1.getCampos().get(cDescripcion).toString()
                        + "''";
                    parametros = "'COMPONENTES','I','" + campos + "'" + ",'"
                        + valores + "'";

                    copiarProyectoTry4(bld, parametros);

                    // Se procede a registrar las actividades
                    // Se buscan las actividades correspondientes al
                    // componente
                    auxB = service.getListado(ConectorPool.ESQUEMA_MGA,
                                    "SELECT DISTINCT \n"
                                        + "	PRE_F07_ACTIVIDADES.DESCRIPCION,\n"
                                        + "	PRE_F07_ACTIVIDADES.ID IDACT\n"
                                        + "FROM PRE_F07_PRODUCTOS INNER JOIN PRE_F07_ACTIVIDADES \n"
                                        + "		ON PRE_F07_PRODUCTOS.ID = PRE_F07_ACTIVIDADES.IDPRODUCTO\n"
                                        + "	INNER JOIN PRE_F07_INSUMOS \n"
                                        + "		ON PRE_F07_ACTIVIDADES.ID = PRE_F07_INSUMOS.IDACTIVIDAD\n"
                                        + "WHERE PRE_F07_PRODUCTOS.ID="
                                        + aux1.getCampos().get(cIdPro) + "\n"
                                        + "	AND PRE_F07_INSUMOS.VIGENCIA = '"
                                        + aux1.getCampos().get(cVigencia)
                                        + "'");

                    if (aux.isEmpty()) {
                        bld.append(plano
                            + "Error al buscar actividades correspondientes al componente."
                            + "\r\n");
                        enc = 1;
                        return;
                    }
                    else {
                        for (Registro aux11 : auxB) {
                            auxC = service.getListado(ConectorPool.ESQUEMA_MGA,
                                            "SELECT SUM(PRE_F07_INSUMOS.VALOR) VALACT\n"
                                                + "FROM PRE_F07_ACTIVIDADES INNER JOIN PRE_F07_INSUMOS \n"
                                                + "	ON PRE_F07_ACTIVIDADES.ID = PRE_F07_INSUMOS.IDACTIVIDAD\n"
                                                + "WHERE PRE_F07_ACTIVIDADES.ID="
                                                + aux11.getCampos().get("IDACT")
                                                + "\n"
                                                + "	AND PRE_F07_INSUMOS.VIGENCIA = '"
                                                + aux1.getCampos()
                                                                .get(cVigencia)
                                                + "'");

                            if (auxC.isEmpty()) {
                                bld.append(plano
                                    + "Error al obtener valor el filtro de vigencia para componente actividades."
                                    + "\r\n");
                                enc = 1;
                                return;
                            }
                            else {
                                parametros = "'" + compania + "','"
                                    + aux11.getCampos().get(cDescripcion)
                                    + "','" + user + "','" + fecha
                                    + "'";
                                actividad = (String) Acciones.ejecutarFuncion(
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                "PCK_BANCOS_PROY4.FC_BUSCARACTIVIDAD",
                                                parametros, Types.VARCHAR);
                                campos = "COMPANIA, CODIGOPROYECTO,COMPONENTE,TIPOCOMPONENTE,ACTIVIDAD,COSTOUNITARIO,COSTOTOTAL,PRIORIDAD,CANTIDAD,DESCRIPCION,VIGENCIA,NOMBREACTIVIDAD,MODIFIED_BY, DATE_MODIFIED, CREATED_BY, DATE_CREATED";
                                valores = "''" + compania + "''  , ''" + codigo
                                    + "''  ,   ''" + codigoComp
                                    + "'',''000060'',''"
                                    + actividad
                                    + "''   ,   ''" + auxC.get(0).getCampos()
                                                    .get("VALACT").toString()
                                    + "''   ,   ''"
                                    + auxC.get(0).getCampos().get("VALACT")
                                                    .toString()
                                    + "'',''5'',''1'',''"
                                    + aux11.getCampos().get(cDescripcion)
                                    + "'',''" + aux1.getCampos().get(cVigencia)
                                    + "'',''"
                                    + aux11.getCampos().get(cDescripcion)
                                    + "''" + ",''" + user + "''," + fecha
                                    + ",''" + user + "''," + fecha;
                                parametros = "'COMPONENTES_ACTIVIDADES','I','"
                                    + campos + "'" + ",'" + valores + "'";

                                copiarProyectoTry5(bld, parametros);
                            }
                        }
                    }
                }
            }

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2375").replace("#$codigo#$",
                                            codigo.substring(
                                                            codigo.length() - 4,
                                                            codigo.length())));
            plano += bld.toString();

        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(MenupControlador.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

	public String getTituloEspecial() {
		return tituloEspecial;
	}

	public void setTituloEspecial(String tituloEspecial) {
		this.tituloEspecial = tituloEspecial;
	}

}
