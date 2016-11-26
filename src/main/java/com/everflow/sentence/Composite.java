package com.everflow.sentence;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ildar Gafarov on 13.05.16.
 */
public abstract class Composite<P extends Part, C extends Composite> extends Part<C> {

    private List<P> parts = new LinkedList<>();

    public Composite() {
    }

    public Composite(List<P> parts) {
        this.parts = parts;
    }

    public List<P> getParts() {
        return parts;
    }

    public void removeAll(List<P> parts) {
        for(P part : parts) {
            remove(part.getSource());
        }
    }

    public boolean remove(String pattern) {
        P toRemove = null;
        for(P part : parts) {
            if(part.getSource().equalsIgnoreCase(pattern)) {
                toRemove = part;
                break;
            }
        }
        if(toRemove != null) {
            return parts.remove(toRemove);
        }
        return false;
    }

    public void add(P part) {
        this.parts.add(part);
    }

    public void add(Composite<P, C> composite) {
        this.parts.addAll(composite.getParts());
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    @Override
    public String getSource() {
        StringBuilder builder = new StringBuilder();
        Iterator<P> iter = parts.iterator();
        while(iter.hasNext()) {
            String source = iter.next().getSource();
            builder.append(source);
            if(iter.hasNext()) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }



}
