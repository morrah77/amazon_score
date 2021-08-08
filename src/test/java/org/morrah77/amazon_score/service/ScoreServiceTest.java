package org.morrah77.amazon_score.service;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.morrah77.amazon_score.domain.AmazonResponseEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {
    static Map<String, Pair<Integer, List<Object>>> data = new HashMap<String, Pair<Integer, List<Object>>>(){
        {put("smart", new Pair(24, parseStringToList("smart watch,smart tv,smart light bulbs,32 in smart tv,samsung smart watch,amazon smart plug,owlet smart sock 3,smart plugs,smart sweets,smart lock")));} // (6 * 0.4 / 10) * 100 = 24
        {put("phone", new Pair(100, parseStringToList("portable phone charger,phone case,car phone holder mount,waterproof phone pouch,phone mount for car,iphone xs max phone case,phone stand,phone,phone holder,phone cases for iphone 11")));} // 100
        {put("alfa", new Pair(100, parseStringToList("alfa, beta, gamma")));} // 100
        {put("beta", new Pair(4, parseStringToList("beta one, beta two, beta three, not beta")));} // (3 * (0.4/(1+10-4)) / 4) * 100 = 4
    };

    @Mock(lenient = true)
    @Qualifier("AmazonDataService")
    private AmazonDataService dataService;

    @InjectMocks
    private ScoreService service;

    @BeforeEach
    public void setUp() {
        for (Map.Entry<String, Pair<Integer, List<Object>>> entry: data.entrySet()) {
            List<Object> entity = new ArrayList<>();
            entity.add(entry.getKey());
            entity.add(entry.getValue().getValue());
            ResponseEntity responseEntityPhone = new ResponseEntity<AmazonResponseEntity>(
                    new AmazonResponseEntity(entity), HttpStatus.OK);
            Mockito.doReturn(responseEntityPhone)
                    .when(dataService)
                    .getData(Mockito.eq(entry.getKey()));
        }
    }

    @ParameterizedTest
    @MethodSource("mapEntryArgumentProvider")
    void calculateScore(Map.Entry<String, Pair<Integer, List<Object>>> testCase) {
        assertAll(
                () -> assertEquals(testCase.getValue().getKey(), service.calculateScore(testCase.getKey(), testCase.getValue().getValue()))
        );
    }

    @Test
    void getScore() {
        final int[] result = new int[1];
        assertAll(
                () -> assertDoesNotThrow(() -> result[0] = service.getScore("smart")),
                () -> Mockito.verify(dataService, Mockito.times(1)).getData("smart"),
                () -> assertEquals(24, result[0])
        );
    }

    static Stream<Map.Entry<String, Pair<Integer, List<Object>>>> mapEntryArgumentProvider() {
        return data.entrySet().stream();
    }

    private static List<String> parseStringToList(String source) {
        return java.util.Arrays.stream(source.split(",")).map(s -> s.trim()).collect(Collectors.toList());
    }
}