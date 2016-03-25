package net.polybugger.apollot.db;

public class FinalGradeCalculationPrefAdapter {

    public enum FinalGradeCalculationType {
        ONE_TO_FIVE(0),
        A_TO_F(1),
        FOUR_TO_ONE(2);

        private static final FinalGradeCalculationType[] sFinalGradeCalculationTypeValues = FinalGradeCalculationType.values();

        public static FinalGradeCalculationType fromInteger(int x) {
            if(x < 0 || x > 2)
                return ONE_TO_FIVE;
            return sFinalGradeCalculationTypeValues[x];
        }

        private int mValue;

        private FinalGradeCalculationType(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

}
