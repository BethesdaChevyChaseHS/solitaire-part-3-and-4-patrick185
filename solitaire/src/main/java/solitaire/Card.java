package solitaire;

public class Card {
    private final Suit suit;
    private final Rank rank;
    boolean isFaceUp;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = false;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }
    public void setFaceUp(boolean val){
        isFaceUp = val;
    }
    public void flip() {
        isFaceUp = !isFaceUp;
    }
    public CardColor getColor() {
        if (suit == Suit.HEARTS || suit == Suit.DIAMONDS) {
            return CardColor.RED;
        } else {
            return CardColor.BLACK;
        }
    }
    @Override
    public String toString() {
        return (isFaceUp ? rank + " of " + suit : "Face Down");
    }
}