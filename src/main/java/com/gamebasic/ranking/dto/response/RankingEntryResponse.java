package com.gamebasic.ranking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingEntryResponse {

    private final int rank;
    private final String playerName;
    private final int clearTimeSeconds;
    private final int remainingHp;
    private final int bossTurns;
    private final int deckSize;
}