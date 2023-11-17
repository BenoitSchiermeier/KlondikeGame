package cs3500.klondike;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.CardImpl;
import cs3500.klondike.model.hw02.CardImpl.Suit;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.view.KlondikeTextualView;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import cs3500.klondike.model.hw02.KlondikeModel;


/**
 * Contains tests for the BasicKlondike model. This includes validating normal gameplay, checking
 * boundary cases, and ensuring that the model throws exceptions where expected.
 */
public class BasicKlondikeTests {

  private KlondikeModel model;
  private List<Card> deck;


  /**
   * Setup the test environment before each test.
   */

  @Before
  public void setup() {
    model = KlondikeCreator.create(GameType.BASIC);
    deck = model.getDeck();
  }


  @Test
  public void testMovePileThenGetCardAt() {
    Appendable appendable = new StringBuilder();
    model.startGame(deck, false, 7, 6);
    model.moveToFoundation(0, 0);
    assertEquals(null, model.getCardAt(0,0));
    model.movePile(5, 1, 0);
    model.discardDraw();
    assertEquals("J♢", model.getCardAt(5, 4).toString());
    Assert.assertThrows(
        IllegalArgumentException.class, () -> model.getCardAt(4, 3).toString());
    assertEquals("Draw: 4♡, 5♡, 6♡, 7♡, 8♡, 9♡\n"
        + "Foundation: A♣, <none>, <none>, <none>\n"
        + " K♢  ?  ?  ?  ?  ?  ?\n"
        + "    8♣  ?  ?  ?  ?  ?\n"
        + "       A♢  ?  ?  ?  ?\n"
        + "          6♢  ?  ?  ?\n"
        + "            10♢ J♢  ?\n"
        + "                    ?\n"
        + "                   2♡\n", new KlondikeTextualView(model, appendable).toString());

    assertEquals("A♣", model.getCardAt(0).toString());
    Assert.assertThrows(IllegalArgumentException.class, () -> model.getCardAt(4));
    Assert.assertThrows(IllegalArgumentException.class, () -> model.getCardAt(5));

    assertThrows(IllegalArgumentException.class,
        () -> model.getCardAt(4, 0).toString());
    assertEquals("2♡", model.getCardAt(6, 6).toString());
    assertThrows(IllegalArgumentException.class,
        () -> model.getCardAt(2, 1).toString());



  }


