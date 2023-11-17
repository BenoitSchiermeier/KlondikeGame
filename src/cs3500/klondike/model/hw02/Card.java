package cs3500.klondike.model.hw02;


import cs3500.klondike.model.hw02.CardImpl.Suit;

/**
 * This (essentially empty) interface marks the idea of cards.  You will need to
 * implement this interface in order to use your model.
 * 
 * <p>The only behavior guaranteed by this class is its {@link Card#toString()} method,
 * which will render the card as specified in the assignment.
 * 
 * <p>In particular, you <i>do not</i> know what implementation of this interface is
 * used by the Examplar wheats and chaffs, so your tests must be defined sufficiently
 * broadly that you do not rely on any particular constructors or methods of cards.
 */
public interface Card {

  /**
   * Renders a card with its value followed by its suit as one of
   * the following symbols (♣, ♠, ♡, ♢).
   * For example, the 3 of Hearts is rendered as {@code "3♡"}.
   * @return the formatted card
   */
  String toString();

  Suit getSuit();

  int getValue();

  boolean isRed();

  boolean isCardVisible();

  void makeVisible();

  void hideCard();

  boolean equals(Object o);

  int hashCode();

}
