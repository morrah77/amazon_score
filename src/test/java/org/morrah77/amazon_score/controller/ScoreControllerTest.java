package org.morrah77.amazon_score.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.morrah77.amazon_score.service.AmazonDataService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScoreControllerTest {
    @MockBean
    private RestTemplate dataRestTemplate;

    @MockBean
    private Logger log;

    @Qualifier("AmazonDataService")
    @InjectMocks
    private AmazonDataService dataService;

    @Autowired
    private ScoreController controller;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private static final String TEST_URL_TEMPLATE = "http://localhost:%d/estimate?keyword=%s";

    @BeforeEach
    public void setUp() {
        Mockito.doReturn(new ResponseEntity<String>("[\"phone\",[\"portable phone charger\",\"phone case\",\"car phone holder mount\",\"waterproof phone pouch\",\"phone mount for car\",\"iphone xs max phone case\",\"phone stand\",\"phone\",\"phone holder\",\"phone cases for iphone 11\"],[{},{},{},{},{},{},{},{},{},{}],[],\"UTJ3T4KLC7GU\"]", HttpStatus.OK))
                .when(dataRestTemplate)
                .getForEntity(Mockito.eq("https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-cli&mkt=1&q=phone"),
                        Mockito.eq(String.class),
                        Mockito.anyMap());
        Mockito.doReturn(new ResponseEntity<String>("[\"smart\",[\"smart watch\",\"smart tv\",\"smart light bulbs\",\"32 in smart tv\",\"samsung smart watch\",\"amazon smart plug\",\"owlet smart sock 3\",\"smart plugs\",\"smart sweets\",\"smart lock\"],[{},{},{},{},{},{},{},{},{},{}],[],\"3VYP3ZRG5R3GO\"]", HttpStatus.OK))
                .when(dataRestTemplate)
                .getForEntity(Mockito.eq("https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-cli&mkt=1&q=smart"),
                        Mockito.eq(String.class),
                        Mockito.anyMap());
    }

    @Test
    void springContextLoadsCorrectly() {
        assertAll(
                () -> assertNotNull(controller)
        );
    }

    @Test
    void should_estimate_RespondWithCorrectScore_WhenDataContainSmart() {
        String keyword = "smart";
        final ResponseEntity<String>[] response = new ResponseEntity[1];
        String expectedResponse = "{\"Keyword\":\"smart\",\"score\":10}";
        assertAll(
                () -> assertDoesNotThrow(() -> {
                    response[0] = template.withBasicAuth("user", "password")
                            .getForEntity(makeTestUrl(keyword), String.class);}),
                () -> assertEquals(expectedResponse, response[0].getBody())
        );
        System.out.println(response[0]);

    }

    @Test
    void should_estimate_RespondWithCorrectScore_WhenDataContainPhone() {
        String keyword = "phone";
        final ResponseEntity<String>[] response = new ResponseEntity[1];
        String expectedResponse = "{\"Keyword\":\"phone\",\"score\":10}";
        assertAll(
                () -> assertDoesNotThrow(() -> {
                    response[0] = template.withBasicAuth("user", "password")
                            .getForEntity(makeTestUrl(keyword), String.class);}),
                () -> assertEquals(expectedResponse, response[0].getBody())
        );
        System.out.println(response[0]);

    }

    private String makeTestUrl(String keyword) {
        return String.format(TEST_URL_TEMPLATE, port, keyword);
    }
}