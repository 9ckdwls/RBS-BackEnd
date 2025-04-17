package com.example.rbs.dto;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxDTO {
	
	private int id;

	private String name;
	
	private String IPAddress;
	
	private double longitude;
	
    private double latitude;
    
    private MultipartFile file;
    
    public Point toPoint() {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
