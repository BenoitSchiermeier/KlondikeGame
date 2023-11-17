package cs3500.klondike;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.KlondikeCreator.GameType;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.model.hw04.WhiteheadKlondike;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Contains tests for the {@link KlondikeCreator} class, ensuring its capability to produce the
 * correct types of Klondike game models. This suite of tests validates that the factory methods of
 * KlondikeCreator generate the expected instances of game models based on the provided game types.
 */
public class TestKlondikeCreator {
  KlondikeModel model;
  List<Card> deckCustom;

  @Before
  public void setup() {
    model = KlondikeCreator.create(GameType.BASIC);
    deckCustom = new ArrayList<>();
    deckCustom.add(getCard("A♢"));
    deckCustom.add(getCard("2♣"));
    deckCustom.add(getCard("2♠"));
    deckCustom.add(getCard("2♢"));
    deckCustom.add(getCard("2♡"));
    deckCustom.add(getCard("A♠"));
    deckCustom.add(getCard("A♣"));
    deckCustom.add(getCard("A♡"));

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
  public void testCreateBasicKlondike() {
    KlondikeModel model = KlondikeCreator.create(KlondikeCreator.GameType.BASIC);
    assertTrue(model instanceof BasicKlondike);
  }

  @Test
  public void testCreateWhiteheadKlondike() {
    KlondikeModel model = KlondikeCreator.create(KlondikeCreator.GameType.WHITEHEAD);
    assertTrue(model instanceof WhiteheadKlondike);
  }

  @Test
  public void testCreateLimitedKlondikeWithDefaultRedraws() {
    KlondikeModel model = KlondikeCreator.create(KlondikeCreator.GameType.LIMITED);
    assertTrue(model instanceof LimitedDrawKlondike);
  }

  @Test
  public void testCreateLimitedKlondikeWithDefaultRedrawsCheckRedraws() {
    KlondikeModel model = KlondikeCreator.create(KlondikeCreator.GameType.LIMITED);
    assertTrue(model instanceof LimitedDrawKlondike);
    model.startGame(deckCustom, false, 3, 10);
    for (int i = 0; i < 6; i++) {
      model.discardDraw();
    }
    assertThrows(IllegalStateException.class, () -> model.discardDraw());
  }

  @Test
  public void testCreateKlondikeLimitedKlondikeWithCustomCheckRedraws() {
    KlondikeModel model =
        KlondikeCreator.createKlondike(KlondikeCreator.GameType.LIMITED, 0);
    assertTrue(model instanceof LimitedDrawKlondike);
    model.startGame(deckCustom, false, 3, 10);
    model.discardDraw();
    model.discardDraw();
    assertThrows(IllegalStateException.class, () -> model.discardDraw());
  }

  @Test
  public void testCreateKlondikeLimitedKlondikeWithCustomCheckRedraws4() {
    KlondikeModel model =
        KlondikeCreator.createKlondike(KlondikeCreator.GameType.LIMITED, 4);
    assertTrue(model instanceof LimitedDrawKlondike);
    model.startGame(deckCustom, false, 3, 10);
    for (int i = 0; i < 10; i++) {
      model.discardDraw();
    }
    assertThrows(IllegalStateException.class, model::discardDraw);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCreateKlondikeLimitedInvalidDraws() {
    KlondikeModel model =
        KlondikeCreator.createKlondike(KlondikeCreator.GameType.LIMITED, -1);
    assertTrue(model instanceof LimitedDrawKlondike);
  }
}
