package com.example.rbs.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class AlarmCheckId implements Serializable {

	@ManyToOne
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // equals, hashCode (복합키를 사용하는 경우 필수)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlarmCheckId that = (AlarmCheckId) o;

        if (!alarm.equals(that.alarm)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = alarm.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }
}
