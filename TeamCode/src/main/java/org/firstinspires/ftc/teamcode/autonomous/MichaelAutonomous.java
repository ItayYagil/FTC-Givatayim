package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class MichaelAutonomous extends LinearOpMode {

  private DcMotor frontLeftMotor;
//  private DcMotor backLeftMotor;
//  private DcMotor frontRightMotor;
  private DcMotor backRightMotor;

  // Convert from the counts per revolution of the encoder to counts per inch
  static final double HD_COUNTS_PER_REV = 28;
  static final double DRIVE_GEAR_REDUCTION = 20.15293;
  static final double WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
  static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
  static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;

  @Override
  public void runOpMode() {

    // Initialize motors
    frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
//    backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
//    frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
    backRightMotor = hardwareMap.get(DcMotor.class, "backRight");

    // Reverse right side motors
//    frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//    backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

    waitForStart();

    if (opModeIsActive()) {
      // Set target positions for moving left (negative on left motors, positive on right motors)
      int rightTarget = backRightMotor.getCurrentPosition() + (int)(15 * DRIVE_COUNTS_PER_IN);  // Move right motor forward
      int leftTarget = frontLeftMotor.getCurrentPosition() + (int)(15 * DRIVE_COUNTS_PER_IN);    // Move left motor backward

      // Set target position for all motors
      frontLeftMotor.setTargetPosition(leftTarget);
//      backLeftMotor.setTargetPosition(leftTarget);
//      frontRightMotor.setTargetPosition(rightTarget);
      backRightMotor.setTargetPosition(rightTarget);

      // Switch to RUN_TO_POSITION mode
      frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//      backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//      frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

      // Set power to move the robot
      frontLeftMotor.setPower(0.5);
//      backLeftMotor.setPower(0.5);
//      frontRightMotor.setPower(0.5);
      backRightMotor.setPower(0.5);

      // Wait until all motors finish moving
      while (opModeIsActive() &&
              (frontLeftMotor.isBusy()  || backRightMotor.isBusy())) {
      }

      // Stop motors after moving
      frontLeftMotor.setPower(0);
//      backLeftMotor.setPower(0);
//      frontRightMotor.setPower(0);
      backRightMotor.setPower(0);
    }
  }
}
