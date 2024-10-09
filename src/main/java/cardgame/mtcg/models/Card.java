package cardgame.mtcg.models;


public abstract class Card { protected String id;
    protected String name;
    protected float damage;
    protected ElementType elementType;

    public Card(String id, String name, float damage, ElementType elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public float getDamage() { return damage; }
    public ElementType getElementType() { return elementType; }
}

