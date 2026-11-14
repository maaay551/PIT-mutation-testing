package pitstudy;

import org.junit.Test;
import static org.junit.Assert.*;

/** Suite B: reaches valid and exceptional paths, but omits decision boundaries. */
public class CoverageTest {
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

}
