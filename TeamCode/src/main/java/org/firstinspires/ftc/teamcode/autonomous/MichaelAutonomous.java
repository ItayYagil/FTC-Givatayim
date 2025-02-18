package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class MichaelAutonomous extends LinearOpMode {

  private DcMotor RightDrive;
  private DcMotor LeftDrive;
  private DcMotor Arm;
  private DcMotor Intake;
  
  // Convert from the counts per revolution of the encoder to counts per inch
  static final double HD_COUNTS_PER_REV = 28;
  static final double DRIVE_GEAR_REDUCTION = 20.15293;
  static final double WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
  static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
  static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;
    
  @Override
  public void runOpMode() {

    RightDrive = hardwareMap.get(DcMotor.class, "RightDrive");
    LeftDrive = hardwareMap.get(DcMotor.class, "LeftDrive");
    Arm = hardwareMap.get(DcMotor.class, "Arm");
    Intake = hardwareMap.get(DcMotor.class, "Intake");

    // Reverse left drive motor direction
    LeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    
    waitForStart();
    if (opModeIsActive()) {
      // Create target positions for a left turn
      // Here, the left motor will turn backward, and the right motor will move forward for the robot to turn left
      int rightTarget = RightDrive.getCurrentPosition() + (int)(15 * DRIVE_COUNTS_PER_IN);  // Move right motor forward
      int leftTarget = LeftDrive.getCurrentPosition() - (int)(15 * DRIVE_COUNTS_PER_IN);   // Move left motor backward
      
      // Set target position for both motors
      LeftDrive.setTargetPosition(leftTarget);
      RightDrive.setTargetPosition(rightTarget);
      
      // Switch to RUN_TO_POSITION mode
      LeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      RightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      
      // Run to position at the designated power
      LeftDrive.setPower(0.5);
      RightDrive.setPower(0.5);
      
      // Wait until both motors are no longer busy running to position
      while (opModeIsActive() && (LeftDrive.isBusy() || RightDrive.isBusy())) {
      }
      
      // Set motor power back to 0
      LeftDrive.setPower(0);
      RightDrive.setPower(0);
    }
  }
}
