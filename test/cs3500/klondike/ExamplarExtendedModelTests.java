package cs3500.klondike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.view.KlondikeTextualView;
import cs3500.klondike.view.TextualView;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests for the extended Klondike models, specifically focusing on the WhiteheadKlondike
 * and LimitedDrawKlondike game variations. This suite of tests evaluates the different behaviors
 * and rules of these extended models, ensuring they adhere to the expected game mechanics.
 */
public class ExamplarExtendedModelTests {

  private KlondikeModel limitedModel;
  private KlondikeModel whiteModel;
  private List<Card> deckCustom;



  @Before
  public void setup() {
    // creates a game with 2 draws
    limitedModel = KlondikeCreator.create(GameType.LIMITED);
    whiteModel = KlondikeCreator.create(GameType.WHITEHEAD);

    // provides a controlled deck:
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


  // chaff 5
  @Test
  public void testValidMoveCardToFoundation() {
    whiteModel.startGame(deckCustom, false, 2, 1);
    whiteModel.moveToFoundation(0, 0);
    assertEquals(whiteModel.getScore(), 1);
  }

  // chaff 0,3,5
  @Test
  public void testPileMove() {
    whiteModel.startGame(deckCustom, false, 2, 5);
    // Illegal move
    assertThrows(IllegalStateException.class, ()
        -> whiteModel.movePile(0, 1, 1));
  }


  // not necessary
  @Test
  public void testValidMovePile() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getWhiteHeadCard("A♠"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("A♡"));



    whiteModel.startGame(deckCustom, false, 2, 1);
    // valid move
    whiteModel.movePile(0, 1, 1);
    // gets the pile height at the second cascade pile
    assertEquals(whiteModel.getPileHeight(1), 3);
    TextualView tv = new KlondikeTextualView(whiteModel);
    System.out.println(tv);
  }



  @Test
  public void testValidMoveManyCardsToEmptyPile() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getWhiteHeadCard("A♠"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("A♡"));



    whiteModel.startGame(deckCustom, false, 2, 1);
    // valid move
    whiteModel.movePile(0, 1, 1);
    // gets the pile height at the second cascade pile
    assertEquals(whiteModel.getPileHeight(1), 3);

    whiteModel.movePile(1, 2, 0);

    assertEquals(whiteModel.getPileHeight(0), 2);

  }


  @Test
  public void testTwoBlackNotSameSuitMovedToEmptyPile() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getWhiteHeadCard("A♣"));
    deckCustom.add(getWhiteHeadCard("2♣"));
    deckCustom.add(getWhiteHeadCard("2♠"));
    deckCustom.add(getWhiteHeadCard("2♢"));
    deckCustom.add(getWhiteHeadCard("2♡"));
    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("A♡"));
    deckCustom.add(getWhiteHeadCard("A♠"));

    whiteModel.startGame(deckCustom, false, 2, 1);
    // valid move
    whiteModel.movePile(0, 1, 1);

    // invalid move, two cards are not of the same suit
    assertThrows(IllegalStateException.class,
        () -> whiteModel.movePile(1, 2, 0));
  }




  // chaff 1
  @Test
  public void testLimitedDiscardDrawDrawView() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getWhiteHeadCard("A♣"));

    deckCustom.add(getWhiteHeadCard("A♢"));
    deckCustom.add(getWhiteHeadCard("A♡"));
    deckCustom.add(getWhiteHeadCard("A♠"));

    TextualView tv = new KlondikeTextualView(limitedModel);
    limitedModel.startGame(deckCustom, false, 1, 3);

    for (int i = 0; i < 9; i++) {
      limitedModel.discardDraw();
    }
    assertTrue(tv.toString().contains("Draw: \n"));

  }

  @Test
  public void test() {
    KlondikeModel limitedModel =
        KlondikeCreator.createKlondike(GameType.LIMITED, 0);

    // start the game
    limitedModel.startGame(deckCustom, false, 3, 10);
    KlondikeTextualView tv = new KlondikeTextualView(limitedModel);

    limitedModel.discardDraw();
    limitedModel.discardDraw();
    // asserts that the draw pile is empty:
    assertTrue(limitedModel.getDrawCards().isEmpty());
  }



}





















