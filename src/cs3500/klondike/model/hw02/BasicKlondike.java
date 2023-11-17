package cs3500.klondike.model.hw02;

import cs3500.klondike.model.hw02.CardImpl.Suit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is a stub implementation of the {@link cs3500.klondike.model.hw02.KlondikeModel} interface.
 * You may assume that the actual implementation of BasicKlondike will have a zero-argument (i.e.
 * default) constructor, and that all the methods below will be implemented.  You may not make any
 * other assumptions about the implementation of this class (e.g. what fields it might have, or
 * helper methods, etc.).
 *
 * <p>Once you've implemented all the constructors and methods on your own, you can
 * delete the placeholderWarning() method.
 */
public class BasicKlondike implements cs3500.klondike.model.hw02.KlondikeModel {

  protected List<Card> deck;
  protected List<List<Card>> cascadePiles;
  protected List<List<Card>> foundationPiles;
  protected boolean gameStarted;
  protected int numberOfDrawCardsVisible;

  /**
   * Constructs a new BasicKlondike game instance.
   */
  public BasicKlondike() {
    this.deck = getDeck();
    this.cascadePiles = new ArrayList<>();
    this.foundationPiles = new ArrayList<>();
    this.gameStarted = false;
    this.numberOfDrawCardsVisible = 0;
  }

  @Override
  public List<Card> getDeck() {
    List<Card> newDeck = new ArrayList<>();

    for (Suit suit : Suit.values()) {
      for (int i = 1; i <= 13; i++) {
        newDeck.add(new CardImpl(i, suit));
      }
    }
    return newDeck;
  }


