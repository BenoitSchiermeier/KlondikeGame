package cs3500.klondike.controller;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

import java.util.List;


/**
 * Represents the controller for the Klondike game. The controller handles the flow of the game,
 * taking user inputs, updating the model, and providing outputs based on the state of the model.
 */
public interface KlondikeController {
  /**
   * The primary method for beginning and playing a game.
   *
   * @param model The game of solitaire to be played
   * @param deck The deck of cards to be used
   * @param shuffle Whether to shuffle the deck or not
   * @param numPiles How many piles should be in the initial deal
   * @param numDraw How many draw cards should be visible
   * @throws IllegalArgumentException if the model is null
   * @throws IllegalStateException if the controller is unable to
   *          successfully receive input or transmit output,
   *          or if the game cannot be started.
   */
  void playGame(KlondikeModel model, List<Card> deck,
                boolean shuffle, int numPiles, int numDraw);
}
