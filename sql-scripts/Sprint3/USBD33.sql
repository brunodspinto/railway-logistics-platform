
-- Trigger USBD33 LOCOMOTIVE --

create or replace TRIGGER triggerTrainMaxLengthLocomotive
BEFORE INSERT OR UPDATE ON Locomotive_Train
FOR EACH ROW
DECLARE
    v_total_length NUMBER := 0;
    v_train_id NUMBER := :NEW.trainId;
    v_sum_locomotives NUMBER;
    v_sum_wagons NUMBER;
    v_new_loco_length NUMBER;
    v_max_length NUMBER;
BEGIN
    SELECT SUM(lm.length) INTO v_sum_locomotives
    FROM Locomotive_Train lt
    JOIN Locomotive l ON lt.locomotiveNumber = l.numberLocomotive
    JOIN LocomotiveModel lm ON l.model = lm.id
    WHERE lt.trainId = v_train_id;

    IF v_sum_locomotives IS NULL THEN
        v_sum_locomotives := 0;
    END IF;

    SELECT SUM(wm.length) INTO v_sum_wagons
    FROM WagonFreights wf
    JOIN Wagon w ON wf.wagonNumber = w.numberWagon
    JOIN WagonModel wm ON w.wagonModelId = wm.id
    JOIN Freights f ON wf.freightsId = f.id
    WHERE f.trainId = v_train_id;

    IF v_sum_wagons IS NULL THEN
        v_sum_wagons := 0;
    END IF;

    SELECT lm.length INTO v_new_loco_length
    FROM Locomotive l
    JOIN LocomotiveModel lm ON l.model = lm.id
    WHERE l.numberLocomotive = :NEW.locomotiveNumber;

    v_total_length := v_sum_locomotives + v_sum_wagons + v_new_loco_length;

    SELECT maxLenght INTO v_max_length
    FROM Train
    WHERE id = v_train_id;

    IF v_total_length > v_max_length THEN
        RAISE_APPLICATION_ERROR(-20331, 'Error: The total length of the train exceeds the maximum limit.');
    END IF;
END;

-- Trigger USBD33 WAGON --

create or replace TRIGGER triggerTrainMaxLengthWagon
BEFORE INSERT OR UPDATE ON WagonFreights
FOR EACH ROW
DECLARE
    v_total_length NUMBER := 0;
    v_train_id NUMBER;
    v_sum_locomotives NUMBER;
    v_sum_wagons NUMBER;
    v_new_wagon_length NUMBER;
    v_max_length NUMBER;
BEGIN
    SELECT trainId INTO v_train_id
    FROM Freights
    WHERE id = :NEW.freightsId;

    SELECT SUM(lm.length) INTO v_sum_locomotives
    FROM Locomotive_Train lt
    JOIN Locomotive l ON lt.locomotiveNumber = l.numberLocomotive
    JOIN LocomotiveModel lm ON l.model = lm.id
    WHERE lt.trainId = v_train_id;

    IF v_sum_locomotives IS NULL THEN
        v_sum_locomotives := 0;
    END IF;

    SELECT SUM(wm.length) INTO v_sum_wagons
    FROM WagonFreights wf
    JOIN Wagon w ON wf.wagonNumber = w.numberWagon
    JOIN WagonModel wm ON w.wagonModelId = wm.id
    JOIN Freights f ON wf.freightsId = f.id
    WHERE f.trainId = v_train_id;

    IF v_sum_wagons IS NULL THEN
        v_sum_wagons := 0;
    END IF;

    SELECT wm.length INTO v_new_wagon_length
    FROM Wagon w
    JOIN WagonModel wm ON w.wagonModelId = wm.id
    WHERE w.numberWagon = :NEW.wagonNumber;

    v_total_length := v_sum_locomotives + v_sum_wagons + v_new_wagon_length;

    SELECT maxLenght INTO v_max_length
    FROM Train
    WHERE id = v_train_id;

    IF v_total_length > v_max_length THEN
        RAISE_APPLICATION_ERROR(-20330, 'Error: The total length of the train exceeds the maximum limit.');
    END IF;
END;




-------------------------------------------------------------------------------

-- Tests Locomotives Train 5421--
INSERT INTO Locomotive_Train (locomotiveNumber, trainId)
VALUES (5630, 5421);

DELETE FROM Locomotive_Train
WHERE locomotiveNumber = 5630
  AND trainId = 5421;

-- Tests Freights Train 5421 --
INSERT INTO WagonFreights (wagonNumber, freightsId)
VALUES (1811011, 1002);

DELETE FROM WagonFreights
WHERE wagonNumber = 1811011
  AND freightsId = 1002;

  -- Tests Locomotives Train 5437--
  INSERT INTO Locomotive_Train (locomotiveNumber, trainId)
  VALUES (5630, 5437);

  DELETE FROM Locomotive_Train
  WHERE locomotiveNumber = 5630
    AND trainId = 5437;

  -- Tests Freights Train 5437 --
  INSERT INTO WagonFreights (wagonNumber, freightsId)
  VALUES (1811011, 1003);

  DELETE FROM WagonFreights
  WHERE wagonNumber = 1811011
    AND freightsId = 1003;



------------------------------------------------------
-- Consulta para ver o Lenght total de um Train--

SELECT SUM(length) AS total_train_length
FROM (
    SELECT lm.length AS length
    FROM Locomotive_Train lt
    JOIN Locomotive l ON lt.locomotiveNumber = l.numberLocomotive
    JOIN LocomotiveModel lm ON l.model = lm.id
    WHERE lt.trainId = 5421

    UNION ALL

    SELECT wm.length AS length
    FROM WagonFreights wf
    JOIN Wagon w ON wf.wagonNumber = w.numberWagon
    JOIN WagonModel wm ON w.wagonModelId = wm.id
    JOIN Freights f ON wf.freightsId = f.id
    WHERE f.trainId = 5421
);
