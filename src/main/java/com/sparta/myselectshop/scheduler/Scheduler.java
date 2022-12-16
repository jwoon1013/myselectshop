package com.sparta.myselectshop.scheduler;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.naver.service.NaverApiService;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final NaverApiService naverApiService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 1 * * *") // (1)1시가 될때마다
    public void updatePrice() throws InterruptedException {
        log.info("가격 업데이트 실행");
        List<Product> productList = productRepository.findAll(); // (2)productRepository를 통해 모든 product를 가져옴
        for (Product product : productList) {
            // 1초에 한 상품 씩 조회합니다 (NAVER 제한)
            TimeUnit.SECONDS.sleep(1);

            String title = product.getTitle(); // (3) product의 title을 가져옴
            List<ItemDto> itemDtoList = naverApiService.searchItems(title); // (4) title을 통해 naverApi를 통해 아이템 리스트 가져온다.
            ItemDto itemDto = itemDtoList.get(0); // 가장 상단에 있는(index0) 아이템 가져와. itemdto에 정보 넣음.

            // i 번째 관심 상품 정보를 업데이트합니다.
            Long id = product.getId();
            productService.updateBySearch(id, itemDto); // 가져온 최상단 아이템의 id를 통해 업데이트 실행.
        }
    }
}