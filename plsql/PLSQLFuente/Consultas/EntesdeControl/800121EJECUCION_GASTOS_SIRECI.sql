SELECT PLAN_PRESUPUESTAL.CODIGOSIRECI CODIGOEQUIVALENTE,
       CASE WHEN VIGENCIAGASTO IN('1') 
            THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
            	          THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
            	          ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
            	     END) 
            ELSE 0 
       END APROPIADO,
       CASE WHEN VIGENCIAGASTO IN('1') 
            THEN SUM(ADICION) 
            ELSE 0 
       END ADICION,
       CASE WHEN VIGENCIAGASTO IN('1') 
            THEN SUM(ABS(REDUCCION)) 
            ELSE 0 
       END REDUCCION,
       CASE WHEN VIGENCIAGASTO IN('1')
            THEN SUM(TRASLADO_DEBITO+APLAZAM_DEBITO) 
            ELSE 0 
       END CREDITOS,
       CASE WHEN VIGENCIAGASTO IN('1')
            THEN SUM(TRASLADO_CREDITO+APLAZAM_CREDITO) 
            ELSE 0 
       END CONTRACREDITOS,
       CASE WHEN VIGENCIAGASTO IN('1')
            THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
            	          THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
            	          ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
            	     END)
                 +SUM(ADICION)
                 -SUM(ABS(REDUCCION))
                 +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                 	       THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                 	       ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                 	  END)
                 +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                 	       THEN APLAZAM_DEBITO-APLAZAM_CREDITO 
                 	       ELSE APLAZAM_CREDITO-APLAZAM_DEBITO 
                 	  END) 
            ELSE 0 
       END TOTALAPROPIADO,  
       CASE WHEN VIGENCIAGASTO IN('1') 
            THEN TRUNC(CASE WHEN SUM(REG_NO_CONTRACT+MODIF_REG_NOCONT)
            	                 +SUM(REG_CONTRACT+MODIF_REG_CONT)>0 
            	            THEN SUM(REG_NO_CONTRACT+MODIF_REG_NOCONT)
            	                 +SUM(REG_CONTRACT+MODIF_REG_CONT)+0.501 
            	            ELSE SUM(REG_NO_CONTRACT+MODIF_REG_NOCONT)
            	                 +SUM(REG_CONTRACT+MODIF_REG_CONT)-0.501 
            	       END) 
            ELSE 0 
       END COMPROMISOS,
       CASE WHEN VIGENCIAGASTO IN('1') 
            THEN TRUNC(CASE WHEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)>0 
            	            THEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)+0.501 
            	            ELSE SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)-0.501 
            	       END ) 
            ELSE 0 
       END OBLIGACIONES,
       CASE WHEN VIGENCIAGASTO IN('1') 
            THEN TRUNC(CASE WHEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
            	                          THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
            	                          ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
            	                     END)
                                 +SUM(MODIF_INGRESOS)>0 
                            THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                            	          THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                            	          ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                            	     END)+SUM(MODIF_INGRESOS)+0.501 
                            ELSE SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                            	          THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                            	          ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                            	     END)
                                 +SUM(MODIF_INGRESOS)-0.501 
                            END) 
            ELSE 0 
       END PAGOS,
       CASE WHEN VIGENCIAGASTO IN('2') 
            THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
            	          THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
            	          ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
            	 END)
                 +SUM(ADICION)
                 -SUM(ABS(REDUCCION))
                 +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                 	       THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                 	       ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                 	  END)
                 +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                 	       THEN APLAZAM_DEBITO-APLAZAM_CREDITO 
                 	       ELSE APLAZAM_CREDITO-APLAZAM_DEBITO 
                 	  END)
            ELSE 0 
       END "RESERVAS CONSTITUIDAS",
       CASE WHEN VIGENCIAGASTO IN('3') 
            THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
            	          THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
            	          ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
            	     END)
                 +SUM(ADICION)
                 -SUM(ABS(REDUCCION))
                 +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                 	       THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                 	       ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                 	  END)
                 +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                 	       THEN APLAZAM_DEBITO-APLAZAM_CREDITO 
                 	       ELSE APLAZAM_CREDITO-APLAZAM_DEBITO 
                 	  END)
            ELSE 0 
       END "CUENTAS X PAGAR CONSTITUIDAS",              
       CASE WHEN VIGENCIAGASTO IN('2') 
            THEN CASE WHEN VIGENCIAGASTO IN('1') 
                      THEN TRUNC(CASE WHEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)>0 
                      	              THEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)+0.501 
                      	              ELSE SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)-0.501 
                      	         END ) 
                      ELSE 0 
                 END
            ELSE 0 
       END "RESRVA PRESUPUESTAL/OBLIGACION",              
       CASE WHEN VIGENCIAGASTO IN('2') 
            THEN CASE WHEN VIGENCIAGASTO IN('1') 
                      THEN TRUNC(CASE WHEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                      	                            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                      	                            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                      	                       END)
                                           +SUM(MODIF_INGRESOS)>0 
                                      THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                      	            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                      	            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                      	       END)
                                           +SUM(MODIF_INGRESOS)+0.501 
                                      ELSE SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                      	            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                      	            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                      	       END)+SUM(MODIF_INGRESOS)-0.501 
                                 END) 
                      ELSE 0 
                 END 
            ELSE 0 
       END "RESERVAS PRESUPUESTALES/PAGOS",
       CASE WHEN VIGENCIAGASTO IN('3') 
            THEN CASE WHEN VIGENCIAGASTO IN('1') 
                      THEN TRUNC(CASE WHEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                      	                            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                      	                            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                      	                       END)
                                           +SUM(MODIF_INGRESOS)>0 
                                      THEN SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                      	            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                      	            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                      	       END)+SUM(MODIF_INGRESOS)+0.501 
                                      ELSE SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                      	            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                      	            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                      	       END)
                                           +SUM(MODIF_INGRESOS)
                                           -0.501 
                                 END) 
                      ELSE 0 
                 END  
            ELSE 0 
        END "CUENTAS X PAGAR / PAGOS"              
   FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
       INNER JOIN SALDO_AUX_PPTAL 
           ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
           AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
           AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_AUX_PPTAL.CODIGO
  WHERE PLAN_PRESUPUESTAL.COMPANIA   =  s$compania$s 
    AND PLAN_PRESUPUESTAL.ANO        =  s$ano$s 
    AND PLAN_PRESUPUESTAL.NATURALEZA =  'D' 
    AND MES                          <= 12 
    AND CODIGOSIRECI                 IS NOT NULL 
    AND VIGENCIAGASTO                IS NOT NULL 
    AND (CASE WHEN VIGENCIAGASTO IN('1') 
              THEN TRUNC(CASE WHEN REG_NO_CONTRACT+MODIF_REG_NOCONT+REG_CONTRACT+MODIF_REG_CONT>0 
             	              THEN REG_NO_CONTRACT+MODIF_REG_NOCONT+REG_CONTRACT+MODIF_REG_CONT+0.501 
                              ELSE REG_NO_CONTRACT+MODIF_REG_NOCONT+REG_CONTRACT+MODIF_REG_CONT-0.501 
                         END) 
              ELSE 0 
         END NOT IN(0)
         OR TRUNC(CASE WHEN REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION>0 
         	           THEN REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION+0.501 
         	           ELSE REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION-0.501 
         	      END) NOT IN (0) 
         OR TRUNC(CASE WHEN MODIF_INGRESOS
         	                +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
         	                      THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
         	                      ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
         	                 END > 0 
         	           THEN MODIF_INGRESOS
         	                +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
         	                      THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
         	                      ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
         	                 END
         	                +0.501 
         	           ELSE MODIF_INGRESOS
         	                +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
         	                      THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
         	                      ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
         	                 END
         	                -0.501 
         	      END) NOT IN (0)
         OR TRUNC(CASE WHEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
        	                     THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
        	                     ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
        	                END
                            +ADICION
                            -ABS(REDUCCION)
                            +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                                  ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                                  END
                            +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN APLAZAM_DEBITO-APLAZAM_CREDITO 
                                  ELSE APLAZAM_CREDITO-APLAZAM_DEBITO 
                                  END>0 
                       THEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                 THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                                 ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                            END
                            +ADICION
                            -ABS(REDUCCION)
                            +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                                  ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                             END
                            +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN APLAZAM_DEBITO-APLAZAM_CREDITO 
                                  ELSE APLAZAM_CREDITO-APLAZAM_DEBITO 
                             END+0.501
                       ELSE CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                 THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                                 ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                            END
                            +ADICION
                            -ABS(REDUCCION)
                            +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                                  ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                             END
                            +CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN APLAZAM_DEBITO-APLAZAM_CREDITO 
                                  ELSE APLAZAM_CREDITO-APLAZAM_DEBITO 
                             END-1.501 
                  END) NOT IN(0))  
       GROUP BY CODIGOSIRECI,VIGENCIAGASTO
       ORDER BY CODIGOSIRECI
