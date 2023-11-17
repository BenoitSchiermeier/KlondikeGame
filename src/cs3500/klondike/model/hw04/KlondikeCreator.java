package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * The KlondikeCreator class serves as a factory for creating various types of Klondike games.
 */
public class KlondikeCreator {

  /**
   * The GameType enum represents the different types of Klondike games that can be created.
   */
  public enum GameType {
    BASIC, LIMITED, WHITEHEAD
  }

  /**
   * Creates a new instance of a Klondike game based on the provided game type.
   *
   * @param type The type of Klondike game to be created.
   * @return A new instance of the specified Klondike game.
   * @throws IllegalArgumentException if an invalid game type is provided.
   */
  public static KlondikeModel create(GameType type) {
    return createKlondike(type, 2);
  }


  /**
   * Creates a Klondike game model based on the specified game type.
   *
   * @param type the type of Klondike game to be created.
   *             This can be BASIC, LIMITED, or WHITEHEAD.
   * @param numTimesRedrawAllowed the number of times redraw is allowed in a LIMITED game.
   *                              This parameter is ignored for other game types.
   * @return a new instance of the specified Klondike game type.
   * @throws IllegalArgumentException if an invalid game type is provided.
   */
  public static KlondikeModel createKlondike(GameType type, int numTimesRedrawAllowed) {
    switch (type) {
      case BASIC:
        return new BasicKlondike();
      case LIMITED:
        return new LimitedDrawKlondike(numTimesRedrawAllowed);
      case WHITEHEAD:
        return new WhiteheadKlondike();
      default:
        throw new IllegalArgumentException("Invalid game type");
    }
  }
}