  /**
   * Determines if the given deck is valid.
   *
   * @param deck the list of cards representing a deck
   * @return true if the deck is valid, false otherwise
   * @throws IllegalArgumentException if the deck is null, empty, or contains null cards
   */
  public boolean isValidDeck(List<Card> deck) {

    checkBaseValidDeckCases(deck);

    List<List<Integer>> allValues = sortDeckValuesIntoLists(deck);
    if (allValues == null) {
      return false;
    }

    for (int i = 0; i < allValues.size(); i++) {
      for (int j = i + 1; j < allValues.size(); j++) {
        if (!allValues.get(i).equals(allValues.get(j))) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Sorts the values of cards in the deck into separate lists.
   *
   * @param deck the list of cards representing a deck
   * @return a list of lists containing sorted card values, or null if the sorting fails
   */
  private static List<List<Integer>> sortDeckValuesIntoLists(List<Card> deck) {
    Map<Suit, List<List<Integer>>> suitToValuesMap = new HashMap<>();

    for (Card card : deck) {
      List<List<Integer>> suitValuesList = suitToValuesMap.computeIfAbsent(card.getSuit(),
          k -> new ArrayList<>());

      boolean added = false;
      for (List<Integer> values : suitValuesList) {
        if (!values.contains(card.getValue())) {
          values.add(card.getValue());
          added = true;
          break;
        }
      }

      if (!added) {
        List<Integer> newValuesList = new ArrayList<>();
        newValuesList.add(card.getValue());
        suitValuesList.add(newValuesList);
      }
    }

    List<List<Integer>> allValues = new ArrayList<>();

    for (List<List<Integer>> valuesList : suitToValuesMap.values()) {
      for (List<Integer> values : valuesList) {
        Collections.sort(values);

        if (values.get(0) != 1) {
          return null;
        }

        for (int i = 1; i < values.size(); i++) {
          if (values.get(i - 1) + 1 != values.get(i)) {
            return null;
          }
        }
        allValues.add(values);
      }
    }
    return allValues;
  }


  /**
   * Checks basic validations for the given deck.
   *
   * @param deck the list of cards representing a deck
   * @throws IllegalArgumentException if the deck is null, empty, or contains null cards
   */
  private static void checkBaseValidDeckCases(List<Card> deck) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck can't be null");
    }
    if (deck.isEmpty()) {
      throw new IllegalArgumentException("Deck can't be empty");
    }

    for (Card card : deck) {
      if (card == null) {
        throw new IllegalArgumentException("Deck contains null card(s)");
      }
    }
  }


  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
      throws IllegalArgumentException, IllegalStateException {

    // STEP 1: setup and validate the game:
    setAndValidateGame(deck, shuffle, numPiles, numDraw);

    // STEP 2: MAKE ALL BOTTOM CARDS OF CASCADE VISIBLE:
    makeCardsVisibleInCascadesToGame();

    // starts the game
    this.gameStarted = true;

  }

  /**
   * Makes the top card of each cascade pile visible.
   * <p>
   * This method iterates through each cascade pile in the game, and if a pile is not empty,
   * it makes the top card (last card in the pile) visible to the player.
   * </p>
   */
  protected void makeCardsVisibleInCascadesToGame() {
    for (List<Card> pile : this.cascadePiles) {
      if (!pile.isEmpty()) {
        pile.get(pile.size() - 1).makeVisible();
      }
    }
  }


  /**
   * Sets up and validates the game parameters.
   *
   * @param deck the list of cards representing a deck
   * @param shuffle whether to shuffle the deck
   * @param numPiles number of cascade piles
   * @param numDraw number of draw cards visible
   * @throws IllegalArgumentException if the setup is invalid
   */
  private void setAndValidateGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw) {
    // STEP 1 VALIDATE THE DECK:
    if (!isValidDeck(deck)) {
      throw new IllegalArgumentException("deck is not valid in startGame");
    }

    if (numDraw <= 0) {
      throw new IllegalArgumentException("draws has to be positive");
    }

    // determines if the game is started
    if (this.gameStarted) {
      throw new IllegalStateException("game has already started");
    }

    // assigns the field numberOfDrawCardsVisible to the number of cards visible in the game
    this.numberOfDrawCardsVisible = numDraw;

    // should the deck be shuffled?
    if (shuffle) {
      Collections.shuffle(deck);
    }

    this.deck = new ArrayList<>(deck);

    // STEP 2: ADDING THE FOUNDATION PILES BASED ON THE NUMBER OF ACES IN THE DECK
    for (Card c : this.deck) {
      if (c.getValue() == 1) {
        this.foundationPiles.add(new ArrayList<>());
      }
    }

    // 3A: CHECK IF THERE IS A CORRECT AMOUNT OF CASCADE PILES:
    int pilesToCards = 0;
    for (int i = 1; i <= numPiles; i++) {
      pilesToCards += i;
    }
    if (pilesToCards > this.deck.size() || (numPiles <= 0)) {
      throw new IllegalArgumentException("there are too many piles for the number cards in deck");
    }

    // 3b: ADD THE PROPER AMOUNT OF CASCADE PILES
    for (int i = 0; i < numPiles; i++) {
      cascadePiles.add(new ArrayList<>());
    }

    // 3: DEAL THE CARDS TO THE CASCADE PILES:
    dealCardsToCascadePiles();

    this.makeTopCardsOfDeckVisible(numDraw);

  }

  /**
   * Deals cards from the deck to the cascade piles.
   *
   * <p>Starts by dealing to the last cascade pile and moves upwards, with each cascade
   * pile receiving one card more than the previous pile. If the deck runs out before
   * completing the deal, an exception is thrown. <\p>
   *
   * @throws IllegalArgumentException if there are insufficient cards in the deck
   *                                  to populate the cascade piles.
   */
  private void dealCardsToCascadePiles() {
    int counter = 0;
    for (int i = cascadePiles.size() - 1; i >= 0; i--) {
      for (int j = 0; j <= i; j++) {
        if (this.deck.isEmpty()) {
          throw new IllegalArgumentException("the deck is empty and cannot be delt");
        }
        cascadePiles.get(j + counter).add(this.deck.remove(0));
      }
      counter++;
    }
  }


  /**
   * Helper method to make the top cards of the deck visible.
   *
   * @param numDraw number of cards to be made visible
   */
  private void makeTopCardsOfDeckVisible(int numDraw) {
    for (int i = 0; (i < numDraw); i++) {
      if (i < this.deck.size()) {
        this.deck.get(i).makeVisible();
      }
    }
  }


