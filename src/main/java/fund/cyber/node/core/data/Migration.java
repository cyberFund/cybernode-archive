package fund.cyber.node.core.data;

public abstract class Migration implements Runnable, Comparable<Migration> {

    protected final int id;

    protected Migration(final int id) {
        this.id = id;
    }

    @Override
    public abstract void run();

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(final Migration o) {
        return id - o.id;
    }
}
