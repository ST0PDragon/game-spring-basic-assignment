package com.gamebasic.ranking.client;

import com.gamebasic.ranking.dto.source.RankingSource;
import com.gamebasic.ranking.exception.RankingSourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class RankingClient {

    private static final String SOURCE_URL =
            "https://f-api.github.io/game-spring-api-docs/basic/rankings.json";

    private final RestClient rankingRestClient;

    public RankingSource fetch() {
        try {
            RankingSource source = rankingRestClient.get()
                    .uri(SOURCE_URL)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(RankingSource.class);

            if (source == null) {
                throw new RankingSourceException("외부 랭킹 응답이 비어 있습니다.");
            }
            return source;
        } catch (RestClientException e) {
            throw new RankingSourceException("외부 랭킹 정보를 불러올 수 없습니다.", e);
        }
    }
}
