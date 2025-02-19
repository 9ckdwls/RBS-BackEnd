package com.example.rbs.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AlarmCheck implements Serializable {
	
	@EmbeddedId
	private AlarmCheckId id;
	
	private Date data;
}
