package com.everflow.grammar;

import java.util.*;

/**
 * @author Ildar Gafarov
 */
public enum Rule {
    TO_BE("Глагол to be", "tobe", 1),
    PRESENT_SIMPLE("Настоящее простое время", "prsm", 2),
    ART("Артикли", "art", 5),
    PLURAL("Множественное число", "plural", 6),
    THERE_IS("There is + ед. ч.", "thereis", 3),
    THERE_ARE("There are + мн. ч.", "thereare", 4),
    PRESENT_CONT("Время Present Continuos", "prcont", 7),
    PAST_SIMPLE("Прошедшее простое время", "pastsim", 8),
    FUTURE_SIMPLE("Будущее простое время", "futsim", 9);


    private String title;
    private String code;
    int order;

    Rule(String title, String code, int order) {
        this.title = title;
        this.code = code;
        this.order = order;
    }

    public static List<Rule> getOrdered() {
        List<Rule> list = Arrays.asList(values());
        return order(list);
    }

    public static List<Rule> order(Collection<Rule> rules) {
        List<Rule> list = new ArrayList<>(rules);
        Collections.sort(list, new Comparator<Rule>() {
            @Override
            public int compare(Rule o1, Rule o2) {
                return o1.order - o2.order;
            }
        });
        return list;
    }

    public static Rule findByCode(String code) {
        if(code != null && !code.isEmpty()) {
            for(Rule rule : values()) {
                if(rule.getCode().equals(code.toLowerCase())) {
                    return rule;
                }
            }
        }

        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public int getOrder() {
        return order;
    }
}
