package org.firstinspires.ftc.teamcode.new3208;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.oldcode.Lululu;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;
import java.util.Timer;

@Autonomous
public class BlueAutoClose extends LinearOpMode {



    AprilTagProcessor aprilTag;
    TfodProcessor tfod;
    VisionPortal myVisionPortal;

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "model_20231121_000004.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "Blue hat","Red hat"
    };

    private int objectPos = 3;

    static final double webcamMidX = 300;

    @Override
    public void runOpMode(){
        SampleMecanumDrive  drive = new SampleMecanumDrive(hardwareMap);
        Lululu              robot = new Lululu(this);

        Pose2d startPos = new Pose2d(-31.125, 64.4375, Math.toRadians(90));

        drive.setPoseEstimate(startPos);

        Trajectory toRight = drive.trajectoryBuilder(startPos,true)
                .splineTo(new Vector2d(12,40),Math.toRadians(90),
                        SampleMecanumDrive.getVelocityConstraint(25, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .splineTo(new Vector2d(4,45),Math.toRadians(110),
                        SampleMecanumDrive.getVelocityConstraint(25, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory outRight = drive.trajectoryBuilder(toRight.end(),false)
                .forward(6)
                .build();

        Trajectory scoreRight = drive.trajectoryBuilder(outRight.end(),false)
                .lineToLinearHeading(new Pose2d(53,32,Math.toRadians(180)))
                .build();

        Trajectory toMiddle = drive.trajectoryBuilder(startPos,true)
                .splineTo(new Vector2d(12,34),Math.toRadians(-90),
                        SampleMecanumDrive.getVelocityConstraint(25, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory outMiddle = drive.trajectoryBuilder(toMiddle.end())
                .splineToConstantHeading(new Vector2d(15,45),Math.toRadians(110))
                .build();

        Trajectory scoreMiddle = drive.trajectoryBuilder(outMiddle.end(),true)
                .lineToLinearHeading(new Pose2d(53,38,Math.toRadians(180)))
                .build();

        Trajectory toLeft = drive.trajectoryBuilder(startPos,true)
                .splineTo(new Vector2d(23,40),Math.toRadians(-90),
                        SampleMecanumDrive.getVelocityConstraint(25, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory outLeft = drive.trajectoryBuilder(toLeft.end())
                .splineTo(new Vector2d(15,55),Math.toRadians(120))
                .build();

        Trajectory scoreLeft = drive.trajectoryBuilder(outLeft.end(),true)
                .lineToLinearHeading(new Pose2d(53,44,Math.toRadians(180)))
                .build();

        Trajectory parkRight = drive.trajectoryBuilder((scoreLeft.end()),true)
                .lineToConstantHeading(new Vector2d(53,64))
                .build();

        robot.init();

        telemetry.addData("Ready for W", "");
        telemetry.update();



        waitForStart();

        drive.followTrajectory(toRight);
        drive.followTrajectory(outRight);
        drive.followTrajectory(scoreRight);
//        robot.setLiftPosition(5000,.3);
//        robot.toScoringPosition();
//        robot.openLowerClaw(true);
//        robot.openLowerClaw(false);
//        robot.neutralPosition();
//        robot.setLiftPosition(0,.3);
        drive.followTrajectory(parkRight);


//        if(objectPos == 1){
//
//        }
//        else{
//            drive.followTrajectory(toRight);
//            drive.followTrajectory(outRight);
//            drive.followTrajectory(scoreRight);
//        }
    }



//    private void initDoubleVision() {
//        // -----------------------------------------------------------------------------------------
//        // AprilTag Configuration
//        // -----------------------------------------------------------------------------------------
//
//        aprilTag = new AprilTagProcessor.Builder()
//                .build();
//
//        // -----------------------------------------------------------------------------------------
//        // TFOD Configuration
//        // -----------------------------------------------------------------------------------------
//
//        tfod = new TfodProcessor.Builder()
//                .setModelAssetName(TFOD_MODEL_ASSET)
//                .setModelLabels(LABELS)
//                .build();
//
//        // -----------------------------------------------------------------------------------------
//        // Camera Configuration
//        // -----------------------------------------------------------------------------------------
//
//
//        myVisionPortal = new org.firstinspires.ftc.vision.VisionPortal.Builder()
//                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
//
//                .addProcessors(tfod, aprilTag)
//                .build();
//
//    }   // end initDoubleVision()
//
//    private int detectObject() {
//        List<Recognition> currentRecognitions = tfod.getRecognitions();
//        telemetry.addData("# Objects Detected", currentRecognitions.size());
//
//        // Step through the list of recognitions and display info for each one.
//        for (Recognition recognition : currentRecognitions) {
//            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
//            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;
//
//            telemetry.addData(""," ");
//            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
//            telemetry.addData("- Position", "%.0f / %.0f", x, y);
//            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
//            telemetry.update();
//
//            if(x < 300){
//                return 1;
//            }
//            if(x >300){
//                return 2;
//            }
//        }   // end for() loop
//        return 3;
//
//    }
}