  @Override
  public void movePile(int srcPile, int numCards, int destPile)
      throws IllegalStateException, IllegalArgumentException {
    // check if the game has started:
    checkGameStarted();

    List<Card> sourcePile = validatePiles(srcPile, destPile);

    List<Card> destinationPile = this.cascadePiles.get(destPile);

    List<Card> cardsBeingMoved = getCardsBeingMovedAndValidate(numCards, sourcePile);

    // STEP 3: CHECKS THAT ALL THE CARDS BEING MOVED ARE VISIBLE:
    for (Card c : cardsBeingMoved) {
      if (!c.isCardVisible()) {
        throw new IllegalArgumentException("card being moved is not visible");
      }
    }

    // checks if the move is possible according to the rules of the game
    if (!canMoveCardsToCascadePile(cardsBeingMoved, destinationPile)) {
      throw new IllegalStateException("this move movePile move is not permitted due to game rules");
    }

    // moves the cards from pile to pile:
    moveCards(srcPile, destPile, cardsBeingMoved);

    // makes the last leftover card from the source pile visible
    if (!sourcePile.isEmpty() && !sourcePile.get(sourcePile.size() - 1).isCardVisible()) {
      sourcePile.get(sourcePile.size() - 1).makeVisible();
    }


  }

  /**
   * Moves a specified list of cards from one cascade pile to another.
   *
   * @param srcPile          the index of the source cascade pile.
   * @param destPile         the index of the destination cascade pile.
   * @param cardsBeingMoved  the list of cards that are to be moved from the source
   *                         pile to the destination pile.
   */
  private void moveCards(int srcPile, int destPile, List<Card> cardsBeingMoved) {
    this.cascadePiles.get(destPile).addAll(cardsBeingMoved);
    this.cascadePiles.get(srcPile).removeAll(cardsBeingMoved);
  }

  /**
   * Validates the move pile operation and retrieves the cards to be moved.
   *
   * @param numCards number of cards to move
   * @param sourcePile source pile of cards
   * @return a list of cards to be moved
   * @throws IllegalArgumentException if the move is invalid
   */
  private static List<Card> getCardsBeingMovedAndValidate(int numCards, List<Card> sourcePile) {
    // 2a: CHECKS THAT NUM OF CARDS BEING MOVED IS POSSIBLE:
    if (sourcePile.size() < numCards || numCards <= 0) {
      throw new IllegalArgumentException("Invalid number of cards being moved");
    }

    // create a list of the cards being moved
    List<Card> cardsBeingMoved =
        sourcePile.subList(sourcePile.size() - numCards, sourcePile.size());

    return cardsBeingMoved;
  }


  /**
   * Validates source and destination piles for the move operation.
   *
   * @param srcPile source pile index
   * @param destPile destination pile index
   * @return the source pile of cards
   * @throws IllegalArgumentException if the source or destination pile is invalid
   */
  private List<Card> validatePiles(int srcPile, int destPile) {
    // STEP 2: VERIFY THE VARIABLES:
    if ((srcPile < 0) || (srcPile >= cascadePiles.size())
        || (destPile < 0) || (destPile >= cascadePiles.size()) || (srcPile == destPile)) {
      throw new IllegalArgumentException("variables are not possible");
    }

    List<Card> sourcePile = this.cascadePiles.get(srcPile);

    if (sourcePile.isEmpty()) {
      throw new IllegalArgumentException("source pile is empty");
    }
    return sourcePile;
  }

  /**
   * Checks if the game has started.
   *
   * @throws IllegalStateException if the game hasn't started yet
   */
  private void checkGameStarted() {
    // STEP 1: CHECK IF THE GAME HAS STARTED:
    if (!this.gameStarted) {
      throw new IllegalStateException("game has not been started yet");
    }
  }


