package oit.is.t1.t1project.result;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchResultMapper {

  @Insert("""
      INSERT INTO match_results (
        match_id,
        mode,
        first_player,
        second_player,
        winner,
        finished_at
      )
      VALUES (
        #{matchId},
        #{mode},
        #{firstPlayer},
        #{secondPlayer},
        #{winner},
        #{finishedAt}
      )
      """)
  void insert(MatchResult result);

  @Select("""
      SELECT
        match_id,
        mode,
        first_player,
        second_player,
        winner,
        finished_at
      FROM match_results
      WHERE first_player = #{playerName}
         OR second_player = #{playerName}
      ORDER BY finished_at DESC
      """)
  List<MatchResult> findByPlayer(String playerName);

  @Select("""
      SELECT
        match_id,
        mode,
        first_player,
        second_player,
        winner,
        finished_at
      FROM match_results
      WHERE match_id = #{matchId}
      """)
  MatchResult findByMatchId(String matchId);
}
