package com.gamebasic.runcard.repository;

import com.gamebasic.game.entity.Game;
import com.gamebasic.runcard.entity.RunCard;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gamebasic.runcard.dto.DeckCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunCardRepository extends JpaRepository<RunCard, Long> {
    List<RunCard> findAllByGameOrderByIdAsc(Game game);

    void deleteAllByGame(Game game);

    // TODO (Lv 11): @Query 작성
    @Query("""
    select new com.gamebasic.runcard.dto.DeckCount(
        c.game.id,
        count(c)
    )
    from RunCard c
    where c.game in :games
    group by c.game.id
    """)
    List<DeckCount> countByGames(@Param("games") List<Game> games);
    // List<DeckCount> countByGames(List<Game> games);

}
