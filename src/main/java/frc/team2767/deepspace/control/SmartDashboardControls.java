package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.HealthCheckCommand;
import frc.team2767.deepspace.command.YawCommand;
import frc.team2767.deepspace.command.ZeroAxisCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.climb.*;
import frc.team2767.deepspace.command.elevator.ElevatorSafeZeroCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.BiscuitDumpCommand;
import frc.team2767.deepspace.command.log.ElevatorDumpCommand;
import frc.team2767.deepspace.command.log.IntakeDumpCommand;
import frc.team2767.deepspace.command.log.VacuumDumpCommand;
import frc.team2767.deepspace.command.sequences.SandstormHatchPickupCommandGroup;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.twist.OrthogonalMovementCommand;
import frc.team2767.deepspace.command.vacuum.*;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.*;
import java.awt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SmartDashboardControls() {
    addMatchCommands();
    addPitCommands();
    addClimbTab();
    if (!Robot.isEvent()) {
      addTestCommands();
    }
    addVisionCommands();
  }

  private void addMatchCommands() {
    logger.debug("creating match commands");
    SmartDashboard.putData("Game/zeroAll", new ZeroAxisCommand());
    SmartDashboard.putData("Game/tridentSol", VACUUM.getTridentSolenoid());
    SmartDashboard.putData("Game/pumpSol", VACUUM.getPumpSolenoid());
    SmartDashboard.putBoolean("Game/onTarget", false);
  }

  private void addPitCommands() {
    addTestCommands();
    addIntakeCommands();
    addElevatorCommands();
    addVacuumCommands();
    SmartDashboard.putData("Pit/ElevatorZero", new ElevatorZeroCommand());
    SmartDashboard.putData("Pit/LowerSuction", new LowerSuctionCupCommand());
    SmartDashboard.putData("Pit/Climb", new ClimbCommand());
    SmartDashboard.putData("Pit/Unwind", new UnwindClimbCommand());
    SmartDashboard.putData("Pit/ClimbStop", new StopClimbCommand());
    SmartDashboard.putData("Pit/Health Check", new HealthCheckCommand());
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
        "Test/Biscuit Down Right",
        new BiscuitSetPositionCommand(BiscuitSubsystem.kDownRightPositionDeg));
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
    SmartDashboard.putData("Test/Vacuum Cargo", new PressureSetCommand(VACUUM.kBallPressureInHg));
    SmartDashboard.putData("Test/Vacuum Hatch", new PressureSetCommand(VACUUM.kHatchPressureInHg));
    SmartDashboard.putData("Test/Vacuum Climb", new PressureSetCommand(VACUUM.kClimbPressureInHg));
    SmartDashboard.putData(
        "Test/Pump Enable", new ActivateValveCommand(VacuumSubsystem.Valve.PUMP));
    SmartDashboard.putData(
        "Test/Pump Disable", new DeactivateValveCommand(VacuumSubsystem.Valve.PUMP));
    SmartDashboard.putData(
        "Test/Trident Enable", new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    SmartDashboard.putData(
        "Test/Trident Disable", new DeactivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    SmartDashboard.putData(
        "Test/Climb Enable", new ActivateValveCommand(VacuumSubsystem.Valve.CLIMB));
    SmartDashboard.putData(
        "Test/Climb Disable", new DeactivateValveCommand(VacuumSubsystem.Valve.CLIMB));

    SmartDashboard.putData("Test/SandstormHatchPickUp", new SandstormHatchPickupCommandGroup());

    SmartDashboard.putData("Test/Yaw Command", new YawCommand());
  }

  private void addVisionCommands() {
    SmartDashboard.putData("Game/OrthogMvmt", new OrthogonalMovementCommand());
    SmartDashboard.putData("Pit/LightsOn", new LightsOnCommand());
    SmartDashboard.putData("Pit/LightsOff", new LightsOffCommand());
  }

  private void addIntakeCommands() {
    //    SmartDashboard.putData("Pit/IntakeOut", new IntakeDownCommand());
    //    SmartDashboard.putData("Pit/IntakeStop", new ShoulderStopCommand());
    //    SmartDashboard.putData("Pit/IntakeIn", new IntakeUpCommand());
  }

  private void addElevatorCommands() {
    //    SmartDashboard.putData("Pit/ElevatorUp", new ElevatorOpenLoopUpCommand());
    //    SmartDashboard.putData("Pit/ElevatorStop", new ElevatorStopCommand());
    //    SmartDashboard.putData("Pit/ElevatorDown", new ElevatorOpenLoopDownCommand());
  }

  private void addVacuumCommands() {
    SmartDashboard.putData("Vacuum/cool", new VacuumCooldownCommandGroup());
    SmartDashboard.putData(
        "Pit/TridentValveActivate",
        new ActivateValveCommand(new VacuumSubsystem.Valve[] {VacuumSubsystem.Valve.TRIDENT}));
    SmartDashboard.putData("Pit/BuildPressure", new PressureAccumulateCommandGroup());

    SmartDashboard.putData("Pit/VacuumStop", new StopPumpCommandGroup());
    SmartDashboard.putData(
        "Pit/Vacuum/Climb", new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg));

    SmartDashboard.putData(
        "Pit/Vacuum/Hatch", new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    SmartDashboard.putData(
        "Pit/Vacuum/Cargo", new PressureSetCommand(VacuumSubsystem.kBallPressureInHg));
  }

  private void addClimbTab() {
    ShuffleboardTab ClimbTab = Shuffleboard.getTab("ClimbTab");

    // Climb Commands
    ShuffleboardLayout Climb = ClimbTab.getLayout("Climb", "List Layout");
    Climb.add("Lower Suction Cup", new LowerSuctionCupCommand())
        .withWidget(BuiltInWidgets.kCommand);
    Climb.add("Climb", new ClimbCommand()).withWidget(BuiltInWidgets.kCommand);
    Climb.add("Stop", new StopClimbCommand()).withWidget(BuiltInWidgets.kCommand);
    Climb.add("Unwind", new UnwindClimbCommand()).withWidget(BuiltInWidgets.kCommand);
    Climb.add("Raise", new RaiseClimbCommand()).withWidget(BuiltInWidgets.kCommand);
    Climb.withPosition(1, 1);

    // Solenoid Commands
    ShuffleboardLayout Solenoid = ClimbTab.getLayout("Solenoids", "List Layout");
    Solenoid.add("Enable Pump", new ActivateValveCommand(VacuumSubsystem.Valve.PUMP));
    Solenoid.add("Disable Pump", new DeactivateValveCommand(VacuumSubsystem.Valve.PUMP));
    Solenoid.add("Enable Trident", new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    Solenoid.add("Disable Trident", new DeactivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    Solenoid.add("Enable Climb", new ActivateValveCommand(VacuumSubsystem.Valve.CLIMB));
    Solenoid.add("Disable Climb", new DeactivateValveCommand(VacuumSubsystem.Valve.CLIMB));
    Solenoid.add("Trident", VACUUM.getTridentSolenoid()).withWidget(BuiltInWidgets.kBooleanBox);
    Solenoid.add("Pump", VACUUM.getPumpSolenoid()).withWidget(BuiltInWidgets.kBooleanBox);
    Solenoid.add("Climb", VACUUM.getClimbSolenoid()).withWidget(BuiltInWidgets.kBooleanBox);
    Solenoid.withPosition(3, 1);

    // Pump Commands
    ShuffleboardLayout Pump = ClimbTab.getLayout("Pump", "List Layout");
    Pump.add("Stop", new StopPumpCommandGroup());
    Pump.add("Hatch Pressure", new PressureSetCommand(VACUUM.kHatchPressureInHg))
        .withWidget(BuiltInWidgets.kCommand);
    Pump.add("Cargo Pressure", new PressureSetCommand(VACUUM.kBallPressureInHg))
        .withWidget(BuiltInWidgets.kCommand);
    Pump.add("Climb Pressure", new PressureSetCommand(VACUUM.kClimbPressureInHg))
        .withWidget(BuiltInWidgets.kCommand);
    Pump.withPosition(2, 1);

    // Release Mechanisms
    ShuffleboardLayout Servos = ClimbTab.getLayout("Servos", "List Layout");
    Servos.add("Release Climb", new ReleaseClimbCommand()).withWidget(BuiltInWidgets.kCommand);
    Servos.add("Release Kickstand", new ReleaseKickstandCommand())
        .withWidget(BuiltInWidgets.kCommand);
  }
}
