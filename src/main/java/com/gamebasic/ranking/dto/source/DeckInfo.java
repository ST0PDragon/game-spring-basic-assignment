package com.gamebasic.ranking.dto.source;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeckInfo {

    private Integer size;
    private List<SourceCard> cards;
}