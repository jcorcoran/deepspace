package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class ResetAxisCommandGroup extends CommandGroup {

  public ResetAxisCommandGroup() {
    addSequential(
        new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoMediumPositionInches), 3.0);
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg), 3.0);
    addSequential(new ElevatorSetPositionCommand(4), 3.0);
    addSequential(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg), 5.0);
  }
}
