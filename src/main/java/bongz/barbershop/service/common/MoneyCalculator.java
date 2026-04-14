package bongz.barbershop.service.common;

public final class MoneyCalculator {

    private MoneyCalculator() {
    }

    public static int calculateBarberEarningAmount(int chargedAmountPesos, int barberCommissionPercent) {
        return (int) Math.round(chargedAmountPesos * (barberCommissionPercent / 100.0));
    }

    public static int calculateShopEarningAmount(int chargedAmountPesos, int barberEarningAmountPesos) {
        return chargedAmountPesos - barberEarningAmountPesos;
    }
}
