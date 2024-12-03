package solitaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameState {
    private Stack<Card> deck; // Full deck of cards
    private Stack<Card>[] gamePiles; // Seven piles on the tableau
    private Stack<Card> visibleCards; // Stack for visible cards
    private Stack<Card> discardedCards; // Discard pile
    private Stack<Card>[] foundationPiles; // Four foundation piles

    @SuppressWarnings("unchecked")
    public GameState() {
        // Initialize the game state
        deck = new Stack<>();
        gamePiles = new Stack[7]; // Array of 7 stacks
        visibleCards = new Stack<>();
        discardedCards = new Stack<>();

        // Initialize each game pile
        for (int i = 0; i < gamePiles.length; i++) {
            gamePiles[i] = new Stack<>();
        }
        foundationPiles = new Stack[4];
        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Stack<>();
        }

        initializeDeck();
        shuffleDeck();
        dealInitialCards();
    }

    //REPLACE THE FOLLOWING 4 functions with your code from part 2

    // Creates a full deck of cards with all combinations of suits and ranks
    private void initializeDeck() {
      //USE IMPLEMENTATION FROM PART 2
      for (Suit suit : Suit.values()) {
        for (Rank rank : Rank.values()) {
            deck.push(new Card(suit, rank));
        }
      }
    }

    // Shuffles the deck
    private void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    // Deals cards to the 7 game piles
    private void dealInitialCards() {
        //USE IMPLEMENTATION FROM PART 2
        for (int i = 0; i < gamePiles.length; i++) {
            for (int j = 0; j <= i; j++) {
                Card card = deck.pop();
                if (j == i) {
                    card.isFaceUp = true; 
                }
                gamePiles[i].push(card);
            }
        }
    }

    // Draws up to three cards from the deck into visibleCards
    public void drawFromDeck() {
        //USE IMPLEMENTATION FROM PART 2
        discardCards();
        for (int i = 0; i < 3 && !deck.isEmpty(); i++) {
            visibleCards.push(deck.pop());
        }
        visibleCards.lastElement().isFaceUp = true;
    }

    public void discardCards() {
        //takes whatever cards are remaining in the visibleCards pile and moves them to the discardPiles
        while (!visibleCards.isEmpty()) {
            discardedCards.push(visibleCards.pop());
        }
    }

    // new methods from part 3

    public boolean canCardMove(Card card, int toPile){
        /*a card can be moved from the visible cards to a pile if 
            A) The card is the opposite color and its rank is ONE smaller than the card it will be placed on
            B) The pile is empty and the card is a King
        */
        if (gamePiles[toPile].isEmpty()) {
            // If the pile is empty, only a King can be placed
            return card.getRank() == Rank.KING;
        }
        
        Card topCard = gamePiles[toPile].peek();
        
        // Check if the rank is one smaller and the colors are opposite
        boolean isOppositeColor =  (!card.getColor().equals(topCard.getColor()));
                                  
        boolean isRankOneSmaller = card.getRank().ordinal() == topCard.getRank().ordinal() - 1;
        
        return isOppositeColor && isRankOneSmaller;
    }
    // attempts to move top card from visible card stack to the toPileIndex
    // returns true if successful and false if unsuccessful
    public boolean moveCardFromVisibleCardsToPile(int toPileIndex) {
        /* 
            If a card can be moved, it should be popped from the visible cards pile and pushed to the pile it is added to
            hints: use peek() and ordinal() to determine whether or not a card can be moved. 
            USE the method you just made, canCardMove

        */
        if (!visibleCards.isEmpty() && canCardMove(visibleCards.peek(), toPileIndex)) {
            Card cardToMove = visibleCards.pop();
            gamePiles[toPileIndex].push(cardToMove);
            if (!gamePiles[toPileIndex].isEmpty()) {
                // Flip the card if the pile is no longer empty
                gamePiles[toPileIndex].peek().isFaceUp = true;
            }
            return true;
        }
        return false;

    }

    // Move a card from one pile to another
    public boolean moveCards(int fromPileIndex, int cardIndex, int toPileIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];

        // Create a sub-stack of cards to move
        ArrayList<Card> cardsToMove = new ArrayList<>(fromPile.subList(cardIndex, fromPile.size()));

        Card bottomCard = cardsToMove.get(0); // the bottom card to be moved

        // Check if bottomCard can be moved to the toPile
        // if we can move the cards, add cardsToMove to the toPile and remove them from the fromPile
        // Then, flip the next card in the fromPile stack

        //return true if successful, false if unsuccessful
        if (canCardMove(bottomCard, toPileIndex)) {
            // Move the cards to the target pile
            for (Card card : cardsToMove) {
                gamePiles[toPileIndex].push(card);
            }
    
            // Remove moved cards from the original pile
            for (int i = 0; i < cardsToMove.size(); i++) {
                fromPile.pop();
            }
    
            // Flip the new top card of the from pile if there are any cards left
            if (!fromPile.isEmpty()) {
                fromPile.peek().isFaceUp = true;
            }
    
            return true;
        }
        return false;
    }
    private boolean canMoveToFoundation(Card card, int foundationIndex){
        Stack<Card> foundationPile = foundationPiles[foundationIndex];
     
        if (foundationPile.isEmpty()) {
           
            return card.getRank() == Rank.ACE;
        } else {
          
            Card topCard = foundationPile.peek();
            return card.getSuit() == topCard.getSuit() && card.getRank().ordinal() == topCard.getRank().ordinal() + 1;
        }
      
        
    }
    public boolean moveToFoundation(int fromPileIndex, int foundationIndex) {
        
        Stack<Card> fromPile = gamePiles[fromPileIndex];
        if (!fromPile.isEmpty()) {
            Card topCard = fromPile.peek();
            if (canMoveToFoundation(topCard, foundationIndex)) {
                
                foundationPiles[foundationIndex].push(fromPile.pop());
    
               
                if (!fromPile.isEmpty()) {
                    fromPile.peek().isFaceUp = true;
                }
    
                return true;
            }
        }
        return false;
        //remember to flip the new top card if it is face down

        //return true if successful, false otherwise
       
    }

    public boolean moveToFoundationFromVisibleCards(int foundationIndex) {
        //similar to the above method, 
        //move the top card from the visible cards to the foundation pile with index foundationIndex if possible
        
        if (!visibleCards.isEmpty()) {
            Card topCard = visibleCards.peek();
            if (canMoveToFoundation(topCard, foundationIndex)) {
                // Move the card to the foundation pile
                foundationPiles[foundationIndex].push(visibleCards.pop());
    
                // Flip the next card in the pile if necessary
                if (!visibleCards.isEmpty()) {
                    visibleCards.peek().isFaceUp = true;
                }
    
                return true;
            }
        }
        //return true if successful, false otherwise. 
        return false;
    }

    

    // Don't change this, used for testing
    public void printState() {
        System.out.println("Deck size: " + deck.size());

        System.out.print("Visible cards: ");
        if (visibleCards.isEmpty()) {
            System.out.println("None");
        } else {
            for (Card card : visibleCards) {
                System.out.print(card + " ");
            }
            System.out.println();
        }

        System.out.println("Discarded cards: " + discardedCards.size());

        System.out.println("Game piles:");
        for (int i = 0; i < gamePiles.length; i++) {
            System.out.print("Pile " + (i + 1) + ": ");
            if (gamePiles[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Card card : gamePiles[i]) {
                    System.out.print(card + " ");
                }
                System.out.println();
            }
        }
    }

    // getters
    public Stack<Card> getGamePile(int index) {
        return gamePiles[index];
    }

    public Stack<Card> getFoundationPile(int index) {
        return foundationPiles[index];
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getVisibleCards() {
        return visibleCards;
    }
}