  /**
   * Determines if the cards can be moved to a destination pile.
   *
   * @param cards    the list of cards to be moved
   * @param destPile the destination pile
   * @return true if the cards can be moved, false otherwise
   */
  protected boolean canMoveCardsToCascadePile(List<Card> cards, List<Card> destPile) {
    if (cards == null || cards.isEmpty()) {
      return false;
    }

    // if the destination pile is empty, it makes sure that the bottom card of the list of cards
    // is a king to adhere to game rules
    if (destPile.isEmpty()) {
      return cards.get(0).getValue() == 13;
    }

    // get the top card of the cascade pile:
    Card topCardOfDestinationPile = destPile.get(destPile.size() - 1);
    // make visible just in case
    topCardOfDestinationPile.makeVisible();
    // get the bottom card of the cards being moved
    Card bottomCardOfCardsMoved = cards.get(0);
    bottomCardOfCardsMoved.makeVisible();

    boolean diffColors = trueIfDifferentColor(topCardOfDestinationPile, bottomCardOfCardsMoved);
    boolean oneApart = isOneApart(topCardOfDestinationPile, bottomCardOfCardsMoved);

    return diffColors && oneApart;
  }


  /**
   * Determines if two cards are one apart in value.
   *
   * @param topCardOfDestinationPile the top card of the destination pile
   * @param bottomCardOfCardsMoved the bottom card of the cards being moved
   * @return true if the cards are one apart, false otherwise
   */
  protected static boolean isOneApart(Card topCardOfDestinationPile, Card bottomCardOfCardsMoved) {
    // determines if the cards are one apart
    boolean oneApart = topCardOfDestinationPile.getValue()
        == (bottomCardOfCardsMoved.getValue() + 1);
    return oneApart;
  }

  /**
   * Determines if two cards are of different colors.
   *
   * @param topCardOfDestinationPile the top card of the destination pile
   * @param bottomCardOfCardsMoved the bottom card of the cards being moved
   * @return true if the cards are of different colors, false otherwise
   */
  protected static boolean trueIfDifferentColor(Card topCardOfDestinationPile,
      Card bottomCardOfCardsMoved) {
    // determines if the cards are a different color
    boolean diffColors = (topCardOfDestinationPile.isRed() && !bottomCardOfCardsMoved.isRed())
        || (!topCardOfDestinationPile.isRed() && bottomCardOfCardsMoved.isRed());
    return diffColors;
  }


  /**
   * Moves the topmost draw-card to the destination pile.  If no draw cards remain, reveal the next
   * available draw cards
   *
   * @param destPile the 0-based index (from the left) of the destination pile for the card
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if destination pile number is invalid
   * @throws IllegalStateException    if there are no draw cards, or if the move is not allowable
   */
  @Override
  public void moveDraw(int destPile) throws IllegalStateException, IllegalStateException {

    Card drawCard = drawCardAndValidateMoveDraw(destPile);

    // STEP 2: IF THE CARD IS VISIBLE IN THE DECK, DETERMINE IF IT CAN BE MOVED TO THE CASCADE PILE
    //         AND MOVE IT THERE
    if (drawCard.isCardVisible()) {
      List<Card> loc = new ArrayList<>();
      loc.add(drawCard);
      if (canMoveCardsToCascadePile(loc, this.cascadePiles.get(destPile))) {
        Card cardToAddToCascade = this.deck.remove(0);
        // add the card from the deck that was removed from the draw pile;
        this.cascadePiles.get(destPile).add(cardToAddToCascade);
      } else {
        throw new IllegalStateException("moving this draw card to the pile is not allowable");
      }
    }

    // STEP 3: MAKE THE REMAINING DRAW CARDS VISIBLE
    this.makeTopCardsOfDeckVisible(this.numberOfDrawCardsVisible);

  }

