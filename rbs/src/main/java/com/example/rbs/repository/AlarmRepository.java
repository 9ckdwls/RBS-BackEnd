package com.example.rbs.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;
import com.example.rbs.entity.Alarm.AlarmType;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

	// 미해결된 모든 알람
	@Query("SELECT a FROM Alarm a WHERE " +
		       "((a.userId = :userId OR a.targetUserId = :userId) " + 
		       "OR (a.role = :role OR a.role = 'ROLE_ALL')) " +
		       "AND a.resolved = 'UNRESOLVED'")
		List<Alarm> findRelevantAlarms(@Param("role") String role, @Param("userId") String userId);


	// boxId로 알람 찾기
	Optional<Alarm> findByBoxId(int id);

	// 관리자가 볼 알람
	// 권한이 ROLE_ADMIN or ROLE_ALL 이고 미해결된 알람
	List<Alarm> findByResolvedAndRoleIn(AlarmStatus unresolved, List<String> roles);
	
	// 화재 관련 로그
	@Query("SELECT a FROM Alarm a WHERE a.type IN ('FIRE', 'FIRE_IN_PROGRESS', 'FIRE_COMPLETED')")
    List<Alarm> findFireLogs();
	
	@Query("""
		      SELECT a
		        FROM Alarm a
		       WHERE a.boxId = :boxId
		         AND a.resolved = 'UNRESOLVED'
		         AND a.type IN ('COLLECTION_RECOMMENDED', 'COLLECTION_NEEDED')
		    """)
		    List<Alarm> findUnresolvedCollectionAlarms(@Param("boxId") int boxId);
	
    //타입 리스트, resolved 상태로 Alarm 조회
	List<Alarm> findByBoxIdAndTypeInAndResolved(
	        int boxId,
	        Collection<AlarmType> types,
	        AlarmStatus resolved
	    );


}