  @Test
  public void testRunOfHeartsAndSpades() {
    List<Card> deck = new ArrayList<>();

    // Add a run of hearts (assuming 1-13 represents Ace to King)
    for (int i = 1; i <= 13; i++) {
      deck.add(new CardImpl(i, Suit.HEARTS));
    }


    // Add a run of hearts (assuming 1-13 represents Ace to King)
    for (int i = 1; i <= 13; i++) {
      deck.add(new CardImpl(i, Suit.HEARTS));
    }


    // Add a run of spades (assuming 1-13 represents Ace to King)
    for (int i = 1; i <= 13; i++) {
      deck.add(new CardImpl(i, Suit.SPADES));
    }

    model.startGame(deck, false, 5, 5);
    assertEquals(5, model.getNumDraw());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRun() {
    // Creating a full run of Hearts
    List<CardImpl> heartsRun = new ArrayList<>();
    for (int value = 1; value <= 13; value++) {
      heartsRun.add(new CardImpl(value, CardImpl.Suit.HEARTS));
    }

    // Creating an invalid run of Spades
    List<Card> spadesRun = new ArrayList<>();
    spadesRun.add(new CardImpl(1, CardImpl.Suit.SPADES));
    spadesRun.add(new CardImpl(2, CardImpl.Suit.SPADES));
    spadesRun.add(new CardImpl(2, CardImpl.Suit.SPADES));  // Duplicate 2 of Spades
    for (int value = 4; value <= 13; value++) {
      spadesRun.add(new CardImpl(value, CardImpl.Suit.SPADES));
    }

    // Merging both runs to create the deck
    List<Card> deck = new ArrayList<>(heartsRun);
    deck.addAll(spadesRun);

    // Now, we will pass this deck to the startGame function and expect it to throw an error
    model.startGame(deck, false, 8, 3);
  }


  private List<Card> createTripleHeartSpadeDeck() {
    List<Card> deck = new ArrayList<>();
    for (int j = 0; j < 3; j++) { // Repeat three times for three sets
      for (int i = 1; i <= 13; i++) {
        deck.add(new CardImpl(i, CardImpl.Suit.HEARTS));
        deck.add(new CardImpl(i, CardImpl.Suit.SPADES));
      }
    }
    return deck;
  }

  private List<Card> createInvalidDeckAllClubsAceToFiveDiamonds() {
    List<Card> deck = new ArrayList<>();
    for (int i = 1; i <= 13; i++) {
      deck.add(new CardImpl(i, CardImpl.Suit.CLUBS));
    }
    for (int i = 1; i <= 5; i++) {
      deck.add(new CardImpl(i, CardImpl.Suit.DIAMONDS));
    }
    return deck;
  }

  private List<Card> createEachSuitAceToFiveDeck() {
    List<Card> deck = new ArrayList<>();
    for (CardImpl.Suit suit : CardImpl.Suit.values()) {
      for (int i = 1; i <= 5; i++) {
        deck.add(new CardImpl(i, suit));
      }
    }
    return deck;
  }


  // --------------------------

  @Test
  public void testStartGameWithThreeSetsHeartsSpades() {
    List<Card> deck = createTripleHeartSpadeDeck();
    Card c = deck.get(0);
    model.startGame(deck, false, 7, 1);  // Should not throw exception
    assertEquals(c, model.getCardAt(0,0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithAllClubsAndAceToFiveDiamonds() {
    List<Card> deck = createInvalidDeckAllClubsAceToFiveDiamonds();
    model.startGame(deck, false, 7, 1);  // Should throw exception
  }


  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithTooManyCascadePiles() {
    List<Card> deck = model.getDeck();  // Standard deck
    model.startGame(deck, false, 10, 1);  // Should throw exception
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithZeroCascadePiles() {
    List<Card> deck = model.getDeck();  // Standard deck
    model.startGame(deck, false, 0, 1);  // Should throw exception
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithNegativeCascadePiles() {
    List<Card> deck = model.getDeck();  // Standard deck
    model.startGame(deck, false, -1, 1);  // Should throw exception
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithInsufficientCards() {
    List<Card> deck = createEachSuitAceToFiveDeck();
    model.startGame(deck, false, 7, 1);
  }

  @Test
  public void testGetDeck() {
    assertEquals(52, deck.size());
    // Further tests can check for uniqueness of cards, correct number of each suit, etc.
  }


  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDeck() {
    List<Card> invalidDeck = null;
    model.startGame(invalidDeck, false, 7, 3);
  }

  @Test
  public void testStartGame() {
    model.startGame(deck, false, 7, 3);
    assertEquals(7, model.getNumPiles());
    assertEquals(3, model.getNumDraw());
    // Further tests can check the initial setup of piles, draw cards, etc.
  }

  @Test(expected = IllegalStateException.class)
  public void testMoveBeforeGameStart() {
    model.movePile(0, 1, 1);
  }

  //-----------------------------------------------------------------------------------

  @Test(expected = IllegalArgumentException.class)
  public void testMoveSamePile() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, 1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveNegativeSourcePile() {
    model.startGame(deck, false, 7, 3);
    model.movePile(-1, 1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveNegativeDestPile() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, 1, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveTooManyCards() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, 5, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNonexistentPile() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, 1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMovePile() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, 5, 1);  // Example of an invalid move
    model.movePile(-1, 1, 1);
    model.movePile(0, 1, -1);
    model.movePile(0, -1, 3);
    model.movePile(0, 1, 20);
  }


  @Test
  public void testIsGameOver() {
    model.startGame(deck, false, 7, 3);
    assertFalse(model.isGameOver());
    // Further tests can setup scenarios where game is over and check the method
  }

  @Test
  public void testGetScore() {
    model.startGame(deck, false, 7, 3);
    // Assuming a specific deck order and no moves made
    assertEquals(0, model.getScore());
    // Further tests can simulate moves and check the score
  }

  // ------------more chaff tries------------------------
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameNullDeck() {
    model.startGame(null, false, 7, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidDeckSize() {
    List<Card> shortDeck = new ArrayList<>();  // assume this list has less than 52 cards
    model.startGame(shortDeck, false, 7, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidPileNum() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 0, 3);  // 0 piles is invalid
  }

  @Test(expected = IllegalStateException.class)
  public void testStartGameAlreadyStarted() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    model.startGame(deck, false, 7, 3);  // Game has already started
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveDrawInvalidPile() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    model.moveDraw(8);  // Assuming you have only 7 cascade piles
  }

  @Test
  public void testMoveDrawNoDrawCards() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    int drawPile = model.getDrawCards().size();
    for (int i = 0; i < 10000; i++) {  // try to exhaust the draw pile
      model.discardDraw();
    }
    assertEquals(drawPile, model.getDrawCards().size());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMoveToFoundationInvalidSourcePile() {
    model.startGame(deck, false, 7, 3);
    model.moveToFoundation(8, 0);  // Assuming you have only 7 cascade piles
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveToFoundationInvalidFoundationPile() {
    model.startGame(deck, false, 7, 3);
    model.moveToFoundation(0, 8);  // Assuming you have only 4 foundation piles
  }


  @Test(expected = IllegalStateException.class)
  public void testMoveDrawToFoundationWithoutStartingGame() {
    model.moveDrawToFoundation(0);  // Game has not started yet
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveDrawToFoundationInvalidFoundationPile() {
    model.startGame(deck, false, 7, 3);
    model.moveDrawToFoundation(5);  // Assuming you have only 4 foundation piles
  }

  @Test(expected = IllegalStateException.class)
  public void testDiscardDrawWithoutStartingGame() {
    model.discardDraw();  // Game has not started yet
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithInvalidDeck() {
    model.startGame(new ArrayList<>(), false, 7, 3);

    model.discardDraw();  // No cards left to discard

  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumRowsWithoutStartingGame() {
    model.getNumRows();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumPilesWithoutStartingGame() {
    model.getNumPiles();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumDrawWithoutStartingGame() {
    model.getNumDraw();
  }

  @Test(expected = IllegalStateException.class)
  public void testIsCardVisibleWithoutStartingGame() {
    model.isCardVisible(0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsCardVisibleInvalidPile() {
    model.startGame(deck, false, 7, 3);
    model.isCardVisible(8, 0);  // Assuming you have only 7 cascade piles
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsCardVisibleInvalidCard() {
    model.startGame(deck, false, 7, 3);
    model.isCardVisible(0, 50);  // Invalid card index
  }

  @Test(expected = IllegalStateException.class)
  public void testGetCardAtWithoutStartingGame() {
    model.getCardAt(0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidPile() {
    model.startGame(deck, false, 7, 3);
    model.getCardAt(8, 0);  // Assuming you have only 7 cascade piles
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidCard() {
    model.startGame(deck, false, 7, 3);
    model.getCardAt(0, 50);  // Invalid card index
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumFoundationsWithoutStartingGame() {
    model.getNumFoundations();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveToFoundationFromEmptyPileToInvalidFoundation() {
    model.startGame(deck, false, 7, 3);
    model.moveToFoundation(6,
        5);  // Assuming the last cascade pile is empty and there are only 4 foundation piles
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBoundaryValuePile() {
    model.startGame(deck, false, 7, 3);
    model.movePile(7, 1, 6);  // Assuming there are only 7 cascade piles (0 to 6)
  }


  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidNum() {
    model.startGame(deck, false, 7, 3);
    model.getCardAt(0,
        model.getPileHeight(0) - 2);  // Trying to get a card that should be face down
  }

  // ****************************************************************************************
  @Test
  public void testDeckIsShuffled() {
    List<Card> originalDeck = new ArrayList<>(deck);
    model.startGame(deck, true, 7, 3);
    assertNotEquals(originalDeck, model.getDeck());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMoveToFoundationNonAceCard() {
    model.startGame(deck, false, 7, 3);
    model.movePile(6, 6, 0);  // Assuming this move will make a non-ace card the top of pile 0
    model.moveToFoundation(0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMoveToFoundationOutOfSequence() {
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(new CardImpl(2, Suit.HEARTS));
    customDeck.add(new CardImpl(1, Suit.HEARTS));
    // Fill in the rest of the deck as needed
    model.startGame(customDeck, false, 7, 3);
    model.moveToFoundation(0, 0);
    model.moveToFoundation(1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCascadeBuild() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, 6, 1);  // Assuming this move will result in an invalid build in cascade
  }


  @Test
  public void testDeckIntegrity() {
    assertEquals(52, deck.size());
    int[] suits = new int[4];
    for (Card card : deck) {
      suits[card.getSuit().ordinal()]++;
    }
    for (int count : suits) {
      assertEquals(13, count);
    }
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMoveToFoundationEmptyCascadePile() {
    model.startGame(deck, false, 7, 3);
    for (int i = 0; i < 7; i++) {  // Assuming we have 7 piles
      model.movePile(i, 0, i + 1);  // Move all cards from each pile to the next
    }
    model.moveToFoundation(0, 0);  // Now, pile 0 should be empty
  }



  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardMoveBetweenCascadePiles() {
    List<Card> customDeck = new ArrayList<>();
    customDeck.add(new CardImpl(7, Suit.HEARTS));
    customDeck.add(new CardImpl(6, Suit.DIAMONDS));
    // Add more cards to fill the deck

    model.startGame(customDeck, false, 7, 3);
    model.movePile(0, 1, 1);  // Invalid move due to same color cards
  }


  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMoveHiddenCard() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, model.getPileHeight(0) - 2, 1);  // Trying to move a hidden card
  }


  @Test
  public void testModelDeckImmutability() {
    List<Card> externalDeck = new ArrayList<>(model.getDeck());
    externalDeck.clear();
    assertNotEquals(0, model.getDeck().size());
  }


  @Test
  public void testIncompleteGameScenario() {
    model.startGame(deck, false, 7, 3);
    assertFalse(model.isGameOver());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMoveHiddenCard() {
    model.startGame(deck, false, 7, 3);
    model.movePile(0, model.getPileHeight(0) - 2, 1);
  }



  @Test
  public void testGetDeck2() {
    List<Card> deck = model.getDeck();
    assertEquals(52, deck.size());
    // Check that each card in the deck is unique
    for (int i = 0; i < deck.size(); i++) {
      for (int j = i + 1; j < deck.size(); j++) {
        assertNotEquals(deck.get(i), deck.get(j));
      }
    }
  }


  @Test
  public void testStartGame2() {
    List<Card> deck = model.getDeck();
    // Start a valid game
    model.startGame(deck, false, 7, 3);
    assertEquals(7, model.getNumPiles());

    // Start game with an already started game
    try {
      model.startGame(deck, false, 7, 3);
      fail("Expected an IllegalStateException");
    } catch (IllegalStateException e) {
      // Expected
    }
  }

  @Test
  public void testGetNumRows() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    assertEquals(7, model.getNumRows());
  }

  @Test
  public void testGetNumDraw() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    assertEquals(3, model.getNumDraw());
  }

  @Test
  public void testIsGameOver2() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    // Assuming a general game setup, the game is not over at the start.
    assertFalse(model.isGameOver());
  }

  @Test
  public void testGetScore2() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    // The score might vary based on the initial setup, so we'll just check it's not negative.
    assertTrue(model.getScore() >= 0);
  }

  @Test
  public void testGetPileHeight() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    assertEquals(1, model.getPileHeight(0));
    assertEquals(2, model.getPileHeight(1));
  }


  @Test
  public void testGetCardAt() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    assertNotNull(model.getCardAt(0, 0));
  }

  @Test
  public void testGetDrawCards() {
    List<Card> deck = model.getDeck();
    model.startGame(deck, false, 7, 3);
    assertEquals(3, model.getDrawCards().size());
  }

  @Test
  public void testGetNumFoundations() {
    List<Card> deck = model.getDeck();
    // getDeck only creates a full deck so there should be 4 aces and therefore 4 foundation Piles
    model.startGame(deck, false, 7, 3);
    assertEquals(4, model.getNumFoundations());
  }




}
