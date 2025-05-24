package com.example.rbs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogItems;
import com.example.rbs.repository.BoxLogItemsRepository;

@Service
public class BoxLogItemsService {

	private final BoxLogItemsRepository boxLogItemsRepository;

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
	public void collectionCompleted(int boxId, int boxLogId, List<BoxLog> boxLogList) {
		Map<String, Integer> totalCountByName = new HashMap<>();

		// 2) 모든 BoxLog에 대해 BoxLogItems 조회해서 Map에 합산
		for (BoxLog boxLog : boxLogList) {
			List<BoxLogItems> items = boxLogItemsRepository.findByBoxLogId(boxLog.getLogId());
			for (BoxLogItems item : items) {
				totalCountByName.merge(item.getName(), item.getCount(), Integer::sum // 기존 값과 item.getCount()를 합산
				);
			}
		}

		// 3) 합산된 결과로 새로운 BoxLogItems 리스트 생성
		List<BoxLogItems> aggregatedItems = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : totalCountByName.entrySet()) {
			BoxLogItems agg = new BoxLogItems();
			agg.setBoxLogId(boxLogId);
			agg.setName(entry.getKey()); // 품목명
			agg.setCount(entry.getValue()); // 합산된 개수
			aggregatedItems.add(agg);

			boxLogItemsRepository.save(agg);
		}
	}

}