  /**
   * Validates the move draw operation and retrieves the draw card.
   *
   * @param destPile destination pile index
   * @return the draw card to be moved
   * @throws IllegalStateException if the move is not valid
   */
  private Card drawCardAndValidateMoveDraw(int destPile) {
    // check if the game has started:
    checkGameStarted();

    if (this.deck.isEmpty()) {
      throw new IllegalStateException("draw pile is empty");
    }
    if (destPile < 0 || destPile >= cascadePiles.size()) {
      throw new IllegalArgumentException("Destination pile number is invalid.");
    }

    // STEP 1: IF THE CARD IS NOT VISIBLE, MAKE IT VISIBLE
    if (!this.deck.get(0).isCardVisible()) {
      this.makeTopCardsOfDeckVisible(this.numberOfDrawCardsVisible);
    }

    return this.deck.get(0);

  }


  /**
   * Determines if a card can be moved to a foundation pile.
   *
   * @param cardToMove     the card to be moved
   * @param foundationPile the destination foundation pile
   * @return true if the card can be moved, false otherwise
   */
  boolean canMoveCardToFoundation(Card cardToMove, List<Card> foundationPile) {
    if (foundationPile.isEmpty()) {
      return cardToMove.getValue() == 1;
    }
    // safety net
    if (!cardToMove.isCardVisible()) {
      return false;
    }

    Card topCard = foundationPile.get(foundationPile.size() - 1);
    return (cardToMove.getSuit() == topCard.getSuit())
        && (cardToMove.getValue() == topCard.getValue() + 1);
  }


  /**
   * Moves the top card of the given pile to the requested foundation pile.
   *
   * @param srcPile        the 0-based index (from the left) of the pile to move a card
   * @param foundationPile the 0-based index (from the left) of the foundation pile to place the
   *                       card
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid
   * @throws IllegalStateException    if the source pile is empty or if the move is not allowable
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile) throws IllegalStateException {
    // check if the game has started:
    checkGameStarted();

    // Assume foundationPiles are initialized to empty lists
    if (foundationPile < 0 || foundationPile >= this.foundationPiles.size()) {
      throw new IllegalArgumentException("Invalid foundation pile number.");
    }

    if (srcPile < 0 || srcPile >= this.cascadePiles.size()) {
      throw new IllegalArgumentException("Invalid src pile number.");
    }

    List<Card> sourceCascadePile = cascadePiles.get(srcPile);
    if (sourceCascadePile.isEmpty()) {
      throw new IllegalStateException("Source pile is empty.");
    }

    Card cardBeingMoved = sourceCascadePile.get(sourceCascadePile.size() - 1);

    if (!canMoveCardToFoundation(cardBeingMoved, this.foundationPiles.get(foundationPile))) {
      throw new IllegalStateException("moving this cascade cart to foundation is not allowed");
    }

    Card cardToAdd = sourceCascadePile.remove(sourceCascadePile.size() - 1);
    this.foundationPiles.get(foundationPile).add(cardToAdd);

    makeCardsVisibleInCascadesToGame();

  }


  /**
   * Moves the topmost draw-card directly to a foundation pile.
   *
   * @param foundationPile the 0-based index (from the left) of the foundation pile to place the
   *                       card
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if the foundation pile number is invalid
   * @throws IllegalStateException    if there are no draw cards or if the move is not allowable
   */
  @Override
  public void moveDrawToFoundation(int foundationPile)
      throws IllegalStateException, IllegalArgumentException {

    // check if the game has started
    checkGameStarted();

    if (foundationPile < 0 || foundationPile >= foundationPiles.size()) {
      throw new IllegalArgumentException("Invalid foundation pile number.");
    }

    if (this.deck.isEmpty()) {
      throw new IllegalStateException("no more cards in draw pile");
    }

    Card cardToMove = this.deck.get(0);
    List<Card> destFoundationPile = foundationPiles.get(foundationPile);

    // determines if the card can be moved to the foundation according to game rules
    if (!canMoveCardToFoundation(cardToMove, destFoundationPile)) {
      throw new IllegalStateException("Move is not allowable.");
    }

    Card cardRemoved = this.deck.remove(0);
    this.foundationPiles.get(foundationPile).add(cardRemoved);

    this.makeTopCardsOfDeckVisible(this.numberOfDrawCardsVisible);

  }


