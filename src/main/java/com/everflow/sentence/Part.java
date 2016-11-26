package com.everflow.sentence;

/**
 * @author Ildar Gafarov on 13.05.16.
 */
public abstract class Part<P extends Part> {

    public abstract String getSource();
    public abstract P clone();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Part part = (Part) o;

        return !(getSource() != null ? !getSource().equalsIgnoreCase(part.getSource()) : part.getSource() != null);

    }

    @Override
    public int hashCode() {
        return getSource() != null ? getSource().toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return getSource();
    }
}
