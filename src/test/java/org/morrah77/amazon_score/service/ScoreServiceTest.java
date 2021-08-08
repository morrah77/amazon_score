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
        {put("smart", new Pair(10, parseStringToList("smart watch,smart tv,smart light bulbs,32 in smart tv,samsung smart watch,amazon smart plug,owlet smart sock 3,smart plugs,smart sweets,smart lock")));}
        {put("phone", new Pair(10, parseStringToList("portable phone charger,phone case,car phone holder mount,waterproof phone pouch,phone mount for car,iphone xs max phone case,phone stand,phone,phone holder,phone cases for iphone 11")));}};

    @Mock(lenient = true)
    @Qualifier("AmazonDataService")
    private AmazonDataService dataService;

    @InjectMocks
    private ScoreService service;

    // TODO generalize it!!!
    @BeforeEach
    public void setUp() {
        List<Object> entryPhone = new ArrayList<>();
        entryPhone.add("phone");
        entryPhone.add(data.get("phone").getValue());
        ResponseEntity responseEntityPhone = new ResponseEntity<AmazonResponseEntity>(
                new AmazonResponseEntity(entryPhone), HttpStatus.OK);
        Mockito.doReturn(responseEntityPhone)
                .when(dataService)
                .getData(Mockito.eq("phone"));

        List<Object> entrySmart = new ArrayList<>();
        entrySmart.add("smart");
        entrySmart.add(data.get("smart").getValue());
        ResponseEntity responseEntitySmart = new ResponseEntity<AmazonResponseEntity>(
        new AmazonResponseEntity(entrySmart), HttpStatus.OK);
                Mockito.doReturn(responseEntitySmart)
                .when(dataService)
                .getData(Mockito.eq("smart"));
    }

    @ParameterizedTest
    @MethodSource("mapEntryArgumentProvider")
    void calculateScore(Map.Entry<String, Pair<Integer, List<Object>>> testCase) {
        assertAll(
                () -> assertEquals(testCase.getValue().getKey(), service.calculateScore(testCase.getValue().getValue()))
        );
    }

    @Test
    void getScore() {
        final int[] result = new int[1];
        assertAll(
                () -> assertDoesNotThrow(() -> result[0] = service.getScore("smart")),
                () -> Mockito.verify(dataService, Mockito.times(1)).getData("smart"),
                () -> assertEquals(10, result[0])
        );
    }

    static Stream<Map.Entry<String, Pair<Integer, List<Object>>>> mapEntryArgumentProvider() {
        return data.entrySet().stream();
    }

    private static List<String> parseStringToList(String source) {
        return java.util.Arrays.stream(source.split(",")).map(s -> s.trim()).collect(Collectors.toList());
    }
}