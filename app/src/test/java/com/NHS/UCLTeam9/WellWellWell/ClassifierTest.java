package com.NHS.UCLTeam9.WellWellWell;

public class ClassifierTest {

    static int actual;
    TensorFlowClassifier classifier;
    /*
    @Test
    public void predictWelfareTest() {
        classifier = new TensorFlowClassifier(RuntimeEnvironment.application);
        int expected = 7;
        float[] actualfloat = classifier.predictWelfare(new float[] {(float) 180000, (float) 11, (float) 2400, (float) 100, (float) 2, (float) 70, (float) 24000, (float) 1000});
        float highest = -1;
        for (int i=0; i<actualfloat.length; i++) {
            if (actualfloat[i] > highest) {
                highest = actualfloat[i];
                actual = i;
            }
        }
        assertEquals("Welfare prediction failed", expected, actual, 0.001);
    }*/
}
