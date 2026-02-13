package io.why503.aiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.aiservice.model.embedding.Performance;
import io.why503.aiservice.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowEmbedService {

    private final VectorStore vectorStore;
    private final PerformanceRepository performanceRepository;
    private final Path csvPath = Paths.get("src/main/resources/공연db.csv");
//    private final Path csvPath = Paths.get("src/main/resources/공연DBtest.csv");
    private final ObjectMapper mapper = new ObjectMapper();

    public void watchFile() {
        Thread watcherThread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                csvPath.getParent().register(watchService,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE
                );
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();
                        if (changed.endsWith(csvPath.getFileName())) {
                            log.info("공연 문서 변경 감지: {}", changed);
                            upsertCheck();
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {
                log.error("문서 변경 감시 오류", e);
            }
        }); watcherThread.setDaemon(true); watcherThread.start(); }

    @Async
    public void upsertCheck() {

        List<Performance> performances = upsertCSV();

        if (performances.isEmpty()) {
            log.warn("임베딩할 공연이 없습니다.");
            return;
        }

        List<Document> documents = performances.stream()
                .map(p -> Performance.toDocument(p))
                .toList();

        //메모리 저장소에 저장
        performanceRepository.saveAll(performances);
        log.info("공연 {}건 메모리 저장 완료", performances.size());
        upsert(documents);
    }

    List<Performance> upsertCSV() {


        List<Performance> performances = new ArrayList<>();

        try (
                InputStream is = getClass()
                        .getClassLoader()
//                        .getResourceAsStream("공연DBtest.csv");
                        .getResourceAsStream("공연db.csv");
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()
                        .parse(reader)
        ) {
            if (is == null) {
                throw new IllegalArgumentException("공연 리소스를 찾을 수 없습니다");
            }

            int index = 1;
            for (CSVRecord record : parser) {
                performances.add(Performance.from(record, index++));
            }

        } catch (Exception e) {
            log.error("공연 CSV 파싱 실패", e);
        }

        return performances;
    }


    /**
     * 컨트롤러 스레드 upsert 호출 -> 엑셀에 파싱하는 스레드 호출 + 임베딩 학습 + 벡터에 저장
     * @param documents
     */
    @Async
    public void upsert(List<Document> documents) {
        vectorStore.add(documents);
        log.info("공연 데이터 {}건 vectorStore upsert 완료", documents.size());
    }
    public List<String> getAllPerformancesJson() {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("공연")
                        .topK(50)  // 필요에 따라 상한 늘림
                        .build()
        );

        // PERFORMANCE 타입 필터링
        List<Document> filteredDocs = docs.stream()
                .filter(d -> "PERFORMANCE".equals(d.getMetadata().get("type")))
                .toList();

        return filteredDocs.stream().map(doc -> {
            try {
                return mapper.writeValueAsString(doc);
            } catch (Exception e) {
                log.error("Document JSON 변환 실패: {}", e.getMessage());
                return "{}";
            }
        }).toList();
    }


}



