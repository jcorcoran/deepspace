package frc.team2767.deepspace.subsystem;

import static frc.team2767.deepspace.subsystem.FieldDirection.RIGHT;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.vision.QueryPyeyeDefaultCommand;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;

public class VisionSubsystem extends Subsystem implements Measurable {

  // 36 in away
  // gain test: 4in off both left and right

  private static final double CAMERA_X = 3.5;
  private static final double CAMERA_Y_LEFT = -13.5;
  private static final double CAMERA_Y_RIGHT = 13.5;
  private static final double GLUE_CORRECTION_FACTOR_RIGHT = -1.9251; // -1.8181
  private static final double GLUE_CORRECTION_FACTOR_LEFT = 0.9625; // 0.8556
  private static final double CAMERA_DEGREES_PER_PIXEL_ADJUSTMENT_RIGHT =
      1.0; // 1.0 is zero value 0.85
  private static final double CAMERA_DEGREES_PER_PIXEL_ADJUSTMENT_LEFT =
      1.0; // 1.0 is zero value 0.85
  private static final double CAMERA_POSITION_BEARING_LEFT = -90.0;
  private static final double CAMERA_POSITION_BEARING_RIGHT = 90.0;
  private static final double CAMERA_RANGE_SLOPE_RIGHT = 1.0886; // 1.054
  private static final double CAMERA_RANGE_OFFSET_RIGHT = -5.7751; // -5.02
  private static final double CAMERA_RANGE_SLOPE_LEFT = 1.0437; // 1.061
  private static final double CAMERA_RANGE_OFFSET_LEFT = -4.3828; // -4.92
  // NEGATIVE = TOWARDS FIELD LEFT (this one was negative) from driver station perspective
  private static final double STRAFE_CORRECTION_RIGHT =
      -0.75; // -1.0 // NEGATIVE TO FIELD LEFT FOR THIS ONE
  private static final double STRAFE_CORRECTION_LEFT = 0; // was positive
  private static final String RANGE = "RANGE";
  private static final String CORRECTED_BEARING = "CORRECTED_BEARING";
  private static final String LIGHT_STATE = "LIGHT_STATE";
  private static final String STRAFE_ERROR = "STRAFE_ERROR";
  private static NetworkTableEntry bearingEntry;
  private static NetworkTableEntry cameraMode;
  private static NetworkTableEntry rangeEntry;
  private static NetworkTableEntry cameraIDEntry;
  private static NetworkTableEntry targetYawEntry;
  private static NetworkTableEntry tuningEntry;
  private static NetworkTableEntry tuningFinished;
  private static double rawRange;
  private static double rawBearing;
  private static double correctedRange;
  private static double correctedHeading;
  private static double targetYaw;
  private static double blinkPeriod;
  private static boolean blinkEnabled;
  private static LightPattern currentPattern;
  private static double lightState;
  private static double strafeError = 0;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final DigitalOutput lightsOutput6 = new DigitalOutput(6);
  private final DigitalOutput lightsOutput5 = new DigitalOutput(5);
  private final Timer blinkTimer = new Timer();
  public GamePiece gamePiece = GamePiece.NOTSET;
  public Action action = Action.NOTSET;
  public FieldDirection direction = FieldDirection.NOTSET;
  public ElevatorLevel elevatorLevel = ElevatorLevel.NOTSET;
  public StartSide startSide = StartSide.NOTSET;

  public VisionSubsystem() {

    CameraServer cameraServer = CameraServer.getInstance();
    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    NetworkTable table = instance.getTable("Pyeye");
    cameraMode = table.getEntry("camera_mode");
    bearingEntry = table.getEntry("camera_bearing");
    rangeEntry = table.getEntry("camera_range");
    cameraIDEntry = table.getEntry("camera_id");
    targetYawEntry = table.getEntry("target_yaw");
    tuningEntry = table.getEntry("tuning_id");
    tuningFinished = table.getEntry("tuning_finished");
    cameraMode.setString("comp");
    targetYawEntry.setNumber(0.0);
    bearingEntry.setNumber(0.0);
    rangeEntry.setNumber(-1.0);
    tuningEntry.setNumber(-1.0);

    UsbCamera usbCamera = cameraServer.startAutomaticCapture();
    logger.info("camera is connected = {}", usbCamera.isConnected());
    lightsOutput6.set(true);
    lightsOutput5.set(true);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(this);
  }

  public double getCorrectedRange() {
    return correctedRange;
  }

  public void setCorrectedRange(double correctedRange) {
    this.correctedRange = correctedRange;
  }

  public double getCorrectedHeading() {
    return correctedHeading;
  }

  public void setCorrectedHeading(double correctedBearing) {
    this.correctedHeading = correctedBearing;
  }

  public double getCameraPositionBearing() {
    return direction == RIGHT ? CAMERA_POSITION_BEARING_RIGHT : CAMERA_POSITION_BEARING_LEFT;
  }

  public double getCameraX() {
    return CAMERA_X;
  }

  public double getCameraY() {
    return direction == RIGHT ? CAMERA_Y_RIGHT : CAMERA_Y_LEFT;
  }

