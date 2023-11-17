package cs3500.klondike;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests for the {@link KlondikeTextualController} class to ensure that the
 * controller correctly processes user input and updates the game state accordingly.
 */
public class ExamplarControllerTests {

  private KlondikeModel model;

  @Before
  public void setup() {
    model = KlondikeCreator.create(GameType.BASIC);
  }


  // chaff 8
  @Test
  public void testImmediateQuit() {
    StringReader input = new StringReader("q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(model, model.getDeck(), false, 7, 3);

    // check if in the output there is the message saying the game was quit:
    assertTrue(output.toString().contains("Game quit!"));
  }

  @Test
  public void testInvalidMoveCommand() {
    StringReader input = new StringReader("mpp 4 2 5\nq");
    StringWriter output = new StringWriter();

    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(model, model.getDeck(), false, 7, 3);

    assertTrue(output.toString().contains("Invalid move."));

  }

  /**
   * Retrieves the {@link Card} object from the deck based on its string representation.
   *
   * @param card The string representation of the card to be retrieved.
   * @return The {@link Card} object that matches the provided string representation.
   * @throws IllegalArgumentException if the provided card string does not match any card in the
   *                                  deck.
   */
  private Card getCard(String card) {
    List<Card> deck = model.getDeck();
    for (Card c : deck) {
      if (c.toString().equals(card)) {
        return c;
      }
    }
    throw new IllegalArgumentException("card is not in deck");
  }


  @Test
  public void testInvalidMoveWithInvalidInput() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input = new StringReader("mpp 0 1 1\nq");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));

    controller.playGame(model, deckCustom, false, 2, 5);

    assertTrue(output.toString().contains("Invalid move."));

    assertTrue(output.toString().contains("Game quit!"));


  }


  // chaff 1:
  @Test
  public void testValidMoveAndCheckingScoreWithQuitStatement() {
    List<Card> deckCustom = new ArrayList<>();
    StringReader input = new StringReader("mdf 1 mdf 2                      q\n");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));
    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));

    controller.playGame(model, deckCustom, false, 1, 5);

    String str = "Game quit!\nState of game when quit:\n";
    assertFalse(output.toString().contains("Invalid move."));
    assertTrue(output.toString().contains(str));

  }


  @Test
  public void testPileToPileMoveCheckScores() {
    List<Card> deckCustom = new ArrayList<>();
    StringReader input =
        new StringReader("mpp 1 1 2\nmpf 2 1\nmd 2\nmpf 2 2\n mpf 3 3\nmpf 2 1\nmpf 2 2\nq");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));

    controller.playGame(model, deckCustom, false, 3, 5);

    assertTrue(output.toString().contains("Score: 2"));

    assertTrue(output.toString().contains("Invalid move."));

    assertTrue(output.toString().contains("Score: 0"));


  }


  // chaff 2:
  @Test
  public void testInvalidInputRetry() {
    StringReader input = new StringReader("xyz 1 2 3\nmpppp 1 1 2\nq");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> deck = model.getDeck();
    controller.playGame(model, deck, false, 2, 5);

    assertTrue(output.toString().contains("Invalid move. Play again."));

  }


  // testing invalid inputs: ************************************************************
  // chaff 3:
  @Test
  public void testMoveMoreCardsThanAvailable() {
    StringReader input = new StringReader("md 1 q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getCard("A♢"));
    customDeck.add(getCard("2♢"));
    customDeck.add(getCard("3♢"));

    controller.playGame(model, customDeck, false, 2, 5);

    assertTrue(output.toString().contains("Invalid move. Play again."));
  }


  @Test
  public void testGameQuitAllMessages() {
    StringReader input = new StringReader("Q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> deck = model.getDeck();

    controller.playGame(model, deck, false, 2, 5);
    assertTrue(output.toString().contains("Game quit!"));
    assertTrue(output.toString().contains("State of game when quit:"));
    assertTrue(output.toString().contains("Foundation: <none>, <none>, <none>, <none>"));
    assertTrue(output.toString().contains("Score: 0"));
  }

  @Test
  public void testGetScoreWithMultipleOnFoundation2() {
    List<Card> deckCustom = new ArrayList<>();
    StringReader input =
        new StringReader("mdf 0 mpp 2 1 1 mpf 1 1 q");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("A♡"));

    deckCustom.add(getCard("A♣"));

    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));

    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("A♠"));

    controller.playGame(model, deckCustom, false, 2, 1);

    assertTrue(output.toString().contains("Invalid move."));
    assertTrue(output.toString().contains("Score: 1"));

  }

  // chaff 5:
  // fails bc there is no quit statement
  @Test
  public void testStartGameWithInvalidInput() {
    List<Card> deckCustom = model.getDeck();

    StringReader input =
        new StringReader("md somethingHere");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    // play the game through the controller
    assertThrows(IllegalStateException.class,
        () -> controller.playGame(model, deckCustom, false, 2, 1));


  }

  // chaff 0:
  @Test
  public void testCheckQuitErrorMessage() {
    List<Card> deckCustom = new ArrayList<>();
    StringReader input = new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));

    controller.playGame(model, deckCustom, false, 2, 1);
    assertTrue(output.toString().contains("Game quit!\n"
        + "State of game when quit:\n"
        + "Draw: A♡\n"
        + "Foundation: <none>, <none>, <none>, <none>\n"
        + " A♢  ?\n"
        + "    A♣\n"
        + "Score: 0"));
  }




  @Test
  public void testInvalidMoveWithInvalidInput2() {
    List<Card> deckCustom = new ArrayList<>();
    StringReader input = new StringReader("mdf hello how are you doing 1 \nq");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));

    controller.playGame(model, deckCustom, false, 2, 5);

    assertTrue(output.toString().contains("Score: 1"));

  }





}
