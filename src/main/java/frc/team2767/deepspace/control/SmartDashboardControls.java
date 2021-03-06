package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.AdjustAzimuthCommand;
import frc.team2767.deepspace.command.AzimuthZeroPositionCommand;
import frc.team2767.deepspace.command.HealthCheckCommand;
import frc.team2767.deepspace.command.ResetAxisCommandGroup;
import frc.team2767.deepspace.command.approach.*;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPickupCommandGroup;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPlaceCommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.climb.*;
import frc.team2767.deepspace.command.elevator.ElevatorSafeZeroCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.BiscuitDumpCommand;
import frc.team2767.deepspace.command.log.ElevatorDumpCommand;
import frc.team2767.deepspace.command.log.IntakeDumpCommand;
import frc.team2767.deepspace.command.log.VacuumDumpCommand;
import frc.team2767.deepspace.command.sequences.pickup.SandstormHatchPickupCommandGroup;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.command.vacuum.StopPumpCommandGroup;
import frc.team2767.deepspace.command.vacuum.VacuumCooldownCommandGroup;
import frc.team2767.deepspace.command.vision.BlinkLightsCommand;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SmartDashboardControls() {
    addMatchCommands();

    addClimbTab();
    if (!Robot.isEvent()) {
      addPitCommands();
      addAzimuthCommands();
      addVisionCommands();
      addVacuumCommands();
      addPathTest();
    }
  }

  private void addMatchCommands() {
    logger.debug("creating match commands");
    SmartDashboard.putData("Game/tridentSol", VACUUM.getTridentSolenoid());
    SmartDashboard.putBoolean("Game/onTarget", false);
    SmartDashboard.putData("Game/SandstormHatchPickUp", new SandstormHatchPickupCommandGroup());
    //    SmartDashboard.putData("Game/hatchPlace", new AutoHatchPlaceCommandGroup(0.0));
    SmartDashboard.putData("Game/Gyro", Robot.DRIVE.getGyro());
    SmartDashboard.putData("Game/Lvl2Deploy", new ClimbLevel2DeployCommand());
    SmartDashboard.putData("Game/Lvl2Climb", new ClimbLevel2AutoCommand());
    SmartDashboard.putData("Stop pump", new StopPumpCommandGroup());
  }

  private void addClimbTab() {
    ShuffleboardTab ClimbTab = Shuffleboard.getTab("ClimbTab");

    // Climb Commands
    ShuffleboardLayout Climb = ClimbTab.getLayout("Climb", "List Layout");
    Climb.add("Climb", new ClimbAutoCommand()).withWidget(BuiltInWidgets.kCommand);
    Climb.add("Stop", new StopClimbCommand()).withWidget(BuiltInWidgets.kCommand);
    Climb.add("Deploy", new DeploySequenceCommandGroup()).withWidget(BuiltInWidgets.kCommand);
    Climb.withPosition(1, 1);

    // Solenoid Commands
    ShuffleboardLayout Solenoid = ClimbTab.getLayout("Solenoids", "List Layout");
    Solenoid.add("Trident", VACUUM.getTridentSolenoid()).withWidget(BuiltInWidgets.kBooleanBox);
    Solenoid.add("Climb", VACUUM.getClimbSolenoid()).withWidget(BuiltInWidgets.kBooleanBox);
    Solenoid.withPosition(3, 1);

    // Pump Commands
    ShuffleboardLayout Pump = ClimbTab.getLayout("Pump", "List Layout");
    Pump.add("Stop", new StopPumpCommandGroup());
    Pump.add("Hatch Pressure", new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg))
        .withWidget(BuiltInWidgets.kCommand);
    Pump.add("Cargo Pressure", new PressureSetCommand(VacuumSubsystem.kBallPressureInHg))
        .withWidget(BuiltInWidgets.kCommand);
    Pump.add("Climb Pressure", new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg))
        .withWidget(BuiltInWidgets.kCommand);
    Pump.withPosition(2, 1);

    // Release Mechanisms
    ShuffleboardLayout Servos = ClimbTab.getLayout("Servos", "List Layout");
  }

  private void addPitCommands() {
    SmartDashboard.putData("Game/SandstormHatchPickUp", new SandstormHatchPickupCommandGroup());
    addTestCommands();
    addVacuumCommands();
    SmartDashboard.putData("Pit/resetAxis", new ResetAxisCommandGroup());
    SmartDashboard.putData("Pit/Climb", new ClimbAutoCommand());
    SmartDashboard.putData("Pit/ClimbStop", new StopClimbCommand());
    SmartDashboard.putData("Pit/Health Check", new HealthCheckCommand());
  }

  private void addAzimuthCommands() {
    SmartDashboard.putData("Azimuth/0inc", new AdjustAzimuthCommand(0, 1));
    SmartDashboard.putData("Azimuth/0dec", new AdjustAzimuthCommand(0, -1));
    SmartDashboard.putData("Azimuth/1inc", new AdjustAzimuthCommand(1, 1));
    SmartDashboard.putData("Azimuth/1dec", new AdjustAzimuthCommand(1, -1));
    SmartDashboard.putData("Azimuth/2inc", new AdjustAzimuthCommand(2, 1));
    SmartDashboard.putData("Azimuth/2dec", new AdjustAzimuthCommand(2, -1));
    SmartDashboard.putData("Azimuth/3inc", new AdjustAzimuthCommand(3, 1));
    SmartDashboard.putData("Azimuth/3dec", new AdjustAzimuthCommand(3, -1));
    SmartDashboard.putData("Azimuth/ZeroAzimuths", new AzimuthZeroPositionCommand());
  }

  private void addVisionCommands() {
    SmartDashboard.putData("Game/OrthogMvmt", new OrthogonalMovementCommand());
    SmartDashboard.putData("Pit/LightsOn", new LightsOnCommand());
    SmartDashboard.putData("Pit/LightsOff", new LightsOffCommand());
  }

  private void addPathTest() {

    ShuffleboardTab pathTab = Shuffleboard.getTab("Path");
    //    pathTab.add("-90.0", new AutoHatchPlaceCommandGroup(-90.0));
    //    pathTab.add("90.0", new AutoHatchPlaceCommandGroup(90.0));
    //    pathTab.add("-180.0", new AutoHatchPlaceCommandGroup(-180.0));
    //    pathTab.add("180.0", new AutoHatchPlaceCommandGroup(180.0));
    pathTab.add("0.0", new AutoHatchPlaceCommandGroup());
    pathTab.add("set left", new SetFieldDirectionCommand(FieldDirection.LEFT));
    pathTab.add("path 1", new PathCommand("hab_to_cargo_l", 90.0));
    //    pathTab.add("place 0.0", new AutoHatchPlaceCommandGroup(0.0));
    pathTab.add("path 2", new PathCommand("cargo_front_to_loading_l", 90.0));
    pathTab.add("pickip", new AutoHatchPickupCommandGroup());
    pathTab.add("set right", new SetFieldDirectionCommand(FieldDirection.RIGHT));
    pathTab.add("path 3", new PathCommand("loading_to_cargo_side_l", 0.0));
    //    pathTab.add("place 90.0", new AutoHatchPlaceCommandGroup(-90.0));
    pathTab.add("r_hab_cargo", new PathCommand("hab_to_cargo_r", 90.0));
    pathTab.add("r_cargo_front_loading", new PathCommand("cargo_front_to_loading_r", 90.0));
    pathTab.add("r_loading_cargo_side", new PathCommand("loading_to_cargo_side_r", 0.0));
  }

  private void addTestCommands() {
    SmartDashboard.putData("Pit/SetPickup", new SetActionCommand(Action.PICKUP));
    SmartDashboard.putData("Pit/SetPlace", new SetActionCommand(Action.PLACE));

    SmartDashboard.putData("Pit/Hatch", new SetGamePieceCommand(GamePiece.HATCH));
    SmartDashboard.putData("Pit/cargo", new SetGamePieceCommand(GamePiece.CARGO));

    SmartDashboard.putData(
        "Test/Intake Cargo", new IntakePositionCommand(IntakeSubsystem.kCargoPlayerPositionDeg));
    SmartDashboard.putData(
        "Test/Intake Load", new IntakePositionCommand(IntakeSubsystem.kLoadPositionDeg));
    SmartDashboard.putData(
        "Test/Intake Middle", new IntakePositionCommand(IntakeSubsystem.kMiddlePositionDeg));
    SmartDashboard.putData(
        "Test/Intake Stow", new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
    SmartDashboard.putData("Test/Intake Dump", new IntakeDumpCommand());

    SmartDashboard.putData(
        "Test/Biscuit Up", new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit Left", new BiscuitSetPositionCommand(BiscuitSubsystem.kLeftPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit Right", new BiscuitSetPositionCommand(BiscuitSubsystem.kRightPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit BS Left",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kBackStopLeftPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit BS Right",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kBackStopRightPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit Down Right", new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition));
    SmartDashboard.putData(
        "Test/Biscuit TU Left",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kTiltUpLeftPositionDeg));
    SmartDashboard.putData(
        "Test/Biscuit TU Right",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kTiltUpRightPositionDeg));
    SmartDashboard.putData("Test/Biscuit Dump", new BiscuitDumpCommand());
    SmartDashboard.putData("Test/biscuit execute", new BiscuitExecutePlanCommand());

    SmartDashboard.putData(
        "Test/Elevator Cargo High",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoHighPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Low",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoLowPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Med",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoMediumPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Pickup",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoPickupPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Cargo Player",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoPlayerPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Hatch High",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchHighPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Hatch Low",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
    SmartDashboard.putData(
        "Test/Elevator Hatch Med",
        new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchMediumPositionInches));
    SmartDashboard.putData("Test/Elevator Dump", new ElevatorDumpCommand());
    SmartDashboard.putData("Test/Elevator Zero", new ElevatorSafeZeroCommand());

    SmartDashboard.putData("Test/Vacuum Dump", new VacuumDumpCommand());
    SmartDashboard.putData(
        "Test/Vacuum Cargo", new PressureSetCommand(VacuumSubsystem.kBallPressureInHg));
    SmartDashboard.putData(
        "Test/Vacuum Hatch", new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    SmartDashboard.putData(
        "Test/Vacuum Climb", new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg));
    //    SmartDashboard.putData("Test/Yaw Command", new YawCommand());
    SmartDashboard.putData(
        "Test/blinkLights", new BlinkLightsCommand(VisionSubsystem.LightPattern.GOT_HATCH));
    SmartDashboard.putData("Test/Twist70at180", new DriveTwistCommand(180, 70, -90.0));
    SmartDashboard.putData("Test/Twist70at0", new DriveTwistCommand(0, 70, -90.0));
    SmartDashboard.putData("Test/Pyeye", new QueryPyeyeCommand());
    SmartDashboard.putData("Test/yawTo", new YawToTargetCommand(90.0));
    SmartDashboard.putData(
        "Test/setTalonConfig", new TalonConfigCommand(DriveSubsystem.DriveTalonConfig.YAW_CONFIG));
  }

  private void addVacuumCommands() {
    SmartDashboard.putData("Vacuum/cool", new VacuumCooldownCommandGroup());
    SmartDashboard.putData(
        "Pit/Ball", new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.CARGO_PICKUP));
    SmartDashboard.putData(
        "Pit/GamePiece", new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.HATCH_PICKUP));
    SmartDashboard.putData(
        "Pit/PressureAccumulate",
        new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE));
    SmartDashboard.putData(
        "Pit/ClimbSolenoids", new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.CLIMB));

    SmartDashboard.putData("Pit/VacuumStop", new StopPumpCommandGroup());
    SmartDashboard.putData(
        "Pit/Vacuum/Climb", new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg));

    SmartDashboard.putData(
        "Pit/Vacuum/Hatch", new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    SmartDashboard.putData(
        "Pit/Vacuum/Cargo", new PressureSetCommand(VacuumSubsystem.kBallPressureInHg));
  }
}
