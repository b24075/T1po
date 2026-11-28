package oit.is.t1.t1project.match;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MatchMapper {

  void insert(MatchRecord match);

  int update(MatchRecord match);

  MatchRecord findById(@Param("matchId") String matchId);

  List<MatchRecord> findWaitingHumanMatches();

  List<MatchRecord> findByPlayer(@Param("playerName") String playerName);
}
