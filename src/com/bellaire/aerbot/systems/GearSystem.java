package com.bellaire.aerbot.systems;

import com.bellaire.aerbot.Environment;
import com.bellaire.aerbot.input.InputMethod;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;

public class GearSystem implements RobotSystem {

	public static final double FIRST_TO_SECOND_SPEED = 0;
	public static final double SECOND_TO_THIRD_SPEED = 0;
	public static final double THIRD_TO_FOURTH_SPEED = 0;
	public static final double UPSHIFT_THROTTLE = .7;
	public static final double SHIFT_DELAY = .5;

	private int gear; // first gear is 1 fourth is 4
	private Timer timer;

	private AccelerometerSystem accelerometer;
	private InputMethod input;
	private Relay gearbox1;
	private Relay gearbox2;

	public void init(Environment environment) {
		accelerometer = environment.getAccelerometerSystem();
		input = environment.getInput();
		//make the gearbox objects with port numbers

		//shift to first gear
		gearbox1.set(Relay.Value.kOff);
		gearbox2.set(Relay.Value.kOff);
		gear = 1;

		timer = new Timer();
		timer.start();
	}

	public void destroy() {
		gearbox1.free();
		gearbox2.free();
	}

	public void shift(int targetGear) {
		//shifts to the target gear regardless of accelerometer, timer, etc.
		switch (targetGear) {
		case 1:
			gearbox1.set(Relay.Value.kOff);
			gearbox2.set(Relay.Value.kOff);
			break;
		case 2:
			gearbox1.set(Relay.Value.kOff);
			gearbox2.set(Relay.Value.kForward);
			break;
		case 3:
			gearbox1.set(Relay.Value.kForward);
			gearbox2.set(Relay.Value.kOff);
		case 4:
			gearbox1.set(Relay.Value.kForward);
			gearbox2.set(Relay.Value.kForward);
		}
		gear = targetGear;
	}

	public void autoshift() {
		//find which gear the bot should be in
		int targetGear = gear;
		double speed = Math.abs(accelerometer.getSpeed());
		if (speed > THIRD_TO_FOURTH_SPEED)
			targetGear = 4;
		else if (speed > SECOND_TO_THIRD_SPEED)
			targetGear = 3;
		else if (speed > FIRST_TO_SECOND_SPEED)
			targetGear = 2;
		else
			targetGear = 1;

		/*
		 * only shift if bot is on the wrong gear, once every half second and
		 * only upshift if the throttle is high and bot isn't turning
		 */
		if (gear != targetGear
				&& timer.get() > SHIFT_DELAY
				&& Math.abs(input.getRightX()) < 14
				&& (targetGear < gear || Math.abs(input.getLeftY()) > UPSHIFT_THROTTLE)) {
			shift(targetGear);
			timer.reset();
		}
	}

}
