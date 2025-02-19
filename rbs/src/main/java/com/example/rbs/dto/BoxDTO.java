package com.example.rbs.dto;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxDTO {

	private String name;
	
	private String IPAddress;
	
	private double longitude;
	
    private double latitude;
    
    public Point toPoint() {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
