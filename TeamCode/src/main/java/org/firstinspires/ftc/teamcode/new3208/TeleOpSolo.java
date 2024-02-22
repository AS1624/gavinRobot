package org.firstinspires.ftc.teamcode.new3208;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp
public class TeleOpSolo extends LinearOpMode {
    ScoringMechanisms lu3 = new ScoringMechanisms(this);
    AprilTagProcessor aprilTag;
    VisionSystem vision;

    boolean armOut = false;
    boolean clawDown = false;
    boolean upperClawOpen = false;
    boolean lowerClawOpen = false;
    boolean fc = false;

    //state 1 = intaking position: arm down, claws open, wrist down
    //state 2 = driving position: arm down, claws closed, wrist up
    //state 3 = outtaking position: arm out, claws ready to ope, wrist flipped
    int state = 2;
    //armHeight 0 = ground
    //armHeight 1 = 1 pixel off the ground to stack
    //arm height 2 = 2 pixels off the ground to pick up top of a stack
    int armHeight = 0;

    boolean lastArmButtonState;
    boolean lastWristButton;
    boolean liftHoming;
    boolean inStackSequence;
    boolean lastStackButton;

    ElapsedTime flipDelay = new ElapsedTime();
    ElapsedTime armDelay = new ElapsedTime();
    ElapsedTime stackDelay = new ElapsedTime();
    ElapsedTime armDelay2 = new ElapsedTime();
    SampleMecanumDrive drive;
    double fwd;
    double strafe;
    double turn;

    boolean bottomDropped = false;
    boolean topDropped = false;



    @Override
    public void runOpMode(){


        lu3.init();
        drive = new SampleMecanumDrive(hardwareMap);
        boolean switchToDrive = false;
        boolean leaveScoring = false;

        waitForStart();
        while(opModeIsActive()) {
            fwd       = -gamepad2.left_stick_y;
            strafe      = -gamepad2.left_stick_x;
            turn        = -gamepad2.right_stick_x;

            boolean grabStack   = gamepad2.b;

            double lift        = -gamepad2.left_stick_y;
            boolean closeClaws  = gamepad2.b;
            boolean openTop     = gamepad2.y;
            boolean openBottom  = gamepad2.a;
            boolean flipArm     = gamepad2.left_bumper;

            //intaking state
            if(state ==1) {
                if(fc){
                    driveFieldCentric();
                }
                else{
                    driveRobotCentric();
                }
                telemetry.addData("",switchToDrive);
                telemetry.update();
                if(gamepad2.right_bumper){
                    switchToDrive = true;
                }
                if(!lu3.isPixelIn() && !switchToDrive) {
                    lu3.openLowerClaw(true);
                    lu3.openUpperClaw(true);
                    flipDelay.reset();
                }
                lu3.wrist.setPosition(lu3.WRIST_INTAKE_POSITION);

                if(gamepad2.dpad_down){
                    armHeight = 0;
                }
                else if(gamepad2.dpad_right || gamepad2.dpad_left){
                    armHeight = 1;
                }
                else if(gamepad2.dpad_up){
                    armHeight = 2;
                }

                if(armHeight == 0) {
                    //disable arm to grab pixels on the ground
                    lu3.disableArm();
                }
                else if(armHeight == 1){
                    //adjust to height where claw is higher than 1 pixel
                    lu3.enableArm();
                    lu3.setArmPosition(lu3.ARM_1_PIXEL_HEIGHT);
                }
                else{
                    //adjust to 5 pixel height to pick up from stacks
                    lu3.enableArm();
                    lu3.setArmPosition(lu3.ARM_3_PIXEL_HEIGHT);
                }

                //switch to driving state
                if(lu3.isPixelIn() || switchToDrive){
                    lu3.openLowerClaw(false);
                    lu3.openUpperClaw(false);
                    if(flipDelay.time() > .5) {
                        state = 2;
                        switchToDrive = false;
                    }
                }
            }
            //driving state
            else if(state == 2){
                if(fc){
                    driveFieldCentric();
                }
                else{
                    driveRobotCentric();
                }
                telemetry.addData("a button", grabStack);
                telemetry.update();
                if(grabStack){
                    lu3.enableArm();
                    lu3.setArmPosition(lu3.ARM_1_PIXEL_HEIGHT - 0.01);
                    lu3.wrist.setPosition(lu3.WRIST_INTAKE_POSITION);
                }
                if(fallingEdge(grabStack,lastStackButton)){
                    inStackSequence = true;
                }
                if(inStackSequence){
                    stackGrab();
                }
                else {
                    stackDelay.reset();
                    if(!grabStack) {
                        lu3.disableArm();
                        lu3.wrist.setPosition(lu3.WRIST_UPWARD_POSITION);

                        lu3.openLowerClaw(false);
                        lu3.openUpperClaw(false);
                    }
                }
                //switch to intake state
                if(gamepad2.a){
                    armHeight = 0;
                    state = 1;
                }
                //switch to outtake state
                if(gamepad2.right_bumper){
                    state = 3;
                }
                armDelay.reset();
                lastStackButton = grabStack;
            }

            /*
             *   outtake state
             */

            else{
                if(!leaveScoring) {
                    lu3.setArmPosition(0.1);
                    if(gamepad1.a){
                        driveScoring();
                    }
                    else if(fc){
                        driveFieldCentric();
                    }
                    else{
                        driveRobotCentric();
                    }
                }

                if(armDelay.time() > 1){


                    if(openBottom){
                        bottomDropped = true;
                    }
                    if(openTop){
                        topDropped = true;
                    }
                    lu3.openUpperClaw(topDropped);
                    lu3.openLowerClaw(bottomDropped);
                    boolean bothDropped = topDropped && bottomDropped;

                    if(gamepad2.right_bumper || bothDropped){
                        leaveScoring = true;
                    }
                    if(leaveScoring){
                        if(fc) {
                            driveFieldCentric();
                        }
                        else{
                            driveRobotCentric();
                        }
                        lu3.setArmPosition(.97);
                        lu3.wrist.setPosition(lu3.WRIST_UPWARD_POSITION);
                        if(armDelay2.time() > .25){
                            topDropped = false;
                            bottomDropped = false;
                        }
                        if(armDelay2.time() > 1.5){
                            leaveScoring = false;
                            state = 2;
                        }
                    }
                    else{
                        lu3.wrist.setPosition(lu3.WRIST_SCORING_POSITION);
                        armDelay2.reset();
                    }
                }



            }
            lastArmButtonState = flipArm;
        }
    }

