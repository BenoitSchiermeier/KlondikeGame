package cs3500.klondike.model.hw02;

import java.util.Objects;

/**
 * Implements the Card interface, representing a playing card in a deck with a specific value, suit,
 * and visibility state.
 */
public class CardImpl implements Card {

  private final int value;
  private final Suit suit;
  private boolean visible;

  /**
   * Constructs a card with a specified value and suit.
   *
   * @param value the value of the card, should be between 1 and 13 inclusive
   * @param suit  the suit of the card, cannot be null
   * @throws IllegalArgumentException if the value is less than 1 or greater than 13
   * @throws NullPointerException     if the suit is null
   */
  public CardImpl(int value, Suit suit) {
    if (value < 1 || value > 13) {
      throw new IllegalArgumentException("Invalid card value");
    }
    this.value = value;
    this.suit = Objects.requireNonNull(suit, "Suit cannot be null");
    this.visible = false;
  }

  /**
   * Returns a string representation of the card, combining its value and suit symbol.
   *
   * @return the string representation of the card
   */
  @Override
  public String toString() {
    String valueStr;
    switch (value) {
      case 1:
        valueStr = "A";
        break;

      case 2:
        valueStr = "2";
        break;

      case 3:
        valueStr = "3";
        break;

      case 4:
        valueStr = "4";
        break;

      case 5:
        valueStr = "5";
        break;

      case 6:
        valueStr = "6";
        break;

      case 7:
        valueStr = "7";
        break;
      case 8:
        valueStr = "8";
        break;
      case 9:
        valueStr = "9";
        break;
      case 10:
        valueStr = "10";
        break;
      case 11:
        valueStr = "J";
        break;
      case 12:
        valueStr = "Q";
        break;
      case 13:
        valueStr = "K";
        break;
      default:
        valueStr = String.valueOf(value);
        break;
    }
    return valueStr + suit.symbol;
  }

  /**
   * Enum representing the four suits of a standard deck of playing cards.
   */
  public enum Suit {
    CLUBS("♣"), DIAMONDS("♢"), HEARTS("♡"), SPADES("♠");

    private final String symbol;

    Suit(String symbol) {
      this.symbol = symbol;
    }
  }

  // returns the suit of the card
  public Suit getSuit() {
    return this.suit;
  }

  // returns the value of the card
  public int getValue() {
    return this.value;
  }

  // determines if the card is red or not
  public boolean isRed() {
    return this.suit == Suit.HEARTS || this.suit == Suit.DIAMONDS;
  }


  // determines whether the card is visible or not
  public boolean isCardVisible() {
    return this.visible;
  }

  // makes the card visible
  public void makeVisible() {
    this.visible = true;
  }

  // hides the card
  public void hideCard() {
    this.visible = false;
  }


  // overriding the equals and hashCode method
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CardImpl card = (CardImpl) o;
    return this.value == card.value
        && this.suit == card.suit
        && this.visible == card.visible;  // optional: if visibility matters in equality
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, suit, visible);
  }

}