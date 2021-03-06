package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.util.ExpoScale;
import org.strykeforce.thirdcoast.util.RateLimit;

public class VisionAutoAlignPickupCommand extends Command {
  public static final double kP_STRAFE = 0.1; // 0.1
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method, 0.01 pyeye
  private static final double MAX_YAW = 0.3;
  private static final double goodEnoughYaw = 1.5;
  private static final double DRIVE_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double MIN_RANGE = 35.0;
  private static final double FWD_SCALE = 0.5;
  private static final double FWD_SCALE_FAST = 0.5;
  private static final double AUTON_OUTPUT = 0.30;

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;

  private final ExpoScale driveExpo;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double range;
  private DriverControls controls;
  private double strafeCorrection;
  private boolean isAuton;
  private double targetYaw;
  private boolean isGood = false;
  private RateLimit strafeRateLimit;

  public VisionAutoAlignPickupCommand() {
    requires(DRIVE);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    strafeRateLimit = new RateLimit(0.04); // 0.015
  }

  @Override
  protected void initialize() {
    VISION.queryPyeye();
    isAuton = DriverStation.getInstance().isAutonomous();
    SmartDashboard.putBoolean("Game/haveHatch", false);
    logger.info("Begin Vision Auto Align Pickup");
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    strafeCorrection = VISION.getStrafeCorrection();
    if (VISION.direction == FieldDirection.LEFT) {
      targetYaw = -90.0;
    } else targetYaw = 90.0;
    DRIVE.setTargetYaw(targetYaw);
    logger.info("Target Yaw: {}", targetYaw);
  }

  @SuppressWarnings("Duplicates")
  @Override
  protected void execute() {
    // Pyeye Method:
    VISION.queryPyeye(); // gets corrected heading and range from NT
    range = VISION.getRawRange();
    isGood = range >= 0; // check if range is good (we have a target), not -1

    // Calculate Yaw Term based on gyro
    double yawError = targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    DRIVE.setYawError(yawError);
    double yaw = kP_YAW * yawError;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    // Determine if actual yaw is close enough to target
    boolean onTarget = Math.abs(yawError) <= goodEnoughYaw;

    double forward;
    // forward is still normal
    if (isAuton) {
      forward = driveExpo.apply(controls.getForward()) * AUTON_OUTPUT;
    } else if (isGood) {
      forward = driveExpo.apply(controls.getForward()) * FWD_SCALE;
    } else {
      forward = driveExpo.apply(controls.getForward()) * FWD_SCALE_FAST;
    }

    double strafe;
    double strafeError =
        Math.sin(Math.toRadians(VISION.getCorrectedBearing())) * range - strafeCorrection;

    VISION.setStrafeError(strafeError);

    // Only take over strafe control if pyeye has a target and the robot is straight to the field
    if (isGood && onTarget) strafe = strafeError * kP_STRAFE * forward;
    else strafe = driveExpo.apply(controls.getStrafe());

    double strafeOutput = strafeRateLimit.apply(strafe);

    DRIVE.drive(forward, strafeOutput, yaw);
  }

  @Override
  protected boolean isFinished() {
    return (range <= MIN_RANGE && isGood);
  }

  @Override
  protected void end() {

    logger.info("range = {}", range);
    logger.info("End Auto Align Pickup Vision");
  }
}
