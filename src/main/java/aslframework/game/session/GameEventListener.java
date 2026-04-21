package aslframework.game.session;


import aslframework.game.result.GameResult;
import aslframework.persistence.AttemptRecord;

import java.util.List;

/**
 * Observer interface for game session events.
 *
 * <p>Sessions call these methods at key moments. By injecting a listener,
 * callers (UI, logging, analytics) receive structured events without the
 * session needing to know anything about how they are handled.
 *
 * <p>This removes all {@code System.out.printf} calls from session logic —
 * output is entirely the listener's responsibility.
 *
 * <p>A no-op default implementation {@link NoOpGameEventListener} is provided
 * so callers only override the events they care about.
 *
 * <h2>Example — console logger</h2>
 * <pre>{@code
 * GameSession session = GameSessionFactory.startPractice(
 *     recognizer, library, "alice", new ConsoleGameEventListener());
 * }</pre>
 */
public interface GameEventListener {

  // ── Common events ────────────────────────────────────────────────────────────

  /**
   * Fired after any attempt is evaluated.
   *
   * @param letter   the letter that was attempted
   * @param record   the resulting attempt record
   * @param streak   consecutive successes on this letter (practice) or 0 (battle)
   */
  void onAttempt(String letter, AttemptRecord record, int streak);

  /**
   * Fired when a letter is fully cleared (practice: streak complete; battle: n/a).
   *
   * @param letter       the cleared letter
   * @param letterScore  points earned for this letter (base + bonus)
   * @param totalScore   cumulative score after this letter
   */
  void onLetterCleared(String letter, int letterScore, int totalScore);

  /**
   * Fired when the session ends (naturally or abandoned).
   *
   * @param result the final game result
   */
  void onSessionFinished(GameResult result);


}
