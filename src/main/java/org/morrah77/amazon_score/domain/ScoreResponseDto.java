package org.morrah77.amazon_score.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
public class ScoreResponseDto implements Serializable {
    @JsonProperty("Keyword")
    String keyword;
    @JsonProperty("score")
    Integer score;
}