    public boolean risingEdge(boolean input, boolean lastInput){
        return input && !lastInput;
    }
    public boolean fallingEdge(boolean input, boolean lastInput){
        return !input && lastInput;
    }

    public void stackGrab(){
        //bring claw down on top of pixel
        lu3.disableArm();
        if(stackDelay.time() < .5) {
            lu3.openLowerClaw(false);
            lu3.openUpperClaw(true);
            lu3.wrist.setPosition(lu3.WRIST_INTAKE_POSITION);
        }
        else if(stackDelay.time() < 1) {
            //open claws
            lu3.openUpperClaw(true);
            lu3.openLowerClaw(true);
        }
        else if(stackDelay.time() < 1.5) {
            //close
            lu3.openLowerClaw(false);
            lu3.openUpperClaw(false);
        }
        else if(stackDelay.time() < 2) {
            //delay then bring back up
            lu3.wrist.setPosition(lu3.WRIST_UPWARD_POSITION);
        }
        else{
            inStackSequence = false;
        }

    }
    public void driveScoring(){
        // Read pose
        Pose2d poseEstimate = RobotPose.currentPose;
        double error;
        double rotError;
        double kp = .1;
        double rotKp = .1;

        if(vision.getTagDistance() != -1){
            error = vision.getTagDistanceError();
        }
        else{
            error = 0;
        }
        if(vision.getTagAngle() != 0){
            rotError = vision.getTagAngleError();
        }
        else{
            rotError = 0;
        }

        // Create a vector from the gamepad x/y inputs
        // Then, rotate that vector by the inverse of that heading
        Vector2d input = new Vector2d(
                fwd,
                (error * kp)
        ).rotated(-poseEstimate.getHeading());

        // Pass in the rotated input + right stick value for rotation
        // Rotation is not part of the rotated input thus must be passed in separately
        drive.setWeightedDrivePower(
                new Pose2d(
                        input.getX(),
                        input.getY(),
                        rotError * rotKp
                )
        );

        // Update everything. Odometry. Etc.
        drive.update();
    }
    public void driveRobotCentric(){
        drive.setWeightedDrivePower(
                new Pose2d(
                        fwd,
                        strafe,
                        turn
                )
        );
    }



    public void driveFieldCentric(){
        // Read pose
        Pose2d poseEstimate = RobotPose.currentPose;

        // Create a vector from the gamepad x/y inputs
        // Then, rotate that vector by the inverse of that heading
        Vector2d input = new Vector2d(
                fwd,
                -strafe
        ).rotated(-poseEstimate.getHeading());

        // Pass in the rotated input + right stick value for rotation
        // Rotation is not part of the rotated input thus must be passed in separately
        drive.setWeightedDrivePower(
                new Pose2d(
                        input.getX(),
                        input.getY(),
                        turn
                )
        );

        // Update everything. Odometry. Etc.
        drive.update();
    }


}