package se.vgregion.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 11/10-11
 * Time: 17:12
 */
public class ComparableMatcher extends TypeSafeMatcher<Comparable> {

    private enum ComparisonType {
        GT, GT_EQ, LT, LT_EQ
    }

    private Comparable that;
    private ComparisonType type;

    public ComparableMatcher(Comparable that, ComparisonType type) {
        this.that = that;
        this.type = type;
    }

    @Override
    public boolean matchesSafely(Comparable item) {
        switch (type) {
            case GT:
                return item.compareTo(that) > 0;
            case GT_EQ:
                return item.compareTo(that) >= 0;
            case LT:
                return item.compareTo(that) < 0;
            case LT_EQ:
                return item.compareTo(that) <= 0;
            default:
                return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("comparable match");
    }

    @Factory
    public static <T> Matcher<Comparable> greaterThan(Comparable that) {
        return new ComparableMatcher(that, ComparisonType.GT);
    }

    @Factory
    public static <T> Matcher<Comparable> greaterThanOrEqual(Comparable that) {
        return new ComparableMatcher(that, ComparisonType.GT_EQ);
    }

    @Factory
    public static <T> Matcher<Comparable> lessThan(Comparable that) {
        return new ComparableMatcher(that, ComparisonType.LT);
    }

    @Factory
    public static <T> Matcher<Comparable> lessThanOrEqual(Comparable that) {
        return new ComparableMatcher(that, ComparisonType.LT_EQ);
    }
}
