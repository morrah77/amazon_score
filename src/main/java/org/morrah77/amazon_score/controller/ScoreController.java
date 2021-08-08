package org.morrah77.amazon_score.controller;

import org.morrah77.amazon_score.domain.ScoreResponseDto;
import org.morrah77.amazon_score.service.IScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ScoreController {
    @Autowired
    IScoreService scoreService;

    @GetMapping(value="/estimate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<ScoreResponseDto> estimate(@RequestParam String keyword) {
        return makeResponse(keyword);
    }

    // TODO move this method into a separate service; process errors with @ControllerAdvice instead of hard-coding them!!!
    ResponseEntity makeResponse(String keyword) {
        try {
            Integer score = scoreService.getScore(keyword);
            return new ResponseEntity<ScoreResponseDto>(new ScoreResponseDto(keyword, score), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ScoreResponseDto>(new ScoreResponseDto(keyword, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
