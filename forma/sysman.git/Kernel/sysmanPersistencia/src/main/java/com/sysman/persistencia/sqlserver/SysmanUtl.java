package com.sysman.persistencia.sqlserver;

import java.util.Arrays;

/**
 * @class......: SysmanUtl.java
 * 
 * @description: @author.....: José Ignacio Becerra
 * Becerra @version....: 2018.09.01 @comment....:
 * Utilidades @generated..: 2018.05
 */
public class SysmanUtl {

    /**
     * Recibe cadena con código PL/SQL y lo traduce a código SQL
     * 
     * @param strCadena:
     * Contiene código PL/SQL a traducir
     * @return Cadena con la traducción a SQL
     *
     * @author.....: Henry de Jesus Puerto Vasquez @created....:
     * 2018.09.01
     */
    public static String strTraductorOnLine(String strCadena) {
        try {
            strCadena = strCadena.replaceAll("\\|\\|", "+");
            strCadena = strCadena.replaceAll("=NULL", "IS NULL");
            strCadena = strCadena.replaceAll("= NULL", "IS NULL");
            strCadena = strCadena.replaceAll("!=NULL", "IS NOT NULL");
            strCadena = strCadena.replaceAll("CHR\\(", "CHAR(");
            strCadena = strCadena.replaceAll("DEFAULT ", "= ");
            strCadena = strCadena.replaceAll("EXTRACT \\(Year from ", "YEAR(");
            strCadena = strCadena.replaceAll("EXTRACT \\(Month from ",
                            "MONTH(");
            strCadena = strCadena.replaceAll("EXTRACT \\(Day from ", "DAY(");

            strCadena = strCadena.replaceAll("EXTRACT \\(Year from DATE\\)",
                            "DATEPART(yyyy, DATE)");
            strCadena = strCadena.replaceAll("EXTRACT \\(Month from DATE\\)",
                            "DATEPART(mm, DATE)");
            strCadena = strCadena.replaceAll("EXTRACT \\(Day from DATE\\)",
                            "DATEPART(dd, DATE)");
            strCadena = strCadena.replaceAll("EXTRACT \\(YEAR from DATE\\)",
                            "DATEPART(yyyy, DATE)");
            strCadena = strCadena.replaceAll("EXTRACT \\(MONTH from DATE\\)",
                            "DATEPART(mm, DATE)");
            strCadena = strCadena.replaceAll("EXTRACT \\(DAY from DATE\\)",
                            "DATEPART(dd, DATE)");

            strCadena = strCadena.replaceAll("NVL\\(", "ISNULL(");
            strCadena = strCadena.replaceAll("SYSDATE", "GETDATE()");
            strCadena = strCadena.replaceAll(
                            "SYS_CONTEXT('USERENV', 'SESSIONID')", "@@SPID");
            strCadena = strCadena.replaceAll("TO_NUMBER\\(",
                            "CONVERT(NUMERIC, ");
            strCadena = strCadena.replaceAll("==", "=");
            strCadena = strCadena.replaceAll("LENGTH\\(", "LEN(");
            strCadena = strCadena.replaceAll("LENGTH \\(", "LEN(");
            strCadena = strCadena.replaceAll("SUBSTR\\(", "SUBSTRING(");
            strCadena = strCadena.replaceAll("SUBSTR \\(", "SUBSTRING(");
            strCadena = strCadena.replaceAll("FROM DUAL", "");

            strCadena = strCadena.replaceAll("ADD_MONTHS\\(", "ADD_MONTHS(");
            strCadena = strCadena.replaceAll("LAST_DAY", "LAST_DAY");
            strCadena = strCadena.replaceAll("INSTR\\(", "INSTR(");
            strCadena = strCadena.replaceAll("LPAD\\(", "LPAD(");
            strCadena = strCadena.replaceAll("LPAD \\(", "LPAD(");
            strCadena = strCadena.replaceAll("RPAD\\(", "RPAD(");
            strCadena = strCadena.replaceAll("RPAD \\(", "RPAD(");
            strCadena = strCadena.replaceAll("PAD\\(", "PAD(");
            strCadena = strCadena.replaceAll("PAD \\(", "PAD(");
            strCadena = strCadena.replaceAll("TRIM\\(", "TRIM(");
            strCadena = strCadena.replaceAll("TRIM \\(", "TRIM(");

            strCadena = strCadena.replaceAll("ROW_NUMBER\\(", "ROWNUM(");
            strCadena = strCadena.replaceAll("NUMBER\\(", "NUMERIC(");
            strCadena = strCadena.replaceAll("ROWNUM\\(", "ROW_NUMBER(");
            strCadena = strCadena.replaceAll("VARCHAR2", "VARCHAR");
            strCadena = strCadena.replaceAll("BOOLEAN", "BIT");
            strCadena = strCadena.replaceAll("INTEGER", "INT");
            strCadena = strCadena.replaceAll("DOUBLE", "NUMERIC");

            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_ANIO", "YEAR");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_CN", "FC_CN");
            strCadena = strCadena.replaceAll(
                            "PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS",
                            "FC_COLOCARCOMILLAS");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_DCH", "FC_DCH");

            if ("PCK_SYSMAN_UTL.FC_DIA".equalsIgnoreCase(strCadena)) {
                strCadena = strCadena.replaceAll(strCadena, "DAY");
            }

            strCadena = strCadena.replaceAll(
                            "PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS",
                            "FC_DIASHABILVIATICOS");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_DIASHABIL",
                            "FC_DIASHABIL");
            strCadena = strCadena.replaceAll(
                            "PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL",
                            "FC_DIASMESCOMERCIAL");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_EDAD",
                            "FC_EDAD");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_EVALC",
                            "FC_EVALC");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_EVAL",
                            "FC_EVAL");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_GETCOMPANIA",
                            "FC_GETCOMPANIA");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_IIF", "IIF");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_LISTA_CAMPOS1",
                            "FC_LISTA_CAMPOS1");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_LISTA_CAMPOS",
                            "FC_LISTA_CAMPOS");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_MES", "MONTH");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_NOCARDINALES",
                            "FC_NOCARDINALES");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_PAR", "FC_PAR");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_PARAMETRO",
                            "FC_PARAMETRO");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_STRZERO",
                            "FC_STRZERO");
            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.FC_WEEKDAY\\(",
                            "DATEPART(WEEKDAY, ");

            strCadena = strCadena.replaceAll("PCK_SYSMAN_UTL.MESCOMPLETOS",
                            "MESCOMPLETOS");

            strCadena = strCadena.replaceAll("MENUK.MENUS", "MENUS");
            strCadena = strCadena.replaceAll("MENUK.PR_ASIGNARACCESOMENUS",
                            "PR_ASIGNARACCESOMENUS");

            strCadena = strCadena.replaceAll("TO_CHAR \\(", "TO_CHAR(");
            strCadena = strCadena.replaceAll("TO_CHAR\\(", "TO_CHAR(");
            strCadena = strCadena.replaceAll("TO_DATE \\(", "TO_DATE(");
            strCadena = strCadena.replaceAll("TO_DATE\\(", "TO_DATE(");

            strCadena = strTo_Date(strCadena);

            strCadena = SysmanUtl.strTo_CharH(strCadena); // para
                                                          // revisar
                                                          // los
                                                          // to_char

            strCadena = SysmanUtl.strDateFormat(strCadena, true);
        }
        catch (Exception ex) {
            return ex.toString();
        }

        return strCadena;
    }

    /**
     * Traduce el método TO_DATE de PL/SQL a CONVERT de SQL
     * 
     * @param strCadena:
     * Cadena que incluye método TO_DATE de PL/SQL
     * @return Cadena con la traducción a SQL
     *
     * @author.....: Henry de Jesus Puerto Vasquez @created....:
     * 2018.09.01
     */
    public static String strTo_Date(String strCadena) {

        if (strCadena.contains("dbo.TO_DATE")) {
            return strCadena;
        }

        int intPi0 = 0;
        int intPi2 = 0;
        int intPf = 0;
        int intNumCom = 0;
        int intNumPar = 0;
        int intParte = 0;

        String strCond = "";
        String strSi = "";
        String strNo = "";

        try {
            while (strCadena.contains("TO_DATE(")) {
                intPi0 = strCadena.indexOf("TO_DATE(");
                intPi2 = intPi0 + "TO_DATE(".length();
                intPf = 0;
                intNumCom = 0;
                intNumPar = 0;
                intParte = 0;
                strCond = "";
                strSi = "";
                strNo = "";

                while (!(intNumCom == 0 && intNumPar == 0
                    && strCadena.substring(intPi2, intPi2 + 1).contains(")"))) {
                    if (strCadena.substring(intPi2, intPi2 + 1).contains("'")
                        && intNumCom == 0) {
                        intNumCom++;
                    }
                    if (strCadena.substring(intPi2, intPi2 + 1).contains("'")
                        && intNumCom > 0) {
                        intNumCom--;
                    }
                    if (intNumCom == 0) {
                        if (strCadena.substring(intPi2, intPi2 + 1)
                                        .contains("(")) {
                            intNumPar++;
                        }
                        if (strCadena.substring(intPi2, intPi2 + 1)
                                        .contains(")")) {
                            intNumPar--;
                        }
                        if (intNumCom == 0 && intNumPar == 0
                            && strCadena.substring(intPi2, intPi2 + 1)
                                            .contains(",")) {
                            intParte++;
                        }
                        else {
                            if (intParte == 0) {
                                strCond = strCond
                                    + strCadena.substring(intPi2, intPi2 + 1);
                            }
                            else if (intParte == 1) {
                                strSi = strSi
                                    + strCadena.substring(intPi2, intPi2 + 1);
                            }
                            else {
                                strNo = strNo
                                    + strCadena.substring(intPi2, intPi2 + 1);
                            }
                        }
                    }
                    intPi2++;
                    intPf = intPi2;
                }
                strSi = strDateFormat(strSi, true);
                if (strCadena.length() <= intPf
                    && !SysmanUtl.booExcludedFormat(strSi, true)) {
                    strCadena = strCadena.substring(0, intPi0)
                        + "CONVERT(DATETIME," + strCond + "," + strSi + ")";
                }
                else if (!SysmanUtl.booExcludedFormat(strSi, true)) {
                    strCadena = strCadena.substring(0, intPi0)
                        + "CONVERT(DATETIME," + strCond + "," + strSi + ")"
                        + strCadena.substring(intPf + 1);
                }
                else {
                    strCadena = strCadena.substring(0, intPi0)
                        + "dbo.T0_CH4R_D4T3(" + strCond + "," + strSi + ")"
                        + strCadena.substring(intPf + 1);
                }
            }
            strCadena = strCadena.replaceAll("T0_CH4R_D4T3", "TO_CHAR_DATE");

        }
        catch (Exception ex) {
            return ex.toString();
        }
        return strCadena;
    }

    /**
     * Traduce el método TO_CHAR de PL/SQL a CONVERT de SQL o utliza
     * la function de SQL TO_CHAR_DATE
     * 
     * @param strCadena:
     * Cadena que incluye método TO_CHAR de PL/SQL
     * @return Cadena con la traducción a SQL
     *
     * @author.....: José Ignacio Becerra Becerra @created....:
     * 2018.09.01
     */
    public static String strTo_Char(String strCadena) {
        int intPi0 = 0;
        int intPi2 = 0;
        int intPf = 0;
        int intNumCom = 0;
        int intNumPar = 0;
        int intParte = 0;
        String strCond = "";
        String strSi = "";
        String strNo = "";

        try {
            while (strCadena.contains("TO_CHAR(")
                || strCadena.contains("TO_CHAR (")) {
                intPi0 = strCadena.indexOf("TO_CHAR(");
                if (intPi0 < 0) {
                    intPi0 = strCadena.indexOf("TO_CHAR (");
                }
                intPi2 = intPi0
                    + (strCadena.contains("TO_CHAR(") ? "TO_CHAR(".length()
                        : "TO_CHAR (".length());
                intPf = 0;
                intNumCom = 0;
                intNumPar = 0;
                intParte = 0;
                strCond = "";
                strSi = "";
                strNo = "";

                while (!(intNumCom == 0 && intNumPar == 0
                    && strCadena.substring(intPi2, intPi2 + 1).contains(")"))) {
                    if (strCadena.substring(intPi2, intPi2 + 1).contains("'")
                        && intNumCom == 0) {
                        intNumCom++;
                    }
                    if (strCadena.substring(intPi2, intPi2 + 1).contains("'")
                        && intNumCom > 0) {
                        intNumCom--;
                    }
                    if (intNumCom == 0) {
                        if (strCadena.substring(intPi2, intPi2 + 1)
                                        .contains("(")) {
                            intNumPar++;
                        }
                        if (strCadena.substring(intPi2, intPi2 + 1)
                                        .contains(")")) {
                            intNumPar--;
                        }
                        if (intNumCom == 0 && intNumPar == 0
                            && strCadena.substring(intPi2, intPi2 + 1)
                                            .contains(",")) {
                            intParte++;
                        }
                        else {
                            if (intParte == 0) {
                                strCond = strCond
                                    + strCadena.substring(intPi2, intPi2 + 1);
                            }
                            else if (intParte == 1) {
                                strSi = strSi
                                    + strCadena.substring(intPi2, intPi2 + 1);
                            }
                            else {
                                strNo = strNo
                                    + strCadena.substring(intPi2, intPi2 + 1);
                            }
                        }
                    }
                    intPi2++;
                    intPf = intPi2;
                }

                if (strSi.contains("/") || strSi.contains(":")) {
                    strSi = strDateFormat(strSi, true);
                    if (strSi.contains("/") && strSi.contains(":")
                        && !SysmanUtl.booExcludedFormat(strSi, true)) {
                        if (strCadena.length() <= intPf) {
                            strCadena = strCadena.substring(0, intPi0)
                                + "CONVERT(VARCHAR," + strCond + "," + strSi
                                + ")";
                        }
                        else {
                            strCadena = strCadena.substring(0, intPi0)
                                + "CONVERT(VARCHAR," + strCond + "," + strSi
                                + ")"
                                + strCadena.substring(intPf + 1);
                        }
                    }
                    else {
                        strCadena = strCadena.substring(0, intPi0)
                            + "dbo.T0_CH4R_D4T3(" + strCond + "," + strSi + ")"
                            + strCadena.substring(intPf + 1);
                    }
                }
                else {
                    strSi = strSi.replaceAll("\\'", "");
                    strSi = strSi.replaceAll("0", "");
                    strSi = strSi.replaceAll("9", "");
                    if (strCadena.length() <= intPf) {
                        strCadena = strCadena.substring(0, intPi0) + "STR("
                            + strCond + ")";
                    }
                    else {
                        strCadena = strCadena.substring(0, intPi0) + "STR("
                            + strCond + ")" + strCadena.substring(intPf + 1);
                    }
                }
            }

            strCadena = strCadena.replaceAll("T0_CH4R_D4T3", "TO_CHAR_DATE");

        }
        catch (Exception ex) {
            return ex.toString();
        }
        return strCadena;
    }

    /**
     * Metodo que traduce los formatos DATETIME de PL/SQL a código
     * para utilizar en CONVERT de SQL
     * 
     * @param strCadena:
     * Cadena que incluye formato en PL/SQL a traducir
     * @param booIndValid:
     * Indica si se deben excluir formatos
     * @return Cadena con la traducción a SQL
     *
     * @author.....: Henry de Jesus Puerto Vasquez @created....:
     * 2018.09.01
     */
    public static String strDateFormat(String strCadena, boolean booIndValid) {
        if (SysmanUtl.booExcludedFormat(strCadena, booIndValid)) {
            return strCadena;
        }

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH\\'", "101");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:mi:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:MI:ss\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:mi:ss\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24\\'", "101");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH\\'", "101");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH24:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH24:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH24\\'", "101");

        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH\\'", "102");

        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH24:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH24:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH24\\'", "102");

        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH\\'", "102");

        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH24:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH24:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH24\\'", "102");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH\\'", "103");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:mi:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:MI:ss\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:mi:ss\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24\\'", "103");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH\\'", "103");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH24:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH24:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH24\\'", "103");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH\\'", "1");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH24:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH24:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH24\\'", "1");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH\\'", "1");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH24:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH24:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH24\\'", "1");

        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH\\'", "2");

        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH24:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH24:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH24\\'", "2");

        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH\\'", "2");

        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH24:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH24:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH24\\'", "2");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH\\'", "3");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH24:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH24:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH24\\'", "3");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH\\'", "3");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH24:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH24:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH24\\'", "3");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR\\'", "101");

        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD\\'", "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD\\'", "102");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY\\'", "103");
        strCadena = strCadena.replaceAll("\\'dd\\/MM\\/yyyy\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR\\'", "103");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR\\'", "1");

        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD\\'", "2");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR\\'", "3");

        return strCadena;
    }

    /**
     * Metodo que traduce los formatos DATETIME de PL/SQL a código
     * para utilizar en CONVERT de SQL
     * 
     * @param strCadena:
     * Cadena que incluye formato en PL/SQL a traducir
     * @param booIndValid:
     * Indica si se deben excluir formatos
     * @return Cadena con la traducción a SQL
     *
     * @author.....: Henry de Jesus Puerto Vasquez @created....:
     * 2018.09.01
     */
    public static String strDateFormat(String strCadena) {

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH\\'", "101");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:mi:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:MI:ss\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:mi:ss\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY HH24\\'", "101");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH\\'", "101");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH24:MI:SS\\'",
                        "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH24:MI\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR HH24\\'", "101");

        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH\\'", "102");

        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH24:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH24:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD HH24\\'", "102");

        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH\\'", "102");

        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH24:MI:SS\\'",
                        "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH24:MI\\'", "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD HH24\\'", "102");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH\\'", "103");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:mi:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:MI:ss\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:mi:ss\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY HH24\\'", "103");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH\\'", "103");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH24:MI:SS\\'",
                        "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH24:MI\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR HH24\\'", "103");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH\\'", "1");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH24:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH24:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY HH24\\'", "1");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH\\'", "1");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH24:MI:SS\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH24:MI\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR HH24\\'", "1");

        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH\\'", "2");

        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH24:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH24:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD HH24\\'", "2");

        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH\\'", "2");

        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH24:MI:SS\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH24:MI\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD HH24\\'", "2");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH\\'", "3");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH24:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH24:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY HH24\\'", "3");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH\\'", "3");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH24:MI:SS\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH24:MI\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR HH24\\'", "3");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YYYY\\'", "101");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RRRR\\'", "101");

        strCadena = strCadena.replaceAll("\\'YYYY\\/MM\\/DD\\'", "102");
        strCadena = strCadena.replaceAll("\\'RRRR\\/MM\\/DD\\'", "102");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YYYY\\'", "103");
        strCadena = strCadena.replaceAll("\\'dd\\/MM\\/yyyy\\'", "103");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RRRR\\'", "103");

        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/YY\\'", "1");
        strCadena = strCadena.replaceAll("\\'MM\\/DD\\/RR\\'", "1");

        strCadena = strCadena.replaceAll("\\'YY\\/MM\\/DD\\'", "2");
        strCadena = strCadena.replaceAll("\\'RR\\/MM\\/DD\\'", "2");

        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/YY\\'", "3");
        strCadena = strCadena.replaceAll("\\'DD\\/MM\\/RR\\'", "3");

        return strCadena;
    }

    /**
     * Metodo que verifica si la cadena enviada contiene formatos
     * DATETIME específicos de PL/SQL
     * 
     * @param strCadena:
     * Cadena que incluye formatos DATETIME en PL/SQL
     * @param booIndValid:
     * Indica si se debe evaluar la cadena con los formatos del
     * arreglo
     * @return true: Si la cadena contiene los formatos del arreglo -
     * false: Si la cadena NO contiene los formatos del arreglo
     *
     * @author.....: José Ignacio Becerra Becerra @created....:
     * 2018.09.01
     */
    public static boolean booExcludedFormat(String strCadena,
        boolean booIndValid) {
        boolean booRet = false;

        if (booIndValid) {
            String strDateFormatV[] = new String[2];
            strDateFormatV[0] = "'DD/MM/YYYY HH24:MI:SS'";
            strDateFormatV[1] = "'dd/MM/YYYY HH24:MI:SS'";

            for (int intK = 0; intK < strDateFormatV.length; intK++) {
                if (strCadena.contains(strDateFormatV[intK])) {
                    booRet = true;
                }
            }
        }

        return booRet;
    }

    public static String strTo_CharH(String strSql) {
        // Por henry Puerto V.
        // Sep 21 de 2018
        // Encontrar una funcion to_char de Fecha y Procesarla
        String strS[];
        String strSql1 = strSql;
        int intIntentos = 0;
        String strB = "";
        String strFecha = "";
        String strFormato = "";
        String strFuncion = "TO_CHAR";
        strSql = strSql.replace(
                        "'day_DD * month * YYYY','nls_date_language=spanish'",
                        "'DD/MM/YYYY'");
        while (strSql.indexOf(strFuncion + "(") >= 0) {
            intIntentos++;
            if (intIntentos > 10) {
                return "\n/*REVISARMANUALMENTE\n " + strSql1
                    + "\nREVISARMANUALMENTE \n*/";
            }

            strB = strBuscarFuncion(strSql, strFuncion);
            if (strB.startsWith("'")
                || strB.toUpperCase().startsWith("DBO.TO_DATE(")
                || strB.startsWith("TO_DATE(")
                || strB.toUpperCase().startsWith("DBO.")) {
                return strSql;
            }
            strS = null;
            strS = strSplit(strB);
            if (strS.length == 1) {
                strSql = strSql.replace(strFuncion + "(" + strB + ")",
                                "CONVERT(VARCHAR," + strB + ")");
            }
            if (strS.length >= 2) {
                strFecha = strS[0];
                strFormato = strS[1].toUpperCase();
                if ("'DD/MM/YYYY HH24:MI:SS'".equals(strFormato)
                    || "'DD/MM/RRRR HH24:MI:SS'".equals(strFormato)) {
                    strSql = strSql.replace(strFuncion + "(" + strB + ")",
                                    "dbo.TO_CHAR_DATE(" + strFecha + ","
                                        + strFormato + ")");
                }
                else if (strFormato.contains("999")
                    || strFormato.contains("00")) {
                    strSql = strSql.replace(strFuncion + "(" + strB + ")",
                                    "FORMAT(" + strFecha + ","
                                        + strFormato.replaceAll("9", "#")
                                        + ")");
                }
                else {
                    strSql = strSql.replace(strFuncion + "(" + strB + ")",
                                    "CONVERT(VARCHAR," + strFecha + ","
                                        + SysmanUtl.strDateFormat(strFormato)
                                        + ")");
                }
            }
        }
        return strSql;
    }

    public static String strBuscarFuncion(String strLin, String strBuscar) {
        // Por henry Puerto V.
        // Sep 21 de 2018
        // Split para Buscar el contenido de una funcion
        // el nombre de la funcion debe venir sin parentesis
        // Se Retorna el contenido de los parantesis de la funcion
        // Ej: strBuscarFuncion("SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY
        // HH24:MI:SS') FROM DUAL", "TO_CHAR")
        // RETORNA SYSDATE,'DD/MM/YYYY HH24:MI:SS' QUE SE PUEDE
        // SPLITAR CON strSplit
        int intPi = 0;
        int intCml = 0;
        int intConPar = 0;
        int intK = 0;
        intPi = strLin.toUpperCase().indexOf(strBuscar.toUpperCase() + "(");
        if (intPi < 0) {
            intPi = strLin.toUpperCase()
                            .indexOf(strBuscar.toUpperCase() + " (");
            if (intPi < 0) {
                return "";
            }
            else {
                intPi++;
            }
        }
        intPi = intPi + strBuscar.length() + 1;
        intK = intPi;
        while (intK < strLin.length()) {
            if ("'".equals(strLin.substring(intK, intK + 1))) {
                if (intCml == 0) {
                    intCml = 1;
                }
                else {
                    intCml = 0;
                }
                intK = intK + 1;
                continue;
            }
            if (intCml != 0) {
                intK = intK + 1;
                continue;
            }
            if (intCml == 0 && "(".equals(strLin.substring(intK, intK + 1))) {
                intConPar = intConPar + 1;
            }
            if (intCml == 0 && ")".equals(strLin.substring(intK, intK + 1))) {
                intConPar = intConPar - 1;
            }
            if (intConPar < 0) {
                break;
            }
            intK = intK + 1;
        }
        // return strLin.substring(intPi+1,intK);
        return strLin.substring(intPi, intK);
    }

    public static int strBuscarString(String strLin, String strBuscar) {
        // Por henry Puerto V.
        // Nov 11 de 2018
        // Split para Buscar la psoicion real de un conjunto de
        // caracteres considerando comillas y parentesis
        // Ej: strBuscarFuncion("SELECT x||y'||' FROM DUAL", "||")
        // RETORNA 12
        int intCml = 0;
        int intConPar = 0;
        int intK = 0;
        int intPi = 0;
        while (intK < strLin.length()) {
            if ("'".equals(strLin.substring(intK, intK + 1))) {
                if (intCml == 0) {
                    intCml = 1;
                }
                else {
                    intCml = 0;
                }
                intK = intK + 1;
                continue;
            }
            if (intCml != 0) {
                intK = intK + 1;
                continue;
            }
            if (intCml == 0 && intK + strBuscar.length() < strLin.length()) {
                if (strBuscar.equals(strLin.substring(intK,
                                intK + strBuscar.length()))) {
                    intPi = intK;
                    break;
                }
            }
            intK = intK + 1;
        }
        return intPi;
    }

    public static String[] strSplit(String strSql) {
        // Por henry Puerto V.
        // Sep 15 de 2018
        // Split apara Sysman sin problemas

        String[] strS = { null };
        int intA = 0;
        int intI = 0;
        int intCml = 0;
        int intConPar = 0;
        int intK = 0;
        while (intK < strSql.length()) {
            if ("'".equals(strSql.substring(intK, intK + 1))) {
                if (intCml == 0) {
                    intCml = 1;
                }
                else {
                    intCml = 0;
                }
                intK = intK + 1;
                strS[intI] = strSql.substring(intA, intK);
                continue;
            }
            if (intCml == 0 && "(".equals(strSql.substring(intK, intK + 1))) {
                intConPar = intConPar + 1;
            }
            if (intCml == 0 && ")".equals(strSql.substring(intK, intK + 1))) {
                intConPar = intConPar - 1;
            }
            if (intCml == 0 && intConPar == 0
                && ",".equals(strSql.substring(intK, intK + 1))) {
                strS[intI] = strSql.substring(intA, intK);
                intA = intK + 1;
                intI++;
                strS = SysmanUtl.RedimensionarVector(strS, strS.length + 1);
                intK = intK + 1;
            }
            else {
                intK = intK + 1;
            }
            strS[intI] = strSql.substring(intA, intK);
        }
        return strS;
    }

    public static String[] strSplit(String strSql, String strSeparador) {
        // Por henry Puerto V.
        // Sep 15 de 2018
        // Split para Sysman sin problemas pero con separador definido
        // por el usuario

        String[] strS = { null };
        int intA = 0;
        int intI = 0;
        int intCml = 0;
        int intConPar = 0;
        int intK = 0;
        if (strSeparador.length() < 0) {
            return null;
        }
        while (intK < strSql.length() - strSeparador.length() + 1) {
            if ("'".equals(strSql.substring(intK, intK + 1))) {
                if (intCml == 0) {
                    intCml = 1;
                }
                else {
                    intCml = 0;
                }
                intK = intK + 1;
                strS[intI] = strSql.substring(intA, intK);
                continue;
            }
            if (intCml == 0 && "(".equals(strSql.substring(intK, intK + 1))) {
                intConPar = intConPar + 1;
            }
            if (intCml == 0 && ")".equals(strSql.substring(intK, intK + 1))) {
                intConPar = intConPar - 1;
            }
            if (intCml == 0 && intConPar == 0 && strSeparador.equals(strSql
                            .substring(intK, intK + strSeparador.length()))) {
                strS[intI] = strSql.substring(intA, intK);
                intA = intK + strSeparador.length();
                intI++;
                strS = SysmanUtl.RedimensionarVector(strS, strS.length + 1);
                intK = intA;

            }
            else {
                intK = intK + 1;
            }
            strS[intI] = strSql.substring(intA, intK);
        }
        return strS;
    }

    public static String strPck(String strCadena) {
        String strRet = strCadena;
        int intPi = 0;
        int intPi1 = 0;
        int intPi2 = 0;

        if (!strCadena.contains("PCK_")) {
            return strCadena;
        }

        while (strRet.substring(intPi).contains("PCK_")) {
            intPi1 = strRet.substring(intPi).indexOf("PCK_");
            if (intPi1 < 0) {
                break;
            }
            intPi2 = strRet.substring(intPi1).indexOf(".");
            if (intPi2 > 0) {
                intPi = intPi1 + intPi2;
                if (intPi + 1 >= strRet.length()) {
                    break;
                }
                strRet = strRet.substring(0, intPi) + "_"
                    + strRet.substring(intPi + 1);
            }
            else {
                break;
            }
        }

        return strRet;
    }

    public static String strTraductorSql(String strSqlT) {
        try {
            strSqlT = strSqlT.replaceAll("ADD_MONTHS\\(", "dbo.ADD_MONTHS(");
            strSqlT = strSqlT.replaceAll("LAST_DAY", "dbo.LAST_DAY");
            strSqlT = strSqlT.replaceAll("INSTR\\(", "dbo.INSTR(");
            strSqlT = strSqlT.replaceAll("TO_DATE\\(", "dbo.TO_DATE(");
            strSqlT = strSqlT.replaceAll("LPAD\\(", "dbo.LPAD(");
            strSqlT = strSqlT.replaceAll("LPAD \\(", "dbo.LPAD(");
            strSqlT = strSqlT.replaceAll("RPAD\\(", "dbo.RPAD(");
            strSqlT = strSqlT.replaceAll("RPAD \\(", "dbo.RPAD(");

            strSqlT = strSqlT.replaceAll("PAD\\(", "dbo.PAD(");
            strSqlT = strSqlT.replaceAll("PAD \\(", "dbo.PAD(");
            strSqlT = strSqlT.replaceAll("TRIM\\(", "dbo.TRIM(");
            strSqlT = strSqlT.replaceAll("TRIM \\(", "dbo.TRIM(");

            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_ANIO", "YEAR");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_CN", "dbo.FC_CN");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS",
                            "dbo.FC_COLOCARCOMILLAS");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_DCH", "dbo.FC_DCH");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_DIA", "DAY");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS",
                            "dbo.FC_DIASHABILVIATICOS");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_DIASHABIL",
                            "dbo.FC_DIASHABIL");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL",
                            "dbo.FC_DIASMESCOMERCIAL");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_EDAD",
                            "dbo.FC_EDAD");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_EVALC",
                            "dbo.FC_EVALC");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_EVAL",
                            "dbo.FC_EVAL");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_GETCOMPANIA",
                            "dbo.FC_GETCOMPANIA");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_IIF", "IIF");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_LISTA_CAMPOS1",
                            "dbo.FC_LISTA_CAMPOS1");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_LISTA_CAMPOS",
                            "dbo.FC_LISTA_CAMPOS");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_MES", "MONTH");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_NOCARDINALES",
                            "dbo.FC_NOCARDINALES");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_NOMBRE_MES",
                            "dbo.FC_NOMBRE_MES");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_PAR", "dbo.FC_PAR");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_PARAMETRO",
                            "dbo.FC_PARAMETRO");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_ROUND", "ROUND");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_STRZERO",
                            "dbo.FC_STRZERO");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_MINIT",
                            "dbo.PCK_SYSMAN_UTL_FC_MINIT");
            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.FC_WEEKDAY\\(",
                            "DATEPART(WEEKDAY, ");

            strSqlT = strSqlT.replaceAll("PCK_SYSMAN_UTL.MESCOMPLETOS",
                            "MESCOMPLETOS");

            strSqlT = strSqlT.replaceAll("MENUK.MENUS", "MENUS");
            strSqlT = strSqlT.replaceAll("MENUK.PR_ASIGNARACCESOMENUS",
                            "PR_ASIGNARACCESOMENUS");

            strSqlT = strSqlT.replaceAll("PCK_DATOS.FC_ACME",
                            "PCK_DATOS_FC_ACME");

            strSqlT = strSqlT.replaceAll("TO_DATE", "dbo.TO_DATE");
            /**
             * Se agrega esta seccion dado que los replaces que se
             * hacen antes, dañan los TRIM y los PAD
             */
            strSqlT = strSqlT.replaceAll("dbo.Ldbo.PAD", "dbo.LPAD");
            strSqlT = strSqlT.replaceAll("dbo.Rdbo.PAD", "dbo.PAD");
            strSqlT = strSqlT.replaceAll("Rdbo.TRIM", "RTRIM");
            strSqlT = strSqlT.replaceAll("Ldbo.TRIM", "LTRIM");

            strSqlT = strSqlT.replaceAll("dbo.dbo.dbo.", "dbo.");
            strSqlT = strSqlT.replaceAll("dbo.dbo.", "dbo.");

        }
        catch (Exception ex) {
            return ex.toString();
        }

        return strSqlT;
    }

    public static String[][] RedimensionarVector(String[][] strVecAct,
        int intVecLen) {
        String[][] strVecTmp = null;

        strVecTmp = Arrays.copyOf(strVecAct, intVecLen);

        return strVecTmp;
    }

    public static String[] RedimensionarVector(String[] strVecAct,
        int intVecLen) {
        String[] strVecTmp = null;

        strVecTmp = Arrays.copyOf(strVecAct, intVecLen);

        return strVecTmp;
    }

}