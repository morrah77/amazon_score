package org.morrah77.amazon_score.domain;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class AmazonResponseEntity implements Serializable {
    List<Object> entry;
}
