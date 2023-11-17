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
import cs3500.klondike.view.KlondikeTextualView;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Represents a set of unit tests for the KlondikeController.
 * This class aims to thoroughly test the functionality and behavior
 * of the KlondikeController to ensure its proper operation.
 */
public class TestKlondikeController {

  private KlondikeModel model;

  @Before
  public void setup() {
    model = KlondikeCreator.create(GameType.BASIC);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullReadable() {
    new KlondikeTextualController(null, new StringBuilder());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullAppendable() {
    new KlondikeTextualController(new StringReader(""), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithBothNull() {
    new KlondikeTextualController(null, null);
  }

  @Test(expected = RuntimeException.class)
  public void testPlayGameExpectException() {
    KlondikeTextualController controller =
        new KlondikeTextualController(new StringReader(""), new StringBuilder());
    List<Card> deck = model.getDeck();

    controller.playGame(model, deck, true, 7, 3);
  }

  @Test
  public void testQuitDuringCommand() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input = new StringReader("mdf q 1");
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

    assertTrue(output.toString().contains("Game quit!"));
    assertFalse(output.toString().contains("Score: 1"));

  }


  @Test
  public void testPlayGameWithNullModel() {
    KlondikeTextualController controller =
        new KlondikeTextualController(new StringReader(""), new StringWriter());

    assertThrows(IllegalArgumentException.class, () ->
        controller.playGame(null, model.getDeck(), false, 7, 3));
  }


  /**
   * Retrieves a {@code Card} object based on its string representation from the deck.
   *
   * @param card the string representation of the card to be retrieved (e.g., "A♢" for Ace of
   *             Diamonds).
   * @return the {@code Card} object that matches the provided string representation.
   * @throws IllegalArgumentException if the specified card string is not found in the deck.
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
  public void testMultipleCardInvalidMoves() {
    StringReader input = new StringReader("mpp 1 2 2\nq");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getCard("A♢"));
    customDeck.add(getCard("2♢"));
    customDeck.add(getCard("3♢"));
    customDeck.add(getCard("4♢"));
    controller.playGame(model, customDeck, false, 2, 5);
    assertTrue(output.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testQuitScenario() {
    StringReader input = new StringReader("q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(model, model.getDeck(), false, 7, 3);
    assertTrue(output.toString().contains("State of game when quit:"));
  }

  @Test
  public void testMoveDrawError() {
    StringReader input = new StringReader("md 1 q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> deck = model.getDeck();
    controller.playGame(model, deck, false, 2, 5);

    assertTrue(output.toString().contains("Invalid move"));
  }

  @Test
  public void testMovePileInvalidInput() {
    StringReader input = new StringReader("mpp 1 1 4 q");
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
  public void testInvalidInput() {
    StringReader input = new StringReader("mpf 1 2 q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getCard("A♢"));
    customDeck.add(getCard("2♢"));
    customDeck.add(getCard("3♢"));

    // THERE SHOULD ONLY BE ONE FOUNDATION PILE BECAUSE THERE IS ONE ACE
    controller.playGame(model, customDeck, false, 2, 5);
    assertTrue(output.toString().contains("Invalid move. Play again."));
  }


  @Test
  public void testControllerMoveEmptyDrawToFoundation() {
    StringReader input = new StringReader("mdf 1 q");
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
  public void testControllerDiscardAnEmptyDraw() {
    StringReader input = new StringReader("dd q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getCard("A♢"));
    customDeck.add(getCard("2♢"));
    customDeck.add(getCard("3♢"));

    controller.playGame(model, customDeck, false, 2, 5);
    assertTrue(output.toString().contains("Invalid move. Play again."));
  }

  @Test(expected = IllegalStateException.class)
  public void testDuplicateCardDeck() {
    StringReader input = new StringReader("q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);

    List<Card> deck = model.getDeck();
    deck.remove(0);

    controller.playGame(model, deck, false, 7, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerNullModelException() {
    StringReader input = new StringReader("q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);

    List<Card> deck = model.getDeck();

    controller.playGame(null, deck, false, 7, 3);
  }

  @Test
  public void testControllerWinState() {
    StringReader input = new StringReader("mpf 1 1 mpf 2 1 mpf 2 1 q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getCard("A♢"));
    customDeck.add(getCard("3♢"));
    customDeck.add(getCard("2♢"));

    controller.playGame(model, customDeck, false, 2, 5);
    assertTrue(output.toString().contains("You win!"));
    assertFalse(output.toString().contains("Game quit!"));

  }

  @Test
  public void testControllerGameOverState() {
    StringReader input = new StringReader("mpf 1 1");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getCard("A♢"));
    customDeck.add(getCard("2♢"));
    customDeck.add(getCard("3♢"));

    controller.playGame(model, customDeck, false, 2, 5);
    assertTrue(output.toString().contains("Game over."));
  }


  @Test
  public void testGameOverAtStartDoesNotContainQuitStatement() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input =
        new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("3♣"));

    controller.playGame(model, deckCustom, false, 2, 1);

    assertFalse(output.toString().contains("Invalid move."));
    assertFalse(output.toString().contains("Game quit!"));

    assertTrue(output.toString().contains("Game over. Score: 0"));

  }

  @Test(expected = IllegalStateException.class)
  public void testControllerNoQuitInInput() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input =
        new StringReader("mdf");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("A♡"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("A♠"));

    controller.playGame(model, deckCustom, false, 1, 1);
  }


  @Test(expected = IllegalStateException.class)
  public void testStartWithEmptyDeckOnController() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input = new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(model, deckCustom, false, 1, 1);

    assertTrue(output.toString().contains("Invalid move."));
    assertTrue(output.toString().contains("Score: 0"));

  }

  @Test
  public void testGetScoreWithMultipleOnFoundation2() throws IOException {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input =
        new StringReader("mdf 1 mpp 2 1 1 dd mdf 2 q");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);
    KlondikeTextualView k = new KlondikeTextualView(model, output);

    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("A♡"));

    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("2♠"));

    controller.playGame(model, deckCustom, false, 2, 1);

    assertFalse(output.toString().contains("Invalid move."));
    assertTrue(output.toString().contains("Score: 1"));
    assertTrue(output.toString().contains("Score: 2"));
    assertTrue(output.toString().contains("Game quit!"));
    String str = "Draw: A♣\n"
        + "Foundation: <none>, <none>, <none>, <none>\n"
        + " 2♣  ?\n"
        + "    A♡\n"
        + "Score: 0\n"
        + "Draw: 2♢\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + " 2♣  ?\n"
        + "    A♡\n"
        + "Score: 1\n"
        + "Draw: 2♢\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + " 2♣ A♢\n"
        + " A♡   \n"
        + "Score: 1\n"
        + "Draw: A♠\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + " 2♣ A♢\n"
        + " A♡   \n"
        + "Score: 1\n"
        + "Draw: 2♡\n"
        + "Foundation: A♣, A♠, <none>, <none>\n"
        + " 2♣ A♢\n"
        + " A♡   \n"
        + "Score: 2\n"
        + "Game quit!\n"
        + "State of game when quit:\n"
        + "Draw: 2♡\n"
        + "Foundation: A♣, A♠, <none>, <none>\n"
        + " 2♣ A♢\n"
        + " A♡   \n"
        + "Score: 2\n";
    assertTrue(output.toString().contains(str));
  }

  @Test
  public void testGameQuitInMiddleOfMove() throws IOException {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input =
        new StringReader("mdf 1 mpp 2 q 1 1");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("A♡"));

    deckCustom.add(getCard("A♣"));

    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("A♠"));

    deckCustom.add(getCard("2♡"));

    deckCustom.add(getCard("2♠"));

    controller.playGame(model, deckCustom, false, 2, 1);
    String str = "Draw: A♣\n"
        + "Foundation: <none>, <none>, <none>, <none>\n"
        + " 2♣  ?\n"
        + "    A♡\n"
        + "Score: 0\n"
        + "Draw: 2♢\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + " 2♣  ?\n"
        + "    A♡\n"
        + "Score: 1\n"
        + "Game quit!\n"
        + "State of game when quit:\n"
        + "Draw: 2♢\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + " 2♣  ?\n"
        + "    A♡\n"
        + "Score: 1\n";

    assertTrue(output.toString().contains(str));

  }

  @Test
  public void testCompleteGameWithInvalidInput() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input =
        new StringReader("invalidInput mpf 1 1 q");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getCard("A♢"));

    controller.playGame(model, deckCustom, false, 1, 1);
    Assert.assertTrue(output.toString().contains("You win!"));
  }






}
