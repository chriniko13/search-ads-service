package com.chriniko.searchadsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdsAppearedOnSearchRequest {

    @NotEmpty
    private List<@NotEmpty String> adIds;

}
