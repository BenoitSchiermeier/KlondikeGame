package cs3500.klondike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the Klondike game model to validate its behavior.
 */
public class ExamplarModelTests {

  private KlondikeModel model;

  @Before
  public void setup() {
    model = KlondikeCreator.create(GameType.BASIC);
  }


  @Test(expected = IllegalStateException.class)
  public void testMovePileFromEmptySourcePile() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    model.movePile(6, 1, 0);  // Assuming last cascade pile empty initially
  }

  @Test(expected = IllegalStateException.class)
  public void testMoveDrawFromEmptyDrawPile() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    for (int i = 0; i < deck.size(); i++) {  // try to exhaust the draw pile
      model.discardDraw();
    }
    model.moveDraw(0);  // No draw cards left to move
  }


  @Test(expected = IllegalStateException.class)
  public void testMoveNonSequentialCardToFoundation() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    model.moveToFoundation(0, 0);  // Assuming the first card of the first cascade pile is an Ace
    model.moveToFoundation(1, 0);  // Trying to move another Ace on top of the first one
  }

  @Test(expected = IllegalStateException.class)
  public void testMoveToNonFittingCascade() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    // Assuming the bottom card of pile 0 doesn't fit onto pile 1
    model.movePile(0, 1, 1);
  }

  @Test(expected = IllegalStateException.class)
  public void testMoveDrawToNonFittingFoundation() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    // Assuming the top of the draw pile doesn't fit directly onto any foundation
    model.moveDrawToFoundation(0);
  }

  @Test(expected = IllegalStateException.class)
  public void testMoveDrawToNonEmptyNonFittingFoundation() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    // Assuming the first card of the first cascade pile is an Ace
    model.moveToFoundation(0, 0);
    // Trying to move a card from the draw pile to the first foundation pile where it doesn't fit
    model.moveDrawToFoundation(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBoundaryValueFoundation() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    // Assuming there are only 4 foundation piles (0 to 3)
    model.moveToFoundation(0, 4);
  }

  @Test(expected = IllegalStateException.class)
  public void testMoveToFoundationWithoutCardInSourcePile() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    model.movePile(0, model.getPileHeight(0), 1);
    // Attempting to move a card from an empty pile
    model.moveToFoundation(0, 0);
  }

  private List<Card> sortByRank(List<Card> deck) {
    deck.sort((card1, card2) -> {
      String rank1 = card1.toString().substring(0, card1.toString().length() - 1);
      String rank2 = card2.toString().substring(0, card2.toString().length() - 1);

      // Assign values for each rank
      int rank1Value = getRankValue(rank1);
      int rank2Value = getRankValue(rank2);

      return Integer.compare(rank1Value, rank2Value);
    });
    return deck;
  }

  private int getRankValue(String rank) {
    switch (rank) {
      case "A":
        return 1;
      case "2":
        return 2;
      case "3":
        return 3;
      case "4":
        return 4;
      case "5":
        return 5;
      case "6":
        return 6;
      case "7":
        return 7;
      case "8":
        return 8;
      case "9":
        return 9;
      case "10":
        return 10;
      case "J":
        return 11;
      case "Q":
        return 12;
      case "K":
        return 13;
      default:
        throw new IllegalArgumentException("Invalid card rank: " + rank);
    }
  }


  private List<Card> customArrangement(List<Card> deck) {
    List<Card> aces = new ArrayList<>();
    List<Card> others = new ArrayList<>();
    for (Card card : deck) {
      if (card.toString().startsWith("A")) {
        aces.add(card);
      } else {
        others.add(card);
      }
    }
    aces.addAll(others);
    return aces;
  }

  // tests using these helpers:---------------------------------------------

  @Test(expected = IllegalStateException.class)
  public void testMoveToFoundationNonAce() {
    List<Card> deck = customArrangement(model.getDeck());
    // Shift all Aces to the end of the deck.
    List<Card> aces = new ArrayList<>();
    List<Card> nonAces = new ArrayList<>();
    for (Card card : deck) {
      if (card.toString().startsWith("A")) {
        aces.add(card);
      } else {
        nonAces.add(card);
      }
    }
    nonAces.addAll(aces);
    model.startGame(nonAces, false, 7, 3);
    model.moveToFoundation(0, 0);
  }

  // ----------------------------------------------------------------------

  private List<Card> arrangeAcesAtBottom(List<Card> deck) {
    List<Card> aces = new ArrayList<>();
    List<Card> others = new ArrayList<>();
    for (Card card : deck) {
      if (card.toString().startsWith("A")) {
        aces.add(card);
      } else {
        others.add(card);
      }
    }
    others.addAll(aces);
    return others;
  }

  // fails sometimes
  @Test(expected = IllegalStateException.class)
  public void testMoveCardToCascadeWithAcesAtBottom() {
    List<Card> deck = arrangeAcesAtBottom(model.getDeck());
    model.startGame(deck, false, 7, 3);
    model.movePile(6, 1, 5);
  }

  // ----------------------------------
  private List<Card> arrangeJacksInTheMiddle(List<Card> deck) {
    List<Card> jacks = new ArrayList<>();
    List<Card> firstHalf = new ArrayList<>();
    List<Card> secondHalf = new ArrayList<>();
    int splitIndex = deck.size() / 2;
    for (int i = 0; i < splitIndex; i++) {
      firstHalf.add(deck.get(i));
    }
    for (int i = splitIndex; i < deck.size(); i++) {
      if (deck.get(i).toString().startsWith("J")) {
        jacks.add(deck.get(i));
      } else {
        secondHalf.add(deck.get(i));
      }
    }
    firstHalf.addAll(jacks);
    firstHalf.addAll(secondHalf);
    return firstHalf;
  }

  // 13th chaff caught
  @Test(expected = IllegalStateException.class)
  public void testMoveJacksFromTheMiddle() {
    List<Card> deck = arrangeJacksInTheMiddle(model.getDeck());
    model.startGame(deck, false, 7, 3);
    int midPileIndex = model.getNumPiles() / 2;
    model.movePile(midPileIndex, 1, midPileIndex + 1);
  }

  // -------------------------------------------------------------------_____________________


  @Test
  public void testDiscard() {
    KlondikeModel model1;
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 9, 7);

    assertEquals(7, model.getDrawCards().size());
    assertEquals(7, model.getNumDraw());
    model.discardDraw();
    assertEquals(7, model.getDrawCards().size());
    assertEquals(7, model.getNumDraw());

  }

  // -----------------------------------------------------------------------


  @Test
  public void testAddAceCascadePileWichGivesAnException() {
    List<Card> deck = model.getDeck();
    List<Card> deckDup = deck;
    List<Card> aceDeck = new ArrayList<>();

    // takes all of the aces out of the deck to use them to start another deck
    for (Card card : deck) {
      if (card.toString().charAt(0) == 'A') {
        aceDeck.add(card);
      }
    }

    assertEquals(deckDup, deck);

    // starts the game with the ace deck
    model.startGame(aceDeck, false, 2, 1);
    model.moveToFoundation(0, 1);
    assertThrows(IllegalStateException.class, () -> model.moveDraw(0));
  }

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
  public void testPileMove() {
    List<Card> deckCustom = new ArrayList<>();

    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));

    model.startGame(deckCustom, false, 2, 5);
    model.movePile(0, 1, 1);
    assertEquals(0, model.getPileHeight(0));
    assertEquals(0, model.getPileHeight(0));

  }


}



























