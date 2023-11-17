package cs3500.klondike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.model.hw04.WhiteheadKlondike;
import cs3500.klondike.view.KlondikeTextualView;
import cs3500.klondike.view.TextualView;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests for the {@link WhiteheadKlondike} implementation
 * of the {@link KlondikeModel} interface.
 * This suite of tests ensures the correct behavior of the
 * WhiteheadKlondike variant of the Klondike game.
 */
public class TestWhiteHeadKlondike {
  private KlondikeModel whiteModel;
  private List<Card> deckCustom;

  @Before
  public void setup() {
    whiteModel = KlondikeCreator.create(GameType.WHITEHEAD);

    deckCustom = new ArrayList<>();
    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♠"));
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("A♡"));

  }


  /**
   * Retrieves the {@link Card} object from the LimitedDrawKlondike
   * deck based on its string representation.
   *
   * @param card The string representation of the card to be retrieved.
   * @return The {@link Card} object that matches the provided string representation.
   * @throws IllegalArgumentException if the provided card string does not match any card in the
   *                                  deck.
   */
  private Card getWhiteHeadCard(String card) {
    List<Card> deck = whiteModel.getDeck();
    for (Card c : deck) {
      if (c.toString().equals(card)) {
        return c;
      }
    }
    throw new IllegalArgumentException("card is not in deck");
  }




  @Test (expected = IllegalArgumentException.class)
  public void testStartGameWithInvalidDeck() {
    List<Card> deckCustom = new ArrayList<>();

    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♠"));
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("A♡"));

    whiteModel.startGame(deckCustom, false, 2, 1);
  }

  @Test
  public void testMoveCardFromEmptySourcePile() {

    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♠"));
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("A♡"));

    whiteModel.startGame(deckCustom, false, 2, 3);
    whiteModel.moveToFoundation(0, 0);

    // this move is not allowable:
    assertThrows(IllegalStateException.class,
        () -> whiteModel.moveToFoundation(0, 0));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInvalidNumberOfCascadePiles() {
    List<Card> deck = whiteModel.getDeck();
    whiteModel.startGame(deck, false, 20, 3);
  }

  @Test
  public void testValidMoveCardsPileToPile() {
    List<Card> deckCustom = new ArrayList<>();

    deckCustom.add(getWhiteHeadCard("3♣"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("A♣"));

    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("A♡"));

    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("A♠"));

    deckCustom.add(getWhiteHeadCard("3♠"));
    deckCustom.add(getWhiteHeadCard("3♡"));
    deckCustom.add(getWhiteHeadCard("3♢"));




    whiteModel.startGame(deckCustom, false, 2, 1);
    // valid move:
    whiteModel.movePile(1, 2, 0);
    assertEquals(whiteModel.getPileHeight(0), 3);
  }

  @Test
  public void testAllCardsVisible() {
    StringBuilder sb = new StringBuilder();
    TextualView tv = new KlondikeTextualView(whiteModel, sb);


    whiteModel.startGame(deckCustom, false, 2, 1);

    assertTrue(tv.toString().contains("2♣"));
  }



  @Test
  public void testQuitDuringCommand() {
    List<Card> deckCustom = new ArrayList<>();

    StringReader input = new StringReader("mdf q 1");
    Appendable output = new StringBuilder();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("A♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("A♡"));

    controller.playGame(whiteModel, deckCustom, false, 2, 5);

    assertTrue(output.toString().contains("Game quit!"));
    assertFalse(output.toString().contains("Score: 1"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMultipleCardInvalidMoves() {
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getWhiteHeadCard("A♢"));
    customDeck.add(getWhiteHeadCard("2♢"));
    customDeck.add(getWhiteHeadCard("3♢"));
    customDeck.add(getWhiteHeadCard("4♢"));
    whiteModel.startGame(customDeck, false, 2, 5);
    whiteModel.movePile(0, 2, 1);
  }

  @Test
  public void testMultipleCardValidMoves() {
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getWhiteHeadCard("A♢"));
    customDeck.add(getWhiteHeadCard("3♢"));
    customDeck.add(getWhiteHeadCard("2♢"));
    customDeck.add(getWhiteHeadCard("4♢"));
    whiteModel.startGame(customDeck, false, 2, 5);
    whiteModel.movePile(0,1, 1);
    whiteModel.movePile(1, 3, 0);
    assertEquals(0, whiteModel.getPileHeight(1));
  }


  @Test
  public void testMultipleCardInvalidMovesWithController() {
    StringReader input = new StringReader("mpp 1 2 2\nq");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(getWhiteHeadCard("A♢"));
    customDeck.add(getWhiteHeadCard("2♢"));
    customDeck.add(getWhiteHeadCard("3♢"));
    customDeck.add(getWhiteHeadCard("4♢"));
    controller.playGame(whiteModel, customDeck, false, 2, 5);
    assertTrue(output.toString().contains("Invalid move. Play again."));
  }


  @Test
  public void testQuitScenarioHas2Boards() {
    StringReader input = new StringReader("mpf 1 1 q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    controller.playGame(whiteModel, whiteModel.getDeck(), false, 7, 3);
    assertTrue(output.toString().contains("State of game when quit:"));
    assertTrue(output.toString().contains("Draw: 3♡, 4♡, 5♡\n"
        + "Foundation: <none>, <none>, <none>, <none>\n"
        + " A♣ 2♣ 3♣ 4♣ 5♣ 6♣ 7♣\n"
        + "    8♣ 9♣10♣ J♣ Q♣ K♣\n"
        + "       A♢ 2♢ 3♢ 4♢ 5♢\n"
        + "          6♢ 7♢ 8♢ 9♢\n"
        + "            10♢ J♢ Q♢\n"
        + "                K♢ A♡\n"
        + "                   2♡\n"
        + "Score: 0\n"
        + "Draw: 3♡, 4♡, 5♡\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + "  X 2♣ 3♣ 4♣ 5♣ 6♣ 7♣\n"
        + "    8♣ 9♣10♣ J♣ Q♣ K♣\n"
        + "       A♢ 2♢ 3♢ 4♢ 5♢\n"
        + "          6♢ 7♢ 8♢ 9♢\n"
        + "            10♢ J♢ Q♢\n"
        + "                K♢ A♡\n"
        + "                   2♡\n"
        + "Score: 1\n"
        + "Game quit!\n"
        + "State of game when quit:\n"
        + "Draw: 3♡, 4♡, 5♡\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + "  X 2♣ 3♣ 4♣ 5♣ 6♣ 7♣\n"
        + "    8♣ 9♣10♣ J♣ Q♣ K♣\n"
        + "       A♢ 2♢ 3♢ 4♢ 5♢\n"
        + "          6♢ 7♢ 8♢ 9♢\n"
        + "            10♢ J♢ Q♢\n"
        + "                K♢ A♡\n"
        + "                   2♡\n"
        + "Score: 1\n"));

  }

  @Test
  public void testValidMoveOntoEmptyPile() {
    StringReader input = new StringReader("mpf 1 1 md 1 q");
    StringWriter output = new StringWriter();
    KlondikeController controller = new KlondikeTextualController(input, output);
    List<Card> deck = whiteModel.getDeck();
    controller.playGame(whiteModel, deck, false, 2, 5);

    assertFalse(output.toString().contains("Invalid move"));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithTooManyCascadePiles() {
    List<Card> deck = whiteModel.getDeck();  // Standard deck
    whiteModel.startGame(deck, false, 10, 1);  // Should throw exception
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithZeroCascadePiles() {
    List<Card> deck = whiteModel.getDeck();  // Standard deck
    whiteModel.startGame(deck, false, -1, 1);  // Should throw exception
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithNegativeCascadePiles() {
    List<Card> deck = whiteModel.getDeck();  // Standard deck
    whiteModel.startGame(deck, false, -1, 1);  // Should throw exception
  }


  @Test(expected = IllegalArgumentException.class)
  public void testValidMoveChangesBoard() {
    List<Card> deck = whiteModel.getDeck();  // Standard deck
    whiteModel.startGame(deck, false, -1, 1);  // Should throw exception
  }
}





















