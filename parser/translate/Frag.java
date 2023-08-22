package translate;

public class Frag
{
    Frag next;

    public Frag()
    {
        this.next = null;
    }

    public Frag(Frag nxt)
    {
        this.next = nxt;
    }

    public void setNext(Frag nxt)
    {
        this.next = nxt;
    }

    public Frag getNext()
    {
        return this.next;
    }

    public boolean hasNext()
    {
        return this.next != null;
    }
}