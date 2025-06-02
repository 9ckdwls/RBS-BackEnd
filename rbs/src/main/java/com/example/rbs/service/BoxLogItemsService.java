package com.example.rbs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogItems;
import com.example.rbs.repository.BoxLogItemsRepository;

@Service
public class BoxLogItemsService {

	private final BoxLogItemsRepository boxLogItemsRepository;
	@Value("${battery}")
	int battery;
	@Value("${discharged}")
	int discharged;
	@Value("${notDischarged}")
	int notDischarged;

	public BoxLogItemsService(BoxLogItemsRepository boxLogItemsRepository) {
		this.boxLogItemsRepository = boxLogItemsRepository;
	}

	// 수거로그 아이디로 아이템 가져오기
	public List<BoxLogItems> getBoxLogItems(int id) {
		List<BoxLogItems> boxLogItems = boxLogItemsRepository.findByBoxLogId(id);
		return boxLogItems;
	}

	// 수거 완료
	// 수거로그 아이템 추가
	public int collectionCompleted(int boxId, int boxLogId, List<BoxLog> boxLogList) {
	    // 1) 아이템별 단가 정의
	    Map<String, Integer> unitPriceMap = Map.of(
	        "battery", battery,
	        "discharged", discharged,
	        "notDischarged", notDischarged
	    );
	    
	    int totalValue = 0;

	    // 2) 모든 BoxLog에 대해 BoxLogItems 조회해서 Map에 합산
	    Map<String, Integer> totalCountByName = new HashMap<>();
	    for (BoxLog boxLog : boxLogList) {
	        List<BoxLogItems> items = boxLogItemsRepository.findByBoxLogId(boxLog.getLogId());
	        for (BoxLogItems item : items) {
	            totalCountByName.merge(item.getName(), item.getCount(), Integer::sum);
	        }
	    }

	    // 3) 합산된 결과로 새로운 BoxLogItems 리스트 생성 및 저장
	    List<BoxLogItems> aggregatedItems = new ArrayList<>();
	    for (Map.Entry<String, Integer> entry : totalCountByName.entrySet()) {
	        String name = entry.getKey();
	        int count = entry.getValue();
	        int unitPrice = unitPriceMap.getOrDefault(name, 0);
	        totalValue += count * unitPrice;
	        
	        System.out.println("수거한 물품당 가격" + totalValue);

	        BoxLogItems agg = new BoxLogItems();
	        agg.setBoxLogId(boxLogId);
	        agg.setName(name);           // 품목명
	        agg.setCount(count);         // 합산된 개수
	        aggregatedItems.add(agg);
	        boxLogItemsRepository.save(agg);
	    }
	    
	    return totalValue;
	}


}
