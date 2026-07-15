
package com.sysman.presupuesto;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.presupuesto.enums.ProgramarpacsControladorEnum;
import com.sysman.presupuesto.enums.ProgramarpacsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 22/06/2016
 * @modified jguerrero
 * @version 2. 204/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Ademï¿½s se ajustaron los errores del sonar
 * @modified amonroy
 * @version 2.1. 24/04/2017 Se realiza el reemplazo del llamado a la
 * clase Acciones por los correspondientes EJBs
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio cï¿½digo formulario y actualizaciï¿½n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class ProgramarpacsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String pacProgramadoCons;
    private final String modificacionesPacCons;
    private final String pacApropiado;
    private final String parametroDistribuirCons;
    private final String saldoApropiacionCons;
    private final String apropiado;
    private final String formato;

    private int indice;
    // <DECLARAR_ATRIBUTOS>
    private String apropiacionPptal;
    private String rezago;
    private String titulo;
    private String totalPacMes;
    private String totalMod;
    private String totalPACpro;
    private String totalReg;
    private String totalEje;
    private String mesInicio;
    private String mesAfecta;
    private boolean bloquea;
    private Registro regEdit;
    private double auxTotalPacMes;
    private double auxTotalMod;
    private String auxCodigo;
    private double pacMes;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPresupuestoDosRemote ejbPresupuestoDos;

    @EJB
    private EjbGeneralesRemote ejbGenerales;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> rid;
    private String anio;
    private String codigo;
    private String nombre;
    private String naturaleza;
    private String apropiacionVigente;
    private String modificaciones;
    Map<String, Object> parametrosEntrada;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
	
	private String adiciones;
	private String reducciones;
	private String traslados;

    /**
     * Creates a new instance of ProgramarpacsControlador
     */
    public ProgramarpacsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        pacProgramadoCons = "PAC_PROGRAMADO";
        modificacionesPacCons = "MODIFICACIONESPAC";
        pacApropiado = "PAC_APROPIADO";
        parametroDistribuirCons = "DISTRIBUIR PAC TENIENDO EN CUENTA MODIFICACIONES";
        saldoApropiacionCons = "SALDOAPROPIACION";
        apropiado = "APROPIADO";
        formato = "$#,##0.00";
        try {
            numFormulario = GeneralCodigoFormaEnum.PROGRAMARPACS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = (String) parametrosEntrada.get("anio");
                codigo = (String) parametrosEntrada.get("codigo");
                nombre = (String) parametrosEntrada.get("nombre");
                naturaleza = (String) parametrosEntrada.get("naturaleza");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ProgramarpacsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = ProgramarpacsControladorEnum.PARAM3.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        regEdit = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProgramarpacsControladorUrlEnum.URL27174
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProgramarpacsControladorUrlEnum.URL27175
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarmesInicio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesesAfecta() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = idioma.getString("TB_TB233") + " " + nombre.toUpperCase();
        mesAfecta = "0";
        mesInicio = "1";
        actualizarTotales();
        calcularRezago();
        calcularApropiacionPptal();
        if (("SI".equals(getParametro(
                        "MANEJA RESTRICCION EN PROGRAMACION DE PAC", "NO")))
            && (SessionUtil.getNivelUsuario(
                            SessionUtil.getModulo()) != 9)) {
            bloquea = true;
        }
        else {
            bloquea = false;
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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

        registro.getCampos().remove("MESN");
        registro.getCampos().remove(pacProgramadoCons);
        registro.getCampos().remove("REGISTRO");
        registro.getCampos().remove("EJECUTADO");
        registro.getCampos().remove(modificacionesPacCons);

        return verificarRezago();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales();
        calcularRezago();
        calcularApropiacionPptal();
        try {
            ejbPresupuestoDos.mayorizarPacApropiado(compania,
                            Integer.parseInt(anio), auxCodigo);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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

    @Override
    public void removerCombos() {
        // Metodo heredado de la clase BeanBase
        auxCodigo = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.CENTRO_COSTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove("AUXILIAR");
        registro.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.FUENTE_RECURSO.getName());
        registro.getCampos().remove("MES");

    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }

   
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public boolean verificar() {
        boolean respuesta = true;
        double dPAcapropiado = SysmanFunciones
                        .redondear(auxTotalPacMes + auxTotalMod, 2);
        Registro regAux;

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramarpacsControladorUrlEnum.URL23487
                                                                            .getValue())
                                            .getUrl(), param));

            if ("SI".equals(getParametro("OBLIGA CONTROLAR PAC EN REGISTRO",
                            "NO"))) {
                if (SysmanFunciones.redondear(dPAcapropiado,
                                2) < SysmanFunciones
                                                .redondear(Double.parseDouble(
                                                                regAux.getCampos()
                                                                                .get("REGISTROS")
                                                                                .toString()),
                                                                2)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB969")
                        + " " + new java.text.DecimalFormat(formato)
                                        .format(SysmanFunciones
                                                        .redondear(Double
                                                                        .parseDouble(regAux
                                                                                        .getCampos()
                                                                                        .get("REGISTROS")
                                                                                        .toString()),
                                                                        2)
                                            - dPAcapropiado));
                    respuesta = false;
                }

                if (SysmanFunciones.redondear(dPAcapropiado,
                                2) < SysmanFunciones
                                                .redondear(Double.parseDouble(
                                                                regAux.getCampos()
                                                                                .get("REGISTROS")
                                                                                .toString()),
                                                                2)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB970")
                        + " " + new java.text.DecimalFormat(formato)
                                        .format(SysmanFunciones
                                                        .redondear(Double
                                                                        .parseDouble(regAux
                                                                                        .getCampos()
                                                                                        .get("REGISTROS")
                                                                                        .toString()),
                                                                        2)
                                            - dPAcapropiado));
                    respuesta = false;
                }

            }

            if ("SI".equals(getParametro("OBLIGA CONTROLAR PAC EN EGRESO",
                            "NO"))
                && !"C".equals(naturaleza) && (dPAcapropiado < SysmanFunciones
                                .redondear(Double.parseDouble(regAux.getCampos()
                                                .get("EJECUCIONPPT")
                                                .toString()), 2))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB971") + " "
                    + new java.text.DecimalFormat(formato)
                                    .format(SysmanFunciones
                                                    .redondear(Double
                                                                    .parseDouble(regAux
                                                                                    .getCampos()
                                                                                    .get("EJECUCIONPPT")
                                                                                    .toString()),
                                                                    2)
                                        - dPAcapropiado));
                respuesta = false;
            }
        }
        catch (NumberFormatException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    public boolean verificarRezago() {
        try {
            boolean rta;

            if (Integer.parseInt(regEdit.getCampos().get("MES")
                            .toString()) < Integer.parseInt(mesInicio)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB962"));
                return false;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
            param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
            param.put("MES", regEdit.getCampos().get("MES"));
            Registro regAux;

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramarpacsControladorUrlEnum.URL23607
                                                                            .getValue())
                                            .getUrl(), param));

            auxTotalPacMes = Double.parseDouble(regAux.getCampos()
                            .get(pacApropiado).toString().replace(",", ""));
            auxTotalMod = Double.parseDouble(
                            regAux.getCampos().get(modificacionesPacCons)
                                            .toString().replace(",", ""));

            auxTotalPacMes = (auxTotalPacMes - pacMes)
                + Double.parseDouble(listaInicial.getDatasource()
                                .get(indice % 10).getCampos()
                                .get(pacApropiado).toString());
            BigDecimal aux;

            aux = ejbGenerales.consultarEjecucionPresupuestal(compania,
                    apropiado, Integer.parseInt(anio),
                    codigo, 12);

            if ("NO".equals(getParametro(parametroDistribuirCons, "NO"))) {
            	 if ((Double.parseDouble(aux.toString()) - auxTotalPacMes)>= 0) {
                         rta = true;
                     }
                     else {
                         JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB954") + " "
                             + new java.text.DecimalFormat(formato)
                                             .format(Math.abs((Double
                                                             .parseDouble(aux.toString())
                                                 - auxTotalPacMes) + auxTotalMod)));
                         return false;
                     }
            
            } else{
            	 if (((Double.parseDouble(aux.toString()) - auxTotalPacMes)
                         + auxTotalMod) >= 0) {
                         rta = true;
                     }
                     else {
                         JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB954") + " "
                             + new java.text.DecimalFormat(formato)
                                             .format(Math.abs((Double
                                                             .parseDouble(aux.toString())
                                                 - auxTotalPacMes) + auxTotalMod)));
                         return false;
                     }
            }
           

            verificar();
            return rta;

        }
        catch (SystemException e) {
            Logger.getLogger(ProgramarpacsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

    }

    public void actualizarTotales() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);

        Registro regAux;
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramarpacsControladorUrlEnum.URL27601
                                                                            .getValue())
                                            .getUrl(), param));
            totalPacMes = regAux.getCampos().get(pacApropiado).toString();
            totalPACpro = regAux.getCampos().get(pacProgramadoCons).toString();
            totalMod = regAux.getCampos().get(modificacionesPacCons).toString();
            totalReg = regAux.getCampos().get("REGISTRO").toString();
            totalEje = regAux.getCampos().get("EJECUTADO").toString();
        
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void calcularRezago() {
        try {
            BigDecimal aux;

            aux = ejbGenerales.consultarEjecucionPresupuestal(compania,
                    apropiado, Integer.parseInt(anio), codigo, 12);

            double auxTotPacMes = Double
                            .parseDouble(totalPacMes.replace(",", ""));
            double auxTotMod = Double.parseDouble(totalMod.replace(",", ""));
            if ("NO".equals(getParametro(parametroDistribuirCons, "NO"))) {
            	  rezago = String.valueOf(
                          (Double.parseDouble(aux.toString())
                              - auxTotPacMes) );
            }else{
            	rezago = String.valueOf(
                        (Double.parseDouble(aux.toString())
                            - auxTotPacMes) + auxTotMod);
            }
            
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(PacsaldopptalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void calcularApropiacionPptal() {
        try {
            BigDecimal aux;

           
            aux = ejbGenerales.consultarEjecucionPresupuestal(compania,
                    apropiado, Integer.parseInt(anio), codigo, 12);
            
            apropiacionPptal = String.valueOf(aux);
            

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
            param.put(GeneralParameterEnum.MESINICIAL.getName(), mesInicio);
            param.put(GeneralParameterEnum.MESFINAL.getName(), 12);

            Registro regAux;
         
                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                		ProgramarpacsControladorUrlEnum.URL129005
                                                                                .getValue())
                                                .getUrl(), param));
           
               
               adiciones = regAux.getCampos().get("ADICION").toString().replace(".00", "").replaceAll(",", "");
               reducciones = regAux.getCampos().get("REDUCCION").toString().replace(".00", "").replaceAll(",", "");
               traslados = regAux.getCampos().get("TRASLADO").toString().replace(".00", "").replaceAll(",", "");
               
                        
	           	// Convierte la cadena resultante en un valor double.        
	           	double adicionesDouble = Double.parseDouble(adiciones);    
	           	double reduccionesDouble = Double.parseDouble(reducciones);    
	           	double trasladosDouble = Double.parseDouble(traslados);    
                double totalmod = (adicionesDouble + reduccionesDouble + trasladosDouble);

               // Crea un objeto DecimalFormat con el formato deseado
               DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
               symbols.setGroupingSeparator(',');
               symbols.setDecimalSeparator('.');
               DecimalFormat formato = new DecimalFormat("#,##0.00", symbols);

               // Formatea el número y lo muestra en el formato deseado
               modificaciones = formato.format(totalmod);
               apropiacionVigente = regAux.getCampos().get("APROPIACIONVIGENTE").toString();

        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(PacsaldopptalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void activarEdicion(Registro registroAux) {
        indice = listaInicial.getRowIndex();
        HashMap<String, Object> auxReg = new HashMap<>();
        auxReg.putAll(registroAux.getCampos());
        regEdit = new Registro(auxReg);
        pacMes = Double.parseDouble(SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get(pacApropiado),
                        "").toString());

        if (!"0".equals(mesAfecta)
            && !SysmanFunciones.validarVariableVacio(mesAfecta)) {
            double auxapro = Double
                            .parseDouble(apropiacionPptal.replace(",", ""));

            listaInicial.getDatasource().get(indice % 10).getCampos().put(
                            pacApropiado,
                            SysmanFunciones.redondear(
                                            auxapro
                                                / Integer.parseInt(mesAfecta),
                                            2));
        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro que se desea buscar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de que el valor sea nulo
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    
    // <SET_GET_ATRIBUTOS>
    public String getApropiacionPptal() {
        return apropiacionPptal;
    }

    public void setApropiacionPptal(String apropiacionPptal) {
        this.apropiacionPptal = apropiacionPptal;
    }

    public String getRezago() {
        return rezago;
    }

    public void setRezago(String rezago) {
        this.rezago = rezago;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTotalPacMes() {
        return totalPacMes;
    }

    public void setTotalPacMes(String totalPacMes) {
        this.totalPacMes = totalPacMes;
    }

    public String getTotalMod() {
        return totalMod;
    }

    public void setTotalMod(String totalMod) {
        this.totalMod = totalMod;
    }

    public String getTotalPACpro() {
        return totalPACpro;
    }

    public void setTotalPACpro(String totalPACpro) {
        this.totalPACpro = totalPACpro;
    }

    public String getTotalReg() {
        return totalReg;
    }

    public void setTotalReg(String totalReg) {
        this.totalReg = totalReg;
    }

    public String getTotalEje() {
        return totalEje;
    }

    public void setTotalEje(String totalEje) {
        this.totalEje = totalEje;
    }

    public String getMesInicio() {
        return mesInicio;
    }

    public void setMesInicio(String mesInicio) {
        this.mesInicio = mesInicio;
    }

    public String getMesAfecta() {
        return mesAfecta;
    }

    public void setMesAfecta(String mesAfecta) {
        this.mesAfecta = mesAfecta;
    }

    public boolean isBloquea() {
        return bloquea;
    }

    public void setBloquea(boolean bloquea) {
        this.bloquea = bloquea;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public Registro getRegEdit() {
        return regEdit;
    }

    public void setRegEdit(Registro regEdit) {
        this.regEdit = regEdit;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

	/**
	 * @return the apropiacionVigente
	 */
	public String getApropiacionVigente() {
		return apropiacionVigente;
	}

	/**
	 * @param apropiacionVigente the apropiacionVigente to set
	 */
	public void setApropiacionVigente(String apropiacionVigente) {
		this.apropiacionVigente = apropiacionVigente;
	}

	/**
	 * @return the modificaciones
	 */
	public String getModificaciones() {
		return modificaciones;
	}

	/**
	 * @param modificaciones the modificaciones to set
	 */
	public void setModificaciones(String modificaciones) {
		this.modificaciones = modificaciones;
	}

	/**
	 * @return the reducciones
	 */
	public String getReducciones() {
		return reducciones;
	}

	/**
	 * @param reducciones the reducciones to set
	 */
	public void setReducciones(String reducciones) {
		this.reducciones = reducciones;
	}

	/**
	 * @return the adiciones
	 */
	public String getAdiciones() {
		return adiciones;
	}

	/**
	 * @param adiciones the adiciones to set
	 */
	public void setAdiciones(String adiciones) {
		this.adiciones = adiciones;
	}

	/**
	 * @return the traslados
	 */
	public String getTraslados() {
		return traslados;
	}

	/**
	 * @param traslados the traslados to set
	 */
	public void setTraslados(String traslados) {
		this.traslados = traslados;
	}
    
    
}
