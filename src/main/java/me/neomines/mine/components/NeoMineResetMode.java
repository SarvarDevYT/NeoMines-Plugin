package me.neomines.mine.components;

public enum NeoMineResetMode {
    TIME,
    PERCENTAGE,
    TIME_PERCENTAGE;

    public NeoMineResetMode next() {
        switch (this) {
            case TIME:
                return PERCENTAGE;
            case PERCENTAGE:
                return TIME_PERCENTAGE;
            case TIME_PERCENTAGE:
            default:
                return TIME;
        }
    }
}