  public boolean isTargetAcquired() {
    if (rawRange > 0.0) {
      logger.info("target found");
      return true;
    }
    return false;
  }

  public void queryPyeye() {
    rawBearing = (double) bearingEntry.getNumber(0.0);
    rawRange = (double) rangeEntry.getNumber(-1.0);
  }

  public void setGamePiece(GamePiece gamePiece) {
    this.gamePiece = gamePiece;
    logger.info("set gamepiece to {}", gamePiece);
  }

  public void setAction(Action action) {
    this.action = action;
    logger.info("set action to {}", action);
  }

  public void setFieldDirection(FieldDirection direction) {
    this.direction = direction;
    selectCamera();
    logger.info("set direction to {}", direction);
  }

  public void selectCamera() {
    VisionSubsystem.Camera camera;
    camera = Camera.LEFT;
    if (direction == RIGHT) {
      camera = Camera.RIGHT;
    }

    logger.info("chose {} camera", camera);
    cameraIDEntry.setNumber(camera.id);
  }

  public void setCameraMode(String mode) {
    cameraMode.setString(mode);
  }

  public void runTuning(int camID) {
    tuningEntry.setNumber(camID);
    tuningFinished.setNumber(0);
    logger.debug("tuning entry set to = {}", tuningEntry.getNumber(2767.0));
  }

  public boolean tuneFinished() {
    return (tuningFinished.getNumber(0.0).equals(1.0));
  }

  public void setElevatorLevel(ElevatorLevel elevatorLevel) {
    this.elevatorLevel = elevatorLevel;
    logger.info("set elevator level to {}", elevatorLevel);
  }

  public void startLightBlink(LightPattern pattern) {
    currentPattern = pattern;
    switch (currentPattern) {
      case CLIMB_GOOD: // fall through
      case GOT_HATCH:
        blinkPeriod = pattern.period;
        blinkEnabled = false;
        blinkTimer.reset();
        blinkTimer.start();
    }
  }

  public void blink() {
    if (blinkTimer.hasPeriodPassed(blinkPeriod)) {
      enableLights(!blinkEnabled);
      blinkEnabled = !blinkEnabled;
    }
  }

  public void enableLights(boolean enabled) {
    logger.info("lights to {}", !enabled);
    lightsOutput6.set(!enabled);
    lightsOutput5.set(!enabled);
    if (enabled) lightState = 1;
    else lightState = 0;
  }

  public boolean isBlinkFinished() {
    return blinkTimer.get() > currentPattern.duration;
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new QueryPyeyeDefaultCommand());
  }

  public double getTargetYaw() {
    return targetYaw;
  }

  public void setTargetYaw(double targetYaw) {
    this.targetYaw = targetYaw;
    logger.debug("Set target yaw to {}", targetYaw);
  }

  public void pyeyeDump() {
    logger.debug("PYEYE DUMP\nrange {} at {} degree\n", correctedRange, correctedHeading);
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Vision Subsystem";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure(RANGE, "Raw Range"),
        new Measure(CORRECTED_BEARING, "Corrected Bearing"),
        new Measure(LIGHT_STATE, "Light State"),
        new Measure(STRAFE_ERROR, "Strafe Error"));
  }

  @NotNull
  @Override
  public String getType() {
    return "vision";
  }

  @Override
  public int compareTo(@NotNull Measurable item) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure.getName()) {
      case RANGE:
        return this::getRawRange;
      case CORRECTED_BEARING:
        return this::getCorrectedBearing;
      case LIGHT_STATE:
        return () -> lightState;
      case STRAFE_ERROR:
        return this::getStrafeError;
      default:
        return () -> 2767;
    }
  }

  public double getRawRange() {
    if (rawRange < 0) {
      return -1;
    }

    return (rawRange * (direction == RIGHT ? CAMERA_RANGE_SLOPE_RIGHT : CAMERA_RANGE_SLOPE_LEFT)
        + (direction == RIGHT ? CAMERA_RANGE_OFFSET_RIGHT : CAMERA_RANGE_OFFSET_LEFT));
  }

  public double getCorrectedBearing() {
    return (rawBearing
        - (direction == RIGHT ? GLUE_CORRECTION_FACTOR_RIGHT : GLUE_CORRECTION_FACTOR_LEFT)
            * (direction == RIGHT
                ? CAMERA_DEGREES_PER_PIXEL_ADJUSTMENT_RIGHT
                : CAMERA_DEGREES_PER_PIXEL_ADJUSTMENT_LEFT));
  }

  public double getStrafeError() {
    return strafeError;
  }

  public void setStrafeError(double strafeError) {
    this.strafeError = strafeError;
  }

  public double getStrafeCorrection() {
    return direction == RIGHT ? STRAFE_CORRECTION_RIGHT : STRAFE_CORRECTION_LEFT;
  }

  public enum Camera {
    LEFT(1),
    RIGHT(0);

    int id;

    Camera(int i) {
      id = i;
    }
  }

  public enum LightPattern {
    GOT_HATCH(0.05, 1.0),
    CLIMB_GOOD(0.2, 2.0);

    double period;
    double duration;

    LightPattern(double period, double duration) {
      this.period = period;
      this.duration = duration;
    }
  }
}
