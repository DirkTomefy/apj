CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_LIB AS
SELECT brp.id,
       brp.daty,
       brp.reference,
       brp.idboncommande,
       brp.idboncommandefille,
       brp.idbrprecedent,
       brp.idlieureception,
       brp.idmagasin,
       brp.etat,
       brp.qtavantcumule,                    --  sans underscore
       brp.qtaprescumule,                    --  sans underscore
       brp.maty,
       brp.very,
       brp.remarque,
       brp.dateeclosion,                     --  NOUVEAU
       bc.reference     AS idboncommandelib,
       bcf.idproduit    AS idboncommandefilleproduit,
       bcf.idsouche     AS idsouche,          --  NOUVEAU
       bcf.qte          AS qtecommandee,      --  NOUVEAU (bonus)
       brq.reference    AS idbrprecedentlib,
       lr.val           AS idlieureceptionlib,
       m.val            AS idmagasinlib,
       i.libelle        AS produitlib,
       s.val            AS souchelibr,
       CASE brp.etat
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS etatlib
FROM BONRECEPTION_POUSSIN brp
LEFT JOIN BONCOMMANDE_POUSSIN        bc  ON bc.id  = brp.idboncommande
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE  bcf ON bcf.id = brp.idboncommandefille
LEFT JOIN BONRECEPTION_POUSSIN       brq ON brq.id = brp.idbrprecedent
LEFT JOIN LIEURECEPTION              lr  ON lr.id  = brp.idlieureception
LEFT JOIN MAGASIN2                   m   ON m.id   = brp.idmagasin
LEFT JOIN AS_INGREDIENTS             i   ON i.id   = bcf.idproduit
LEFT JOIN SOUCHE                     s   ON s.id   = bcf.idsouche;
/
SHOW ERRORS VIEW BONRECEPTION_POUSSIN_LIB