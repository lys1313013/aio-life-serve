package top.aiolife.record.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.aiolife.record.pojo.entity.TimeRecordEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeRecordServiceImplTest {

    private TimeRecordServiceImpl timeRecordService;

    @BeforeEach
    void setUp() {
        timeRecordService = new TimeRecordServiceImpl();
    }

    @Test
    void testRecommendNextWithGap() {
        // Given: 0-10, 11-12, 15-20
        List<TimeRecordEntity> records = new ArrayList<>();
        records.add(createRecord(0, 10));
        records.add(createRecord(11, 12));
        records.add(createRecord(15, 20));

        LocalDate targetDate = LocalDate.now().minusDays(1); // Not today

        // When
        TimeRecordEntity result = timeRecordService.calculateRecommendNext(records, targetDate);

        // Then: Should recommend 13-14
        assertEquals(13, result.getStartTime());
        assertEquals(14, result.getEndTime());
    }

    @Test
    void testRecommendNextNoGapNotToday() {
        // Given: 0-10
        List<TimeRecordEntity> records = new ArrayList<>();
        records.add(createRecord(0, 10));

        LocalDate targetDate = LocalDate.now().minusDays(1); // Not today

        // When
        TimeRecordEntity result = timeRecordService.calculateRecommendNext(records, targetDate);

        // Then: Should recommend 11 - 41
        assertEquals(11, result.getStartTime());
        assertEquals(41, result.getEndTime());
    }

    @Test
    void testRecommendNextEmptyRecords() {
        // Given: empty
        List<TimeRecordEntity> records = new ArrayList<>();

        LocalDate targetDate = LocalDate.now().minusDays(1); // Not today

        // When
        TimeRecordEntity result = timeRecordService.calculateRecommendNext(records, targetDate);

        // Then: Should recommend 0 - 30
        assertEquals(0, result.getStartTime());
        assertEquals(30, result.getEndTime());
    }

    private TimeRecordEntity createRecord(int start, int end) {
        TimeRecordEntity record = new TimeRecordEntity();
        record.setStartTime(start);
        record.setEndTime(end);
        return record;
    }
}
