package cs3500.klondike;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.view.KlondikeTextualView;
import cs3500.klondike.view.TextualView;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests for the {@link LimitedDrawKlondike} implementation of the
 * {@link KlondikeModel} interface.
 * This suite of tests ensures the correct behavior of the
 * LimitedDrawKlondike, particularly focusing on
 * the draw and discard functionalities, and how they interact with the predefined redraw limit.
 */
public class TestLimitedDrawKlondike {

  private KlondikeModel limitedModel;

  private List<Card> deckCustom;

  @Before
  public void setup() {
    // should create a game w 2 redraws
    limitedModel = KlondikeCreator.create(GameType.LIMITED);

    // create a custom deck:
    deckCustom = new ArrayList<>();
    deckCustom.add(getLimitedCard("A♣"));
    deckCustom.add(getLimitedCard("2♣"));
    deckCustom.add(getLimitedCard("2♠"));
    deckCustom.add(getLimitedCard("2♢"));
    deckCustom.add(getLimitedCard("2♡"));
    deckCustom.add(getLimitedCard("A♢"));
    deckCustom.add(getLimitedCard("A♡"));
    deckCustom.add(getLimitedCard("A♠"));
  }

  /**
   * Retrieves the {@link Card} object from the LimitedDrawKlondike deck based on its string
   * representation.
   *
   * @param card The string representation of the card to be retrieved.
   * @return The {@link Card} object that matches the provided string representation.
   * @throws IllegalArgumentException if the provided card string does not match any card in the
   *                                  deck.
   */
  private Card getLimitedCard(String card) {
    List<Card> deck = limitedModel.getDeck();
    for (Card c : deck) {
      if (c.toString().equals(card)) {
        return c;
      }
    }
    throw new IllegalArgumentException("card is not in deck");
  }


  @Test(expected = IllegalStateException.class)
  public void testLimitedDiscardDraw() {
    // start the game
    limitedModel.startGame(deckCustom, false, 2, 1);
    // valid move
    for (Card c : deckCustom) {
      limitedModel.discardDraw();
      limitedModel.discardDraw();
    }
  }

  @Test
  public void testDiscardOneToManyDrawsToThrowException() {
    // start the game
    limitedModel.startGame(deckCustom, false, 2, 10);

    // after drawing the cards, there are 5 cards left in the draw pile
    for (int i = 0; i < 15; i++) {
      limitedModel.discardDraw();
    }
    assertThrows(IllegalStateException.class, () -> limitedModel.discardDraw());

  }


  @Test
  public void testViewDD() {
    // start the game
    limitedModel.startGame(deckCustom, false, 2, 10);
    // create a textual view
    KlondikeTextualView tv = new KlondikeTextualView(limitedModel);

    // should be no cards left in deck after this:
    for (int i = 0; i < 11; i++) {
      limitedModel.discardDraw();
    }

    // MAKE SURE THAT DISCARD DRAW STARTS DISCARDING CARDS PROPERLY
    assertTrue(tv.toString().contains("Draw: 2♡, A♢, A♡, A♠\n"));
    limitedModel.discardDraw();
    assertTrue(tv.toString().contains("Draw: A♢, A♡, A♠\n"));
    limitedModel.discardDraw();
    assertTrue(tv.toString().contains("Draw: A♡, A♠\n"));
    limitedModel.discardDraw();
    assertTrue(tv.toString().contains("Draw: A♠\n"));
    limitedModel.discardDraw();
    assertTrue(tv.toString().contains("Draw: \n"));
  }


  @Test
  public void testDiscardDrawOnZeroNumTimesRedrawAllowed() {
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

  @Test
  public void testLimitedDiscardsSameCards() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getLimitedCard("A♣"));

    deckCustom.add(getLimitedCard("A♣"));

    deckCustom.add(getLimitedCard("2♣"));
    deckCustom.add(getLimitedCard("2♣"));
    deckCustom.add(getLimitedCard("A♣"));
    deckCustom.add(getLimitedCard("2♣"));
    deckCustom.add(getLimitedCard("A♣"));
    deckCustom.add(getLimitedCard("2♣"));


    TextualView tv = new KlondikeTextualView(limitedModel);
    limitedModel.startGame(deckCustom, false, 1, 30);

    limitedModel.moveDrawToFoundation(0);

    // discard draw for 2 draw cards where they can be reused twice:
    for (int i = 0; i < 18; i++) {
      limitedModel.discardDraw();
    }

    assertTrue(tv.toString().contains("Draw: \n"));
  }



  @Test
  public void testLimitedDiscardDrawDrawView() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getLimitedCard("A♣"));

    deckCustom.add(getLimitedCard("A♢"));
    deckCustom.add(getLimitedCard("A♡"));
    deckCustom.add(getLimitedCard("A♠"));

    TextualView tv = new KlondikeTextualView(limitedModel);
    limitedModel.startGame(deckCustom, false, 1, 3);

    for (int i = 0; i < 9; i++) {
      limitedModel.discardDraw();
    }
    assertTrue(tv.toString().contains("Draw: \n"));

  }


  @Test
  public void testLimitedDD() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getLimitedCard("A♣"));

    deckCustom.add(getLimitedCard("A♢"));
    deckCustom.add(getLimitedCard("A♡"));
    deckCustom.add(getLimitedCard("A♠"));

    TextualView tv = new KlondikeTextualView(limitedModel);
    limitedModel.startGame(deckCustom, false, 1, 30);

    limitedModel.moveDrawToFoundation(0);

    // discard draw for 2 draw cards where they can be reused twice:
    for (int i = 0; i < 6; i++) {
      limitedModel.discardDraw();
    }

    assertTrue(tv.toString().contains("Draw: \n"));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testInvalidConstructorArgumentInLimitedDraw() {
    // create LimitedDrawKlondike with invalid constructor
    KlondikeModel limitedModel = KlondikeCreator.createKlondike(GameType.LIMITED, -1);
  }

  @Test
  public void testLimitedDD2() {
    List<Card> deckCustom = new ArrayList<>();
    deckCustom.add(getLimitedCard("A♣"));

    TextualView tv = new KlondikeTextualView(limitedModel);
    limitedModel.startGame(deckCustom, false, 1, 30);
    System.out.println(tv);
    assertTrue(tv.toString().contains("Draw: \n"));

  }




}










































