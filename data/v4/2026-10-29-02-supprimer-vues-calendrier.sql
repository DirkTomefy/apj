SET DEFINE OFF

-- ============================================================================
--  Le calendrier est calcule en Java (a_tahina.prevision.simulation.Elevage) :
--  les vues de calcul ci-dessous ne sont plus lues.
--  A lancer apres avoir verifie le calendrier dans l'application.
--  Chaque DROP ignore la vue absente (ORA-00942).
-- ============================================================================

BEGIN
    FOR r IN (SELECT column_value n FROM TABLE(sys.odcivarchar2list(
        'CALENDRIER_POUSSIN_CUMUL', 'CALENDRIER_POUSSIN_RAW', 'V_CAL_PERTES_COUT', 'V_CAL_PERTES',
        'V_CAL_GEN2_PONTE', 'V_CAL_GEN1_PONTE', 'V_CAL_PONTE_JOUR', 'V_CAL_GENERATIONS',
        'V_CAL_LOT_INFO', 'V_CAL_INITIAUX', 'V_CAL_LOT_PU', 'V_CAL_K7', 'V_CAL_JOURS', 'V_CAL_BORNES',
        'V_CAL_SOUCHE', 'V_CAL_PARAM',
        'V_CAL_GEN2_HENS', 'V_CAL_GEN1_PONTE_DETAIL', 'V_CAL_GEN1_HENS', 'V_CAL_PONTE_JOUR_DETAIL'))) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP VIEW ' || r.n;
        EXCEPTION
            WHEN OTHERS THEN
                IF SQLCODE <> -942 THEN
                    RAISE;
                END IF;
        END;
    END LOOP;
END;
/
