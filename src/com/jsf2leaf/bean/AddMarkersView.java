package com.jsf2leaf.bean;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "addMarkersView")
@RequestScoped
public class AddMarkersView implements Serializable {

    private MapModel mapModel;

    private String title;
    private String description;
    private String rankPlace;
    private String website;
    private double lat;
    private double lng;

    @PostConstruct
    public void init() {
        mapModel = new DefaultMapModel();
        List<Marker> markerList = getAllMarkers();
        for (Marker m : markerList) {
            mapModel.addOverlay(m);
        }
    }

    private List<Marker> getAllMarkers() {
        List<Marker> markers = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234567890");
            String query = "SELECT * FROM description;";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next() == false) {
                System.out.println("Brak znaczników.");
            } else {
                do {
                    Marker marker = new Marker(
                            new LatLng(rs.getDouble("latitude"), rs.getDouble("longtitude")),
                            rs.getString("name"));
                    markers.add(marker);
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return markers;
    }

    public MapModel getEmptyModel() {
        return mapModel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void addMarker() {
        Marker marker = new Marker(new LatLng(lat, lng), title);
        mapModel.addOverlay(marker);
        saveMarker();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Znacznik dodany", "Lat:" + lat + ", Lng:" + lng));
    }

    private void saveMarker() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234567890");
            String query = "INSERT INTO public.description (name, description, latitude, longtitude, rankPlace, website) VALUES (?, ? ,? ,? ,? ,? )";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, getTitle());
            statement.setString(2, getDescription());
            statement.setDouble(3, getLat());
            statement.setDouble(4, getLng());
            statement.setString(5, getRankPlace());
            statement.setString(6, getWebsite());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRankPlace() {
        return this.rankPlace;
    }

    public void setRankPlace(String rankPlace) {
        this.rankPlace = rankPlace;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