  /**
   * Discards the topmost draw-card.
   *
   * @throws IllegalStateException if the game hasn't been started yet
   * @throws IllegalStateException if move is not allowable
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    validateDiscardDraw();

    discardTheDrawCard(true);
  }


  /**
   * Discards the top card of the draw pile. If {@code addToBackOfDeck} is true,
   * the discarded card is added to the back of the deck; otherwise, it's removed from the deck.
   * After discarding, the method ensures that the top {@code numberOfDrawCardsVisible} cards
   * of the deck are made visible.
   *
   * @param addToBackOfDeck If true, the discarded card is added to the back of the deck.
   */
  protected void discardTheDrawCard(boolean addToBackOfDeck) {
    // move the top card of the draw pile to the bottom
    Card topCard = this.deck.remove(0);
    topCard.hideCard();
    // adds the card to the back of the deck if addToBackOfDeck is true
    if (addToBackOfDeck) {
      this.deck.add(topCard);
    }

    // makes the numberOfDrawCardsVisible visible in the deck
    this.makeTopCardsOfDeckVisible(this.numberOfDrawCardsVisible);
  }

  /**
   * Validates the discard draw operation.
   * This method checks if the game has started and if there are cards left in the draw pile.
   *
   * @throws IllegalStateException if the game hasn't been started or if the draw pile is empty.
   */
  protected void validateDiscardDraw() {
    // check if the game has started:
    checkGameStarted();

    if (this.deck.isEmpty()) {
      throw new IllegalStateException("No cards left in the deck.");
    }
  }


