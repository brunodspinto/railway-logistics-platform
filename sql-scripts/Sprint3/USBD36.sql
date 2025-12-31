-- Função USBD36 --

CREATE OR REPLACE FUNCTION add_building_to_facility(
    p_station_id IN NUMBER,
    p_area_type_id IN NUMBER
) RETURN SYS_REFCURSOR IS
    refcursor SYS_REFCURSOR;
    v_station_exists NUMBER;
    v_area_type_exists NUMBER;
    v_already_exists NUMBER;

    e_station_not_found EXCEPTION;
    e_area_type_not_found EXCEPTION;
    e_association_exists EXCEPTION;

BEGIN
    -- 1. Validar se a Station existe
SELECT COUNT(*) INTO v_station_exists
FROM Station
WHERE idStation = p_station_id;

IF v_station_exists = 0 THEN
        RAISE e_station_not_found;
END IF;

    -- 2. Validar se o AreaType existe
SELECT COUNT(*) INTO v_area_type_exists
FROM AreaType
WHERE id = p_area_type_id;

IF v_area_type_exists = 0 THEN
        RAISE e_area_type_not_found;
END IF;

    -- 3. Verificar se a associação já existe
SELECT COUNT(*) INTO v_already_exists
FROM AreaTypeStation
WHERE areaId = p_area_type_id
  AND stationId = p_station_id;

IF v_already_exists > 0 THEN
        RAISE e_association_exists;
END IF;

    -- 4. Inserir a associação
INSERT INTO AreaTypeStation (areaId, stationId)
VALUES (p_area_type_id, p_station_id);

COMMIT;

-- 5. Retornar cursor com o resultado
OPEN refcursor FOR
SELECT
    s.idStation,
    s.nameStation,
    st.description AS stationType,
    at.id AS areaId,
    at.name AS areaName
FROM Station s
         INNER JOIN StationType st ON s.stationTypeId = st.id
         INNER JOIN AreaTypeStation ats ON s.idStation = ats.stationId
         INNER JOIN AreaType at ON ats.areaId = at.id
WHERE s.idStation = p_station_id
  AND at.id = p_area_type_id;

RETURN refcursor;

EXCEPTION
    WHEN e_station_not_found THEN
        RAISE_APPLICATION_ERROR(-20361, 'Station with ID ' || p_station_id || ' does not exist.');

WHEN e_area_type_not_found THEN
        RAISE_APPLICATION_ERROR(-20362, 'AreaType with ID ' || p_area_type_id || ' does not exist.');

WHEN e_association_exists THEN
        RAISE_APPLICATION_ERROR(-20363, 'Area is already associated with this station.');

WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20369, 'Error adding building to facility: ' || SQLERRM);
END add_building_to_facility;
/