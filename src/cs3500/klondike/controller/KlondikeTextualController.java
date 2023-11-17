package cs3500.klondike.controller;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

import cs3500.klondike.view.KlondikeTextualView;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This controller facilitates textual interaction for the Klondike game. It allows users to input
 * commands and see game outputs through a textual interface.
 */
public class KlondikeTextualController implements cs3500.klondike.controller.KlondikeController {

  // the Readable object from which user input will be read
  private Readable rd;
  // the Appendable object to which game output will be written
  private Appendable ap;


  /**
   * Constructs a new KlondikeTextualController with the given Readable and Appendable objects.
   *
   * @param r the Readable object from which user input will be read
   * @param a the Appendable object to which game output will be written
   * @throws IllegalArgumentException if either the Readable or Appendable inputs are null
   */
  public KlondikeTextualController(Readable r, Appendable a) {
    if ((r == null) || (a == null)) {
      throw new IllegalArgumentException("inputs cannot be null");
    }
    this.rd = r;
    this.ap = a;
  }


  /**
   * Initiates and controls the flow of the Klondike game based on user input.
   *
   * @param model   the Klondike game model
   * @param deck    the deck of cards to be used in the game
   * @param shuffle indicates if the deck should be shuffled
   * @param numRows the number of rows in the game
   * @param numDraw the number of cards to draw
   * @throws IllegalArgumentException if the model is null or the game cannot be started
   * @throws IllegalStateException    if there's an error during input or output
   */
  public void playGame(KlondikeModel model, List<Card> deck, boolean shuffle, int numRows,
      int numDraw) {

    initializeGame(model, deck, shuffle, numRows, numDraw);

    Scanner scanner = new Scanner(rd);
    KlondikeTextualView view = new KlondikeTextualView(model, ap);
    boolean quit = false;

    // STEP 3: PROCESS USER INPUT
    while (!model.isGameOver() && !quit) {
      try {
        String userInput = scanner.next();
        // show the score and board after each move
        view.render();
        ap.append("Score: ").append(Integer.toString(model.getScore()));
        appendN();
        // CHECKS FOR USER INPUT AND ACTS ACCORDINGLY
        switch (userInput) {
          // MOVE PILE
          case "mpp":
            doMpp(model, scanner);
            break;
          // MOVE DRAW:
          case "md":
            doMd(model, scanner);
            break;
          // MOVE PILE TO FOUNDATION:
          case "mpf":
            doMpf(model, scanner);
            break;
          // MOVE DRAW TO FOUNDATION:
          case "mdf":
            doMdf(model, scanner);
            break;
          // DISCARD DRAW:
          case "dd":
            model.discardDraw();
            break;
          // THE GAME IS QUIT:
          case "q":
          case "Q":
            doQuit(model, view);
            quit = true;
            break;
          // if no valid input is found, append an invalid move
          default:
            ap.append("Invalid move. Play again.");
            appendN();
        }
      }
      // handle NullPointerException
      catch (NullPointerException e) {
        handleNullPointer();
      }
      // CATCH THE APPEND EXCEPTIONS
      catch (IOException e) {
        throw new IllegalStateException("Error during input or output.", e);
      }
      // ACTIVATES WHEN QUIT IS NEVER ENCOUNTERED AND THROWS EXCEPTION
      catch (NoSuchElementException e) {
        throw new IllegalStateException("quit never found");
      }
      // CATCHES EXCEPTIONS DURING GAME AND APPENDS ACCORDINGLY
      catch (IllegalArgumentException | IllegalStateException e) {
        quit = handleGameExceptions(model, e, view, quit);
      }
    }
    // CHECKS IF THE GAME IS OVER AND IF YOU LOST OR WON
    finalizeWinOrGameOver(model, deck, view);
  }


  /**
   * Handles a null pointer exception that might occur during the game.
   * Appends a message indicating the occurrence of the exception to the output.
   *
   * @throws IllegalStateException if there is an error during output
   */
  private void handleNullPointer() {
    try {
      ap.append("A null pointer error occurred.");
      appendN();
    } catch (IOException ioException) {
      throw new IllegalStateException("Error during output.", ioException);
    }
  }

  /**
   * Handles general game exceptions that occur during gameplay.
   * If the exception indicates a "Game quit" scenario, the game will be terminated.
   * Otherwise, it appends a relevant message to the output.
   *
   * @param model the Klondike game model
   * @param e the exception encountered during the game
   * @param view the textual view used to render the game state
   * @param quit the current state indicating if the game is quit or not
   * @return a boolean indicating if the game should be quit
   * @throws IllegalStateException if there is an error during appending to the output
   */
  private boolean handleGameExceptions(KlondikeModel model, RuntimeException e,
      KlondikeTextualView view,
      boolean quit) {
    if (e.getMessage().equals("Game quit")) {
      try {
        doQuit(model, view);
        quit = true;
      } catch (IOException x) {
        throw new IllegalStateException("error appending");
      }
    } else {
      // helpe method to handle invalid move exceptions
      handleGameBaseException(e);
    }
    return quit;
  }

