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

    @Test
    void testRecommendNextNearEndOfDay() {
        // Given: Record ending at 1430 (23:50)
        List<TimeRecordEntity> records = new ArrayList<>();
        records.add(createRecord(0, 1430));

        LocalDate targetDate = LocalDate.now().minusDays(1); // Not today

        // When
        TimeRecordEntity result = timeRecordService.calculateRecommendNext(records, targetDate);

        // Then: Should recommend 1431 - 1439 (not 1461)
        assertEquals(1431, result.getStartTime());
        assertEquals(1439, result.getEndTime());
        assertEquals(9, result.getDuration());
    }

    @Test
    void testRecommendNextAtEndOfDay() {
        // Given: Record ending at 1439 (23:59)
        List<TimeRecordEntity> records = new ArrayList<>();
        records.add(createRecord(0, 1439));

        LocalDate targetDate = LocalDate.now().minusDays(1); // Not today

        // When
        TimeRecordEntity result = timeRecordService.calculateRecommendNext(records, targetDate);

        // Then: Should recommend 1439 - 1439 (since we cap at 1439)
        assertEquals(1439, result.getStartTime());
        assertEquals(1439, result.getEndTime());
        assertEquals(1, result.getDuration());
    }

    private TimeRecordEntity createRecord(int start, int end) {
        TimeRecordEntity record = new TimeRecordEntity();
        record.setStartTime(start);
        record.setEndTime(end);
        return record;
    }
}
