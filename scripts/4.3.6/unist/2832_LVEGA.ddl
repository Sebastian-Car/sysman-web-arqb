BEGIN
     SYS.DBMS_SCHEDULER.create_job (
    job_name         => 'JOB_ACT_SALDOPEPS',
    job_type         => 'PLSQL_BLOCK',
    job_action       => 'BEGIN PCK_ALMACEN_COM5.PR_ACT_SALDOPEPS_PROGRAMADO; END;',
    start_date       => SYSTIMESTAMP,
    repeat_interval  => 'FREQ=DAILY; BYHOUR=23; BYMINUTE=0',
    enabled          => TRUE
  );
END;
/