package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.acmerobotics.dashboard.config.Config;

@Config
@TeleOp(name = "MechanumExample")


public class MechanumExample extends LinearOpMode {
    public static class Params {
        public double maxSpeed = 1;
        public double turnMult = 1;
    }
    public static Params PARAMS = new Params();


    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRight");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double leftStickY = -gamepad1.left_stick_y * PARAMS.maxSpeed; // Remember, leftStickY stick value is reversed
            double leftStickX = -gamepad1.left_stick_x * PARAMS.maxSpeed; // Counteract imperfect strafing
            double rightStickX =  gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(leftStickY) + Math.abs(leftStickX) + Math.abs(rightStickX), 1);
            double frontLeftPower = (leftStickY + leftStickX + rightStickX) / denominator;
            double backLeftPower = (leftStickY - leftStickX + rightStickX) / denominator;
            double frontRightPower = (leftStickY - leftStickX - rightStickX) / denominator;
            double backRightPower = (leftStickY + leftStickX - rightStickX) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
        }


    }
}