  /**
   * Returns the number of rows currently in the game.
   *
   * @return the height of the current table of cards
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumRows() {

    checkGameStarted();

    // iterates through all the cascade piles to find the longest one
    int maxRows = 0;
    for (List<Card> pile : this.cascadePiles) {
      if (pile.size() > maxRows) {
        maxRows = pile.size();
      }
    }
    return maxRows;

  }

  /**
   * Returns the number of piles for this game.
   *
   * @return the number of piles
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumPiles() {

    checkGameStarted();

    return this.cascadePiles.size();
  }


  /**
   * Returns the maximum number of visible cards in the draw pile.
   *
   * @return the number of visible cards in the draw pile
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public int getNumDraw() {
    checkGameStarted();
    return this.numberOfDrawCardsVisible;

  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    checkGameStarted();

    // you can do an unlimited amount of discard draw
    if (!this.deck.isEmpty()) {
      return false;
    }

    // Check if any moves are possible between cascade piles
    if (checkPossibleCascadeCascade()) {
      return false;  // Move is possible, so game is not over
    }

    return !checkCascadeFoundation();

  }

  /**
   * Checks if a card from any of the cascade piles can be moved to a foundation pile.
   * This method iterates through each cascade pile, and for the bottom-most card of each
   * pile, it checks if it can be moved to any of the foundation piles based on the game rules.
   *
   * @return true if a card from a cascade pile can be moved to a foundation pile; false otherwise.
   */
  private boolean checkCascadeFoundation() {
    for (int i = 0; i < cascadePiles.size(); i++) {
      if (!cascadePiles.get(i).isEmpty()) {
        Card bottomCard = cascadePiles.get(i).get(cascadePiles.get(i).size() - 1);
        for (int j = 0; j < foundationPiles.size(); j++) {
          if (canMoveCardToFoundation(bottomCard, foundationPiles.get(j))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Checks if a sequence of cards from one cascade pile can be moved to another cascade pile.
   * This method iterates through each card of every cascade pile, and for each visible card and
   * its subsequent sequence, it checks if the sequence can be moved to any other cascade pile
   * based on the game rules. The method ensures that it doesn't check the move within the same
   * cascade pile.
   *
   * @return true if a sequence from one cascade pile can be moved to another
   *          cascade pile; false otherwise.
   */
  protected boolean checkPossibleCascadeCascade() {
    for (int i = 0; i < cascadePiles.size(); i++) {
      List<Card> sourceCascadePile = cascadePiles.get(i);
      for (int cardIdx = sourceCascadePile.size() - 1; cardIdx >= 0; cardIdx--) {
        if (sourceCascadePile.get(cardIdx).isCardVisible()) {
          List<Card> cardsToMove = sourceCascadePile.subList(cardIdx, sourceCascadePile.size());
          for (int j = 0; j < cascadePiles.size(); j++) {
            if (i != j && canMoveCardsToCascadePile(cardsToMove, cascadePiles.get(j))) {
              return true;
            }
          }
          break;
        }
      }
    }
    return false;
  }


  @Override
  public int getScore() throws IllegalStateException {
    checkGameStarted();

    // adds the score of the topmost cards in the foundation piles
    int score = 0;
    for (int i = 0; i < this.foundationPiles.size(); i++) {
      if (!this.foundationPiles.get(i).isEmpty()) {
        Card c = this.getCardAt(i);
        score += c.getValue();
      }
    }

    return score;
  }

  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException, IllegalArgumentException {
    checkGameStarted();

    if ((pileNum < 0) || (pileNum >= cascadePiles.size())) {
      throw new IllegalArgumentException("pile num invalid");
    }

    return cascadePiles.get(pileNum).size();
  }

  @Override
  public boolean isCardVisible(int pileNum, int card)
      throws IllegalStateException, IllegalArgumentException {

    checkGameStarted();

    if ((pileNum < 0) || (pileNum >= cascadePiles.size())) {
      throw new IllegalArgumentException("pile num invalid");
    }

    if ((card < 0) || (card >= cascadePiles.get(pileNum).size())) {
      throw new IllegalArgumentException("card num not valid");
    }
    if (this.cascadePiles.get(pileNum).isEmpty()) {
      throw new IllegalArgumentException("this pile is empty");
    }

    return cascadePiles.get(pileNum).get(card).isCardVisible();
  }

  @Override
  public Card getCardAt(int pileNum, int card)
      throws IllegalStateException, IllegalArgumentException {

    checkGameStarted();

    if ((pileNum < 0) || (pileNum >= this.cascadePiles.size())) {
      throw new IllegalArgumentException("pile num invalid");
    }
    if (this.cascadePiles.get(pileNum).isEmpty()) {
      return null;
    }

    if ((card < 0) || (card >= cascadePiles.get(pileNum).size())) {
      throw new IllegalArgumentException("card num not valid");
    }

    Card c = this.cascadePiles.get(pileNum).get(card);

    if (!this.isCardVisible(pileNum, card)) {
      throw new IllegalArgumentException("card is not visible");
    }

    return c;

  }


  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException, IllegalArgumentException {

    checkGameStarted();

    if ((foundationPile < 0) || (foundationPile >= this.foundationPiles.size())) {
      throw new IllegalArgumentException("foundation pile index invalid");
    }

    if (this.foundationPiles.get(foundationPile).isEmpty()) {
      return null;
    }

    return this.foundationPiles.get(foundationPile).get(
        this.foundationPiles.get(foundationPile).size() - 1);
  }


  /**
   * Returns the currently available draw cards. There should be at most
   * {@link KlondikeModel#getNumDraw} cards (the number specified when the game started) -- there
   * may be fewer, if cards have been removed. NOTE: Users of this method should not modify the
   * resulting list.
   *
   * @return the ordered list of available draw cards (i.e. first element of this list is the first
   *          one to be drawn)
   * @throws IllegalStateException if the game hasn't been started yet
   */
  @Override
  public List<Card> getDrawCards() throws IllegalStateException {

    checkGameStarted();

    // goes through the deck and adds the visible cards to a list of cards
    List<Card> drawCards = new ArrayList<>();
    if (!this.deck.isEmpty()) {
      for (int i = 0; i < this.deck.size() && i < numberOfDrawCardsVisible; i++) {
        drawCards.add(this.deck.get(i));
      }
    }
    return drawCards;
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    checkGameStarted();
    return this.foundationPiles.size();
  }


}
