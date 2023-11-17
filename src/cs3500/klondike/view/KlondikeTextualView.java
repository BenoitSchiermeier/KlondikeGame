package cs3500.klondike.view;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

import java.io.IOException;
import java.util.List;

/**
 * Provides a textual view of the Klondike solitaire game state.
 * The view displays the current state of the draw cards, foundation piles, and cascade piles.
 */
public class KlondikeTextualView implements TextualView {

  // a KlondikeModel
  private final KlondikeModel model;

  // appendable object is where the game's textual representation will be appended to
  private Appendable appendable;


  /**
   * Constructs a new KlondikeTextualView object.
   *
   * @param model the Klondike game model to be visualized
   * @throws IllegalArgumentException if the provided model is null
   */
  public KlondikeTextualView(KlondikeModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  /**
   * Constructs a KlondikeTextualView using a specified game model and an appendable object.
   * The appendable object is where the game's textual representation will be appended to.
   * This provides flexibility in where the view's output is directed,
   * such as the console or a file.
   *
   * @param model the model representing the game state to be displayed
   * @param appendable the object to which the game state is to be appended
   * @throws IllegalArgumentException if the given appendable or model is null
   */
  public KlondikeTextualView(KlondikeModel model, Appendable appendable) {
    if (appendable == null || model == null) {
      throw new IllegalArgumentException("model or appendable cannot be null");
    }
    this.model = model;
    this.appendable = appendable;
  }


  /**
   * Returns a string representation of the game state.
   *
   * <p>
   * The state includes: 1. Draw cards. 2. Foundation piles. 3. Cascade piles. </p>
   *
   * @return a string representation of the game state
   */
  public String toString() {
    StringBuilder result = new StringBuilder();

    // 1. Display draw cards
    displayDrawCards(result);

    // 2. Display foundation piles
    displayFoundationPiles(result);

    // 3. Display cascade piles
    displayCascadePiles(result);

    // 4. return the result
    return result.toString();
  }

  private void displayCascadePiles(StringBuilder result) {
    int maxPileHeight = 0;
    for (int i = 0; i < model.getNumPiles(); i++) {
      maxPileHeight = Math.max(maxPileHeight, model.getPileHeight(i));
    }

    for (int row = 0; row < maxPileHeight; row++) {
      for (int pile = 0; pile < model.getNumPiles(); pile++) {
        if (row < model.getPileHeight(pile)) {
          if (model.isCardVisible(pile, row)) {
            Card card = model.getCardAt(pile, row);
            result.append(String.format("%3s", card.toString()));
          } else {
            result.append("  ?");
          }
        } else if (row == 0) {
          result.append("  X");
        } else {
          result.append("   ");
        }
      }
      result.append("\n");
    }
  }


  private void displayFoundationPiles(StringBuilder result) {
    result.append("Foundation: ");
    for (int i = 0; i < model.getNumFoundations(); i++) {
      Card foundationTop = model.getCardAt(i);
      if (foundationTop == null) {
        result.append("<none>").append(", ");
      } else {
        result.append(foundationTop).append(", ");
      }
    }
    result.setLength(result.length() - 2); // Remove trailing comma and space
    result.append("\n");
  }


  private void displayDrawCards(StringBuilder result) {
    List<Card> drawCards = model.getDrawCards();
    result.append("Draw: ");
    for (Card card : drawCards) {
      result.append(card.toString()).append(", ");
    }
    if (!drawCards.isEmpty()) {
      result.setLength(result.length() - 2); // Remove trailing comma and space
    }
    result.append("\n");
  }


  @Override
  public void render() throws IOException {
    appendable.append(this.toString());
  }


}
