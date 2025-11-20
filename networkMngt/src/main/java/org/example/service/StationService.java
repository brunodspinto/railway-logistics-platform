package org.example.service;

import org.example.Results.RadiusResult;
import org.example.data.StationLoader;
import org.example.domain.Station;
import org.example.queries.*;
import org.example.trees.StationIndexes;

import java.io.IOException;
import java.util.*;

public class StationService {
    private StationLoader loader;
    private StationIndexes indexes;
    private TimeZoneQuery tzQuery;
    private CoordinateQuery coordQuery;
    private SampleQueries samples;
    private boolean ready;
    private NearestNQuery nearestQuery;
    private RadiusSearchQuery radiusQuery;

    public StationService() {
        this.loader = new StationLoader();
        this.indexes = new StationIndexes();
        this.ready = false;
    }

    public boolean initialize(String path) {
        try {
            List<Station> stations = loader.loadFromCSV(path);
            System.out.println(loader.getReport());

            if (stations.isEmpty()) {
                System.err.println("No valid stations");
                return false;
            }

            indexes.buildIndexes(stations);

            this.tzQuery = new TimeZoneQuery(indexes);
            this.coordQuery = new CoordinateQuery(indexes);
            this.samples = new SampleQueries(indexes);

            this.nearestQuery = new NearestNQuery(indexes.getSpatialIndex());
            this.radiusQuery = new RadiusSearchQuery(indexes.getSpatialIndex());

            this.ready = true;
            return true;

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean isReady() {
        return ready;
    }

    private void check() {
        if (!ready) throw new IllegalStateException("Not initialized");
    }

    public QueryResult queryByTimeZoneGroup(String tz) {
        check();
        return tzQuery.queryByTimeZoneGroup(tz);
    }

    public QueryResult queryByTimeZoneGroupAndCountry(String tz, String country) {
        check();
        return tzQuery.queryByTimeZoneGroupAndCountry(tz, country);
    }

    public QueryResult queryByTimeZoneWindow(List<String> tzs) {
        check();
        return tzQuery.queryByTimeZoneWindow(tzs);
    }

    public QueryResult queryByLatitudeRange(double min, double max) {
        check();
        return coordQuery.queryByLatitudeRange(min, max);
    }

    public QueryResult queryByLongitudeRange(double min, double max) {
        check();
        return coordQuery.queryByLongitudeRange(min, max);
    }

    public QueryResult queryByExactCoordinates(double lat, double lon) {
        check();
        return coordQuery.queryByExactCoordinates(lat, lon);
    }

    public String runAllSampleQueries() {
        check();
        return samples.runAllSamples();
    }

    public QueryResult runSampleQuery1() {
        check();
        return samples.sample1_AllCETStations();
    }

    public QueryResult runSampleQuery2() {
        check();
        return samples.sample2_PortugueseWETStations();
    }

    public QueryResult runSampleQuery3() {
        check();
        return samples.sample3_TimeZoneWindow();
    }


    public QueryResult runSampleQuery4() {
        check();
        return samples.sample4_LisbonDuplicateCoordinates();
    }

    public String getPerformanceReport() {
        check();
        return indexes.getReport();
    }

    public String getImportReport() {
        return loader.getReport();
    }

    public StationIndexes getIndexes() {
        return indexes;
    }

    public List<NearestNQuery.StationDistance> queryNearestN(double lat, double lon, int n, String tzGroupFilter, String countryFilter) {
        check();
        return nearestQuery.nearestN(lat, lon, n, tzGroupFilter, countryFilter);
    }

    public RadiusResult queryRadius(double lat, double lon, double radiusKm) {
        check();
        return radiusQuery.search(lat, lon, radiusKm);
    }
}