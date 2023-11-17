package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.CardImpl.Suit;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;

import java.util.List;

/**
 * Represents the Whitehead variant of the Klondike solitaire game.
 * In this variant, all cards in the cascade piles are visible from the start
 * and cards moved between cascade piles should be of the same suit and in consecutive order.
 */
public class WhiteheadKlondike extends BasicKlondike implements KlondikeModel {

  /**
   * Constructs a WhiteheadKlondike game.
   */
  public WhiteheadKlondike() {
    super();
  }


  /**
   * Makes all the cards in the cascade piles visible.
   * This method iterates through each cascade pile and updates the visibility
   * of each card to ensure they are visible in the game.
   */
  @Override
  protected void makeCardsVisibleInCascadesToGame() {
    for (List<Card> pile : this.cascadePiles) {
      if (!pile.isEmpty()) {
        for (Card c : pile) {
          c.makeVisible();
        }
      }
    }
  }

  /**
   * Validates a list of cards to be moved in the Whitehead variant of the Klondike game.
   * Ensures that the cards being moved are consecutive and of the same suit. If the cards
   * are not valid for moving, it throws an exception.
   *
   * @param cardsBeingMoved the list of cards intended to be moved
   * @throws IllegalStateException if the cards are not valid for moving
   *         in the Whitehead game variant
   */
  private void validateWhiteHeadCardsBeingMoved(List<Card> cardsBeingMoved) {

    boolean validCards = true;
    Suit suit = cardsBeingMoved.get(0).getSuit();
    int consecutive = cardsBeingMoved.get(0).getValue();

    // makes sure cards being moved are valid:
    if (cardsBeingMoved.isEmpty()) {
      validCards = false;
    }

    for (int i = 1; i < cardsBeingMoved.size(); i++) {
      Card c = cardsBeingMoved.get(i);
      if (!c.getSuit().equals(suit)) {
        validCards = false;
      }
      if ((c.getValue() + 1) != consecutive) {
        validCards = false;
      }
      consecutive = c.getValue();
    }

    if (!validCards) {
      throw new IllegalStateException("the cards being moved are not valid in whitehead mp");
    }
  }

  /**
   * Validates if a list of cards from one cascade pile can be moved to another
   * according to the Whitehead Klondike rules.
   *
   * @param cards The list of cards to be moved.
   * @param destPile The destination cascade pile.
   * @return True if the cards can be moved, false otherwise.
   */
  @Override
  protected boolean canMoveCardsToCascadePile(List<Card> cards,
      List<Card> destPile) {

    validateWhiteHeadCardsBeingMoved(cards);

    if (destPile.isEmpty()) {
      return true;
    }

    // get the top card of the cascade pile:
    Card topCardOfDestinationPile = destPile.get(destPile.size() - 1);
    // get the bottom card of the cards being moved
    Card bottomCardOfCardsMoved = cards.get(0);

    boolean diffColors = !trueIfDifferentColor(topCardOfDestinationPile, bottomCardOfCardsMoved);
    boolean oneApart = isOneApart(topCardOfDestinationPile, bottomCardOfCardsMoved);

    return diffColors && oneApart;

  }

  /**
   * Checks if any cards from one cascade pile can be moved to another.
   *
   * @return True if there's a valid move between any two cascade piles, false otherwise.
   */
  @Override
  protected boolean checkPossibleCascadeCascade() {
    for (int i = 0; i < this.cascadePiles.size(); i++) {
      for (int j = 0; j < this.cascadePiles.size(); j++) {
        if (i != j && canMoveBetweenCascadePiles(i, j)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Validates if a list of cards from one cascade pile can be moved to another
   * cascade pile according to the Whitehead Klondike rules.
   *
   * @param srcIndex The index of the source cascade pile.
   * @param destIndex The index of the destination cascade pile.
   * @return True if the cards from the source pile can be moved to
   *          the destination pile, false otherwise.
   */
  private boolean canMoveBetweenCascadePiles(int srcIndex, int destIndex) {
    List<Card> srcPile = this.cascadePiles.get(srcIndex);
    List<Card> destPile = this.cascadePiles.get(destIndex);

    // Check each card of the source pile if it can be moved to the destination pile
    for (int i = 0; i < srcPile.size(); i++) {
      List<Card> cardsToMove = srcPile.subList(i, srcPile.size());
      if (canMoveCardsToCascadePile(cardsToMove, destPile)) {
        return true;
      }
    }

    return false;
  }
}

