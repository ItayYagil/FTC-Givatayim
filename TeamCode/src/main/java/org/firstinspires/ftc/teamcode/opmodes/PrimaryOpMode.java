package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.lift.LiftActions;

import com.arcrobotics.ftclib.controller.PIDController;

import java.util.ArrayList;
import java.util.List;


@Config
@TeleOp(name = "PrimaryOpMode")
public class PrimaryOpMode extends LinearOpMode {

    // private FtcDashboard dash = FtcDashboard.getInstance();
    // private List<Action> runningActions = new ArrayList<>();

    public static class Params {
        public double speedMult      = 1;
        public double turnMult       = 1;

        public double backMotorMult  = 1;
        public double frontMotorMult = 1;

        public double kP             = 2;
        public double kI             = 0.1;
        public double kD             = 0.2;

        public double power        = 1;
    }
    public static Params PARAMS = new Params();

    @Override
    public void runOpMode() throws InterruptedException {
        // TelemetryPacket packet = new TelemetryPacket();

        CRServo rightClawArmServo      = hardwareMap.get(CRServo.class, "rightClawArmServo");
        CRServo leftClawArmServo       = hardwareMap.get(CRServo.class, "leftClawArmServo");
        CRServo clawServo              = hardwareMap.get(CRServo.class, "clawServo");
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor         = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeftMotor          = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRightMotor        = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRightMotor         = hardwareMap.dcMotor.get("backRight");
        
        DcMotor leftElevatorMotor      = hardwareMap.get(DcMotor.class, "armLeft");
        DcMotor rightElevatorMotor     = hardwareMap.get(DcMotor.class, "armRight");
        DcMotor frontArmMotor          = hardwareMap.get(DcMotor.class, "frontArm");

        // Reset the motor encoder so that it reads zero ticks
        frontLeftMotor      .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor       .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor     .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor      .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftElevatorMotor   .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightElevatorMotor  .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Turn the motor back on, required if you use STOP_AND_RESET_ENCODER
        frontLeftMotor      .setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor       .setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor     .setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor      .setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeftMotor      .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor       .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor     .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor      .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //For manual control without Actions
        leftElevatorMotor   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightElevatorMotor  .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontLeftMotor      .setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor       .setDirection(DcMotorSimple.Direction.REVERSE);
        
        rightElevatorMotor  .setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");

        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.UP));

        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        imu.resetYaw();

        PIDController pid = new PIDController(PARAMS.kP, PARAMS.kI, PARAMS.kD);
        // LiftActions lift = new LiftActions(hardwareMap);

        waitForStart();

        if (isStopRequested()) return;

        double wantedAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        pid.setSetPoint(wantedAngle);
        boolean isTurning  = false;
        double botHeading  = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        int rightElevatorMotorStartingPos = rightElevatorMotor.getCurrentPosition();
        int leftElevatorMotorStartingPos  = leftElevatorMotor.getCurrentPosition();

        while (opModeIsActive()) {

            /* ##################################################
                            Inputs and Initializing
               ################################################## */

            double currentHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            botHeading = unwrapAngle(botHeading, currentHeading); // Use unwrapping here

            double y  = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x  = 1.1 * -gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;


            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.y)
                imu.resetYaw();

            /* ##################################################
                        Movement Controls Calculations
               ################################################## */

            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

            rotX *= PARAMS.speedMult;
            rotY *= PARAMS.speedMult;
            rx   *= PARAMS.turnMult;

            /* ##################################################
                                   Rotation
               ################################################## */
            double fixError = pid.calculate(botHeading);
            if (rx == 0 && isTurning) {
                wantedAngle = botHeading % (Math.PI*2);
                isTurning = false;
            }
            else if (rx != 0 && !isTurning) {
                isTurning = true;
            }

            //makes sure the robot doesn't fix small angles
            if (Math.abs(Math.toDegrees(botHeading-wantedAngle)) > 6 && !isTurning){
                rx -= fixError;
            }

            pid.setSetPoint(wantedAngle);

            if (gamepad1.dpad_up)
                wantedAngle = 0;
            if (gamepad1.dpad_right)
                wantedAngle -= 0.5 * Math.PI;
            if (gamepad1.dpad_left)
                wantedAngle += 0.5 * Math.PI;

            /* ##################################################
                                    Elevator
               ################################################## */

            //controls the elevator
            if (gamepad1.left_bumper) {
                // runningActions.add(lift.liftDown());
                setElevatorPower(leftElevatorMotor, rightElevatorMotor, PARAMS.power);
            } else if (gamepad1.right_bumper) {
                // runningActions.add(lift.liftUp());
                setElevatorPower(leftElevatorMotor, rightElevatorMotor, -PARAMS.power);
            } else setElevatorPower(leftElevatorMotor, rightElevatorMotor, 0);

            /* ##################################################
                                  Front Arm
            ################################################## */
            
            double frontArmMult = 0;
            if (gamepad1.right_trigger > 0 && gamepad1.left_trigger == 0) {
                frontArmMult = gamepad1.right_trigger;
                setFrontArmPower(frontArmMotor, PARAMS.power*frontArmMult);
            } else if(gamepad1.left_trigger > 0 && gamepad1.right_trigger == 0) {
                frontArmMult = gamepad1.left_trigger;
                setFrontArmPower(frontArmMotor, -PARAMS.power*frontArmMult);
            } else setFrontArmPower(frontArmMotor, 0);
            
            /* ##################################################
                                    Claw
            ################################################## */

            boolean isClawArmDown = false;
            boolean isClawClosed = false;

            if(gamepad1.b){
                if (isClawArmDown) {
                    setClawArmPower(0.5f, rightClawArmServo, leftClawArmServo);
                    isClawArmDown = false;
                } else {
                    setClawArmPower(-0.5f, rightClawArmServo,leftClawArmServo);
                    isClawArmDown = true;
                }
            }

            if(gamepad1.a){
                if(!isClawClosed){
                    setClawPower(0.5f, clawServo);
                    isClawClosed = true;
                } else{
                    setClawPower(-0.5f, clawServo);
                    isClawClosed = false;
                }
            }

