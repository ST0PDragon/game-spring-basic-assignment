package com.gamebasic.ranking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RankingResponse {

    private final String season;
    private final int totalRecords;
    private final int excludedCount;
    private final List<RankingEntryResponse> entries;
}