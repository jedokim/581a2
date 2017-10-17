package comp581a2;

// Team Members:
// Kyle Dixon (PID: 720464669)
// Jeremy Kim (PID: 720471249)

// Measurements:
// Large wheel d = 5.5cm
// Large wheel c = pi * 5.5cm

//import java.io.*;
//import java.util.*;
import java.util.concurrent.TimeUnit;
//import java.lang.*;
import lejos.hardware.*;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;

public class Main {

    static EV3MediumRegulatedMotor mA;
    static EV3MediumRegulatedMotor mB;
    static EV3TouchSensor touchSensor;
    static SensorMode touch;
    static float[] touchSample;
    static EV3UltrasonicSensor ultrasonicSensor;
    static SensorMode distance;
    static float[] distanceSample;

    public static void main(String[] args) {
        // Grab motors.
        mA = new EV3MediumRegulatedMotor(MotorPort.A);
        mB = new EV3MediumRegulatedMotor(MotorPort.B);
        mA.synchronizeWith(new EV3MediumRegulatedMotor[] { mB });

        touchSensor = new EV3TouchSensor(SensorPort.S1);
        touch = touchSensor.getTouchMode();
        touchSample = new float[touch.sampleSize()];

        ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
        distance = (SensorMode) ultrasonicSensor.getDistanceMode();
        distanceSample = new float[distance.sampleSize()];
        ultrasonicSensor.enable();

        ButtonSequencer();

        Objective1();

        ButtonSequencer();

        Objective2();

        ButtonSequencer();

        Objective3();

        ButtonSequencer();

        // close resources
        mA.close();
        mB.close();
        ultrasonicSensor.close();
        touchSensor.close();
    }

    private static void ButtonSequencer() {
        System.out.println("Press button to continue.");
        Sound.beep();
        Button.waitForAnyPress();
    }

    private static void Objective1() {
        mA.setSpeed(180);
        mB.setSpeed(180);

        float objective1_time = TimeToAchieveDistance(1.5f, 180.0f);

        mA.startSynchronization();
        mA.forward();
        mB.forward();
        mA.endSynchronization();

        try {
            TimeUnit.SECONDS.sleep((long) objective1_time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mA.setSpeed(0);
        mB.setSpeed(0);
        mA.waitComplete();
        mB.waitComplete();

        mA.stop();
        mB.stop();
        mA.waitComplete();
        mB.waitComplete();
    }

    private static void Objective2() {
        distance.fetchSample(distanceSample, 0);

        float distanceAverage = 0.0f;
        for (int i = 0; i < distance.sampleSize(); i++) {
            distanceAverage += distanceSample[i];
        }
        distanceAverage /= distance.sampleSize();

        mA.startSynchronization();
        mA.forward();
        mB.forward();
        mA.endSynchronization();

        for (int i = 0; i < 180; i++) {
            if (distanceAverage > .75) {
                mA.setSpeed(i);
                mB.setSpeed(i);
            } else
                break;
        }

        touchSample = new float[touch.sampleSize()];
        while (touchSample[0] == 0) {
            // If Infinity, don't use Sonar to break out of loop yet.
            distance.fetchSample(distanceSample, 0);
            touch.fetchSample(touchSample, 0);

            distanceAverage = 0.0f;
            for (int i = 0; i < distance.sampleSize(); i++) {
                distanceAverage += distanceSample[i] - 0.01f;
            }
            distanceAverage /= distance.sampleSize();

            // System.out.println("distanceAverage: " + distanceAverage);

            if (distanceAverage < .55) {
                mA.setSpeed(160);
                mB.setSpeed(160);
            }

            if (distanceAverage < .525) {
                mA.setSpeed(140);
                mB.setSpeed(140);
            }

            if (distanceAverage < .5) {
                mA.setSpeed(120);
                mB.setSpeed(120);
            }

            if (distanceAverage < .475) {
                mA.setSpeed(100);
                mB.setSpeed(100);
            }

            if (distanceAverage < .45) {
                System.out.println("Saw distance of " + distanceAverage + ".");
                break;
            }

            mA.forward();
            mB.forward();
        }

        for (int i = mA.getSpeed(); i > 0; i--) {
            mA.setSpeed(i);
            mB.setSpeed(i);
        }

        mA.stop();
        mB.stop();
        mA.waitComplete();
        mB.waitComplete();

        distance.fetchSample(distanceSample, 0);

        distanceAverage = 0.0f;
        for (int i = 0; i < distance.sampleSize(); i++) {
            distanceAverage += distanceSample[i];
        }
        distanceAverage /= distance.sampleSize();
        System.out.println("Ended at distance of " + distanceAverage + ".");

        ultrasonicSensor.disable();
    }

    private static void Objective3() {
        mA.setSpeed(180);
        mB.setSpeed(180);

        touchSample = new float[touch.sampleSize()];
        while (touchSample[0] == 0) {
            touch.fetchSample(touchSample, 0);

            mA.forward();
            mB.forward();

            // System.out.println("Wall not yet hit!");
        }

        mA.setSpeed(0);
        mB.setSpeed(0);
        mA.waitComplete();
        mB.waitComplete();

        mA.stop();
        mB.stop();
        mA.waitComplete();
        mB.waitComplete();

        mA.setSpeed(180);
        mB.setSpeed(180);

        float objective3_time = TimeToAchieveDistance(0.45f, 180.0f);

        mA.startSynchronization();
        mA.backward();
        mB.backward();
        mA.endSynchronization();

        try {
            TimeUnit.SECONDS.sleep((long) objective3_time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mA.setSpeed(0);
        mB.setSpeed(0);
        mA.waitComplete();
        mB.waitComplete();

        mA.stop();
        mB.stop();
        mA.waitComplete();
        mB.waitComplete();
    }

    private static float TimeToAchieveDistance(float distance, float speed) {
        float time = distance / ((.055f / 2.0f) * (speed * 0.01745329f));
        return time;
    }

}