/*           ######################################################
             Runs Autonomous Actions in TeleOp - CURRENTLY DISABLED
             ###################################################### */

//            // update running actions
//            List<Action> newActions = new ArrayList<>();
//            for (Action action : runningActions) {
//                action.preview(packet.fieldOverlay());
//                if (action.run(packet)) {
//                    newActions.add(action);
//                }
//            }
//            runningActions = newActions;
//
//            dash.sendTelemetryPacket(packet);

            /* ##################################################
                     Applying the Calculations to the Motors
               ################################################## */

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator     = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower  = PARAMS.frontMotorMult * (rotY + rotX + rx) / denominator;
            double backLeftPower   = PARAMS.backMotorMult * (rotY - rotX + rx) / denominator;
            double frontRightPower = PARAMS.frontMotorMult * (rotY - rotX - rx) / denominator;
            double backRightPower  = PARAMS.backMotorMult *(rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);


            /* ##################################################
                             TELEMETRY ADDITIONS
               ################################################## */

            telemetry.addData("Angle", Math.toDegrees(botHeading));
            telemetry.addData("rx:", rx);
            telemetry.addData("wanted angle",wantedAngle);
            telemetry.addData("bot heading", botHeading);
            telemetry.addData("x", x);
            telemetry.addData("y", y);
            telemetry.update();
        }
    }
    public void setClawArmPower(float power, CRServo rightServo, CRServo leftServo){
        rightServo.setPower(power);
        leftServo.setPower(power);
    }
    public void setClawPower(float power, CRServo clawServo){ clawServo.setPower(power); }

    private double unwrapAngle(double previousAngle, double currentAngle) {
        double delta = currentAngle - previousAngle;
        if (delta > Math.PI) {
            delta -= 2 * Math.PI;
        } else if (delta < -Math.PI) {
            delta += 2 * Math.PI;
        }
        return previousAngle + delta;
    }

    public void setElevatorPower(DcMotor left, DcMotor right, double power) {
        left.setPower(power);
        right.setPower(power);
    }
    
    public void setFrontArmPower(DcMotor arm, double power) {
        arm.setPower(power);
    }
}