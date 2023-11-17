


changes to current implementation:
1. All fields in BasicKlondike changed to protected, so that they can be accessed by
    LimitedDrawKlondike and WhiteheadKlondike.
2. created helper methods for start game like : validateTheGame for a shorter
    and more concise method.
3. Created helper method for discard draw validateDiscardDraw for cleaner and shorter method.
4. Added boolean to discardDraw helper method, discardTheDrawCard(boolean), to determine if card
    should be added at the end this change was made so that there would be less duplicate code.
5. added a catch (NullPointerException) to the controller -> made it append an error message
    to the appendable.
6. Changed validateDeck method in BasicKlondike. Changed it so that it now considers two runs
    of the same suits valid. Added helper method sortDeckValuesIntoLists which Sorts the values of
    cards in the deck into separate lists
7. Added helper methods to KlondikeTextualController's playGame to make method
    shorter and more concise, including:
    initializeGame
    handleNullPointer
    finalizeWinOrGameOver
8. created checkGameStarted in BasicKlondike to eliminate code duplication
9. changed makeCardsVisibleInCascadesToGame to protected in BasicKlondike
11. changed canMoveCardsToCascadePile to protected in BasicKlondike
12. changed checkPossibleCascadeCascade to protected in BasicKlondike
13. added testCompleteGameWithInvalidInput to test missed test in self eval : "verify
    that playGame works correctly by starting a new game and completing it
    successfully, including invalid inputs"
