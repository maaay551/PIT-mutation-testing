package pitstudy;

import org.junit.Test;
import static org.junit.Assert.*;

/** Suite C: keeps Suite B tests and adds targeted boundary conditions. */
public class MutationTest {
    private final OrderRules rules = new OrderRules();

    @Test
    public void paidShipping() {
        assertEquals(5, rules.shippingFee(30));
    }

    @Test
    public void freeShipping() {
        assertEquals(0, rules.shippingFee(70));
    }

    @Test
    public void noDiscount() {
        assertEquals(60, rules.discountedTotal(60));
    }

    @Test
    public void discountApplied() {
        assertEquals(135, rules.discountedTotal(150));
    }

    @Test
    public void couponForMember() {
        assertTrue(rules.couponEligible(90, true));
    }

    @Test
    public void couponForNonmember() {
        assertFalse(rules.couponEligible(90, false));
    }

    @Test
    public void loyaltyForNormalOrder() {
        assertEquals(8, rules.loyaltyPoints(80));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeShipping() {
        rules.shippingFee(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeDiscount() {
        rules.discountedTotal(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeCoupon() {
        rules.couponEligible(-1, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeLoyalty() {
        rules.loyaltyPoints(-1);
    }

    @Test
    public void couponForLowSubtotal() {
        assertFalse(rules.couponEligible(60, true));
    }

    @Test
    public void loyaltyForZero() {
        assertEquals(0, rules.loyaltyPoints(0));
    }

    @Test
    public void shippingExactlyAtThreshold() {
        assertEquals(0, rules.shippingFee(50));
    }

    @Test
    public void shippingJustBelowThreshold() {
        assertEquals(5, rules.shippingFee(49));
    }

    @Test
    public void discountExactlyAtThreshold() {
        assertEquals(90, rules.discountedTotal(100));
    }

    @Test
    public void discountJustBelowThreshold() {
        assertEquals(99, rules.discountedTotal(99));
    }

    @Test
    public void couponExactlyAtThreshold() {
        assertTrue(rules.couponEligible(75, true));
    }

    @Test
    public void couponJustBelowThreshold() {
        assertFalse(rules.couponEligible(74, true));
    }

    @Test
    public void loyaltyAtFirstPoint() {
        assertEquals(1, rules.loyaltyPoints(10));
    }

    @Test
    public void loyaltyJustBeforeFirstPoint() {
        assertEquals(0, rules.loyaltyPoints(9));
    }

}
