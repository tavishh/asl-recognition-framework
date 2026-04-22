package aslframework.game.session;

import aslframework.recognition.GestureLibrary;
import aslframework.recognition.GestureRecognizer;

/**
 * Factory for creating game sessions.
 *
 * <p>Returns the {@link GameSession} interface type so callers are fully
 * decoupled from the concrete implementations.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * GameSession session = GameSessionFactory.startPractice(recognizer, library, "alice");
 * }</pre>
 *
 * <p><strong>Future work:</strong> Battle mode (multi-player) is deferred for a future release.
 */

public final class GameSessionFactory {

  private GameSessionFactory() {}

  /**
   * Creates a single-player practice session.
   *
   * @param recognizer the gesture recognizer backend
   * @param library    the loaded gesture library
   * @param playerId   unique identifier for the player
   * @return a ready-to-use {@link GameSession} (concrete type: {@link PracticeSession})
   */
  public static GameSession startPractice(GestureRecognizer recognizer,
                                           GestureLibrary library,
                                           String playerId) {
    return new PracticeSession(recognizer, library, playerId);
  }

}
