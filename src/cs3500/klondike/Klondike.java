package cs3500.klondike;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import java.io.InputStreamReader;

/**
 * The main class to run the Klondike game based on command line arguments.
 * This class parses the command line arguments, determines the game type,
 * and initializes the game with the specified parameters.
 */
public final class Klondike {

  /**
   * The entry point of the Klondike game application. This method processes
   * the command line arguments and starts the game.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // STEP 1: DETERMINE THE FIRST ARGUMENT TO CHOOSE GAME TYPE
    if (args.length < 1) {
      throw new IllegalArgumentException("no args on command line");
    }

    // gets the first argument and determines if it is limited,
    // whitehead or basic Klondike:
    GameType gameType = getGameType(args);

    int argumentIndex = 1;
    int numRedraws = -1; // will be set if game type is limited

    // STEP 2: IF GAME TYPE IS LIMITED FIND NUMBER OF RE-DRAW ALLOWED
    if (gameType == KlondikeCreator.GameType.LIMITED) {
      if (args.length > argumentIndex) {
        try {
          // number of times the draw pile can be used = redraws +1
          numRedraws = Integer.parseInt(args[argumentIndex].trim()) - 1;
          // make sure that it is a valid number of redraws
          if (numRedraws < 0) {
            throw new IllegalArgumentException("invalid number of redraws");
          }
          // increase the index
          argumentIndex++;
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("invalid redraws number");
        }
        // if there is no value after limited :
      } else {
        throw new IllegalArgumentException("missing arg (numRedraws) for limited game");
      }
    }

    findPilesAndDrawsAndStartGame(args, argumentIndex, gameType, numRedraws);
  }

  /**
   * Processes the command line arguments to determine the number of cascade piles
   * and draw cards. After which, it initiates the game with the specified parameters.
   *
   * @param args the command line arguments
   * @param argumentIndex the current index of the argument being processed
   * @param gameType the type of Klondike game to be played
   * @param numRedraws the number of times the draw pile can be redrawn
   */
  private static void findPilesAndDrawsAndStartGame(String[] args,
      int argumentIndex, GameType gameType, int numRedraws) {
    try {
      // STEP 3: FIND THE NUMBER OF CASCADE PILES AND DRAW CARDS FROM ARGS:
      int numPiles = (args.length > argumentIndex) ? Integer.parseInt(args[argumentIndex++]) : 7;
      int numDraw = (args.length > argumentIndex) ? Integer.parseInt(args[argumentIndex++]) : 3;

      startGameInMainWithController(gameType, numRedraws, numPiles, numDraw);
    } catch (NumberFormatException e) {
      // do nothing with it so that it does not crash
    } catch (IllegalStateException | IllegalArgumentException e) {
      // do nothing
    }
  }


  /**
   * Starts the Klondike game with the specified parameters using the textual controller.
   *
   * @param gameType the type of the Klondike game (BASIC, LIMITED, WHITEHEAD)
   * @param numRedraws the number of times the draw pile can be redrawn
   * @param numPiles the number of cascade piles
   * @param numDraw the number of cards to draw at a time
   */
  private static void startGameInMainWithController(GameType gameType, int numRedraws, int numPiles,
      int numDraw) {
    KlondikeModel model = KlondikeCreator.createKlondike(gameType, numRedraws);
    Readable reader = new InputStreamReader(System.in);

    KlondikeController controller =
        new KlondikeTextualController(reader, System.out);
    controller.playGame(model, model.getDeck(), false, numPiles, numDraw);
  }

  /**
   * Determines the game type based on the first command line argument.
   *
   * @param args the command line arguments
   * @return the type of the Klondike game (BASIC, LIMITED, WHITEHEAD)
   * @throws IllegalArgumentException if the game type argument is invalid
   */
  private static GameType getGameType(String[] args) {
    GameType gameType;
    switch (args[0].toLowerCase()) {
      case "basic":
        gameType = GameType.BASIC;
        break;
      case "limited":
        gameType = GameType.LIMITED;
        break;
      case "whitehead":
        gameType = GameType.WHITEHEAD;
        break;
      default:
        throw new IllegalArgumentException("invalid game type");
    }
    return gameType;
  }


}
