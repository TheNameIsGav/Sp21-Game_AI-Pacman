package pacman.entries.ghosts.mannerspackage;

public abstract class States<T> {
	public abstract void update(T g);
	public abstract void exit(T g);
	public abstract void enter(T g, States<T> a);
}
