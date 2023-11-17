package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.BasicKlondike;


/**
 * Represents a Klondike solitaire variant with limited draw capabilities.
 * In this variant, cards from the draw pile can only be redrawn a specified
 * number of times before they are permanently discarded.
 */
public class LimitedDrawKlondike extends BasicKlondike implements KlondikeModel {

  // determines the amount of times redraws are allowed
  private final int numTimesDDAllowed;
  // shows the amount of times the deck has been recycled
  private int numCycles;
  // Tracks the number of individual cards discarded from the draw pile during the current cycle.
  private int numEachCardDD;

  /**
   * Constructs a new LimitedDrawKlondike game with specified redraw limits.
   *
   * @param numTimesRedrawAllowed The maximum number of times any card
   *                              can be redrawn from the draw pile.
   * @throws IllegalArgumentException if the provided redraw limit is negative.
   */
  public LimitedDrawKlondike(int numTimesRedrawAllowed) {
    if (numTimesRedrawAllowed < 0) {
      throw new IllegalArgumentException("invalid number of redraws");
    }
    this.numTimesDDAllowed = numTimesRedrawAllowed + 1;
    this.numCycles = 1;
    this.numEachCardDD = 0;
  }

  /**
   * Discards the top card from the draw pile. If the draw pile has not been
   * cycled through more than the allowed number of times, the discarded card
   * is added to the back of the draw pile. Otherwise, the card is permanently
   * discarded.
   *
   * @throws IllegalStateException if the operation is not allowed.
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    validateDiscardDraw();

    if (numCycles < numTimesDDAllowed) {
      discardTheDrawCard(true);
      numEachCardDD++;
      if (numEachCardDD == deck.size()) {
        numCycles++;
        numEachCardDD = 0;
      }
    }
    else if (numCycles == numTimesDDAllowed) {
      discardTheDrawCard(false);
      numEachCardDD++;
    }
  }
}