  /**
   * Finalizes the game by checking if the game is over and determining if the player won or lost.
   * Renders the final state of the game and appends the appropriate message to the output.
   *
   * @param model the Klondike game model
   * @param deck the deck of cards to be used in the game
   * @param view the textual view used to render the game state
   * @throws IllegalStateException if there is an error during output
   */
  private void finalizeWinOrGameOver(KlondikeModel model, List<Card> deck,
      KlondikeTextualView view) {
    try {
      if (model.isGameOver()) {
        if (determineIfGameWon(model, deck)) {
          doYouWin(view);
        } else {
          view.render();
          doGameOver(model);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error during output.", e);
    }
  }


  /**
   * Initializes the Klondike game by checking if the model is not null and starting the game.
   *
   * @param model the Klondike game model
   * @param deck the deck of cards to be used in the game
   * @param shuffle whether the deck should be shuffled
   * @param numRows the number of rows in the game
   * @param numDraw the number of cards to draw
   * @throws IllegalStateException if the game cannot be started
   */
  private static void initializeGame(KlondikeModel model, List<Card> deck, boolean shuffle,
      int numRows,
      int numDraw) {
    // STEP 1: CHECKS IF THE MODEL IS NULL
    if (model == null) {
      throw new IllegalArgumentException("Model must not be null.");
    }

    // STEP 2: SEE IF THE GAME CAN BE STARTED AND THROW EXCEPTION IF IT CANNOT
    try {
      model.startGame(deck, shuffle, numRows, numDraw);
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new IllegalStateException("game cannot be started");
    }
  }

  /**
   * Appends a newline character to the Appendable.
   *
   * @throws IOException if there's an error appending the newline character
   */
  private void appendN() throws IOException {
    ap.append("\n");
  }

  /**
   * Appends the game over message and score to the Appendable.
   *
   * @param model the Klondike game model
   * @throws IOException if there's an error during appending
   */
  private void doGameOver(KlondikeModel model) throws IOException {
    ap.append("Game over. ")
        .append("Score: ").append(Integer.toString(model.getScore()));
    appendN();
  }

  /**
   * Appends the winning message to the Appendable.
   *
   * @param view the textual view of the Klondike game
   * @throws IOException if there's an error during appending
   */
  private void doYouWin(KlondikeTextualView view) throws IOException {
    view.render();
    ap.append("You win!");
    appendN();
  }

  /**
   * Appends the game quit message, current state of the game, and score to the Appendable.
   *
   * @param model the Klondike game model
   * @param view the textual view of the Klondike game
   * @throws IOException if there's an error during appending
   */
  private void doQuit(KlondikeModel model, KlondikeTextualView view) throws IOException {
    ap.append("Game quit!");
    appendN();
    ap.append("State of game when quit:");
    appendN();
    view.render();
    ap.append("Score: ").append(Integer.toString(model.getScore()));
    appendN();
  }

  /**
   * Handles the "Move Draw to Foundation" command by reading the foundation pile number from
   * the input and invoking the model's moveDrawToFoundation method.
   *
   * @param model the Klondike game model
   * @param scanner the scanner used to read user input
   */
  private void doMdf(KlondikeModel model, Scanner scanner) {
    int mdfFoundationPile = nextInt(scanner) - 1;
    model.moveDrawToFoundation(mdfFoundationPile);
  }

  /**
   * Handles the "Move Pile to Foundation" command by reading the pile number and foundation number
   * from the input and invoking the model's moveToFoundation method.
   *
   * @param model the Klondike game model
   * @param scanner the scanner used to read user input
   */
  private void doMpf(KlondikeModel model, Scanner scanner) {
    int pileNumber = nextInt(scanner) - 1;
    int foundationNumber = nextInt(scanner) - 1;
    model.moveToFoundation(pileNumber, foundationNumber);
  }

  /**
   * Handles the "Move Draw" command by reading the destination pile number from the input
   * and invoking the model's moveDraw method.
   *
   * @param model the Klondike game model
   * @param scanner the scanner used to read user input
   */
  private void doMd(KlondikeModel model, Scanner scanner) {
    int drawDestinationPile = nextInt(scanner) - 1;
    model.moveDraw(drawDestinationPile);
  }

  /**
   * Handles the "Move Pile to Pile" command by reading the source pile number, number of cards,
   * and destination pile number from the input and invoking the model's movePile method.
   *
   * @param model the Klondike game model
   * @param scanner the scanner used to read user input
   */
  private void doMpp(KlondikeModel model, Scanner scanner) {
    // uses nextInt helper method
    int source = nextInt(scanner) - 1;
    int numCards = nextInt(scanner);
    int destination = nextInt(scanner) - 1;
    model.movePile(source, numCards, destination);
  }

  /**
   * Determines if the game has been won based on the maximum card value, the number of aces, and
   * the current game score.
   *
   * @param model the Klondike game model
   * @param deck  the deck of cards to be used in the game
   * @return true if the game has been won, false otherwise
   */
  private boolean determineIfGameWon(KlondikeModel model, List<Card> deck) {
    int maxValue = 0;
    int numberOfAces = 0;
    for (Card c : deck) {
      int cardVal = c.getValue();
      if (cardVal > maxValue) {
        maxValue = cardVal;
      }
      if (cardVal == 1) {
        numberOfAces++;
      }
    }
    return (maxValue * numberOfAces) == model.getScore();
  }

  /**
   * Handles exceptions that occur during the game by appending a relevant message to the output.
   *
   * @param e the exception encountered during the game
   * @throws IllegalStateException if there's an error during output
   */
  private void handleGameBaseException(Exception e) {
    try {
      ap.append("Invalid move. Play again. ").append(e.getMessage());
      appendN();
    } catch (IOException x) {
      throw new IllegalStateException("Error during output.", x);
    }

  }

  /**
   * Retrieves the next integer from the scanner input, skipping any invalid tokens. If a quit
   * command ('q' or 'Q') is encountered, the game is terminated.
   *
   * @param scanner the scanner used to read user input
   * @return the next valid integer from the input
   * @throws IllegalStateException if a quit command is encountered
   */
  private int nextInt(Scanner scanner) {
    while (!scanner.hasNextInt()) {
      if (scanner.hasNext("q") || scanner.hasNext("Q")) {
        throw new IllegalStateException("Game quit");
      }
      scanner.next(); // Skip invalid token
    }
    return scanner.nextInt();
  }

}











